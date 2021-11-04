package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.exceptions.AssertionException;
import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.state.State;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/21 at  12:53 PM
 */
public class AssertStatement implements Statement{
    TokenPosition tokenPosition = null;
    @Override
    public void setTokenPosition(TokenPosition tokenPosition) {this.tokenPosition=tokenPosition;}

    @Override
    public TokenPosition getTokenPosition() {return tokenPosition;}

    @Override
    public boolean hasTokenPosition() {return tokenPosition!=null;}

    public AssertStatement(TokenPosition tokenPosition) {
        this.tokenPosition = tokenPosition;
    }

    StatementWithResultInterface conditional;

    public StatementWithResultInterface getConditional() {
        return conditional;
    }

    public void setConditional(StatementWithResultInterface conditional) {
        this.conditional = conditional;
    }

    public ExpressionNode getMesssge() {
        return messsge;
    }

    public void setMesssge(ExpressionNode messsge) {
        this.messsge = messsge;
    }

    ExpressionNode messsge;
    @Override
    public Object evaluate(State state) {
        if(!state.isAssertionsOn()){
            return null;
        }

        Object obj = getConditional().evaluate(state);
        if(obj instanceof Boolean){
             Boolean b = (Boolean) obj;
             if(!b){
                 if(getMesssge() != null) {
                     Object m = getMesssge().evaluate(state);
                     throw new AssertionException(m.toString());
                 }else{
                     throw new AssertionException(""); // no message implies empty message
                 }
             }else{
                 return Boolean.TRUE;
             }
        }
        throw new IllegalArgumentException("error: the conditional must be boolean valued.");
    }

    @Override
    public List<String> getSourceCode() {
        return null;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {

    }
}
