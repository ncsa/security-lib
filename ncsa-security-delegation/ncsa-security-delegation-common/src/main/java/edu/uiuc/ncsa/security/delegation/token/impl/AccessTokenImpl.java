package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  1:01:13 PM
 */
public class AccessTokenImpl extends TokenImpl implements AccessToken {

    public AccessTokenImpl(URI token, URI sharedSecret) {
        super(token, sharedSecret);
    }

    public AccessTokenImpl(URI token) {
        super(token, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && !(obj instanceof AccessTokenImpl)) return false;
        return super.equals(obj);
    }


}
