package domain;

public class TreeRepresentation {

    // all to be interpreted as unsigned bytes
    private final byte[] bytes;
    private final int codewordLengthsLength;
    private final int totalLength;

    /**
     * Leaves are sorted in canonical order.
     * @param leafNodes
     */
    public TreeRepresentation(HuffNode[] leafNodes) {
        // since there's one entry for each length from 1 to maxLength,
        // which is the length of the last leaf's codeword since they are sorted
        codewordLengthsLength = leafNodes[leafNodes.length - 1]
                .getCodeword().getLengthInBits().intValue();
        // this should be bigger than needed (or equal)
        // also note, byte[] are initialized to 0
        bytes = new byte[codewordLengthsLength + leafNodes.length];

        for (HuffNode leaf : leafNodes) {
            // consciously allow "overflow", since bytes are to be interpreted as unsigned,
            // so each of these counts could be up to 255
            // -1 is because length 0 isn't used
            bytes[leaf.getCodeword().getLengthInBits().intValue() - 1]++;
        }

        int i = codewordLengthsLength;
        for (HuffNode leaf : leafNodes) {
            bytes[i++] = leaf.getSymbol();
        }
        totalLength = i;
    }

    /**
     *
     * @param compressedData
     */
    public TreeRepresentation(byte[] compressedData) {
        codewordLengthsLength = Byte.toUnsignedInt(compressedData[Huffman.OFFSET_CWLENGTHS_LENGTH]);
        int symbolsLength = 0;
        // offset where the second part of treerepr starts
        // meaning that first part is cwLengths, second is symbols (or original alphabet)
        // so totalLength = codewordLengthsLength + symbolsLength
        int symbolsOffset = Huffman.OFFSET_TREE + codewordLengthsLength;
        for (int i = Huffman.OFFSET_TREE; i < symbolsOffset; i++) {
            symbolsLength += Byte.toUnsignedInt(compressedData[i]);
        }
        totalLength = codewordLengthsLength + symbolsLength;
        bytes = new byte[totalLength];
        System.arraycopy(compressedData, Huffman.OFFSET_TREE, bytes, 0, totalLength);
    }

    /**
     *
     * @return
     */
    public HuffNode[] buildLeafNodes() {
        int symbolsLength = totalLength - codewordLengthsLength;
        HuffNode[] leafNodes = new HuffNode[symbolsLength];
        // each length l is in bytes[l-1]
        int codewordLength = 1;
        int symbolsIndex = codewordLengthsLength;
        HuffNode leaf;
        for (int i = 0; i < symbolsLength; i++) {
            while (bytes[codewordLength - 1] == 0) {
                codewordLength++;
            }
            leaf = new HuffNode(bytes[symbolsIndex++]);
            leaf.setCodeword(new BitSequence(codewordLength));
            leafNodes[i] = leaf;
            bytes[codewordLength - 1]--;
        }
        return leafNodes;
    }

    /**
     *
     * @return
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     *
     * @return
     */
    public int getCodewordLengthsLength() {
        return codewordLengthsLength;
    }

    /**
     * 
     * @return
     */
    public int getTotalLength() {
        return totalLength;
    }
}
