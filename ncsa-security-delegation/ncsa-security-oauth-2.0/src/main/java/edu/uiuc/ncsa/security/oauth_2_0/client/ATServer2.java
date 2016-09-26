package edu.uiuc.ncsa.security.oauth_2_0.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.client.request.ATRequest;
import edu.uiuc.ncsa.security.delegation.client.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.client.server.ATServer;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.oauth_2_0.IDTokenUtil;
import edu.uiuc.ncsa.security.oauth_2_0.OA2RefreshTokenImpl;
import edu.uiuc.ncsa.security.servlet.ServiceClient;
import net.sf.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static edu.uiuc.ncsa.security.oauth_2_0.OA2Constants.*;
import static edu.uiuc.ncsa.security.oauth_2_0.server.OA2Claims.*;


/**
 * This class handles the client call to the access token endpoint
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  4:33 PM
 */

public class ATServer2 extends ASImpl implements ATServer {
    ServiceClient serviceClient;

    public ServiceClient getServiceClient() {
        return serviceClient;
    }


    public ATServer2(ServiceClient serviceClient) {
        super(serviceClient.host());
        this.serviceClient = serviceClient;

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
     *
     * @param atRequest Access token request
     * @return Access token response
     */
    protected ATResponse getAccessToken(ATRequest atRequest) {
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
        // parse the response. Generally an HTML response will start with a tag or some blank lines.
        if (response.startsWith("<") || response.startsWith("\n") ) {
            // this is actually HTML
        //    System.out.println(getClass().getSimpleName() + ".getAccessToken: response from server is " + response);
            throw new GeneralException("Error: Response from server was html: " + response);
        }
        JSONObject jsonObject = JSONObject.fromObject(response);
        // FIXME!! Make sure these are not null and issue some messages or something.
        if(!jsonObject.containsKey(ACCESS_TOKEN)){
            throw new IllegalArgumentException("Error: No access token found in server response");
        }
        AccessTokenImpl  at = new AccessTokenImpl(URI.create(jsonObject.getString(ACCESS_TOKEN)));

        OA2RefreshTokenImpl rt = null;
        if(jsonObject.containsKey(REFRESH_TOKEN)){
            // the refresh token is optional, so if it is missing then there is nothing to do.
            rt  = new OA2RefreshTokenImpl(URI.create(jsonObject.getString(REFRESH_TOKEN)));
            try {
                      if (jsonObject.containsKey(EXPIRES_IN)) {
                          long expiresIn = Long.parseLong(jsonObject.getString(EXPIRES_IN)) * 1000L; // convert from sec to ms.
                          rt.setExpiresIn(expiresIn);
                      }
                  } catch (NumberFormatException nfx) {
                      // This is optional to return, so it is possible that this might not work.
                  }
        }


        if (!jsonObject.getString(TOKEN_TYPE).equals(BEARER_TOKEN_TYPE)) {
            throw new GeneralException("Error: incorrect token type");
        }
        // Fix for OAUTH-164, id_token support follows.
        JSONObject claims = IDTokenUtil.readIDToken(jsonObject.getString(ID_TOKEN));
        // Now we have to check claims.
        if (!claims.getString(AUDIENCE).equals(atRequest.getClient().getIdentifierString())) {
            throw new GeneralException("Error: Audience is incorrect");
        }
        if (!claims.getString(NONCE).equals(atRequest.getParameters().get(NONCE))) {
            throw new GeneralException("Error: Incorrect nonce \"" + atRequest.getParameters().get(NONCE) + "\" returned from server");
        }
        try {
            URL host = getAddress().toURL();
            URL remoteHost = new URL(claims.getString(ISSUER));
            if (!host.getProtocol().equals(remoteHost.getProtocol()) ||
                    !host.getHost().equals(remoteHost.getHost()) ||
                    host.getPort() != remoteHost.getPort()) {
                throw new GeneralException("Error: Issuer is incorrect");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if(!claims.containsKey(EXPIRATION)){
            throw new GeneralException("Error: Claims failed to have required expiration");
        }
        long exp = Long.parseLong(claims.getString(EXPIRATION))*1000L; // convert to ms.
        if(exp <= System.currentTimeMillis()){
            throw new GeneralException("Error: expired claim.");
        }
        params.put(ISSUED_AT, new Date(claims.getLong(ISSUED_AT)*1000L));
        params.put(SUBJECT, claims.getString(SUBJECT));
        params.put(AUTHORIZATION_TIME, claims.getLong(AUTHORIZATION_TIME));
        ATResponse atr = new ATResponse2(at,rt);
        atr.setParameters(params);
        return atr;
    }

}
