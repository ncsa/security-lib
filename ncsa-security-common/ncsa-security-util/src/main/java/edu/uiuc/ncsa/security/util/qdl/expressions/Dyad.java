package edu.uiuc.ncsa.security.util.qdl.expressions;

import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:47 PM
 */
public class Dyad extends ExpressionImpl {
    public Dyad(OpEvaluator evaluator) {
        super(evaluator);
        valence = 2;
        arguments = new ArrayList<>(valence);
    }

    public Dyad(OpEvaluator evaluator,
                int operatorType) {
        super(evaluator, operatorType);
        valence = 2;
        arguments = new ArrayList<>(valence);
    }

    public Dyad(OpEvaluator evaluator,
                int operatorType,
                ExpressionNode leftNode,
                ExpressionNode rightNode) {
        this(evaluator, operatorType);
        getArgumments().add(leftNode);
        getArgumments().add(rightNode);
    }

    @Override
    public Object evaluate() {
        getEvaluator().evaluate(this);
        return getResult();
    }

    public ExpressionNode getLeftArgument() {
        return getArgumments().get(0);
    }

    public ExpressionNode getRightArgument() {
        return getArgumments().get(1);
    }

    public void setLeftArgument(ExpressionNode node) {
        getArgumments().add(0, node);
    }

    public void setRightArgument(ExpressionNode node) {
        getArgumments().add(1, node);

    }
}
