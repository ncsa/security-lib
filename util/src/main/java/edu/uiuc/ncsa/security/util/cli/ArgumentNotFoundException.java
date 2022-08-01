package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/17/13 at  11:13 AM
 */
public class ArgumentNotFoundException extends GeneralException {
    public ArgumentNotFoundException() {
    }

    public ArgumentNotFoundException(Throwable cause) {
        super(cause);
    }

    public ArgumentNotFoundException(String message) {
        super(message);
    }

    public ArgumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
