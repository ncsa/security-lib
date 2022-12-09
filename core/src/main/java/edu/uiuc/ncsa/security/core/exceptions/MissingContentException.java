package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 12/6/22 at  4:16 PM
 */
public class MissingContentException extends GeneralException{
    public MissingContentException() {
    }

    public MissingContentException(Throwable cause) {
        super(cause);
    }

    public MissingContentException(String message) {
        super(message);
    }

    public MissingContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
