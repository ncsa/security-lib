package edu.uiuc.ncsa.security.delegation.storage.impl;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.storage.ClientKeys;
import edu.uiuc.ncsa.security.storage.data.ConversionMap;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/13/12 at  3:48 PM
 */
public class ClientConverter<V extends Client> extends MapConverter<V> {
    public ClientConverter(IdentifiableProvider<V> identifiableProvider) {
      this(new ClientKeys(), identifiableProvider);
    }

    public ClientConverter(SerializationKeys keys, IdentifiableProvider<V> identifiableProvider) {
        super(keys, identifiableProvider);
    }

    protected ClientKeys getCK() {
        return (ClientKeys) keys;
    }

    @Override
    public V fromMap(ConversionMap<String, Object> map,V v) {
        V value = super.fromMap(map, v);
        value.setName(map.getString(getCK().name()));
        value.setHomeUri(map.getString(getCK().homeURL()));
        value.setCreationTS(map.getDate(getCK().creationTS()));
        value.setErrorUri(map.getString(getCK().errorURL()));
        value.setSecret(map.getString(getCK().secret()));
        value.setEmail(map.getString(getCK().email()));
        value.setProxyLimited(map.getBoolean(getCK().proxyLimited()));
        return value;
    }

    @Override
    public void toMap(V client, ConversionMap<String, Object> map) {
        super.toMap(client, map);

        map.put(getCK().secret(), client.getSecret());
        map.put(getCK().name(), client.getName());
        map.put(getCK().homeURL(), client.getHomeUri());
        map.put(getCK().creationTS(), client.getCreationTS());
        map.put(getCK().errorURL(), client.getErrorUri());
        map.put(getCK().email(), client.getEmail());
        map.put(getCK().proxyLimited(), client.isProxyLimited());
    }
}
