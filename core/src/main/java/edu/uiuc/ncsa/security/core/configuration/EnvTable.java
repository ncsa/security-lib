package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.state.SKey;
import edu.uiuc.ncsa.security.core.state.STable;

public class EnvTable<K extends SKey, V extends EnvEntry> extends STable<K, V> {

    public String get(String key) {
        return get(new SKey(key)).getValue();
    }

    public void put(String key, String value) {
        put(new EnvEntry(new SKey(key), value));
    }
}
