package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.storage.impl.ClientConverter;
import edu.uiuc.ncsa.security.oauth_2_0.server.LDAPConfiguration;
import edu.uiuc.ncsa.security.oauth_2_0.server.LDAPConfigurationUtil;
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
        if(map.containsKey(getCK2().issuer())){
            otherV.setIssuer((String) map.get(getCK2().issuer()));
        }
        if(map.containsKey(getCK2().signTokens()) && map.get(getCK2().signTokens())!=null){
            otherV.setSignTokens(map.getBoolean(getCK2().signTokens()));
        }
        if(map.containsKey(getCK2().ldap())){
            otherV.setLdaps(mapToLDAPS(map, getCK2().ldap()));
        }
        return otherV;
    }

    protected Collection<LDAPConfiguration> mapToLDAPS(ConversionMap<String, Object> map, String key) {
        JSONObject json = new JSONObject();
        JSON j =  JSONSerializer.toJSON(map.get(key));
        json.put("ldap", j);
        return LDAPConfigurationUtil.fromJSON(j);
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
        if(client.getIssuer()!= null){
            map.put(getCK2().issuer(), client.getIssuer());
        }
        map.put(getCK2().signTokens(), client.isSignTokens());
        if (client.getScopes() != null) {
            JSONArray scopes = new JSONArray();

            for (String s : client.getScopes()) {
                scopes.add(s);
            }

            map.put(getCK2().scopes(), scopes.toString());
        }
        if(client.getLdaps()!= null && !client.getLdaps().isEmpty()){
   //         map.put(getCK2().ldap(), LDAPConfigurationUtil.toJSON(client.getLdaps()));
            map.put(getCK2().ldap(), LDAPConfigurationUtil.toJSON(client.getLdaps()).toString());
        }
    }

    @Override
    public V fromJSON(JSONObject json) {
        V v = super.fromJSON(json);
        v.setRtLifetime(getJsonUtil().getJSONValueLong(json, getCK2().rtLifetime()));
        v.setIssuer(getJsonUtil().getJSONValueString(json, getCK2().issuer()));
        v.setSignTokens(getJsonUtil().getJSONValueBoolean(json, getCK2().signTokens()));
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
        JSON ldaps = (JSON) getJsonUtil().getJSONValue(json, getCK2().ldap());
        if(ldaps!=null){
                v.setLdaps(LDAPConfigurationUtil.fromJSON(ldaps));
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

        if(client.getIssuer()!= null){
            getJsonUtil().setJSONValue(json, getCK2().issuer(), client.getIssuer());
        }

        getJsonUtil().setJSONValue(json, getCK2().signTokens(), client.isSignTokens());
        for (String x : scopeList) {
            scopes.add(x);
        }
        if (scopes.size() != 0) {
            getJsonUtil().setJSONValue(json, getCK2().scopes(), scopes);
        }

        if(client.getLdaps()!=null && !client.getLdaps().isEmpty()){
            getJsonUtil().setJSONValue(json,getCK2().ldap(), LDAPConfigurationUtil.toJSON(client.getLdaps()));
        }

    }
}
