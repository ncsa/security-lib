package edu.uiuc.ncsa.security.oauth_1_0a.client;

import edu.uiuc.ncsa.security.delegation.client.DelegationService;
import edu.uiuc.ncsa.security.delegation.client.request.AGResponse;
import edu.uiuc.ncsa.security.delegation.client.request.DelegationRequest;
import edu.uiuc.ncsa.security.delegation.client.server.AGServer;
import edu.uiuc.ncsa.security.delegation.client.server.ATServer;
import edu.uiuc.ncsa.security.delegation.client.server.PAServer;

import java.net.URI;

import static net.oauth.OAuth.OAUTH_TOKEN;

/**
 * An OAuth 1.0a implementation of the delegation service.
 * <p>Created by Jeff Gaynor<br>
 * on Apr 15, 2011 at  2:38:40 PM
 */
public class DelegationServiceImpl extends DelegationService {
    public DelegationServiceImpl(AGServer agServer,
                                 ATServer atServer,
                                 PAServer paServer) {
        super(agServer, atServer, paServer);
    }

    @Override
    public URI createRedirectURL(DelegationRequest delegationRequest, AGResponse agResp) {
       return URI.create(delegationRequest.getBaseUri() +
                        "?" + OAUTH_TOKEN + "=" + agResp.getAuthorizationGrant().getToken());
    }
}
