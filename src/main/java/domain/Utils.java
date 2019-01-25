package domain;

import java.util.Comparator;

/**
 * A general purpose utility class.
 */
public class Utils {

    public static final int POSSIBLE_BYTE_VALUES_COUNT = Byte.MAX_VALUE + 1 - Byte.MIN_VALUE;

    /**
     * Merge sort implementation, adapted from Wikipedia
     * (https://en.wikipedia.org/wiki/Merge_sort).
     *
     * @param original Array to be sorted.
     * @param comp Comparator used to get the ordering of the elements.
     */
    public static void mergeSort(HuffNode[] original, Comparator<HuffNode> comp) {

        int n = original.length;
        HuffNode[] a = original;
        HuffNode[] b = new HuffNode[n];
        HuffNode[] temp;

        for (int width = 1; width < n; width *= 2) {

            for (int i = 0; i < n; i += 2 * width) {
                merge(a, i, Math.min(i + width, n), Math.min(i + 2 * width, n), b, comp);
            }

            temp = a;
            a = b;
            b = temp;
        }

        if (a != original) {
            arrayCopy(a, 0, original, 0, n);
        }
    }

    private static void merge(HuffNode[] a, int iLeft, int iRight, int iEnd,
            HuffNode[] b, Comparator<HuffNode> comp) {

        int i = iLeft;
        int j = iRight;

        for (int k = iLeft; k < iEnd; k++) {

            if (i < iRight && (j >= iEnd || comp.compare(a[i], a[j]) <= 0)) {
                b[k] = a[i++];
            } else {
                b[k] = a[j++];
            }
        }
    }

    /**
     * Copies data from an array to another one. It works like System.arraycopy
     * (but more slowly), so for more details see that method's documentation.
     *
     * @param src Source array.
     * @param srcPos Starting position in the source array.
     * @param dest Destination array.
     * @param destPos Starting position in the destination array.
     * @param length Number of elements to be copied.
     */
    public static void arrayCopy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {

        int i = srcPos;
        int j = destPos;
        int limit = srcPos + length;

        while (i < limit) {
            dest[j++] = src[i++];
        }
    }

    /**
     * Copies data from an array to another one. It works like System.arraycopy
     * (but more slowly), so for more details see that method's documentation.
     *
     * @param src Source array.
     * @param srcPos Starting position in the source array.
     * @param dest Destination array.
     * @param destPos Starting position in the destination array.
     * @param length Number of elements to be copied.
     */
    public static void arrayCopy(Object[] src, int srcPos, Object[] dest, int destPos, int length) {

        int i = srcPos;
        int j = destPos;
        int limit = srcPos + length;

        while (i < limit) {
            dest[j++] = src[i++];
        }
    }

    /**
     * Fills the given range of the given array with the given value. It works
     * like Arrays.fill.
     *
     * @param a Array to be filled.
     * @param fromIndex Initial index of the range to be filled (inclusive).
     * @param toIndex Final index of the range to be filled (exclusive).
     * @param val Value that will be used to fill the array.
     */
    public static void fill(byte[]a, int fromIndex, int toIndex, byte val) {
        for (int i = fromIndex; i < toIndex; i++) {
            a[i] = val;
        }
    }

    /**
     * Converts an integer into a byte array; the integer's bytes are in
     * big-endian order.
     *
     * @param k Integer to be converted.
     * @return An array containing the given integer's bytes in big-endian
     * order.
     */
    public static byte[] toByteArray(int k) {
        byte[] bytes = new byte[Integer.BYTES];
        for (int i = 0; i < Integer.BYTES; i++) {
            bytes[Integer.BYTES - 1 - i] = (byte) (k >>> (i * Byte.SIZE));
        }
        return bytes;
    }

    /**
     * Takes the first 4 bytes of a byte array and converts them to the
     * corresponding integer; the bytes are interpreted in big-endian order.
     *
     * @param bytes Array from which the integer will be extracted.
     * @return An integer corresponding to the first 4 bytes of the given array
     * (in big-endian order).
     */
    public static int extractInt(byte[] bytes) {
        int k = 0;
        for (int i = 0; i < Integer.BYTES; i++) {
            k |= Byte.toUnsignedInt(bytes[Integer.BYTES - 1 - i]) << (i * Byte.SIZE);
        }
        return k;
    }
}
