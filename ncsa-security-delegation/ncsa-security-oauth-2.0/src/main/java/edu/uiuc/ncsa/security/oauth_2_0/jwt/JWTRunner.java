package edu.uiuc.ncsa.security.oauth_2_0.jwt;

import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Errors;
import edu.uiuc.ncsa.security.oauth_2_0.OA2GeneralError;
import edu.uiuc.ncsa.security.oauth_2_0.server.OIDCServiceTransactionInterface;
import edu.uiuc.ncsa.security.oauth_2_0.server.claims.ClaimSource;
import edu.uiuc.ncsa.security.util.scripting.ScriptRunRequest;
import edu.uiuc.ncsa.security.util.scripting.ScriptRunResponse;
import edu.uiuc.ncsa.security.util.scripting.ScriptRuntimeEngine;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.util.DebugUtil.trace;
import static edu.uiuc.ncsa.security.oauth_2_0.jwt.ScriptingConstants.*;

/**
 * This will create a JWT. The contract is generally that it has (multiple) {@link PayloadHandler}s
 * which process a given token. These are run at various times during execution based on the phase
 * and flow states. For various historical reasons, the JWT is referred to as "claims" and it would
 * take far too much tracking down in the code to change it.
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/20 at  7:38 AM
 */
public class JWTRunner {
    OIDCServiceTransactionInterface transaction;

    public AccessTokenHandlerInterface getAccessTokenHandler() {
        return accessTokenHandler;
    }

    public void setAccessTokenHandler(AccessTokenHandlerInterface accessTokenHandler) {
        this.accessTokenHandler = accessTokenHandler;
        addHandler(accessTokenHandler);
    }

    AccessTokenHandlerInterface accessTokenHandler = null;


    public boolean hasATHandler() {
        return accessTokenHandler != null;
    }

    RefreshTokenHandlerInterface refreshTokenHandler = null;

    public void setRefreshTokenHandler(RefreshTokenHandlerInterface refreshTokenHandler) {
        this.refreshTokenHandler = refreshTokenHandler;
        addHandler(refreshTokenHandler);
    }

    public RefreshTokenHandlerInterface getRefreshTokenHandler() {
        return refreshTokenHandler;
    }

    public boolean hasRTHandler() {
        return refreshTokenHandler != null;
    }


    public JWTRunner(OIDCServiceTransactionInterface transaction, ScriptRuntimeEngine scriptRuntimeEngine) {
        this.transaction = transaction;
        this.scriptRuntimeEngine = scriptRuntimeEngine;
    }

    List<PayloadHandler> handlers = new ArrayList<>();

    public void addHandler(PayloadHandler handler) {
        handlers.add(handler);
    }

    public void doAuthClaims() throws Throwable {
        DebugUtil.trace(this, "Starting Auth claims");
        transaction.setFlowStates(new FlowStates());
        for (PayloadHandler h : handlers) {
            DebugUtil.trace(this, "Running init for handler " + h);
            h.init();
            h.setAccountingInformation();
        }
        /*
        In point of fact init and pre-auth are redundant, however, some older
        scripting frameworks (well, functors) allowed for both since they
        did not have actual state they could manage. Since there are a lot of
        older configurations we still must support, we run both of these back to back.
        Any new scripting support should only support pre/post auth and pre/post token.
         */
        doScript(SRE_EXEC_INIT);
        doScript(ScriptingConstants.SRE_PRE_AUTH);

        // now for the actual getting of the claims

        getSources(transaction.getFlowStates(), true);

        doScript(ScriptingConstants.SRE_POST_AUTH);

        for (PayloadHandler h : handlers) {
            h.saveState();
        }
    }

    public void doRefreshClaims() throws Throwable {
        doTokenClaims(true);
    }

    public void doTokenExchange() throws Throwable {
        for (PayloadHandler h : handlers) {
             h.setAccountingInformation();
         }
        doScript(SRE_PRE_EXCHANGE);

        doScript(SRE_POST_EXCHANGE);
        for (PayloadHandler h : handlers) {
            h.checkClaims();
        }

        for (PayloadHandler h : handlers) {
            h.saveState();
        }

        for (PayloadHandler h : handlers) {
            h.finish();
        }
    }

    public void doTokenClaims() throws Throwable {
        doTokenClaims(false);
    }

    protected void doTokenClaims(boolean isRefresh) throws Throwable {
        doScript(isRefresh ? SRE_PRE_REFRESH : SRE_PRE_AT);

        getSources(transaction.getFlowStates(), false);
        if (isRefresh) {
            for (PayloadHandler h : handlers) {
                h.setAccountingInformation();
            }
        }
        doScript(isRefresh ? SRE_POST_REFRESH : SRE_POST_AT);

        for (PayloadHandler h : handlers) {
            h.checkClaims();
        }

        for (PayloadHandler h : handlers) {
            h.saveState();
        }

        for (PayloadHandler h : handlers) {
            h.finish();
        }
    }

