package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.ServiceTransaction;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.oauth_2_0.JWTUtil;
import edu.uiuc.ncsa.security.delegation.token.impl.RefreshTokenImpl;
import edu.uiuc.ncsa.security.servlet.ServletDebugUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKey;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static edu.uiuc.ncsa.security.oauth_2_0.OA2Constants.*;

/**
 * This is the superclass for responses that must include the ID token.
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/17 at  1:03 PM
 */
public abstract class IDTokenResponse extends IResponse2 {
    public IDTokenResponse(AccessTokenImpl accessToken,
                           RefreshTokenImpl refreshToken,
                           boolean isOIDC) {
        super(isOIDC);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    AccessToken accessToken;
    RefreshTokenImpl refreshToken;
    public boolean hasRefreshToken(){
        return refreshToken != null;
    }

    public RefreshTokenImpl getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshTokenImpl refreshToken) {
        this.refreshToken = refreshToken;
    }

    ServiceTransaction serviceTransaction;

    public ServiceTransaction getServiceTransaction() {
        return serviceTransaction;
    }

    public void setServiceTransaction(ServiceTransaction serviceTransaction) {
        this.serviceTransaction = serviceTransaction;
    }

    public JSONWebKey getJsonWebKey() {
        return jsonWebKey;
    }

    public void setJsonWebKey(JSONWebKey jsonWebKey) {
        this.jsonWebKey = jsonWebKey;
    }

    JSONWebKey jsonWebKey;

    public boolean isSignToken() {
        return signToken;
    }

    public void setSignToken(boolean signToken) {
        this.signToken = signToken;
    }

    boolean signToken = false;

    JSONObject claims;

    public JSONObject getClaims() {
        if (claims == null) {
            claims = new JSONObject();
        }
        return claims;
    }

    public void setClaims(JSONObject claims) {
        this.claims = claims;
    }

    /**
     * The server must decide which scopes to return if any.
     *
     * @return
     */
    public Collection<String> getSupportedScopes() {
        return supportedScopes;
    }

    public void setSupportedScopes(Collection<String> supportedScopes) {
        this.supportedScopes = supportedScopes;
    }

    Collection<String> supportedScopes = new ArrayList<>();

    /**
     * Write JSON response to response's output stream
     *
     * @param response Response to write to
     */
    public void write(HttpServletResponse response) throws IOException {
        // m contains the top-level JSON object that is serialized for the response. The
        // claims are part of this and keyed to the id_token.
        HashMap m = new HashMap();
        m.put(ACCESS_TOKEN, accessToken.getToken());
        m.put(EXPIRES_IN, (accessToken.getLifetime() / 1000));

        m.put(TOKEN_TYPE, "Bearer");
        if (getRefreshToken() != null && getRefreshToken().getToken() != null) {
            m.put(REFRESH_TOKEN, getRefreshToken().getToken());
        }
        if (!getSupportedScopes().isEmpty()) {
            // construct the scope response.
            String ss = "";
            boolean firstPass = true;
            for (String s : getSupportedScopes()) {
                ss = ss + (firstPass ? "" : " ") + s;
                if (firstPass) {
                    firstPass = false;
                }
            }
            m.put(SCOPE, ss);
        }
        if (isOIDC()) {
            JSONObject claims = getClaims();

            try {
                String idTokken = null;
                if (isSignToken()) {
                    idTokken = JWTUtil.createJWT(claims, getJsonWebKey());
                } else {
                    idTokken = JWTUtil.createJWT(claims);
                }
                if (ServletDebugUtil.isEnabled()) {
                    ServletDebugUtil.trace(this, "raw ID_Token=" + idTokken);
                }
                m.put(ID_TOKEN, idTokken);
            } catch (Throwable e) {
                throw new IllegalStateException("Error: cannot create ID token", e);
            }

        }

        JSONObject json = JSONObject.fromObject(m);

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        Writer osw = response.getWriter();
        json.write(osw);
        osw.flush();
        osw.close();
    }


}
