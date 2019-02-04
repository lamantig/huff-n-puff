package domain;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BitSequenceTest {

    private byte[] bits1,
                   bits2,
                   bits3;

    private final String bitsString0 = "01100001",
                         bitsString1 = "010010111110011011011101010010000001101110010101",
                         bitsString2 = "11001001110001001001000110000111",
                         bitsString3 = "0111100100011101";

    private BitSequence bitSeq;

    private void initBits1() {
        bits1 = new byte[]{
            (byte) 0b01001011,
            (byte) 0b11100110,
            (byte) 0b11011101,
            (byte) 0b01001000,
            (byte) 0b00011011,
            (byte) 0b10010101
        };
    }

    private void initBits2() {
        bits2 = new byte[]{
            (byte) 0b11001001,
            (byte) 0b11000100,
            (byte) 0b10010001,
            (byte) 0b10000111
        };
    }

    private void initBits3() {
        bits3 = new byte[]{
            (byte) 0b01111001,
            (byte) 0b00011101
        };
    }

    @BeforeEach
    public void setUp() {
        initBits1();
        initBits2();
        initBits3();
    }

    @Test
    public void appendBitWorksCorrectlyWhenItDoesNotNeedToExpandBitsArray() {

        int strLen = 11;
        bitSeq = new BitSequence();

        appendBitsFromString(bitSeq, bitsString1, strLen);

        assertEquals(bitsString1.substring(0, strLen), bitSeq.toString());
    }

    @Test
    public void appendBitWorksCorrectlyWhenItNeedsToExpandBitsArray() {

        int freeBits = 3;
        int strLen = 16;
        bitSeq = new BitSequence(bits2, freeBits);

        appendBitsFromString(bitSeq, bitsString1, strLen);

        String bitSeqString = bitsString2.substring(0, bitsString2.length() - freeBits)
                + bitsString1.substring(0, strLen);

        assertEquals(bitSeqString, bitSeq.toString());
    }

    @Test
    public void appendBitSequenceWorksCorrectlyWhenItDoesNotNeedToExpandBitsArray() {

        BitSequence bitSeq03,
                    bitSeq3;

        String bitSeqString03,
               bitSeqString3,
               actualBitSeqString03;

        for (int strLen0 = 0; strLen0 <= bitsString0.length(); strLen0++) {
            for (int freeBits3 = 0; freeBits3 <= Byte.SIZE; freeBits3++) {

                initBits3();

                bitSeq03 = new BitSequence();
                appendBitsFromString(bitSeq03, bitsString0, strLen0);
                bitSeq3 = new BitSequence(bits3, freeBits3);

                bitSeqString03 = bitsString0.substring(0, strLen0);
                bitSeqString3 = bitsString3.substring(0, bitsString3.length() - freeBits3);

                bitSeq03.append(bitSeq3);
                bitSeqString03 += bitSeqString3;

                actualBitSeqString03 = bitSeq03.toString();

                assertEquals(bitSeqString03, actualBitSeqString03, "with strLen0 = "
                        + strLen0 + " and freeBits3 = " + freeBits3 + ", bitSeqString03: ");
            }
        }
    }

    @Test
    public void appendBitSequenceWorksCorrectlyWhenItNeedsToExpandBitsArray() {

        BitSequence bitSeq1,
                    bitSeq12,
                    bitSeq21;

        String bitSeqString1,
               bitSeqString12,
               bitSeqString21,
               actualBitSeqString12,
               actualBitSeqString21;

        for (int freeBits1 = 0; freeBits1 <= Byte.SIZE; freeBits1++) {
            for (int freeBits2 = 0; freeBits2 <= Byte.SIZE; freeBits2++) {

                initBits1();
                initBits2();

                bitSeq1 = new BitSequence(bits1, freeBits1);
                bitSeq12 = new BitSequence(bits1, freeBits1);
                bitSeq21 = new BitSequence(bits2, freeBits2);

                bitSeqString1 = bitsString1.substring(0, bitsString1.length() - freeBits1);
                bitSeqString12 = bitSeqString1;
                bitSeqString21 = bitsString2.substring(0, bitsString2.length() - freeBits2);

                bitSeq12.append(bitSeq21);
                bitSeq21.append(bitSeq1);

                bitSeqString12 += bitSeqString21;
                bitSeqString21 += bitSeqString1;

                actualBitSeqString12 = bitSeq12.toString();
                actualBitSeqString21 = bitSeq21.toString();

                assertEquals(bitSeqString12, actualBitSeqString12, "with freeBits1 = "
                        + freeBits1 + " and freeBits2 = " + freeBits2 + ", bitSeqString12: ");
                assertEquals(bitSeqString21, actualBitSeqString21, "with freeBits1 = "
                        + freeBits1 + " and freeBits2 = " + freeBits2 + ", bitSeqString21: ");
            }
        }
    }

    @Test
    public void getLengthInBitsReturnsCorrectLength() {

        BitSequence bitSeq0,
                    bitSeq1;

        long bitSeqBitLength0,
             bitSeqBitLength1,
             actualBitSeqBitLength0,
             actualBitSeqBitLength1;

        for (int strLen0 = 0; strLen0 <= bitsString0.length(); strLen0++) {

            bitSeq0 = new BitSequence();
            appendBitsFromString(bitSeq0, bitsString0, strLen0);

            bitSeqBitLength0 = bitsString0.substring(0, strLen0).length();
            actualBitSeqBitLength0 = bitSeq0.getLengthInBits();

            assertEquals(bitSeqBitLength0, actualBitSeqBitLength0,
                    "with strLen0 = " + strLen0 + ", bitSeqBitLength0: ");
        }

        for (int freeBits1 = 0; freeBits1 <= Byte.SIZE; freeBits1++) {

            initBits1();
            bitSeq1 = new BitSequence(bits1, freeBits1);

            bitSeqBitLength1 = bitsString1.substring(0, bitsString1.length() - freeBits1).length();
            actualBitSeqBitLength1 = bitSeq1.getLengthInBits();

            assertEquals(bitSeqBitLength1, actualBitSeqBitLength1,
                    "with freeBits1 = " + freeBits1 + ", bitSeqBitLength1: ");
        }
    }

    @Test
    public void nextSequenceReturnsSequenceCorrespondingToNextBinaryNumber() {

        bitSeq = new BitSequence();
        assertEquals("", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("0", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("1", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("10", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("11", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("100", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("101", bitSeq.toString());

        bitSeq = new BitSequence(new byte[]{(byte) 0xFF, (byte) 0xF0}, 4);
        bitSeq = bitSeq.nextSequence();
        assertEquals("1000000000000", bitSeq.toString());

        bitSeq = new BitSequence(new byte[]{(byte) 0b11101100}, 0);
        bitSeq = bitSeq.nextSequence();
        assertEquals("11101101", bitSeq.toString());

        bitSeq = new BitSequence(new byte[]{0, (byte) 0xFF, (byte) 0xFF}, 0);
        bitSeq = bitSeq.nextSequence();
        assertEquals("000000010000000000000000", bitSeq.toString());
    }

    @Test
    public void nextSequencePreservesCorrectAmountOfLeadingZeros() {

        bitSeq = new BitSequence(new byte[]{(byte) 0b00001100, (byte) 0b11111111}, 0);

        bitSeq = bitSeq.nextSequence();
        assertEquals("0000110100000000", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("0000110100000001", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("0000110100000010", bitSeq.toString());

        bitSeq = new BitSequence(new byte[]{(byte) 0b00111111}, 0);

        bitSeq = bitSeq.nextSequence();
        assertEquals("01000000", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("01000001", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("01000010", bitSeq.toString());

        bitSeq = new BitSequence(new byte[]{(byte) 0, (byte) 0, (byte) 0b11000000}, 5);

        bitSeq = bitSeq.nextSequence();
        assertEquals("0000000000000000111", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("0000000000000001000", bitSeq.toString());
        bitSeq = bitSeq.nextSequence();
        assertEquals("0000000000000001001", bitSeq.toString());
    }

    @Test
    public void setReadPositionSetsPositionCorrectlyWhenGivenLegalPosition() {

        byte[] bits;
        for (int position = 0; position < 32; position++) {

            int index = position / Byte.SIZE;
            int offset = position % Byte.SIZE;
            int freeBits = Byte.SIZE - offset;

            bitSeq = new BitSequence(new byte[4], freeBits, index);
            bitSeq.append(true);
            bitSeq.setReadPosition(index, offset);
            assertTrue(bitSeq.readNextBit());

            bits = new byte[4];
            Arrays.fill(bits, (byte) 0b11111111);
            bitSeq = new BitSequence(bits, freeBits, index);
            bitSeq.append(false);
            bitSeq.setReadPosition(index, offset);
            assertFalse(bitSeq.readNextBit());
        }
    }

    @Test
    public void setReadPositionSetsPositionToZeroWhenGivenIllegalPosition() {

        int legalIndex = 1;
        int legalOffset = 5;
        int a = Byte.SIZE + 1;
        // these illegalIndexes and illegalOffset are illegal when combined
        // elementwise, not necessarily by themselves
        int[] illegalIndexes = new int[]{-33, -5, -3, -1,  0, 0, -7, 2, 2, 3};
        int[] illegalOffsets = new int[]{-44, -1,  0,  4, -1, a,  a, 0, 3, 0};
        byte[] bits = new byte[]{(byte) 0b01111111, (byte) 0xFF, (byte) 0, (byte) 0};

        bitSeq = new BitSequence(bits, 0, legalIndex);
        assertFalse(bitSeq.readNextBit());

        for (int i = 0; i < illegalIndexes.length; i++) {
            bitSeq.setReadPosition(legalIndex, legalOffset);
            assertTrue(bitSeq.readNextBit());
            bitSeq.setReadPosition(illegalIndexes[i], illegalOffsets[i]);
            assertFalse(bitSeq.readNextBit());
        }
    }

    @Test
    public void readNextBitReturnsNullWhenReadPositionIsAfterLastBitOfSequence() {

        int index = 2;
        int offset = 3;
        byte[] bits = new byte[]{0, 127, 17, 0};

        bitSeq = new BitSequence(bits, Byte.SIZE - offset, index);
        bitSeq.setReadPosition(index, offset - 1);
        assertNotNull(bitSeq.readNextBit());
        assertNull(bitSeq.readNextBit());
    }

    @Test
    public void getFreeBitsReturnsNumberOfFreeBitsInLastByteOfSequence() {
        for (int freeBits = 0; freeBits <= Byte.SIZE; freeBits++) {
            bitSeq = new BitSequence(bits1, freeBits);
            assertEquals(freeBits % Byte.SIZE, bitSeq.getFreeBits());
        }
    }

    @Test
    public void getBitsReturnsInternalBitsArray() {
        bitSeq = new BitSequence(bits1, 0);
        assertEquals(bits1, bitSeq.getBits());
    }

    @Test
    public void constructorWithLengthInBitsReturnsBitSequenceOfCorrectLength() {
        for (int length = 0; length < 30; length++) {
            bitSeq = new BitSequence(length);
            assertEquals(new Long(length), bitSeq.getLengthInBits());
        }
    }

    @Test
    public void constructorThrowsExceptionWhenCalledWithIllegalLengthInBitsArgument() {
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(-23));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(-1));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(((long) Integer.MAX_VALUE) * Byte.SIZE));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(42000000000L));
    }

    @Test
    public void constructorThrowsExceptionWhenCalledWithIllegalFreeBitsArgument() {
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, -42));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, -1));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, Byte.SIZE + 1));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, 23));
    }

    @Test
    public void constructorThrowsExceptionWhenCalledWithIllegalWriteIndexArgument() {
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, 3, -50));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, 3, -1));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, 3, bits1.length));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, 3, bits1.length + 71));
    }

    @Test
    public void appendIntAppendsCorrectValueAndCorrectNumberOfBits() {

        bitSeq = new BitSequence();
        String bitSeqString = "";

        int[] toBeAppended = {91, 1999, 1, 129214151, 3, 9, 27, 1999327};
        int[] leadingZeroes = {0, 2, 5};

        bitSeqString = appendIntegersToStringAndBitSeq(bitSeqString, bitSeq, toBeAppended, leadingZeroes);
        assertEquals(bitSeqString, bitSeq.toString());
    }

    @Test
    public void readNextIntReadsCorrectValueAndCorrectNumberOfBits() {

        bitSeq = new BitSequence();
        String bitSeqString = "";

        int[] toBeAppended = {833, 270, 259918547, 556711, 8771};
        int[] leadingZeroes = {0, 1, 4};

        bitSeqString = appendIntegersToStringAndBitSeq(bitSeqString, bitSeq, toBeAppended, leadingZeroes);
        assertEquals(bitSeqString, bitSeq.toString());

        for (int a : toBeAppended) {
            for (int lz : leadingZeroes) {
                assertEquals(a, (int) bitSeq.readNextInt(lz + Integer.toBinaryString(a).length()));
            }
        }
    }

    private void appendBitsFromString(BitSequence bitSeq, String bitsString, int strLen) {
        for (int i = 0; i < strLen; i++) {
            if (bitsString.charAt(i) == '0') {
                bitSeq.append(false);
            } else {
                bitSeq.append(true);
            }
        }
    }

    private String appendIntegersToStringAndBitSeq(String bitSeqString,
            BitSequence bitSeq, int[] toBeAppended, int[] leadingZeroes) {

        String newBitSeqString = bitSeqString;
        char[] zeroes;
        String tbaStr;

        for (int tba : toBeAppended) {
            for (int lzs : leadingZeroes) {

                zeroes = new char[lzs];
                Arrays.fill(zeroes, '0');
                tbaStr = new String(zeroes) + Integer.toBinaryString(tba);

                newBitSeqString += tbaStr;
                bitSeq.append(tba, tbaStr.length());
            }
        }

        return newBitSeqString;
    }
}
