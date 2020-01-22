package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.statements.Statement;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/20 at  6:18 AM
 */
public class StatementRecord extends ParseRecord {
    public StatementRecord(ParseTree parseTree, Statement statement) {
        super(parseTree);
        this.statement = statement;
    }

    Statement statement;

    @Override
    public String toString() {
        return "StatementRecord[" +
                "identifier='" + identifier + '\'' +
                ", parentIdentifier='" + parentIdentifier + '\'' +
                ", statement=" + statement +
                ']';
    }
}
