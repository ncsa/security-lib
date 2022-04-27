package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  6:06 AM
 */
public class ExtraArgException extends FunctionArgException{
    public ExtraArgException(Statement statement) {
        super(statement);
    }

    public ExtraArgException(Throwable cause, Statement statement) {
        super(cause, statement);
    }

    public ExtraArgException(String message, Statement statement) {
        super(message, statement);
    }

    public ExtraArgException(String message, Throwable cause, Statement statement) {
        super(message, cause, statement);
    }
}
