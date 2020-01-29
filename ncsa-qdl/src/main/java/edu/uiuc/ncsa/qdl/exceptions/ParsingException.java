package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/29/20 at  3:00 PM
 */
public class ParsingException extends QDLException {
    public ParsingException() {
    }

    public ParsingException(Throwable cause) {
        super(cause);
    }

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
