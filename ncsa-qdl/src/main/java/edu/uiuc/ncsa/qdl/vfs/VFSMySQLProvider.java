package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants;

import java.util.List;

import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.PATH_SEPARATOR;
import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.normalize;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/28/20 at  7:17 AM
 */
public class VFSMySQLProvider extends AbstractVFSFileProvider {
    public VFSMySQLProvider(VFSDatabase db, String scheme, String mountPoint, boolean canRead, boolean canWrite) {
        super(scheme, mountPoint, canRead, canWrite);
        this.db = db;
    }

    public VFSMySQLProvider(String scheme, String mountPoint, String currentDir, boolean canRead, boolean canWrite) {
        super(scheme, mountPoint, currentDir, canRead, canWrite);
    }

    VFSDatabase db;

    @Override
    protected String getRealPath(String path) {
        return VFSPaths.PATH_SEPARATOR + super.getRealPath(path);
    }

    @Override
    public VFSEntry get(String path) throws Throwable {
        super.get(path);
        return db.get(getPrimaryKey(path));
    }

    public static int PATH_INDEX = 0;
    public static int FILENAME_INDEX = 1;

    protected String[] getPrimaryKey(String path) {

        path = getRealPath(VFSPaths.normalize(path));
        path = unqualifyPath(path); // no schemes in database.
        String[] output = new String[2];
        output[PATH_INDEX] = VFSPaths.getParentPath(path);
        output[PATH_INDEX] = output[PATH_INDEX] + (output[PATH_INDEX].endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        output[FILENAME_INDEX] = VFSPaths.getFileName(path);
        return output;
    }

    @Override
    public void put(String path, VFSEntry entry) throws Throwable {
        super.put(path, entry);
        String[] key = getPrimaryKey(path);
        if (db.containsEntry(key)) {
            db.update(key, entry);
        } else {
            db.put(key, entry);
        }
    }


    @Override
    public void delete(String path) throws Throwable {
        super.delete(path);
        db.remove(getPrimaryKey(path));

    }

    @Override
    public boolean contains(String path) throws Throwable {
        super.contains(path);
        return db.containsEntry(getPrimaryKey(path));
    }


    @Override
    public String[] dir(String path) throws Throwable {
        super.dir(path);

        String relPath = VFSPaths.relativize(getStoreRoot(), normalize(path));
        relPath = relPath + (relPath.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        relPath = VFSPaths.getUnqPath(relPath);
        boolean isRoot = relPath.equals(PATH_SEPARATOR);
        List<String> output = db.selectByPath(PATH_SEPARATOR + relPath);
        if (output.size() == 0) {
            // nothing in this directory
            return new String[0];
        }
        List<String> distinctPaths = db.getDistinctPaths();
        String[] components = VFSPaths.toPathComponents(relPath);
        for (String key : distinctPaths) {
            if (isChildOf(components, key, isRoot)) {
                if (isRoot) {
                    // no surgery on root nodes.
                    output.add(key);
                } else {
                    // Database paths are of the form /a/b/c/ so they start and end with slashes.
                    // This cuts off the lead slash so the returned directory relative
                    output.add(key.substring(1 + relPath.length()));
                }
            }
        }
        String[] fileList = new String[output.size()];
        int i = 0;

        for (String x : output) {
            if (!x.isEmpty()) {
                fileList[i++] = x;
            }
        }

        return fileList;

    }

    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_MYSQL;
    }

    @Override
    public boolean mkdir(String path) {
        super.mkdir(path);
        // Trick is we have to make all the intervening directories.
        String realpath = getRealPath(path);
        realpath = realpath + (realpath.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        String[] key = getPrimaryKey(realpath);
        if (db.containsEntry(key)) {
            return false; // The key always turns the last component in to the file name, so this check if a file is already there.
        }


        String[] components = VFSPaths.toPathComponents(path);
        String startPath = PATH_SEPARATOR;
        String[] newPath = new String[]{startPath, ""};

        if (!db.containsEntry(newPath)) {
            db.mkdir(newPath);
        }

        // If its a new store, then make the root dir
        for (int i = 0; i < components.length; i++) {
            startPath = startPath + components[i] + PATH_SEPARATOR;
            newPath[PATH_INDEX] = startPath;

            if (!db.containsEntry(newPath)) {
                if (!db.mkdir(newPath)) {
                    return false; // bail for any reason
                }
            }

        }
        return true; // Um. Guess it worked...
    }

    /**
     * Removes a directory if it is empty. If it is not empty, it or the directory cannot be removed, it returns false.
     * @param path
     * @return
     * @throws Throwable
     */
    @Override
    public boolean rmdir(String path) throws Throwable {
        super.rmdir(path);
        String realpath = getRealPath(path);
        realpath = realpath + (realpath.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        String[] key = getPrimaryKey(realpath);
        if (db.containsEntry(key)) {
            return false; // The key always turns the last component in to the file name, so this check if a file is already there.
        }
        List<String> output = db.selectByPath( realpath);
        if(0 < output.size()){
            // then this is a directory and has entries in it, so no go
            //throw new QDLIOException("Error: The directory \"" + path + "\" is not empty.");
            return false;
        }
        key = new String[]{realpath, ""};
        if (db.containsEntry(key)) {
            db.remove(key);
        }
        return true;
    }

    @Override
    public void rm(String path) throws Throwable {
        super.rm(path);
        db.remove(getPrimaryKey(path));
    }
}