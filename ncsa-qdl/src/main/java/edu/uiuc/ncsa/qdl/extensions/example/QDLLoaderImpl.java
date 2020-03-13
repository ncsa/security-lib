package edu.uiuc.ncsa.qdl.extensions.example;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.extensions.QDLVariable;
import edu.uiuc.ncsa.qdl.module.Module;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a sample of how to write a loader to get a module in to QDL.
 * All you need to do is override the {@link #load()} method.
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  5:44 PM
 */
public class QDLLoaderImpl implements QDLLoader {
    @Override
    public List<Module> load(){
        // Step 1: create the module with the URI and alias
        JavaModule javaModule = new JavaModule(URI.create("qdl:/examples/java"), "java");
        // Step 2: create a list of functions and populate it
        ArrayList<QDLFunction> funcs = new ArrayList<>();
        funcs.add(new Concat());
        // Once the list of functions is populated, add the functions to the module
        javaModule.addFunctions(funcs);

        // Step 3: create a list of variables and populate it
        ArrayList<QDLVariable> vars = new ArrayList<>();
        vars.add(new EGStem());
        // Once the list of variables is populated, add it to the module
        javaModule.addVariables(vars);
        // Step 4: Now that we have created all of our modules (one in this example),
        // create the list of modules and add the module created here
        ArrayList<Module> modules = new ArrayList<>();
        modules.add(javaModule);
        // Return this list of modules.
        return modules;
    }
}
