package edu.uiuc.ncsa.security.core;

import edu.uiuc.ncsa.security.core.exceptions.UnregisteredObjectException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for stores. This models a specific case, where there is a primary key, which
 * can be represented with an {@link Identifier} so that this is
 * logically also a map. Each row corresponds to (usually) one java object. What the map interface
 * lacks is concepts for CRUD (create, retrieve, update, delete) and this interface adds those.
 * <h3>Usage</h3>
 * A very large number of practical database programming issues fall into this case and
 * the interfaces and basic classes in this package make setting up and managing them
 * extremely simple. This interface comes with a variety of basic implementations, such
 * as an in-memory store, a file store (which emulates having an index on disk) as well
 * as support for SQL databases.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  10:21:39 AM
 */
public interface Store<V extends Identifiable> extends Map<Identifier, V> {
      public static final String VERSION_TAG = "#version";

    /**
     * Create a new object of the given type. This is not in the store until it is registered. Attempts
     * to update the object should throw an exception. Note that this allows for a separation of creation
     * semantics. Some objects require specific initialization before saving
     *
     * @return
     */
    public V create();

    /**
     * Update an existing object. An {@link UnregisteredObjectException} is thrown if the object has not been saved
     * first.
     *
     * @param value
     */
    public void update(V value);

    /**
     * Almost Identical to put(K,V) but since the object should have an identifier, passing along the key is redundant.
     * This persists the object in the store. Note that this returns void since the contract assumes that this is not
     * registered. If the object is registered an exception should be thrown. Generally use save(V).
     *
     * @param value
     */
    public void register(V value);

    /**
     * Saves an object. This bridges the gap between SQL stores update and insert commands. Implementations should
     * check if the object already exists in the store and issue an appropriate call.
     *
     * @param value
     */
    public void save(V value);

    /**
     * Method to get every element in the store. This is useful for command line interfaces. Note
     * that this might be very expensive.
     * @return
     */
    public List<V> getAll();

    public XMLConverter<V> getXMLConverter();
    

    /**
     * Allows for searching via a reg ex. Note that this may be very expensive for certain stores!
     * @param key
     * @param condition
     * @param isRegEx
     * @return
     */
    public List<V> search(String key, String condition, boolean isRegEx);

    /**
     * Return a subset of all the attributes. For non-SQL stores performance may be slow.
     * @param key
     * @param condition
     * @param isRegEx
     * @param attr
     * @return
     */
    public List<V> search(String key, String condition, boolean isRegEx, List<String> attr);

    public List<V> search(String key, String condition,
                              boolean isRegEx,
                              List<String> attr,
                              String dateField,
                              Date before,
                              Date after);

    public int size(boolean includeVersions);

    /**
     * Removes a list of identifiable objects.
     * @param objects
     * @return
     */
     boolean remove(List<V> objects);

     boolean removeByID(List<Identifier> objects);


     List<V> getMostRecent(int n, List<String> attributes);

}
