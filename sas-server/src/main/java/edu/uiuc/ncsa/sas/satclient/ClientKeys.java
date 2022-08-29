package edu.uiuc.ncsa.sas.satclient;

import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  9:09 AM
 */
public class ClientKeys extends SerializationKeys {
    String publicKey="public_key";
    String name = "name";
    String creation_ts = "creation_ts";
    String config = "config";

    public String name(String... x) {
        if (0 < x.length) name = x[0];
        return name;
    }

    public String config(String... x) {
        if (0 < x.length) config = x[0];
        return config;
    }

    public String publicKey(String... x) {
        if (0 < x.length) publicKey = x[0];
        return publicKey;
    }


    public String creation_ts(String... x) {
        if (0 < x.length) creation_ts = x[0];
        return creation_ts;
    }

    @Override
    public List<String> allKeys() {
        List<String> keys = super.allKeys();
        keys.add(creation_ts());
        keys.add(publicKey());
        keys.add(name());
        return keys;
    }
}
