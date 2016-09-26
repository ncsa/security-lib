package edu.uiuc.ncsa.security.oauth_1_0a;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.services.AddressableServer;
import edu.uiuc.ncsa.security.oauth_1_0a.client.OAClient;
import edu.uiuc.ncsa.security.util.ssl.VerifyingHTTPClientFactory;
import net.oauth.*;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;
import net.oauth.server.OAuthServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static net.oauth.OAuth.OAUTH_SIGNATURE_METHOD;

/**
 * Utilities for OAuth. These are idioms that crop up so frequently it is useful to just centralize them.
 * <p>Created by Jeff Gaynor<br>
 * on May 4, 2010 at  12:27:19 PM
 */
public class OAuthUtilities implements OAuthConstants {

    public static OAuthAccessor createOAuthAccessor(AddressableServer authorizationServer, OAClient client) {
        String resourceUri = authorizationServer.getAddress().toString();
        OAuthServiceProvider provider = new OAuthServiceProvider(
                resourceUri,//requestUrl,
                resourceUri, //authorizationUrl,
                resourceUri //accessTokenUrl
        );
        OAuthConsumer consumer
                = new OAuthConsumer(client.getHomeUri(),
                client.getIdentifierString(),
                client.getSecret(),
                provider);
        consumer.setProperty(OAUTH_SIGNATURE_METHOD, client.getSignatureMethod());
        consumer.setProperty(OAUTH_CALLBACK, client.getHomeUri());
        return new OAuthAccessor(consumer);

    }

    /**
     * Added support for configurable keystores when generating the client. This addresses OAUTH-110.
     * @return
     */
    public static VerifyingHTTPClientFactory getClientFactory() {
        return clientFactory;
    }

    public static void setClientFactory(VerifyingHTTPClientFactory clientFactory) {
        OAuthUtilities.clientFactory = clientFactory;
    }

    static VerifyingHTTPClientFactory clientFactory;

    /**
     * This method is pretty much the chief reason for this class to exist!
     * Use a client pool or you will run out of http connections very fast indeed and requests will hang forever.
     *
     * @return
     */
    public static OAuthHTTPSClientPool newClientPool(URI address) throws IOException {
        return new OAuthHTTPSClientPool(getClientFactory().getClient(address.getHost()));
    }

    public static OAuthClient newOAuthClient(URI address) throws IOException {
        return new OAuthClient(new HttpClient4(newClientPool(address)));
    }


    /**
     * Validate the message.
     *
     * @param message
     * @param accessor
     */
    public static void validate(OAuthMessage message, OAuthAccessor accessor) {
        try {
            SimpleOAuthValidator validator = new SimpleOAuthValidator();
            validator.validateMessage(message, accessor);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new GeneralException("Error validating message", e);
        }
    }

    /**
     * Converts a servlet request to an OAuthMessage. No checking/validation is done, just the straight conversion
     *
     * @param request
     * @return
     * @throws javax.servlet.ServletException
     */
    public static OAuthMessage getMessage(HttpServletRequest request) throws ServletException, IOException {
        return OAuthServlet.getMessage(request, null);
    }

    public static Map<String, String> getParameters(HttpServletRequest request) throws IOException, ServletException {
        return getParameters(getMessage(request));
    }

    public static Map<String, String> getParameters(OAuthMessage message) throws IOException {
        HashMap<String, String> x = new HashMap<String, String>();
        for (Map.Entry p : message.getParameters()) {
            x.put(p.getKey().toString(), p.getValue().toString());
        }
        return x;
    }

    /**
     * Takes and OAuth message and removes any parameters that starts with "oauth_".
     * The OAuth 1.0a spec states that the server must return any unkown parameters to
     * the client. This strips off oauth-specific values so they may be included in the
     * {@link edu.uiuc.ncsa.security.delegation.services.Response} object.
     *
     * @param message
     * @return
     * @throws IOException
     */
    public static HashMap whittleParameters(OAuthMessage message) throws IOException {
        HashMap m = new HashMap();
        for (Map.Entry<String, String> ee : message.getParameters()) {
            if (!ee.getKey().startsWith("oauth_")) {
                m.put(ee.getKey(), ee.getValue());
            }
        }
        return m;
    }

    public static HashMap<String, String> whittleMap(Map<String, String> map) throws IOException {
        HashMap<String, String> m = new HashMap<String, String>();
        for (String k : map.keySet()) {
            if (!k.startsWith("oauth_")) {
                m.put(k, map.get(k));
            }
        }
        return m;
    }

}
