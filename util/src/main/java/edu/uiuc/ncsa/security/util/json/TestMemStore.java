package edu.uiuc.ncsa.security.util.json;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.exceptions.UnregisteredObjectException;

import java.util.*;

/**
 * This is used in various places for testing and debugging. It ends up here because of test re-usability
 * requirements and should not be used for anything in production.
 * <p>Created by Jeff Gaynor<br>
 * on 6/6/19 at  6:23 PM
 */
public class TestMemStore<V extends JSONEntry> extends HashMap<Identifier, V> implements JSONStore<V> {
    @Override
    public void update(List<Identifier> ids, Map<String, Object> values) throws UnregisteredObjectException {

    }

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
    public int size(boolean includeVersions) {
        if(includeVersions){
            return super.size();
        }
        int count = 0;
        for(Identifier id : keySet() ){
            if(!id.toString().contains(VERSION_TAG)){
                    count++;
            }
        }
        return count;
    }

    @Override
    public XMLConverter<V> getXMLConverter() {
        return null;
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx) {
        return null;
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx, List<String> attr) {
        return null;
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx, List<String> attr, String dateField, Date before, Date after) {
        return null;
    }

    @Override
    public boolean remove(List<V> objects) {
        for(Identifiable identifiable : objects){
             remove(identifiable);
        }
        return true;
    }

    @Override
    public boolean removeByID(List<Identifier> objects) {
        return false;
    }

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }
}
