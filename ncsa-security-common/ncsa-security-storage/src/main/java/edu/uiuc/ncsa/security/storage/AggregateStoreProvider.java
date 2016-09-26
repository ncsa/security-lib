package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Store;

import javax.inject.Provider;
import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  10:21 AM
 */
public class AggregateStoreProvider implements Provider<AggregateStore> {
    ArrayList<Provider<? extends Store>> providers = new ArrayList<Provider<? extends Store>>();

    public void addProvider(Provider<? extends Store> x) {
        providers.add(x);
    }

    AggregateStore<Store> store;

    @Override
    public AggregateStore get() {
        Store[] stores = new Store[providers.size()];
        int i = 0;
        for (Provider p : providers) {
            stores[i++] = (Store) p.get();
        }
        if (store == null) {
            store = new AggregateStore(stores);
        }
        return store;
    }
}
