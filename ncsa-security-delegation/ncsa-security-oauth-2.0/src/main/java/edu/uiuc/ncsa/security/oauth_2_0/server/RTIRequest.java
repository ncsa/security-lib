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
    public RTIRequest(Client client) {
        super(client);
    }

    public RTIRequest(HttpServletRequest servletRequest, Client client) {
        super(servletRequest, client);
    }

    public RTIRequest(HttpServletRequest servletRequest, Client client, AccessToken accessToken) {
        super(servletRequest, client);
        this.accessToken = accessToken;
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
            return ((RTI2)server).processRTRequest(this);
        }
        return super.process(server);
    }
}
