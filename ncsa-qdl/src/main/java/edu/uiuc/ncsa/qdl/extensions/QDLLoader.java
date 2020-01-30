package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.module.Module;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for loading your classes. To use this you need to make it available (along with yourc
 * classes in the classpath). The contract is that this takes a zero argument constructor. All state
 * needed will be injected. So populate you {@link JavaModule}s as you see fit.
 * Programatically, you can set this in the CLI before starting it or specify it on the command line
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  5:47 PM
 */
public interface QDLLoader extends Serializable {
    List<Module> load();
}
