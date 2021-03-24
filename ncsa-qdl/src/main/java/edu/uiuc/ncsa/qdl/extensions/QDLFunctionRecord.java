package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.functions.FunctionRecord;

/**
 * This is needed for internal bookkeeping. It will be created for you are needed by the {@link JavaModule},
 * s you don't need to do anything with it directly.
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

    @Override
    public QDLFunctionRecord clone() throws CloneNotSupportedException {
        QDLFunctionRecord functionRecord = new QDLFunctionRecord();
        functionRecord.name = name;
        functionRecord.sourceCode = sourceCode;
        functionRecord.documentation = documentation;
        functionRecord.statements = statements;
        functionRecord.argNames = argNames;
        functionRecord.argCount = argCount;
        functionRecord.qdlFunction = qdlFunction;
        return functionRecord;
    }


}
