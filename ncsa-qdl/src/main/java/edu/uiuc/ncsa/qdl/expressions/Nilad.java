package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:36 PM
 */
public class Nilad extends ExpressionImpl {
    public Nilad(int operatorType) {
        super(operatorType);
        valence = 0;
    }


    @Override
    public Object evaluate(State state) {
        state.getOpEvaluator().evaluate(this, state);
        return getResult();
    }

    @Override
    public ExpressionNode makeCopy() {
        Nilad nilad = new Nilad(operatorType);
        return nilad;
    }
}
