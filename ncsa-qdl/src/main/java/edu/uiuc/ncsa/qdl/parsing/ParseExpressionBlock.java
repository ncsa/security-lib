package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.statements.TokenPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/21 at  6:43 AM
 */
public class ParseExpressionBlock implements StatementWithResultInterface {
    TokenPosition tokenPosition = null;
    @Override
    public void setTokenPosition(TokenPosition tokenPosition) {this.tokenPosition=tokenPosition;}

    @Override
    public TokenPosition getTokenPosition() {return tokenPosition;}

    @Override
    public boolean hasTokenPosition() {return tokenPosition!=null;}
    public List<ExpressionNode> getExpressionNodes() {
        return expressionNodes;
    }

    public void setExpressionNodes(List<ExpressionNode> expressionNodes) {
        this.expressionNodes = expressionNodes;
    }

    @Override
    public boolean isInModule() {
        return alias != null;
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

    List<ExpressionNode> expressionNodes = new ArrayList<>();

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
