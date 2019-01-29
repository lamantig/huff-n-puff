package domain;

/**
 * A class with static methods for data compression/decompression using an
 * implementation of the Lempel–Ziv–Welch algorithm with variable-length
 * codewords and dictionary resets when full.
 */
public class LZW extends CompressionAlgorithm {

    /**
     * Minimum length for a codeword; since the symbol alphabet is made up of
     * all possible byte values (which are 256), this minimum length can't be
     * less than 9 (and it doesn't have any good reason to be greater than 9,
     * either).
     */
    private static final int MIN_CW_LENGTH = 9;
    /**
     * Maximum length for a codeword; this can be set to values ranging from 9
     * (inclusive) up to about 29 (inclusive). However, often value larger than
     * 20 don't make any improvement in the compression size, and they increase
     * memory usage, which may result in an OutOfMemoryError.
     */
    private static final int MAX_CW_LENGTH = 12;

    /**
     * The hash table size can (and probably should) be changed depending on the
     * value of {@link #MAX_CW_LENGTH}. Some example values: for a MAX_CW_LENGTH
     * of 12, 12289; for 16, 196663; for 20, 393161. Here are more candidate
     * values in increasing order (they are all prime numbers as far as possible
     * from any power of 2): 12289, 24527, 49193, 98347, 196663, 393161, 786337,
     * 1572919, 3145661, 6291529, 12582971, 25165913, 50331709, 100663207.
     */
    public static final int HASH_TABLE_SIZE = 12289;
    /**
     * The hash factor used in the rolling hash function for the ByteSequence
     * class. The value of 257 was chosen because it is a prime number slightly
     * larger than the size of the alphabet (symbols are single bytes, so the
     * size of the alphabet is 256).
     */
    public static final int HASH_FACTOR = 257;

    /**
     * The file extension used for files compressed using this class; it
     * includes a number indicating the value of {@link #MAX_CW_LENGTH} so that
     * the same value can be used for decompression.
     */
    public static final String COMPRESSED_FILE_EXTENSION = ".lzw" + MAX_CW_LENGTH;
    /**
     * Short name for this algorithm (used in TUI).
     */
    public static final String NAME = "lzw";
    /**
     * Longer name for this algorithm (used in TUI).
     */
    public static final String DESCRIPTION = "Lempel-Ziv-Welch";

    /**
     * When the new codeword to be inserted into the dictionary reaches this
     * value, codeword length has to be increased for the first time.
     */
    private static final int FIRST_LENGTH_THRESHOLD = 1 << MIN_CW_LENGTH;
    /**
     * The maximum value for codeword, after which the dictionary will be reset.
     */
    private static final int POSSIBLE_CW_VALUES_COUNT = 1 << MAX_CW_LENGTH;

    /**
     * Offset (in bytes) from the beginning of compressed files, indicating
     * where the length of the original data (in bytes) will be written.
     */
    private static final int OFFSET_ORIG_DATA_LENGTH = 0;
    /**
     * Offset (in bytes) from the beginning of compressed files, indicating
     * where the value of freeBits (unused bits in the last byte of the
     * compressed file, see the BitSequence class) will be written.
     */
    private static final int OFFSET_FREEBITS = Integer.BYTES;
    /**
     * Offset (in bytes) from the beginning of compressed files, indicating
     * where the actual compressed data will be written.
     */
    private static final int OFFSET_DATA = OFFSET_FREEBITS + Byte.SIZE;

