package edu.uiuc.ncsa.qdl.exceptions;

/**
 *  Used in loops to signal a break command since it is almost impossible to hop out of a complex
 *  expression tree and do the right flow
 * <p>Created by Jeff Gaynor<br>
 * on 1/28/20 at  7:25 PM
 */
public class BreakException extends QDLException {
    public BreakException() {
    }

    public BreakException(Throwable cause) {
        super(cause);
    }

    public BreakException(String message) {
        super(message);
    }

    public BreakException(String message, Throwable cause) {
        super(message, cause);
    }
}
