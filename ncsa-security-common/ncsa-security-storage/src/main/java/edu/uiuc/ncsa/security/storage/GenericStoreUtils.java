package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Store;
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



}
