package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * To treat defined lambda expressions they must be {@link edu.uiuc.ncsa.qdl.expressions.ExpressionNode}s
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/21 at  8:45 AM
 */
public class LambdaDefinitionNode extends ExpressionImpl {
    @Override
    public Object evaluate(State state) {
        state.getFTStack().put(functionRecord);
        FunctionReferenceNode functionReferenceNode = new FunctionReferenceNode();
        functionReferenceNode.setFunctionName("");
        setEvaluated(true);
        setResultType(Constant.FUNCTION_TYPE);
        setResult(functionRecord);
        return functionRecord; // for now
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        return null;
    }

    public boolean isLambda() {
        return lambda;
    }

    public void setLambda(boolean lambda) {
        this.lambda = lambda;
    }

    boolean lambda = false;

    public FunctionRecord getFunctionRecord() {
        return functionRecord;
    }

    public void setFunctionRecord(FunctionRecord functionRecord) {
        this.functionRecord = functionRecord;
    }

    FunctionRecord functionRecord;
}
