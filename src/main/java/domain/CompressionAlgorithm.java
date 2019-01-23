package domain;

import io.FileUtils;
import java.nio.file.Path;

public abstract class CompressionAlgorithm {

    /**
     * Compresses the file at the given path.
     *
     * @param originalFilePath Path of the file to be compressed.
     * @return True if successful, false otherwise.
     */
    public boolean compressFile(Path originalFilePath) {

        byte[] originalData = FileUtils.readFile(originalFilePath);
        if (originalData == null) {
            return false;
        }

        BitSequence compressedDataBitSeq = compressData(originalData);

        Path compressedFilePath = originalFilePath.resolveSibling(
                originalFilePath.getFileName() + getExtension());

        return FileUtils.writeFile(compressedFilePath, compressedDataBitSeq.getBits(),
                compressedDataBitSeq.getLengthInBytes());
    }

    public abstract BitSequence compressData(byte[] originalData);

    /**
     * Decompresses the file at the given path.
     *
     * @param compressedFilePath Path of the file to be decompressed.
     * @return True if successful, false otherwise.
     */
    public boolean decompressFile(Path compressedFilePath) {

        byte[] compressedData = FileUtils.readFile(compressedFilePath);
        if (compressedData == null) {
            return false;
        }

        byte[] originalData = decompressData(compressedData);

        Path originalFilePath = FileUtils.cutPathTail(
                compressedFilePath, getExtension().length());

        return FileUtils.writeFile(originalFilePath, originalData);
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
