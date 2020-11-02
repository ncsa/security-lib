package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/2/20 at  8:18 AM
 */
public class AssignmentException extends QDLException {
    public AssignmentException() {
    }

    public AssignmentException(Throwable cause) {
        super(cause);
    }

    public AssignmentException(String message) {
        super(message);
    }

    public AssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
