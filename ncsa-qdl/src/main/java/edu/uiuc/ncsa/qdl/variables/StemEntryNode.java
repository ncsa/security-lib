package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.UnknownSymbolException;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.statements.TokenPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Models an entry for a stem variable (that is not a list element). This is needed because these can be
 * expressions for the key and value which can only be determined at runtime, not earlier, hence everything has to be evaluated.
 * Note that these come from the parser directly and are really only used by {@link StemVariableNode} to track its
 * entries.
 * <p>Created by Jeff Gaynor<br>
 * on 9/28/20 at  1:47 PM
 */
public class StemEntryNode implements StatementWithResultInterface {
    TokenPosition tokenPosition = null;
    @Override
    public void setTokenPosition(TokenPosition tokenPosition) {this.tokenPosition=tokenPosition;}

    @Override
    public TokenPosition getTokenPosition() {return tokenPosition;}

    @Override
    public boolean hasTokenPosition() {return tokenPosition!=null;}
    StatementWithResultInterface key;
    StatementWithResultInterface value;

    @Override
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

    public boolean isDefaultValue() {
        return isDefaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        isDefaultValue = defaultValue;
    }

    boolean isDefaultValue = false;
    public StatementWithResultInterface getKey() {
        return key;
    }

    public void setKey(StatementWithResultInterface key) {
        this.key = key;
    }

    public Statement getValue() {
        return value;
    }

    public void setValue(StatementWithResultInterface value) {
        this.value = value;
    }

    boolean evaluated = false;

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    @Override
    public Object evaluate(State state) {
        if(!isDefaultValue) {
            getKey().evaluate(state);
            if(getKey() instanceof VariableNode){
                if(((VariableNode)getKey()).getResult() == null){
                    throw new UnknownSymbolException("\'" + ((VariableNode)getKey()).getVariableReference() + "' not found for stem key", getKey());
                }
            }

        }
        getValue().evaluate(state);
        if(getValue() instanceof VariableNode){
            if(((VariableNode)getValue()).getResult() == null){
                throw new UnknownSymbolException("\'" + ((VariableNode)getValue()).getVariableReference() + "' not found for stem value", getValue());
            }
        }
        setEvaluated(true);
        return null;
    }

    List<String> sourceCode = new ArrayList<>();

    @Override
    public List<String> getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        return null;
    }

    // None of these should **ever** be called, but the interface requires them.
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
}
