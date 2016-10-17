package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/13/16 at  3:12 PM
 */
public class IllegalAccessException extends GeneralException {
    public IllegalAccessException() {
    }

    public IllegalAccessException(Throwable cause) {
        super(cause);
    }

    public IllegalAccessException(String message) {
        super(message);
    }

    public IllegalAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
