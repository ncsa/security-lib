package edu.uiuc.ncsa.security.core.cache;

import java.util.Date;

/**
 * Class the contains a cacheable object. These are what is stored in the cache and are comparable
 * by their timestamps.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 11, 2010 at  1:02:39 PM
 */
public class CachedObject implements Comparable {
    public CachedObject() {
    }

    public CachedObject(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    Object value;

    public void setKey(String key) {
        this.key = key;
    }

    String key;

    public String getKey() {
        return key;

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public long getTime() {
        return getTimestamp().getTime();
    }

    Date timestamp = new Date();

    public int compareTo(Object o) {
        if (!(o instanceof CachedObject)) {
            throw new ClassCastException("The given object is of type " + o.getClass().getName() + " and cannot be compared to a CachedObject");
        }
        CachedObject co = (CachedObject) o;
        return Long.signum(getTime() - co.getTime());
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CachedObject)) return false;
        return getKey().equals(((CachedObject) obj).getKey());
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
