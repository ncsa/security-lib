package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.module.Module;
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

    @Override
    public Object evaluate(State state) {
        State localState = state.newLocalState();
        Module module = new Module(getNamespace(), getAlias(), localState);
        for(Statement s : getStatements()){
            s.evaluate(localState);
        }
        state.getModuleMap().put(module);
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
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    String sourceCode;
}
