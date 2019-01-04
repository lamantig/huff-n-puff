package domain;

import java.util.Arrays;

/**
 * Represents a sequence of bits of arbitrary length.
 */
public class BitSequence {

    private static final int SIZE_INCREMENT = 64;

    private byte[] bits;
    private int readIndex,
                readOffset,
                writeIndex,
                freeBits;
    private boolean full;

    /**
     * Returns an instance of BitSequence of length zero.
     */
    public BitSequence() {
        init(new byte[SIZE_INCREMENT], Byte.SIZE);
        this.writeIndex = 0;
        this.full = false;
    }

    /**
     * Returns an instance of BitSequence of length indicated by lengthInBits;
     * all the bits in the sequence will be set to zero.
     *
     * @param lengthInBits The length in bits of the bit sequence; it must
     * between 0 (inclusive) and Integer.MAX_VALUE * Byte.SIZE (exclusive).
     */
    public BitSequence(long lengthInBits) {
        long maxAllowed = ((long) Integer.MAX_VALUE) * Byte.SIZE - 1;
        if (lengthInBits < 0 || lengthInBits > maxAllowed) {
            throw new IllegalArgumentException("Argument long lengthInBits has to be "
                    + "between 0 and " + maxAllowed + " (both inclusive).");
        }
        writeIndex = (int) (lengthInBits / Byte.SIZE);
        init(new byte[writeIndex + 1], (int) (lengthInBits % Byte.SIZE));
        if (freeBits == 0) {
            freeBits = Byte.SIZE;
        }
    }

    /**
     * Returns an instance of BitSequence corresponding to the bits contained in
     * the given array (excluding the last n bits of the last byte in the array,
     * where n is given by freeBits).
     *
     * @param bits The byte array containing the bits of the bit sequence.
     * @param freeBits The free bits in the last byte of the array; freeBits can
     * have values from 0 to Byte.SIZE (both inclusive).
     */
    public BitSequence(byte[] bits, int freeBits) {
        this(bits, freeBits, bits.length - 1);
    }

    /**
     * Will set bits[i] == 0 for all i > writeIndex.
     *
     * @param bits
     * @param freeBits
     * @param writeIndex
     */
    public BitSequence(byte[] bits, int freeBits, int writeIndex) {

        if (freeBits < 0 || freeBits > Byte.SIZE) {
            throw new IllegalArgumentException("Argument int freeBits has to be "
                    + "between 0 and " + Byte.SIZE + " (both inclusive).");
        }
        if (writeIndex < 0 || writeIndex >= bits.length) {
            throw new IllegalArgumentException("Argument int writeIndex has to be "
                    + "between 0 (inclusive) and bits.length (exclusive).");
        }

        init(bits, freeBits);
        this.writeIndex = writeIndex;
        this.full = false;

        if (this.freeBits == 0) {
            incrementWriteIndex();
            if (!full) {
                clearFreeBytes(this.writeIndex);
            }
        } else {
            clearFreeBits();
            if (this.writeIndex + 1 < bits.length) {
                clearFreeBytes(this.writeIndex + 1);
            }
        }
    }

    /**
     *
     * @param bit
     */
    public void append(boolean bit) {

        if (full) {
            expand(bits.length + SIZE_INCREMENT);
            full = false;
        }

        if (bit) {
            int shift = freeBits - 1;
            int b = Byte.toUnsignedInt(bits[writeIndex]);
            b >>>= shift;
            b++;
            bits[writeIndex] = (byte) (b << shift);
        }

        if (--freeBits == 0) {
            incrementWriteIndex();
        }
    }

    /**
     *
     * @param otherBS
     */
    public void append(BitSequence otherBS) {

        int thisLength = getLengthInBytes();
        int otherBSLength = otherBS.getLengthInBytes();

        if (thisLength + otherBSLength > bits.length) {
            expand(bits.length + Math.max(otherBS.bits.length, SIZE_INCREMENT));
        }

        if (freeBits == Byte.SIZE) {
            copyWithoutShift(otherBS, thisLength, otherBSLength);
        } else {
            copyWithShift(otherBS, otherBSLength);
        }
    }

    /**
     * Reads the next bit at the reading position.
     *
     * @return False if the bit is 0, true if it's 1.
     */
    public Boolean readNextBit() {

        if (isLessThanWritePosition(readIndex, readOffset)) {

            boolean bit = readBit(readIndex, readOffset);
            incrementReadPosition();
            return bit;
        }

        return null;
    }

    /**
     * Returns the bit sequence corresponding to the next binary number
     * (preserving leading zeros).
     *
     * @return The bit sequence corresponding to the next binary number
     * (preserving leading zeros).
     */
    public BitSequence nextSequence() {

        int length = getLengthInBytes();
        if (length == 0) {
            BitSequence bitSeq = new BitSequence();
            bitSeq.append(false);
            return bitSeq;
        }
        int index = length - 1;
        int offset = offsetOfLastZero(index, Byte.SIZE - freeBits - 1);

        if (offset < 0) {
            for (index--; index >= 0; index--) {
                if ((offset = offsetOfLastZero(index, Byte.SIZE - 1)) >= 0) {
                    break;
                }
            }
            if (offset < 0) {
                byte[] nextBits = new byte[length];
                nextBits[0] = (byte) 0b10000000;
                Arrays.fill(nextBits, 1, length, (byte) 0);
                return new BitSequence(nextBits, freeBits - 1);
            }
        }

        byte[] nextBits = new byte[length];
        System.arraycopy(bits, 0, nextBits, 0, index);
        nextBits[index] = setBitToOne(bits[index], offset);
        index++;
        System.arraycopy(bits, index, nextBits, index, length - index);

        return new BitSequence(nextBits, freeBits);
    }

