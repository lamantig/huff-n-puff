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
    public static <T> void mergesort(T[] original, Comparator<T> comp) {

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
            System.arraycopy(a, 0, original, 0, n);
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
}
