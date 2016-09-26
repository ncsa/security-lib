package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.InvalidTokenException;
import edu.uiuc.ncsa.security.core.util.IdentifierProvider;
import edu.uiuc.ncsa.security.delegation.server.MissingTokenException;
import edu.uiuc.ncsa.security.delegation.token.*;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.delegation.token.impl.AuthorizationGrantImpl;
import edu.uiuc.ncsa.security.delegation.token.impl.VerifierImpl;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  4:21 PM
 */
public class OA2TokenForge implements TokenForge {


    public OA2TokenForge(String server) {
        this.server = server;
        setup();
    }

    protected void setup() {
        if (server == null) return;
        // Basically run it through the URI checks so it blows up if an illegal server string is given.
        URI serverUri = URI.create(server);
        atIdProvider = new IdentifierProvider<Identifier>(serverUri, accessToken(), true) {
        };
        agIdProvider = new IdentifierProvider<Identifier>(serverUri, authzGrant(), true) {
        };
        refreshTokenProvider = new IdentifierProvider<Identifier>(serverUri, refreshToken(), true) {
        };
        verifierTokenProvider = new IdentifierProvider<Identifier>(serverUri, verifierToken(), true) {
        };
    }

    /**
     * This and similarly named methods are provided so you can override the specific path components and enforce
     * your own semantics on the tokens. Note that these are called once in {@link #setup()} and are immutable
     * after that. If you need something really exotic you should override the setup() method.
     *
     * @return
     */
    protected String authzGrant(String... x) {
        if (1 == x.length) authzGrant = x[0];
        return authzGrant;
    }

    protected String accessToken(String... x) {
        if (1 == x.length) accessToken = x[0];
        return accessToken;
    }


    protected String refreshToken(String... x) {
        if (1 == x.length) refreshToken = x[0];
        return refreshToken;
    }

    protected String verifierToken(String... x) {
        if (1 == x.length) verifierToken = x[0];
        return verifierToken;
    }

    String server;
    public String authzGrant = "authzGrant";
    public String accessToken = "accessToken";
    public String refreshToken = "refreshToken";
    public String verifierToken = "verifierToken";

    @Override
    public AccessToken getAccessToken(Map<String, String> parameters) {


        String tokenVal = parameters.get(OA2Constants.ACCESS_TOKEN);
        if (tokenVal != null) {
            //return tokenVal;
            return new AccessTokenImpl(URI.create(tokenVal));
        }
        String authCode = parameters.get(OA2Constants.AUTHORIZATION_CODE);
        if (authCode == null) {
            throw new GeneralException("Error: missing authorization code");
        }
        return getAccessToken(authCode);
    }

    @Override
    public AuthorizationGrant getAuthorizationGrant(Map<String, String> parameters) {
        String token = parameters.get(OA2Constants.AUTHORIZATION_CODE);
        if (token == null) {
            throw new MissingTokenException("Error: the authorization grant token is missing.");
        }
        return getAuthorizationGrant(token);
    }

    @Override
    public AuthorizationGrant getAuthorizationGrant(HttpServletRequest request) {
        try {
            return getAuthorizationGrant(OA2Utilities.getParameters(request));
        } catch (Exception e) {
            throw new GeneralException("Error: could not create the authorization grant", e);
        }
    }

    @Override
    public AuthorizationGrant getAuthorizationGrant(String... tokens) {
        switch (tokens.length) {
            case 0:
                return new AuthorizationGrantImpl(agIdProvider.get().getUri());

            default:
                return new AuthorizationGrantImpl(tokens[0] == null ? null : URI.create(tokens[0]));
        }
    }

    @Override
    public AccessToken getAccessToken(HttpServletRequest request) {
        try {
            return getAccessToken(OA2Utilities.getParameters(request));
        } catch (Exception e) {
            throw new GeneralException("Could not create a token", e);
        }
    }

    /*
   Note that our specification dictates that grants, verifiers  and access tokens conform to the
   semantics of identifiers. We have to provide these.
    */
    IdentifierProvider<Identifier> atIdProvider;
    IdentifierProvider<Identifier> agIdProvider;
    IdentifierProvider<Identifier> refreshTokenProvider;
    IdentifierProvider<Identifier> verifierTokenProvider;

    protected URI getURI(String token){
        try{
          return URI.create(token);
        }catch(Throwable t){
            throw new InvalidTokenException("Invalid token \"" + token + "\"", t);
        }
    }
    public RefreshToken getRefreshToken(String... tokens) {
        switch (tokens.length) {
            case 0:
                return new OA2RefreshTokenImpl(refreshTokenProvider.get().getUri());

            default:
                return new OA2RefreshTokenImpl(tokens[0] == null ? null : URI.create(tokens[0]));
        }
    }


    @Override
    public AccessToken getAccessToken(String... tokens) {
        switch (tokens.length) {
            case 0:
                return new AccessTokenImpl(atIdProvider.get().getUri());

            default:
                return new AccessTokenImpl(URI.create(tokens[0]));
        }
    }

    //TODO Resolve conflict between this and legacy classes (e.g. AbstractAuthorizationServlet)
    @Override
    public Verifier getVerifier(Map<String, String> parameters) {
        //throw new UnsupportedOperationException("Error: Verifiers are not used in OAuth2");
        return null;
    }

    @Override
    public Verifier getVerifier(HttpServletRequest request) {
        //throw new UnsupportedOperationException("Error: Verifiers are not used in OAuth2");
        return null;
    }

    @Override
    public Verifier getVerifier(String... tokens) {
        switch (tokens.length) {
            case 0:
                return new VerifierImpl(verifierTokenProvider.get().getUri());

            default:
                return new VerifierImpl(URI.create(tokens[0]));
        }

    }
}
