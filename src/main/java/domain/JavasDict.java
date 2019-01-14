package domain;

import java.util.HashMap;
import java.util.Map;

public class JavasDict<K, V> implements Dictionary<K, V> {

    private final Map<K, V> map;

    public JavasDict() {
        map = new HashMap<>();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }
}
