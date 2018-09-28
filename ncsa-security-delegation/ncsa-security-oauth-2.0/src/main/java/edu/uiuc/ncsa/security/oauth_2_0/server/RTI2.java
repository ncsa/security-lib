package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.IssuerRequest;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Constants;
import edu.uiuc.ncsa.security.oauth_2_0.OA2TokenForge;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Utilities;
import edu.uiuc.ncsa.security.servlet.ServletDebugUtil;

import java.net.URI;
import java.util.Map;

/**
 * Refresh Token Issuer for OAuth2.
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/14 at  10:03 AM
 */
public class RTI2 extends AbstractIssuer {
    public RTI2(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    public IResponse2 processRTRequest(IssuerRequest req, boolean isOIDC) {

        RTIRequest request = (RTIRequest) req;
        Map<String, String> reqParamMap = OA2Utilities.getParameters(request.getServletRequest());
        ServletDebugUtil.dbg(this,"Request parameters:" + reqParamMap);
        reqParamMap.put(OA2Constants.CLIENT_ID, req.getClient().getIdentifierString());
        OA2TokenForge tokenForge2 = (OA2TokenForge) tokenForge;
        RefreshToken refreshToken = tokenForge2.getRefreshToken();
        AccessToken accessToken = tokenForge2.getAccessToken(); // spec says all new access token

        RTIResponse resp = new RTIResponse(accessToken,refreshToken,isOIDC);
        resp.setParameters(reqParamMap);
        return resp;
    }
}
