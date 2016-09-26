package edu.uiuc.ncsa.security.delegation.server.storage;

import edu.uiuc.ncsa.security.storage.AggregateStore;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  11:18 AM
 */
public class AggregateClientStore<V extends ClientStore> extends AggregateStore<V> implements ClientStore {
    public AggregateClientStore(V... stores) {
        super(stores);
    }
}
