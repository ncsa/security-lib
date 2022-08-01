package edu.uiuc.ncsa.security.core.exceptions;


/**
 * Used by caches, this is thrown if an attempt to write to the cache is made after the cache has had its
 * <code>destroy</code> method invoked.
 * <p>Created by Jeff Gaynor<br>
 * on May 10, 2010 at  8:40:42 AM
 */
public class DestroyedException extends GeneralException {
    public DestroyedException() {
    }

    public DestroyedException(String message) {
        super(message);
    }

    public DestroyedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DestroyedException(Throwable cause) {
        super(cause);
    }
}
