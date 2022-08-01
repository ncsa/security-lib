package edu.uiuc.ncsa.security.core.cache;

import java.util.Map;

/**
 * The retention policy for
 * <p>Created by Jeff Gaynor<br>
 * on Nov 12, 2010 at  9:51:36 AM
 */
public class MaxCacheSizePolicy<K, V> implements RetentionPolicy {
    public MaxCacheSizePolicy(Map<K, V> cache, int maxCacheSize) {
        setMap(cache);
        setMaximumSize(maxCacheSize);
    }

    public Map<K, V> getMap() {
        return map;
    }

    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    Map<K, V> map;

    public int getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    int maximumSize;

    public boolean retain(Object key, Object value) {
        return sizeOk();
    }

    public boolean applies() {
        // applies as long as the size of the cache is NOT ok!
        return !sizeOk();
    }

    /**
     * Returns true if the size of the cache is ok.
     *
     * @return
     */
    protected boolean sizeOk() {
        return (getMap().size() <= getMaximumSize());
    }
}
