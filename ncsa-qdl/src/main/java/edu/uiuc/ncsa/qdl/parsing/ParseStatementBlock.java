package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Tope level for various block statements in the parser. These exist only to leverage
 * all the machinery for dealing with statements (and are blocks of statements).
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/21 at  6:37 AM
 */
public class ParseStatementBlock implements StatementWithResultInterface {
    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public void setResult(Object object) {

    }

    @Override
    public int getResultType() {
        return 0;
    }

    @Override
    public void setResultType(int type) {

    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    List<Statement> statements = new ArrayList<>();

    @Override
    public boolean isEvaluated() {
        return false;
    }

    @Override
    public void setEvaluated(boolean evaluated) {

    }

    @Override
    public Object evaluate(State state) {
        return null;
    }

    List<String> src = new ArrayList<>();
    @Override
    public List<String> getSourceCode() {
        return src;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
                  this.src = sourceCode;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        return null;
    }
}
