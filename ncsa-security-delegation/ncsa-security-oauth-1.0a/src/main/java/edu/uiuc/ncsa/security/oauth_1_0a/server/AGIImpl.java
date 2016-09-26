package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.exceptions.UnsupportedProtocolException;
import edu.uiuc.ncsa.security.delegation.server.issuers.AGIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.AGRequest;
import edu.uiuc.ncsa.security.delegation.server.request.AGResponse;
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

import static edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants.*;
import static edu.uiuc.ncsa.security.util.pkcs.KeyUtil.fromX509PEM;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  8:21:14 AM
 */
public class AGIImpl extends AbstractIssuer implements AGIssuer {

    public AGIImpl(TokenForge tokenForge, URI address) {
        super(tokenForge, address);
    }

    @Override
    public AGResponse processAGRequest(AGRequest agReq) {
        try {

            OAuthMessage m = OAuthUtilities.getMessage(agReq.getServletRequest());
            if (!(agReq.getClient() instanceof OAClient)) {
                throw new NotImplementedException("This is only implemented for OAuth client. The class found was " + agReq.getClient().getClass().getName());
            }
            OAClient oaClient = (OAClient) agReq.getClient();

            OAuthAccessor accessor = OAuthUtilities.createOAuthAccessor(this, oaClient);
            if (oaClient.getSignatureMethod().equals(OAuthConstants.RSA_SHA1)) {
                // then there *has* to be a public key with the client. Use it.
                PublicKey pk = fromX509PEM(oaClient.getSecret());
                accessor.consumer.setProperty(OAuthConstants.PUBLIC_KEY, pk);
                accessor.setProperty(OAuthConstants.PUBLIC_KEY, pk);
            }
            // Now that we have the message and accessor, validate it or refuse to do anything.
            OAuthUtilities.validate(m, accessor);
            AuthorizationGrant ag = tokenForge.getAuthorizationGrant();
            AGResponseImpl agResponse = new AGResponseImpl();
            agResponse.setGrant(ag);
            // convert the list to a map.

            agResponse.setParameters(OAuthUtilities.getParameters(m));
            Map<String, String> params = agResponse.getParameters();
            // now for the non-OAuth specific part of the protocol...
            String certReq = params.get(OAuthConstants.CERT_REQUEST);
            if (isEmpty(certReq)) {
                throw new GeneralException("Error: No cert request");
            }
            String certLifetimeString = params.get(CERT_LIFETIME);
            long certLifetime = 0;
            if (!isEmpty(certLifetimeString)) {
                // set to default
                try {
                    // Again, the lifetime is stored in milliseconds, but the request is in seconds.
                    certLifetime = Long.parseLong(certLifetimeString) * 1000;
                    // Negative value is also rejected. Send along zero ==> take server default.
                    if (certLifetime < 0) {
                        certLifetime = 0L;
                    }
                } catch (NumberFormatException x) {
                    // do nothing, just accept default.
                }
            }
            params.put(CERT_LIFETIME, Long.toString(certLifetime)); // ensures this is valid for later use.

            String callback = params.get(OAuthConstants.OAUTH_CALLBACK);
            if (isEmpty(callback)) {
                throw new GeneralException("Error: No callback specified");
            }
            URI cb = URI.create(callback); // if it is legal, continue, otherwise, this throws a runtime exception

            if (cb.getScheme() == null || !cb.getScheme().equals("https")) {
                throw new UnsupportedProtocolException("Error: protocol in the callback must be https");
            }
            // chop ot the parameters we do not want returned to the user
/*            params.remove(CERT_REQUEST);
            params.remove(CERT_LIFETIME);
            params.remove(OAUTH_CONSUMER_KEY);*/
            return agResponse;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new GeneralException(e);
        }
    }

    private boolean isEmpty(String s) {
        if (s == null || s.isEmpty()) return true;
        return false;
    }
}
