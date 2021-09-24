package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.module.QDLModule;
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
        // Only use local state at this point.
        State localState = state.newModuleState();
        for(Statement s : getStatements()){
            s.evaluate(localState);
        }

        QDLModule module = new QDLModule();
        module.setNamespace(getNamespace());
        module.setAlias(getAlias());
        module.setState(localState);
        module.setTemplate(true);
        module.setModuleStatement(this);
        state.getModuleMap().put(getNamespace(), module);

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

    public List<String> getDocumentation() {
        return documentation;
    }

    public void setDocumentation(List<String> documentation) {
        this.documentation = documentation;
    }

    public List<String> documentation = new ArrayList();
}
