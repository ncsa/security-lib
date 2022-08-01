package edu.uiuc.ncsa.security.core.cache;



import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Facade for a map that is backed by a cache. Since all stores are logically
 * maps as well, this can be used to cache any store.
 * <br><b>Note:<b> This persists immediately on put, so there are lazy reads but not writes.
 * Gets to this are checked against the cache. If found, then that is returned, if not,
 * the store is checked. Invoking put will either add a new item to the store or over-write an existing one
 * with the same identifier. There is consequently a save operation implied with it.
 * <h2>Usage</h2>
 * Extend this to the correct parameters. Implement any interfaces you need for storage
 * then set the backing store to be your real store and use this class everyplace you would
 * normally have your store.<br><br>
 * This will remove from the store immediately as well.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 12, 2010 at  11:31:47 AM
 */
public abstract class CachedMapFacade<V extends Cacheable> implements Store<V> {

    public CachedMapFacade(Store<V> theStore) {
        this.theStore = theStore;
    }

    public CachedMapFacade() {
        super();
    }

    public Cache getCache() {
        if (cache == null) {
            cache = new Cache();
        }
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    Cache cache;

    public Store<V> getTheStore() {
        return theStore;
    }

    public void setTheStore(Store<V> theStore) {
        this.theStore = theStore;
    }

    Store<V> theStore;

    public boolean hasStore() {
        return theStore != null;
    }

    public void clear() {
        getCache().clear();
        if (hasStore()) {
            getTheStore().clear();
        }
    }


    public boolean isEmpty() {
        if (hasStore()) {
            return getTheStore().size() == 0;
        }
        return getCache().size() == 0;
    }

    public boolean containsKey(Object key) {
        if (getCache().containsKey(key)) return true;
        if (hasStore()) {
            return getTheStore().containsKey(key);
        }
        return false;
    }

    public boolean containsValue(Object value) {
        if (getCache().containsObject((Cacheable) value)) return true;
        if (hasStore()) {
            return getTheStore().containsValue(value);
        }
        return false;
    }


    public V get(Object key) {
        // key is a string or URI for most cases
        if (getCache().containsKey(key)) {
            return (V) getCache().get(key).getValue();
        }
        V it = null;
        if (hasStore()) {
            it = (V) getTheStore().get(key);
            if (it != null) {
                // only add it if it exists.
                getCache().add(it);
            }
        }
        return it;
    }

    public V put(Identifier key, V cacheable) {
      //  String keyString = cacheable.getIdentifierString();
        V it = null;
        if (getCache().containsKey(key)) {
            it = (V) getCache().get(key).getValue();
        }
        if (it == null && hasStore()) {
            it = (V) getTheStore().get(key);
        }
        getCache().add(cacheable);
        return it;
    }

    public V remove(Object key) {
        V it = null;
        CachedObject co = getCache().remove(key);
        if (co != null) {
            it = (V) co.getValue();
        }
        if (it == null && hasStore()) {
            it = (V) getTheStore().get(key);
        }
        if (hasStore()) {
            getTheStore().remove(key);
        }
        return it;
    }

    public void putAll(Map<? extends Identifier, ? extends V> m) {
        for (Map.Entry e : m.entrySet()) {
            getCache().add((Cacheable) e.getValue());
        }
        if (hasStore()) {
            getTheStore().putAll(m);
        }
    }


    public Set<Map.Entry<Identifier, V>> entrySet() {
        if (hasStore()) {
            return getTheStore().entrySet();
        }
        Set<Map.Entry<Identifier, V>> entries = new HashSet<Map.Entry<Identifier, V>>();
        for (Identifier key : keySet()) {
            entries.add(new SimpleEntryImpl<Identifier, V>(key, get(key)));
        }
        return entries;
    }

    public int size() {
        return keySet().size();
    }

    public Set<Identifier> keySet() {
        if (hasStore()) {
            return getTheStore().keySet();
        }
        return  getCache().keySet();
    }

    public Collection<V> values() {
        if (hasStore()) {
            return getTheStore().values();
        }
        return (Collection<V>) getCache().objectValues();
    }
}
