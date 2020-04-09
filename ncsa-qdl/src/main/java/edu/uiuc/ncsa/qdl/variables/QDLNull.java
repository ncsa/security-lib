package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.expressions.ConstantNode;

/**
 * This represents when the user explicitly sets a variable to null. This just exists. It does nothing.
 * <p>Created by Jeff Gaynor<br>
 * on 4/9/20 at  9:08 AM
 */
public class QDLNull extends ConstantNode {
    public QDLNull() {
        super(null, Constant.NULL_TYPE);  // Have to set since the result is QDLNode which does not exist yet.
        setSourceCode("null");
        setResultType(Constant.NULL_TYPE);
        setResult(this);
        setEvaluated(true);
    }

    @Override
    public String toString() {
        return "null";
    }
}
