package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  3:42 PM
 */
public class NamespaceException extends QDLException {
    public NamespaceException() {
    }

    public NamespaceException(Throwable cause) {
        super(cause);
    }

    public NamespaceException(String message) {
        super(message);
    }

    public NamespaceException(String message, Throwable cause) {
        super(message, cause);
    }
}
