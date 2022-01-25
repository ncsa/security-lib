package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.security.core.configuration.XProperties;

import java.io.Serializable;

/**
 * A QDL  virtual file system. This provides files from a mounted VFS.  Requests come with
 * a scheme, e.g. <code>qdl-vfs:myscript.qdl</code>. If the scheme (always ends with a :) matches, then the name is
 * resolved and the script is returned. You add these to the {@link edu.uiuc.ncsa.qdl.state.State}
 * load/run commands resolve against any script libraries then fall through to the local file system
 * (unless it is running in server mode).
 * <br/><br/>
 * Why not just use the Java {@link java.nio.file.FileSystem}? Because that allows for access to the underlying
 * native file system and in a scripting environment, we want to severely restrict access to just
 * read-only virtual file systems which can e.g., make libraries available.
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  7:43 AM
 */
public interface VFSFileProvider extends Serializable {
    /**
     * mostly this is so when information is being displayed to the user they can see the origin of the provider.
     *
     * @return
     */
    String getType();

    /**
     * The scheme that uniquely identifies this provider
     *
     * @return
     */
    String getScheme();

    /**
     * The scheme is does not end with the scheme delimiter and should be normalized here.
     *
     * @param scheme
     */
    void setScheme(String scheme);

    /**
     * This is the mount point for the virtual file system.
     *
     * @return
     */
    String getMountPoint();

    /**
     * The mount point <i>does</i> <b>both</b> start and end with the path separator since we always need it.
     * The mount point should be normalized in the setter.
     *
     * @param mountPoint
     */
    void setMountPoint(String mountPoint);

    /**
     * Checks if the name has the scheme for this provider.
     *
     * @param name
     * @return
     */
    boolean checkScheme(String name);

    /**
     * Get the named item. Note that all of the names are qualified.
     *
     * @param name
     * @return
     */
    VFSEntry get(String name, int type) throws Throwable;

    /**
     * Add the using the path. If and entry exists there it will be over-written.
     *
     * @param newPath
     * @param entry
     */
    void put(String newPath, VFSEntry entry) throws Throwable;

    /**
     * Put this in the store at its current path
     *
     * @param entry
     * @throws Throwable
     */
    void put(VFSEntry entry) throws Throwable;

    /**
     * Checks if the FQ name can be resolved to a script by this provider.
     *
     * @param path
     * @return
     */
    boolean isScript(String path);

    boolean canRead();
    void setRead(boolean newValue);

    boolean canWrite();
    void setWrite(boolean newValue);

    /**
     * A delete is a type of write. If the store is not writeable, it cannot delete files.
     *
     * @param path
     * @throws Throwable
     */
    void delete(String path) throws Throwable;

    /**
     * Contains is a type of read. If the store is not readable, it cannot tell if it contains an entry.
     *
     * @param path
     * @return
     * @throws Throwable
     */
    boolean contains(String path) throws Throwable;

    XProperties getFileInfo(String path) throws Throwable;

    String getCurrentDir();

    void setCurrentDir(String path);

    String[] dir(String path) throws Throwable;

    /**
     * Make a set of directories. This will make a given directory and any intermediate directories.
     * @param path
     * @return
     */
    boolean mkdir(String path);

    /**
     * This will remove a directory. Note that this requires that the directory be empty of
     * all files.
     * @param path
     * @return
     * @throws Throwable
     */
    boolean rmdir(String path) throws Throwable;

    /**
     * Remove a file from the store.
     * @param path
     * @throws Throwable
     */
    void rm(String path) throws Throwable;

    /**
     * If this store supports extended attributes.
     * @return
     */
    boolean easSupported();


}
