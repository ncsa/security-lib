package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.exceptions.RaiseErrorException;
import edu.uiuc.ncsa.qdl.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:36 AM
 */
public class TryCatch implements Statement {
    @Override
    public Object evaluate(State state) {
        State localState = state.newStateWithImports();
        try {
            for (Statement s : tryStatements) {
                s.evaluate(localState);
            }
        } catch (RaiseErrorException t) {
            localState.getSymbolStack().getTopST().setStringValue("error_message", t.getPolyad().getArgumments().get(0).getResult().toString());
            localState.getSymbolStack().getTopST().setLongValue("error_code", (Long) t.getPolyad().getArgumments().get(1).getResult());
            for (Statement c : catchStatements) {
                c.evaluate(localState);
            }
        }
        return null;
    }

    List<Statement> catchStatements = new ArrayList<>();
    List<Statement> tryStatements = new ArrayList<>();

    public List<Statement> getCatchStatements() {
        return catchStatements;
    }

    public void setCatchStatements(List<Statement> catchStatements) {
        this.catchStatements = catchStatements;
    }

    public List<Statement> getTryStatements() {
        return this.tryStatements;
    }

    public void seTryStatements(List<Statement> tryStatements) {
        this.catchStatements = catchStatements;
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

