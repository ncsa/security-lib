package edu.uiuc.ncsa.security.core.cache;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;

import java.util.*;

/**
 * This cache stores {@link CachedObject}s by a given key.
 * The cached objects are always sorted by added date to the cache, which facilitates and
 * optimizes aging retention policies. Each object to be cached should implement the interface
 * {@link Cacheable} which has a single method that gives back th associated key. This is used to
 * retrieve objects from the cache (rather than cachedObjects which contain state about the
 * entry and when it was made).
 * <p>Note that this is not tasked with saving cached objects or any other persistence activities. It is up
 * to the application to set an manage its own policies.
 * <p/>
 * <h3>Use</h3>
 * This contains {@link CachedObject}s but this does not mean you actually should ever make them.
 * Since {@link Cacheable} objects are {@link edu.uiuc.ncsa.security.core.Identifiable},
 * this means that simple invoking {@link #add(Cacheable)} will create a cached object internally.
 * Invoking {@link #get} will return a cached object whose value (via {@link edu.uiuc.ncsa.security.core.cache.CachedObject#getValue()}
 * is the original cacheable object. Generally in OA4MP all useful objects (transactions, clients and pretty
 * much anything with an identifier associated with it) are cacheable. If you need to cache something, use this,
 * set your policies for aging and periodically call {@link Cleanup}.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 11, 2010 at  1:02:04 PM
 */
public class Cache implements Map<Identifier, CachedObject> {

    /**
     * Convenience method to add an object with its key to the cache. This
     * creates the CachedObject wrapper to manage its state. If the object already
     * exists in the cache, it updates its value.
     *
     * @param cacheable
     * @return
     */
    public CachedObject add(Cacheable cacheable) {
        Identifier key = cacheable.getIdentifier();
        CachedObject co = get(key);
        if (co == null) {
            co = new CachedObject();
            co.setKey(key.toString());
            put(key, co);
        }
        co.setValue(cacheable);
        return co;
    }


    /**
     * Sorted list of cached items, ordered by timestamp.
     *
     * @return
     */
    public PriorityQueue<CachedObject> getSortedList() {
        if (sortedList == null) {
            sortedList = new PriorityQueue<CachedObject>();
        }
        return sortedList;
    }

    PriorityQueue<CachedObject> sortedList;

    public TreeMap<Identifier, CachedObject> getTheRealCache() {
        if (theRealCache == null) {
            theRealCache = new TreeMap<Identifier, CachedObject>();
        }
        return theRealCache;
    }


    TreeMap<Identifier, CachedObject> theRealCache;

    public void clear() {
        getSortedList().clear();
        getTheRealCache().clear();
    }

    public int size() {
        return getTheRealCache().size();
    }

    public boolean isEmpty() {
        return getTheRealCache().isEmpty();
    }


    public boolean containsKey(Object key) {
        return getTheRealCache().containsKey(key);
    }

    /**
     * Checks if the cacheable object is in this cache.
     *
     * @param cacheable
     * @return
     */
    public boolean containsObject(Cacheable cacheable) {
        return getTheRealCache().containsKey(cacheable.getIdentifier());
    }

    /**
     * Get the objects that have been cached.
     *
     * @return
     */
    public Collection<? extends Cacheable> objectValues() {
        Collection<Cacheable> objects = new ArrayList<Cacheable>();
        for (CachedObject co : values()) {
            objects.add((Cacheable) co.getValue());
        }
        return objects;
    }

    // Note that this returns the CachedObject not the object. To check for whether an object has
    // been cached, use

    public boolean containsValue(Object value) {
        return getTheRealCache().containsValue(value);
    }

    public CachedObject get(Object key) {
        return getTheRealCache().get(key);
    }

    public void put(CachedObject co) {
        put(new BasicIdentifier(co.getKey()), co);
    }

    /**
     * Using this rather than {@link #add} will let you cache anything you please, even objects
     * (like connections to servers) that are not inherently {@link edu.uiuc.ncsa.security.core.Identifiable}.
     * @param key
     * @param value
     * @return
     */
    public CachedObject put(Identifier key, CachedObject value) {
        // issue is that the sorted list is a list -- adding the same cached value
        // repeatedly will result in duplicates. The real cache vets these by key.
        // It is entirely possible that a user will add a new cached object that will replace
        // a currently cached object, effectively changing how the retention policy will work with
        // it.

        CachedObject oldCO = getTheRealCache().get(key);
        if (oldCO == null) {
            // it's new
            getTheRealCache().put(key, value);
            getSortedList().add(value);

        } else {
            oldCO.setTimestamp(value.getTimestamp());
        }
        return oldCO;
    }

    public CachedObject remove(Object key) {
        CachedObject co = getTheRealCache().get(key);
        if (co == null) return null; // don't have one
        // have to find the cached
        getSortedList().remove(co);
        return getTheRealCache().remove(key);
    }

    public void putAll(Map<? extends Identifier, ? extends CachedObject> m) {
        for (Entry e : m.entrySet()) {
            getSortedList().add((CachedObject) e.getValue());
        }
        getTheRealCache().putAll(m);
    }

    public Set<Identifier> keySet() {
        return getTheRealCache().keySet();
    }

    public Collection<CachedObject> values() {
        return getTheRealCache().values();
    }

    public Set<Entry<Identifier, CachedObject>> entrySet() {
        return getTheRealCache().entrySet();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + size() + " objects]";
    }
}
