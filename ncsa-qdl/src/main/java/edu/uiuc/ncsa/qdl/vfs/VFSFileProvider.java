package edu.uiuc.ncsa.qdl.vfs;

import java.io.Serializable;

/**
 * A QDL  virtual file system. This provides files from a mounted VFS.  Requests come with
 * a scheme, e.g. <code>qdl-vfs:myscript.qdl</code>. If the scheme (always ends with a :) matches, then the name is
 * resolved and the script is returned. You add these to the {@link edu.uiuc.ncsa.qdl.state.State}
 * load/run commands resolve against any script libraries then fall through to the local file system
 * (unless it is running in server mode).
 * <br/><br/>
 * Why not just use the Java {@link java.io.FileSystem}? Because that allows for access to the underlying
 * native file system and in a scripting environment, we want to severely restrict access to just
 * read-only virtual file systems which can e.g., make libraries available.
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  7:43 AM
 */
public interface VFSFileProvider extends Serializable {
    String SCHEME_DELIMITER = ":";
    String PATH_SEPARATOR = "/";
    /**
     * The scheme that uniquely identifies this provider
     * @return
     */
    String getScheme();

    /**
     * This is the mount point for the virtual file system.
     * @return
     */
    String getMountPoint();



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
    VFSEntry get(String name) throws Throwable;

    /**
     * Add the script using the given FQ name.
     * @param name
     * @param script
     */
    void put(String name, VFSEntry script) throws Throwable;

    /**
     * Checks if the FQ name can be resolved to a script by this provider.
     * @param name
     * @return
     */
    boolean isScript(String name);

    boolean canRead();
    boolean canWrite();
}
