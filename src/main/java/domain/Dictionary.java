package domain;

/**
 * Represents dictionaries having ByteSequence as key and Integer as value.
 */
public interface Dictionary {

    /**
     * Removes all entries from this dictionary.
     */
    public void clear();

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * dictionary contains no entry for the key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value to which the specified key is mapped, or null if this
     * dictionary contains no entry for the key.
     */
    public Integer get(ByteSequence key);

    /**
     * Associates the specified value with the specified key in this dictionary.
     * If the dictionary previously contained an entry for the key, the old
     * value is replaced by the specified value.
     *
     * @param key Key with which the specified value is to be associated.
     * @param value Value to be associated with the specified key.
     * @return The previous value associated with key, or null if there was no
     * entry for key.
     */
    public Integer put(ByteSequence key, Integer value);
}
