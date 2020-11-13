package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  1:01:13 PM
 */
public class AccessTokenImpl extends TokenImpl implements AccessToken {
    public AccessTokenImpl(String rawToken) {
        super(rawToken);
    }

    public AccessTokenImpl(URI token) {
        super(token);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && !(obj instanceof AccessTokenImpl)) return false;
        return super.equals(obj);
    }


}
