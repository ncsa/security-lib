package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/4/21 at  6:07 AM
 */
public class InvalidSignatureException extends GeneralException{
    public InvalidSignatureException() {
    }

    public InvalidSignatureException(Throwable cause) {
        super(cause);
    }

    public InvalidSignatureException(String message) {
        super(message);
    }

    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
