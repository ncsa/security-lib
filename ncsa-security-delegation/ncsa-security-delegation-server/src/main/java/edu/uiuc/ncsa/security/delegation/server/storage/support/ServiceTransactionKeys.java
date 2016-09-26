package edu.uiuc.ncsa.security.delegation.server.storage.support;

import edu.uiuc.ncsa.security.delegation.storage.impl.BasicTransactionKeys;

/**
* <p>Created by Jeff Gaynor<br>
* on 4/25/12 at  3:09 PM
*/
public class ServiceTransactionKeys extends BasicTransactionKeys {

    protected String lifetime = "certlifetime";
    String callbackUri = "oauth_callback";
    String tempCredValid = "temp_token_valid";
    String accessTokenValid = "access_token_valid";
    String nonce = "nonce";

    public String lifetime(String... x) {
        if (0 < x.length) lifetime = x[0];
        return lifetime;
    }

    public String callbackUri(String... x) {
        if (0 < x.length) callbackUri = x[0];
        return callbackUri;
    }

    public String tempCredValid(String... x) {
        if (0 < x.length) tempCredValid = x[0];
        return tempCredValid;
    }

    public String accessTokenValid(String... x) {
        if (0 < x.length) accessTokenValid = x[0];
        return accessTokenValid;
    }

    public String nonce(String... x) {
        if (0 < x.length) nonce= x[0];
        return nonce;
    }

}
