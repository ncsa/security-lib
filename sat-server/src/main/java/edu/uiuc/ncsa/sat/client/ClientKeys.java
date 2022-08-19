package edu.uiuc.ncsa.sat.client;

import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

import java.security.PublicKey;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  9:09 AM
 */
public class ClientKeys extends SerializationKeys {
    PublicKey publicKey;
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

    public String creation_ts(String... x) {
        if (0 < x.length) creation_ts = x[0];
        return creation_ts;
    }

    @Override
    public List<String> allKeys() {
        return super.allKeys();
    }
}
