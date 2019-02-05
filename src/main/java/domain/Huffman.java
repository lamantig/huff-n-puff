package domain;

/**
 * A {@link CompressionAlgorithm} implementation which uses canonical Huffman
 * coding. For more details see {@link #compressData(byte[]) compressData} and
 * {@link #decompressData(byte[]) decompressData}.
 */
public final class Huffman extends CompressionAlgorithm {

    /**
     * The file extension used for files compressed using this class.
     */
    private static final String COMPRESSED_FILE_EXTENSION = ".huff";
    /**
     * Short name for this algorithm (used in TUI).
     */
    private static final String NAME = "huffman";
    /**
     * Longer name for this algorithm (used in TUI).
     */
    private static final String DESCRIPTION = "canonical Huffman coding";

    /**
     * Offset (in bytes) from the beginning of compressed files, indicating
     * where the length of the original data (in bytes) will be written.
     */
    public static final int OFFSET_ORIG_DATA_LENGTH = 0;
    /**
     * Offset (in bytes) from the beginning of compressed files, indicating
     * where the value of codewordLenghtsLength (needed in TreeRepresentation)
     * will be written.
     */
    public static final int OFFSET_CWLENGTHS_LENGTH = OFFSET_ORIG_DATA_LENGTH + Integer.BYTES;
    /**
     * Offset (in bytes) from the beginning of compressed files, indicating
     * where the value of freeBits (unused bits in the last byte of the
     * compressed file, see the BitSequence class) will be written.
     */
    private static final int OFFSET_FREEBITS = OFFSET_CWLENGTHS_LENGTH + Byte.BYTES;
    /**
     * Offset (in bytes) from the beginning of compressed files, indicating
     * where the TreeRepresentation of the Huffman tree used for compression
     * will be written.
     */
    public static final int OFFSET_TREE = OFFSET_FREEBITS + Byte.BYTES;

    /**
     * Compresses the given data by first computing a canonical Huffman code,
     * and then using it as compression code. The compressed data will include
     * (in this order):
     * - int: length (in bytes) of the original (uncompressed) file;
     * - unsigned byte: length (in bytes) of the first part (codewordLengths) of
     * the representation of the canonical Huffman tree used for compression
     * (the total length of the tree representation can be calculated from this,
     * see {@link TreeRepresentation});
     * - unsigned byte: unused bits in the last byte of the compressed data
     * (freeBits variable of the BitSequence class);
     * - representation of the canonical Huffman tree;
     * - compressed representation of the original data.
     *
     * @param originalData The data to be compressed.
     * @return A bit sequence corresponding to the compressed data.
     */
    @Override
    public BitSequence compressData(byte[] originalData) {

        HuffNode[] leafNodes = computeCanonicalHuffmanTree(originalData);

        BitSequence[] huffmanCode = extractHuffmanCode(leafNodes);

        TreeRepresentation treeRepresentation = new TreeRepresentation(leafNodes);

        int treeRepresentationLength = treeRepresentation.getTotalLength();
        int dataOffset = OFFSET_TREE + treeRepresentationLength;
        // probably larger than needed (avoids switching to a larger array later on)
        byte[] bits = new byte[dataOffset + originalData.length];

        byte[] originalDataLength = Utils.toByteArray(originalData.length);
        Utils.arrayCopy(originalDataLength, 0,
                bits, OFFSET_ORIG_DATA_LENGTH, originalDataLength.length);
        bits[OFFSET_CWLENGTHS_LENGTH] = (byte) treeRepresentation.getCodewordLengthsLength();
        Utils.arrayCopy(treeRepresentation.getBytes(), 0,
                bits, OFFSET_TREE, treeRepresentationLength);

        BitSequence compressedData = new BitSequence(bits, Byte.SIZE, dataOffset);
        for (byte b : originalData) {
            compressedData.append(huffmanCode[Byte.toUnsignedInt(b)]);
        }

        bits = compressedData.getBits();
        bits[OFFSET_FREEBITS] = (byte) compressedData.getFreeBits();

        return compressedData;
    }

