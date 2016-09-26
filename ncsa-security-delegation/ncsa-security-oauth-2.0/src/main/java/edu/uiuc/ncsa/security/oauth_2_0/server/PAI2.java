package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.PAIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.PARequest;
import edu.uiuc.ncsa.security.delegation.server.request.PAResponse;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;

import java.net.URI;

/**
 * Protected asset (cert) issuer for Oauth 2 class
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  5:05 PM
 */
public class PAI2 extends AbstractIssuer implements PAIssuer {

    /** Constructor
    @param tokenForge Token forge to use
    @param address URI of cert request endpoint
    */
    public PAI2(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    /**
    Process cert request
    @param paRequest Protected asset request object
    @response Protected asset response object
    */
    public PAResponse processProtectedAsset(PARequest paRequest) {
        try {
         //   Map<String, String> reqParamMap = OA2Utilities.getParameters(paRequest.getServletRequest());

            PAIResponse2 paResponse = new PAIResponse2();
            paResponse.setAccessToken(paRequest.getAccessToken()); // return the right access token with this, so the caller can track it
            return paResponse;
        } catch (Exception x) {
            if(x instanceof RuntimeException){
                throw (RuntimeException)x;
            }
            throw new GeneralException("Error: could not get protected asset", x);
        }
    }
}
