package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.request.IssuerRequest;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/14 at  11:32 AM
 */
public class RTIRequest extends IssuerRequest {
    boolean isOIDC = true;
    public RTIRequest(Client client, boolean isOIDC) {
        super(client);
        this.isOIDC =isOIDC;
    }

    public RTIRequest(HttpServletRequest servletRequest, Client client, boolean isOIDC) {
        super(servletRequest, client);
        this.isOIDC = isOIDC;
    }

    public RTIRequest(HttpServletRequest servletRequest, Client client, AccessToken accessToken, boolean isOIDC) {
        super(servletRequest, client);
        this.accessToken = accessToken;
        this.isOIDC = isOIDC;
    }

    public AccessToken getAccessToken() {

        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    AccessToken accessToken;

    @Override
    public Response process(Server server) {
        if(server instanceof RTI2){
            return ((RTI2)server).processRTRequest(this, isOIDC);
        }
        return super.process(server);
    }
}
