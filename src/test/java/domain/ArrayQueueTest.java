package domain;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

public class ArrayQueueTest {

    private SimpleQueue<HuffNode> q;

    private static final Random R = new Random();
    private static final HuffNode[] ELEM_1 = new HuffNode[23];
    private static final HuffNode[] ELEM_2 = new HuffNode[17];

    @BeforeAll
    public static void setUp() {
        for (HuffNode[] elem : new HuffNode[][]{ELEM_1, ELEM_2}) {
            for (int i = 0; i < elem.length; i++) {
                elem[i] = new HuffNode((byte) R.nextInt(256));
            }
        }
    }

    @Test
    public void isEmptyReturnsTrueWhenQueueIsEmptyAndFalseOtherwise() {
        q = new ArrayQueue();
        HuffNode e = ELEM_1[0];
        assertTrue(q.isEmpty());
        assertTrue(q.offer(e));
        assertFalse(q.isEmpty());
        assertEquals(e, q.poll());
        assertTrue(q.isEmpty());
    }

    @Test
    public void pollReturnsFirstElementRemovingItOrNullIFQueueIsEmpty() {
        q = new ArrayQueue();
        assertNull(q.poll());
        for (HuffNode e : ELEM_2) {
            assertTrue(q.offer(e));
        }
        assertEquals(ELEM_2[0], q.poll());
    }

    @Test
    public void offerExpandsInternalArrayWhenFull() {
        HuffNode[] elements = Arrays.copyOf(ELEM_1, ELEM_1.length);
        q = new ArrayQueue(elements);
        for (HuffNode e : ELEM_2) {
            assertTrue(q.offer(e));
        }
        for (HuffNode[] elem : new HuffNode[][]{ELEM_1, ELEM_2}) {
            for (HuffNode e : elem) {
                assertEquals(e, q.poll());
            }
        }
    }

    @Test
    public void peekReturnsFirstElementWithoutRemovingItOrNullIFQueueIsEmpty() {
        q = new ArrayQueue();
        assertNull(q.peek());
        for (HuffNode e : ELEM_1) {
            assertTrue(q.offer(e));
        }
        for (int i = 0; i < ELEM_1.length * 2; i++) {
            assertEquals(ELEM_1[0], q.peek());
        }
        for (HuffNode e : ELEM_1) {
            assertEquals(e, q.poll());
        }
        assertTrue(q.isEmpty());
        assertNull(q.peek());
    }

    @Test
    public void sizeReturnsQueueSize() {
        q = new ArrayQueue();
        assertEquals(0, q.size());
        int elementCount = R.nextInt(ELEM_1.length);
        for (int i = 0; i < elementCount; i++) {
            assertTrue(q.offer(ELEM_1[i]));
        }
        assertEquals(elementCount, q.size());
        for (int i = 0; i < elementCount; i++) {
            q.poll();
        }
        assertEquals(0, q.size());
    }
}
