package edu.uiuc.ncsa.security.util.scripting;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/31/22 at  8:34 AM
 */
public interface ScriptingConstants {
    /*
         Execution actions for the SRE = script runtime engine.
        */
    String SRE_NO_EXEC_PHASE = "none";
    String SRE_EXEC_PHASE = "exec_phase";
    String SRE_EXEC_INIT = "init";
    String PRE_PREFIX = "pre_";
    String POST_PREFIX = "post_";
    String AUTH_PHASE = "auth";
    String TOKEN_PHASE = "token";
    String REFRESH_PHASE = "refresh";
    String EXCHANGE_PHASE = "exchange";
    String USER_INFO_PHASE = "user_info";
    String ALL_PHASES = "all";
    String SRE_PRE_ALL = PRE_PREFIX + ALL_PHASES;
    String SRE_POST_ALL = POST_PREFIX + ALL_PHASES;

    String SRE_PRE_AUTH = PRE_PREFIX + AUTH_PHASE;
    String SRE_POST_AUTH = POST_PREFIX + AUTH_PHASE;
    String SRE_PRE_AT = PRE_PREFIX + TOKEN_PHASE ;
    String SRE_POST_AT = POST_PREFIX + TOKEN_PHASE ;
    String SRE_PRE_REFRESH = PRE_PREFIX + REFRESH_PHASE;
    String SRE_POST_REFRESH = POST_PREFIX + REFRESH_PHASE;
    String SRE_PRE_EXCHANGE = PRE_PREFIX + EXCHANGE_PHASE;
    String SRE_POST_EXCHANGE = POST_PREFIX + EXCHANGE_PHASE;
    String SRE_PRE_USER_INFO = PRE_PREFIX + USER_INFO_PHASE;
    String SRE_POST_USER_INFO = POST_PREFIX + USER_INFO_PHASE;
    String SRE_REQ_CLAIMS = "claims";
    String SRE_REQ_PROXY_CLAIMS = "proxy_claims";
    String SRE_REQ_ACCESS_TOKEN = "access_token";
    String SRE_REQ_REFRESH_TOKEN = "refresh_token";
    String SRE_REQ_SCOPES = "scopes";
    String SRE_TX_REQ_SCOPES = "tx_scopes";
    String SRE_REQ_AUDIENCE = "audience";
    String SRE_REQ_RESOURCE = "resource";
    String SRE_TX_REQ_RESOURCES = "tx_resources";
    String SRE_TX_REQ_AUDIENCE = "tx_audience";
    String SRE_REQ_FLOW_STATES = "flow_states";
    String SRE_REQ_CLAIM_SOURCES = "claim_sources";
    String SRE_REQ_EXTENDED_ATTRIBUTES = "eas";
    String[] SRE_PHASES = {
            SRE_EXEC_INIT,
            SRE_PRE_AUTH,
            SRE_POST_AUTH,
            SRE_PRE_AT,
            SRE_POST_AT,
            SRE_PRE_REFRESH,
            SRE_PRE_REFRESH,
            SRE_PRE_EXCHANGE,
            SRE_POST_EXCHANGE,
            SRE_PRE_USER_INFO,
            SRE_POST_USER_INFO};
}
