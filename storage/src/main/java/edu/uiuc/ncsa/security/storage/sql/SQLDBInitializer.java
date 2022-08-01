package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.storage.StoreInitializer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/23/12 at  11:12 AM
 */
public class SQLDBInitializer extends StoreInitializer {
    @Override
    public boolean createNew() {
        return false;
    }

    @Override
    public boolean destroy() {
        return false;
    }

    @Override
    public boolean init() {
        return false;
    }
}
