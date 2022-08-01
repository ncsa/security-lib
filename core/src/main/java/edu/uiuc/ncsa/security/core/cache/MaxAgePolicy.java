package edu.uiuc.ncsa.security.core.cache;

import edu.uiuc.ncsa.security.core.Identifier;

/**
 * A retention policy that removes objects that have been in the cache for too long.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 12, 2010 at  10:21:04 AM
 */
public class MaxAgePolicy implements RetentionPolicy<Identifier, CachedObject> {
    public MaxAgePolicy(Cache cache, long maximumAge) {
        setMap(cache);
        setMaximumAge(maximumAge);
    }

    public long getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(long maximumAge) {
        this.maximumAge = maximumAge;
    }

    long maximumAge = 10000L;

    public void setMap(Cache map) {
        this.map = map;
    }

    Cache map;

    public Cache getMap() {
        return map;
    }

    public boolean retain(Identifier key, CachedObject cachedObject) {
        return timeOk(cachedObject.getTime());
    }

    boolean timeOk(long currentTime) {
        return (System.currentTimeMillis() - currentTime) <= getMaximumAge();
    }

    public boolean applies() {
        if (getMap().isEmpty()) return false;
        CachedObject first = getMap().getSortedList().peek();
        return !timeOk(first.getTime());
    }
}
