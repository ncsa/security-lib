package edu.uiuc.ncsa.qdl.exceptions;

/**
 * Thrown when the argument to a  function is not an accepted type.
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  6:07 AM
 */
public class BadArgException extends FunctionArgException{
    public BadArgException() {
    }

    public BadArgException(Throwable cause) {
        super(cause);
    }

    public BadArgException(String message) {
        super(message);
    }

    public BadArgException(String message, Throwable cause) {
        super(message, cause);
    }
}
