package domain;

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
        BitSequence bitSeq = new BitSequence();

        appendBitsFromString(bitSeq, bitsString1, strLen);

        assertEquals(bitsString1.substring(0, strLen), bitSeq.toString());
    }

    @Test
    public void appendBitWorksCorrectlyWhenItNeedsToExpandBitsArray() {

        int freeBits = 3;
        int strLen = 16;
        BitSequence bitSeq = new BitSequence(bits2, freeBits);

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

    // needs more test cases
    @Test
    public void nextSequenceReturnsSequenceCorrespondingToNextBinaryNumber() {

        BitSequence bitSeq = new BitSequence();
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
    }

    @Test
    public void nextSequencePreservesLeadingZeros() {

    }

    @Test
    public void constructorThrowsExceptionWhenCalledWithIllegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, -42));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, -1));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, Byte.SIZE + 1));
        assertThrows(IllegalArgumentException.class, () -> new BitSequence(bits1, 23));
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
}
