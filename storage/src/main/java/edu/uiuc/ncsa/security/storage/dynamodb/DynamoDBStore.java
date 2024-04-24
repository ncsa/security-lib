package edu.uiuc.ncsa.security.storage.dynamodb;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.XMLConverter;

import java.util.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/22/24 at  12:59 PM
 */
public class DynamoDBStore<V extends Identifiable>  implements Store<V> {
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
    public List<V> getAll() {
        return null;
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
    public int size(boolean includeVersions) {
        return 0;
    }

    @Override
    public boolean remove(List<Identifiable> objects) {
        return false;
    }

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object o) {
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        return false;
    }

    @Override
    public V get(Object o) {
        return null;
    }

    @Override
    public V put(Identifier identifier, V v) {
        return null;
    }

    @Override
    public V remove(Object o) {
        return null;
    }

    @Override
    public void putAll(Map<? extends Identifier, ? extends V> map) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<Identifier> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<Identifier, V>> entrySet() {
        return null;
    }
}
