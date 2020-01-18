package edu.uiuc.ncsa.security.util.qdl.expressions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  4:08 PM
 */
public class ConstantNode extends ExpressionImpl {
    @Override
    public Object evaluate() {
        return result;
    }

    public ConstantNode(Object result, int resultType) {
        valence = 0;
        this.result = result;
        this.resultType = resultType;
        evaluated = true; //trivally
    }

}
