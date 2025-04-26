package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.*;
import edu.uiuc.ncsa.security.core.exceptions.UnregisteredObjectException;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This is where static generic methods for stores like searches live.
 * These are <b><i>horribly</i></b> inefficient but sometimes there is no other
 * way. The intent is that if you need to use these, they are centralized
 * to prevent boiler plating.
 * <p>Created by Jeff Gaynor<br>
 * on 8/10/21 at  6:38 AM
 */
public class GenericStoreUtils {
    public static <V extends Identifiable> List<V> search(Store<V> store,
                                                          String key,
                                                          String condition,
                                                          boolean isRegEx,
                                                          List<String> attr) {
        ArrayList<V> results = new ArrayList();
        Collection<V> values = store.values();
        Iterator iterator = values.iterator();
        Pattern pattern = null;
        if (isRegEx) {
            pattern = Pattern.compile(condition);
        }
        while (iterator.hasNext()) {
            V v = (V) iterator.next();
            XMLMap map = new XMLMap();

            store.getXMLConverter().toMap(v, map);
            String targetValue = map.get(key).toString();
            boolean keepV = false;
            if (isRegEx) {
                keepV = pattern.matcher(targetValue).matches();
            } else {
                keepV = targetValue.equals(condition);
            }
            if (keepV) {
                map.removeKeys(attr);  // return subset
                results.add(store.getXMLConverter().fromMap(map, null));
            }
        }
        return results;
    }


    public static <V extends Identifiable> List<V> search(Store<V> store,
                                                          String key,
                                                          String condition,
                                                          boolean isRegEx,
                                                          List<String> attr,
                                                          String dateField,
                                                          Date before, Date after) {
        ArrayList<V> results = new ArrayList();
        Collection<V> values = store.values();
        Iterator iterator = values.iterator();
        boolean hasKey = !StringUtils.isTrivial(key);
        boolean hasDate = !StringUtils.isTrivial(dateField);

        Pattern pattern = null;
        if (hasKey && isRegEx) {
            pattern = Pattern.compile(condition);
        }
        while (iterator.hasNext()) {
            V v = (V) iterator.next();
            XMLMap map = new XMLMap();

            store.getXMLConverter().toMap(v, map);
            boolean keepV = true;
            if (hasKey) {
                String targetValue = map.get(key).toString();
                if (isRegEx) {
                    keepV = !pattern.matcher(targetValue).matches();
                } else {
                    keepV = !targetValue.equals(condition);
                }
            }
            if (!keepV) {
                continue;
            }
            if (hasDate) {
                if (after != null) {
                    keepV = 0 <= map.getDate(dateField).compareTo(after);
                }
                if (before != null) {
                    keepV = map.getDate(dateField).compareTo(before) <= 0;
                }

            }
            if (keepV) {
                map.removeKeys(attr);  // return subset
                results.add(store.getXMLConverter().fromMap(map, null));
            }
        }
        return results;
    }

    public static <V extends Identifiable> List<V> getMostRecent(Store<V> store, int n, List<String> attributes) {
        // Some black magic in Java to cast everything in the Collection as some other type.
        // Casts between different object types are finicky, but you can cast a collection of
        // objects to anything. Do this so if we ever change our inheritance later it does not break
        Collection<? extends Object> list = store.values();
        ArrayList<V> results = new ArrayList();

        if (list.isEmpty() || n == 0) {
            return results;
        }
        if (!(list.iterator().next() instanceof DateComparable)) {
            throw new UnsupportedOperationException("getting the most recent object is not supported by this store.");
        }
        // Can still blow up if one of the objects is not DateComparable. Best we can do in Java....
        Collection<DateComparable> values = (Collection<DateComparable>) list;
        SortByDate sortByDate = new SortByDate();
        TreeSet<DateComparable> treeSet = new TreeSet<>(sortByDate);
        treeSet.addAll(values);
        Iterator iterator;
        if (n < 0) {
            iterator = treeSet.descendingSet().iterator();// creates a view, then returns iterator in it
            n = Math.abs(n);
        } else {
            iterator = treeSet.iterator();
        }
        // We are constrained by both n and the number of elements in the store.
        // If the store has < n elements, don't try to return extras.
        int i = 0;
        while (iterator.hasNext() && i < n) {
            results.add((V) iterator.next());
            i++;
        }
        return results;
    }


    static class SortByDate<V extends DateComparable> implements Comparator<V> {
        @Override
        public int compare(V a, V b) {
            return a.getCreationTS().compareTo(b.getCreationTS());
        }
    }

    /**
     * Convert an identifiable object to an {@link XMLMap}. This is useful for serializing
     * objects in the store, backing them up, etc.
     * @param store
     * @param identifiable
     * @return
     */
    public static XMLMap toXML(Store store, Identifiable identifiable) {
        XMLMap map = new XMLMap();
        store.getXMLConverter().toMap(identifiable, map);
        return map;
    }

    /**
     * This will convert a map into an object. You must issue a save separately or
     * if you prefer, {@link #fromXMLAndSave(Store, XMLMap)}.
     * @param store
     * @param map
     * @return
     */
    public static Identifiable fromXML(Store store, XMLMap map) {
        return store.getXMLConverter().fromMap(map, null);
    }

    public static void fromXMLAndSave(Store store, XMLMap map){
        store.save(fromXML(store, map));
    }

    /**
     * Generic implementation of {@link Store#update(List, Map)} and will loop through the elements doing the update
     * one at a time. If the store has the capability at all for batch processing, implement it. This is for things
     * like {@link MemoryStore}s and {@link FileStore}s that have no such ability. Note that part of the contract is
     * that the element must exist and an exception is thrown of there is no such element.
     * @param store
     * @param ids
     * @param values
     */
    public static <V extends Identifiable> void  update(Store<V>  store, List<Identifier> ids, Map<String, Object>  values){
        XMLConverter<V> xmlConverter = store.getXMLConverter();
        for(Identifier id : ids){
            V v = store.get(id);
              if(v==null){
                  throw new UnregisteredObjectException("No object in the store with id "+id+" found for update");
              }
              Map<String, Object> oldObject = new HashMap<>();
              xmlConverter.toMap(v, oldObject);
              oldObject.putAll(values);
              store.save(xmlConverter.fromMap(oldObject, null)); // creates a new object
        }
    }
    public static <V extends Identifiable> List<V> search(Store<V>  store, String key, boolean isNull) {
        ArrayList<V> results = new ArrayList();
        Collection<V> values = store.values();
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            V v = (V) iterator.next();
            XMLMap map = new XMLMap();

            store.getXMLConverter().toMap(v, map);
            if(isNull && !map.containsKey(key)){
                results.add(store.getXMLConverter().fromMap(map, null));
            }

            if(!isNull && map.containsKey(key)){
                results.add(store.getXMLConverter().fromMap(map, null));
            }
        }// end while
        return results;
    }
}
