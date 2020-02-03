package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  9:36 AM
 */
public class IndexError extends QDLException {
    public IndexError() {
    }

    public IndexError(Throwable cause) {
        super(cause);
    }

    public IndexError(String message) {
        super(message);
    }

    public IndexError(String message, Throwable cause) {
        super(message, cause);
    }
}
