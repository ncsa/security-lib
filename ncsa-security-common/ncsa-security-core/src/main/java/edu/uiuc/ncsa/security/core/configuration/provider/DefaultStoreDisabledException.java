package edu.uiuc.ncsa.security.core.configuration.provider;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * An exception to be thrown if default stores are disabled.
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/12 at  12:09 PM
 */
public class DefaultStoreDisabledException extends GeneralException {
    public DefaultStoreDisabledException() {
    }

    public DefaultStoreDisabledException(Throwable cause) {
        super(cause);
    }

    public DefaultStoreDisabledException(String message) {
        super(message);
    }

    public DefaultStoreDisabledException(String message, Throwable cause) {
        super(message, cause);
    }
}
