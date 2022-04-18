package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  5:41 PM
 */
public class WrongArgCountException extends FunctionArgException{
    public WrongArgCountException() {
    }

    public WrongArgCountException(Throwable cause) {
        super(cause);
    }

    public WrongArgCountException(String message) {
        super(message);
    }

    public WrongArgCountException(String message, Throwable cause) {
        super(message, cause);
    }
}
