package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.core.util.DateUtils;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.delegation.token.NewToken;
import net.sf.json.JSONObject;

import java.net.URI;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkNoNulls;

/**
 * OAuth 1.0 tokens always have an associated shared secret.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  12:58:52 PM
 */
public class TokenImpl implements NewToken {

    public TokenImpl(URI token) {
        this.token = token;
    }

    public TokenImpl(String rawToken) {
        fromString(rawToken);
    }

    URI token;

    public String getToken() {
        if (token == null) return null;
        return getURIToken().toString();
    }

    public URI getURIToken() {
        return token;
    }

    public void setToken(URI token) {
        this.token = token;
    }


    public boolean equals(Object obj) {
        // special case: If the object is null and the values are, then accept them as being equal.
        if (!(obj instanceof TokenImpl)) return false;
        TokenImpl at = (TokenImpl) obj;
        // special case is that this has null values and the object is null.
        // These then should be considered equal.
        if (!checkNoNulls(getURIToken(), at.getURIToken())) return false;
        if(!at.getToken().equals(getToken())) return false;
        if(at.getExpiresAt() != getExpiresAt()) return false;
        if(at.getIssuedAt() != getIssuedAt()) return false;
        return true;
    }

    /**
     * Does everything but final ]. Over-ride this and your {@link #toString()} will work
     *
     * @return
     */
    protected StringBuilder createString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getSimpleName() + "[");
        if (getToken() == null) {
            stringBuilder.append(TOKEN_KEY + "=(null)");
        } else {
            stringBuilder.append(TOKEN_KEY + "=" + getToken());
        }
        stringBuilder.append(", " + ISSUED_AT_KEY + "=" + issuedAt);
        stringBuilder.append(", " + EXPIRES_AT_KEY + "=" + expiresAt);
        return stringBuilder;

    }

    @Override
    public String toString() {
        return createString().toString() + "]";
    }


    @Override
    public boolean isExpired() {
        if(getExpiresAt()< System.currentTimeMillis()) return true;
        return false;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    long expiresAt = -1L;

    @Override
    public long getExpiresAt() {
        return expiresAt;
    }

    long issuedAt = -1L;

    @Override
    public long getIssuedAt() {
        return issuedAt;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
         json.put(TOKEN_KEY, token.toString());
         json.put(ISSUED_AT_KEY, issuedAt);
         json.put(EXPIRES_AT_KEY, expiresAt);
        return json;
    }


    @Override
    public void fromJSON(JSONObject json) {
        if(!json.containsKey(TOKEN_KEY)){
            throw new IllegalArgumentException("Error: the json object is not a token");
        }
        token =  URI.create(json.getString(TOKEN_KEY));
        if(json.containsKey(ISSUED_AT_KEY)) {
            issuedAt = json.getLong(ISSUED_AT_KEY);
        }
        if(json.containsKey(EXPIRES_AT_KEY)) {
            expiresAt = json.getLong(EXPIRES_AT_KEY);
        }
    }

    /**
     * For use deserializing tokens from a backend. This tries to figure out if the token is new or old format
     * and then to do the right thing.
     * @param rawToken
     */
    public void fromString(String rawToken){
        if(StringUtils.isTrivial(rawToken)){
            return; // nothing to do...
        }
        rawToken = rawToken.trim();
        if(rawToken.startsWith("{")){
            // Assume its JSON
            fromJSON(JSONObject.fromObject(rawToken));
            return;
        }
        // it's a legacy token
        token = URI.create(rawToken);
        try {
            issuedAt = DateUtils.getDate(token).getTime();
            expiresAt = issuedAt + NewToken.OLD_SYSTEM_DEFAULT_LIFETIME; // no other option.
        }catch (Throwable t){
            DebugUtil.trace(this, "error: unable to determine date for token \"" + rawToken + "\"");
        }
    }

}
