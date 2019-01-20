package domain;

public class LZWDictEntry {

    private final ByteSequence key;
    private final Integer value;
    private LZWDictEntry next;

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
