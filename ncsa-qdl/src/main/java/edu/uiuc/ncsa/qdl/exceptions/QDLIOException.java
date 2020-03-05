package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/4/20 at  10:33 AM
 */
public class QDLIOException extends QDLException {
    public QDLIOException() {
    }

    public QDLIOException(Throwable cause) {
        super(cause);
    }

    public QDLIOException(String message) {
        super(message);
    }

    public QDLIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
