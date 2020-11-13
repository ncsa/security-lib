package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.delegation.token.OA1TokenImpl;
import edu.uiuc.ncsa.security.delegation.token.Verifier;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 3, 2011 at  1:30:15 PM
 */
public class VerifierImpl extends OA1TokenImpl implements Verifier {
    public VerifierImpl(URI token) {
        super(token, null);
    }

    public VerifierImpl(URI token, URI sharedSecret) {
        super(token, sharedSecret);
    }
}
