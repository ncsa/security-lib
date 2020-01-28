package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/25/20 at  6:47 AM
 */
public class UnknownSymbolException extends QDLException {
    public UnknownSymbolException() {
    }

    public UnknownSymbolException(Throwable cause) {
        super(cause);
    }

    public UnknownSymbolException(String message) {
        super(message);
    }

    public UnknownSymbolException(String message, Throwable cause) {
        super(message, cause);
    }
}
