package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.delegation.token.impl.TokenImpl;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 6, 2011 at  3:18:38 PM
 */
public class FakeTokenImpl extends TokenImpl {

    @Override
    public String toString() {
        return "FakeTokenImpl{" +
                "token='" + getToken() + '\'' +
                '}';
    }



    public FakeTokenImpl(URI token) {
        this(token.toString());
    }

    public FakeTokenImpl(String token) {
        super(token);
    }


/*
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Token)) return false;
        Token token = (Token) obj;
        if (getToken() == null) {
            if (token.getToken() == null) return true;
            return false;
        } else {
            if (token.getToken() == null) return false;
        }
        if (!token.getToken().equals(getToken())) return false;
        return true;
    }

    String token;

*/

}
