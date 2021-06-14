package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;

import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:47 PM
 */
public class Dyad extends ExpressionImpl {


    public Dyad(int operatorType) {
        super(operatorType);
        valence = 2;
        arguments = new ArrayList<>(valence);
    }


    public Dyad(int operatorType,
                ExpressionNode leftNode,
                ExpressionNode rightNode) {
        this(operatorType);
        getArguments().add(leftNode);
        getArguments().add(rightNode);
    }

  

    @Override
    public Object evaluate(State state) {
        state.getOpEvaluator().evaluate(this, state);
        return getResult();
    }

    public StatementWithResultInterface getLeftArgument() {
        return getArguments().get(0);
    }

    public StatementWithResultInterface getRightArgument() {
        return getArguments().get(1);
    }

    public void setLeftArgument(StatementWithResultInterface node) {
        if(getArguments().size()==0){
            getArguments().add(node);
        }else {
            getArguments().set(0, node);
        }
    }

    public void setRightArgument(StatementWithResultInterface node) {
        switch(getArguments().size()){
            case 0:
                getArguments().add(null);
            case 1:
                getArguments().add(node);
                break;
            case 2:
                getArguments().set(1, node);
        }

    }

    @Override
    public ExpressionNode makeCopy() {
        Dyad dyad = new Dyad( operatorType);
        dyad.setLeftArgument(getLeftArgument().makeCopy());
        dyad.setRightArgument(getRightArgument().makeCopy());
        return dyad;
    }
}
