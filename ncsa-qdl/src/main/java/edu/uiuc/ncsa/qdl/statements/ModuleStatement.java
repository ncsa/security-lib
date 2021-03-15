package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Has to exist and fulfill various contracts, but mostly this collects state and statements and puts them
 * in a module object.
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:35 AM
 */
public class ModuleStatement implements Statement {
    URI namespace;
    String alias;

    public URI getNamespace() {
        return namespace;
    }

    public void setNamespace(URI namespace) {
        this.namespace = namespace;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public State getLocalState() {
        return localState;
    }

    public void setLocalState(State localState) {
        this.localState = localState;
    }

    State localState;
    @Override
    public Object evaluate(State state) {
        // Only use local state at this point.
        for(Statement s : getStatements()){
            s.evaluate(getLocalState());
        }
        return null;
    }
    List<Statement> statements = new ArrayList<>();

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public List<String> getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    List<String> sourceCode;

}