    /**
     * Sets the reading position to the bit corresponding to the given readIndex
     * and readOffset.
     *
     * @param readIndex An index of the byte array used to store the bit
     * sequence.
     * @param readOffset The offset of the bit into the byte at the given index;
     * readOffset can have values between 0 (first bit on the left) and 7 (last
     * bit on the right).
     */
    public void setReadPosition(int readIndex, int readOffset) {

        if (readIndex >= 0 && readOffset >= 0 && readOffset < Byte.SIZE
                && isLessThanWritePosition(readIndex, readOffset)) {
            this.readIndex = readIndex;
            this.readOffset = readOffset;
        } else {
            this.readIndex = 0;
            this.readOffset = 0;
        }
    }

    /**
     * Returns the number of bytes needed for storing this bit sequence.
     *
     * @return The number of bytes needed for storing this bit sequence.
     */
    public int getLengthInBytes() {
        return freeBits == Byte.SIZE ? writeIndex : writeIndex + 1;
    }

    /**
     * Returns the number of bits in this bit sequence.
     *
     * @return The number of bits in this bit sequence.
     */
    public Long getLengthInBits() {
        return Byte.SIZE * ((long) writeIndex + 1) - freeBits;
    }

    /**
     * Returns the number of free bits in the last byte of this bit sequence.
     *
     * @return The number of free bits in the last byte of this bit sequence.
     */
    public int getFreeBits() {
        // it makes sense to show externally that freeBits are zero when they are
        // Byte.SIZE (consider also what getLengthInBytes() returns)
        return freeBits == Byte.SIZE ? 0 : freeBits;
    }

    public byte[] getBits() {
        return bits;
    }

    private void init(byte[] bits, int freeBits) {
        this.bits = bits;
        setReadPosition(0, 0);
        this.freeBits = freeBits;
    }

    private void incrementWriteIndex() {
        freeBits = Byte.SIZE;
        if (++writeIndex == bits.length) {
            full = true;
        }
    }

    private void clearFreeBits() {
        int mask = 0xFF;
        mask <<= freeBits;
        bits[writeIndex] &= mask;
    }

    private void clearFreeBytes(int fromIndex) {
        Arrays.fill(bits, fromIndex, bits.length, (byte) 0);
    }

    private void expand(int newLength) {
        byte[] expandedBits = new byte[newLength];
        System.arraycopy(bits, 0, expandedBits, 0, getLengthInBytes());
        bits = expandedBits;
    }

    private void copyWithoutShift(BitSequence otherBS, int thisLength, int otherBSLength) {
        System.arraycopy(otherBS.bits, 0, bits, thisLength, otherBSLength);
        freeBits = otherBS.freeBits;
        writeIndex += otherBS.writeIndex;
    }

    private void copyWithShift(BitSequence otherBS, int otherBSLength) {

        int usedBits = Byte.SIZE - freeBits;
        int currentByte = Byte.toUnsignedInt(bits[writeIndex]);

        for (int i = 0; i < otherBSLength; i++) {
            int nextByte = Byte.toUnsignedInt(otherBS.bits[i]);
            bits[writeIndex++] = (byte) (currentByte | (nextByte >>> usedBits));
            currentByte = nextByte << freeBits;
        }
        bits[writeIndex] = (byte) currentByte;

        int updatedFreeBits = freeBits + otherBS.freeBits;
        if (updatedFreeBits > Byte.SIZE) {
            updatedFreeBits -= Byte.SIZE;
            if (otherBS.freeBits != Byte.SIZE) {
                writeIndex--;
            }
        }
        freeBits = updatedFreeBits;
    }

    private boolean isLessThanWritePosition(int readIndex, int readOffset) {
        // read position is already assumed to be legal, meaning
        // readIndex >= 0 and  0 <= readOffset < Byte.SIZE
        return readIndex < writeIndex
                || (readIndex == writeIndex && readOffset < Byte.SIZE - freeBits);
    }

    private boolean readBit(int index, int offset) {
        byte bit = bits[index];
        bit <<= offset;
        return bit < 0;
    }

    private boolean readBit(long bitIndex) {
        int arrayIndex = (int) bitIndex / Byte.SIZE;
        int offset = (int) bitIndex % Byte.SIZE;
        return readBit(arrayIndex, offset);
    }

    private void incrementReadPosition() {
        if (++readOffset == Byte.SIZE) {
            readIndex++;
            readOffset = 0;
        }
    }

    private int offsetOfLastZero(int index, int startingOffset) {
        for (; startingOffset >= 0; startingOffset--) {
            if (!readBit(index, startingOffset)) {
                return startingOffset;
            }
        }
        return -1;
    }

    private byte setBitToOne(byte b, int offset) {
        int mask = 1 << (Byte.SIZE - offset - 1);
        return (byte) (b | mask);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        long length = getLengthInBits();

        for (long i = 0; i < length; i++) {
            sb.append(readBit(i) ? '1' : '0');
        }

        return sb.toString();
    }
}
