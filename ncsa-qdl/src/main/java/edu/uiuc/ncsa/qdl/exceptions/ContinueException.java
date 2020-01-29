package edu.uiuc.ncsa.qdl.exceptions;

/**
 * Used in loops to signal a continue command since it is almost impossible to hop out of a complex
 * expression tree and do the right flow
 * <p>Created by Jeff Gaynor<br>
 * on 1/28/20 at  7:25 PM
 */
public class ContinueException extends QDLException {
    public ContinueException() {
    }

    public ContinueException(Throwable cause) {
        super(cause);
    }

    public ContinueException(String message) {
        super(message);
    }

    public ContinueException(String message, Throwable cause) {
        super(message, cause);
    }
}
