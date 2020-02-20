package edu.uiuc.ncsa.security.oauth_2_0.jwt;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/20 at  5:38 PM
 */
public interface ScriptingConstants {
    /*
      Execution actions for the SRE = script runtime engine.
     */
    String SRE_NO_EXEC_PHASE = "none";
    String SRE_EXEC_INIT = "init";
    String SRE_PRE_AUTH = "pre_auth";
    String SRE_POST_AUTH = "post_auth";
    String SRE_PRE_AT = "pre_token";
    String SRE_POST_AT = "post_token";
    String SRE_PRE_REFRESH = "pre_refresh";
    String SRE_POST_REFRESH = "post_refresh";
    String SRE_REQ_CLAIMS = "claims";
    String SRE_REQ_SCOPES = "scopes";
    String SRE_REQ_FLOW_STATES = "flow_states";
    String SRE_REQ_CLAIM_SOURCES = "claim_sources";
    String[] SRE_PHASES = {
            SRE_EXEC_INIT,
            SRE_PRE_AUTH,
            SRE_POST_AUTH,
            SRE_PRE_AT,
            SRE_POST_AT,
            SRE_PRE_REFRESH,
            SRE_PRE_REFRESH};

}
