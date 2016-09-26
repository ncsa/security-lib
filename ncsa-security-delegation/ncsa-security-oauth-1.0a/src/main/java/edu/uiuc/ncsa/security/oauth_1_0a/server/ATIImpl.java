package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.server.issuers.ATIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.ATRequest;
import edu.uiuc.ncsa.security.delegation.server.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities;
import edu.uiuc.ncsa.security.oauth_1_0a.client.OAClient;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;

import java.net.URI;
import java.security.PublicKey;
import java.util.Map;

import static edu.uiuc.ncsa.security.util.pkcs.KeyUtil.fromX509PEM;
import static net.oauth.OAuth.RSA_SHA1;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  12:46:28 PM
 */
public class ATIImpl extends AbstractIssuer implements ATIssuer {
    public ATIImpl(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    @Override
    public ATResponse processATRequest(ATRequest accessTokenRequest) {
        try {
            OAClient oaClient = (OAClient) accessTokenRequest.getClient();
            OAuthMessage message = OAuthUtilities.getMessage(accessTokenRequest.getServletRequest());
            Map<String, String> x = OAuthUtilities.getParameters(message);

            OAuthAccessor accessor = OAuthUtilities.createOAuthAccessor(this, oaClient);
            if (oaClient.getSignatureMethod().equals(RSA_SHA1)) {
                // then there *has* to be a public key with the client. Use it.
                PublicKey pk = fromX509PEM(oaClient.getSecret());
                accessor.consumer.setProperty(OAuthConstants.PUBLIC_KEY, pk);
                accessor.setProperty(OAuthConstants.PUBLIC_KEY, pk);
            }
            AuthorizationGrant ag = accessTokenRequest.getAuthorizationGrant();
            // If the authorization grant is not set, try to get it from the arguments.
            if (ag == null) {
                ag = tokenForge.getAuthorizationGrant(x);
                accessor.tokenSecret = null;
            } else {
                accessor.tokenSecret = ag.getSharedSecret();
            }
            OAuthUtilities.validate(message, accessor);
            if (accessTokenRequest.getVerifier() == null) {
                throw new GeneralException("Error, missing verifier");
            }
            AccessToken at = tokenForge.getAccessToken();
            ATResponseImpl atResp = new ATResponseImpl();
            atResp.setVerifier(accessTokenRequest.getVerifier());
            atResp.setAccessToken(at);
            atResp.setParameters(x);
            return atResp;
        } catch (Exception
                x) {
            if (x instanceof RuntimeException) {
                throw (RuntimeException) x;
            }
            throw new GeneralException(x);
        }
    }
}
