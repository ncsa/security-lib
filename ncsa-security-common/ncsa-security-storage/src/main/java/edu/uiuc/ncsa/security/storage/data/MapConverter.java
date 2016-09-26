package edu.uiuc.ncsa.security.storage.data;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;

/**
 * A class that converts between objects and maps. You must supply some key.
 * <p>Created by Jeff Gaynor<br>
 * on 4/13/12 at  11:38 AM
 */
public class MapConverter<V extends Identifiable> {
    public SerializationKeys keys;
    protected IdentifiableProvider<V> provider;


    public MapConverter(SerializationKeys keys, IdentifiableProvider<V> provider) {
        this.keys = keys;
        this.provider = provider;
    }

    /**
     * Takes a map and returns an object of the given type, initialized with the values of the map.
     *
     * @param data
     * @return
     */
    public V fromMap(ConversionMap<String, Object> data) {
        return fromMap(data, null);
    }

    public V fromMap(ConversionMap<String, Object> map, V v)  {
        if (v == null) {
            v = provider.get(false);
        }
        v.setIdentifier(map.getIdentifier(keys.identifier()));
        return v;

    }

    /**
     * Takes the value and writes the data to the map. The reason that the map is supplied is
     * that there are many specialized maps. It would place undue constraints on this class to try
     * and manage these as well.
     *
     * @param value
     * @param data
     */
    public void toMap(V value, ConversionMap<String, Object> data) {
        data.put(keys.identifier(), value.getIdentifierString());
    }
}
