package edu.uiuc.ncsa.security.core.util;

/**
 * Element in a {@link Pool}. This wraps the connection and adds a timestamp so
 * elements can be monitored for staleness.
 * <p>Created by Jeff Gaynor<br>
 * on 4/21/21 at  10:31 AM
 */
public class PoolEntryWrapper<T> {
    public T connection;
    public long lastUsed;

    /**
     * Has this connection outlived its designated lifetime?
     * @param interval
     * @return
     */
    public boolean isExpired(long interval){
        return lastUsed + interval < System.currentTimeMillis();
    }
}
