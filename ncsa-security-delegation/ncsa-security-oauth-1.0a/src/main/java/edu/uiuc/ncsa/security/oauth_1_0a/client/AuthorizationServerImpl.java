package edu.uiuc.ncsa.security.oauth_1_0a.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.ServerRedirectException;
import edu.uiuc.ncsa.security.core.exceptions.ServerSideException;
import edu.uiuc.ncsa.security.core.exceptions.UnknownClientException;
import edu.uiuc.ncsa.security.core.util.MapUtilities;
import edu.uiuc.ncsa.security.delegation.client.request.AGRequest;
import edu.uiuc.ncsa.security.delegation.client.request.AGResponse;
import edu.uiuc.ncsa.security.delegation.client.request.ATRequest;
import edu.uiuc.ncsa.security.delegation.client.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.client.server.AGServer;
import edu.uiuc.ncsa.security.delegation.client.server.ATServer;
import edu.uiuc.ncsa.security.delegation.server.UnapprovedClientException;
import edu.uiuc.ncsa.security.delegation.services.AddressableServer;
import edu.uiuc.ncsa.security.delegation.services.Request;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.delegation.token.impl.AuthorizationGrantImpl;
import edu.uiuc.ncsa.security.delegation.token.impl.VerifierImpl;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.client.OAuthClient;
import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities.whittleParameters;
import static net.oauth.OAuth.*;

/**
 * In our control flow, the grant issuer and the authorization server are the same, which is why
 * this class processes grant requests as well as access token requests.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  2:46:28 PM
 */
public class AuthorizationServerImpl implements AddressableServer, AGServer, ATServer {
    @Override
    public URI getAddress() {
        return address;
    }

    public static final String DEBUG_STACKTRACE_KEY = "stacktrace";
    URI address;

    public AuthorizationServerImpl(URI address) {
        this.address = address;
    }


    public Response process(Request request) {
        return request.process(this);
    }

    @Override
    public AGResponse processAGRequest(AGRequest agRequest) {
        return getAuthorizationGrant(agRequest);
    }

    @Override
    public ATResponse processATRequest(ATRequest atRequest) {
        return getAccessToken(atRequest);
    }

