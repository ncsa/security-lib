package edu.uiuc.ncsa.qdl.statements;

import java.util.HashMap;
import java.util.TreeSet;

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

    public FunctionRecord get(String key, int argCount) {
        return super.get(createKey(key, argCount));
    }

    public boolean containsKey(String var, int argCount) {
        return super.containsKey(createKey(var, argCount));
    }

    public TreeSet<String> listFunctions() {
        TreeSet<String> names = new TreeSet<>();

        for (String key : keySet()) {
            String name = key.substring(0, key.indexOf(munger)); // de-munge
            FunctionRecord fr = get(key);
            name = name + "(" + fr.getArgCount() + ")";
            names.add(name);
        }
        return names;
    }

    /**
     * This looks up to see if there is any such named function, regardless of number of arguments.
     *
     * @param name
     * @return
     */
    public FunctionRecord getSomeFunction(String name) {
        String almostMungedName = name + munger;
        for (String key : keySet()) {
            if (key.startsWith(almostMungedName)) return get(key);
        }
        return null;
    }
}
