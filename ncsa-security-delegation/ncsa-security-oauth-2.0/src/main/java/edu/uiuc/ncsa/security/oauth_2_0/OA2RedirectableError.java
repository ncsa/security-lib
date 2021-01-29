package edu.uiuc.ncsa.security.oauth_2_0;

import java.net.URI;

/**
 * A standard OIDC error, where there is a valid redirect and the return codes are turned into parameters in the
 * redirect. In cases where there is no redirect available, you must use an {@link OA2GeneralError}
 * instead.
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/15 at  11:33 AM
 */
public class OA2RedirectableError extends OA2GeneralError {
    URI callback;

    public URI getCallback() {
        return callback;
    }

    public void setCallback(URI callback) {
        this.callback = callback;
    }

    public OA2RedirectableError(String error,
                                String description,
                                int httpStatus,
                                String state,
                                URI callback) {
        this(error, description, httpStatus, state);
        this.callback = callback;
    }


    public OA2RedirectableError(String error, String description, int httpStatus, String state) {
        super("error: "+error+" (description: "+description+")");
        this.error = error;
        this.description = description;
        this.state = state;
        this.httpStatus = httpStatus;
    }


    public OA2RedirectableError() {
    }

    public OA2RedirectableError(Throwable cause) {
        super(cause);
    }

    public OA2RedirectableError(String message) {
        super(message);
    }

    public OA2RedirectableError(String message, Throwable cause) {
        super(message, cause);
    }

    public boolean hasCallback(){
        return callback != null;
    }
}
