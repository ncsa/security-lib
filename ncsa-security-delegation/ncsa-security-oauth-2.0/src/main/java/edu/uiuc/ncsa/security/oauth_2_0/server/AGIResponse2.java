package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.request.AGResponse;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Constants;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


/**
 * Authorization grant response from authorization
 * endpoint on server
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  5:06 PM
 */
public class AGIResponse2 extends IResponse2 implements AGResponse {
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    Client client;
    /**
     * Getter for grant
     *
     * @return Authorization grant object
     */
    public AuthorizationGrant getGrant() {
        return grant;
    }

    AuthorizationGrant grant;

    /**
     * Setter for grant
     *
     * @param grant Authorization grant object
     */
    public void setGrant(AuthorizationGrant grant) {
        this.grant = grant;
    }

    /**
     * Setter for grant
     *
     * @param parameters Map of parameters
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Write the appropriate auth response
     *
     * @param response Response object to write (using OutputStream)
     */
    public void write(HttpServletResponse response) throws IOException {
        HashMap m = new HashMap();
        m.put(OA2Constants.AUTHORIZATION_CODE, grant.getToken());
        if (parameters.get(OA2Constants.STATE) != null && 0 < parameters.get(OA2Constants.STATE).length()) {
            m.put(OA2Constants.STATE, parameters.get(OA2Constants.STATE));
        }
        JSONObject jsonObject = JSONObject.fromObject(m);
        Writer osw = response.getWriter();
        jsonObject.write(osw);
        osw.flush();
        osw.close();
    }

}
