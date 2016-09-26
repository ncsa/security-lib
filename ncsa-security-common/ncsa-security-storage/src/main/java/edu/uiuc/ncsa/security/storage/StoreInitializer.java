package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Initializable;

/**
 * Initializes a store. note that stores have different create and destroy semantics which
 * are all logically indep. of each other. File stores, e.g., are created as needed, but
 * database stores correspond to sets of tables.
 * <p>Created by Jeff Gaynor<br>
 * on 4/23/12 at  10:23 AM
 */
public abstract class StoreInitializer implements Initializable {
    protected boolean destroyed = false;
    protected boolean created = false;
    protected boolean initialized = false;



    @Override
    public boolean isCreated() {
        return created;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
