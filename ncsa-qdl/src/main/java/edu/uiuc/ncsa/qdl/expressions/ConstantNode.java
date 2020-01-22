package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  4:08 PM
 */
public class ConstantNode extends ExpressionImpl {
    @Override
    public Object evaluate(State state) {
        return result;
    }

    public ConstantNode(Object result, int resultType) {
        valence = 0;
        this.result = result;
        this.resultType = resultType;
        evaluated = true; //trivally
    }

    @Override
    public ExpressionNode makeCopy() {
        ConstantNode constantNode = new ConstantNode(result,resultType);
        return constantNode;
    }
}
