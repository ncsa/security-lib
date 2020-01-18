package edu.uiuc.ncsa.security.util.qdl.expressions;

import edu.uiuc.ncsa.security.util.qdl.statements.Statement;
import edu.uiuc.ncsa.security.util.qdl.util.StemVariable;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;

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
    SymbolTable symbolTable;
    ExpressionNode argument;

    public Object evaluate() {
        result = argument.evaluate();
        resultType = argument.getResultType();
        switch (argument.getResultType()) {
            case Constant.STEM_TYPE:
                symbolTable.setStemVariable(variableReference, (StemVariable) result);
                break;
            case Constant.STRING_TYPE:
                symbolTable.setStringValue(variableReference, (String) result);
                break;
            case Constant.BOOLEAN_TYPE:
                symbolTable.setBooleanValue(variableReference, (Boolean) result);
                break;
            case Constant.LONG_TYPE:
                symbolTable.setLongValue(variableReference, (Long) result);
                break;
            default:
                throw new IllegalArgumentException("error, the type of the value \"" + argument.getResult() + "\" is unknown");


        }
        return result;
    }


    public Assignment(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public Assignment(String variableReference, ExpressionNode node, SymbolTable symbolTable) {


        this.variableReference = variableReference;
        argument = node;
        this.symbolTable = symbolTable;
    }

}
