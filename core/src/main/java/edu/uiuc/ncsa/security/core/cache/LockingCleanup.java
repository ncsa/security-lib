package edu.uiuc.ncsa.security.core.cache;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

/**
 * Extension of cleanup that adds a lock to the store. If this lock is there, then no
 * GC will happen.  Note that since stores are maps, there is no reason to set the
 * map, just the store.
 * <p>Created by Jeff Gaynor<br>
 * on 4/11/22 at  1:18 PM
 */
/* *****
    Not used in this project, used extensively in OA4MP.
   ***** */
public class LockingCleanup<K, V> extends Cleanup<K, V> {
    public LockingCleanup(MyLoggingFacade logger, String name) {
        super(logger, name);
    }

}
