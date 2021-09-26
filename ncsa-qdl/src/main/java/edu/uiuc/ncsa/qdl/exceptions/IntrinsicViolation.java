package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/26/21 at  6:54 AM
 */
public class IntrinsicViolation extends QDLException{
    public IntrinsicViolation() {
    }

    public IntrinsicViolation(Throwable cause) {
        super(cause);
    }

    public IntrinsicViolation(String message) {
        super(message);
    }

    public IntrinsicViolation(String message, Throwable cause) {
        super(message, cause);
    }
}
