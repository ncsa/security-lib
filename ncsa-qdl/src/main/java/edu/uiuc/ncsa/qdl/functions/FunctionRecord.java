package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.statements.Statement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:48 AM
 */
public class FunctionRecord implements Serializable, Cloneable {
    public static int FREF_ARG_COUNT = -10;
    public String name;
    public List<String> sourceCode = new ArrayList<>();
    public List<String> documentation = new ArrayList<>();
    public List<Statement> statements = new ArrayList<>();
    public List<String> argNames = new ArrayList<>();
    public boolean isFuncRef = false;
    public String fRefName = null;
    public int getArgCount(){
        if(isFuncRef) return FREF_ARG_COUNT;
        return argNames.size();
    }

    @Override
    public String toString() {
        return "FunctionRecord{" +
                "name='" + name + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", statements=" + statements +
                ", argNames=" + argNames +
                ", arg count = " + getArgCount() +
                ", f_ref? = " + isFuncRef +
                (fRefName==null?"":", ref_name = \"" + fRefName + "\"") +
                '}';
    }

    @Override
    public FunctionRecord clone() throws CloneNotSupportedException {
        FunctionRecord functionRecord = new FunctionRecord();
        functionRecord.name = name;
        functionRecord.sourceCode = sourceCode;
        functionRecord.documentation = documentation;
        functionRecord.statements = statements;
        functionRecord.argNames = argNames;
        return functionRecord;
    }
}
