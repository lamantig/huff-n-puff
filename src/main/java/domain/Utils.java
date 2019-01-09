package domain;

import java.util.Comparator;

/**
 * A general purpose utility class.
 */
public class Utils {

    /**
     * Merge sort implementation, adapted from Wikipedia
     * (https://en.wikipedia.org/wiki/Merge_sort).
     *
     * @param <T> Type of the elements to be sorted.
     * @param original Array to be sorted.
     * @param comp Comparator used to get the ordering of the elements.
     */
    public static <T> void mergeSort(T[] original, Comparator<T> comp) {

        int n = original.length;
        T[] a = original;
        T[] b = (T[]) new Object[n];
        T[] temp;

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

    private static <T> void merge(T[] a, int iLeft, int iRight, int iEnd, T[] b, Comparator<T> comp) {

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
}
