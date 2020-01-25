package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;

/**
 * Top-level interface for all statements.
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  9:11 AM
 */
public interface Statement {
    public Object evaluate(State state);
    public String getSourceCode();
    public void setSourceCode(String sourceCode);
}
