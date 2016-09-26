package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.issuers.ATIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.ATRequest;
import edu.uiuc.ncsa.security.delegation.server.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.oauth_2_0.OA2TokenForge;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Utilities;

import java.net.URI;
import java.util.Map;

/**
 * Access token issuer class for OAuth2.  Creates and issues
 * access tokens
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  5:05 PM
 */
public class ATI2 extends AbstractIssuer implements ATIssuer {

    /**
    Constructor
    @param tokenForge Token forge to use
    @address URI of access token endpoint
     */
    public ATI2(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    /**
    Processes access token request
    @param accessTokenRequest Access token request
    @return Access token response
     */
    public ATResponse processATRequest(ATRequest accessTokenRequest) {
        Map<String,String> reqParamMap = OA2Utilities.getParameters(accessTokenRequest.getServletRequest());
        // get access token
        OA2TokenForge tf2 = (OA2TokenForge) tokenForge;
        ATIResponse2 atResp = new ATIResponse2(tf2.getAccessToken(), tf2.getRefreshToken());
        atResp.setParameters(reqParamMap);
        return atResp;
    }
}
