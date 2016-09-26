package edu.uiuc.ncsa.security.oauth_2_0.client;

import edu.uiuc.ncsa.security.delegation.client.DelegationService;
import edu.uiuc.ncsa.security.delegation.client.request.*;
import edu.uiuc.ncsa.security.delegation.client.server.AGServer;
import edu.uiuc.ncsa.security.delegation.client.server.ATServer;
import edu.uiuc.ncsa.security.delegation.client.server.PAServer;
import edu.uiuc.ncsa.security.delegation.client.server.RTServer;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Constants;
import edu.uiuc.ncsa.security.servlet.ServiceClient;

import java.net.URI;
import java.util.Map;

/**
 * Delegation service for OIDC
 * <p>Created by Jeff Gaynor<br>
 * on 11/25/13 at  2:20 PM
 */
public class DS2 extends DelegationService {
    UIServer2 uiServer;

    /**
     * Constructor
     *
     * @param agServer Authorization grant handler for client
     * @param atServer Access token handler for client
     * @param paServer Protected asset (cert) request handler for client
     * @param uiServer UserInfo handler for client
     */
    public DS2(AGServer agServer, ATServer atServer, PAServer paServer, UIServer2 uiServer, RTServer rtServer) {
        super(agServer, atServer, paServer);
        this.uiServer = uiServer;
        this.rtServer = rtServer;
    }

    RTServer rtServer;

    /**
     * Getter for UIServer
     *
     * @return UserInfo handler
     */
    public UIServer2 getUiServer() {
        return uiServer;
    }


    public UIResponse getUserInfo(UIRequest uiRequest) {
        return (UIResponse) getUiServer().process(uiRequest);
    }

    public RTServer getRtServer() {
        return rtServer;
    }

    public void setRtServer(RTServer rtServer) {
        this.rtServer = rtServer;
    }

    /**
     * As per spec., issue request for refresh from server. Returned {@link RTResponse} has the associated
     * {@link edu.uiuc.ncsa.security.delegation.token.RefreshToken} and {@link AccessToken}.
     *
     * @return
     */
    public RTResponse refresh(RTRequest refreshTokenRequest) {
        return (RTResponse) getRtServer().process(refreshTokenRequest);
    }

    @Override
    public DelegationResponse processDelegationRequest(DelegationRequest delegationRequest) {
        DelegationResponse delResp = new DelegationResponse(null);
        Map<String,String> m = delegationRequest.getParameters();
        m.put(OA2Constants.CLIENT_ID, delegationRequest.getClient().getIdentifierString());
        m.put(OA2Constants.REDIRECT_URI, delegationRequest.getParameters().get(OA2Constants.REDIRECT_URI));
        URI authZUri = ((AGServer2)getAgServer()).getServiceClient().host();
        URI redirectURI = URI.create(ServiceClient.convertToStringRequest(authZUri.toString(), m));
        delResp.setParameters(m); //send them all back.
        delResp.setRedirectUri(redirectURI);
        return delResp;

    }

    /**
     * Creates redirect URL
     *
     * @param delegationAssetRequest Delegation asset request
     * @param agResp                 Authorization grant response
     * @return URI for redirect
     */
    @Override
    public URI createRedirectURL(DelegationRequest delegationAssetRequest, AGResponse agResp) {
        String rc = delegationAssetRequest.getBaseUri().toString() +
                "?" + OA2Constants.AUTHORIZATION_CODE + "=" + agResp.getAuthorizationGrant().getToken();
        Object state = agResp.getParameters().get(OA2Constants.STATE);
        // As per spec, only return the state if it was sent in the first place.
        if (state != null) {
            rc = rc + "&" + OA2Constants.STATE + "=" + state;
        }
        return URI.create(rc);
    }

}
