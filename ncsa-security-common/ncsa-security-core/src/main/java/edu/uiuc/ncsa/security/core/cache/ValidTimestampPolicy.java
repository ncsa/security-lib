package edu.uiuc.ncsa.security.core.cache;


import edu.uiuc.ncsa.security.core.exceptions.InvalidTimestampException;
import edu.uiuc.ncsa.security.core.util.DateUtils;

/**
 * The given key is overloaded to have the timestamp information. This just looks at that and
 * parses it to make the decision.
 * <p>Created by Jeff Gaynor<br>
 * on 7/12/11 at  12:26 PM
 */
public class ValidTimestampPolicy implements RetentionPolicy {
    protected long maxTimeout = -1L;

    public ValidTimestampPolicy(long maxTimeout) {
        this.maxTimeout = maxTimeout;
    }

    public ValidTimestampPolicy() {
    }

    public boolean applies() {
        return true;
    }


    public boolean retain(java.lang.Object key, java.lang.Object value) {
        try {
            // if there is no max timeout set, then use whatever the default is.
            if (maxTimeout <= 0) {
                DateUtils.checkTimestamp(key.toString());
            } else {
                DateUtils.checkTimestamp(key.toString(), maxTimeout);
            }
            return true;
        } catch (InvalidTimestampException its) {
            return false;
        }
    }

    /**
     * This returns null, since the retention decision requires only the key of the entry,
     * not the entire store (cf. MaxSizePolicy which does have to think about the store size.)
     * No reason to have setters or getters.
     *
     * @return
     */
    public Cache getMap() {
        return null;
    }
}