    protected ATResponse getAccessToken(ATRequest atRequest) {
        AccessTokenImpl accessToken = null;
        AuthorizationGrantImpl ag = null;
        VerifierImpl vImpl = null;

        OAClient oaClient = (OAClient) atRequest.getClient();
        OAuthAccessor accessor = OAuthUtilities.createOAuthAccessor(this, oaClient);

        if (atRequest.getAuthorizationGrant() instanceof AuthorizationGrantImpl) {
            ag = (AuthorizationGrantImpl) atRequest.getAuthorizationGrant();
        } else {
            // This should never happen, but is here since *someday* the codebase may change and this will intercept
            // such an error.
            throw new GeneralException("Internal Error: Incorrect authorization grant found. Should have been a TempCred but was a " + atRequest.getAuthorizationGrant().getClass());
        }

        if (atRequest.getVerifier() == null) {
            throw new GeneralException("Error: No verifier found. This is required by the OAuth spec.");
        }
        if (atRequest.getVerifier() instanceof VerifierImpl) {
            vImpl = (VerifierImpl) atRequest.getVerifier();
        } else {
            throw new GeneralException("Internal Error: Incorrect verifier instance found. Should have been a VerifierImpl but was a " + atRequest.getVerifier());

        }
        // Note that the OAuth 1.0a spec needs the verifier (set as an extra parameter)
        // and Authz grant shared secret (set in the accessor as per below), not the accessor.requestToken
        // which would send along a copy of the authz grant. The OAuth library should not be so simple-minded,
        //
        if (ag.getSharedSecret() != null) {
            accessor.tokenSecret = ag.getSharedSecret().toString();
        }

        if (oaClient.getSignatureMethod().equals(RSA_SHA1)) {
            accessor.setProperty(OAuthConstants.PRIVATE_KEY, oaClient.getSecret());
            accessor.consumer.setProperty(OAuthConstants.PRIVATE_KEY, oaClient.getSecret());
        }

        try {
            net.oauth.client.OAuthClient oauthClient = OAuthUtilities.newOAuthClient(getAddress());
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(OAuth.OAUTH_VERIFIER);
            arrayList.add(vImpl.getURIToken().toString());
            for (String key : atRequest.getParameters().keySet()) {
                arrayList.add(key);
                arrayList.add(atRequest.getParameters().get(key));
            }
            OAuthMessage message = oauthClient.getAccessToken(accessor, "GET", OAuth.newList(arrayList.toArray(new String[arrayList.size()])));
            HashMap m = whittleParameters(message);
            accessToken = new AccessTokenImpl(URI.create(message.getParameter(OAUTH_TOKEN)), URI.create(message.getParameter(OAUTH_TOKEN_SECRET)));
            ATResponse atr = new ATResponse(accessToken);
            atr.setParameters(m);
            return atr;
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }


    protected AGResponse getAuthorizationGrant(AGRequest agRequest) {
        List<Map.Entry<String, String>> params = MapUtilities.toList(agRequest.getParameters());
        OAClient oac = (OAClient) agRequest.getClient();
        OAuthAccessor accessor = OAuthUtilities.createOAuthAccessor(this, oac);
        if (oac.getSignatureMethod().equals(RSA_SHA1)) {
            accessor.consumer.setProperty(OAuthConstants.PRIVATE_KEY, oac.getSecret());
            accessor.setProperty(OAuthConstants.PRIVATE_KEY, oac.getSecret());
        }

        try {
            OAuthClient client = OAuthUtilities.newOAuthClient(getAddress());
            OAuthMessage message = client.getRequestTokenResponse(accessor, "GET", params);
            String rt = message.getParameter(OAUTH_TOKEN);
            if (rt == null || rt.length() == 0) {
                throw new IllegalArgumentException("Error: delegation server did not return a request token");
            }
            String rtss = message.getParameter(OAUTH_TOKEN_SECRET);
            if (!((OAClient) agRequest.getClient()).getSignatureMethod().equals(RSA_SHA1) && (rtss == null || rtss.length() == 0)) {
                throw new IllegalArgumentException("Error: delegation server did not return a shared secret");
            }
            AuthorizationGrantImpl agi = new AuthorizationGrantImpl(URI.create(rt), URI.create(rtss));
            AGResponse agr = new AGResponse(agi);
            HashMap m = whittleParameters(message);
            // grab any unused parameters from the server, as per OAuth spec.
            agr.setParameters(m);
            return agr;
        } catch (Throwable e) {
            handleException(e);
            return null;
        }
    }

    protected void handleException(Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }

        if (t instanceof OAuthProblemException) {
            // If debugging is enabled, any server-side exceptions will be
            // returned in a B64 encoding. OAuth does support this as part of
            // its mechanism for errors, but getting the information from the
            // generic OAuthProblemException is messy (done here) since it
            // gets buried way down in an un-intuitive place.
            OAuthProblemException opx = (OAuthProblemException) t;
            // take the exception. If there is a Location parameter, i.e., redirect,
            // parse that into key value pairs. Return a server side exception
            // Otherwise, just have generic error handling.
            int status = opx.getHttpStatusCode();
            // OAuth library might get a redirect wrong and assume all is ok.


            // Actual HTTP status code for a redirect, in case it comes back or check for status code of 200 if OAuth library gets it wrong..
            if(status == 200 && opx.getParameters().containsKey("oauth_parameters_absent") || (status == 302)){
                throw new GeneralException("Server attempted to redirect to another page. This is not permitted. Please contact the site administrator");
            }

            if(status == 404){
                throw new GeneralException("Page not found");
            }

            if (500 <= status ) {
                // &*&*%&*%@#!! OAuth libraries.
                String message = "Server Error -- unknown cause"; //default
                for(String key: opx.getParameters().keySet()){
                     if(key.toLowerCase().startsWith("<html>")){
                         message = opx.getParameters().get(key).toString();
                         if(message.contains(UnknownClientException.class.getCanonicalName())){
                             throw new UnknownClientException("Unknown client. Be sure to register your client.  Is your client id correct?");
                         }
                         if(message.contains(UnapprovedClientException.class.getCanonicalName())){
                             throw new UnapprovedClientException("Your client has been registered, but the administrator has not approved it yet", null);
                         }
                         break;
                     }
                }
                throw new GeneralException(message);
            }
            // A specific parameter in the OAuth exception that tells of a redirect to an
            // error location on the server. This is optional.
            if (opx.getParameters().get("Location") != null) {
                ServerSideException sse = new ServerSideException(t);

                // ***IF*** the server supplies a diagnostic redirect, parse it
                // and set it as the redirect for the server side exception to read off
                // and handle.
                // OA4MP servers might also return a stack trace, which is a b64 encoded
                // stack trace from the server. The client must request this with
                // a parameter in the original call.
                String redirect = opx.getParameters().get("Location").toString();
                URI r = URI.create(redirect);
                String query = r.getQuery();
                // have to parse this since Java doesn't have a built in parser
                HashMap<String, String> qparams = new HashMap<String, String>();
                String[] arrParameters = query.split("&");
                for (String tempParameterString : arrParameters) {
                    String[] arrTempParameter = tempParameterString.split("=");
                    if (arrTempParameter.length >= 2) {
                        String parameterKey = arrTempParameter[0];
                        String parameterValue = arrTempParameter[1];
                        if (parameterKey.toLowerCase().equals(DEBUG_STACKTRACE_KEY)) {
                            try {
                                parameterValue = new String(Base64.decodeBase64(parameterValue));
                            } catch (Throwable x) {
                                // System.out.println("Could not decode stack trace for cause " + x.getClass().getName() + " msg=\"" + x.getMessage() + "\", trace:" + parameterValue);
                                parameterValue = "(none)";
                            }
                        }
                        qparams.put(parameterKey, parameterValue);
                    }
                }
                sse.setQueryParameters(qparams);
                sse.setRedirect(r);
                throw sse;
            }
            // So we don't have a heavyweight server-side message waiting for us.
            Map<String, Object> parameters = opx.getParameters();

            if (opx.getHttpStatusCode() == 200 && parameters.containsKey("oauth_parameters_absent")) {
                ServerRedirectException sre = new ServerRedirectException("Server exception with redirect. The server is trying to redirect to an error page, but the OAuth 1.0a libraries cannot process this and fail.");

                // we have that the server is trying to redirect us to its error page rather than
                // simply returning an exception.
                for (String v : parameters.keySet()) {
                    // If a redirect page is returned, the OAuth library assumes it is a key with
                    // a random subset of it assigned to being the value. Glom them together.
                    if (v.toLowerCase().startsWith("<html>")) {
                        String webpage = v;
                        Object value = parameters.get(v);
                        if (value != null) {
                            webpage = webpage + value.toString();
                        }
                        sre.setWebpage(webpage);
                        throw sre;
                    }
                }
                throw sre;
            }
        }
        // so this is not an OAuth-intercepted exception. Something else happened.

/*
        if (t.getCause() != null) {
            t.getCause().printStackTrace();
        } else {
            t.printStackTrace();
        }
*/
                /*
                Special case this exception since a bad SSL configuration on the client side will
                most likely fail here first. This way they don't lose the exception and probable
                cause buried in a stack trace.
                 */
        if (t instanceof SSLPeerUnverifiedException) {
            throw new GeneralException("Error: could not connect to the server. Is your trusted roots store up to date?", t);
        }
        throw new GeneralException("Error invoking OAuth client", t);
    }

    @Override
    public String toString() {
        return "AuthorizationServerImpl[address=" + getAddress() + "]";
    }
}
