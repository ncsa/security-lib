package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.security.core.configuration.XProperties;

import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.PATH_SEPARATOR;
import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.SCHEME_DELIMITER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/28/20 at  5:07 PM
 */
public abstract class AbstractVFSFileProvider implements VFSFileProvider {
    /**
     * Constructor without a current directory
     *
     * @param scheme
     * @param mountPoint
     * @param canRead
     * @param canWrite
     */
    public AbstractVFSFileProvider(String scheme,
                                   String mountPoint,
                                   boolean canRead,
                                   boolean canWrite
    ) {
        this(scheme, mountPoint, null, canRead, canWrite);
    }

    public AbstractVFSFileProvider(String scheme,
                                   String mountPoint,
                                   String currentDir,
                                   boolean canRead,
                                   boolean canWrite
    ) {
        this.currentDir = currentDir;
        this.scheme = (scheme.endsWith(SCHEME_DELIMITER) ? scheme.substring(0, scheme.length() - 1) : scheme);
        this.mountPoint = mountPoint + (mountPoint.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        this.canRead = canRead;
        this.canWrite = canWrite;
    }

    @Override
    public String getMountPoint() {
        return mountPoint;
    }

    String scheme = null;
    String mountPoint = null;

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public boolean checkScheme(String name) {
        return name.startsWith(getScheme() + SCHEME_DELIMITER);
    }

    boolean canRead = false;
    boolean canWrite = false;

    @Override
    public boolean canRead() {
        return canRead;
    }

    @Override
    public boolean canWrite() {
        return canWrite;
    }

    @Override
    public XProperties getFileInfo(String path) throws Throwable {
        checkPath(path);
        return get(path).getProperties();
    }

    String currentDir = null;

    @Override
    public String getCurrentDir() {
        return currentDir;
    }

    @Override
    public void setCurrentDir(String path) {
        checkPath(path);
        this.currentDir = path;
    }

    protected void checkPath(String path) {
        if (!VFSPaths.getScheme(path).equals(getScheme())) {
            throw new QDLIOException("Error: Path \"" + path + "\" is not in this store. The scheme must be \"" + getScheme() + "\".");
        }
    }

    @Override
    public boolean isScript(String name) {
        return VFSPaths.getFileName(name).endsWith(QDLVersion.DEFAULT_FILE_EXTENSION);
    }

    /**
     * Resolves this against any current directory and returns the unqualified *relative* path against the
     * store's root path.
     *
     * @param path
     * @return
     */
    protected String getRealPath(String path) {
        path = VFSPaths.normalize(path); //cuts out attempts to use ../../.. to get out of the VFS.
        String rPath = null;
        if (!VFSPaths.isAbsolute(path)) {
            if (getCurrentDir() == null) {
                throw new QDLIOException("Error: No current directory set. Cannot resolve the relative file name \"" + path + "\"");
            } else {
                path = VFSPaths.resolve(getCurrentDir(), path);
            }
        }
        rPath = VFSPaths.relativize(getStoreRoot(), path);
        return unqualifyPath(rPath); // don't need scheme any more.

    }

    String storeRoot = null;

    protected String unqualifyPath(String x) {
        if(x.indexOf(SCHEME_DELIMITER) == -1){return  x;} // already unqualified
        return x.substring((getScheme() + SCHEME_DELIMITER).length()); // chop off scheme since we don't want it now.
    }

    /**
     * The root fo this store. So if the scheme is A and the mount point is /a/b/c then this returns
     * A#/a/b/c. Operations on the store typiclly use this for resolving paths.
     *
     * @return
     */
    public String getStoreRoot() {
        if (storeRoot == null) {
            storeRoot = getScheme() + SCHEME_DELIMITER + getMountPoint() + PATH_SEPARATOR;
        }
        return storeRoot;
    }

    @Override
    public VFSEntry get(String path) throws Throwable {
        if (!canRead()) {
            throw new QDLIOException("Error: You do not have permission to read from the virtual file system");
        }
        checkPath(path);
        return null;
    }

    @Override
    public void put(String newPath, VFSEntry entry) throws Throwable {
        if (!canRead()) {
            throw new QDLIOException("Error: You do not have permission to write to the virtual file system");
        }
        checkPath(newPath);
    }

    @Override
    public void put(VFSEntry entry) throws Throwable {
        put(entry.getPath(), entry);
    }

    @Override
    public void delete(String path) throws Throwable {
        if (!canRead()) {
            throw new QDLIOException("Error: You do not have permission to write to the virtual file system");
        }
        checkPath(path);

    }

    @Override
    public boolean contains(String path) throws Throwable {
        if (!canRead()) {
            throw new QDLIOException("Error: You do not have permission to read from the virtual file system");
        }
        checkPath(path);
        return false;
    }

    @Override
    public String[] dir(String path) {
        if (!canRead()) {
            throw new QDLIOException("Error: You do not have permission to read from the virtual file system");
        }
        checkPath(path);
        return new String[0];
    }
}
