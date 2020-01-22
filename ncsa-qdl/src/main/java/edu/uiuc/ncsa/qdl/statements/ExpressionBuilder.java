package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.SymbolTable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  11:24 AM
 */
public class ExpressionBuilder extends StatementBuilder {
    public ExpressionBuilder(SymbolTable symbolTable, OpEvaluator opEvaluator) {
        super(symbolTable, opEvaluator);
    }
      protected ExpressionNode getENode(){
        return (ExpressionNode)statement;
      }
    @Override
    public Statement createStatement() {
        return null;
    }
}
