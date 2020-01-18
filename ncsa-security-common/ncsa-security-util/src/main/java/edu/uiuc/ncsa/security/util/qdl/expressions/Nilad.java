package edu.uiuc.ncsa.security.util.qdl.expressions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:36 PM
 */
public class Nilad extends ExpressionImpl {
    public Nilad(OpEvaluator evaluator, int operatorType) {
        super(evaluator, operatorType);
        valence = 0;
    }


    @Override
    public Object evaluate() {
        getEvaluator().evaluate(this);
        return getResult();
    }
}
