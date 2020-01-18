package edu.uiuc.ncsa.security.util.qdl.expressions;

import java.util.ArrayList;

/**
 * A post or prefix operator, such a logical not or ++. The default is that this is postfix.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:43 PM
 */
public class Monad extends ExpressionImpl {
    public Monad(OpEvaluator evaluator) {
        super(evaluator);
        valence = 1;
        arguments = new ArrayList<>(valence);
    }

    public Monad(OpEvaluator evaluator, boolean isPostFix) {
        this(evaluator);
        this.postFix = isPostFix;
    }

    public Monad(OpEvaluator evaluator, int operatorType, boolean isPostFix) {
        this(evaluator, isPostFix);
        this.operatorType = operatorType;
    }

    /**
     * Constructor for making a post fix monad.
     *
     * @param evaluator
     * @param operatorType
     * @param argument
     */
    public Monad(OpEvaluator evaluator, int operatorType, ExpressionNode argument) {
        this(evaluator, operatorType, true);
        getArgumments().add(0, argument);
    }

    /**
     * Constructor for specifying the type of operator.
     *
     * @param evaluator
     * @param operatorType
     * @param argument
     * @param isPostFix
     */
    public Monad(OpEvaluator evaluator, int operatorType, ExpressionNode argument, boolean isPostFix) {
        this(evaluator, operatorType, argument);
        this.postFix = isPostFix;
    }

    public boolean isPostFix() {
        return postFix;
    }

    public void setPostFix(boolean postFix) {
        this.postFix = postFix;
    }

    boolean postFix = true; //default

    @Override
    public Object evaluate() {
        getEvaluator().evaluate(this);
        return getResult();
    }

    public ExpressionNode getArgument() {
        return getArgumments().get(0);
    }

    public void setArgument(ExpressionNode node) {
        if (getArgumments().size() == 0) {
            getArgumments().add(node);
            return;
        }
        arguments = new ArrayList<>();
        arguments.add(node);
    }
}
