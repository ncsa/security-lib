package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.delegation.server.ServiceTransaction;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;
import edu.uiuc.ncsa.security.oauth_2_0.JWTUtil;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Client;
import edu.uiuc.ncsa.security.oauth_2_0.UserInfo;
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
import static edu.uiuc.ncsa.security.oauth_2_0.server.OA2Claims.*;

/**
 * This is the superclass for responses that must include the ID token.
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/17 at  1:03 PM
 */
public abstract class IDTokenResponse extends IResponse2 {
    public IDTokenResponse(AccessToken accessToken, RefreshToken refreshToken) {
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
    RefreshToken refreshToken;

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }


    ScopeHandlerFactory scopeHandlerFactory;
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

    Collection<? extends ScopeHandler> scopeHandlers;

    public Collection<? extends ScopeHandler> getScopeHandlers() {
        return scopeHandlers;
    }

    public void setScopeHandlers(Collection<? extends ScopeHandler> scopeHandler) {
        this.scopeHandlers = scopeHandler;
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
         Writer osw = response.getWriter();
         HashMap m = new HashMap();
         m.put(ACCESS_TOKEN, accessToken.getToken());
         m.put(TOKEN_TYPE, "Bearer");
         if (getRefreshToken() != null && getRefreshToken().getToken() != null) {
             m.put(REFRESH_TOKEN, getRefreshToken().getToken());
             m.put(EXPIRES_IN, (getRefreshToken().getExpiresIn() / 1000));
         }
         //m.put(SCOPE, "openid"); // all we support in base protocol.
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
         JSONObject claims = new JSONObject();
         // All of these are required.
         claims.put(ISSUER, parameters.get(ISSUER)); // Issuer - url of server
         claims.put(SUBJECT, parameters.get(SUBJECT)); // subject - unique user id, i.e., username
         claims.put(EXPIRATION, System.currentTimeMillis() / 1000 + 15 * 60); // expiration is in SECONDS from the epoch.
         claims.put(AUDIENCE, parameters.get(CLIENT_ID)); // audience = client id.
         claims.put(ISSUED_AT, System.currentTimeMillis() / 1000); // issued at = current time in seconds.
         claims.put(NONCE, parameters.get(NONCE)); // nonce must match that in authz request.

         DebugUtil.dbg(this, "REMOVE email from claims");

         // Optional claims the handler may over-write the default claims as needed.
         if (parameters.containsKey(AUTHORIZATION_TIME)) {
             claims.put(AUTHORIZATION_TIME, parameters.get(AUTHORIZATION_TIME));
         }
         DebugUtil.dbg(this,"\n\n********");
         ServletDebugUtil.dbg(this, "All basic claims:" + claims);

         DebugUtil.dbg(this,"starting to run scope handlers. There are " + (getScopeHandlers()==null?"no":Integer.toString(getScopeHandlers().size())) + " handlers");
         // only run scope handlers if this is not a public client.
         if(!((OA2Client)getServiceTransaction().getClient()).isPublicClient()) {
             if (getScopeHandlers() != null) {
                 UserInfo userInfo = new UserInfo();
                 userInfo.setMap(claims);
                 if (getScopeHandlers() != null) {
                     for (ScopeHandler scopeHandler : getScopeHandlers()) {
                         DebugUtil.dbg(this, "\n*** ");
                         DebugUtil.dbg(this, "   starting to process handler, " + scopeHandler + Integer.toString(userInfo.getMap().size()) + " entries before");
                         scopeHandler.process(userInfo, getServiceTransaction());
                         DebugUtil.dbg(this, "   processed handler, " + Integer.toString(userInfo.getMap().size()) + " entries after");
                     }
                 }
                 claims = userInfo.toJSon();
                 DebugUtil.dbg(this, "final claims= " + claims);

             } else {
                 DebugUtil.dbg(this, "NO scope handler");
             }

         }

             try {
                 String idTokken = null;
                 if (isSignToken()) {
                     idTokken = JWTUtil.createJWT(claims, getJsonWebKey());
                 } else {
                     idTokken = JWTUtil.createJWT(claims);
                 }
                 m.put(ID_TOKEN, idTokken);
             } catch (Throwable e) {
                 throw new IllegalStateException("Error: cannot create token", e);
             }

         JSONObject json = JSONObject.fromObject(m);

         response.setContentType("application/json");
         json.write(osw);
         osw.flush();
         osw.close();
     }

}
