package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/4/21 at  6:11 AM
 */
public class UnsupportedJWTTypeException extends GeneralException{
    public UnsupportedJWTTypeException() {
    }

    public UnsupportedJWTTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedJWTTypeException(String message) {
        super(message);
    }

    public UnsupportedJWTTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
