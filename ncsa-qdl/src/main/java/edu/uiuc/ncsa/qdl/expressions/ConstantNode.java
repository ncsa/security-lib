package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import java.math.BigDecimal;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  4:08 PM
 */
public class ConstantNode extends ExpressionImpl {
    @Override
    public Object evaluate(State state) {
        return result;
    }

    public ConstantNode(String result) {
        this(result, Constant.STRING_TYPE);
    }

    public ConstantNode(Long result) {
        this(result, Constant.LONG_TYPE);
    }

    public ConstantNode(BigDecimal result) {
        this(result, Constant.DECIMAL_TYPE);
    }

    public ConstantNode(Boolean result) {
        this(result, Constant.BOOLEAN_TYPE);
    }

    public ConstantNode(QDLNull result) {
        this(result, Constant.NULL_TYPE);
    }

    public ConstantNode(Object result) {
        this(result, Constant.getType(result));
    }

    public ConstantNode(Object result, int resultType) {
        valence = 0;
        this.result = result;
        this.resultType = resultType;
        evaluated = true; //trivally

        getSourceCode().add(result == null ? "null" : result.toString());
    }

    @Override
    public ExpressionNode makeCopy() {
        ConstantNode constantNode = new ConstantNode(result, resultType);
        return constantNode;
    }
}
