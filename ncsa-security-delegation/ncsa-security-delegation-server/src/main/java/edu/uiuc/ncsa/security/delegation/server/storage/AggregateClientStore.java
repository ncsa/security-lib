package edu.uiuc.ncsa.security.delegation.server.storage;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.storage.impl.BaseClientConverter;
import edu.uiuc.ncsa.security.storage.AggregateStore;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  11:18 AM
 */
public class AggregateClientStore<V extends ClientStore> extends AggregateStore<V> implements ClientStore {
    public AggregateClientStore(V... stores) {
        super(stores);
    }

    @Override
    public BaseClientConverter getConverter() {
        return null;
    }

    @Override
    public IdentifiableProvider getACProvider() {
        return null;
    }
}
