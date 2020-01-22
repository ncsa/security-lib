package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Statement;

import java.util.ArrayList;

/**
 * This class mostly manages the structure of expressions (so arguments are the children) and
 * evluating them is delegating the result to the {@link OpEvaluator} class.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:02 PM
 */
public interface ExpressionNode extends Statement {

    ArrayList<ExpressionNode> getArgumments(); // need this to preserver order of lists

    void setArguments(ArrayList<ExpressionNode> arguments);

    int valence = 0;

    int getOperatorType();

    void setOperatorType(int operatorType);

    Object getResult();

    void setResult(Object object);

    int getResultType();

    void setResultType(int type);

    Object evaluate(State state);

    public ExpressionNode makeCopy(); // would prefer clone, but there is a conflict in packages because it has protected access
}
