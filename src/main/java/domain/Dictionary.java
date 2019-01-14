package domain;

public interface Dictionary<K, V> {
    public void clear();
    public V get(K key);
    public V put(K key, V value);
}
