package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/22/21 at  6:34 AM
 */
public class BlockStatement implements Statement{
    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    List<Statement> statements = new ArrayList<>();



    @Override
    public Object evaluate(State state) {
        State state1 = state.newStateWithImports();
        for(Statement statement : statements){
            statement.evaluate(state1);
        }

        return null;
    }

    @Override
    public List<String> getSourceCode() {
        return null;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {

    }
}
