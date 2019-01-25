package domain;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    /**
     * These array sizes assures that the outermost for-loop in Utils.mergeSort
     * is executed an odd and an even number of times, so that both branches of
     * its last if-statement are tested
     */
    private static final int[] ARRAY_LENGTHS = new int[]{123, 137};
    /**
     * This upper bound assures there will be some repetitions in the randomly
     * generated elements.
     */
    private static final int UPPER_BOUND = 99;

    private final Random r = new Random();

    @Test
    public void mergeSortSortsHuffNodeArraysCorrectly() {

        for (int length : ARRAY_LENGTHS) {

            HuffNode[] a = new HuffNode[length];
            HuffNode[] b = new HuffNode[length];

            for (int i = 0; i < length; i++) {
                a[i] = new HuffNode(Byte.MIN_VALUE, r.nextInt(UPPER_BOUND));
            }
            System.arraycopy(a, 0, b, 0, length);

            Arrays.sort(a, new HuffNode.ByWeight());
            Utils.mergeSort(b, new HuffNode.ByWeight());

            assertArrayEquals(a, b);
        }
    }

    @Test
    public void arrayCopyCopiesWholeIntegerArraysCorrectly() {

        int length = 117;
        Integer[] a = new Integer[length];
        Integer[] b = new Integer[length];

        fillWithRandomNonZeroData(a, length * 5);

        Utils.arrayCopy(a, 0, b, 0, length);

        assertArrayEquals(a, b);
    }

    @Test
    public void arrayCopyDoesNotWriteToIntegerArrayWhenLengthIsZero() {

        int length = 105;
        Integer[] a = new Integer[length];
        Integer[] b = new Integer[length];
        Integer[] empty = new Integer[length];

        fillWithRandomNonZeroData(a, length * 5);

        Utils.arrayCopy(a, 0, b, 0, 0);

        assertArrayEquals(empty, b);
    }

    @Test
    public void arrayCopyCopiesIntegerArrayPartialRangeCorrectly() {

        int totalLength = 119;
        int rangeLength = 5;
        Integer[] a = new Integer[totalLength];
        Integer[] b = new Integer[totalLength];

        fillWithRandomNonZeroData(a, totalLength * 5);

        int i = 4;
        int j = 7;
        int k = 0;
        Utils.arrayCopy(a, j, b, i, rangeLength);

        while (k < i) {
            assertEquals(null, b[k++]);
        }
        while (i < k + rangeLength) {
            assertEquals(a[j++], b[i++]);
        }
        while (i < totalLength) {
            assertEquals(null, b[i++]);
        }
    }

    @Test
    public void arrayCopyCopiesWholeByteArraysCorrectly() {

        int length = 109;
        byte[] a = new byte[length];
        byte[] b = new byte[length];

        r.nextBytes(a);
        setZerosToOne(a);

        Utils.arrayCopy(a, 0, b, 0, length);

        assertArrayEquals(a, b);
    }

    @Test
    public void arrayCopyDoesNotWriteToByteArrayWhenLengthIsZero() {

        int length = 106;
        byte[] a = new byte[length];
        byte[] b = new byte[length];
        byte[] empty = new byte[length];

        r.nextBytes(a);
        setZerosToOne(a);

        Utils.arrayCopy(a, 0, b, 0, 0);

        assertArrayEquals(empty, b);
    }

    @Test
    public void arrayCopyCopiesByteArrayPartialRangeCorrectly() {

        int totalLength = 121;
        int rangeLength = 5;
        byte[] a = new byte[totalLength];
        byte[] b = new byte[totalLength];

        r.nextBytes(a);
        setZerosToOne(a);

        int i = 4;
        int j = 7;
        int k = 0;
        Utils.arrayCopy(a, j, b, i, rangeLength);

        while (k < i) {
            assertEquals(0, b[k++]);
        }
        while (i < k + rangeLength) {
            assertEquals(a[j++], b[i++]);
        }
        while (i < totalLength) {
            assertEquals(0, b[i++]);
        }
    }

    @Test
    public void fillFillsWholeByteArraysCorrectly() {

        int length = 113;
        byte[] a = new byte[length];
        byte[] b = new byte[length];

        Arrays.fill(a, 0, length, (byte) 4);
        Utils.fill(b, 0, length, (byte) 4);

        assertArrayEquals(a, b);
    }

    @Test
    public void fillDoesNotWriteToByteArrayWhenRangeLengthIsZero() {

        int length = 111;
        byte[] a = new byte[length];
        byte[] b = new byte[length];

        Arrays.fill(a, 7, 7, (byte) 9);
        Utils.fill(b, 7, 7, (byte) 9);

        assertArrayEquals(a, b);
    }

    @Test
    public void fillFillsByteArrayPartialRangeCorrectly() {

        int totalLength = 115;
        byte[] a = new byte[totalLength];
        byte[] b = new byte[totalLength];

        Arrays.fill(a, 23, 42, (byte) 35);
        Utils.fill(b, 23, 42, (byte) 35);

        assertArrayEquals(a, b);
    }

    private void fillWithRandomNonZeroData(Integer[] a, int upperBound) {
        for (int i = 0; i < a.length; i++) {
            // no element can be zero
            a[i] = r.nextInt(upperBound) + 1;
        }
    }

    private void setZerosToOne(byte[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) {
                a[i]++;
            }
        }
    }
}
