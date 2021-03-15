package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;

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
    StatementWithResultInterface key;
    StatementWithResultInterface value;

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
        }
        getValue().evaluate(state);
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
