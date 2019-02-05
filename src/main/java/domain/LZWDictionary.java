package domain;

/**
 * A {@link Dictionary} implementation which uses internally a hash table with
 * separate chaining with linked lists. This Dictionary can be used only in the
 * context of the {@link LZW} algorithm, because it works only if
 * {@link #get(ByteSequence) get} and {@link #put(ByteSequence, Integer) put}
 * are called in such an order that could occur in LZW. If
 * {@link #get(ByteSequence) get} founds an entry for the given key, the hash
 * code for the key is cached; we know that what will immediately follow is
 * another call to get, with a key that will be equal to the key of the previous
 * call plus a symbol (a byte, since keys are of type {@link ByteSequence}). So,
 * since the hash function for ByteSequence uses a rolling hash, to get the hash
 * code of the new key we can simply use the cached hash of the previous key
 * together with the new symbol (which is obviously faster than calculating the
 * hash code from scratch using {@link ByteSequence#hashCode()}). When a key has
 * no corresponding entry in the dictionary (and so
 * {@link #get(ByteSequence) get} returns null), we know that what will follow
 * is a call to {@link #put(ByteSequence, Integer) put} with that same key; we
 * can thus use once again the cached hash, which will then be reset, so that
 * {@link ByteSequence#hashCode()} will be used to calculate the hash code of
 * the key in the next call to {@link #get(ByteSequence) get} (or
 * {@link #put(ByteSequence, Integer) put}, during dictionary initialization).
 * The cached hash code is reset because in the next call to
 * {@link #get(ByteSequence) get} the key will be a new symbol, with possibly no
 * relation to the previous call's key. Because of all this hash code cashing,
 * an LZWDictionary instance will work only with ByteSequence keys that have the
 * same hash table size and hash factor. This is not checked, therefore it's the
 * responsibility of the user of these classes to make sure they have equal
 * values for those two parameters. For more information see
 * https://en.wikipedia.org/wiki/Hash_table and
 * https://en.wikipedia.org/wiki/Rolling_hash#Polynomial_rolling_hash .
 */
public class LZWDictionary implements Dictionary {

    private final LZWDictEntry[] hashTable;
    private static final int DEFAULT_HASH_TABLE_SIZE = LZW.DEFAULT_HASH_TABLE_SIZE;

    private final int hashFactor;
    private static final int DEFAULT_HASH_FACTOR = LZW.HASH_FACTOR;

    private int cachedHashCode;

    /**
     * Returns an instance of LZWDictionary with default values for hash table
     * size and hash factor.
     */
    public LZWDictionary() {
        this(DEFAULT_HASH_TABLE_SIZE, DEFAULT_HASH_FACTOR);
    }

    /**
     * Returns an instance of LZWDictionary with the given hash table size and
     * hash factor. Each {@link LZWDictEntry entry} put in this dictionary
     * should have as key a {@link ByteSequence} with these same values for hash
     * table size and hash factor.
     *
     * @param hashTableSize Size of the hash table that will be used internally;
     * it is never resized.
     * @param hashFactor Hash factor that will be used to calculate the hash
     * code of a key using a previously cached hash code (for more info see the
     * description of this class and of {@link ByteSequence}).
     */
    public LZWDictionary(int hashTableSize, int hashFactor) {
        hashTable = new LZWDictEntry[hashTableSize];
        this.hashFactor = hashFactor;
        resetCachedHashCode();
    }

    @Override
    public void clear() {
        for (int i = 0; i < hashTable.length; i++) {
            hashTable[i] = null;
        }
        resetCachedHashCode();
    }

    @Override
    public Integer get(ByteSequence key) {

        if (cachedHashCode < 0) {
            cachedHashCode = key.hashCode();
        } else {
            cachedHashCode = (int) (((long) cachedHashCode * hashFactor
                    + Byte.toUnsignedInt(key.getLast())) % hashTable.length);
        }

        LZWDictEntry entry = hashTable[cachedHashCode];

        while (entry != null) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
            entry = entry.getNext();
        }

        return null;
    }

    @Override
    public Integer put(ByteSequence key, Integer value) {

        if (cachedHashCode < 0) {
            cachedHashCode = key.hashCode();
        }

        LZWDictEntry newEntry = new LZWDictEntry(key, value);
        LZWDictEntry currentEntry = hashTable[cachedHashCode];

        if (currentEntry == null) {
            hashTable[cachedHashCode] = newEntry;
        } else {
            LZWDictEntry previousEntry = currentEntry;
            // no need to check for equality, since we know that an entry is put
            // into the dictionary only when it wasn't found in it already
            while ((currentEntry = currentEntry.getNext()) != null) {
                previousEntry = currentEntry;
            }
            previousEntry.setNext(newEntry);
        }

        resetCachedHashCode();

        return null;
    }

    /**
     * Resets the cached hash code by setting it to a negative value (you can
     * see from {@link ByteSequence#hashCode()} that a real hash code will
     * always be non-negative).
     */
    private void resetCachedHashCode() {
        cachedHashCode = -1;
    }
}
