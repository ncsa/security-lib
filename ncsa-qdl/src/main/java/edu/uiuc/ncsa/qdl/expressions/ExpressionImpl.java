package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:15 PM
 */
public abstract class ExpressionImpl implements ExpressionNode {
    public ExpressionImpl() {
    }


    public ExpressionImpl(int operatorType) {
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

    protected ArrayList<ExpressionNode> arguments = new ArrayList<>();

    @Override
    public ArrayList<ExpressionNode> getArgumments() {
        return arguments;
    }

    public Object evalArg(int index, State state){
        return getArgumments().get(index).evaluate(state);
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

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    String sourceCode;

    @Override
    public String toString() {
        return "exp=" +
                "\"" + sourceCode + '\"';
    }
}
