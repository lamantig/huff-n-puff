package domain;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HuffNodeTest {

    private HuffNode[] nodes;
    private HuffNode hn;

    @Test
    public void byWeightComparatorComparesNodesByWeightInAscendingOrder() {

        nodes = new HuffNode[5];

        nodes[0] = new HuffNode((byte) 1, 47);
        nodes[1] = new HuffNode((byte) 4, 909003);
        nodes[2] = new HuffNode((byte) 0, 3);
        nodes[3] = new HuffNode((byte) 3, 4489);
        nodes[4] = new HuffNode((byte) 2, 47);

        Arrays.sort(nodes, new HuffNode.ByWeight());

        for (Byte i = 0; i < nodes.length; i++) {
            assertEquals(i, nodes[i].getSymbol());
        }
    }

    @Test
    public void byCanonicalOrderComparatorComparesNodesFirstByCodewordLenghtThenBySymbol() {

        nodes = new HuffNode[6];

        nodes[0] = new HuffNode((byte) 99, 0);
        nodes[0].setCodeword(new BitSequence(2));

        nodes[1] = new HuffNode((byte) 13, 1);
        nodes[1].setCodeword(new BitSequence(7));

        nodes[2] = new HuffNode((byte) 23, 2);
        nodes[2].setCodeword(new BitSequence(3));

        nodes[3] = new HuffNode((byte) 17, 3);
        nodes[3].setCodeword(new BitSequence(4));

        nodes[4] = new HuffNode((byte) 98, 4);
        nodes[4].setCodeword(new BitSequence(2));

        nodes[5] = new HuffNode((byte) 16, 5);
        nodes[5].setCodeword(new BitSequence(4));

        HuffNode[] manuallyOrderedNodes = new HuffNode[nodes.length];
        manuallyOrderedNodes[0] = nodes[4];
        manuallyOrderedNodes[1] = nodes[0];
        manuallyOrderedNodes[2] = nodes[2];
        manuallyOrderedNodes[3] = nodes[5];
        manuallyOrderedNodes[4] = nodes[3];
        manuallyOrderedNodes[5] = nodes[1];

        Arrays.sort(nodes, new HuffNode.ByCanonicalOrder());

        assertArrayEquals(manuallyOrderedNodes, nodes);
    }

    @Test
    public void isLeafReturnsTrueWhenNodeIsCreatedUsingOneOfTheLeafConstructors() {
        hn = new HuffNode(Byte.MIN_VALUE);
        assertTrue(hn.isLeaf());
        hn = new HuffNode(Byte.MAX_VALUE, 17);
        assertTrue(hn.isLeaf());
    }

    @Test
    public void isLeafReturnsFalseWhenNodeIsCreatedUsingTheInternalNodeConstructor() {
        hn = new HuffNode(new HuffNode((byte) 2), new HuffNode((byte) 5));
        assertFalse(hn.isLeaf());
    }
}
