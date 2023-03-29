package edu.uiuc.ncsa.security.storage.data;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  8:26 AM
 */
public class MonitoredConverter<V extends Monitored> extends MapConverter<V> {
    public MonitoredConverter(MonitoredKeys keys, IdentifiableProvider<V> provider) {
        super(keys, provider);
    }

    @Override
    public MonitoredKeys getKeys() {
        return (MonitoredKeys) super.getKeys();
    }

    public V fromMap(ConversionMap<String, Object> map, V client) {
        V v = super.fromMap(map, client);
        v.setIdentifier(map.getIdentifier(getKeys().identifier()));
        if (map.containsKey(getKeys().creationTS())) {
            v.setCreationDate(map.getDate(getKeys().creationTS()));
        }
        if (map.containsKey(getKeys().lastModifiedTS())) {
            v.setLastModifiedDate(map.getDate(getKeys().lastModifiedTS()));
        }
        if (map.containsKey(getKeys().lastAccessed())) {
            v.setLastAccessedDate(map.getDate(getKeys().lastAccessed()));
        }
        return v;
    }

    @Override
    public void toMap(V v, ConversionMap<String, Object> map) {
        super.toMap(v, map);
        map.put(getKeys().creationTS(), v.getCreationDate());
        if (v.getLastAccessedDate() != null) {
            map.put(getKeys().lastAccessed(), v.getLastAccessedDate());
        }
        if (v.getLastModifiedDate() != null) {
            map.put(getKeys().lastModifiedTS(), v.getLastModifiedDate());
        }
    }
}
