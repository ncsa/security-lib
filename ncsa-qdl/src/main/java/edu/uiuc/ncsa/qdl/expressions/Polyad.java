package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;

/**
 * For multiple arguments. This is used, e.g., for all functions.
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  6:12 AM
 */
public class Polyad extends ExpressionImpl {
    public Polyad() {
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



    public Polyad(int operatorType) {
        super(operatorType);
    }

    @Override
    public Object evaluate(State state) {
         state.getMetaEvaluator().evaluate(this, state);
         return getResult();
    }
    public void addArgument(ExpressionNode expr){
        getArgumments().add(expr);
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
        for(ExpressionNode arg: getArgumments()){
            polyad.addArgument(arg.makeCopy());
        }
        return polyad;
    }
}
