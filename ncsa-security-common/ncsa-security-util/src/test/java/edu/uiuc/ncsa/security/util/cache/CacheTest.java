package edu.uiuc.ncsa.security.util.cache;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.cache.*;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.TestBase;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Nov 12, 2010 at  10:20:05 AM
 */
public class CacheTest extends TestBase {

    // just to have something cacheable, for cache testing...
    class StupidObject extends IdentifiableImpl implements Cacheable {
        StupidObject(String identifier) {
            super(BasicIdentifier.newID(identifier));
        }
    }


    @Test
    public void testBasic() throws Exception {
        int count = 15;
        int maxCacheSize = count - 5;
        Cache cache = new Cache();
        MaxCacheSizePolicy maxCacheSizePolicy = new MaxCacheSizePolicy(cache, maxCacheSize);
        for (int i = 0; i < count; i++) {
            cache.add(new StupidObject(TestBase.getRandomString()));
        }
        // check all the values are stored in the right order by time
        CachedObject lastCO = null;
        for (CachedObject co : cache.getSortedList()) {
            if (lastCO != null) {
                assert lastCO.getTime() <= co.getTime();
            }
            lastCO = co;
        }

        lastCO = null;
        // Check they are stored in the tree map sorted by key:
        for (CachedObject co : cache.values()) {
            if (lastCO != null) {
                assert lastCO.getKey().compareTo(co.getKey()) < 0;
            }
            lastCO = co;

        }
        Cleanup<Identifier, CachedObject> cacheAger = new Cleanup<Identifier, CachedObject>(new MyLoggingFacade("test cleanup", true));
        cacheAger.setMap(cache);
        cacheAger.addRetentionPolicy(maxCacheSizePolicy);
        cacheAger.age();
        assert maxCacheSize == cache.size();

    }
}
