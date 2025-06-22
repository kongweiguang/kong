package io.github.kongweiguang.core.lang;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 不可变二元组对象
 *
 * @param <K> k
 * @param <V> v
 * @author kongweiguang
 */
public class Pair<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final K k;
    private final V v;

    public Pair(K key, V val) {
        this.k = key;
        this.v = val;
    }

    public static <L, R> Pair<L, R> of(L key, R val) {
        return new Pair<>(key, val);
    }

    public K k() {
        return k;
    }

    public V v() {
        return v;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Pair.class.getSimpleName() + "[", "]")
                .add("key=" + k)
                .add("val=" + v)
                .toString();
    }
}
