package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.storage.impl.ClientConverter;
import edu.uiuc.ncsa.security.storage.data.ConversionMap;
import edu.uiuc.ncsa.security.storage.data.SerializationKeys;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
            otherV.setCallbackURIs(jsonArrayToCollection(map, getCK2().callbackUri()));
        }
        if (map.get(getCK2().scopes()) != null) {
            otherV.setScopes(jsonArrayToCollection(map, getCK2().scopes()));

        }
        otherV.setRtLifetime(map.getLong(getCK2().rtLifetime()));
        return otherV;
    }

    protected Collection<String> jsonArrayToCollection(ConversionMap<String, Object> map, String key) {
        JSONArray json = (JSONArray) JSONSerializer.toJSON(map.get(key));
        Collection<String> zzz = (Collection<String>) JSONSerializer.toJava(json);
        return zzz;
    }


    @Override
    public void toMap(V client, ConversionMap<String, Object> map) {
        super.toMap(client, map);
        map.put(getCK2().rtLifetime(), client.getRtLifetime());
        if (client.getCallbackURIs() == null) {
            return;
        }
        JSONArray callbacks = new JSONArray();
        for (String s : client.getCallbackURIs()) {
            callbacks.add(s);
        }

        map.put(getCK2().callbackUri(), callbacks.toString());

        if (client.getScopes() != null) {
            JSONArray scopes = new JSONArray();

            for (String s : client.getScopes()) {
                scopes.add(s);
            }

            map.put(getCK2().scopes(), scopes.toString());
        }
    }

    @Override
    public V fromJSON(JSONObject json) {
        V v = super.fromJSON(json);
        v.setRtLifetime(getJsonUtil().getJSONValueLong(json, getCK2().rtLifetime()));
        JSON cbs = (JSON) getJsonUtil().getJSONValue(json, getCK2().callbackUri());
        if (cbs != null && cbs instanceof JSONArray) {
            JSONArray array = (JSONArray) json.getJSONObject(getJSONComponentName()).get(getCK2().callbackUri());
            Collection<String> zzz = (Collection<String>) JSONSerializer.toJava(array);
            v.setCallbackURIs(zzz);
        }

        JSON scopes = (JSON) getJsonUtil().getJSONValue(json, getCK2().scopes());
        if (scopes != null && scopes instanceof JSONArray) {
            JSONArray array = (JSONArray) json.getJSONObject(getJSONComponentName()).get(getCK2().scopes());
            Collection<String> zzz = (Collection<String>) JSONSerializer.toJava(array);
            v.setScopes(zzz);
        }
        return v;
    }

    @Override
    public void toJSON(V client, JSONObject json) {
        super.toJSON(client, json);
        getJsonUtil().setJSONValue(json, getCK2().rtLifetime(), client.getRtLifetime());
        JSONArray callbacks = new JSONArray();
        Collection<String> callbackList = client.getCallbackURIs();
        for (String x : callbackList) {
            callbacks.add(x);
        }
        if (callbacks.size() != 0) {
            getJsonUtil().setJSONValue(json, getCK2().callbackUri(), callbacks);
        }
        JSONArray scopes = new JSONArray();

        Collection<String> scopeList = client.getScopes();


        for (String x : scopeList) {
            scopes.add(x);
        }
        if (scopes.size() != 0) {
            getJsonUtil().setJSONValue(json, getCK2().scopes(), scopes);
        }


    }
}
