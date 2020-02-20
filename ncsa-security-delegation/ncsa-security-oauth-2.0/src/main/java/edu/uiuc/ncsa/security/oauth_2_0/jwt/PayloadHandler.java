package edu.uiuc.ncsa.security.oauth_2_0.jwt;

import edu.uiuc.ncsa.security.oauth_2_0.server.claims.ClaimSource;
import edu.uiuc.ncsa.security.util.scripting.ScriptRunRequest;
import edu.uiuc.ncsa.security.util.scripting.ScriptRunResponse;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * This class is charged with creating and managing the payload of a single type of JWT. As
 * we get more types of these (OIDC, SciToken, etc.) each of these has completely
 * separate requirements for creating, management and such. All of that should be encapsulated
 * into a class.
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/20 at  7:13 AM
 */
public interface PayloadHandler {
    /**
     * Creates and initializes the claims object this class manages.
     */
    void init() throws Throwable;

    /**
     * If the claims need to be updated (e.g. for a refresh and the timestamps need
     * adjusting) this method needs to be called
     */
    void refresh()  throws Throwable;

    /**
     * Marshall any resources this script needs to make a request.
     * @return
     */
    void addRequestState(ScriptRunRequest req)  throws Throwable;

    /**
     * This takes the response from a script and unmarshalls the resources
     * @param resp
     */
    void handleResponse(ScriptRunResponse resp)  throws Throwable;

    /**
     * Called after the runner has gotten the claims so that this class can check integrity.
     * For instance, an OIDC server would need to see that the subject is set properly.
     * SciTokens needs to check that its scopes (aka resource permissions) were set
     */
    void checkClaims()  throws Throwable;

    /**
     * These are the sources that the runner will use to populate the claims
     * @return
     */
    List<ClaimSource> getSources()  throws Throwable;

    /**
     * Runs this specific claim source against the internal state of this class.
     * @param claims
     * @return
     */
    JSONObject execute(ClaimSource source, JSONObject claims)  throws Throwable;
    /**
     * Called at the very end of all processing, this lets the handler, clean up or whatever it needs to do.
     * It is called before {@link #saveState()}.
     */
    void finish()  throws Throwable;
    /**
     * Called at the end of each block, this lets the handler save its state. E.g. if you are storing state
     * in a database, this is where you make that call.
     */

    void saveState()  throws Throwable;
    /**
     * Get the claims (the actual payload).
     * @return
     */
    JSONObject getClaims()  throws Throwable;

    /**
     * This sets the accounting information (such as the expiration and such) for a token.
     * This is called when a token is created or refreshed. 
     */
    void setAccountingInformation();
    
}