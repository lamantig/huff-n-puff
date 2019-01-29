package domain;

public class ByteSequence {

    private static final int SIZE_INCREMENT = 7;

    private byte[] bytes;
    private int length;

    public ByteSequence() {
        bytes = new byte[SIZE_INCREMENT];
        length = 0;
    }

    public ByteSequence(byte[] bytes) {
        this.bytes = bytes;
        this.length = bytes.length;
    }

    public void append(byte b) {
        if (length == bytes.length) {
            expand();
        }
        bytes[length++] = b;
    }

    public ByteSequence makeClone() {
        byte[] newBytes = new byte[length];
        Utils.arrayCopy(bytes, 0, newBytes, 0, length);
        return new ByteSequence(newBytes);
    }

    // returns the number of copied elements
    public int copyTo(byte[] a, int fromIndex) {
        Utils.arrayCopy(bytes, 0, a, fromIndex, length);
        return length;
    }

    public void reset() {
        length = 0;
    }

    // careful, no checking for emptyness
    public byte getFirst() {
        return bytes[0];
    }

    // careful, no checking for emptyness
    public byte getLast() {
        return bytes[length - 1];
    }

    @Override
    public int hashCode() {
        long hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (hash * LZW.HASH_FACTOR + Byte.toUnsignedInt(bytes[i])) % LZW.HASH_TABLE_SIZE;
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
