package domain;

import java.util.BitSet;

/**
 * Represents a node in a Huffman tree.
 */
public class HuffNode {

    private final Byte symbol;  // symbol of the original alphabet, null if node is not a leaf
    private long weight; // number of occurrences in the original data
    private int length;  // codeword's length (and node's height in the tree)
    private BitSet code; // codeword for this symbol of this node;
    private HuffNode left;  // left child
    private HuffNode right; // right child

    /**
     * Creates a new HuffNode instance corresponding to the given symbol.
     *
     * @param symbol A symbol in the original alphabet (8-bit alphabet); if this
     * HuffNode is an internal node (not a leaf), symbol should be null.
     */
    public HuffNode(Byte symbol) {
        this.symbol = symbol;
    }
}
