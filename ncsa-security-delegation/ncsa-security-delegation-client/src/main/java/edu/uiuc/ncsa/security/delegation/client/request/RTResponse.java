package edu.uiuc.ncsa.security.delegation.client.request;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/24/14 at  11:19 AM
 */
public class RTResponse extends ATResponse{
    public RTResponse(AccessToken accessToken) {
        super(accessToken);
    }

    public RTResponse(AccessToken accessToken, RefreshToken refreshToken) {
        super(accessToken);
        this.refreshToken = refreshToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    RefreshToken refreshToken;
}
