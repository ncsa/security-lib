package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;

import java.util.List;

import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.PATH_SEPARATOR;

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
        output[PATH_INDEX] =output[PATH_INDEX] +  (output[PATH_INDEX].endsWith(PATH_SEPARATOR)?"":PATH_SEPARATOR);
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

        String relPath = VFSPaths.relativize(getStoreRoot(), path);
        relPath = relPath + (relPath.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
        relPath = VFSPaths.getUnqPath(relPath);
        boolean isRoot = relPath.equals(PATH_SEPARATOR);
        List<String> output = db.selectByPath(PATH_SEPARATOR + relPath);
        // should be at most one, actually.
        if (output.size() == 0) {
            // nothing in this directory
            throw new QDLIOException("Error: The directory \"" + path + "\" does not exist.");
        }
         List<String> distinctPaths = db.getDistinctPaths();;
       String[] components = VFSPaths.toPathComponents(relPath);
        for (String key : distinctPaths) {
                  if (isChildOf(components, key, isRoot)) {
                      if (isRoot) {
                          // no surgery on root nodes.
                          output.add(key);
                      } else {
                          // Datanbase paths are of the form /a/b/c/ so they start and end with slashes.
                          // This cuts off the lead slash so the returned directory relative
                          output.add(key.substring(1+ relPath.length()));
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
}
