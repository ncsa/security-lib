package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;

import java.io.Serializable;

/**
 * Top-level interface for all statements.
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  9:11 AM
 */
public interface Statement extends Serializable {
    public Object evaluate(State state);
    public String getSourceCode();
    public void setSourceCode(String sourceCode);
}
