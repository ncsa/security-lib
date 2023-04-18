package edu.uiuc.ncsa.sas;

/**
 * Constants for the Subject-Action Service.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:36 AM
 */
public interface SASConstants {
    // Keys for things in JSON
    public static final String KEYS_SAS = "sas";
    public static final String KEYS_TYPE = "type";
    public static final String KEYS_SUBJECT = "subject";
    public static final String KEYS_STATE = "state";
    public static final String KEYS_INTERNAL_ID = "id";
    public static final String KEYS_ACTION = "action";
    public static final String REQUEST_TYPE = KEYS_ACTION;
    public static final String RESPONSE_TYPE = "response_type";
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
    public static final String RESPONSE_SESSION_ID = "sas_session_id";  // See note below on HEADER_SESSION_ID
    public static final String RESPONSE_SYMMETRIC_KEY = "s_key";
    public static final String RESPONSE_MESSAGE = "message";
    public static final String RESPONSE_TYPE_ERROR = "error";
    public static final String RESPONSE_TYPE_OUTPUT = "output";
    public static final String RESPONSE_TYPE_NEW_KEY = "new_key";
    public static final String RESPONSE_TYPE_LOGON = "logon";
    public static final String RESPONSE_TYPE_LOGOFF = "logoff";
    public static final String RESPONSE_TYPE_PROMPT = "prompt";

    // Response codes
    public static final int RESPONSE_STATUS_OK = 0;
    public static final int RESPONSE_STATUS_ERROR = 1;

    /**
     * For the header in the HTTP request. Used only in logon
     */
    public static final String CLIENT_ID_HEAD = "sas:client/";

    // Header seesion ID. Used in every other request after logon.
    // NOTE that Session-id is a reserved header for HTTP, so ours has to
    // be named something else.
    public static final String HEADER_SESSION_ID = "sas-session-id";

}
