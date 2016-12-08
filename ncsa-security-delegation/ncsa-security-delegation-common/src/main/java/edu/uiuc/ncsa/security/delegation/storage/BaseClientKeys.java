package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/20/16 at  12:57 PM
 */
public class BaseClientKeys extends SerializationKeys {
    String secret = "oauth_client_pubkey";
    String creationTS = "creation_ts";
    String name = "name";
    String email = "email";


    public String name(String... x) {
        if (0 < x.length) name = x[0];
        return name;
    }

    public String email(String... x) {
        if (0 < x.length) email = x[0];
        return email;
    }

    public String creationTS(String... x) {
         if (0 < x.length) creationTS = x[0];
         return creationTS;
     }


    public String secret(String... x) {
        if (0 < x.length) secret = x[0];
        return secret;
    }

    @Override
    public List<String> allKeys() {
        List<String> allKeys = super.allKeys();
        allKeys.add(name());
        allKeys.add(email());
        allKeys.add(creationTS());
        allKeys.add(secret());
        return allKeys;
    }
}
