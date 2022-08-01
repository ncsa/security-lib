package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/25/16 at  10:50 AM
 */
public class InvalidURIException extends GeneralException {
    public InvalidURIException() {
    }

    public InvalidURIException(Throwable cause) {
        super(cause);
    }

    public InvalidURIException(String message) {
        super(message);
    }

    public InvalidURIException(String message, Throwable cause) {
        super(message, cause);
    }
}
