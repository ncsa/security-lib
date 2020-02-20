package edu.uiuc.ncsa.qdl.scripting;

import java.io.Serializable;

/**
 * A QDL script library. This functions much like a virtual file system. Requests come with
 * a scheme, e.g. <code>qdl-vfs:myscript.qdl</code>. If the scheme (always ends with a :) matches, then the name is
 * resolved and the script is returned. You add these to the {@link edu.uiuc.ncsa.qdl.state.State}
 * load/run commands resolve against any script libraries then fall through to the local file system
 * (unless it is running in server mode).
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  7:43 AM
 */
public interface ScriptProvider extends Serializable {
    /**
     * The scheme that uniquely identifies this provider
     * @return
     */
    String getScheme();

    /**
     * Checks if the name has the scheme for this provider.
     * @param name
     * @return
     */
    boolean checkScheme(String name);

    /**
     * Get the named item. Note that all of the names are qualified.
     * @param name
     * @return
     */
    LibraryEntry get(String name);

    /**
     * Add the script using the given FQ name.
     * @param name
     * @param script
     */
    void put(String name, LibraryEntry script);

    /**
     * Checks if the FQ name can be resolved to a script by this provider.
     * @param name
     * @return
     */
    boolean isScript(String name);
}
