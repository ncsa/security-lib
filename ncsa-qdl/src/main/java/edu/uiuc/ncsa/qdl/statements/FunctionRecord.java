package edu.uiuc.ncsa.qdl.statements;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:48 AM
 */
public class FunctionRecord {
    public String name;
    public String sourceCode;
    public List<Statement> statements = new ArrayList<>();
    public List<String> argNames = new ArrayList<>();
    public int getArgCount(){
        return argNames.size();
    }

    @Override
    public String toString() {
        return "FunctionRecord{" +
                "name='" + name + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", statements=" + statements +
                ", argNames=" + argNames +
                '}';
    }
}
