package edu.uiuc.ncsa.security.delegation.server.request;

import edu.uiuc.ncsa.security.delegation.server.issuers.CBIssuer;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.Verifier;

import java.net.URI;

/**
 * Request to a callback server.
 * <p>Created by Jeff Gaynor<br>
 * on May 23, 2011 at  11:30:10 AM
 */
public class CBRequest extends IssuerRequest {
    /**
     * How long should the issuer wait for a response to this request? A value of 0 (or less)
     * means to accept whatever the defaults are for the underlying library.
     *
     * @return
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    int connectionTimeout = 0;

    @Override
    public Response process(Server server) {
        if (server instanceof CBIssuer) {
            return ((CBIssuer) server).processCallbackRequest(this);
        }
        return super.process(server);
    }

    AuthorizationGrant authorizationGrant;

    public AuthorizationGrant getAuthorizationGrant() {
        return authorizationGrant;
    }

    public void setAuthorizationGrant(AuthorizationGrant authorizationGrant) {
        this.authorizationGrant = authorizationGrant;
    }

    public URI getCallbackUri() {
        return callbackUri;
    }

    public void setCallbackUri(URI callbackUri) {
        this.callbackUri = callbackUri;
    }

    public Verifier getVerifier() {
        return verifier;
    }

    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
    }

    Verifier verifier;
    URI callbackUri;

    public CBRequest(Client client) {
        super(client);

    }

    @Override
    public String toString() {
        return "CBRequest[grant=" + authorizationGrant + ", uri=" + callbackUri + ", verifier=" + verifier + "]";
    }
}
