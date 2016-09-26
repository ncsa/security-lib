package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.delegation.server.request.CBResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 23, 2011 at  12:54:59 PM
 */
public class CBResponseImpl implements CBResponse {

    public void write(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        return;
    }

    Map<String, String> parameters;

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
