package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  3:39 PM
 */
public class ImportException extends GeneralException {
    public ImportException() {
    }

    public ImportException(Throwable cause) {
        super(cause);
    }

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