    @Override
    public BitSequence compressData(byte[] originalData) {

        Dictionary dict = new LZWDictionary();
        initializeDictionary(dict);
        byte[] bytes = new byte[originalData.length];
        byte[] originalDataLength = Utils.toByteArray(originalData.length);
        Utils.arrayCopy(originalDataLength, 0,
                bytes, OFFSET_ORIG_DATA_LENGTH, originalDataLength.length);
        BitSequence compressedData = new BitSequence(bytes, Byte.SIZE, OFFSET_DATA);

        int i = 0;
        int newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;
        ByteSequence string = new ByteSequence();
        string.append(originalData[i]);
        ByteSequence nextString;
        Integer codewordForString = dict.get(string);
        Integer codewordForNextString;
        int codeWordLength = MIN_CW_LENGTH;
        int lengthThreshold = FIRST_LENGTH_THRESHOLD;
        byte symbol;

        while (++i < originalData.length) {

            symbol = originalData[i];
            nextString = string.makeClone();
            nextString.append(symbol);
            codewordForNextString = dict.get(nextString);

            if (codewordForNextString == null) {

                compressedData.append(codewordForString, codeWordLength);

                if (newCodeword == lengthThreshold) {
                    if (++codeWordLength > MAX_CW_LENGTH) {
                        dict.clear();
                        initializeDictionary(dict);
                        newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;
                        codeWordLength = MIN_CW_LENGTH;
                        lengthThreshold = FIRST_LENGTH_THRESHOLD;
                    } else {
                        lengthThreshold <<= 1;
                    }
                }

                dict.put(nextString, newCodeword++);
                string.reset();
                string.append(symbol);
                codewordForString = dict.get(string);

            } else {
                codewordForString = codewordForNextString;
                string = nextString;
            }
        }

        compressedData.append(codewordForString, codeWordLength);
        compressedData.getBits()[OFFSET_FREEBITS] = (byte) compressedData.getFreeBits();
        return compressedData;
    }

    @Override
    public byte[] decompressData(byte[] compressedData) {

        int originalDataLength = Utils.extractInt(compressedData, OFFSET_ORIG_DATA_LENGTH);
        byte[] originalData = new byte[originalDataLength];
        int freeBits = compressedData[OFFSET_FREEBITS];
        BitSequence compressedBitSeq = new BitSequence(compressedData, freeBits);
        compressedBitSeq.setReadPosition(OFFSET_DATA, 0);

        ByteSequence[] dict = new ByteSequence[POSSIBLE_CW_VALUES_COUNT];
        initializeDictionary(dict);
        int codeWordLength = MIN_CW_LENGTH;
        int lengthThreshold = FIRST_LENGTH_THRESHOLD;
        Integer codeword = compressedBitSeq.readNextInt(codeWordLength);
        ByteSequence string = dict[codeword].makeClone();
        ByteSequence entry;
        int i = string.copyTo(originalData, 0);
        int newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;

        while ((codeword = compressedBitSeq.readNextInt(codeWordLength)) != null) {

            entry = dict[codeword];

            if (entry == null) {
                entry = string.makeClone();
                entry.append(string.getFirst());
            }

            i += entry.copyTo(originalData, i);
            string.append(entry.getFirst());

            // codeWordLength is updated one codeword before the threshold
            if (newCodeword == lengthThreshold - 1) {

                if (++codeWordLength > MAX_CW_LENGTH) {
                    // this is the only case when string is not entered in dict
                    reinitializeDictionary(dict);
                    newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;
                    codeWordLength = MIN_CW_LENGTH;
                    lengthThreshold = FIRST_LENGTH_THRESHOLD;
                } else {
                    lengthThreshold <<= 1;
                    dict[newCodeword++] = string;
                }

            } else {
                dict[newCodeword++] = string;
            }

            string = entry.makeClone();
        }

        return originalData;
    }

    private static void initializeDictionary(Dictionary dict) {
        dict.clear();
        for (int byteValue = 0; byteValue < Utils.POSSIBLE_BYTE_VALUES_COUNT; byteValue++) {
            dict.put(new ByteSequence(new byte[]{(byte) byteValue}), byteValue);
        }
    }

    private static void initializeDictionary(ByteSequence[] dict) {
        for (int byteValue = 0; byteValue < Utils.POSSIBLE_BYTE_VALUES_COUNT; byteValue++) {
            dict[byteValue] = new ByteSequence(new byte[]{(byte) byteValue});
        }
    }

    private static void reinitializeDictionary(ByteSequence[] dict) {
        int codeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;
        while (codeword < POSSIBLE_CW_VALUES_COUNT) {
            dict[codeword++] = null;
        }
    }

    @Override
    public String getExtension() {
        return COMPRESSED_FILE_EXTENSION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