    /**
     * Get the claims sources for the ID token. This is needed only if the handler will attempt to get
     * claims at some point.
     * @param flowStates
     * @param checkAuthClaims
     * @throws Throwable
     */
    protected void getSources(FlowStates flowStates, boolean checkAuthClaims) throws Throwable {
        for (PayloadHandler h : handlers) {
            if (!h.getSources().isEmpty()) {
                JSONObject claims = h.getClaims();
                // so there is
                for (int i = 0; i < h.getSources().size(); i++) {
                    ClaimSource claimSource = h.getSources().get(i);
                    boolean isRunAtAuthz;
                    if (checkAuthClaims) {
                        isRunAtAuthz = claimSource.isRunAtAuthorization();
                    } else {
                        isRunAtAuthz = !claimSource.isRunAtAuthorization();
                    }
                    if (isRunAtAuthz)
                        claims = h.execute(claimSource, claims);
                    //claimSource.process(claims, request, transaction);

                    // keep this in case this was set earlier.
                    if (!flowStates.acceptRequests) {
                        // This practically means that the come situation has arisen whereby the user is
                        // immediately banned from access -- e.g. they were found to be on a blacklist.
                        h.finish();
                        throw new OA2GeneralError(OA2Errors.ACCESS_DENIED, "access denied", HttpStatus.SC_UNAUTHORIZED);
                    }
                    trace(this, "user info for claim source #" + claimSource + " = " + claims.toString(1));
                }
            }
        }
    }

    public ScriptRuntimeEngine getScriptRuntimeEngine() {
        if (scriptRuntimeEngine == null) {
            //scriptRuntimeEngine = ScriptRuntimeEngineFactory.createRTE(getOA2Client().getConfig());
        }
        return scriptRuntimeEngine;
    }


    ScriptRuntimeEngine scriptRuntimeEngine = null;

    /**
     * creates new {@link ScriptRunRequest} with the basic information. This sends along the current claims, scopes
     * flow states and claim sources then harvests them <i>in toto</i> from the response.
     *
     * @return
     */
    protected ScriptRunRequest newSRR(OIDCServiceTransactionInterface transaction, String phase) {
        ScriptRunRequest initReq = new ScriptRunRequest() {
            HashMap<String, Object> map = new HashMap<>();

            @Override
            public Map<String, Object> getArgs() {
                map.put(SRE_REQ_SCOPES, transaction.getScopes());
                map.put(SRE_REQ_AUDIENCE, transaction.getAudience());
                map.put(SRE_REQ_EXTENDED_ATTRIBUTES, transaction.getExtendedAttributes());
                map.put(SRE_REQ_FLOW_STATES, transaction.getFlowStates()); // so its a map
                return map;
            }

            String p = phase;

            @Override
            public String getAction() {
                return p;
            }

            @Override
            public boolean returnArgs() {
                return true;
            }

            @Override
            public String getResponseArgName() {
                return "";
            }

            @Override
            public boolean hasReturnedValue() {
                return false;
            }
        };
        return initReq;
    }

    /**
     * Process the script, but the claim sources are not updated because
     * we are not interested in the claim sources, e.g. if this is
     * called after all claims sources have been processed  and the script just massages the claims or flow states.
     *
     * @param scriptRunResponse
     * @return
     */

    protected void handleSREResponse(OIDCServiceTransactionInterface transaction, ScriptRunResponse scriptRunResponse) throws IOException {
        switch (scriptRunResponse.getReturnCode()) {
            case ScriptRunResponse.RC_OK:
                // Note that the returned values from a script are very unlikely to be the same object we sent
                // even if the contents are the same, since scripts may have to change these in to other data structures
                // to make them accessible to their machinery, then convert them back.
                transaction.setFlowStates((FlowStates) scriptRunResponse.getReturnedValues().get(SRE_REQ_FLOW_STATES));
            case ScriptRunResponse.RC_NOT_RUN:
                return;

        }

        throw new NotImplementedException("Error: other script runtime reponses not implemented yet.");
    }

    protected void doScript(String phase) throws Throwable {
        //oldDoScript(phase);
        newDoScript(phase);
    }

    protected void newDoScript(String phase) throws Throwable {
        if (getScriptRuntimeEngine() == null) {
            return;
        }
        ScriptRunRequest req = newSRR(transaction, phase);
        if (handlers.isEmpty()) {
            // Functors do not have handlers, it all comes through the script engine.
            // Therefore, if this does not have handlers, try to run it as legacy code

            ScriptRunResponse resp = getScriptRuntimeEngine().run(req);
            handleSREResponse(transaction, resp);

        } else {
            // This has handlers so it new and should be run as such.
            for (PayloadHandler h : handlers) {
                h.addRequestState(req);
                getScriptRuntimeEngine().clearScriptSet();
                getScriptRuntimeEngine().setScriptSet(h.getPhCfg().getScriptSet());
                ScriptRunResponse resp = getScriptRuntimeEngine().run(req);
                handleSREResponse(transaction, resp);
                h.handleResponse(resp);
            }
        }

    }

    // This next method did not clear the script set and reset it. It is kept for reference in case some
    // legacy (e.g. functor) handlers do not react well to this
    private void oldDoScript(String phase) throws Throwable {
        if (getScriptRuntimeEngine() != null) {
            ScriptRunRequest req = newSRR(transaction, phase);
            for (PayloadHandler h : handlers) {
                h.addRequestState(req);

            }

            ScriptRunResponse resp = getScriptRuntimeEngine().run(req);
            handleSREResponse(transaction, resp);
            for (PayloadHandler h : handlers) {
                h.handleResponse(resp);
            }
        }
    }

}
