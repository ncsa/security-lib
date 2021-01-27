package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/25/21 at  10:16 AM
 */
public class QDLIllegalAccessException extends QDLException {
    public QDLIllegalAccessException() {
    }

    public QDLIllegalAccessException(Throwable cause) {
        super(cause);
    }

    public QDLIllegalAccessException(String message) {
        super(message);
    }

    public QDLIllegalAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
