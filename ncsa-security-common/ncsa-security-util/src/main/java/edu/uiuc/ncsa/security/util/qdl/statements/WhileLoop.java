package edu.uiuc.ncsa.security.util.qdl.statements;

import edu.uiuc.ncsa.security.util.qdl.expressions.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  4:44 PM
 */
public class WhileLoop implements Statement {
    ExpressionNode conditional;

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public ExpressionNode getConditional() {
        return conditional;
    }

    public void setConditional(ExpressionNode conditional) {
        this.conditional = conditional;
    }

    List<Statement> statements = new ArrayList<>();

    @Override
    public Object evaluate() {
        while((Boolean)conditional.evaluate()){
            for(Statement statement: getStatements()){
                statement.evaluate();
            }
        }
        return Boolean.TRUE;
    }
}
