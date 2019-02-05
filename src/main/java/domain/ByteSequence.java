package domain;

/**
 * Represents a sequence of bytes. Constructor parameters hash table size and
 * hash factor determine how the {@link #hashCode()} will be calculated, using a
 * rolling hash formula like the one in this Wikipedia article
 * https://en.wikipedia.org/wiki/Rolling_hash#Polynomial_rolling_hash
 * (where a = hashFactor, and n = hashTableSize).
 */
public class ByteSequence {

    private final int hashTableSize;
    private static final int DEFAULT_HASH_TABLE_SIZE = LZW.DEFAULT_HASH_TABLE_SIZE;

    private final int hashFactor;
    private static final int DEFAULT_HASH_FACTOR = LZW.HASH_FACTOR;

    private static final int SIZE_INCREMENT = 7;

    private byte[] bytes;
    private int length;

    /**
     * Returns an instance of ByteSequence with length zero and default values
     * for hash table size and hash factor.
     */
    public ByteSequence() {
        this(DEFAULT_HASH_TABLE_SIZE, DEFAULT_HASH_FACTOR);
    }

    /**
     * Returns an instance of ByteSequence whose {@link #hashCode()} method will
     * use the given hash table size and hash factor. The new ByteSequence will
     * have length zero.
     *
     * @param hashTableSize Size of the hash table that will be used to
     * calculate the hash code.
     * @param hashFactor Hash factor that will be used to calculate the hash
     * code.
     */
    public ByteSequence(int hashTableSize, int hashFactor) {
        this.hashTableSize = hashTableSize;
        this.hashFactor = hashFactor;
        bytes = new byte[SIZE_INCREMENT];
        length = 0;
    }

    /**
     * Returns an instance of ByteSequence corresponding to the given byte
     * array, meaning that the new ByteSequence will be as long as the given
     * array, and will consist of the same byte values which are found in the
     * given array, in the same order. The given array isn't copied, instead it
     * is used directly, so be careful. The new ByteSequence will use default
     * values for hash table size and hash factor.
     *
     * @param bytes The array on which the new ByteSequence will be based on.
     */
    public ByteSequence(byte[] bytes) {
        hashTableSize = DEFAULT_HASH_TABLE_SIZE;
        hashFactor = DEFAULT_HASH_FACTOR;
        this.bytes = bytes;
        this.length = bytes.length;
    }

    /**
     * Appends the given byte value to the end of this ByteSequence.
     *
     * @param b Byte value to be appended.
     */
    public void append(byte b) {
        if (length == bytes.length) {
            expand();
        }
        bytes[length++] = b;
    }

    /**
     * Returns a clone of this ByteSequence, which will use a new array
     * internally, and thus will be completely independent from this
     * ByteSequence. The new ByteSequence will have the same length, and will
     * contain the same byte values in the same order.
     *
     * @return A clone of this ByteSequence.
     */
    public ByteSequence makeClone() {
        byte[] newBytes = new byte[length];
        Utils.arrayCopy(bytes, 0, newBytes, 0, length);
        return new ByteSequence(newBytes);
    }

    /**
     * Copies the byte values of this ByteSequence into the given array,
     * starting from the given index, meaning that the first byte of this
     * sequence will be copied into a[fromIndex]. It returns the number of
     * copied elements (which is equal to the length of this ByteSequence).
     *
     * @param a Array into which the byte values of this ByteSequence will be
     * copied.
     * @param fromIndex Index of the given array indicating the starting point
     * where the byte values of this sequence will be copied.
     * @return The number of copied bytes.
     */
    public int copyTo(byte[] a, int fromIndex) {
        Utils.arrayCopy(bytes, 0, a, fromIndex, length);
        return length;
    }

    /**
     * Resets this ByteSequence (sets its length to zero).
     */
    public void reset() {
        length = 0;
    }

    /**
     * Returns the first byte of this ByteSequence. It doesn't check for
     * emptiness, so be careful not to call this on an empty sequence.
     *
     * @return The first byte of this ByteSequence (don't call this on empty
     * sequences!).
     */
    public byte getFirst() {
        return bytes[0];
    }

    /**
     * Returns the last byte of this ByteSequence. It doesn't check for
     * emptiness, so be careful not to call this on an empty sequence.
     *
     * @return The last byte of this ByteSequence (don't call this on empty
     * sequences!).
     */
    public byte getLast() {
        return bytes[length - 1];
    }

    @Override
    public int hashCode() {
        long hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (hash * hashFactor + Byte.toUnsignedInt(bytes[i])) % hashTableSize;
        }
        return (int) hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ByteSequence other = (ByteSequence) obj;
        if (this.length != other.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (this.bytes[i] != other.bytes[i]) {
                return false;
            }
        }
        return true;
    }

    private void expand() {
        byte[] newBytes = new byte[bytes.length + SIZE_INCREMENT];
        Utils.arrayCopy(bytes, 0, newBytes, 0, bytes.length);
        bytes = newBytes;
    }
}
