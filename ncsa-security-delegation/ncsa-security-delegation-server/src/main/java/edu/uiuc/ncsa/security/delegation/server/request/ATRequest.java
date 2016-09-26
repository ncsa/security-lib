package edu.uiuc.ncsa.security.delegation.server.request;

import edu.uiuc.ncsa.security.delegation.server.issuers.ATIssuer;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.Verifier;

import javax.servlet.http.HttpServletRequest;

/**
 * Request for a {@link AccessToken}.
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  12:30:35 PM
 */
public class ATRequest extends IssuerRequest {
    public ATRequest(HttpServletRequest httpServletRequest, Client client) {
        super(httpServletRequest, client);
    }

    AuthorizationGrant authorizationGrant;

    public AuthorizationGrant getAuthorizationGrant() {
        return authorizationGrant;
    }

    public void setAuthorizationGrant(AuthorizationGrant authorizationGrant) {
        this.authorizationGrant = authorizationGrant;
    }

    @Override
    public Response process(Server server) {
        if (server instanceof ATIssuer) {
            return ((ATIssuer) server).processATRequest(this);
        }
        return super.process(server);
    }

      public Verifier getVerifier() {
        return verifier;
    }

    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
    }

    Verifier verifier;
    public long getExpiresIn() {
           return expiresIn;
       }

       public void setExpiresIn(long expiresIn) {
           this.expiresIn = expiresIn;
       }

       long expiresIn;
}
