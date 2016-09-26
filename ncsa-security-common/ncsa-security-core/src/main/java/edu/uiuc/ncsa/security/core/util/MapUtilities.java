package edu.uiuc.ncsa.security.core.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utilities for dealing with maps.
 * <p>Created by Jeff Gaynor<br>
 * on May 2, 2011 at  1:08:01 PM
 */
public class MapUtilities {

    /**
     * A convenience method to turn this into a list of map entries. This is different from entrySet in that
     * all keys and values are converted to strings.
     *
     * @return
     */
    public static List<Map.Entry<String, String>> toList(Map map) {
        LinkedList<Map.Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>();
        if (map == null) {
            return list;
        }
        for (Object key : map.keySet()) {
            MapEntry me = new MapEntry();
            me.setKey(key.toString());
            me.setValue(map.get(key.toString()).toString());
            list.add(me);
        }
        return list;
    }

    static protected class MapEntry implements Map.Entry<String, String> {
        public void setKey(String key) {
            this.key = key;
        }

        String key;

        public String getKey() {
            return key;
        }

        String value;

        public String getValue() {
            return value;
        }

        public String setValue(String value) {
            String old = value;
            this.value = value;
            return old;
        }
    }

}
