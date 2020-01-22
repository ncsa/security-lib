package edu.uiuc.ncsa.qdl.module;

import java.net.URI;
import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:04 AM
 */
public class ModuleMap extends HashMap<URI, Module> {
    public void put(Module module){
        super.put(module.getName(), module);
    }
}
