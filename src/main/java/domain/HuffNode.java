package domain;

import java.util.Comparator;

/**
 * Represents a node in a Huffman tree.
 */
public class HuffNode {

    /**
     * Symbol of the original alphabet, null if node is not a leaf.
     */
    private final Byte symbol;
    /**
     * Number of occurrences of the symbol in the original data (if node is a
     * leaf), or sum of the weights of this node's children (if node is
     * internal).
     */
    private final Long weight;
    /**
     * BitSequence for the symbol represented by this node (its length is also
     * the node's height in the tree).
     */
    private BitSequence codeword;

    private HuffNode leftChild,
                     rightChild;

    /**
     * Compares HuffNodes by weight, in ascending order.
     */
    public static class ByWeight implements Comparator<HuffNode> {
        @Override
        public int compare(HuffNode hn1, HuffNode hn2) {
            return hn1.weight.compareTo(hn2.weight);
        }
    }

    /**
     * Compares HuffNodes by canonical order: first by codeword length (in
     * ascending order), then by symbol (in alphabetical order). Here we use the
     * byte alphabet, so the alphabetical order is the numerical ascending order
     * of the bytes interpreted as unsigned (for more info see
     * https://en.wikipedia.org/wiki/Canonical_Huffman_code).
     */
    public static class ByCanonicalOrder implements Comparator<HuffNode> {
        @Override
        public int compare(HuffNode hn1, HuffNode hn2) {
            // first by codeword length
            int byCodewordLength = hn1.codeword.getLengthInBits()
                    .compareTo(hn2.codeword.getLengthInBits());
            if (byCodewordLength == 0) {
                // then by symbol in alphabetical order
                return Byte.toUnsignedInt(hn1.symbol) - Byte.toUnsignedInt(hn2.symbol);
            }
            return byCodewordLength;
        }
    }

    /**
     * Constructor for leaf nodes. It creates a new HuffNode instance
     * corresponding to the given symbol.
     *
     * @param symbol A symbol in the original alphabet (8-bit alphabet); if this
     * HuffNode is an internal node (not a leaf), symbol should be null.
     */
    public HuffNode(Byte symbol) {
        this(symbol, 0);
    }

    /**
     * Constructor for leaf nodes. It creates a new HuffNode instance
     * corresponding to the given symbol.
     *
     * @param symbol A symbol in the original alphabet (8-bit alphabet); if this
     * HuffNode is an internal node (not a leaf), symbol should be null.
     * @param weight Number of occurrences of the given symbol in the original
     * data.
     */
    public HuffNode(Byte symbol, long weight) {
        this.symbol = symbol;
        this.weight = weight;
        this.leftChild = null;
        this.rightChild = null;
    }

    /**
     * Constructor for internal nodes. It creates a new HuffNode instance
     * corresponding to the given symbol.
     *
     * @param leftChild The node's left child.
     * @param rightChild The node's right child.
     */
    public HuffNode(HuffNode leftChild, HuffNode rightChild) {
        this.symbol = null;
        this.weight = (leftChild != null ? leftChild.weight : 0)
                + (rightChild != null ? rightChild.weight : 0);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    /**
     * Returns true if this node is a leaf node. This means that it was created
     * using a constructor for leaf nodes, and thus {@link #getSymbol()} will
     * return a non-null value.
     *
     * @return True if this node is a leaf node, and thus its variable symbol
     * has a non-null value.
     */
    public boolean isLeaf() {
        return symbol != null;
    }

    public void setCodeword(BitSequence codeword) {
        this.codeword = codeword;
    }

    public void setLeftChild(HuffNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(HuffNode rightChild) {
        this.rightChild = rightChild;
    }

    public Byte getSymbol() {
        return symbol;
    }

    public BitSequence getCodeword() {
        return codeword;
    }

    public HuffNode getLeftChild() {
        return leftChild;
    }

    public HuffNode getRightChild() {
        return rightChild;
    }
}
