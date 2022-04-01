package edu.uiuc.ncsa.qdl.extensions.sets;

import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.module.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/1/22 at  12:28 PM
 */
public class QDLSetsLoader implements QDLLoader {
    @Override
    public List<Module> load() {
        List<Module> modules = new ArrayList<>();
        modules.add(new QDLSetsModule().newInstance(null));
        return modules;
    }
}
