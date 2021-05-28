package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/27/21 at  5:13 PM
 */
public class AltIfExpressionNode extends ExpressionImpl {
    public StatementWithResultInterface getIF() {
        return getArguments().get(0);
    }

    public void setIF(StatementWithResultInterface x) {
        getArguments().set(0, x);
    }

    public StatementWithResultInterface getTHEN() {
        return getArguments().get(1);
    }

    public void setTHEN(StatementWithResultInterface x) {
        getArguments().set(1, x);
    }

    public StatementWithResultInterface getELSE() {
        return getArguments().get(2);
    }

    public void setELSE(StatementWithResultInterface x) {
        getArguments().set(2, x);
    }

    @Override
    public ArrayList<StatementWithResultInterface> getArguments() {
        if (arguments == null || arguments.size()!= 3) {
            arguments = new ArrayList<>(3);
            arguments.add(null);
            arguments.add(null);
            arguments.add(null);
        }
        return arguments;
    }

    @Override
    public Object evaluate(State state) {
        Object arg0 = getIF().evaluate(state);
        if (!(arg0 instanceof Boolean)) {
            throw new IllegalArgumentException("error: expression requires a boolean as its first argument");
        }
        Boolean flag = (Boolean) arg0;
        Object arg1;
        if (flag) {
            arg1 = getTHEN().evaluate(state);
        } else {
            arg1 = getELSE().evaluate(state);
        }
        setResult(arg1);
        setResultType(Constant.getType(arg1));
        setEvaluated(true);
        return arg1;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        return null;
    }
}
