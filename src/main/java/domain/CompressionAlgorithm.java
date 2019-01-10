package domain;

import java.nio.file.Path;

public interface CompressionAlgorithm {

    /**
     * Compresses the file at the given path.
     *
     * @param originalFilePath Path of the file to be compressed.
     * @return Time elapsed during compression in nanoseconds (excluding file
     * reading and writing operations), or a negative number if the compression
     * operation failed.
     */
    public long compressFile(Path originalFilePath);

    /**
     * Decompresses the file at the given path.
     *
     * @param compressedFilePath Path of the file to be decompressed.
     * @return Time elapsed during decompression in nanoseconds (excluding file
     * reading and writing operations), or a negative number if the
     * decompression operation failed.
     */
    public long decompressFile(Path compressedFilePath);

    /**
     * Returns the file extension used by the CompressionAlgorithm (for
     * compressed files).
     *
     * @return The file extension used by the CompressionAlgorithm (for
     * compressed files).
     */
    public String getExtension();

    /**
     * Returns a short name for the CompressionAlgorithm.
     *
     * @return A short name for the CompressionAlgorithm.
     */
    public String getName();

    /**
     * Returns a description of the CompressionAlgorithm.
     *
     * @return The CompressionAlgorithm's description.
     */
    public String getDescription();
}
