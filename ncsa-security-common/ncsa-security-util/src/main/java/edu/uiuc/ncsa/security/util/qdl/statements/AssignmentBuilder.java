package edu.uiuc.ncsa.security.util.qdl.statements;

import edu.uiuc.ncsa.security.util.qdl.expressions.Assignment;
import edu.uiuc.ncsa.security.util.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  11:02 AM
 */
public class AssignmentBuilder extends StatementBuilder {
    public AssignmentBuilder(SymbolTable symbolTable, OpEvaluator opEvaluator) {
        super(symbolTable, opEvaluator);
    }

    protected Assignment getAssignment() {
        return (Assignment) statement;
    }

    @Override
    public Statement createStatement() {

        statement = new Assignment(symbolTable);
        return (Assignment) statement;
    }

    public Assignment setVariableReference(String var) {
        getAssignment().setVariableReference(var);
        return (Assignment) statement;
    }
    public Assignment setArgument(ExpressionNode node){
        getAssignment().setArgument(node);
        return (Assignment)statement;
    }
}
