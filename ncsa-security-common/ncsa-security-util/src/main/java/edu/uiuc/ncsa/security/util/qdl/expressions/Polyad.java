package edu.uiuc.ncsa.security.util.qdl.expressions;

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
    AbstractFunctionEvaluator fEvaluator = null;
    public Polyad(OpEvaluator evaluator, AbstractFunctionEvaluator fEvaluator) {
        super(evaluator);
        this.fEvaluator = fEvaluator;
    }

    public Polyad(OpEvaluator evaluator, AbstractFunctionEvaluator fEvaluator, int operatorType) {
        super(evaluator, operatorType);
        this.fEvaluator = fEvaluator;
    }

    @Override
    public Object evaluate() {
         fEvaluator.evaluate(this);
         return getResult();
    }
    public void addArgument(ExpressionImpl expr){
        getArgumments().add(expr);
    }
    @Override
    public String toString() {
        return "Polyad[" +
                "name='" + name + '\'' +
                ", operatorType=" + operatorType +
                ']';
    }
}