    /**
     * Computes a canonical Huffman tree from the given data.
     *
     * @param data A byte array containing the data to be used for building the
     * tree.
     * @return The root of the tree.
     */
    private static HuffNode[] computeCanonicalHuffmanTree(byte[] data) {

        long[] byteCounts = countByteOccurrences(data);

        HuffNode[] leafNodes = sortedLeafNodes(byteCounts);

        HuffNode huffmanTree = linearTimeHuffman(leafNodes);

        computeCodewords(huffmanTree, new BitSequence());

        Utils.mergeSort(leafNodes, new HuffNode.ByCanonicalOrder());
        convertToCanonical(leafNodes);

        return leafNodes;
    }

    /**
     * Counts how many times each possible byte value occurs in the given data.
     *
     * @param data The data from which byte values occurrences will be counted.
     * @return An array containing occurrence counts for each possible byte
     * value; the count for a given byte value can be found using its unsigned
     * value as an index.
     */
    private static long[] countByteOccurrences(byte[] data) {

        long[] byteCounts = new long[Utils.POSSIBLE_BYTE_VALUES_COUNT];

        for (byte byteValue : data) {
            byteCounts[Byte.toUnsignedInt(byteValue)]++;
        }

        return byteCounts;
    }

    /**
     * Returns an array containing all the leaf nodes (sorted by weight) of the
     * Huffman tree under construction.
     *
     * @param weights Contains the weight of each possible byte value.
     * @return Leaf nodes, sorted by weight.
     */
    private static HuffNode[] sortedLeafNodes(long[] weights) {

        HuffNode[] leafNodes = new HuffNode[Utils.POSSIBLE_BYTE_VALUES_COUNT];
        int nodesCount = 0;

        for (int byteValue = 0; byteValue < Utils.POSSIBLE_BYTE_VALUES_COUNT; byteValue++) {
            long byteCount = weights[byteValue];
            if (byteCount > 0) {
                leafNodes[nodesCount++] = new HuffNode((byte) byteValue, byteCount);
            }
        }

        HuffNode[] resizedLeafNodes = new HuffNode[nodesCount];
        Utils.arrayCopy(leafNodes, 0, resizedLeafNodes, 0, nodesCount);
        Utils.mergeSort(resizedLeafNodes, new HuffNode.ByWeight());
        return resizedLeafNodes;
    }

    /**
     * Builds a Huffman tree, using a known linear time algorithm (time
     * complexity O(n)) which uses two queues and takes as input the leaf nodes
     * sorted by weight (for more info see
     * https://en.wikipedia.org/wiki/Huffman_coding#Compression).
     *
     * @param leafNodes All the leaf nodes of the tree under construction,
     * sorted by weight.
     * @return The root of the built tree.
     */
    private static HuffNode linearTimeHuffman(HuffNode[] leafNodes) {

        SimpleQueue<HuffNode> q1 = new ArrayQueue(leafNodes);
        SimpleQueue<HuffNode> q2 = new ArrayQueue();

        HuffNode leftChild,
                 rightChild;

        while (!q1.isEmpty() || q2.size() > 1) {

            leftChild = chooseNodeFromQueues(q1, q2);
            rightChild = chooseNodeFromQueues(q1, q2);

            q2.offer(new HuffNode(leftChild, rightChild));
        }

        return q2.peek();
    }

