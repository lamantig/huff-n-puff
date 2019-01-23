package domain;

import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

public class ByteSequenceTest {

    private static final Random R = new Random();
    private static final byte[] BYTES = new byte[15];
    private ByteSequence bs1;
    private ByteSequence bs2;

    @BeforeAll
    public static void setUp() {
        R.nextBytes(BYTES);
    }

    @Test
    public void equalsReturnsTrueWhenByteSequencesContainSameBytesInSameOrder() {
        bs1 = new ByteSequence();
        bs2 = new ByteSequence();
        for (byte b : BYTES) {
            bs1.append(b);
            bs2.append(b);
        }
        assertTrue(bs1.equals(bs2));
    }

    @Test
    public void equalsReturnsFalseWhenGivenNullArgument() {
        bs1 = new ByteSequence();
        bs2 = null;
        assertFalse(bs1.equals(bs2));
    }

    @Test
    public void equalsReturnsFalseWhenGivenObjectOfDifferentClass() {
        bs1 = new ByteSequence();
        Object obj = "";
        assertFalse(bs1.equals(obj));
    }

    @Test
    public void makeCloneReturnsNewInstanceWhichIsEqualToTheOldOne() {
        bs1 = new ByteSequence(BYTES);
        bs2 = bs1.makeClone();
        assertFalse(bs1 == bs2);
        assertEquals(bs1, bs2);
    }

    @Test
    public void copyToCopiesByteSequenceIntoSpecifiedPositionOfByteArrayAndReturnsNumberOfCopiedBytes() {
        bs1 = new ByteSequence(BYTES);
        int fromIndex = 7;
        byte[] ba = new byte[BYTES.length + fromIndex + 4];
        assertEquals(BYTES.length, bs1.copyTo(ba, fromIndex));
        for (int i = 0; i < BYTES.length; i++) {
            assertEquals(BYTES[i], ba[i + fromIndex]);
        }
    }

    @Test
    public void resetResetsByteSequenceLengthToZero() {
        bs1 = new ByteSequence();
        for (byte b = 0; b < 7; b++) {
            bs1.append(b);
        }
        bs2 = new ByteSequence();
        assertNotEquals(bs2, bs1);
        bs1.reset();
        assertEquals(bs2, bs1);
    }

    @Test
    public void getFirstReturnsFirstByteOfSequence() {
        bs1 = new ByteSequence();
        byte first = 19;
        byte b = first;
        bs1.append(first);
        assertEquals(first, bs1.getFirst());
        while (++b < 24) {
            bs1.append(b);
        }
        assertEquals(first, bs1.getFirst());
    }

    @Test
    public void getLastReturnsLastByteOfSequence() {
        bs1 = new ByteSequence();
        for (byte b = 117; b < 123; b++) {
            bs1.append(b);
            assertEquals(b, bs1.getLast());
        }
    }
}
