package domain;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    /**
     * These array sizes assures that the outermost for-loop in Utils.mergesort
     * is executed an odd and an even number of times, so that both branches of
     * its last if-statement are tested
     */
    private static final int[] ARRAY_SIZES = new int[]{123, 137};
    /**
     * This upper bound assures there will be some repetitions in the randomly
     * generated elements.
     */
    private static final int UPPER_BOUND = 99;

    @Test
    public void mergeSortSortsIntegerArraysCorrectly() {

        for (int size : ARRAY_SIZES) {

            Random r = new Random();
            Integer[] a = new Integer[size];
            Integer[] b = new Integer[size];

            for (int i = 0; i < size; i++) {
                a[i] = r.nextInt(UPPER_BOUND);
            }
            System.arraycopy(a, 0, b, 0, size);

            Arrays.sort(a);
            Utils.mergesort(b, (e1, e2) -> Integer.compare(e1, e2));

            assertArrayEquals(a, b);
        }
    }
}
