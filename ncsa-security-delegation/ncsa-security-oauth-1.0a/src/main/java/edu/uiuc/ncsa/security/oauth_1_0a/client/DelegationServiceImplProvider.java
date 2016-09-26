package edu.uiuc.ncsa.security.oauth_1_0a.client;

import edu.uiuc.ncsa.security.delegation.client.AbstractDelegationServiceProvider;
import edu.uiuc.ncsa.security.delegation.client.DelegationService;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/12/12 at  12:12 PM
 */
public class DelegationServiceImplProvider extends AbstractDelegationServiceProvider {
    public DelegationServiceImplProvider(URI grantServerURI, URI accessServerURI, URI assetServerURI) {
        super(grantServerURI, accessServerURI, assetServerURI);
    }

    @Override
    public DelegationService get() {
        return new DelegationServiceImpl(new AuthorizationServerImpl(grantServerURI),
                new AuthorizationServerImpl(accessServerURI),
                new PAServerImpl(assetServerURI));
    }
}
