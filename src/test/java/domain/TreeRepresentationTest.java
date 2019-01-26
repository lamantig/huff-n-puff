package domain;

import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TreeRepresentationTest {

    @Test
    public void bothConstructorsBuildTreeRepresentationWhichReturnsTheOriginalHuffNodeArray() {

        HuffNode[] originalLeafNodes = new HuffNode[13];

        byte[] randomSymbols = new byte[originalLeafNodes.length];
        Random r = new Random();
        r.nextBytes(randomSymbols);

        long[] codewordLengths = new long[]{1, 2, 3, 5, 5, 5, 8, 8, 8, 8, 8, 8, 8};

        for (int i = 0; i < originalLeafNodes.length; i++) {
            originalLeafNodes[i] = new HuffNode(randomSymbols[i]);
            originalLeafNodes[i].setCodeword(new BitSequence(codewordLengths[i]));
        }

        TreeRepresentation treeRepresentation = new TreeRepresentation(originalLeafNodes);
        HuffNode[] generatedLeafNodes = treeRepresentation.buildLeafNodes();
        assertTrue(areEqual(originalLeafNodes, generatedLeafNodes));

        int treeRepresentationLength = treeRepresentation.getTotalLength();
        byte[] fakeCompressedData = new byte[Huffman.OFFSET_TREE + treeRepresentationLength];
        fakeCompressedData[Huffman.OFFSET_CWLENGTHS_LENGTH] =
                (byte) treeRepresentation.getCodewordLengthsLength();
        System.arraycopy(treeRepresentation.getBytes(), 0,
                fakeCompressedData, Huffman.OFFSET_TREE, treeRepresentationLength);

        treeRepresentation = new TreeRepresentation(fakeCompressedData);
        generatedLeafNodes = treeRepresentation.buildLeafNodes();
        assertTrue(areEqual(originalLeafNodes, generatedLeafNodes));
    }

    private boolean areEqual(HuffNode[] ln1, HuffNode[] ln2) {
        if (ln1.length != ln2.length) {
            return false;
        }
        for (int i = 0; i < ln1.length; i++) {
            if (!ln1[i].getSymbol().equals(ln2[i].getSymbol())
                    || !ln1[i].getCodeword().getLengthInBits()
                            .equals(ln2[i].getCodeword().getLengthInBits())) {
                return false;
            }
        }
        return true;
    }
}
