package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.delegation.token.RefreshToken;
import edu.uiuc.ncsa.security.delegation.token.impl.TokenImpl;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/2/13 at  12:02 PM
 */
public class OA2RefreshTokenImpl extends TokenImpl implements RefreshToken{
    public OA2RefreshTokenImpl(URI token) {
        super(token, null);
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    long expiresIn = 0L;

    @Override
    public long getExpiresIn() {
        return expiresIn;
    }
}
