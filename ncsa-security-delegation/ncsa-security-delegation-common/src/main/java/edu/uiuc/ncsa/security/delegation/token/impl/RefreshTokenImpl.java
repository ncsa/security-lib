package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.delegation.token.RefreshToken;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/2/13 at  12:02 PM
 */
public class RefreshTokenImpl extends TokenImpl implements RefreshToken {
    public RefreshTokenImpl(URI token) {
        super(token);
    }

    public RefreshTokenImpl(String rawToken) {
        super(rawToken);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj
                instanceof
                RefreshTokenImpl)) {
            return false;
        }
        return super.equals(obj);
    }
}
