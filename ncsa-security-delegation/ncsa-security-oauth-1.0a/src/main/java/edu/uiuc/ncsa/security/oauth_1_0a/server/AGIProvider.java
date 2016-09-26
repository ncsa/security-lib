package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.delegation.server.issuers.AGIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.IssuerProvider;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/26/12 at  2:16 PM
 */
public class AGIProvider extends IssuerProvider<AGIssuer> {
    public AGIProvider(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    @Override
    public AGIssuer get() {
        return new AGIImpl(tokenForge, address);
    }
}
