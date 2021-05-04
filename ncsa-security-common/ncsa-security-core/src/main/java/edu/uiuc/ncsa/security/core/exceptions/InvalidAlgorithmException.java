package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/4/21 at  6:08 AM
 */
public class InvalidAlgorithmException extends GeneralException{
    public InvalidAlgorithmException() {
    }

    public InvalidAlgorithmException(Throwable cause) {
        super(cause);
    }

    public InvalidAlgorithmException(String message) {
        super(message);
    }

    public InvalidAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }
}
