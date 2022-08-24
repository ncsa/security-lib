package edu.uiuc.ncsa.sat.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.data.ConversionMap;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.util.crypto.KeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKey;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil;
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

    public ClientConverter(ClientKeys keys, ClientProvider provider) {
        super(keys, provider);
    }


    @Override
    public V fromMap(ConversionMap<String, Object> map, V client) {
        V v = super.fromMap(map, client);
        v.setIdentifier(map.getIdentifier(getKeys().identifier()));
        v.setName(map.getString(getKeys().name()));
        v.setCreationTS(map.getDate(getKeys().creation_ts()));
        if(!StringUtils.isTrivial(map.getString(getKeys().config()))){
            JSONObject cfg = JSONObject.fromObject(map.getString(getKeys().config()));
            v.setCfg(cfg);
        }

        if(!StringUtils.isTrivial(map.getString(getKeys().publicKey()))){
            String rawKey = map.getString(getKeys().publicKey());
            if(rawKey.indexOf("{") == -1){
                // not JSON!
                v.setPublicKey(KeyUtil.fromX509PEM(map.getString(getKeys().publicKey())));
            }else{
                try {
                    JSONWebKey webKey = JSONWebKeyUtil.getJsonWebKey(rawKey);
                    v.setPublicKey(webKey.publicKey);
                } catch (Throwable e) {
                  throw new GeneralException("Error decoding key for client " + v.getIdentifierString());
                }
            }

        }
        return v;
    }

    @Override
    public void toMap(V client, ConversionMap<String, Object> map) {
        super.toMap(client, map);
        if (client.getName() != null) {
            map.put(getKeys().name(), client.getName());
        }
        map.put(getKeys().creation_ts(), client.getCreationTS());
        if (client.getCfg() != null) {
            map.put(getKeys().config(), client.getCfg().toString());
        }
        if (client.getPublicKey() != null) {
            map.put(getKeys().publicKey(), KeyUtil.toX509PEM(client.getPublicKey()));
        }
    }
}
