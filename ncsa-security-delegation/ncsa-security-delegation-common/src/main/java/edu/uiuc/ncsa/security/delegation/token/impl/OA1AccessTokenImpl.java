package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.OA1TokenImpl;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/9/20 at  10:12 AM
 */
public class OA1AccessTokenImpl extends OA1TokenImpl implements AccessToken {
    public OA1AccessTokenImpl(URI token, URI sharedSecret) {
        super(token, sharedSecret);
    }

}