    /**
     * Choose the HuffNode with the lowest weight between from the heads of the
     * given queues. Please note: at least one queue must be not empty. Ties
     * break in favor of the first queue, to minimize the variance of codeword
     * length (see https://en.wikipedia.org/wiki/Huffman_coding#Compression).
     *
     * @param q1 The first queue (ties break in favor of this queue); at least
     * one of the two queues must be not empty.
     * @param q2 The second queue (ties break in favor of the other queue); at
     * least one of the two queues must be not empty.
     * @return The queue head with lowest weight.
     */
    private static HuffNode chooseNodeFromQueues(SimpleQueue<HuffNode> q1, SimpleQueue<HuffNode> q2) {

        HuffNode q1Head = q1.peek();
        HuffNode q2Head = q2.peek();

        if (q1Head == null) {
            return q2.poll();
        } else if (q2Head == null) {
            return q1.poll();

        } else if (new HuffNode.ByWeight().compare(q1Head, q2Head) <= 0) {
            return q1.poll();
        } else {
            return q2.poll();
        }
    }

    /**
     * A recursive depth-first tree traversal algorithm for computing codewords
     * of a given Huffman tree. Going to the left child appends a zero, going to
     * the right child appends a one.
     *
     * @param node The current node.
     */
    private static void computeCodewords(HuffNode node, BitSequence codeword) {

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
     * Given the leaf nodes of a Huffman tree, changes the codewords contained
     * in them to obtain a canonical Huffman code. Please note: the leaves
     * should be sorted in canonical order (for more info see
     * https://en.wikipedia.org/wiki/Canonical_Huffman_code).
     *
     * @param leafNodes All the leaf nodes of a Huffman tree; the leaf nodes
     * must be sorted in canonical order.
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

    /**
     * Extracts the Huffman code contained in the given leaf nodes of a Huffman
     * tree.
     *
     * @param leafNodes All the leaf nodes of a Huffman tree.
     * @return An array containing the extracted Huffman code. The codeword of a
     * given byte value (symbol) can be found using its unsigned value as index.
     */
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

        int originalDataLength = Utils.extractInt(compressedData, OFFSET_ORIG_DATA_LENGTH);
        byte[] originalData = new byte[originalDataLength];

        TreeRepresentation treeRepresentation = new TreeRepresentation(compressedData);
        HuffNode huffmanTreeRoot = buildTreeFromRepresentation(treeRepresentation);

        int freeBits = compressedData[OFFSET_FREEBITS];
        BitSequence compressedBitSeq = new BitSequence(compressedData, freeBits);

        int dataOffset = OFFSET_TREE + treeRepresentation.getTotalLength();
        compressedBitSeq.setReadPosition(dataOffset, 0);

        parseData(huffmanTreeRoot, compressedBitSeq, originalData);

        return originalData;
    }

    /**
     * Builds a canonical Huffman tree using the given representation.
     *
     * @param treeRepresentation The representation that will used to build the
     * canonical Huffman tree.
     * @return The root of the Huffman tree that was built.
     */
    private static HuffNode buildTreeFromRepresentation(TreeRepresentation treeRepresentation) {
        HuffNode[] leafNodes = treeRepresentation.buildLeafNodes();
        convertToCanonical(leafNodes);
        HuffNode root = buildTree(leafNodes);
        return root;
    }

    private static HuffNode buildTree(HuffNode[] leafNodes) {
        HuffNode root = new HuffNode(null, null);
        for (HuffNode leaf : leafNodes) {
            buildPath(root, leaf);
        }
        return root;
    }

    /**
     * Builds a path from node fromNode to leaf node toLeaf, using the codeword
     * contained in toLeaf, creating new nodes along the path when they don't
     * exist yet.
     *
     * @param fromNode The starting node of the path to be built.
     * @param toLeaf The destination leaf node of the path to be built; the
     * codeword contained in this node will be used to build the path (zero
     * corresponds to the left child, one corresponds to the right child).
     */
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

    /**
     * Parses compressedDataBS bit by bit with the given Huffman tree, writing
     * the obtained symbols to originalData.
     *
     * @param root The root of the Huffman tree to be used to parse the data.
     * @param compressedDataBS The data to be parsed.
     * @param originalData The array which will contain the symbols obtained
     * while parsing the compressed data.
     */
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
