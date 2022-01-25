package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.security.core.configuration.XProperties;

import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.*;

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
        // use setters since there is clean up done in them.
        setScheme(scheme);
        setMountPoint(mountPoint);
        setRead(canRead);
        setWrite(canWrite);
    }

    @Override
    public String getMountPoint() {
        return mountPoint;
    }


    @Override
    public void setScheme(String scheme) {
        this.scheme = (scheme.endsWith(SCHEME_DELIMITER) ? scheme.substring(0, scheme.length() - 1) : scheme);
        storeRoot = null;
    }


    @Override
    public void setMountPoint(String mountPoint) {
        this.mountPoint = (mountPoint.startsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR) + mountPoint + (mountPoint.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        storeRoot = null;
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
    public void setRead(boolean newValue) {
        this.canRead = newValue;
    }

    @Override
    public void setWrite(boolean newValue) {
        this.canWrite = newValue;
    }

    @Override
    public XProperties getFileInfo(String path) throws Throwable {
        checkPath(path);
        return get(path, AbstractFunctionEvaluator.FILE_OP_AUTO).getProperties();
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
     * Returns if this path is absolute, i.e. if it starts with the path separator.
     * So A#/a/b/c is absolute, A#b/c is not. <br/><br/>
     * An absolute path contains enough information to find the file. A relative path
     * does not. This will compare them ignoring schemes.
     *
     * @param path
     * @return
     */
   public static boolean isAbsolute(String path) {
        return getUnqPath(path).startsWith(VFSPaths.PATH_SEPARATOR);
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
        rPath = unqualifyPath(rPath); // don't need scheme any more.
        if (rPath.endsWith("..")) {
            // Edge case from relativize method that if this resolved to the root of the
            // current store, /, it would get turned in to a ".." which might let certain
            // stores pass outside of their mount point and access other directories.
            // A common hackey (innocent) thing people do is if they are a few levels down
            // and want to get to the root directory issue a path like ../../../../..
            // which they assume will stop at the root. We should too.
            // Therefore
            // this always stops at the root.
            rPath = PATH_SEPARATOR;
        }

        return rPath;
    }

    String storeRoot = null;

    protected String unqualifyPath(String x) {
        if (x.indexOf(SCHEME_DELIMITER) == -1) {
            return x;
        } // already unqualified
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
    public VFSEntry get(String path, int type) throws Throwable {
        if (!canRead()) {
            throw new QDLIOException("Error: You do not have permission to read from the virtual file system");
        }
        checkPath(path);
        return null;
    }

    @Override
    public void put(String newPath, VFSEntry entry) throws Throwable {
        if (!canWrite()) {
            throw new QDLIOException("Error: You do not have permission to write to the virtual file system");
        }
        checkPath(newPath);
    }

    @Override
    public void put(VFSEntry entry) throws Throwable {
        if (!canWrite()) {
            throw new QDLIOException("Error: You do not have permission to write to the virtual file system");
        }

        put(entry.getPath(), entry);
    }

    @Override
    public void delete(String path) throws Throwable {
        if (!canWrite()) {
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
    public String[] dir(String path) throws Throwable {
        if (!canRead()) {
            throw new QDLIOException("Error: You do not have permission to read from the virtual file system");
        }
        checkPath(path);
        return new String[0];
    }

    /**
       * Use this for stores that keep everything in a flat list (like a database table or hash map of file
       * paths.) You use {@link VFSPaths#toPathComponents(String)} on the argument to the {@link #dir(String)}command
       * and specify if it is the root node (since figuring that out is store dependent). This then compares
       * paths by lengths to give back the right one.
       * @param top
       * @param target
       * @param isRoot
       * @return
       */
      protected boolean isChildOf(String[] top, String target, boolean isRoot) {
          String[] other = VFSPaths.toPathComponents(target);
          if (isRoot) {
              // Special case of the root directory
              if (other.length == 1) return true;
          }
          if (top.length + 1 != other.length) return false;
          for (int i = 0; i < top.length; i++) {
              if (!top[i].equals(other[i])) return false;
          }
          return true;
      }

    @Override
    public boolean mkdir(String path) {
        if (!canWrite()) {
            throw new QDLIOException("Error: You do not have permissions make directories in the virtual file system");
        }
        checkPath(path);
        return false;
    }

    @Override
    public boolean rmdir(String path) throws Throwable {
        if (!canWrite()) {
            throw new QDLIOException("Error: You do not have permissions to remove directories in the virtual file system");
        }
        checkPath(path);
        return false;
    }

    @Override
    public void rm(String path) throws Throwable {
        if (!canWrite()) {
            throw new QDLIOException("Error: You do not have permissions to remove this file in the virtual file system");
        }
        checkPath(path);
    }


    @Override
    public boolean easSupported() {
        return false;
    }
}
