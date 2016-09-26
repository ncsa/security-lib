package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.server.issuers.AbstractIssuer;
import edu.uiuc.ncsa.security.delegation.server.issuers.CBIssuer;
import edu.uiuc.ncsa.security.delegation.server.request.CBRequest;
import edu.uiuc.ncsa.security.delegation.server.request.CBResponse;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.util.ssl.VerifyingHTTPClientFactory;
import net.oauth.OAuth;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

/**
 * Some implementations (e.g. CILogon version 1) do an out of band callback that requires a complete call to
 * a service, rather than a simple redirect (such as is standard in OA4MP). This creates a small http client
 * and issues the callback. It sends along the grant and verifier.
 * <p>Created by Jeff Gaynor<br>
 * on May 23, 2011 at  11:37:23 AM
 */
public class CBIImpl extends AbstractIssuer implements CBIssuer{
    public CBIImpl(TokenForge tokenForge,
                   URI address,
                   VerifyingHTTPClientFactory verifyingHTTPClientFactory) {
        super(tokenForge, address);
        this.verifyingHTTPClientFactory = verifyingHTTPClientFactory;
    }

    VerifyingHTTPClientFactory verifyingHTTPClientFactory;

    public VerifyingHTTPClientFactory getVerifyingHTTPClientFactory() {
        return verifyingHTTPClientFactory;
    }

    @Override
    public CBResponse processCallbackRequest(CBRequest CBRequest) {
        List<OAuth.Parameter> parameters = OAuth.newList(OAuth.OAUTH_TOKEN,
                CBRequest.getAuthorizationGrant().getToken(),
                OAuth.OAUTH_VERIFIER,
                CBRequest.getVerifier().getToken());
        HttpResponse response = null;
        try {
            String callback = OAuth.addParameters(CBRequest.getCallbackUri().toString(), parameters);
            HttpGet httpget = new HttpGet(callback);

            HttpClient httpClient = getVerifyingHTTPClientFactory().getClient(getAddress().toString(),
                    CBRequest.getConnectionTimeout(),
                    CBRequest.getConnectionTimeout());
            response = httpClient.execute(httpget);
        } catch (Exception e) {
            if (e instanceof SSLPeerUnverifiedException) {
                throw new GeneralException("Error verifying host. Is your trusted roots store up to date? Are you using the correct cert chain?", e);
            }
            // Don't know what it is, even if it's a runtime exception, add the token and a callback uri..
            throw new GeneralException("Error processing callback request for token=\"" +
                    CBRequest.getAuthorizationGrant().getToken() + "\" and url =" + CBRequest.getCallbackUri(), e);

        }
        if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
            throw new GeneralException("Error, bad status code of \"" + response.getStatusLine().getStatusCode() +
                    "\" returned for token=\"" +
                    CBRequest.getAuthorizationGrant().getToken() + "\" and url =" + CBRequest.getCallbackUri());
        }
        return new CBResponseImpl();
    }
}
