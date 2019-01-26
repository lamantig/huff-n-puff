package domain;

/**
 * Represents a canonical Huffman tree. The second part (called "symbols") of
 * this representation contains all symbols in canonical order (so firstly by
 * bit-length of their codeword, and secondly by alphabetical order); the first
 * part (called "codewordLengths") contains the number of symbols for each
 * bit-length. See Wikipedia for more details:
 * https://en.wikipedia.org/wiki/Canonical_Huffman_code#Encoding_the_codebook
 */
public class TreeRepresentation {

    /**
     * Array used to store this tree representation; each byte is to be
     * interpreted as unsigned, meaning that each element of this array can have
     * values ranging from 0 to 255 (both inclusive). The special case when 256
     * codewords have the same length (which will then be Byte.SIZE) is handled
     * by setting {@link codewordLengthsLength} to zero (since in that case we
     * know there are 256 different symbols and their codeword lengths are all
     * Byte.SIZE).
     */
    private final byte[] bytes;
    /**
     * Length (in bytes) of the first part of this tree representation, where
     * the counts for each codeword length are stored.
     */
    private final int codewordLengthsLength;
    /**
     * Constant to be used in the special case when there are 256 symbols with
     * codeword of length Byte.SIZE (and codewordLengthsLength is set to zero).
     */
    private static final int SPECIAL_CASE = (int) Math.pow(2, Byte.SIZE);

    /**
     * Returns an instance of TreeRepresentation corresponding to the Huffman
     * tree whose leaves are found in array leafNodes (given as parameter).
     *
     * @param leafNodes Leaves of the Huffman tree of which we want a
     * representation; this array must be sorted in canonical order (so firstly
     * by bit-length of their codeword, and secondly by alphabetical order; see
     * the HuffNode class for more info).
     */
    public TreeRepresentation(HuffNode[] leafNodes) {

        // maxLength is the length of the last leaf's codeword, since they are sorted
        int maxLength = leafNodes[leafNodes.length - 1]
                .getCodeword().getLengthInBits().intValue();

        if (maxLength == Byte.SIZE && leafNodes.length == SPECIAL_CASE) {
            // special case when there are 256 symbols with codeword of length Byte.SIZE
            codewordLengthsLength = 0;
            bytes = new byte[leafNodes.length];
            for (int i = 0; i < leafNodes.length; i++) {
                bytes[i] = leafNodes[i].getSymbol();
            }
        } else {
            // since there's one entry for each length from 1 to maxLength
            codewordLengthsLength = maxLength;
            bytes = new byte[codewordLengthsLength + leafNodes.length];

            int i = codewordLengthsLength;
            for (HuffNode leaf : leafNodes) {
                // consciously allow "overflow", since bytes are to be interpreted as unsigned
                // length 0 isn't used, so the count for each codeword length l is in bytes[l-1]
                bytes[leaf.getCodeword().getLengthInBits().intValue() - 1]++;
                bytes[i++] = leaf.getSymbol();
            }
        }
    }

    /**
     * Returns an instance of TreeRepresentation built from array compressedData (given as a parameter).
     *
     * @param compressedData Array used to build the tree representation. It
     * must have been encoded by the Huffman class.
     */
    public TreeRepresentation(byte[] compressedData) {

        codewordLengthsLength = Byte.toUnsignedInt(compressedData[Huffman.OFFSET_CWLENGTHS_LENGTH]);

        int symbolsLength;
        if (codewordLengthsLength == 0) {
            // special case when there are 256 symbols with codeword of length Byte.SIZE
            symbolsLength = SPECIAL_CASE;
        } else {
            symbolsLength = 0;
            // offset where the second part of treerepr starts
            // meaning that first part is cwLengths, second is symbols (or original alphabet)
            // so totalLength = codewordLengthsLength + symbolsLength
            int symbolsOffset = Huffman.OFFSET_TREE + codewordLengthsLength;
            for (int i = Huffman.OFFSET_TREE; i < symbolsOffset; i++) {
                symbolsLength += Byte.toUnsignedInt(compressedData[i]);
            }
        }

        int totalLength = codewordLengthsLength + symbolsLength;
        bytes = new byte[totalLength];
        Utils.arrayCopy(compressedData, Huffman.OFFSET_TREE, bytes, 0, totalLength);
    }

    /**
     * Returns an array containing the leaves of the Huffman tree this is a
     * representation of.
     *
     * @return Leaves (HuffNodes) of the Huffman tree this is a representation
     * of.
     */
    public HuffNode[] buildLeafNodes() {

        int symbolsLength = bytes.length - codewordLengthsLength;
        HuffNode[] leafNodes = new HuffNode[symbolsLength];
        HuffNode leaf;

        if (codewordLengthsLength == 0) {
            // special case when there are 256 symbols with codeword of length Byte.SIZE
            for (int i = 0; i < symbolsLength; i++) {
                leaf = new HuffNode(bytes[i]);
                leaf.setCodeword(new BitSequence(Byte.SIZE));
                leafNodes[i] = leaf;
            }
        } else {

            int symbolsIndex = codewordLengthsLength;
            int leafNodesIndex = 0;
            int codewordLength = 1;

            while (leafNodesIndex < symbolsLength) {
                // the count for each codeword length l is in bytes[l-1]
                while (bytes[codewordLength - 1] == 0) {
                    codewordLength++;
                }

                for (int i = Byte.toUnsignedInt(bytes[codewordLength - 1]); i > 0; i--) {
                    leaf = new HuffNode(bytes[symbolsIndex++]);
                    leaf.setCodeword(new BitSequence(codewordLength));
                    leafNodes[leafNodesIndex++] = leaf;
                }

                codewordLength++;
            }
        }

        return leafNodes;
    }

    /**
     * Returns the byte array used internally to store this tree representation.
     *
     * @return Array used internally to store this tree representation.
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Returns the length (in bytes) of the first part of this tree
     * representation, where the counts for each codeword length are stored.
     *
     * @return Length (in bytes) of the first part of this tree representation,
     * where the counts for each codeword length are stored.
     */
    public int getCodewordLengthsLength() {
        return codewordLengthsLength;
    }

    /**
     * Returns the total length (in bytes) of this tree representation.
     *
     * @return Total length (in bytes) of this tree representation (equal to
     * {@link getBytes()}.length).
     */
    public int getTotalLength() {
        return bytes.length;
    }
}
