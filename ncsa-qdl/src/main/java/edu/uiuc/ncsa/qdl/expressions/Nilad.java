package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;

/**
 * This would be an <b>operator</b> that takes no arguments. Niladic user defined
 * functions (no arguments) abount. It is possible to have a niladic operator
 * in QDL, but as of yet there is no example of one. One <i>possible</i> way would be
 * to use these as control symbols, such a special operator that means to, say, open
 * an editor window. These would also require special handling inthe parser.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:36 PM
 */
public class Nilad extends ExpressionImpl{
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
