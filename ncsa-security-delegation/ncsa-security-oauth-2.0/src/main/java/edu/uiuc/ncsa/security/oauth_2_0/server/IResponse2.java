package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.request.IssuerResponse;

import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  5:07 PM
 */
public abstract class IResponse2 implements IssuerResponse {

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    Map<String,String> parameters;
    public Map<String, String> getParameters() {
        return parameters;
    }

}
