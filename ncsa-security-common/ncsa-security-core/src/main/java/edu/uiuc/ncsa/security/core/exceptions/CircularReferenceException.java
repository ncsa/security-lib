package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/19/19 at  7:19 PM
 */
public class CircularReferenceException extends GeneralException {
    public CircularReferenceException() {
    }

    public CircularReferenceException(Throwable cause) {
        super(cause);
    }

    public CircularReferenceException(String message) {
        super(message);
    }

    public CircularReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
