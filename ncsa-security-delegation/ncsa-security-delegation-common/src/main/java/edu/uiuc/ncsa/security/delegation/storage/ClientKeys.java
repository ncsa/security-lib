package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

/**
* <p>Created by Jeff Gaynor<br>
* on 4/25/12 at  3:06 PM
*/
public class ClientKeys extends SerializationKeys {
    public ClientKeys() {
        identifier("oauth_consumer_key");
    }

    String secret = "oauth_client_pubkey";
    String name = "name";
    String homeURL = "home_url";
    String creationTS = "creation_ts";
    String errorURL = "error_url";
    String email = "email";
    String proxyLimited = "proxy_limited";

    public String proxyLimited(String... x) {
        if (0 < x.length) proxyLimited = x[0];
        return proxyLimited;
    }


    public String secret(String... x) {
        if (0 < x.length) secret = x[0];
        return secret;
    }

    public String name(String... x) {
        if (0 < x.length) name = x[0];
        return name;
    }

    public String homeURL(String... x) {
        if (0 < x.length) homeURL = x[0];
        return homeURL;
    }

    public String creationTS(String... x) {
        if (0 < x.length) creationTS = x[0];
        return creationTS;
    }

    public String errorURL(String... x) {
        if (0 < x.length) errorURL = x[0];
        return errorURL;
    }

    public String email(String... x) {
        if (0 < x.length) email = x[0];
        return email;
    }
}
