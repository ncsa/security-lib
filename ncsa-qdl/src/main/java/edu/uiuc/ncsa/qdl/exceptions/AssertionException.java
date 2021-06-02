package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/21 at  12:57 PM
 */
public class AssertionException extends QDLException{
    public AssertionException() {
    }

    public AssertionException(Throwable cause) {
        super(cause);
    }

    public AssertionException(String message) {
        super(message);
    }

    public AssertionException(String message, Throwable cause) {
        super(message, cause);
    }
}
