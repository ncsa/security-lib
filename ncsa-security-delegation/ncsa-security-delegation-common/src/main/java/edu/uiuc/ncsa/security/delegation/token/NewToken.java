package edu.uiuc.ncsa.security.delegation.token;

import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/9/20 at  8:34 AM
 */
public interface NewToken extends Token{
    boolean isExpired();
    String getVersion();
    long getLifetime();
    long getIssuedAt();
    JSONObject toJSON();
    void fromJSON(JSONObject json);
   // void fromString(String rawToken);
     void fromB64(String rawb64String);
     String toB64();
    long OLD_SYSTEM_DEFAULT_LIFETIME = 1000L*60L*15L; // 15 minutes.
}
