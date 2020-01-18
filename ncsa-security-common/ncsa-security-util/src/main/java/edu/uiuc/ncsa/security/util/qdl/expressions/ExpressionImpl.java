package edu.uiuc.ncsa.security.util.qdl.expressions;

import edu.uiuc.ncsa.security.util.qdl.variables.Constant;

import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:15 PM
 */
public abstract class ExpressionImpl implements ExpressionNode {
    protected ExpressionImpl() {
    }

    public ExpressionImpl(OpEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public ExpressionImpl(OpEvaluator evaluator, int operatorType) {
        this(evaluator);
        this.operatorType = operatorType;
    }

    /**
     * The valence is the number of arguments this expression allows. <br/><br/>
     * 0 = niladic
     * 1 = monadic
     * 2 = dyadic
     * 3 = polyadic (more than 2)
     */
    protected int valence = 0;
    protected boolean isRoot = false;

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    protected ArrayList<ExpressionNode> arguments = new ArrayList<>();

    @Override
    public ArrayList<ExpressionNode> getArgumments() {
        return arguments;
    }

    @Override
    public void setArguments(ArrayList<ExpressionNode> arguments) {
        this.arguments = arguments;
    }

    protected Object result;

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    @Override
    public Object getResult() {
        if (!evaluated) {
            throw new UnevaluatedExpressionException();
        }
        return result;
    }

    int resultType = Constant.UNKNOWN_TYPE;

    @Override
    public int getResultType() {
        return resultType;
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    boolean evaluated = false;

    public void setOperatorType(int operatorType) {
        this.operatorType = operatorType;
    }

    int operatorType = OpEvaluator.UNKNOWN_VALUE;

    @Override
    public int getOperatorType() {
        return operatorType;
    }

    public OpEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(OpEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    OpEvaluator evaluator;

}
