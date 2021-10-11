package edu.uiuc.ncsa.qdl.extensions.example;

import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;
import edu.uiuc.ncsa.qdl.state.State;

import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator.getOperator;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/7/21 at  4:40 PM
 */
public class FunctionReferenceExample implements QDLFunction {
    @Override
    public String getName() {
        return "f_ref";
    }

    @Override
    public int[] getArgCount() {
        return new int[]{2};
    }

    @Override
    public Object evaluate(Object[] objects, State state) {
        ExpressionImpl expression = getOperator(state, (FunctionReferenceNode) objects[0], 1);
        expression.getArguments().add( new ConstantNode(objects[1]));
        return expression.evaluate(state);
    }

    @Override
    public List<String> getDocumentation(int argCount) {
        List<String> doxx = new ArrayList<>();

        doxx.add(getName() + "(@f, x) - simple example for a Java extension that processes f(x) using a function reference.");
        doxx.add("E.g.");
        doxx.add("    " + getName() + "(@cos, pi()/7)");
        doxx.add("0.900968867902419");
        return doxx;
    }
}
