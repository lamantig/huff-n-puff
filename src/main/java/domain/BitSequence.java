package domain;

/**
 * Represents a sequence of bits of arbitrary length.
 */
public class BitSequence {

    /**
     * Default size of array {@link bits}, and its size increment when it needs
     * to be expanded.
     */
    private static final int SIZE_INCREMENT = 64;

    /**
     * Array used to store the bit values of this bit sequence (grouped together
     * as bytes).
     */
    private byte[] bits;
    /**
     * Index of the byte of array {@link bits} that contains the bit that will
     * be read next.
     */
    private int readIndex;
    /**
     * Offset in byte {@link bits}[{@link readIndex}] of the bit that will be
     * read next. It can have values ranging from 0 (the most significant bit on
     * the left), to Byte.SIZE - 1 (the least significant bit on the right).
     */
    private int readOffset;
    /**
     * Index of the byte of array {@link bits} that contains the bit that will
     * be written next.
     */
    private int writeIndex;
    /**
     * Free (unused) bits of byte {@link bits}[{@link writeIndex}]. Used bits
     * are stored into the most significant bits of the byte (meaning into the
     * leftmost bits).
     */
    private int freeBits;
    /**
     * Indicates whether all the bits of all the bytes of array {@link bits} are
     * currently used to store the bits of this bit sequence.
     */
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

        int usedBits = (int) (lengthInBits % Byte.SIZE);
        int lengthInBytes = (int) (lengthInBits / Byte.SIZE);
        if (usedBits == 0) {
            usedBits = Byte.SIZE;
        } else {
            lengthInBytes++;
        }

