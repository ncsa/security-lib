package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents when the user explicitly sets a variable to null. This just exists. It does nothing.
 * Note that this is a static class -- there is exactly one null object in QDL.
 * <p>Created by Jeff Gaynor<br>
 * on 4/9/20 at  9:08 AM
 */
public class QDLNull extends ConstantNode {
    static QDLNull qdlNull = null;

    public static QDLNull getInstance() {
        if (qdlNull == null) {
            qdlNull = new QDLNull();
        }
        return qdlNull;
    }

    private QDLNull() {
        super(null, Constant.NULL_TYPE);  // Have to set since the result is QDLNode which does not exist yet.
        List<String> source = new ArrayList<>();
        if (State.isPrintUnicode()) {
            source.add("∅");
        } else {
            source.add("null");
        }
        setSourceCode(source);
        setResultType(Constant.NULL_TYPE);
        setResult(this);
        setEvaluated(true);
    }

    @Override
    public String toString() {
        if (State.isPrintUnicode()) {
            return "∅";
        }
        return "null";
    }
}
