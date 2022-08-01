package edu.uiuc.ncsa.security.core.exceptions;


/**
 * Thrown when an attempt to write to a store is made before it has been initialized.
 * <p>Created by Jeff Gaynor<br>
 * on May 10, 2010 at  8:41:15 AM
 */
public class UninitializedException extends GeneralException {
    public UninitializedException() {
    }

    public UninitializedException(String message) {
        super(message);
    }

    public UninitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UninitializedException(Throwable cause) {
        super(cause);
    }
}
