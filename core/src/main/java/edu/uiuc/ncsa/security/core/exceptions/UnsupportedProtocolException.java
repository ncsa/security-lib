package edu.uiuc.ncsa.security.core.exceptions;


/**
 * An exception when an unsupported protocol in a request is encountered.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 24, 2010 at  11:53:26 AM
 */
public class UnsupportedProtocolException extends GeneralException {
    public UnsupportedProtocolException() {
    }

    public UnsupportedProtocolException(Throwable cause) {
        super(cause);
    }

    public UnsupportedProtocolException(String message) {
        super(message);
    }

    public UnsupportedProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
