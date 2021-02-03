package edu.uiuc.ncsa.security.core.inheritance;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/3/21 at  7:42 AM
 */
public class UnresolvedReferenceException extends GeneralException {
    public UnresolvedReferenceException() {
    }

    public UnresolvedReferenceException(Throwable cause) {
        super(cause);
    }

    public UnresolvedReferenceException(String message) {
        super(message);
    }

    public UnresolvedReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
