package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  6:05 AM
 */
public class FunctionArgException extends QDLException{
    public FunctionArgException() {
    }

    public FunctionArgException(Throwable cause) {
        super(cause);
    }

    public FunctionArgException(String message) {
        super(message);
    }

    public FunctionArgException(String message, Throwable cause) {
        super(message, cause);
    }
}
