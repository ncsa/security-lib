package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.exceptions.UnregisteredObjectException;

import java.util.*;

/**
 * A store of stores. This implements the store interface and passes through the
 * calls to each underlying store. This allows an application to treat a collection
 * of stores as a one big logical store. Note that this works locally
 * at each stage, so, e.g. if an {@link #update(edu.uiuc.ncsa.security.core.Identifiable)}
 * is issued, the first store found containing the information is what is updated.
 * Not perfect, but will probably keep information more or less localized...
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  9:16 AM
 */
public class AggregateStore<V extends Store> implements Store {
    public AggregateStore(V... stores) {
        for (V s : stores) {
            this.stores.add(s);
        }
    }

    protected List<V> stores = new ArrayList<V>();

    /**
     * Gives access to the list of stores. Mostly for testing purposes.
     *
     * @return
     */
    public List<V> stores() {
        return stores;
    }

    /**
     * Caveat! This does not check if the store has already been added! This is because store comparison
     * is probably too expensive and in some cases almost impossible to do. It is up to the application not
     * to add multiple copies of the same store.
     *
     * @param store
     */
    public void addStore(V store) {
        stores.add(store);
    }

    @Override
    public int size(boolean includeVersions) {
        int x = 0;
        for (Store s : stores) {
            x = x + s.size(includeVersions);
        }
        return x;
    }

    protected void checkValid() {
        if (0 == stores.size()) {
            throw new GeneralException("Error: Aggregate store is empty. There must be at least one store in the aggregate.");
        }
    }

    public V defaultStore() {
        checkValid();
        return stores.get(0);
    }

    @Override
    public Identifiable create() {
        return defaultStore().create();
    }

    @Override
    public void update(Identifiable value) {
        checkValid();
        for (Store s : stores) {
            if (s.containsKey(value.getIdentifier())) {
                s.update(value);
                return;
            }
        }
        throw new UnregisteredObjectException("Error: cannot update non-existent object. You must register it first");
    }

    @Override
    public void register(Identifiable value) {
        for (Store s : stores) {
            if (s.containsKey(value.getIdentifier())) return;
        }
        defaultStore().register(value);
    }

    @Override
    public void save(Identifiable value) {
        for (Store s : stores) {
            // try to get it to the right store.
            try {
                if (s.containsKey(value.getIdentifier())) {
                    s.save(value);
                    return;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        defaultStore().save(value);
    }

    /**
     * This is expensive to compute, so use sparingly.
     *
     * @return
     */
    @Override
    public int size() {
        int x = 0;
        for (Store s : stores) {
            x = x + s.size();
        }
        return x;
    }


    @Override
    public boolean isEmpty() {
        return size() != 0;
    }

    @Override
    public boolean containsKey(Object key) {
        for (Store s : stores) {
            if (s.containsKey(key)) return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Store s : stores) {
            if (s.containsValue(value)) return true;
        }
        return false;
    }

    /**
     * Returns a null if the object is not found, regardless of the underlying store's policies.
     *
     * @param key
     * @return
     */
    @Override
    public Object get(Object key) {
        for (Store s : stores) {
            try {
                Object obj = s.get(key);
                if (obj != null) return obj;
            } catch (Throwable t) {
                // do nothing. Allow for unavailable stores.
            }
        }
        return null;
    }

    @Override
    public Object put(Object key, Object value) {
        for (Store s : stores) {
            if (s.containsKey(key)) {
                return s.put(key, value);
            }
        }
        return defaultStore().put(key, value);
    }

    /**
     * This removes the object from every store.
     *
     * @param key
     * @return
     */
    @Override
    public Object remove(Object key) {
        Object obj = null;
        for (Store s : stores) {
            Object obj2 = s.remove(key);
            if (obj2 != null) {
                obj = obj2;
            }
        }
        return obj;
    }

    /**
     * Expensive method since this must check each value in the map against each store.
     *
     * @param m
     */
    @Override
    public void putAll(Map m) {
        for (Object obj : m.keySet()) {
            Identifier identifier = (Identifier) obj;
            put(identifier, m.get(identifier));
        }
    }

    /**
     * Use with extreme caution, since this will clear every store!
     */
    @Override
    public void clear() {
        for (Store s : stores) {
            s.clear();
        }
    }

    @Override
    public Set keySet() {
        HashSet<Identifier> set = new HashSet<Identifier>();
        for (Store s : stores) {
            set.addAll(s.keySet());
        }
        return set;
    }

    @Override
    public Collection values() {
        HashSet<Identifiable> set = new HashSet<Identifiable>();
        for (Store s : stores) {
            set.addAll(s.values());
        }
        return set;
    }

    @Override
    public Set entrySet() {
        HashSet set = new HashSet();
        for (Store s : stores) {
            set.addAll(s.entrySet());
        }
        return set;
    }

    @Override
    public List getAll() {
        LinkedList<V> allEntries = new LinkedList<>();
        for (Object object : values()) {
            allEntries.add((V) object);
        }
        return allEntries;
    }

    @Override
    public XMLConverter getXMLConverter() {
        throw new NotImplementedException("Error: Cannot have a single converter for an aggregate store");
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }

    @Override
    public List search(String key, String condition, boolean isRegEx, List attr) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }
}

