package edu.uiuc.ncsa.security.oauth_2_0;


import edu.uiuc.ncsa.security.delegation.storage.ClientKeys;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/14/14 at  1:05 PM
 */
public class OA2ClientKeys extends ClientKeys {
    public OA2ClientKeys() {
        super();
        identifier("client_id");
        secret("public_key");
    }


    String callback_uri = "callback_uri";

    public String callbackUri(String... x) {
        if (0 < x.length) callback_uri = x[0];
        return callback_uri;
    }

    String rtLifetime = "rt_lifetime";

    public String rtLifetime(String... x) {
        if (0 < x.length) rtLifetime = x[0];
        return rtLifetime;
    }

}
