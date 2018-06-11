package edu.uiuc.ncsa.security.oauth_2_0.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.client.request.ATRequest;
import edu.uiuc.ncsa.security.delegation.client.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.client.server.ATServer;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.oauth_2_0.OA2RefreshTokenImpl;
import edu.uiuc.ncsa.security.servlet.ServiceClient;
import net.sf.json.JSONObject;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static edu.uiuc.ncsa.security.oauth_2_0.OA2Constants.*;
import static edu.uiuc.ncsa.security.oauth_2_0.server.claims.OA2Claims.ISSUED_AT;
import static edu.uiuc.ncsa.security.oauth_2_0.server.claims.OA2Claims.SUBJECT;


/**
 * This class handles the client call to the access token endpoint
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  4:33 PM
 */

public class ATServer2 extends TokenAwareServer implements ATServer {

    public ATServer2(ServiceClient serviceClient, String wellKnown) {
        super(serviceClient, wellKnown);
    }

    /**
     * Processes access token request
     *
     * @param atRequest Access token request
     * @return Access token response
     */
    public ATResponse processATRequest(ATRequest atRequest) {
        return getAccessToken(atRequest);
    }

    /**
     * Gets access token. This also returns the refresh token (if any) in the response.
     * Note that there are claims that are returned in the a parameter map for the
     * "subject" and the "issued at" claims. Neither of these require any processing
     * here, but clients should have them available to enforce policies.
     * The id token is returned as a parameter in the response as well as a json object.
     *
     * @param atRequest Access token request
     * @return Access token response
     */
    protected ATResponse2 getAccessToken(ATRequest atRequest) {
        Map params = atRequest.getParameters();
        if (params.get(REDIRECT_URI) == null) {
            throw new GeneralException("Error: the client redirect uri was not set in the request.");
        }
        // Create the request
        HashMap m = new HashMap();
        m.put(AUTHORIZATION_CODE, atRequest.getAuthorizationGrant().getToken());
        m.put(GRANT_TYPE, AUTHORIZATION_CODE_VALUE);
        m.put(CLIENT_ID, atRequest.getClient().getIdentifierString());
        m.put(CLIENT_SECRET, atRequest.getClient().getSecret());

        m.put(REDIRECT_URI, params.get(REDIRECT_URI));
        String response = getServiceClient().getRawResponse(m);
        JSONObject jsonObject = getAndCheckResponse(response);
        if (!jsonObject.containsKey(ACCESS_TOKEN)) {
            throw new IllegalArgumentException("Error: No access token found in server response");
        }
        AccessTokenImpl at = new AccessTokenImpl(URI.create(jsonObject.getString(ACCESS_TOKEN)));
        OA2RefreshTokenImpl rt = null;
        if (jsonObject.containsKey(REFRESH_TOKEN)) {
            // the refresh token is optional, so if it is missing then there is nothing to do.
            rt = new OA2RefreshTokenImpl(URI.create(jsonObject.getString(REFRESH_TOKEN)));
            try {
                if (jsonObject.containsKey(EXPIRES_IN)) {
                    long expiresIn = Long.parseLong(jsonObject.getString(EXPIRES_IN)) * 1000L; // convert from sec to ms.
                    rt.setExpiresIn(expiresIn);
                }
            } catch (NumberFormatException nfx) {
                // This is optional to return, so it is possible that this might not work.
            }
        }
        JSONObject claims = getAndCheckIDToken(jsonObject, atRequest);
        if (jsonObject.containsKey(ID_TOKEN)) {
            params.put(RAW_ID_TOKEN, jsonObject.getString(ID_TOKEN));
        }
        // and now the specific checks for ID tokens returned by the AT server.
        if (!claims.getString(NONCE).equals(atRequest.getParameters().get(NONCE))) {
            throw new GeneralException("Error: Incorrect nonce \"" + atRequest.getParameters().get(NONCE) + "\" returned from server");
        }

        params.put(ISSUED_AT, new Date(claims.getLong(ISSUED_AT) * 1000L));
        params.put(SUBJECT, claims.getString(SUBJECT));
        if (claims.containsKey(AUTHORIZATION_TIME)) {
            // auth_time claim is optional (unless max_age is returned). At this point we do not do max_age.
            params.put(AUTHORIZATION_TIME, claims.getLong(AUTHORIZATION_TIME));
        }
        params.put(ID_TOKEN, claims);
        ATResponse2 atr = createResponse(at, rt);
        atr.setParameters(params);
        return atr;
    }

    protected ATResponse2 createResponse(AccessToken at, RefreshToken rt) {
        return new ATResponse2(at, rt);
    }

}
