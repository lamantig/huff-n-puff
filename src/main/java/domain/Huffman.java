package domain;

/**
 * A class with static methods for file compression/decompression using Huffman
 * coding.
 */
public final class Huffman extends CompressionAlgorithm {

    /**
     * The file extension used for files compressed using the
     * {@link #compressFile(String) compressFile} method.
     */
    public static final String COMPRESSED_FILE_EXTENSION = ".huff";
    public static final String NAME = "huffman";
    public static final String DESCRIPTION = "canonical Huffman coding";

    public static final int OFFSET_CWLENGTHS_LENGTH = Integer.BYTES;
    // as unsigned byte
    private static final int OFFSET_FREEBITS = OFFSET_CWLENGTHS_LENGTH + Byte.BYTES;
    // as unsigned byte (it's the same actually since max is 8)
    public static final int OFFSET_TREE = OFFSET_FREEBITS + Byte.BYTES;

    /**
     * Computes a canonical Huffman tree from the given data.
     *
     * @param data A byte array containing the data to be used for building the
     * tree.
     * @return The root of the tree.
     */
    private static HuffNode[] computeCanonicalHuffmanTree(byte[] data) {

        // an array containing occurrence counts for each possible byte value
        long[] byteCounts = countByteOccurrences(data);

        // an array with all the leaf nodes, sorted by weight
        HuffNode[] leafNodes = sortedLeafNodes(byteCounts);

        HuffNode huffmanTree = linearTimeHuffman(leafNodes);

        computeCodewords(huffmanTree, new BitSequence());

        Utils.mergeSort(leafNodes, new HuffNode.ByCanonicalOrder());
        convertToCanonical(leafNodes);

        return leafNodes;
    }

    private static long[] countByteOccurrences(byte[] data) {

        long[] byteCounts = new long[Utils.POSSIBLE_BYTE_VALUES_COUNT];

        for (byte byteValue : data) {
            // array indexes have to be positive, so we read the byte as unsigned
            byteCounts[Byte.toUnsignedInt(byteValue)]++;
        }

        return byteCounts;
    }

    private static HuffNode[] sortedLeafNodes(long[] byteCounts) {

        HuffNode[] leafNodes = new HuffNode[Utils.POSSIBLE_BYTE_VALUES_COUNT];
        int nodesCount = 0;

        for (int byteValue = 0; byteValue < Utils.POSSIBLE_BYTE_VALUES_COUNT; byteValue++) {
            long byteCount = byteCounts[byteValue];
            if (byteCount > 0) {
                // the cast to byte is the reverse of Byte.toUnsignedInt(byteValue)
                leafNodes[nodesCount++] = new HuffNode((byte) byteValue, byteCount);
            }
        }

        HuffNode[] resizedLeafNodes = new HuffNode[nodesCount];
        Utils.arrayCopy(leafNodes, 0, resizedLeafNodes, 0, nodesCount);
        Utils.mergeSort(resizedLeafNodes, new HuffNode.ByWeight());
        return resizedLeafNodes;
    }

