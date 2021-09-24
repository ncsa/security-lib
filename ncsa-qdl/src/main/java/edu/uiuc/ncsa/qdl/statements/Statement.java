package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;

import java.io.Serializable;
import java.util.List;

/**
 * Top-level interface for all statements.
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  9:11 AM
 */
public interface Statement extends Serializable {
    Object evaluate(State state);

    List<String> getSourceCode();

    void setSourceCode(List<String> sourceCode);

/*    Module getModule();

    void setModule(Module newModule);

    boolean hasModule();*/
}
