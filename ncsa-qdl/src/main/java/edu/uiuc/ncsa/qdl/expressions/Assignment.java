package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.QDLConstants;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.HasResultInterface;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * Once evaluated, this will return its value as the result.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  5:08 PM
 */
public class Assignment implements Statement, HasResultInterface {
    Object result;

    public Object getResult() {
        if(!isEvaluated()){
            throw new UnevaluatedExpressionException("source=\"" + (getSourceCode() == null?"(none)":getSourceCode()) + "\"");
        }
        return result;
    }

    public int getResultType() {
        return resultType;
    }

    @Override
    public void setResult(Object object) {
        this.result = object;
    }

    @Override
    public void setResultType(int type) {
        this.resultType = type;
    }

    protected boolean evaluated;

    @Override
    public boolean isEvaluated() {
        return evaluated;
    }

    @Override
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    int resultType;

    public String getVariableReference() {
        return variableReference;
    }

    public void setVariableReference(String variableReference) {
        this.variableReference = variableReference;
    }

    public Statement getArgument() {
        return argument;
    }

    public void setArgument(Statement argument) {
        this.argument = argument;
    }

    String variableReference;
    Statement argument;
    // just a case
    protected HasResultInterface getHRI(){return (HasResultInterface) argument;}
    public Object evaluate(State state) {
        if(QDLConstants.isReservedWord(variableReference)){
            throw new IllegalArgumentException("Error: Cannnot use reserved word \"" + variableReference + "\" as a variable.");
        }
        result = argument.evaluate(state);
        getHRI().setEvaluated(true);
        setResult(getHRI().getResult());
        evaluated = true;
        resultType = getHRI().getResultType();
        // Now the real work -- set the value of the variable in the symbol table.
        // Mostly this just traps if some how we get an unknown type, but this is the
        // right place to do it, before it gets in to the symbol table.
        switch (resultType) {

            case Constant.STEM_TYPE:
                if(!getVariableReference().endsWith(STEM_INDEX_MARKER)){
                    throw new IllegalArgumentException("Error: Cannot set the stem \"" + getVariableReference() +"\" to a non-stem variable");
                }
                state.setValue(variableReference, result);
                break;
            case Constant.NULL_TYPE:
                // Can set any variable to null
                state.setValue(variableReference, QDLNull.getInstance());
                break;

            case Constant.STRING_TYPE:
            case Constant.BOOLEAN_TYPE:
            case Constant.LONG_TYPE:
            case Constant.DECIMAL_TYPE:
                if(getVariableReference().endsWith(STEM_INDEX_MARKER)){
                    throw new IllegalArgumentException("Error: Cannot set the scalar value to a stem variable");
                }

                state.setValue(variableReference, result);
                break;
            default:
                throw new IllegalArgumentException("error, the type of the value \"" + getHRI().getResult() + "\" is unknown");
        }
        return result;
    }

    public Assignment() {
    }


    public Assignment(String variableReference, ExpressionNode node) {
        this.variableReference = variableReference;
        argument = node;
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    String sourceCode = null;
}
