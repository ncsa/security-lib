package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/26/20 at  6:27 AM
 */
public class MissingArgumentException extends QDLException {
    public MissingArgumentException() {
    }

    public MissingArgumentException(Throwable cause) {
        super(cause);
    }

    public MissingArgumentException(String message) {
        super(message);
    }

    public MissingArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
