package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.qdl.state.SymbolTable.var_regex;

/**
 * This will let you create your own extensions to QDL in Java. Simply implement the interfaces
 * {@link QDLFunction} for functions and {@link QDLVariable} for variables, add the module
 * and you can use it in QDL like any other module.
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  12:03 PM
 */
public class JavaModule extends Module {
    @Override
    public boolean isExternal() {
        return true;
    }

    public JavaModule(URI namespace, String alias) {
        super(namespace, alias, null); // no state here -- it is injected later
    }

    public void addFunctions(List<QDLFunction> functions) {
            funcs.addAll(functions);
    }

    protected List<QDLVariable> vars = new ArrayList<>();
    protected List<QDLFunction> funcs = new ArrayList<>();

    public void addVariables(List<QDLVariable> variables) {
        vars.addAll(variables);
    }
     Pattern pattern = Pattern.compile(var_regex);
    public void init(State state) {
        setState(state);
        for (QDLVariable v : vars) {
            if(Constant.getType(v.getValue()) == Constant.UNKNOWN_TYPE){
                throw new IllegalArgumentException("Error: The value of  " + v.getValue() + " is unknown.");
            }
            if(!pattern.matcher(v.getName()).matches()){
                throw new IllegalArgumentException("Error: The variable name \"" + v.getName() + "\" is not a legal variable name.") ;
            }
            state.setValue(v.getName(), v.getValue());
        }
        for(QDLFunction f : funcs){
            for(int i : f.getArgCount()){
                QDLFunctionRecord fr = new QDLFunctionRecord();
                fr.qdlFunction = f;
                fr.argCount = i;
                fr.name = f.getName();
                if(f.getDocumentation()!=null && !f.getDocumentation().isEmpty()) {
                    fr.documentation = f.getDocumentation();
                }
                state.getFunctionTable().put(fr);

            }
        }

    }

}
