package edu.uiuc.ncsa.security.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** A hashmap with two way lookup. This maintains a second hashmap that has the
 * keys reversed. Access these elements with {@link #getByValue(Object)}.
 * There are some caveats. For one thing, this assumes that there
 * is a one to one correspondence between keys and values so
 * this specifically dis-allows null objects as values. Multiple values will get over-written.
 * with "predictable but unwanted results."
 * <p>Created by Jeff Gaynor<br>
 * on 3/1/12 at  2:06 PM
 */
public class DoubleHashMap<K,V> implements Map<K,V> {
    HashMap<K,V> kToV = new HashMap<K, V>();
    HashMap<V,K> vToK = new HashMap<V, K>();

    @Override
    public void clear() {
        kToV = new HashMap<K, V>();
        vToK = new HashMap<V, K>();
    }

    @Override
    public int size() {
        return kToV.size();
    }

    @Override
    public boolean isEmpty() {
        return kToV.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return kToV.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return kToV.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return kToV.get(key);
    }

    @Override
    public V put(K key, V value) {
        if(value == null){
            throw new IllegalArgumentException("Error: null values are not allowed. Key = " + key);
        }
        vToK.put(value, key);
        return kToV.put(key, value);
    }

    @Override
    public V remove(Object key) {
        if(kToV.containsKey(key)){
            vToK.remove(kToV.get(key));
        }
        return kToV.remove(key);
    }

    /**
     * Slow since this loops. Best to over-ride if you performance is an issue.
     * @param m
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
          for(K k : m.keySet()){
              put(k, m.get(k));
          }
    }

    @Override
    public Set<K> keySet() {
        return kToV.keySet();
    }

    @Override
    public Collection<V> values() {
        return kToV.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return kToV.entrySet();
    }

    public K getByValue(V value){
        return vToK.get(value);
    }
}
