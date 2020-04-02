package edu.uiuc.ncsa.qdl.extensions.example;

import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.module.Module;

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
        MyModule myModule = new MyModule();
        ArrayList<Module> modules = new ArrayList<>();
        modules.add(myModule.newInstance(null));
        // Return this list of modules.
        return modules;
    }
}
