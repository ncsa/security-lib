package edu.uiuc.ncsa.security.oauth_2_0;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/15 at  1:43 PM
 */
public interface OA2Errors {
    String ERROR_URI_PARAMETER = "error_uri";


    // OIDC specific error codes that must be returned if the authorization fails.
    /**
     * The Authorization Server requires End-User interaction of some form to proceed. This error MAY be returned when
     * the prompt parameter value in the Authentication Request is none, but the Authentication Request cannot be
     * completed without displaying a user interface for End-User interaction. \
     */
    String INTERACTION_REQUIRED = "interaction_required";
    public static int INTERACTION_REQUIRED_CODE = 100;
    /**
     * The Authorization Server requires End-User authentication. This error MAY be returned when the prompt parameter
     * value in the Authentication Request is none, but the Authentication Request cannot be completed without
     * displaying a user interface for End-User authentication.
     */
    String LOGIN_REQUIRED = "login_required";
    public static int LOGIN_REQUIRED_CODE = 101;
    /**
     * The End-User is REQUIRED to select a session at the Authorization Server. The End-User MAY be authenticated at
     * the Authorization Server with different associated accounts, but the End-User did not select a session. This
     * error MAY be returned when the prompt parameter value in the Authentication Request is none, but the
     * Authentication Request cannot be completed without displaying a user interface to prompt for a session to use.
     */
    String ACCOUNT_SELECTION_REQUIRED = "account_selection_required";
    public static int ACCOUNT_SELECTION_REQUIRED_CODE = 102;
    /**
     * The Authorization Server requires End-User consent. This error MAY be returned when the prompt parameter value
     * in the Authentication Request is none, but the Authentication Request cannot be completed without displaying a
     * user interface for End-User consent.
     */
    String CONSENT_REQUIRED = "consent_required";
    public static int CONSENT_REQUIRED_CODE = 103;
    /**
     * The request_uri in the Authorization Request returns an error or contains invalid data.
     */
    String INVALID_REQUEST_URI = "invalid_request_uri";
    public static int INVALID_REQUEST_URI_CODE = 104;
    /**
     * The request parameter contains an invalid Request Object.
     */
    String INVALID_REQUEST_OBJECT = "invalid_request_object";
    public static int INVALID_REQUEST_OBJECT_CODE = 105;
    /**
     * The OP does not support use of the request parameter defined in Section 6.
     */
    String REQUEST_NOT_SUPPORTED = "request_not_supported";
    public static int REQUEST_NOT_SUPPORTED_CODE = 106;
    /**
     * The OP does not support use of the request_uri parameter defined in Section 6.
     */
    String REQUEST_URI_NOT_SUPPORTED = "request_uri_not_supported";
    public static int REQUEST_URI_NOT_SUPPORTED_CODE = 107;
    /**
     * The OP does not support use of the registration parameter defined in Section 7.2.1.
     */
    String REGISTRATION_NOT_SUPPORTED = "registration_not_supported";
    public static int REGISTRATION_NOT_SUPPORTED_CODE = 108;
    /**
     * The request is missing a required parameter, includes an
     * invalid parameter value, includes a parameter more than
     * once, or is otherwise malformed.
     */
    String INVALID_REQUEST = "invalid_request";
    public static int INVALID_REQUEST_CODE = 109;
    /**
     * The client is not authorized to request an authorization
     * code using this method.
     */
    String UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static int UNAUTHORIZED_CLIENT_CODE = 110;
    /**
     * The resource owner or authorization server denied the request.
     */
    String ACCESS_DENIED = "access_denied";
    public static int ACCESS_DENIED_CODE = 111;
    /**
     * The authorization server does not support obtaining an
     * authorization code using this method.
     */
    String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    public static int UNSUPPORTED_RESPONSE_TYPE_CODE = 112;
    /**
     * The requested scope is invalid, unknown, or malformed.
     */
    String INVALID_SCOPE = "invalid_scope";
    public static int INVALID_SCOPE_CODE = 113;
    /**
     * The authorization server encountered an unexpected
     * condition that prevented it from fulfilling the request.
     * (This error code is needed because a 500 Internal Server
     * Error HTTP status code cannot be returned to the client
     * via an HTTP redirect.)
     */
    String SERVER_ERROR = "server_error";
    public static int SERVER_ERROR_CODE = 114;
    /**
     * The authorization server is currently unable to handle
     * the request due to a temporary overloading or maintenance
     * of the server.  (This error code is needed because a 503
     * Service Unavailable HTTP status code cannot be returned
     * to the client via an HTTP redirect.)
     */
    String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    public static int TEMPORARILY_UNAVAILABLE_CODE = 115;
    /**
     * Specifically for the userInfo and getCert endpoints. This is used whenever a token is encountered
     * that is not valid (for whatever reason).
     */
     String INVALID_TOKEN = "invalid_token";
     public static int  INVALID_TOKEN_CODE  = 116;

    /**
     * Used in the access servlet when a grant is presented that is either expired or invalid.
     */
    String INVALID_GRANT = "invalid_grant";

    String INVALID_TARGET = "invalid_target";
    int INVALID_TARGET_CODE = 117;
    /**
     * Returned code if an error is returned but cannot be found on the list of standard errors
     */
    public static int UNKNOWN_ERROR = -1;
    /**
     * Returned if the error description itself is trivial (null or empty).
     */
    public static int INVALID_ERROR = -2;

    public static class ErrorUtil {
        public static int lookupErrorCode(String errorDescription) {
            if (errorDescription == null && errorDescription.length() == 0) return INVALID_ERROR;
            if (errorDescription.equals(INTERACTION_REQUIRED)) return INTERACTION_REQUIRED_CODE;
            if(errorDescription.equals(LOGIN_REQUIRED)) return LOGIN_REQUIRED_CODE;
            if(errorDescription.equals(ACCOUNT_SELECTION_REQUIRED)) return ACCOUNT_SELECTION_REQUIRED_CODE;
            if(errorDescription.equals(CONSENT_REQUIRED)) return CONSENT_REQUIRED_CODE;
            if(errorDescription.equals(INVALID_REQUEST_OBJECT)) return INVALID_REQUEST_OBJECT_CODE;
            if(errorDescription.equals(INVALID_REQUEST)) return INVALID_REQUEST_CODE;
            if(errorDescription.equals(INVALID_REQUEST_URI)) return INVALID_REQUEST_URI_CODE;
            if(errorDescription.equals(UNAUTHORIZED_CLIENT)) return UNAUTHORIZED_CLIENT_CODE;
            if(errorDescription.equals(ACCESS_DENIED)) return ACCESS_DENIED_CODE;
            if(errorDescription.equals(UNSUPPORTED_RESPONSE_TYPE)) return UNSUPPORTED_RESPONSE_TYPE_CODE;
            if(errorDescription.equals(INVALID_SCOPE)) return INVALID_SCOPE_CODE;
            if(errorDescription.equals(TEMPORARILY_UNAVAILABLE)) return TEMPORARILY_UNAVAILABLE_CODE;
            if(errorDescription.equals(SERVER_ERROR)) return SERVER_ERROR_CODE;
            if(errorDescription.equals(INVALID_TOKEN)) return INVALID_TOKEN_CODE;
            if(errorDescription.equals(INVALID_TARGET)) return INVALID_TARGET_CODE;
            return UNKNOWN_ERROR;
        }
    }
}
