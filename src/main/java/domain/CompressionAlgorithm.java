package domain;

import io.FileUtils;
import java.nio.file.Path;

public abstract class CompressionAlgorithm {

    /**
     * Compresses the file at the given path.
     *
     * @param originalFilePath Path of the file to be compressed.
     * @return Time elapsed during compression in nanoseconds (excluding file
     * reading and writing operations), or a negative number if the compression
     * operation failed.
     */
    public long compressFile(Path originalFilePath) {

        byte[] originalData = FileUtils.readFile(originalFilePath);
        if (originalData == null) {
            return -1;
        }

        long startingTime = System.nanoTime();
        BitSequence compressedData = compressData(originalData);
        long endingTime = System.nanoTime();

        Path compressedFilePath = originalFilePath.resolveSibling(
                originalFilePath.getFileName() + getExtension());
        if (!FileUtils.writeFile(compressedFilePath,
                compressedData.getBits(), compressedData.getLengthInBytes())) {
            return -1;
        }

        return endingTime - startingTime;
    }

    public abstract BitSequence compressData(byte[] originalData);

    /**
     * Decompresses the file at the given path.
     *
     * @param compressedFilePath Path of the file to be decompressed.
     * @return Time elapsed during decompression in nanoseconds (excluding file
     * reading and writing operations), or a negative number if the
     * decompression operation failed.
     */
    public long decompressFile(Path compressedFilePath) {

        byte[] compressedData = FileUtils.readFile(compressedFilePath);
        if (compressedData == null) {
            return -1;
        }

        long startingTime = System.nanoTime();
        byte[] originalData = decompressData(compressedData);
        long endingTime = System.nanoTime();

        Path originalFilePath = FileUtils.cutPathTail(
                compressedFilePath, getExtension().length());
        if (!FileUtils.writeFile(originalFilePath, originalData)) {
            return -1;
        }

        return endingTime - startingTime;
    }

    public abstract byte[] decompressData(byte[] compressedData);

    /**
     * Returns the file extension used by the CompressionAlgorithm (for
     * compressed files).
     *
     * @return The file extension used by the CompressionAlgorithm (for
     * compressed files).
     */
    public abstract String getExtension();

    /**
     * Returns a short name for the CompressionAlgorithm.
     *
     * @return A short name for the CompressionAlgorithm.
     */
    public abstract String getName();

    /**
     * Returns a description of the CompressionAlgorithm.
     *
     * @return The CompressionAlgorithm's description.
     */
    public abstract String getDescription();
}
