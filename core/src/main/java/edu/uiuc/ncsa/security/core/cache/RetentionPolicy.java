package edu.uiuc.ncsa.security.core.cache;

import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Nov 11, 2010 at  1:03:54 PM
 */
public interface RetentionPolicy<K, V> {
    public boolean retain(K key, V value);

    /**
     * The cache to which this policy is applied.
     *
     * @return
     */
    public Map<K, V> getMap();

    /**
     * If this applies to the current cache. While this is true, the {@link #retain(Object, Object)}
     * method will be applied to each element of the cache. When false, this policy will be skipped.
     * <br>E.g. for a cache that limits the number of cached items, there is no reason to check every
     * item in the cache directly if the cache size is below a certain threshold.
     *
     * @return
     */
    public boolean applies();
}
