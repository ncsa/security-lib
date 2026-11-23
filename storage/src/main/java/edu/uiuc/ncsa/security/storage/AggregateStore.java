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
 * calls to each underlying store. This supports lists of read and write
 * store, so it is possible to have one store for reading and anothe store for
 * writing. If an item is not in a store, then the next store is checked.
 * This allows an application to treat a collection
 * of stores as a one big logical store. Note that this works locally
 * at each stage, so, e.g. if an {@link #update(edu.uiuc.ncsa.security.core.Identifiable)}
 * is issued, the first store found containing the information is what is updated.
 * Not perfect, but will probably keep information more or less localized...
 * <h2>Using this store to migrate stores</h2>
 * <p>
 *     If you judiciously set the read and write stores, so that exactly one is a target
 *     and both are sources, then you will gradually migrate the data from source
 *     to target. This is useful when you need to seamlessly migrate.. The
 *     best part is that you amy check what is not migrated after a certain interval to
 *     winnow out unused store objects.
 * </p>
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  9:16 AM
 */
public class AggregateStore<V extends Store> implements Store {
    public AggregateStore(List<V> writeStores, List<V> readStores) {
        for (V s : writeStores) {
            this.writeStores.addAll(writeStores);
            this.readStores.addAll(readStores);
        }
    }

    public AggregateStore() {
    }

    /*
        Partial implementation of the store interface. This should be completed if
        we go back to using it, otherwise, we are keeping it for now pending more uses
        and just want it to compile.
     */
    @Override
    public int[] update(List m) {
        // ToDO: If a target does not exist yet, this will bomb, so updating between stores is not possible.
        // Make it so failures are checked for existence. This will probably slow it down considerably.
        Throwable lastThrown = null;
        int[] rc;
        for(V s:writeStores){
            try{
                rc = s.update(m);
            }catch (Throwable t){
                lastThrown = t;
            }
        }
        if(lastThrown instanceof RuntimeException){
            throw (RuntimeException) lastThrown;
        }
        throw new GeneralException("Error: could not update any store", lastThrown);
    }

    @Override
    public int[] register(List value) {
        for (V s : writeStores) {
            try {
                return s.register(value);
            }catch (Throwable t){

            }
        }
        return new int[0];
    }

    @Override
    public int[] save(List value) {
        for (V s : writeStores) {
            try {
                return s.save(value);
            }catch(Throwable t){}
        }
        return new int[0];
    }

    @Override
    public void update(Map m) {

    }

    @Override
    public void updateRS(List ids, Map values) throws UnregisteredObjectException {
       throw new NotImplementedException("implement me!");
    }

    @Override
    public boolean removeByID(List objects) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }

    protected List<V> writeStores = new ArrayList<V>();

    public List<V> getReadStores() {
        return readStores;
    }

    public void setReadStores(List<V> readStores) {
        this.readStores = readStores;
    }

    protected List<V> readStores = new ArrayList<V>();

    /**
     * Gives access to the list of stores. Mostly for testing purposes.
     *
     * @return
     */
    public List<V> writeStores() {
        return writeStores;
    }

    /**
     * Caveat! This does not check if the store has already been added! This is because store comparison
     * is probably too expensive and in some cases almost impossible to do. It is up to the application not
     * to add multiple copies of the same store.
     *
     * @param store
     */
    public void addWriteStore(V store) {
        writeStores.add(store);
    }

    public void addReadStore(V store) {
        readStores.add(store);
    }

    /**
     * Adds a store for both reading and writing.
     * @param store
     */
    public void addReadAndWriteStore(V store) {
        addReadStore(store);
        addWriteStore(store);
    }
    @Override
    public int size(boolean includeVersions) {
        int x = 0;
        for (Store s : writeStores) {
            x = x + s.size(includeVersions);
        }
        return x;
    }

    protected void checkValid() {
        if (0 == writeStores.size()) {
            throw new GeneralException("Error: Aggregate store is empty. There must be at least one store in the aggregate.");
        }
    }

    public V defaultStore() {
        checkValid();
        return writeStores.get(0);
    }

    @Override
    public Identifiable create() {
        return defaultStore().create();
    }

    @Override
    public void update(Identifiable value) {
        checkValid();
        for (Store s : writeStores) {
            if (s.containsKey(value.getIdentifier())) {
                    s.update(value);
                    return;
            } // end for
            // If we get here, this does not exist.
            // Assuming that the object is not registered, we will try to register it.
            defaultStore().register(value);
        }
    }

    @Override
    public void register(Identifiable value) {
        defaultStore().register(value);
    }

    @Override
    public void save(Identifiable value) {
        for (Store s : writeStores) {
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
        for (Store s : writeStores) {
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
        for (Store s : writeStores) {
            if (s.containsKey(key)) return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Store s : writeStores) {
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
        for (Store s : readStores) {
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
        if(!(value instanceof Identifiable)){
            throw new IllegalArgumentException("Error: value must be an Identifiable");
        }
        if(!((Identifiable)value).getIdentifier().equals(key)){
            throw new IllegalArgumentException("Error: identifier and value key must match");
        }
        save((Identifiable) value);
        return value;
        }

    /**
     * This removes the object from <b>every</b> store.
     *
     * @param key
     * @return
     */
    @Override
    public Object remove(Object key) {
        Object obj = null;
        for (Store s : writeStores) {
            Object obj2 = s.remove(key);
            if (obj2 != null) {
                obj = obj2;
            }
        }
        for (Store s : readStores) {
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
        for (Object obj : m.values()) {
            if(obj instanceof Identifiable){
                save((Identifiable) obj);
            }
        }
    }

    /**
     * Use with extreme caution, since this will clear every store!
     */
    @Override
    public void clear() {
        for (Store s : readStores) {
            s.clear();
        }

        for (Store s : writeStores) {
            s.clear();
        }
    }

    @Override
    public HashSet keySet() {
        HashSet<Identifier> set = new HashSet<Identifier>();
        for (Store s : writeStores) {
            set.addAll(s.keySet());
        }
        return set;
    }

    @Override
    public Collection values() {
        HashSet<Identifiable> set = new HashSet<Identifiable>();
        for (Store s : writeStores) {
            set.addAll(s.values());
        }
        return set;
    }

    @Override
    public Set entrySet() {
        HashSet set = new HashSet();
        for (Store s : writeStores) {
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
        checkValid();
        return defaultStore().getXMLConverter();
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }

    @Override
    public List search(String key, String condition, boolean isRegEx, List attr) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }

    @Override
    public List search(String key, String condition, boolean isRegEx, List attr, String dateField, Date before, Date after) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }

    @Override
    public List search(String key, boolean isNull) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }

    @Override
    public boolean remove(List objects) {
        throw new NotImplementedException("Error: This is not yet implemented for aggregate stores");
    }

    @Override
    public List getMostRecent(int n, List attributes) {
        return null;
    }

}

