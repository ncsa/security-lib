package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.*;
import edu.uiuc.ncsa.security.core.exceptions.UnregisteredObjectException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * An in-memory store. This is useful in several different ways and is in effect
 * just a map that implements {@link edu.uiuc.ncsa.security.core.Store}.
 * To use this practically, you must implement the following method:
 * <ul>
 * <li>create - returns the correct actual object.</li>
 * </ul>
 * And have the key and value types specified to concrete classes.
 * <p>Created by Jeff Gaynor<br>
 * on 11/3/11 at  1:14 PM
 */
public class MemoryStore<V extends Identifiable> extends HashMap<Identifier, V> implements Store<V> {
    public MemoryStore(IdentifiableProvider<V> identifiableProvider) {
        super();
        this.identifiableProvider = identifiableProvider;
        this.initializable = new MSInitializer(this);
    }

    Initializable initializable;

    public Initializable getInitializer() {
        return initializable;
    }

    protected IdentifiableProvider<V> identifiableProvider;

    @Override
    public V create() {
        return identifiableProvider.get();
    }

    public static class MSInitializer implements Initializable {
        MemoryStore memoryStore;

        public MSInitializer(MemoryStore memoryStore) {
            this.memoryStore = memoryStore;
        }

        @Override
        public boolean createNew() {
            return destroy();
        }

        @Override
        public boolean destroy() {
            memoryStore.clear();
            return true;
        }

        @Override
        public boolean init() {
            return destroy();
        }

        @Override
        public boolean isCreated() {
            return true;
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public boolean isDestroyed() {
            return false;
        }
    }

    @Override
    public void update(V value) {
        // contract is that this blows up if the user tries to do an update and the item has not been registered
        if (!containsKey(value.getIdentifier())) {
            throw new UnregisteredObjectException("Error: the given item has not been added to the store yet:" + value);
        }
        realSave(value);
    }

    protected void realSave(V value) {
        if(value.getIdentifier() == null){
            throw new UnsupportedOperationException("Error: null identifiers are not allowed");
        }
        put(value.getIdentifier(), value);
    }

    @Override
    public void register(V value) {
        realSave(value);
    }

    @Override
    public void save(V value) {
        realSave(value);
    }

    @Override
    public List<V> getAll() {
        LinkedList<V> allEntries = new LinkedList<>();
        for(Identifier d: keySet()){
            allEntries.add(get(d));
        }
        return allEntries;
    }
}
