package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  6:06 AM
 */
public class MissingArgException extends FunctionArgException{
    public MissingArgException() {
    }

    public MissingArgException(Throwable cause) {
        super(cause);
    }

    public MissingArgException(String message) {
        super(message);
    }

    public MissingArgException(String message, Throwable cause) {
        super(message, cause);
    }
}
