package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

import java.net.URI;

/**
 * A standard OIDC error, where there is a valid redirect and the return codes are turned into parameters in the
 * redirect. In cases where there is no redirect available, you must use an {@link OA2GeneralError}
 * instead.
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/15 at  11:33 AM
 */
public class OA2RedirectableError extends GeneralException {
    String error;
    String description;
    String state;

    public URI getCallback() {
        return callback;
    }

    public void setCallback(URI callback) {
        this.callback = callback;
    }

    URI callback;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    int errorCode;

    public OA2RedirectableError(String error, String description, String state, URI callback) {
        this(error, description, state);
        this.callback = callback;
    }

    public OA2RedirectableError(String error, String description, String state, String callback) {
        this(error, description, state);
        if (callback != null) {
            // if there is a missing callback, then keep going rather than get a nullpointer exception.
            this.callback = URI.create(callback);
        }
    }

    public OA2RedirectableError(String error, String description, String state) {
        super("error: "+error+" (description: "+description+")");
        this.error = error;
        this.description = description;
        this.state = state;
        this.errorCode = OA2Errors.ErrorUtil.lookupErrorCode(error);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

}
