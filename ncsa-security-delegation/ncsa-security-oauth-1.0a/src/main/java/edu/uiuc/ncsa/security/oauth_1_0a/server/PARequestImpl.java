package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.delegation.server.request.PARequest;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities;
import net.oauth.OAuthMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Specific OAuth-based request. This has to be done because of issues with reprocessing the
 * servlet request to extract parameters and cert requests.
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/12 at  3:01 PM
 */
public class PARequestImpl extends PARequest {
    public PARequestImpl(PARequest paRequest) {
        super(paRequest.getServletRequest(), paRequest.getClient());
        if(paRequest.getAccessToken() != null){
            setAccessToken(paRequest.getAccessToken());
        }
    }

    OAuthMessage message;
    public OAuthMessage getMessage() throws IOException, ServletException {
        if(message == null){
            message = OAuthUtilities.getMessage(getServletRequest());
        }
        return message;
    }

    public Map<String,String> parameters;
    public Map<String,String> getParameters() throws IOException, ServletException {
        if(parameters == null){
            parameters = OAuthUtilities.getParameters(getMessage());
        }
        return parameters;
    }

    public PARequestImpl(HttpServletRequest servletRequest, Client client) {
        super(servletRequest, client);
    }
}
