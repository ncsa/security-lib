package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A post or prefix operator, such a logical not or ++. The default is that this is postfix.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:43 PM
 */
public class Monad extends ExpressionImpl {
    public Monad(boolean postFix) {
        this.postFix = postFix;
    }


    public Monad( int operatorType, boolean isPostFix) {
        this(isPostFix);
        this.operatorType = operatorType;
    }


    /**
     * Constructor for making a post fix monad.
     *
     * @param operatorType
     * @param argument
     */
    public Monad(int operatorType, ExpressionNode argument) {
        this(operatorType, true);
        getArguments().add(0, argument);
    }

    /**
     * Constructor for specifying the type of operator.
     *
     * @param operatorType
     * @param argument
     * @param isPostFix
     */
    public Monad(int operatorType, ExpressionNode argument, boolean isPostFix) {
        this(operatorType, argument);
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
    public Object evaluate(State state) {
        state.getOpEvaluator().evaluate(this, state);
        return getResult();
    }

    public ExpressionNode getArgument() {
        return getArguments().get(0);
    }

    public void setArgument(ExpressionNode node) {
        if (getArgCount() == 0) {
            getArguments().add(node);
            return;
        }
        arguments = new ArrayList<>();
        arguments.add(node);
    }

    @Override
    public ExpressionNode makeCopy() {
        Monad monad = new Monad(operatorType,postFix);
        monad.setArgument(getArgument().makeCopy());
        return monad;
    }
    public boolean isSigned(){
        return operatorType == OpEvaluator.MINUS_VALUE;
    }
    public Long getSignedLongValue(){
        if(!evaluated){
            throw new UnevaluatedExpressionException("Error: unevaulated expression");
        }
        if(resultType != Constant.LONG_TYPE){
            throw new IllegalArgumentException("error: not a long");
        }
        return (isSigned()?-1L:1L)* (Long)getResult();
    }
    public BigDecimal getSignedDecimalValue(){
        if(!evaluated){
            throw new UnevaluatedExpressionException("Error: unevaulated expression");
        }
        if(resultType != Constant.DECIMAL_TYPE){
            throw new IllegalArgumentException("error: not a long");
        }
        return isSigned()?((BigDecimal)getResult()).negate():(BigDecimal)getResult();
    }

}
