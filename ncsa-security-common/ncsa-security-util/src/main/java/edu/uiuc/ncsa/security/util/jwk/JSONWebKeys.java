package edu.uiuc.ncsa.security.util.jwk;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/9/17 at  10:37 AM
 */
public class JSONWebKeys extends HashMap<String, JSONWebKey> {
    public JSONWebKey getDefault(){
         if(!hasDefaultKey() || !containsKey(defaultKeyID)){
             throw new IllegalStateException("Error: No default key ID specified.");
         }
        return get(defaultKeyID);
    }

    public boolean hasDefaultKey(){
        return defaultKeyID != null;
    }
    public String getDefaultKeyID() {
        return defaultKeyID;
    }

    public void setDefaultKeyID(String defaultKeyID) {
        this.defaultKeyID = defaultKeyID;
    }

    String defaultKeyID;

    public JSONWebKeys(String defaultKeyID) {
        this.defaultKeyID = defaultKeyID;
    }

    public void put(JSONWebKey webKey){
        if(webKey.id == null){
            throw new IllegalStateException("Error: no key id for this webkey");
        }
        put(webKey.id, webKey);
    }
}
