package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.request.IssuerRequest;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;

import javax.servlet.http.HttpServletRequest;

/**
 * Request to issuer for UserInfo.
 * <p>Created by Jeff Gaynor<br>
 * on 10/7/13 at  2:36 PM
 */
public class UIIRequest2 extends IssuerRequest{
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String username;
    public UIIRequest2(HttpServletRequest servletRequest, Client client, AccessToken accessToken) {
        super(servletRequest, client);
        this.accessToken = accessToken;
    }

    public UIIRequest2(HttpServletRequest servletRequest, AccessToken accessToken) {
          super(servletRequest, null);
          this.accessToken = accessToken;
      }
    private AccessToken accessToken;

    @Override
     public Response process(Server server) {
         if (server instanceof UII2) {
             return ((UII2) server).processUIRequest(this);
         }
         return super.process(server);
     }

    /**
     * Getter for access token
     * @return Access token
     */
    public AccessToken getAccessToken() {
        return accessToken;
    }

    /**
     * Setter for access token
     * @param accessToken  Access token
     */
    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }
}
