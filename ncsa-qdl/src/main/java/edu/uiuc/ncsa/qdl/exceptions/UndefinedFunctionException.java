package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  6:51 AM
 */
public class UndefinedFunctionException extends GeneralException {
    public UndefinedFunctionException() {
    }

    public UndefinedFunctionException(Throwable cause) {
        super(cause);
    }

    public UndefinedFunctionException(String message) {
        super(message);
    }

    public UndefinedFunctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
