package edu.uiuc.ncsa.security.oauth_2_0.jwt;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * If there is a user-created exception thrown by the {@link edu.uiuc.ncsa.security.util.scripting.ScriptRuntimeEngine}
 * this will be thrown. This allows for propagating error conditions inside of scripts outside whatever the runtime
 * is.
 * <p>Created by Jeff Gaynor<br>
 * on 10/9/20 at  8:43 AM
 */
public class ScriptRuntimeException extends GeneralException {
    public ScriptRuntimeException() {
    }

    public ScriptRuntimeException(Throwable cause) {
        super(cause);
    }

    public ScriptRuntimeException(String message) {
        super(message);
    }

    public ScriptRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getRequestedType() {
        return requestedType;
    }

    public void setRequestedType(String requestedType) {
        this.requestedType = requestedType;
    }

    String requestedType;
}
