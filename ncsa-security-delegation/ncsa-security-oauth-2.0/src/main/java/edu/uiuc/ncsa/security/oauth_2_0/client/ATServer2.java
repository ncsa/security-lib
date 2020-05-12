package edu.uiuc.ncsa.security.oauth_2_0.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.delegation.client.request.ATRequest;
import edu.uiuc.ncsa.security.delegation.client.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.client.server.ATServer;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.oauth_2_0.OA2RefreshTokenImpl;
import edu.uiuc.ncsa.security.servlet.ServiceClient;
import edu.uiuc.ncsa.security.servlet.ServletDebugUtil;
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
    /**
     * Place holder class for storing ID tokens. ID tokens are consumable in the sense that once we
     * get them back, they are checked for validity and passed along to the client. The problem is that
     * many applications (such as Kubernetes) are using them as a "poor man's SciToken", necessitating
     * that we keep them around for at least a bit. This store holds the raw token (as a string) and
     * the corresponding {@link net.sf.json.JSONObject} keyed by {@link edu.uiuc.ncsa.security.delegation.token.AccessToken}.
     */

    public static class IDTokenEntry {
        public JSONObject idToken;
        public String rawToken;

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "[idToken=" + (idToken == null ? "(null)" : idToken.toString(2)) + ", rawToken=" + (rawToken == null ? "(null)" : rawToken) + "]";
        }
    }


    static HashMap<String, IDTokenEntry> idTokenStore = new HashMap<String, IDTokenEntry>();

    public static HashMap<String, IDTokenEntry> getIDTokenStore() {
        return idTokenStore;
    }

    public ATServer2(ServiceClient serviceClient,
                     String wellKnown,
                     boolean oidcEnabled,
                     long expiresIn,
                     boolean useBasicAuth) {
        super(serviceClient, wellKnown, oidcEnabled);
        this.useBasicAuth = useBasicAuth;
    }

    boolean useBasicAuth = false;
    long expiresIn = 0L;

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
        DebugUtil.trace(this, "getting access token, use http header for token? " + useBasicAuth);
        // Create the request
        HashMap m = new HashMap();
        m.put(AUTHORIZATION_CODE, atRequest.getAuthorizationGrant().getToken());
        m.put(GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
        String clientID = atRequest.getClient().getIdentifierString();
        String clientSecret = atRequest.getClient().getSecret();
        if (!useBasicAuth) {
            // using HTTP Basic Authorization, do not send the credentials along in the map.
            // Use the appropriate call below.
            m.put(CLIENT_ID, clientID);
            m.put(CLIENT_SECRET, clientSecret);
        }
        m.put(REDIRECT_URI, params.get(REDIRECT_URI));
        String response = null;
        if (useBasicAuth) {
            response = getServiceClient().getRawResponse(m, clientID, clientSecret);
        } else {
            response = getServiceClient().getRawResponse(m);
        }
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
        ServletDebugUtil.trace(this, "Is OIDC enabled? " + oidcEnabled);

        if (oidcEnabled) {
            ServletDebugUtil.trace(this, "Processing id token entry");
            IDTokenEntry idTokenEntry = new IDTokenEntry();
            ServletDebugUtil.trace(this, "created new idTokenEntry ");
            JSONObject idToken = getAndCheckIDToken(jsonObject, atRequest);
            ServletDebugUtil.trace(this, "got id token = " + idToken.toString(2));
            if (jsonObject.containsKey(ID_TOKEN)) {
                params.put(RAW_ID_TOKEN, jsonObject.getString(ID_TOKEN));
                idTokenEntry.rawToken = (String) params.get(RAW_ID_TOKEN);
                ServletDebugUtil.trace(this, "raw token = " + idTokenEntry.rawToken);
            }

            idTokenEntry.idToken = idToken;
            ServletDebugUtil.trace(this, "idTokenEntry= " + idTokenEntry);

            // and now the specific checks for ID tokens returned by the AT server.
            if (!idToken.getString(NONCE).equals(atRequest.getParameters().get(NONCE))) {
                throw new GeneralException("Error: Incorrect nonce \"" + atRequest.getParameters().get(NONCE) + "\" returned from server");
            }

            params.put(ISSUED_AT, new Date(idToken.getLong(ISSUED_AT) * 1000L));
            params.put(SUBJECT, idToken.getString(SUBJECT));
            if (idToken.containsKey(AUTHORIZATION_TIME)) {
                // auth_time claim is optional (unless max_age is returned). At this point we do not do max_age.
                params.put(AUTHORIZATION_TIME, idToken.getLong(AUTHORIZATION_TIME));
            }
            params.put(ID_TOKEN, idToken);
            params.put(EXPIRES_IN, expiresIn/1000 ); //convert to seconds.
            ServletDebugUtil.trace(this, "Adding idTokenEntry with id = " + at.getToken() + " to the ID Token store. Store has " + getIDTokenStore().size() + " entries");
            getIDTokenStore().put(at.getToken(), idTokenEntry);
            ServletDebugUtil.trace(this, "ID Token store=" + getIDTokenStore().size());
            ServletDebugUtil.trace(this, "Added idTokenEntry to the ID Token store. Store now has " + getIDTokenStore().size() + " entries");
        } else {
            ServletDebugUtil.trace(this, "Skipping id token entry...");
        }
        ATResponse2 atr = createResponse(at, rt);
        atr.setParameters(params);
        return atr;
    }

    protected ATResponse2 createResponse(AccessToken at, RefreshToken rt) {
        return new ATResponse2(at, rt);
    }

}
