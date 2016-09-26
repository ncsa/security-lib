package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.issuers.AGIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.AGRequest;
import edu.uiuc.ncsa.security.delegation.server.request.IssuerResponse;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Utilities;

import java.net.URI;
import java.util.Map;

/**
 * Authorization grant issuer class.  Creates and issues
 * authorization grants.
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  5:03 PM
 */
public class AGI2 extends AbstractIssuer implements AGIssuer {

    /**
    Constructor
    @param tokenForge Token forge to use
    @param address URI of authorization endpoint
     */
    public AGI2(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    /**
    Accepts authorization grant request and returns response with an authorization
    code
    @param authorizationGrantRequest
    @return Authorization grant response
    */
    public IssuerResponse processAGRequest(AGRequest authorizationGrantRequest) {

        // Get values out of AGRequest and populate variables
        Map<String, String> reqParamMap = OA2Utilities.getParameters(authorizationGrantRequest.getServletRequest());

        // TODO Check parameters passed in

        AuthorizationGrant ag = tokenForge.getAuthorizationGrant(); // get a fresh new shiny one.
        AGIResponse2 agResponse = new AGIResponse2();
        agResponse.setGrant(ag);
        agResponse.setParameters(reqParamMap);

        return agResponse;
    }
}
