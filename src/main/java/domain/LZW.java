package domain;

public class LZW extends CompressionAlgorithm {

    private static final int MIN_CW_LENGTH = 9;
    private static final int MAX_CW_LENGTH = 12;

    public static final int HASH_TABLE_SIZE = 12289;
    public static final int HASH_FACTOR = 257;

    public static final String COMPRESSED_FILE_EXTENSION = ".lzw" + MAX_CW_LENGTH;
    public static final String NAME = "lzw";
    public static final String DESCRIPTION = "Lempel-Ziv-Welch";
    
    private static final int FIRST_LENGTH_THRESHOLD = 1 << MIN_CW_LENGTH;
    private static final int POSSIBLE_CW_VALUES_COUNT = 1 << MAX_CW_LENGTH;
    private static final int OFFSET_FREEBITS = Integer.BYTES;
    private static final int OFFSET_DATA = OFFSET_FREEBITS + Byte.SIZE;

    @Override
    public BitSequence compressData(byte[] originalData) {

        Dictionary<ByteSequence, Integer> dict = new LZWDictionary();
        initializeDictionary(dict);
        byte[] bytes = new byte[originalData.length];
        byte[] originalDataLength = Utils.toByteArray(originalData.length);
        Utils.arrayCopy(originalDataLength, 0, bytes, 0, originalDataLength.length);
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

        int originalDataLength = Utils.extractInt(compressedData);
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

            if (newCodeword == lengthThreshold - 1) {

                if (++codeWordLength > MAX_CW_LENGTH) {
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
