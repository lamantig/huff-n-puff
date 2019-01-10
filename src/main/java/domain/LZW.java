package domain;

public class LZW extends CompressionAlgorithm {

    public static final String COMPRESSED_FILE_EXTENSION = ".lzw12";
    public static final String NAME = "lzw";
    public static final String DESCRIPTION = "Lempel–Ziv–Welch";

    @Override
    protected BitSequence compressData(byte[] originalData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected byte[] decompressData(byte[] compressedData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
