package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * Once evaluated, this will return its value as the result.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  5:08 PM
 */
public class Assignment implements Statement {
    Object result;
    int resultType;

    public String getVariableReference() {
        return variableReference;
    }

    public void setVariableReference(String variableReference) {
        this.variableReference = variableReference;
    }

    public ExpressionNode getArgument() {
        return argument;
    }

    public void setArgument(ExpressionNode argument) {
        this.argument = argument;
    }

    String variableReference;
    ExpressionNode argument;

    public Object evaluate(State state) {
        result = argument.evaluate(state);
        resultType = argument.getResultType();
       // SymbolTable symbolTable = state.getSymbolStack();
        switch (argument.getResultType()) {
            case Constant.STEM_TYPE:
            case Constant.STRING_TYPE:
            case Constant.BOOLEAN_TYPE:
            case Constant.LONG_TYPE:
            case Constant.DECIMAL_TYPE:
                state.setValue(variableReference, result);
                break;
            default:
                throw new IllegalArgumentException("error, the type of the value \"" + argument.getResult() + "\" is unknown");


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

    String sourceCode;
}
