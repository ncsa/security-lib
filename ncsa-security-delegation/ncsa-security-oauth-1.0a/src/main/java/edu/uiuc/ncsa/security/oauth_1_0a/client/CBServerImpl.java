package edu.uiuc.ncsa.security.oauth_1_0a.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.client.request.CallbackRequest;
import edu.uiuc.ncsa.security.delegation.client.request.CallbackResponse;
import edu.uiuc.ncsa.security.delegation.client.server.CBServer;
import edu.uiuc.ncsa.security.delegation.services.AddressableServer;
import edu.uiuc.ncsa.security.delegation.services.Request;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import net.oauth.OAuth;

import javax.servlet.ServletRequest;
import java.net.URI;

import static net.oauth.OAuth.OAUTH_TOKEN;
import static net.oauth.OAuth.OAUTH_VERIFIER;

/**
 * Sometimes this is useful for services that require a heavyweight callback. Normally this is not
 * needed for most cases.
 * <p>Created by Jeff Gaynor<br>
 * on May 12, 2011 at  1:14:51 PM
 */
public class CBServerImpl implements AddressableServer, CBServer {
    public CBServerImpl(TokenForge tokenForge
            , URI address) {
        this.tokenForge = tokenForge;
        this.address = address;
    }

    @Override
    public URI getAddress() {
        return address;
    }

    @Override
    public Response process(Request request) {
        return request.process(this);
    }

    TokenForge tokenForge;

    URI address;

    @Override
    public CallbackResponse processCallback(CallbackRequest callbackRequest) {
        CallbackResponse cResp = new CallbackResponse();
        ServletRequest servletRequest = callbackRequest.getServletRequest();
        String token = servletRequest.getParameter(OAUTH_TOKEN);
        if (token == null || token.length() == 0) {
            throw new GeneralException("Error: No token found");
        }
        String tc = OAuth.decodePercent(token);
        String verifier = servletRequest.getParameter(OAUTH_VERIFIER);
        if (verifier == null || verifier.length() == 0) {
            throw new GeneralException("Error: No verifier found");
        }
        String v = OAuth.decodePercent(verifier);
        AuthorizationGrant ag = tokenForge.getAuthorizationGrant(tc);
        cResp.setAuthorizationGrant(ag);
        cResp.setVerifier(tokenForge.getVerifier(v));
        return cResp;
    }

}
