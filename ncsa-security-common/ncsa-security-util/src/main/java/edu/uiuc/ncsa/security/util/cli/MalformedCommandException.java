package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/17/13 at  3:20 PM
 */
public class MalformedCommandException extends GeneralException {
    public MalformedCommandException() {
    }

    public MalformedCommandException(Throwable cause) {
        super(cause);
    }

    public MalformedCommandException(String message) {
        super(message);
    }

    public MalformedCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
