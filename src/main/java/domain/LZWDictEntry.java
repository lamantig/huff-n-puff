package domain;

/**
 * Represents an {@link LZWDictionary} entry. Since LZWDictionary is implemented
 * as a hash table with separate chaining with linked lists, LZWDictEntry has a
 * pointer to the next entry in the bucket.
 */
public class LZWDictEntry {

    private final ByteSequence key;
    private final Integer value;
    /**
     * Pointer to the next entry in the bucket.
     */
    private LZWDictEntry next;

    /**
     * Returns an instance of LZWDictEntry with the given key and value.
     *
     * @param key Entry's key.
     * @param value Entry's value.
     */
    public LZWDictEntry(ByteSequence key, Integer value) {
        this.key = key;
        this.value = value;
        next = null;
    }

    public void setNext(LZWDictEntry next) {
        this.next = next;
    }

    public ByteSequence getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    public LZWDictEntry getNext() {
        return next;
    }
}
