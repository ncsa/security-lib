package edu.uiuc.ncsa.security.util.qdl.statements;

import edu.uiuc.ncsa.security.util.qdl.expressions.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  3:34 PM
 */
public class ConditionalStatement implements Statement {
    ExpressionNode conditional;

    public ExpressionNode getConditional() {
        return conditional;
    }

    public void setConditional(ExpressionNode conditional) {
        this.conditional = conditional;
    }

    public List<Statement> getIfArguments() {
        return ifArguments;
    }

    public void setIfArguments(List<Statement> ifArguments) {
        this.ifArguments = ifArguments;
    }

    public List<Statement> getElseArguments() {
        return elseArguments;
    }

    public void setElseArguments(List<Statement> elseArguments) {
        this.elseArguments = elseArguments;
    }

    List<Statement> ifArguments = new ArrayList<>();
    List<Statement> elseArguments = new ArrayList<>();

    @Override
    public Object evaluate() {
        Boolean result = false;
         if((Boolean)getConditional().evaluate()){
             result = true;
             for(Statement arg : ifArguments ){
                 arg.evaluate();
             }
         }else{
             if(elseArguments != null) {
                 for (Statement arg : elseArguments) {
                     arg.evaluate();
                 }
             }

         }
        return result;
    }
}
