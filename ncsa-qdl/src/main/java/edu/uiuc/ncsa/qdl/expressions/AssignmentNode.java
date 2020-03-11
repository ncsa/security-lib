package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * In order to use chained assignments (a := b += c -= d ...) we need to wrap an
 * assignment in an {@link ExpressionNode} so it can get daisy chained. There is not
 * a lot that this node has to do, but if we don't we have to substantially change the
 * machinery of assignments to allow them to operate in this fashion.
 * <p>Created by Jeff Gaynor<br>
 * on 3/11/20 at  7:14 AM
 */
public class AssignmentNode extends ExpressionImpl {
    public AssignmentNode(Assignment assignment) {
        this.assignment = assignment;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    Assignment assignment;

    @Override
    public ExpressionNode makeCopy() {
       throw new QDLException("Error: Do we even use this?");
    }

    @Override
    public Object evaluate(State state) {
        result = getAssignment().evaluate(state);
        evaluated = true;
        resultType = Constant.getType(result);
        return result;
    }
}
