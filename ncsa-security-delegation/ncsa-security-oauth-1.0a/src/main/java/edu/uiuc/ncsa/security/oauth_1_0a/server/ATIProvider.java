package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.delegation.server.issuers.ATIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.IssuerProvider;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/26/12 at  2:18 PM
 */
public class ATIProvider extends IssuerProvider<ATIssuer> {
    public ATIProvider(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    @Override
    public ATIssuer get() {
        return new ATIImpl(tokenForge, address);
    }
}
