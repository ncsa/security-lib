package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.delegation.token.NewToken;
import net.sf.json.JSONObject;

import java.net.URI;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkNoNulls;
import static edu.uiuc.ncsa.security.core.util.Identifiers.*;

/**
 * OAuth 1.0 tokens always have an associated shared secret. These do not.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  12:58:52 PM
 */
public class TokenImpl implements NewToken {
    XProperties params = new XProperties();
    String version = null;

    @Override
    public String getVersion() {
        if (version == null) {
            if (params.containsKey(VERSION_2_0_TAG)) {
                version = params.getString(VERSION_2_0_TAG);
            }
        }
        return version;
    }

    /**
     * Checks if the version is null, effectively meaning it was created before versions existed.
     *
     * @return
     */
    public boolean isOldVersion() {
        return getVersion() == null || getVersion().equals(VERSION_1_0_TAG);
    }

    public TokenImpl(String sciToken, URI jti) {
        this.token = URI.create(sciToken);
        init(jti);
    }

    public TokenImpl(URI token) {
        this.token = token;
        init(token);
    }

    protected void init(URI uri) {
        if (uri == null) {
            return; // can happen. Return so there is not an NPE.
        }
        String s = uri.getQuery();
        if (StringUtils.isTrivial(uri.getQuery())) {
            // Version 1.0 tokens.
            version = VERSION_1_0_TAG;

        } else {
            // Version 2.0+ tokens.
            Map<String, String> parameters = getParameters(uri);
            params.putAll(parameters);
            version = VERSION_2_0_TAG;
        }

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
        if (!checkNoNulls(getVersion(), at.getVersion())) return false;
        if (!at.getToken().equals(getToken())) return false;
        if (at.getLifetime() != getLifetime()) return false;
        if (at.getIssuedAt() != getIssuedAt()) return false;
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
            stringBuilder.append("token=(null)");
        } else {
            stringBuilder.append("token=" + getToken());
        }
        stringBuilder.append(", " + TIMESTAMP_TAG + "=" + getIssuedAt());
        stringBuilder.append(", " + LIFETIME_TAG + "=" + getLifetime());
        stringBuilder.append(", " + VERSION_TAG + "=" + getVersion());
        return stringBuilder;

    }

    @Override
    public String toString() {
        return createString().toString() + "]";
    }


    @Override
    public boolean isExpired() {
        DebugUtil.trace(this, "current time " + System.currentTimeMillis() + " exp at " + (getLifetime() + getIssuedAt()));
        if (getLifetime() + getIssuedAt() < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }


    public void setLifetime(long lifetime) {
        this.lifetime = lifetime;
    }

    long lifetime = -1L;

    @Override
    public long getLifetime() {
        if (lifetime < 0) {
            if (params.containsKey(LIFETIME_TAG)) {
                lifetime = params.getLong(LIFETIME_TAG);
            }
        }
        return lifetime;
    }

    long issuedAt = -1L;

    public void setVersion(String version) {
        this.version = version;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public long getIssuedAt() {
        if (issuedAt < 0) {
            if (params.containsKey(TIMESTAMP_TAG)) {
                issuedAt = params.getLong(TIMESTAMP_TAG);
            }
        }
        return issuedAt;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("token", token.toString());
        json.put(TIMESTAMP_TAG, issuedAt);
        json.put(LIFETIME_TAG, lifetime);
        return json;
    }


    @Override
    public void fromJSON(JSONObject json) {
        if (!json.containsKey("token")) {
            throw new IllegalArgumentException("Error: the json object is not a token");
        }
        token = URI.create(json.getString("token"));
        if (json.containsKey(TIMESTAMP_TAG)) {
            issuedAt = json.getLong(TIMESTAMP_TAG);
        }
        if (json.containsKey(LIFETIME_TAG)) {
            lifetime = json.getLong(LIFETIME_TAG);
        }
    }

    public String toB64(){
        return TokenUtils.encodeToken(this);
    }
    public void fromB64(String b64Encoded){
        String rawToken = TokenUtils.decodeToken(b64Encoded);
        URI newToken = URI.create(rawToken);
        setToken(newToken);
        init(newToken);
    }
}
