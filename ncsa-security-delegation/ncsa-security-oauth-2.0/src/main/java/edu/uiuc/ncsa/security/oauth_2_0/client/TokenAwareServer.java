package edu.uiuc.ncsa.security.oauth_2_0.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.delegation.client.request.BasicRequest;
import edu.uiuc.ncsa.security.oauth_2_0.JWTUtil;
import edu.uiuc.ncsa.security.servlet.ServiceClient;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeys;
import net.sf.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static edu.uiuc.ncsa.security.oauth_2_0.OA2Constants.*;
import static edu.uiuc.ncsa.security.oauth_2_0.server.claims.OA2Claims.*;

/**
 * Since the processing of claims is to be supported for refresh tokens as well, the machinery for it should be
 * available generally to access and refresh token servers.
 * <p>Created by Jeff Gaynor<br>
 * on 9/13/17 at  2:37 PM
 */
public abstract class TokenAwareServer extends ASImpl {

    ServiceClient serviceClient;

    public ServiceClient getServiceClient() {
        return serviceClient;
    }


    public TokenAwareServer(ServiceClient serviceClient, String wellKnown) {
        super(serviceClient.host());
        this.serviceClient = serviceClient;
        this.wellKnown = wellKnown;
    }

    String wellKnown = null;


    public JSONWebKeys getJsonWebKeys() {
        // Fix for OAUTH-164, id_token support follows.
        if (wellKnown == null) {
            throw new NFWException("Error: no well-known URI has been configured. Please add this to the configuration file.");
        }
        return JWTUtil.getJsonWebKeys(getServiceClient(), wellKnown);
    }

    protected JSONObject getAndCheckResponse(String response) {
        if (response.startsWith("<") || response.startsWith("\n")) {
            // this is actually HTML
            //    System.out.println(getClass().getSimpleName() + ".getAccessToken: response from server is " + response);
            throw new GeneralException("Error: Response from server was html: " + response);
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.fromObject(response);
        }catch(Throwable t){
            // it is at this point we may not have a JSON object because the request failed and the server returned an
            // error string. Throw an exception, print the response.
            DebugUtil.dbg(this,"Response from server was not a JSON Object: " + response);
            throw new GeneralException("Error: The server encountered an error nd the response was not JSON.", t);
        }
        if (!jsonObject.getString(TOKEN_TYPE).equals(BEARER_TOKEN_TYPE)) {
            throw new GeneralException("Error: incorrect token type");
        }
        return jsonObject;
    }

    protected JSONObject getAndCheckIDToken(JSONObject jsonObject, BasicRequest atRequest) {
        JSONWebKeys keys = getJsonWebKeys();

        JSONObject claims;
        if (!jsonObject.containsKey(ID_TOKEN)) {
            throw new GeneralException("Error: Missing id token.");
        }
        claims = JWTUtil.verifyAndReadJWT(jsonObject.getString(ID_TOKEN), keys);
        if(claims.isNullObject()){
            // the response may be a null object. At this point it means that there was a null
            // object and that the resulting signature was valid for it, so that is indeed the server response.
            return new JSONObject();
        }
        // Now we have to check claims.
        if (!claims.getString(AUDIENCE).equals(atRequest.getClient().getIdentifierString())) {
            throw new GeneralException("Error: Audience is incorrect");
        }

        try {
            URL host = getAddress().toURL();
            URL remoteHost = new URL(claims.getString(ISSUER));
            if (!host.getProtocol().equals(remoteHost.getProtocol()) ||
                    !host.getHost().equals(remoteHost.getHost()) ||
                    host.getPort() != remoteHost.getPort()) {
                throw new GeneralException("Error: Issuer is incorrect. Got \"" + remoteHost + "\", expected \"" + host + "\"");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (!claims.containsKey(EXPIRATION)) {
            throw new GeneralException("Error: Claims failed to have required expiration");
        }
        long exp = Long.parseLong(claims.getString(EXPIRATION)) * 1000L; // convert to ms.
        if (exp <= System.currentTimeMillis()) {
            throw new GeneralException("Error: expired claim.");
        }
        return claims;
    }
}
