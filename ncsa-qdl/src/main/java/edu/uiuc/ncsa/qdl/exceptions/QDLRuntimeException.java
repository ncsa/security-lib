package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/28/20 at  7:13 AM
 */
public class QDLRuntimeException extends QDLException {
    public QDLRuntimeException() {
    }

    public QDLRuntimeException(Throwable cause) {
        super(cause);
    }

    public QDLRuntimeException(String message) {
        super(message);
    }

    public QDLRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
