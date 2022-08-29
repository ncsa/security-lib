package edu.uiuc.ncsa.sas.exceptions;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/25/22 at  7:37 AM
 */
public class SASException extends GeneralException {
    public SASException() {
    }

    public SASException(Throwable cause) {
        super(cause);
    }

    public SASException(String message) {
        super(message);
    }

    public SASException(String message, Throwable cause) {
        super(message, cause);
    }
}
