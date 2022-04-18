package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  6:06 AM
 */
public class ExtraArgException extends FunctionArgException{
    public ExtraArgException() {
    }

    public ExtraArgException(Throwable cause) {
        super(cause);
    }

    public ExtraArgException(String message) {
        super(message);
    }

    public ExtraArgException(String message, Throwable cause) {
        super(message, cause);
    }
}
