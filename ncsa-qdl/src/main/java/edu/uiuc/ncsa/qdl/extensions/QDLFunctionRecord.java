package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.statements.FunctionRecord;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  12:03 PM
 */
public class QDLFunctionRecord extends FunctionRecord {
    protected int argCount = 0;

    @Override
    public int getArgCount() {
        return argCount;
    }

    public QDLFunction qdlFunction = null;
}
