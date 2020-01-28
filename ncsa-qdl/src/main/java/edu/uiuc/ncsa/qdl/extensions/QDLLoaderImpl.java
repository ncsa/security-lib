package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.extensions.example.Concat;
import edu.uiuc.ncsa.qdl.extensions.example.EGStem;
import edu.uiuc.ncsa.qdl.module.Module;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This is used to get your modules in to quiddle
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  5:44 PM
 */
public class QDLLoaderImpl implements QDLLoader {
    @Override
    public List<Module> load(){
        JavaModule javaModule = new JavaModule(URI.create("qdl:/java"), "java");
        ArrayList<QDLFunction> funcs = new ArrayList<>();
        funcs.add(new Concat());
        ArrayList<QDLVariable> vars = new ArrayList<>();
        vars.add(new EGStem());
        javaModule.addFunctions(funcs);
        javaModule.addVariables(vars);
        ArrayList<Module> modules = new ArrayList<>();
        modules.add(javaModule);
        return modules;
      //edu.uiuc.ncsa.qdl.QDLLoaderImpl
    }
}
