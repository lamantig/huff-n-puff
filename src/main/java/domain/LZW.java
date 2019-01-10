package domain;

import java.nio.file.Path;

public class LZW implements CompressionAlgorithm {

    public static final String COMPRESSED_FILE_EXTENSION = ".lzw12";
    public static final String NAME = "lzw";
    public static final String DESCRIPTION = "Lempel–Ziv–Welch";

    @Override
    public long compressFile(Path originalFilePath) {
        return -1;
    }

    @Override
    public long decompressFile(Path compressedFilePath) {
        return -1;
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
