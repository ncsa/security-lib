package edu.uiuc.ncsa.qdl.statements;

import java.io.Serializable;

/**
 * The parser returns elements. These contain {@link Statement}s which may contain
 * many other statements {@link Statement}s. So conceptually, an elements contains
 * an executable statement that may contain statements.
 * 
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/20 at  8:54 AM
 */
public class Element implements Serializable {
    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    Statement statement;
}
