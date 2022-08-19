package edu.uiuc.ncsa.sat;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:36 AM
 */
public interface SATConstants {
    // Keys for things in JSON
    public static final String KEYS_SAT = "sat";
    public static final String KEYS_TYPE = "type";
    public static final String KEYS_SUBJECT = "subject";
    public static final String KEYS_ACTION = "action";
    public static final String KEYS_TARGET = "object";
    public static final String KEYS_CONTENT = "content";
    public static final String KEYS_PROMPT = "prompt";
    public static final String KEYS_COMMENT = "comment";
    public static final String KEYS_SUBJECT_ID = "id";
    public static final String KEYS_SUBJECT_SESSION_ID = "session_id";


    // Actions
    public static final String ACTION_LOGON = "logon";
    public static final String ACTION_EXECUTE = "execute";
    public static final String ACTION_LOGOFF = "logoff";

    // Response
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_PROMPT = "prompt";
    public static final String RESPONSE_SESSION_ID = "session_id";
    public static final String RESPONSE_SYMMETRIC_KEY = "s_key";
    public static final String RESPONSE_LOG_OFF_MESSAGE = "message";

    // Response codes
    public static final int RESPONSE_STATUS_OK = 0;

    public static final String CLIENT_ID_HEAD = "sat:client/";

    // Header
    public static final String HEADER_SESSION_ID = "sat-session-id";

}
