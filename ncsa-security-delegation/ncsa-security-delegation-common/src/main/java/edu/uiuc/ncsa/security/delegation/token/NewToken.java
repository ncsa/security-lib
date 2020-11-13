package edu.uiuc.ncsa.security.delegation.token;

import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/9/20 at  8:34 AM
 */
public interface NewToken extends Token{
    boolean isExpired();
    long getExpiresAt();
    long getIssuedAt();
    JSONObject toJSON();
    void fromJSON(JSONObject json);
    void fromString(String rawToken);

    String TOKEN_KEY = "token";
    String EXPIRES_AT_KEY = "expires_at";
    String ISSUED_AT_KEY = "issued_at";
    long OLD_SYSTEM_DEFAULT_LIFETIME = 1000L*60L*15L; // 15 minutes.
}
