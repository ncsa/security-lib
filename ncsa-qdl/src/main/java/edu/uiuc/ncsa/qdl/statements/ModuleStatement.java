package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.module.Module;
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
    TokenPosition tokenPosition = null;

    @Override
    public void setTokenPosition(TokenPosition tokenPosition) {
        this.tokenPosition = tokenPosition;
    }

    @Override
    public TokenPosition getTokenPosition() {
        return tokenPosition;
    }

    @Override
    public boolean hasTokenPosition() {
        return tokenPosition != null;
    }

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

    /*
      module['a:/a','a']body[q:=1;];
 module_import('a:/a');
 module_import('a:/a','b');
 module['q:/q','w']body[module_import('a:/a');zz:=a#q+2;];
     */
    @Override
    public Object evaluate(State state) {

        QDLModule module = new QDLModule();
        module.setNamespace(getNamespace());
        module.setAlias(getAlias());
        module.setTemplate(true);
        module.setModuleStatement(this);
        module.setDocumentation(getDocumentation());
        //state.getMTemplates().put(new MTKey(getNamespace()), module);
        if (state.isImportMode()) {
            // This creates the instance from the statement.
            State localState = state.newLocalState(state);
            //State localState = state.newCleanState();
            localState.setImportMode(true);
            for (Statement s : getStatements()) {
                if (s instanceof ModuleStatement) {
                    // Then a module is being created inside the current module. Toggle import mode
                    // so the templates inside the local state get updated (and not either lost or
                    // something else is updated.
                    localState.setImportMode(false);
                }
                s.evaluate(localState);
                if (s instanceof ModuleStatement) {
                    localState.setImportMode(true);
                }
            }
            localState.setImportMode(false);
            module.setState(localState);
            //state.getMInstances().put(module);
            this.mInstance = module;
        } else {
            state.getMTemplates().put(module);
        }

        return null;
    }

    public Module getmInstance() {
        return mInstance;
    }

    Module mInstance;

    public void clearInstance() {
        mInstance = null;
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
