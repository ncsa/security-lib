package edu.uiuc.ncsa.security.util.json;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.XMLConverter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This is used in various places for testing and debugging. It ends up here because of test re-usability
 * requirements and should not be used for anything in production.
 * <p>Created by Jeff Gaynor<br>
 * on 6/6/19 at  6:23 PM
 */
public class TestMemStore<V extends JSONEntry> extends HashMap<Identifier, V> implements JSONStore<V> {

    @Override
    public List<V> getAll() {
        LinkedList<V> returnValues = new LinkedList<>();
        returnValues.addAll(values());
        return returnValues;
    }

    @Override
    public V create() {
        return null;
    }

    @Override
    public void update(V value) {

    }

    @Override
    public void register(V value) {

    }

    @Override
    public void save(V value) {

    }

    @Override
    public XMLConverter<V> getXMLConverter() {
        return null;
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx) {
        return null;
    }
}