package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * Thrown when the argument to a  function is not an accepted type.
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  6:07 AM
 */
public class BadArgException extends FunctionArgException{
    public BadArgException(Statement statement) {
        super(statement);
    }

    public BadArgException(Throwable cause, Statement statement) {
        super(cause, statement);
    }

    public BadArgException(String message, Statement statement) {
        super(message, statement);
    }

    public BadArgException(String message, Throwable cause, Statement statement) {
        super(message, cause, statement);
    }
}
