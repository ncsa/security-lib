package edu.uiuc.ncsa.qdl.module;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:04 AM
 */
public class ModuleMap extends HashMap<String, Module> {
    public void put(Module module){
        super.put(module.name, module);
    }
}
