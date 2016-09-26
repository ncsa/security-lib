package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;

import java.net.URI;

/**
 * The OAuth 1.0a version of an AuthorizationGrant
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  1:00:30 PM
 */
public class AuthorizationGrantImpl extends TokenImpl implements AuthorizationGrant {
    public AuthorizationGrantImpl(URI token, URI sharedSecret) {
        super(token, sharedSecret);
    }

    public AuthorizationGrantImpl(URI token) {
        super(token, null);
    }
}
