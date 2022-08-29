package edu.uiuc.ncsa.sas.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/22 at  7:10 AM
 */
public class UnknownSessionException extends SASException {
    public UnknownSessionException() {
    }

    public UnknownSessionException(Throwable cause) {
        super(cause);
    }

    public UnknownSessionException(String message) {
        super(message);
    }

    public UnknownSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
