package edu.uiuc.ncsa.security.delegation.storage.impl;

import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/25/12 at  3:10 PM
 */
public class BasicTransactionKeys extends SerializationKeys {
    public BasicTransactionKeys() {
        identifier("temp_token");
    }

    String tempCred = "temp_token";
    String accessToken = "access_token";
    String verifier = "oauth_verifier";


    public String tempCred(String... x) {
        if (0 < x.length) tempCred = x[0];
        return tempCred;
    }

    public String accessToken(String... x) {
        if (0 < x.length) accessToken = x[0];
        return accessToken;
    }

    public String verifier(String... x) {
        if (0 < x.length) verifier = x[0];
        return verifier;
    }

    @Override
    public List<String> allKeys() {
        List<String> allKeys = super.allKeys();
        allKeys.add(tempCred());
        allKeys.add(accessToken());
        allKeys.add(verifier());
        return allKeys;
    }
}
