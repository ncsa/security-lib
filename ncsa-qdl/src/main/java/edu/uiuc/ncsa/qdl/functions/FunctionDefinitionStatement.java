package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/20/21 at  8:46 AM
 */
public class FunctionDefinitionStatement implements Statement {
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

    @Override
    public Object evaluate(State state) {
        state.getFTStack().put(functionRecord);
/*
        FunctionReferenceNode functionReferenceNode = new FunctionReferenceNode();
        functionReferenceNode.setFunctionName("");
*/
        return QDLNull.getInstance(); // for now
    }

    List<String> source;

    @Override
    public List<String> getSourceCode() {
        return source;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.source = sourceCode;
    }
}
