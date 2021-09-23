package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/21 at  3:44 PM
 */
public class ParenthesizedExpression implements ExpressionNode {

    boolean inModule = false;

    @Override
    public boolean isInModule() {
        return inModule;
    }

    @Override
    public void setInModule(boolean inModule) {
                     this.inModule = inModule;
    }
    public StatementWithResultInterface getExpression() {
        if(getArgCount() == 0){
            return null;
        }
        return getArguments().get(0);
    }

    @Override
    public StatementWithResultInterface getArgAt(int index) {
        if((index < 0)||(getArgCount() <= index)){
            return null;
        }
        return getArguments().get(index);
    }

    public void setExpression(StatementWithResultInterface expression) {
        if(getArgCount() == 0){
            getArguments().add(expression);
        }else{
            getArguments().set(0,expression);
        }
    }


    @Override
    public Object getResult() {
        return getExpression().getResult();
    }

    @Override
    public void setResult(Object object) {
        getExpression().setResult(object);
    }

    @Override
    public int getResultType() {
        return getExpression().getResultType();
    }

    @Override
    public void setResultType(int type) {
        getExpression().setResultType(type);
    }

    @Override
    public boolean isEvaluated() {
        return getExpression().isEvaluated();
    }

    @Override
    public void setEvaluated(boolean evaluated) {
        getExpression().setEvaluated(evaluated);
    }

    @Override
    public Object evaluate(State state) {
        return getExpression().evaluate(state);
    }

    List<String> doxx;

    @Override
    public List<String> getSourceCode() {
        if (doxx == null) {
            doxx = new ArrayList<>();
        }
        return doxx;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        doxx = sourceCode;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        ParenthesizedExpression parenthesizedExpression = new ParenthesizedExpression();
        parenthesizedExpression.setExpression(getExpression().makeCopy());
        return parenthesizedExpression;
    }

    ArrayList<StatementWithResultInterface> args = new ArrayList<>();
    @Override
    public ArrayList<StatementWithResultInterface> getArguments() {
        return args;
    }

    @Override
    public void setArguments(ArrayList<StatementWithResultInterface> arguments) {
                  args =arguments;
    }

    @Override
    public int getArgCount() {
        return args.size();
    }

    @Override
    public int getOperatorType() {
        return OpEvaluator.UNKNOWN_VALUE;
    }

    @Override
    public void setOperatorType(int operatorType) {

    }
}
