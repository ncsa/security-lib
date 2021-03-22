package edu.uiuc.ncsa.qdl.exceptions;

/**
 * Thrown when trying to access parts of stems that don't exist.
 * <p>Created by Jeff Gaynor<br>
 * on 3/22/21 at  2:28 PM
 */
public class RankException extends QDLException{
    public RankException() {
    }

    public RankException(Throwable cause) {
        super(cause);
    }

    public RankException(String message) {
        super(message);
    }

    public RankException(String message, Throwable cause) {
        super(message, cause);
    }
}
