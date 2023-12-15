package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.*;
import edu.uiuc.ncsa.security.core.exceptions.UninitializedException;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * A high-level class for storing things to streams. Implementations must produce the streams.
 * These streams must be storable and retrievable by a key or identifier (being a store,
 * this is required) and allow for retrieving streams based on other keys as well,
 * which are simple hash strings of the identifier.
 * A simple example would be a file system where each file has a name (a hash of the identifier) and
 * then several other strings are used to create unique index entries. The index entries are
 * named by hashes of the new key and their content is a simple string with the identifier in it.
 * <br><br>Note that this will try to convert the objects to XML using a supplied converter.
 * Failing this, it will default to java object serialization.
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on 4/25/12 at  10:54 AM
 */
public abstract class IndexedStreamStore<V extends Identifiable> implements Store<V> {


    protected MapConverter<V> converter;
    protected IdentifiableProvider<V> identifiableProvider;
    protected Initializable initializer;

    /**
     * A hash map of items created by this store. You <i>should</i> keep track of every item created
     * and if an item already exists return that.
     *
     * @return returns created items.
     */
    public HashMap<Identifier, V> getCreatedItems() {
        if (createdItems == null) {
            createdItems = new HashMap<Identifier, V>();
        }
        return createdItems;
    }


    HashMap<Identifier, V> createdItems;


    protected void put(V t) {
        if (t.getIdentifier() == null) {
            throw new UninitializedException("Error: There is no identifier for this store entry");
        }
        put(t.getIdentifier(), t);
    }


    public V put(Identifier key, V value) {
        V oldValue = null;
        if (!containsKey(value.getIdentifier())) {
            save(value);
        } else {
            oldValue = get(value.getIdentifier());
            update(value);
        }
        return oldValue;
    }

    protected String hashString(String identifier) {
        return getDigest(identifier);
    }


    String getDigest(String identifier) {
        return DigestUtils.shaHex(identifier);
    }


    protected V loadStream(InputStream fis) throws IOException {
        V t = null;

        if (converter == null) {
            throw new IllegalStateException("Error: no converter");
        }
        XMLMap map = new XMLMap();
        map.fromXML(fis);
        t = identifiableProvider.get(false);
        converter.fromMap(map, t);
        fis.close();
        return t;

    }

    public boolean isEmpty() {
        return size() == 0;
    }

    protected void createIndexEntry(String otherKey, String identifier, OutputStream outputStream) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        osw.write(hashString(identifier));
        osw.flush();
        osw.close();
    }


    @Override
    public V create() {
        return identifiableProvider.get();
    }
}
