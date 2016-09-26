package edu.uiuc.ncsa.security.core;

import java.util.Map;

/**
 * Interface for stores. This models a specific case, where there is a primary key, so that this is
 * logically also a map. Each row corresponds to (usually) one java object. What the map interface
 * lacks is concepts for CRUD (create, retrieve, update, delete) and this interface adds those.
 * <h3>Usage</h3>
 * A very large number of practical database programming issues fall into this case and
 * the interfaces and basic classes in this package make setting up and managing them
 * extremely simple.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  10:21:39 AM
 */
public interface Store<V extends Identifiable> extends Map<Identifier, V> {


    /**
     * Create a new object of the given type. This is not in the store until it is registered. Attempts
     * to update the object should throw an exception. Note that this allows for a separation of creation
     * semantics. Some objects require specific initialization before saving
     *
     * @return
     */
    public V create();

    /**
     * Update an existing object.
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
}
