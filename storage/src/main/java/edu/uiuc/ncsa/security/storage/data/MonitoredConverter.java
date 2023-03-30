package edu.uiuc.ncsa.security.storage.data;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;

import java.util.Date;

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
        if (map.containsKey(getKeys().creationTS()) && null!=map.get(getKeys().creationTS())) {
            v.setCreationTS(map.getDate(getKeys().creationTS()));
        }
        if (map.containsKey(getKeys().lastModifiedTS()) && null!= map.get(getKeys().lastModifiedTS())) {
            v.setLastModifiedTS(map.getDate(getKeys().lastModifiedTS()));
        }
        if (map.containsKey(getKeys().lastAccessed()) && null != map.get(getKeys().lastAccessed())) {
            long lll = map.getLong(getKeys().lastAccessed());
            if(0 < lll) {
                // Finally. Only turn it into a date if there is really something there.
                // Various databases return various trivial garbage
                v.setLastAccessed(new Date(lll));
            }
        }
        return v;
    }

    @Override
    public void toMap(V v, ConversionMap<String, Object> map) {
        super.toMap(v, map);
        map.put(getKeys().creationTS(), v.getCreationTS());
        if (v.getLastAccessed() != null) {
            map.put(getKeys().lastAccessed(), v.getLastAccessed().getTime());
        }
        if (v.getLastModifiedTS() != null) {
            map.put(getKeys().lastModifiedTS(), v.getLastModifiedTS());
        }
    }
}
