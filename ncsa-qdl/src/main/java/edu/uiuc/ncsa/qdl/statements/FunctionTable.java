package edu.uiuc.ncsa.qdl.statements;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:52 AM
 */
public class FunctionTable extends HashMap<String, FunctionRecord> {
    String munger = "$$$";

    public String createKey(String name, int argCount) {
        return name + munger + argCount;

    }
    public String createKey(FunctionRecord fr) {
        return createKey(fr.name, fr.getArgCount());
    }

    public FunctionRecord put(FunctionRecord value) {
        return super.put(createKey(value), value);
    }
    public FunctionRecord get(String key, int argCount){
        return super.get(createKey(key,argCount));
    }

    public boolean containsKey(String var, int argCount) {
        return super.containsKey(createKey(var, argCount));
    }
}
