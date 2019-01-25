package domain;

public interface Dictionary {
    public void clear();
    public Integer get(ByteSequence key);
    public Integer put(ByteSequence key, Integer value);
}
