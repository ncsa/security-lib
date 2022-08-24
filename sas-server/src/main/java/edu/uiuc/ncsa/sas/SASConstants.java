package edu.uiuc.ncsa.sas;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:36 AM
 */
public interface SASConstants {
    // Keys for things in JSON
    public static final String KEYS_SAT = "sat";
    public static final String KEYS_TYPE = "type";
    public static final String KEYS_SUBJECT = "subject";
    public static final String KEYS_STATE = "state";
    public static final String KEYS_INTERNAL_ID = "id";
    public static final String KEYS_ACTION = "action";
    public static final String KEYS_TARGET = "object";
    public static final String KEYS_ARGUMENT = "arg";
    public static final String KEYS_METHOD = "method";
    public static final String KEYS_PROMPT = "prompt";
    public static final String KEYS_COMMENT = "comment";
    public static final String KEYS_SUBJECT_ID = "id";
    public static final String KEYS_SUBJECT_SESSION_ID = "session_id";


    // Actions
    public static final String ACTION_LOGON = "logon";
    public static final String ACTION_EXECUTE = "execute";
    public static final String ACTION_LOGOFF = "logoff";
    public static final String ACTION_INVOKE = "invoke";
    public static final String ACTION_NEW_KEY = "new_key";

    // Response
    public static final String RESPONSE_STATUS = "status";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_PROMPT = "prompt";
    public static final String RESPONSE_SESSION_ID = "session_id";
    public static final String RESPONSE_SYMMETRIC_KEY = "s_key";
    public static final String RESPONSE_MESSAGE = "message";

    // Response codes
    public static final int RESPONSE_STATUS_OK = 0;
    public static final int RESPONSE_STATUS_ERROR = 1;

    public static final String CLIENT_ID_HEAD = "sas:client/";

    // Header
    public static final String HEADER_SESSION_ID = "sas-session-id";

}
