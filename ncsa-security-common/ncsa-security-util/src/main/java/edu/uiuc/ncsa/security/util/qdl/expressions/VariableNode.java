package edu.uiuc.ncsa.security.util.qdl.expressions;

import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  4:28 PM
 */
public class VariableNode extends ExpressionImpl {
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    SymbolTable symbolTable = null;

    public String getVariableReference() {
        return variableReference;
    }

    public void setVariableReference(String variableReference) {
        this.variableReference = variableReference;
    }

    String variableReference = null;

    public VariableNode(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    public VariableNode(String variableReference, SymbolTable symbolTable) {
        this(symbolTable);
        this.variableReference = variableReference;
    }

    @Override
    public Object evaluate() {
        // The contract is that variables resolve to their values when asked and are not mutable.
        // Might change that..
        result = symbolTable.resolveValue(variableReference);
        evaluated = true;
        resultType = Constant.getType(result);
        return result;
    }
}
