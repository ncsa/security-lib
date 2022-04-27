package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/22 at  5:41 PM
 */
public class WrongArgCountException extends FunctionArgException{
    public WrongArgCountException(Statement statement) {
        super(statement);
    }

    public WrongArgCountException(Throwable cause, Statement statement) {
        super(cause, statement);
    }

    public WrongArgCountException(String message, Statement statement) {
        super(message, statement);
    }

    public WrongArgCountException(String message, Throwable cause, Statement statement) {
        super(message, cause, statement);
    }
}
