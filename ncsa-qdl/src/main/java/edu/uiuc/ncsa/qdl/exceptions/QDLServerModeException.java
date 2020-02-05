package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  9:32 AM
 */
public class QDLServerModeException extends QDLException {
    public QDLServerModeException() {
    }

    public QDLServerModeException(Throwable cause) {
        super(cause);
    }

    public QDLServerModeException(String message) {
        super(message);
    }

    public QDLServerModeException(String message, Throwable cause) {
        super(message, cause);
    }
}
