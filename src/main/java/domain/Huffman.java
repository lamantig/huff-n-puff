package domain;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Queue;

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
    private static final int POSSIBLE_BYTE_VALUES_COUNT = Byte.MAX_VALUE + 1 - Byte.MIN_VALUE;

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

        // an array containing occurrence counts for each possible byte value
        long[] byteCounts = countByteOccurrences(data);

        // an array with all the leaf nodes, sorted by weight
        HuffNode[] leafNodes = sortedLeafNodes(byteCounts);

        return linearTimeHuffman(leafNodes);
    }

    private static long[] countByteOccurrences(byte[] data) {

        long[] byteCounts = new long[POSSIBLE_BYTE_VALUES_COUNT];

        for (byte byteValue : data) {
            // array indexes have to be positive, so we read the byte as unsigned
            byteCounts[Byte.toUnsignedInt(byteValue)]++;
        }

        return byteCounts;
    }

    private static HuffNode[] sortedLeafNodes(long[] byteCounts) {

        HuffNode[] leafNodes = new HuffNode[POSSIBLE_BYTE_VALUES_COUNT];
        int nodesCount = 0;

        for (int byteValue = 0; byteValue < POSSIBLE_BYTE_VALUES_COUNT; byteValue++) {
            long byteCount = byteCounts[byteValue];
            if (byteCount > 0) {
                // the cast to byte is the reverse of Byte.toUnsignedInt(byteValue)
                leafNodes[nodesCount++] = new HuffNode((byte) byteValue, byteCount);
            }
        }

        leafNodes = Arrays.copyOfRange(leafNodes, 0, nodesCount);
        Arrays.sort(leafNodes, new HuffNode.ByWeight());
        return leafNodes;
    }

    private static HuffNode linearTimeHuffman(HuffNode[] leafNodes) {

        Queue<HuffNode> q1 = new ArrayDeque<>(Arrays.asList(leafNodes));
        Queue<HuffNode> q2 = new ArrayDeque<>();

        HuffNode leftChild, rightChild;

        while (!q1.isEmpty() || q2.size() > 1) {
            /* break ties between queues by choosing the item in the first queue,
            to minimize the variance of codeword length
            (see https://en.wikipedia.org/wiki/Huffman_coding#Compression ) */
            leftChild = chooseNodeFromQueues(q1, q2);
            rightChild = chooseNodeFromQueues(q1, q2);

            q2.offer(new HuffNode(leftChild, rightChild));
        }

        return q2.peek();
    }

    /**
     * Please note, at least one Queue must be not empty. Also ties break in favor of the first queue.
     * @param q1
     * @param q2
     * @return
     */
    private static HuffNode chooseNodeFromQueues(Queue<HuffNode> q1, Queue<HuffNode> q2) {
        HuffNode q1Head = q1.peek();
        HuffNode q2Head = q2.peek();
        Comparator<HuffNode> byWeight = new HuffNode.ByWeight();
        HuffNode chosen;
        if (q1Head == null) {
            chosen = q2.poll();
        } else if (q2Head == null) {
            chosen = q1.poll();
        } else if (new HuffNode.ByWeight().compare(q1Head, q2Head) <= 0) {
            // break ties by choosing the item in q1
            chosen = q1.poll();
        } else {
            chosen = q2.poll();
        }
        return chosen;
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
        HuffNode canonicalHuffmanTreeRoot = computeCanonicalHuffmanTree(data);

        computeCodewords(canonicalHuffmanTreeRoot, new BitSet());

        return data;
    }

    /**
     * A tree DFS for
     * @param root
     */
    private static void computeCodewords(HuffNode node, BitSet code) {

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
