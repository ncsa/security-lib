package edu.uiuc.ncsa.security.core.exceptions;

/**
 * For "No F-ing Way" Exception. Normally these should never arise unless there is some internal inconsistency
 * in the server. Checks for these effectively give regression information should things that always work suddenly stop.
 * I suppose these might be called "InternalException" or some such, but that is over-used and less informative.
 * <p>Created by Jeff Gaynor<br>
 * on 3/17/14 at  1:02 PM
 */
public class NFWException extends GeneralException {
    public NFWException() {
    }

    public NFWException(Throwable cause) {
        super(cause);
    }

    public NFWException(String message) {
        super(message);
    }

    public NFWException(String message, Throwable cause) {
        super(message, cause);
    }
}
