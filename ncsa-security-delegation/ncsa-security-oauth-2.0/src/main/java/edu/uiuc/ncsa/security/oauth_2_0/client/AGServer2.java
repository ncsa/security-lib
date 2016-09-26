package edu.uiuc.ncsa.security.oauth_2_0.client;

import edu.uiuc.ncsa.security.delegation.client.request.AGRequest;
import edu.uiuc.ncsa.security.delegation.client.request.AGResponse;
import edu.uiuc.ncsa.security.delegation.client.server.AGServer;
import edu.uiuc.ncsa.security.delegation.token.impl.AuthorizationGrantImpl;
import edu.uiuc.ncsa.security.oauth_2_0.NonceHerder;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Constants;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Scopes;
import edu.uiuc.ncsa.security.servlet.ServiceClient;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;

import java.net.URI;
import java.security.SecureRandom;
import java.util.HashMap;

/**
 * This class manages the client call to the authorization grant server
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  4:27 PM
 */
public class AGServer2 extends ASImpl implements AGServer {
    /**
     * The number of bytes in the random state string sent to the server.
     */
    public static int STATE_LENGTH = 16;
    ServiceClient serviceClient;

    public ServiceClient getServiceClient() {
        return serviceClient;
    }

    public AGServer2(ServiceClient serviceClient) {
        super(serviceClient.host());
        this.serviceClient = serviceClient;
    }

    SecureRandom secureRandom = new SecureRandom();

    /**
     * Accepts AGRequest, obtains auth code, packs said authCode into AGResponse
     * and returns AGResponse
     *
     * @param acRequest Authorization grant request
     * @return Authorization grant response
     */
    public AGResponse processAGRequest(AGRequest acRequest) {
        String nonce = NonceHerder.createNonce();
        HashMap m = new HashMap();
        m.put(OA2Constants.RESPONSE_TYPE, OA2Constants.AUTHORIZATION_CODE);
        m.put(OA2Constants.CLIENT_ID, acRequest.getClient().getIdentifierString());
        m.put(OA2Constants.SCOPE, OA2Scopes.SCOPE_OPENID + " " + OA2Scopes.SCOPE_MYPROXY + " " + OA2Scopes.SCOPE_PROFILE);
        m.put(OA2Constants.REDIRECT_URI, acRequest.getParameters().get(OA2Constants.REDIRECT_URI));
        byte[] bytes = new byte[STATE_LENGTH];
        secureRandom.nextBytes(bytes);
        String sentState = Hex.encodeHexString(bytes);
        m.put(OA2Constants.STATE, sentState);
        m.put(OA2Constants.NONCE, nonce);
        m.put(OA2Constants.PROMPT, OA2Constants.PROMPT_LOGIN);
        String responseString = getServiceClient().getRawResponse(m);
        //System.out.println(getClass().getSimpleName() + ".processAGRequest: raw response=" + responseString);
        JSONObject json = JSONObject.fromObject(responseString);
        String accessCode = json.getString(OA2Constants.AUTHORIZATION_CODE);
        if (accessCode == null) {
            throw new IllegalArgumentException("Error: server did not return an access code.");
        }
        String state = json.getString(OA2Constants.STATE);
        if (!sentState.equals(state)) {

            throw new IllegalStateException("The state string returned by the server does not match the one sent.");
        }
        HashMap map = new HashMap();
        // optional but send it along if it is there.
        map.put(OA2Constants.STATE, state);
        AuthorizationGrantImpl agi = new AuthorizationGrantImpl(URI.create(accessCode), null);
        AGResponse agr = new AGResponse(agi);
        agr.setParameters(map);
        return agr;
    }
}
