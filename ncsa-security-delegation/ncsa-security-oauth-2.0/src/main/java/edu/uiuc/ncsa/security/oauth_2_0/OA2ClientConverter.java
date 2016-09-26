package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.storage.impl.ClientConverter;
import edu.uiuc.ncsa.security.storage.data.ConversionMap;
import edu.uiuc.ncsa.security.storage.data.SerializationKeys;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import java.util.Collection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/17/14 at  1:29 PM
 */
public class OA2ClientConverter<V extends OA2Client> extends ClientConverter<V> {
    public OA2ClientConverter(IdentifiableProvider<V> identifiableProvider) {
        this(new OA2ClientKeys(), identifiableProvider);
    }

    public OA2ClientConverter(SerializationKeys keys, IdentifiableProvider<V> identifiableProvider) {
        super(keys, identifiableProvider);
    }

    OA2ClientKeys getCK2() {
        return (OA2ClientKeys) keys;
    }

    @Override
    public V fromMap(ConversionMap<String, Object> map, V v) {
        V otherV = super.fromMap(map, v);
        if (map.get(getCK2().callbackUri()) != null) {
            JSONArray json = (JSONArray) JSONSerializer.toJSON(map.get(getCK2().callbackUri()));
            Collection<String> zzz = (Collection<String>) JSONSerializer.toJava(json);
            otherV.setCallbackURIs(zzz);
        }

        otherV.setRtLifetime(map.getLong(getCK2().rtLifetime()));
        return otherV;
    }

    @Override
    public void toMap(V client, ConversionMap<String, Object> map) {
        super.toMap(client, map);
        map.put(getCK2().rtLifetime(), client.getRtLifetime());
        if(client.getCallbackURIs() == null){
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for (String s : client.getCallbackURIs()) {
            jsonArray.add(s);
        }
        map.put(getCK2().callbackUri(), jsonArray.toString());
    }
}
