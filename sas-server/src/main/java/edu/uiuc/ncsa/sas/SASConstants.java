package edu.uiuc.ncsa.sas;

/**
 * Constants for the Subject-Action Service.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:36 AM
 */
public interface SASConstants {
    // Keys for things in JSON
    String KEYS_SAS = "sas";
    String KEYS_TYPE = "type";
    String KEYS_SUBJECT = "subject";
    String KEYS_STATE = "state";
    String KEYS_INTERNAL_ID = "id";
    String KEYS_ACTION = "action";
    String REQUEST_TYPE = KEYS_ACTION;
    String RESPONSE_TYPE = "response_type";
    String KEYS_ARGUMENT = "arg";
    String KEYS_METHOD = "method";
    String KEYS_PROMPT = "prompt";
    String KEYS_COMMENT = "comment";
    String KEYS_EXECUTABLE_NAME = "executable_name";
    String KEYS_SUBJECT_ID = "id";
    String KEYS_SUBJECT_SESSION_ID = "session_id";


    // Actions
    String ACTION_LOGON = "logon";
    String ACTION_EXECUTE = "execute";
    String ACTION_LOGOFF = "logoff";
    String ACTION_INVOKE = "invoke";
    String ACTION_NEW_KEY = "new_key";

    // Response
    String RESPONSE_STATUS = "status";
    String RESPONSE_CONTENT = "content";
    String RESPONSE_PROMPT = "prompt";
    String RESPONSE_SESSION_ID = "sas_session_id";  // See note below on HEADER_SESSION_ID
    String RESPONSE_SYMMETRIC_KEY = "s_key";
    String RESPONSE_MESSAGE = "message";
    String RESPONSE_TYPE_ERROR = "error";
    String RESPONSE_TYPE_OUTPUT = "output";
    String RESPONSE_TYPE_NEW_KEY = "new_key";
    String RESPONSE_TYPE_LOGON = "logon";
    String RESPONSE_TYPE_LOGOFF = "logoff";
    String RESPONSE_TYPE_PROMPT = "prompt";

    // Response codes
     int RESPONSE_STATUS_OK = 0;
     int RESPONSE_STATUS_ERROR = 1;

    /**
     * For the header in the HTTP request. Used only in logon
     */
     String CLIENT_ID_HEAD = "sas:";

    // Header seesion ID. Used in every other request after logon.
    // NOTE that Session-id is a reserved header for HTTP, so ours has to
    // be named something else.
     String HEADER_SESSION_ID = "sas-session-id";

}
