package edu.uiuc.ncsa.security.oauth_2_0.client;

import edu.uiuc.ncsa.security.delegation.client.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;

/**
 * Since the OAuth 2 protocol supports getting a refresh token back from the server with the access token,
 * we have to include it in this class.
 * <p>Created by Jeff Gaynor<br>
 * on 3/12/14 at  12:05 PM
 */
public class ATResponse2 extends ATResponse {
    public ATResponse2(AccessToken accessToken, RefreshToken refreshToken) {
        super(accessToken);
        this.refreshToken = refreshToken;
    }

    RefreshToken refreshToken;

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }
}
