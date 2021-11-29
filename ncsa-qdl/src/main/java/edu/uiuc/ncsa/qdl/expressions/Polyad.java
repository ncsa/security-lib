package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.functions.FKey;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.statements.TokenPosition;

/**
 * For multiple arguments. This is used, e.g., for all functions.
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  6:12 AM
 */
public class Polyad extends ExpressionImpl {
    public Polyad() {
    }

    public Polyad(TokenPosition tokenPosition) {
        super(tokenPosition);
    }

    /**
     * Human readable name for this. 
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public Polyad(String name) {
        this.name = name;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(boolean builtIn) {
        this.builtIn = builtIn;
    }

    boolean builtIn = true;

    public Polyad(int operatorType) {
        super(operatorType);
    }

    @Override
    public Object evaluate(State state) {
        // Some finagling. If this is being evaluated in a module, check that
        // there is an override in place. If not, kick it up to the main system
        // (so evalute with no alias).
        if(isInModule()){
            if(state.getFTStack().containsKey(new FKey(getName(), getArgCount()))) {
                state.getMetaEvaluator().evaluate(getAlias(), this, state);
            }else{
                state.getMetaEvaluator().evaluate(this, state);
            }
        }else {
            state.getMetaEvaluator().evaluate(this, state);
        }
         return getResult();
    }
    public void addArgument(StatementWithResultInterface expr){
        getArguments().add(expr);
    }
    @Override
    public String toString() {
        return "Polyad[" +
                "name='" + name + '\'' +
                ", operatorType=" + operatorType +
                ']';
    }


    @Override
    public ExpressionNode makeCopy() {
        Polyad polyad = new Polyad(operatorType);
        polyad.setName(getName());
        for(StatementWithResultInterface arg: getArguments()){
            polyad.addArgument(arg.makeCopy());
        }
        return polyad;
    }
}
