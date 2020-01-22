package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  4:28 PM
 */
public class VariableNode extends ExpressionImpl {
    public VariableNode() {
           super();
    }

    public String getVariableReference() {
        return variableReference;
    }

    public void setVariableReference(String variableReference) {
        this.variableReference = variableReference;
    }

    String variableReference = null;

    public VariableNode(String variableReference) {
        this.variableReference = variableReference;
    }

    @Override
    public Object evaluate(State state) {
        // The contract is that variables resolve to their values when asked and are not mutable.
        // Might change that..
        result = state.getSymbolStack().resolveValue(variableReference);
        evaluated = true;
        resultType = Constant.getType(result);
        return result;
    }

    @Override
    public ExpressionNode makeCopy() {
        VariableNode variableNode = new VariableNode(variableReference);
        return variableNode;
    }
}
