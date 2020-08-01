package edu.uiuc.ncsa.security.oauth_2_0.jwt;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKey;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/24/20 at  7:28 AM
 */
public interface AccessTokenHandlerInterface extends PayloadHandler {
    /**
     * The actual simple access token (usually used as the identifier for the claims-based AT.
     * To get the signed claims, invoke {@link #getSignedAT(JSONWebKey}.
     * @return
     */
    AccessToken getAccessToken();
    void setAccessToken(AccessToken accessToken);


    AccessToken getSignedAT(JSONWebKey key);
}
