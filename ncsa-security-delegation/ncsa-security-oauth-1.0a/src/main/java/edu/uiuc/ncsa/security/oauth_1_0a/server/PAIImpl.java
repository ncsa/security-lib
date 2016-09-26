package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.PAIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.PARequest;
import edu.uiuc.ncsa.security.delegation.server.request.PAResponse;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities;
import edu.uiuc.ncsa.security.oauth_1_0a.client.OAClient;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;

import javax.servlet.ServletException;
import java.net.URI;
import java.security.PublicKey;

import static edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities.whittleMap;
import static edu.uiuc.ncsa.security.util.pkcs.KeyUtil.fromX509PEM;
import static net.oauth.OAuth.RSA_SHA1;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  1:17:46 PM
 */
public class PAIImpl extends AbstractIssuer implements PAIssuer {
    public PAIImpl(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    @Override
    public PAResponse processProtectedAsset(PARequest paRequest) {
        try {
            PARequestImpl protectedAssetRequest = null;
            if (!(paRequest instanceof PARequestImpl)) {
                protectedAssetRequest = new PARequestImpl(paRequest);
            } else {
                protectedAssetRequest = (PARequestImpl) paRequest;
            }

            if (protectedAssetRequest.getParameters().get(OAuth.OAUTH_TOKEN) == null) {
                throw new ServletException("Error: missing access token");
            }
            OAClient oaClient = (OAClient) protectedAssetRequest.getClient();

            OAuthAccessor accessor = OAuthUtilities.createOAuthAccessor(this, oaClient);
            if (oaClient.getSignatureMethod().equals(RSA_SHA1)) {
                // then there *has* to be a public key with the client. Use it.
                PublicKey pk = fromX509PEM(oaClient.getSecret());
                accessor.consumer.setProperty(OAuthConstants.PUBLIC_KEY, pk);
                accessor.setProperty(OAuthConstants.PUBLIC_KEY, pk);
            }
            AccessToken at = protectedAssetRequest.getAccessToken();

            if (at == null) {
                at = tokenForge.getAccessToken(protectedAssetRequest.getParameters());
            }
            accessor.tokenSecret = at.getSharedSecret();

            OAuthUtilities.validate(protectedAssetRequest.getMessage(), accessor);
            PAResponseImpl paResponse = new PAResponseImpl();
            paResponse.setAccessToken(at); // return the right access token with this, so the caller can track it
            paResponse.setParameters(whittleMap(protectedAssetRequest.getParameters()));
            return paResponse;
        } catch (Exception x) {
            throw new GeneralException(x);
        }
    }
}
