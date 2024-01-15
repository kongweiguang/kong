package io.github.kongweiguang.http.server.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多个值的map
 *
 * @author kongweiguang
 */
public class MultiValueMap<K, V> {

    private final Map<K, List<V>> map = new HashMap<>();

    public Map<K, List<V>> map() {
        return map;
    }

    public MultiValueMap<K, V> put(K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        return this;
    }

    public List<V> get(K key) {
        return map.getOrDefault(key, Collections.emptyList());
    }

    public List<V> removeKey(K key) {
        return map.remove(key);
    }

    public boolean removeValue(K key, V value) {
        final List<V> list = map.computeIfAbsent(key, k -> new ArrayList<>());
        return list.remove(value);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
