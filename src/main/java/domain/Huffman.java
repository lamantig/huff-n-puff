package domain;

import java.nio.file.Path;

/**
 * A class with static methods for file compression/decompression using Huffman
 * coding.
 */
public final class Huffman implements CompressionAlgorithm {

    /**
     * The file extension used for files compressed using the
     * {@link #compressFile(String) compressFile} method.
     */
    public static final String COMPRESSED_FILE_EXTENSION = ".huff";
    public static final String DESCRIPTION = "canonical Huffman coding";

    /**
     * Compresses the file at the given path, using Huffman coding.
     *
     * @param originalFilePath Path of the file to be compressed.
     * @return Time elapsed during compression in nanoseconds (excluding file
     * reading and writing operations), or a negative number if the compression
     * operation failed.
     */
    @Override
    public long compressFile(Path originalFilePath) {

        byte[] originalData = FileUtilities.readFile(originalFilePath);
        if (originalData == null) {
            return -1;
        }

        long startingTime = System.nanoTime();
        byte[] compressedData = compressData(originalData);
        long endingTime = System.nanoTime();

        Path compressedFilePath = originalFilePath.resolveSibling(
                originalFilePath.getFileName() + COMPRESSED_FILE_EXTENSION);
        if (!FileUtilities.writeFile(compressedFilePath, compressedData)) {
            return -1;
        }

        return endingTime - startingTime;
    }

    /**
     * Computes a canonical Huffman tree from the given data.
     *
     * @param data A byte array containing the data to be used for building the
     * tree.
     * @return The root of the tree.
     */
    private static HuffNode computeCanonicalHuffmanTree(byte[] data) {
        return new HuffNode(Byte.MIN_VALUE);
    }

    /**
     * Compresses the given data by first computing a canonical Huffman tree,
     * and then using it as compression code.
     * The compressed data will include (in this order):
     * - a long: length (in bytes) of the Huffman tree representation;
     * - a long: length (in bits) of the compressed representation of the original data;
     * - representation of the canonical Huffman tree;
     * - compressed representation of the original data.
     *
     * @param data The data to be compressed.
     * @return Compressed data.
     */
    private static byte[] compressData(byte[] data) {
        HuffNode canonicalHuffmanTree = computeCanonicalHuffmanTree(data);
        return data;
    }

    /**
     * Decompresses the file at the given path, using Huffman coding.
     *
     * @param compressedFilePath Path of the file to be decompressed.
     * @return True if file decompression succeeds, false otherwise.
     */
    @Override
    public long decompressFile(Path compressedFilePath) {

        byte[] compressedData = FileUtilities.readFile(compressedFilePath);
        if (compressedData == null) {
            return -1;
        }

        long startingTime = System.nanoTime();
        byte[] originalData = decompressData(compressedData);
        long endingTime = System.nanoTime();

        Path originalFilePath = FileUtilities.cutPathTail(
                compressedFilePath, COMPRESSED_FILE_EXTENSION.length());
        if (!FileUtilities.writeFile(originalFilePath, originalData)) {
            return -1;
        }

        return endingTime - startingTime;
    }

    /**
     * Decompresses the given compressed data by first building the canonical
     * Huffman tree that was used for compression (using its representation,
     * included in the compressed data) and then using it as decompression code.
     *
     * @param compressedData The compressed data to be decompressed.
     * @return The original, uncompressed data.
     */
    private static byte[] decompressData(byte[] compressedData) {
        return compressedData;
    }

    /**
     * Returns a description of the Huffman CompressionAlgorithm.
     *
     * @return Time elapsed during decompression in nanoseconds (excluding file
     * reading and writing operations), or a negative number if the
     * decompression operation failed.
     */
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Returns the file extension used by the Huffman CompressionAlgorithm (for
     * compressed files).
     *
     * @return The file extension used by the Huffman CompressionAlgorithm (for
     * compressed files).
     */
    @Override
    public String getExtension() {
        return COMPRESSED_FILE_EXTENSION;
    }
}
