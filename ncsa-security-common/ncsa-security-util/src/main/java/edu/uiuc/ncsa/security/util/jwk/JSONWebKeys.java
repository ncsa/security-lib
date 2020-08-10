package edu.uiuc.ncsa.security.util.jwk;

import java.util.HashMap;

/**
 * A collection of {@link JSONWebKey} objects. This also allows specifying an identifier as the
 * default to be used for all signing.
 * <p>Created by Jeff Gaynor<br>
 * on 1/9/17 at  10:37 AM
 */
public class JSONWebKeys extends HashMap<String, JSONWebKey> {
    public JSONWebKey getDefault(){
         if(!hasDefaultKey()){  // This checks that the key is set AND the key is actually in this collection.
             throw new IllegalStateException("Error: No default key ID specified.");
         }
        if(!containsKey(defaultKeyID)){  // This checks that the key is set AND the key is actually in this collection.
            throw new IllegalStateException("Error: The default key id \"" + defaultKeyID + "\" does not match any keys. Check your keys and their ids.");
        }

        return get(defaultKeyID);
    }

    /**
     * test if a default key id has been set for this set.
     * @return
     */
    public boolean hasDefaultKey(){
        return defaultKeyID != null;
    }

    /**
     * Get the default key id for this set.
     * @return
     */
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

    /**
     * Add a key to this set. Since the hash is on the identifier, the key is checked for having one before
     * being added and adding a key will fail if there is no identifier set.
     * @param webKey
     */
    public void put(JSONWebKey webKey){
        if(webKey.id == null){
            throw new IllegalStateException("Error: no key id for this webkey");
        }
        put(webKey.id, webKey);
    }
}
