package edu.uiuc.ncsa.security.oauth_2_0.jwt;

import edu.uiuc.ncsa.security.delegation.token.RefreshToken;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKey;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/5/20 at  9:09 AM
 */
public interface RefreshTokenHandlerInterface extends PayloadHandler {
    RefreshToken getRefreshToken();
    void setRefreshToken(RefreshToken refreshToken);
    RefreshToken getSignedRT(JSONWebKey key);
}
