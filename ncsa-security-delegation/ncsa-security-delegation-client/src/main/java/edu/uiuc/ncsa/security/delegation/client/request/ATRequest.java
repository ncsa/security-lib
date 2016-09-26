package edu.uiuc.ncsa.security.delegation.client.request;

import edu.uiuc.ncsa.security.delegation.client.server.ATServer;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.Verifier;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Apr 13, 2011 at  4:02:13 PM
 */
public class ATRequest extends BasicRequest {

    /**
     * Optional if supported. This should be set to null if it is not supported.
     *
     * @return
     */
    public Verifier getVerifier() {
        return verifier;
    }

    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
    }

    Verifier verifier;

    public Response process(Server server) {
        if (server instanceof ATServer) {
            return ((ATServer) server).processATRequest(this);
        }
        return super.process(server);
    }


    public AuthorizationGrant getAuthorizationGrant() {
        return authorizationGrant;
    }

    public void setAuthorizationGrant(AuthorizationGrant authorizationGrant) {
        this.authorizationGrant = authorizationGrant;
    }

    AuthorizationGrant authorizationGrant;

}
