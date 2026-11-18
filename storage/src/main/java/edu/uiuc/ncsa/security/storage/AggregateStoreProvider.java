package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Store;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  10:21 AM
 */
public class AggregateStoreProvider implements Provider<AggregateStore> {
    List<Store> writeStores = new ArrayList<Store>();
    List<Store> readStores = new ArrayList<Store>();

    public List<Store> getWriteStores() {
        return writeStores;
    }

    public void setWriteStores(List<Store> writeStores) {
        this.writeStores = writeStores;
    }

    public List<Store> getReadStores() {
        return readStores;
    }

    public void setReadStores(List<Store> readStores) {
        this.readStores = readStores;
    }



    AggregateStore store = null; // so provider gives back same store

    @Override
    public AggregateStore get() {
        if (store == null) {
            store = new AggregateStore(writeStores, readStores);
        }
        return store;
    }
}
