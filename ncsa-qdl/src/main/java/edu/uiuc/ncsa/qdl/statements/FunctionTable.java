package edu.uiuc.ncsa.qdl.statements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:52 AM
 */
public class FunctionTable extends HashMap<String, FunctionRecord> implements Documentable {
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
        if (argCount == -1) {
            for (String k : keySet()) {
                if (k.startsWith(var + munger)) {
                    return true;
                }
            }
            return false;
        }
        return super.containsKey(createKey(var, argCount));
    }

    @Override
    public TreeSet<String> listFunctions(String regex) {
        TreeSet<String> names = new TreeSet<>();

        for (String key : keySet()) {
            String name = key.substring(0, key.indexOf(munger)); // de-munge
            FunctionRecord fr = get(key);
            if (regex != null && !regex.isEmpty()) {
                if (name.matches(regex)) {
                    name = name + "(" + fr.getArgCount() + ")";
                    names.add(name);
                }
            } else {
                name = name + "(" + fr.getArgCount() + ")";
                names.add(name);
            }
        }
        return names;
    }

    /**
     * Just lists the first line of every function with documentation
     *
     * @return
     */
    @Override
    public List<String> listAllDocs() {
        ArrayList<String> docs = new ArrayList<>();
        for (String key : keySet()) {
            String name = key.substring(0, key.lastIndexOf(munger)); // de-munge
            FunctionRecord fr = get(key);
            name = name + "(" + fr.getArgCount() + ")";
            if (0 < fr.documentation.size()) {
                if (!fr.documentation.get(0).contains(name)) {
                    name = fr.documentation.get(0);
                } else {
                    name = name + ": " + fr.documentation.get(0);
                }
            } else {
                name = name + ": (none)";

            }
            docs.add(name);
        }

        return docs;
    }

    // Filter by fname.
    public List<String> listAllDocs(String fname) {
        ArrayList<String> docs = new ArrayList<>();
        for (String key : keySet()) {
            if (key.startsWith(fname + munger)) {
                FunctionRecord fr = get(key);
                String name = fname + "(" + fr.getArgCount() + ")";
                if (0 < fr.documentation.size()) {
                    if (!fr.documentation.get(0).contains(name)) {
                        name = fr.documentation.get(0);
                    } else {
                        name = name + ": " + fr.documentation.get(0);
                    }
                } else {
                    name = name + ": (none)";

                }
                docs.add(name);
            }
        }
        return docs;
    }

    /**
     * Returns the specific documentation for a function. The request is of the form name(args);
     *
     * @param fName
     * @return
     */
    @Override
    public List<String> getDocumentation(String fName, int argCount) {
        if (get(fName, argCount) == null) {
            return null;
        } else {
            return get(fName, argCount).documentation;
        }
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