    private static HuffNode linearTimeHuffman(HuffNode[] leafNodes) {

        SimpleQueue<HuffNode> q1 = new ArrayQueue<>(leafNodes);
        SimpleQueue<HuffNode> q2 = new ArrayQueue<>();

        HuffNode leftChild,
                 rightChild;

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
    private static HuffNode chooseNodeFromQueues(SimpleQueue<HuffNode> q1, SimpleQueue<HuffNode> q2) {
        HuffNode q1Head = q1.peek();
        HuffNode q2Head = q2.peek();
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
     * Compresses the given data by first computing a canonical Huffman code,
     * and then using it as compression code.
     * The compressed data will include (in this order):
     * - a long: length (in bytes) of the Huffman tree representation;
     * - a long: length (in bits) of the compressed representation of the original data;
     * - representation of the canonical Huffman tree;
     * - compressed representation of the original data.
     *
     * @param originalData The data to be compressed.
     * @return Compressed data.
     */
    @Override
    public BitSequence compressData(byte[] originalData) {
        HuffNode[] leafNodes = computeCanonicalHuffmanTree(originalData);
        BitSequence[] huffmanCode = extractHuffmanCode(leafNodes);
        TreeRepresentation treeRepresentation = new TreeRepresentation(leafNodes);
        int treeRepresentationLength = treeRepresentation.getTotalLength();
        int dataOffset = OFFSET_TREE + treeRepresentationLength;
        // larger then needed, but can be almost sure of no resizing, so faster
        // (and probably less memory use anyway, because avoids resizing big arrays
        // which would imply double memory use of one array, and if garbage collection
        // is slow, maybe even three times as much memory or more)
        byte[] bits = new byte[dataOffset + originalData.length];
        byte[] originalDataLength = Utils.toByteArray(originalData.length);
        Utils.arrayCopy(originalDataLength, 0, bits, 0, originalDataLength.length);
        bits[OFFSET_CWLENGTHS_LENGTH] = (byte) treeRepresentation.getCodewordLengthsLength();
        Utils.arrayCopy(treeRepresentation.getBytes(), 0, bits, OFFSET_TREE, treeRepresentationLength);
        BitSequence compressedData = new BitSequence(bits, Byte.SIZE, dataOffset);
        for (byte b : originalData) {
            compressedData.append(huffmanCode[Byte.toUnsignedInt(b)]);
        }
        bits = compressedData.getBits();
        bits[OFFSET_FREEBITS] = (byte) compressedData.getFreeBits();
        return compressedData;
    }

    /**
     * A recursive tree DFS for computing codewords given a Huffman tree.
     *
     * @param root
     */
    private static void computeCodewords(HuffNode node, BitSequence codeword) {

        if (node == null || codeword == null) {
            return;
        }

        if (node.isLeaf()) {
            node.setCodeword(codeword);
            return;
        }

        BitSequence cwLeft = new BitSequence();
        BitSequence cwRight = new BitSequence();
        cwLeft.append(codeword);
        cwRight.append(codeword);

        cwLeft.append(false);
        cwRight.append(true);
        computeCodewords(node.getLeftChild(), cwLeft);
        computeCodewords(node.getRightChild(), cwRight);
    }

    /**
     * Please note: leafNodes should be sorted in canonical order (see
     * HuffNode.ByCanonicalOrder).
     *
     * @param leafNodes
     */
    private static void convertToCanonical(HuffNode[] leafNodes) {
        BitSequence canonicalCodeword = new BitSequence();
        long previousBitLength;
        long currentBitLength = leafNodes[0].getCodeword().getLengthInBits();
        for (long i = 0; i < currentBitLength; i++) {
            canonicalCodeword.append(false);
        }
        leafNodes[0].setCodeword(canonicalCodeword);
        for (int i = 1; i < leafNodes.length; i++) {
            canonicalCodeword = canonicalCodeword.nextSequence();
            previousBitLength = currentBitLength;
            currentBitLength = leafNodes[i].getCodeword().getLengthInBits();
            for (long j = 0; j < currentBitLength - previousBitLength; j++) {
                canonicalCodeword.append(false);
            }
            leafNodes[i].setCodeword(canonicalCodeword);
        }
    }

    private static BitSequence[] extractHuffmanCode(HuffNode[] leafNodes) {
        BitSequence[] huffmanCode = new BitSequence[Utils.POSSIBLE_BYTE_VALUES_COUNT];
        for (HuffNode leaf : leafNodes) {
            huffmanCode[Byte.toUnsignedInt(leaf.getSymbol())] = leaf.getCodeword();
        }
        return huffmanCode;
    }

    /**
     * Decompresses the given compressed data by first building the canonical
     * Huffman tree that was used for compression (using its representation,
     * included in the compressed data) and then using it as decompression code.
     *
     * @param compressedData The compressed data to be decompressed.
     * @return The original, uncompressed data.
     */
    @Override
    public byte[] decompressData(byte[] compressedData) {
        int originalDataLength = Utils.extractInt(compressedData);
        TreeRepresentation treeRepresentation = new TreeRepresentation(compressedData);
        int freeBits = compressedData[OFFSET_FREEBITS];
        HuffNode huffmanTreeRoot = buildTreeFromRepresentation(treeRepresentation);
        int dataOffset = OFFSET_TREE + treeRepresentation.getTotalLength();
        BitSequence compressedBitSeq = new BitSequence(compressedData, freeBits);
        compressedBitSeq.setReadPosition(dataOffset, 0);
        byte[] originalData = new byte[originalDataLength];
        parseData(huffmanTreeRoot, compressedBitSeq, originalData);
        return originalData;
    }

    // this cwlengthsLentgh is actually only length of the first half of the treerepr
    private static HuffNode buildTreeFromRepresentation(TreeRepresentation treeRepresentation) {
        HuffNode[] leafNodes = treeRepresentation.buildLeafNodes();
        convertToCanonical(leafNodes);
        HuffNode huffmanTree = buildTree(leafNodes);
        return huffmanTree;
    }

    private static HuffNode buildTree(HuffNode[] leafNodes) {
        HuffNode root = new HuffNode(null, null);
        for (HuffNode leaf : leafNodes) {
            buildPath(root, leaf);
        }
        return root;
    }

    private static void buildPath(HuffNode fromNode, HuffNode toLeaf) {
        HuffNode from = fromNode;
        BitSequence codeword = toLeaf.getCodeword();
        long codewordLength = codeword.getLengthInBits();
        HuffNode next;
        for (long i = 0; i < codewordLength - 1; i++) {
            if (codeword.readNextBit()) {
                next = from.getRightChild();
                if (next == null) {
                    next = new HuffNode(null, null);
                    from.setRightChild(next);
                }
            } else {
                next = from.getLeftChild();
                if (next == null) {
                    next = new HuffNode(null, null);
                    from.setLeftChild(next);
                }
            }
            from = next;
        }
        if (codeword.readNextBit()) {
            from.setRightChild(toLeaf);
        } else {
            from.setLeftChild(toLeaf);
        }
    }

    private static void parseData(HuffNode root, BitSequence compressedDataBS, byte[] originalData) {
        HuffNode currentNode = root;
        int i = 0;
        Boolean bit;
        while ((bit = compressedDataBS.readNextBit()) != null) {
            currentNode = bit ? currentNode.getRightChild() : currentNode.getLeftChild();
            if (currentNode.isLeaf()) {
                originalData[i++] = currentNode.getSymbol();
                currentNode = root;
            }
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
