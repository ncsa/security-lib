package edu.uiuc.ncsa.security.core.util;


import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * Exception thrown by the {@link Pool} class under certain conditions.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  3:58:07 PM
 */
public class PoolException extends GeneralException {
    public PoolException() {
    }

    public PoolException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoolException(Throwable cause) {
        super(cause);
    }

    public PoolException(String message) {
        super(message);
    }
}
