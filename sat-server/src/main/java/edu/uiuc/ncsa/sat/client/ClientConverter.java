package edu.uiuc.ncsa.sat.client;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.storage.data.ConversionMap;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  9:38 AM
 */
public class ClientConverter<V extends SATClient> extends MapConverter<V> {
    @Override
    public ClientKeys getKeys() {
        return (ClientKeys) super.getKeys();
    }

    public ClientConverter(ClientKeys keys, IdentifiableProvider provider) {
        super(keys, provider);
    }


    @Override
    public V fromMap(ConversionMap<String, Object> map, V client) {
        V v= super.fromMap(map, client);
        v.setIdentifier(map.getIdentifier(getKeys().identifier()));
        v.setName(map.getString(getKeys().name()));
        v.setCreationTS(map.getDate(getKeys().creation_ts()));
        JSONObject cfg = JSONObject.fromObject(map.getString(getKeys().config()));
        v.setCfg(cfg);
        return v;
    }

    @Override
    public void toMap(V client, ConversionMap<String, Object> map) {
        super.toMap(client, map);
        map.put(getKeys().name(),client.getName());
        map.put(getKeys().creation_ts(), client.getCreationTS());
        map.put(getKeys().config(), client.getCfg().toString());
    }
}
