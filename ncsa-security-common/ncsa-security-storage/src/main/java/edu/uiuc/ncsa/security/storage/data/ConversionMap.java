package edu.uiuc.ncsa.security.storage.data;

import edu.uiuc.ncsa.security.core.Identifier;

import java.net.URI;
import java.util.Date;
import java.util.Map;

/**
 * This is a specific type of map that allows for converting from a data type to a given type.
 * For instance, a {@link edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap} contains
 * column names from an SQL table as its keys and the values are the entries, which are strings.
 * A call to {@link #getDate(Object)} would convert the string to a date object. This
 * encapsulates all the work involved in converting between objects.
 * This is part of the data abstraction layer for all stores.
 * <p>Created by Jeff Gaynor<br>
 * on 4/16/12 at  12:06 PM
 */
public interface ConversionMap<K, V> extends Map<K, V> {
    /**
     * Convenience method to change the value to a date.
     * @param key
     * @return
     */
    public Date getDate(K key);

    /**
     * Convenience method to change the value to a boolean
     * @param key
     * @return
     */
    public boolean getBoolean(K key);

    /**
     * Convenience method to change the value to a long.
     * @param key
     * @return
     */
    public long getLong(K key);

    /**
     * Convenience method to change the value to a string
     * @param key
     * @return
     */
    public String getString(K key);

    /**
     * Convenience method to change the value to an identifier
     * @param key
     * @return
     */
    public Identifier getIdentifier(K key);

    /**
     * Convenience method to change the value to a URI.
     * @param key
     * @return
     */
    public URI getURI(K key);

    /**
     * Convenience method to change the value to a byte array. Note that if the value is already a byte
     * array (such as a BLOB in a database) it is simply returned unaltered.
     * @param key
     * @return
     */
    byte[] getBytes(K key);
}
