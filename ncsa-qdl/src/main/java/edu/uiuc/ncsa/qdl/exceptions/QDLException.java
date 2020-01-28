package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/25/20 at  6:47 AM
 */
public class QDLException extends GeneralException {
    public QDLException() {
    }

    public QDLException(Throwable cause) {
        super(cause);
    }

    public QDLException(String message) {
        super(message);
    }

    public QDLException(String message, Throwable cause) {
        super(message, cause);
    }
}
