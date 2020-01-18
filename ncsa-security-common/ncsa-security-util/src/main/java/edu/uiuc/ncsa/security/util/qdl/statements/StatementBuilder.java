package edu.uiuc.ncsa.security.util.qdl.statements;

import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;

/** This class will build statements. You call the correct component as you go along and the result is a statement.
 * It creates a single statement.
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  10:50 AM
 */
public abstract class StatementBuilder {
    SymbolTable symbolTable;
    OpEvaluator opEvaluator;

    public StatementBuilder(SymbolTable symbolTable, OpEvaluator opEvaluator) {
        this.symbolTable = symbolTable;
        this.opEvaluator = opEvaluator;
    }

    Statement statement;
   public abstract Statement createStatement();
}
