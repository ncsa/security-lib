package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.delegation.token.impl.RefreshTokenImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/14 at  10:27 AM
 */
public class RTIResponse extends IDTokenResponse {
    public RTIResponse(AccessTokenImpl accessToken,
                       RefreshTokenImpl refreshToken,
                       boolean isOIDC) {
        super(accessToken, refreshToken,isOIDC);
    }
}
