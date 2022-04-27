package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  6:06 AM
 */
public class MissingArgException extends FunctionArgException{
    public MissingArgException(Statement statement) {
        super(statement);
    }

    public MissingArgException(Throwable cause, Statement statement) {
        super(cause, statement);
    }

    public MissingArgException(String message, Statement statement) {
        super(message, statement);
    }

    public MissingArgException(String message, Throwable cause, Statement statement) {
        super(message, cause, statement);
    }
}
