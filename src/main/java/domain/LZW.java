package domain;

public class LZW extends CompressionAlgorithm {

    public static final String COMPRESSED_FILE_EXTENSION = ".lzw12";
    public static final String NAME = "lzw";
    public static final String DESCRIPTION = "Lempel-Ziv-Welch";

    public static final int HASH_TABLE_SIZE = 12289;
    public static final int HASH_FACTOR = 257;

    private static final int CODEWORD_LENGTH = 12;
    private static final int POSSIBLE_CW_VALUES_COUNT = 1 << CODEWORD_LENGTH;
    private static final int OFFSET_FREEBITS = Integer.BYTES;
    private static final int OFFSET_DATA = OFFSET_FREEBITS + Byte.SIZE;

    @Override
    protected BitSequence compressData(byte[] originalData) {

        Dictionary<ByteSequence, Integer> dict = new JavasDict<>();
        initializeDictionary(dict);
        byte[] bytes = new byte[originalData.length];
        byte[] originalDataLength = Utils.toByteArray(originalData.length);
        Utils.arrayCopy(originalDataLength, 0, bytes, 0, originalDataLength.length);
        BitSequence compressedData = new BitSequence(bytes, Byte.SIZE, OFFSET_DATA);

        int i = 0;
        int newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;
        ByteSequence string = new ByteSequence();
        string.append(originalData[i]);
        ByteSequence stringPlusNextSymbol;
        Integer codewordForNextString;
        byte symbol;

        while (++i < originalData.length) {

            symbol = originalData[i];
            stringPlusNextSymbol = string.makeClone();
            stringPlusNextSymbol.append(symbol);
            codewordForNextString = dict.get(stringPlusNextSymbol);

            if (codewordForNextString == null) {

                compressedData.append(dict.get(string), CODEWORD_LENGTH);

                if (newCodeword == POSSIBLE_CW_VALUES_COUNT) {
                    dict.clear();
                    initializeDictionary(dict);
                    newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;
                }

                dict.put(stringPlusNextSymbol, newCodeword++);
                string.reset();
                string.append(symbol);

            } else {
                string = stringPlusNextSymbol;
            }
        }

        compressedData.append(dict.get(string), CODEWORD_LENGTH);
        compressedData.getBits()[OFFSET_FREEBITS] = (byte) compressedData.getFreeBits();
        return compressedData;
    }

    @Override
    protected byte[] decompressData(byte[] compressedData) {

        int originalDataLength = Utils.extractInt(compressedData);
        byte[] originalData = new byte[originalDataLength];
        int freeBits = compressedData[OFFSET_FREEBITS];
        BitSequence compressedBitSeq = new BitSequence(compressedData, freeBits);
        compressedBitSeq.setReadPosition(OFFSET_DATA, 0);

        ByteSequence[] dict = new ByteSequence[POSSIBLE_CW_VALUES_COUNT];
        initializeDictionary(dict);
        Integer codeword = compressedBitSeq.readNextInt(CODEWORD_LENGTH);
        ByteSequence string = dict[codeword].makeClone();
        ByteSequence entry;
        int i = string.copyTo(originalData, 0);
        int newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;

        while ((codeword = compressedBitSeq.readNextInt(CODEWORD_LENGTH)) != null) {

            entry = dict[codeword];

            if (entry == null) {
                entry = string.makeClone();
                entry.append(string.getFirst());
            }

            i += entry.copyTo(originalData, i);
            string.append(entry.getFirst());

            if (newCodeword == POSSIBLE_CW_VALUES_COUNT) {
                reinitializeDictionary(dict);
                newCodeword = Utils.POSSIBLE_BYTE_VALUES_COUNT;
            }

            dict[newCodeword++] = string;
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
