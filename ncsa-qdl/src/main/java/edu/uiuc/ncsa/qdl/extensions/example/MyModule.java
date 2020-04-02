package edu.uiuc.ncsa.qdl.extensions.example;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.extensions.QDLVariable;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;

import java.net.URI;
import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/2/20 at  8:09 AM
 */
public class MyModule extends JavaModule {
    public MyModule() {
    }

    protected MyModule(URI namespace, String alias) {
        super(namespace, alias);
    }

    @Override
    public Module newInstance(State state) {
        // Step 1: create the module with the URI and alias
        MyModule myModule = new MyModule(URI.create("qdl:/examples/java"), "java");
        // Step 2: create a list of functions and populate it
        ArrayList<QDLFunction> funcs = new ArrayList<>();
        funcs.add(new Concat());
        // Once the list of functions is populated, add the functions to the module
        myModule.addFunctions(funcs);

        // Step 3: create a list of variables and populate it
        ArrayList<QDLVariable> vars = new ArrayList<>();
        vars.add(new EGStem());
        // Once the list of variables is populated, add it to the module
        myModule.addVariables(vars);
        // Step 4: If this has a State object passed in initialize it. Note that
        // if you do not, then this instance will not be usable.
        // If the state is null, this implies that the state will be injected later and
        // the init method will be called too. 
        if(state != null){
            myModule.init(state);
        }
        return myModule;
    }
}
