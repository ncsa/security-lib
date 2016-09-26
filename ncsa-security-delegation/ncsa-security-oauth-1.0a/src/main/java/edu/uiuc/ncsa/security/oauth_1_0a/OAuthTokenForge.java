package edu.uiuc.ncsa.security.oauth_1_0a;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.IdentifierProvider;
import edu.uiuc.ncsa.security.delegation.server.MissingTokenException;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.delegation.token.Verifier;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.delegation.token.impl.AuthorizationGrantImpl;
import edu.uiuc.ncsa.security.delegation.token.impl.VerifierImpl;
import net.oauth.OAuth;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

/**
 * A forge for OAuth 1.0a tokens following semantics of the delelgation framework.
 * <p>Created by Jeff Gaynor<br>
 * on 4/10/12 at  11:21 AM
 */
public class OAuthTokenForge implements TokenForge {


    public OAuthTokenForge(String server) {
        this.server = server;
        setup();
    }

    protected void setup() {
        if (server == null) return;
        // Basically run it through the URI checks so it blows up if an illegal server string is given.
        URI serverUri = URI.create(server);
        atIdProvider = new IdentifierProvider<Identifier>(serverUri, accessToken(), true) {};
        atssIdProvider = new IdentifierProvider<Identifier>(serverUri, accessTokenSS(), true) {};
        agIdProvider = new IdentifierProvider<Identifier>(serverUri, tempCred(), true) {};
        agssIdProvider = new IdentifierProvider<Identifier>(serverUri, tempCredSS(), true) {};
        verifierIdProvider = new IdentifierProvider<Identifier>(serverUri, verifier(), true) {};
    }

    /**
     * This and similarly named methods are provided so you can override the specific path components and enforce
     * your own semantics on the tokens. Note that these are called once in {@link #setup()} and are immutable
     * after that. If you need something really exotic you should override the setup() method.
     *
     * @return
     */
    protected String tempCred(String... x) {
        if (1 == x.length)tempCred = x[0];
        return tempCred;
    }

    protected String tempCredSS(String... x) {
        if(1 == x.length) tempCredSS = x[0];
        return tempCredSS;
    }

    protected String accessToken(String... x) {
        if(1 == x.length) accessToken = x[0];
        return accessToken;
    }

    protected String accessTokenSS(String... x) {
        if(1 == x.length) accessTokenSS = x[0];
        return accessTokenSS;
    }

    protected String verifier(String... x) {
        if(1 == x.length) verifier = x[0];
        return verifier;
    }

    String server;
    public String tempCred = "tempCred";
    public String tempCredSS = "tempCred/sharedSecret";
    public String accessToken = "accessToken";
    public String accessTokenSS = "accessToken/sharedSecret";
    public String verifier = "verifier";

    @Override
    public AccessToken getAccessToken(Map<String, String> parameters) {
        String token = parameters.get(OAuth.OAUTH_TOKEN);
        if (token == null) {
            throw new MissingTokenException("Error: the access token is missing.");
        }
        String secret = parameters.get(OAuth.OAUTH_TOKEN_SECRET);
        return getAccessToken(token, secret);
    }

    @Override
    public AuthorizationGrant getAuthorizationGrant(Map<String, String> parameters) {
        String token = parameters.get(OAuth.OAUTH_TOKEN);
        if (token == null) {
            return null;
        }
        String secret = parameters.get(OAuth.OAUTH_TOKEN_SECRET);
        return getAuthorizationGrant(token, secret);
    }

    @Override
    public AuthorizationGrant getAuthorizationGrant(HttpServletRequest request) {
        try {

            return getAuthorizationGrant(OAuthUtilities.getParameters(request));
        } catch (Exception e) {
            throw new GeneralException("Error: could not create the authorization grant", e);
        }
    }

    @Override
    public AuthorizationGrant getAuthorizationGrant(String... tokens) {
        switch (tokens.length) {
            case 0:
                return new AuthorizationGrantImpl(agIdProvider.get().getUri(), agssIdProvider.get().getUri());

            case 1:
                return new AuthorizationGrantImpl(tokens[0] == null ? null : URI.create(tokens[0]));

            default:
                return new AuthorizationGrantImpl(tokens[0] == null ? null : URI.create(tokens[0]),
                        tokens[1] == null ? null : URI.create(tokens[1]));
        }
    }

    @Override
    public AccessToken getAccessToken(HttpServletRequest request) {
        try {
            return getAccessToken(OAuthUtilities.getParameters(request));
        } catch (Exception e) {
            throw new GeneralException("Could not create a token", e);
        }
    }

    /*
   Note that our specification dictates that grants, verifiers  and access tokens conform to the
   semantics of identifiers. We have to provide these.
    */
    IdentifierProvider<Identifier> atIdProvider;
    IdentifierProvider<Identifier> atssIdProvider;
    IdentifierProvider<Identifier> agIdProvider;
    IdentifierProvider<Identifier> agssIdProvider;
    IdentifierProvider<Identifier> verifierIdProvider;

    @Override
    public AccessToken getAccessToken(String... tokens) {
        switch (tokens.length) {
            case 0:
                return new AccessTokenImpl(atIdProvider.get().getUri(), atssIdProvider.get().getUri());

            case 1:
                return new AccessTokenImpl(tokens[0] == null ? null : URI.create(tokens[0]));

            default:
                return new AccessTokenImpl(tokens[0] == null ? null : URI.create(tokens[0]),
                        tokens[1] == null ? null : URI.create(tokens[1]));

        }
    }

    @Override
    public Verifier getVerifier(Map<String, String> parameters) {
        String verifier = parameters.get(OAuth.OAUTH_VERIFIER);
        if (verifier == null) {
            throw new MissingTokenException("Error: missing verifier");
        }
        return getVerifier(verifier);
    }

    @Override
    public Verifier getVerifier(HttpServletRequest request) {
        try {
            return getVerifier(OAuthUtilities.getParameters(request));
        } catch (Exception e) {
            throw new GeneralException("Error: Could not create verifier", e);
        }

    }

    @Override
    public Verifier getVerifier(String... tokens) {
        switch (tokens.length) {
            case 0:
                return new VerifierImpl(verifierIdProvider.get().getUri());

            case 1:
                return new VerifierImpl(tokens[0] == null ? null : URI.create(tokens[0]));

            default:
                throw new IllegalArgumentException("Error: verifiers do not require multiple arguments");

        }

    }
}
