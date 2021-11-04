package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.statements.TokenPosition;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Top level for various block statements in the parser. These exist only to leverage
 * all the machinery for dealing with statements (and are blocks of statements).
 * <h2>Normal usage</h2>
 * In the course of parsing, these get made and are mined for their statements. They are
 * (at this point) not passed along or evaluated.
 * This allows you to collect things in syntactic blocks then restructure them,
 * e.g. in a conditional or a loop.
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/21 at  6:37 AM
 */
public class ParseStatementBlock implements StatementWithResultInterface {
    TokenPosition tokenPosition = null;
    @Override
    public void setTokenPosition(TokenPosition tokenPosition) {this.tokenPosition=tokenPosition;}

    @Override
    public TokenPosition getTokenPosition() {return tokenPosition;}

    @Override
    public boolean hasTokenPosition() {return tokenPosition!=null;}    @Override
    public boolean isInModule() {
        return alias!=null;
    }
    String alias = null;

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
         this.alias = alias;
    }

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
     throw new NotImplementedException("parse statement blocks do not execute.");
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
