package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/14 at  10:27 AM
 */
public class RTIResponse extends IDTokenResponse {
    public RTIResponse(AccessToken accessToken,
                       RefreshToken refreshToken,
                       boolean isOIDC) {
        super(accessToken, refreshToken,isOIDC);
    }
}
