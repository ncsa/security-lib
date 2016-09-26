package edu.uiuc.ncsa.security.delegation.server.request;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.Verifier;

/**
 * Server response to a request for an {@link AccessToken}
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  12:35:57 PM
 */
public interface ATResponse extends IssuerResponse {
    public AccessToken getAccessToken();

    public Verifier getVerifier();
}
