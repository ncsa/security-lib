package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:15 PM
 */
public abstract class ExpressionImpl implements ExpressionNode {
    public ExpressionImpl() {
    }

/*    Module module = null;

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public void setModule(Module newModule) {
         this.module = newModule;
        for(int i = 0; i < getArgCount(); i++){
            getArguments().get(i).setModule(newModule);
        }

    }

    @Override
    public boolean hasModule() {
        return module!=null;
    }*/

 //   boolean inModule = false;

    @Override
    public boolean isInModule() {
        return alias!=null;
    }

    /*@Override
    public void setInModule(boolean inModule) {
        this.inModule = inModule;
    }*/

    public ExpressionImpl(int operatorType) {
        this.operatorType = operatorType;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    String alias = null;

    @Override
    public int getArgCount() {
        return getArguments().size();
    }

    /**
     * The valence is the number of arguments this expression allows. <br/><br/>
     * 0 = niladic
     * 1 = monadic
     * 2 = dyadic
     * 3 = polyadic (more than 2)
     */
    protected int valence = 0;

    protected ArrayList<StatementWithResultInterface> arguments = new ArrayList<>();

    @Override
    public ArrayList<StatementWithResultInterface> getArguments() {
        return arguments;
    }

    @Override
    public StatementWithResultInterface getArgAt(int index) {
        if ((index < 0) || (getArgCount() <= index)) {
            return null;
        }
        return getArguments().get(index);
    }

    public Object evalArg(int index, State state) {
        return getArguments().get(index).evaluate(state);
    }

    @Override
    public void setArguments(ArrayList<StatementWithResultInterface> arguments) {
        this.arguments = arguments;
    }

    protected Object result;

    @Override
    public boolean isEvaluated() {
        return evaluated;
    }

    @Override
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    @Override
    public Object getResult() {
        if (!evaluated) {
            throw new UnevaluatedExpressionException("source='" + (getSourceCode() == null ? "(none)" : getSourceCode()) + "'");
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
    public List<String> getSourceCode() {
        if (sourceCode == null) {
            sourceCode = new ArrayList<>();
        }
        return sourceCode;
    }

    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    List<String> sourceCode;

    @Override
    public String toString() {
        return "source=" +
                "\"" + sourceCode + '\"';
    }
}
