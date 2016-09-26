package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.delegation.token.Token;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 6, 2011 at  3:18:38 PM
 */
public class FakeTokenImpl implements Token {
    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    String sharedSecret;

    public FakeTokenImpl(URI token) {
        this(token.toString());
    }

    public FakeTokenImpl(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Token)) return false;
        if (!((Token) obj).getToken().equals(getToken())) return false;
        ;
        return true;
    }

    String token;
}
