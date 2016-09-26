package edu.uiuc.ncsa.security.delegation.server.issuers;

import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;

import javax.inject.Provider;
import java.net.URI;

/**
 * Abstract factory for issuers.
 * <p>Created by Jeff Gaynor<br>
 * on 4/26/12 at  1:37 PM
 */
public abstract class IssuerProvider<T extends DoubleDispatchServer> implements Provider<T> {
    protected TokenForge tokenForge;
    protected URI address;

    protected IssuerProvider(TokenForge tokenForge, URI address) {
        this.address = address;
        this.tokenForge = tokenForge;
    }
}
