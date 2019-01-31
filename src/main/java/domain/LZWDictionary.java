package domain;

public class LZWDictionary implements Dictionary {

    private final LZWDictEntry[] hashTable;
    private static final int DEFAULT_HASH_TABLE_SIZE = LZW.DEFAULT_HASH_TABLE_SIZE;

    private final int hashFactor;
    private static final int DEFAULT_HASH_FACTOR = LZW.HASH_FACTOR;

    private int cachedHashCode;

    public LZWDictionary() {
        this(DEFAULT_HASH_TABLE_SIZE, DEFAULT_HASH_FACTOR);
    }

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
        // don't calculate hashCode, since an element was just searched for with get
        // we know that element (whose hashcode is cached in cachedHashCode)
        // is the one added here
        LZWDictEntry currentEntry = hashTable[cachedHashCode];
        if (currentEntry == null) {
            hashTable[cachedHashCode] = newEntry;
        } else {
            LZWDictEntry previousEntry = currentEntry;
            // omit check for equality, since we know that the only case an entry is put into the dict
            // is when it wasn't found in it already (so always return null)
            while ((currentEntry = currentEntry.getNext()) != null) {
                previousEntry = currentEntry;
            }
            previousEntry.setNext(newEntry);
        }
        resetCachedHashCode();
        return null;
    }

    private void resetCachedHashCode() {
        cachedHashCode = -1;
    }
}
