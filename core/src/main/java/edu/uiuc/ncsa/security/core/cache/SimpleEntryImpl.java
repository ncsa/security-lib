package edu.uiuc.ncsa.security.core.cache;

import java.util.Map;

/**
 * Really basic entry implementation of {@link Map.Entry}.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 26, 2010 at  3:02:20 PM
 */
public class SimpleEntryImpl<K, V> implements Map.Entry<K, V> {
    public SimpleEntryImpl(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K key;
    V value;

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V value) {
        return this.value = value;
    }
}
