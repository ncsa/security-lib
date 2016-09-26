package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.delegation.token.Token;

import java.net.URI;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkNoNulls;

/**
 * OAuth 1.0 tokens always have an associated shared secret.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  12:58:52 PM
 */
public class TokenImpl implements Token {

    public TokenImpl(URI token, URI sharedSecret) {
        this.sharedSecret = sharedSecret;
        this.token = token;
    }

    URI token;

    public URI getURISharedSecret() {
        return sharedSecret;
    }

    public String getSharedSecret() {
        if (sharedSecret == null) return null;
        return getURISharedSecret().toString();
    }

    public void setSharedSecret(String sharedSecret) {
        if (sharedSecret == null) {
            this.sharedSecret = null;
        } else {
            setSharedSecret(URI.create(sharedSecret));
        }
    }

    public void setSharedSecret(URI sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

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

    URI sharedSecret;

    public boolean equals(Object obj) {
        // special case: If the object is null and the values are, then accept them as being equal.
        if (obj == null && getURIToken() == null && getSharedSecret() == null) return true;
        if (!(obj instanceof TokenImpl)) return false;
        TokenImpl at = (TokenImpl) obj;
        // special case is that this has null values and the object is null.
        // These then should be considered equal.
        if (!checkNoNulls(getURIToken(), at.getURIToken())) return false;
        if (!checkNoNulls(getSharedSecret(), at.getSharedSecret())) return false;
        return true;
    }


    @Override
    public String toString() {
        String out = getClass().getSimpleName() + "[";
        if (getToken() == null) {
            out = out + "token=(null)";

        } else {
            out = out + "token=" + getToken();
        }
        if (getSharedSecret() == null) {
            out = out + "]";
        } else {
            out = out + ", secret=" + getSharedSecret() + "]";
        }

        return out;
    }
}