        init(new byte[lengthInBytes], Byte.SIZE - usedBits);
        initWriteIndex(lengthInBytes - 1);
    }

    /**
     * Returns an instance of BitSequence corresponding to the bits contained in
     * the given array (excluding the last n bits of the last byte in the array,
     * where n is given by parameter freeBits). Will set the free bits of
     * bits[bits.length - 1] to zero. Be careful, since the bits array (given as
     * parameter) isn't copied, instead it is used and modified directly (until
     * it needs to be resized).
     *
     * @param bits Byte array containing the bits of the bit sequence.
     * @param freeBits Amount of free bits in the last byte of the array; it can
     * have values ranging from 0 to Byte.SIZE (both inclusive).
     */
    public BitSequence(byte[] bits, int freeBits) {
        this(bits, freeBits, bits.length - 1);
    }

    /**
     * Returns an instance of BitSequence corresponding to the bits contained in
     * the given array, up to bits[writeIndex] (excluding the last n bits of
     * byte bits[writeIndex], where n is given by parameter freeBits). Will set
     * bits[i] = 0 for all i > writeIndex. Be careful, since the bits array
     * (given as parameter) isn't copied, instead it is used and modified
     * directly (until it needs to be resized).
     *
     * @param bits Byte array containing the bits of the bit sequence.
     * @param freeBits Amount of free bits of byte bits[writeIndex]; it can have
     * values ranging from 0 to Byte.SIZE (both inclusive).
     * @param writeIndex Index of last byte of array bits (given as a parameter)
     * containing bits of the bit sequence; it can have values ranging from 0
     * (inclusive) to bits.length (exclusive).
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
        initWriteIndex(writeIndex);
    }

    /**
     * Appends a bit to this bit sequence.
     *
     * @param bit False for 0, true for 1.
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
     * Appends the bit sequence given as parameter to the end of this bit
     * sequence.
     *
     * @param otherBS Bit sequence to be appended.
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
     * Returns the value of the bit at the reading position, and then increments
     * the reading position by one.
     *
     * @return False for 0, true for 1.
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
     * Returns the bit sequence corresponding to the next binary number. The
     * returned sequence will have the same length of this one (thus preserving
     * possible leading zeros), except when the next binary number cannot be
     * represented with the same amount of bits (in that case, the returned
     * sequence will be one bit longer).
     *
     * @return Bit sequence corresponding to the next binary number.
     */
    public BitSequence nextSequence() {

        int length = getLengthInBytes();

        if (length == 0) {
            BitSequence bitSeq = new BitSequence();
            bitSeq.append(false);
            return bitSeq;
        }

        int index = length - 1;
        int freeBitsInLastUsedByte = freeBits == Byte.SIZE ? 0 : freeBits;
        boolean lastZeroFound = !containsOnlyOnes(index, freeBitsInLastUsedByte);
        byte byteWithLastZero;

        if (lastZeroFound) {
            byteWithLastZero = addOne(bits[index], freeBitsInLastUsedByte);
        } else {
            for (index--; index >= 0; index--) {
                if (lastZeroFound = !containsOnlyOnes(index, 0)) {
                    break;
                }
            }

            if (!lastZeroFound) {
                byte[] nextBits = new byte[length];
                nextBits[0] = (byte) 0b10000000;
                Utils.fill(nextBits, 1, length, (byte) 0);
                return new BitSequence(nextBits, freeBits - 1);
            }

            byteWithLastZero = addOne(bits[index], 0);
        }

        byte[] nextBits = new byte[length];
        Utils.arrayCopy(bits, 0, nextBits, 0, index);
        nextBits[index] = byteWithLastZero;
        index++;
        Utils.fill(nextBits, index, length, (byte) 0);
        return new BitSequence(nextBits, freeBitsInLastUsedByte);
    }

    /**
     * Sets the reading position to the given read position (specified by
     * parameters readIndex and readOffset).
     *
     * @param readIndex Index of the byte array used internally to store the bit
     * sequence; read position will be set at this index.
     * @param readOffset Offset (into the byte at readIndex) of the bit to which
     * the read position will be set; it can have values ranging from 0 (most
     * significant bit on the left) to Byte.SIZE - 1 (least significant bit on
     * the right).
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
     * Returns the number of bytes effectively needed for storing this bit
     * sequence.
     *
     * @return Number of bytes effectively needed for storing this bit sequence.
     */
    public int getLengthInBytes() {
        return freeBits == Byte.SIZE ? writeIndex : writeIndex + 1;
    }

    /**
     * Returns the number of bits in this bit sequence.
     *
     * @return Number of bits in this bit sequence.
     */
    public Long getLengthInBits() {
        return Byte.SIZE * ((long) writeIndex + 1) - freeBits;
    }

    /**
     * Returns the number of free bits in the last byte of this bit sequence.
     *
     * @return Number of free bits in the last byte of this bit sequence,
     * ranging from 0 to Byte.SIZE - 1.
     */
    public int getFreeBits() {
        // it makes sense to show externally that freeBits are zero when they are
        // Byte.SIZE (consider also what getLengthInBytes() returns)
        return freeBits == Byte.SIZE ? 0 : freeBits;
    }

    /**
     * Returns the byte array used internally to store the bits of this bit
     * sequence.
     *
     * @return Byte array used internally to store the bits of this bit
     * sequence.
     */
    public byte[] getBits() {
        return bits;
    }

    private void init(byte[] bits, int freeBits) {
        this.bits = bits;
        readIndex = 0;
        readOffset = 0;
        this.freeBits = freeBits;
    }

    private void initWriteIndex(int writeIndex) {

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
        Utils.fill(bits, fromIndex, bits.length, (byte) 0);
    }

    private void expand(int newLength) {
        byte[] expandedBits = new byte[newLength];
        Utils.arrayCopy(bits, 0, expandedBits, 0, getLengthInBytes());
        bits = expandedBits;
    }

    private void copyWithoutShift(BitSequence otherBS, int thisLength, int otherBSLength) {
        Utils.arrayCopy(otherBS.bits, 0, bits, thisLength, otherBSLength);
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

    /**
     * Checks that the given read position (specified by parameters readIndex
     * and readOffset) is less than the write position (specified by class
     * variables {@link writeIndex} and {@link freeBits}). It assumes that
     * validity conditions readIndex >= 0 and 0 <= readOffset < Byte.SIZE have
     * already been checked. @param readIndex Index of the
     *
     * byte array used internally to store the bit sequence; read position will
     * be set at this index.
     * @param readOffset Offset (into the byte at readIndex) of the bit to which
     * the read position will be set; it can have values ranging from 0 (most
     * significant bit on the left) to Byte.SIZE - 1 (least significant bit on
     * the right).
     * @return True if the given read position is less than this sequence's
     * write position.
     */
    private boolean isLessThanWritePosition(int readIndex, int readOffset) {
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

    private boolean containsOnlyOnes(int index, int freeBits) {
        int allOnes = 0b11111111 << freeBits;
        return bits[index] == (byte) allOnes;
    }

    private byte addOne(byte b, int freeBits) {
        int shiftedB = Byte.toUnsignedInt(b) >>> freeBits;
        shiftedB++;
        return (byte) (shiftedB << freeBits);
    }

    /**
     * Returns a String representing this BitSequence (for debugging purposes).
     *
     * @return A String representing this BitSequence.
     */
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
