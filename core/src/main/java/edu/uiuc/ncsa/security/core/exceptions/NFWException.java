package edu.uiuc.ncsa.security.core.exceptions;

/**
 * For "No F-ing Way" Exception. Normally these should never arise unless there is some internal inconsistency
 * in the server. Checks for these effectively give regression information should things that always work suddenly stop.
 * I suppose these might be called "InternalException" or some such, but that is over-used and less informative.
 *
 * <h3>Usage</h3>
 * <p>When coding, if you have a situation that should not occur, throw one of these. For instance, if you have
 * to catch an EncodingException but that should never, every actually happen, catch it and rethrow with an NFWException.
 * This means that at some point in the future, if something internal in the code changes, you might get one of these and
 * will know that some basic contract or functionality of the software has been altered. For software with an
 * expected very long lifetime (years if not decades) this can become invaluable at finding errors, especially if
 * the maintainers of the code are long gone. It is very good the ensure the code will tell you things change.</p>
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
