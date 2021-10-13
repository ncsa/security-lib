package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.util.List;

/**
 * Just a pointer to the function. Just has the name.
 * <p>Created by Jeff Gaynor<br>
 * on 3/14/21 at  3:26 PM
 */
public class FunctionReferenceNode implements StatementWithResultInterface {
  //  boolean inModule = false;

    @Override
    public boolean isInModule() {
        return alias!=null;
    }
String alias = null;;

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }
/*
    @Override
    public void setInModule(boolean inModule) {
                     this.inModule = inModule;
    }
*/

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    String functionName;
    Object result;
    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object object) {
                 this.result = object;
    }

    int resultType = Constant.UNKNOWN_TYPE;
    @Override
    public int getResultType() {
        return resultType;
    }

    @Override
    public void setResultType(int type) {
               this.resultType = type;
    }

    boolean evaluated = false;

    @Override
    public boolean isEvaluated() {
        return evaluated;
    }

    @Override
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    @Override
    public Object evaluate(State state) {
        return null;
    }

    List<String> source;

    @Override
    public List<String> getSourceCode() {
        return null;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {

    }

    @Override
    public StatementWithResultInterface makeCopy() {
        return null;
    }
}
