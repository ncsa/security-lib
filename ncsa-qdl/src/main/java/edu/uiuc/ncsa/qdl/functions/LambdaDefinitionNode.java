package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

/**
 * To treat defined lambda expressions they must be {@link edu.uiuc.ncsa.qdl.expressions.ExpressionNode}s
 * This wraps a {@link FunctionDefinitionStatement}, which cannot be replaced. The strategy
 * is to swap out FDS for these at very specific places to allow for passing lambdas as arguments.
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/21 at  8:45 AM
 */
public class LambdaDefinitionNode extends ExpressionImpl {

    public boolean hasName(){
        if(functionRecord == null){
            return false;
        }
        return functionRecord.hasName();
    }
    @Override
    public Object evaluate(State state) {
        state.getFTStack().put(functionRecord);
        return null; // for now
    }


    public LambdaDefinitionNode(FunctionDefinitionStatement fds) {
        functionRecord = fds.getFunctionRecord();
        setLambda(fds.isLambda());
        setSourceCode(fds.getSourceCode());
    }

    public LambdaDefinitionNode(int operatorType) {
        super(operatorType);
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        throw new NotImplementedException();
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
