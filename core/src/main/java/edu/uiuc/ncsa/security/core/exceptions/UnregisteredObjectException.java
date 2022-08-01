package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/20/16 at  11:23 AM
 */
public class UnregisteredObjectException extends GeneralException {
    public UnregisteredObjectException() {
    }

    public UnregisteredObjectException(Throwable cause) {
        super(cause);
    }

    public UnregisteredObjectException(String message) {
        super(message);
    }

    public UnregisteredObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
