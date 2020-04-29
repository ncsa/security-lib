package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.qdl.util.FileUtil;

import java.io.File;

import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.PATH_SEPARATOR;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/20 at  1:05 PM
 */
public class VFSPassThruFileProvider extends AbstractVFSFileProvider {
    public VFSPassThruFileProvider(String rootDir,
                                   String scheme,
                                   String mountPoint,
                                   boolean canRead,
                                   boolean canWrite) {
        super(scheme, mountPoint, canRead, canWrite);
        this.rootDir = rootDir + (rootDir.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR);
    }

    String rootDir = null;

    @Override
    public VFSEntry get(String path) throws Throwable {
        super.get(path);
        String realPath = getRealPath(path);
        try {
            return FileEntries.fileToEntry(realPath);
        }catch(Throwable t){
            return null;
        }
    }


    protected String getRealPath(String path) {
        return rootDir + super.getRealPath(path);
    }


    @Override
    public void put(String path, VFSEntry entry) throws Throwable {
        super.put(path, entry);

        if (entry.isBinaryType()) {
            FileUtil.writeFileAsBinary(getRealPath(path), entry.getText());
        } else {
            FileUtil.writeStringToFile(getRealPath(path), entry.getText());
        }
    }

    @Override
    public void put(VFSEntry entry) throws Throwable {
        put(entry.getPath(), entry);
    }


    @Override
    public void delete(String path) throws Throwable {
        super.delete(path);
        File f = new File(getRealPath(path));
        if (!f.exists()) return; //nothing to do
        if (f.isDirectory()) {
            throw new QDLIOException("Error: \"" + path + "\" is a directory.");
        }
        f.delete(); // we don't care if a real file was deleted. The contract is that if this is a file, its gone now.
    }

    @Override
    public boolean contains(String path) throws Throwable {
        super.contains(path);
        String realPath = getRealPath(path);
        File f = new File(realPath);
        return f.exists();
    }


    @Override
    public String[] dir(String path) throws Throwable {
        super.dir(path);
        String realPath = getRealPath(path);

        File f = new File(realPath);
        String[] fileList = f.list();
        return fileList;
    }

    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_PASS_THROUGH;
    }

    @Override
    public boolean mkdir(String path) {
        super.mkdir(path);
        String realPath = getRealPath(path);

        File f = new File(realPath);
        if (f.isFile()) {
            return false;
        }
        return f.mkdirs();
    }

    @Override
    public boolean rmdir(String path) throws Throwable {
        super.rmdir(path);
        String realPath = getRealPath(path);

        File f = new File(realPath);
        if (f.isFile()) {
            return false;
        }
        return f.delete();
    }

    @Override
    public void rm(String path) throws Throwable {
        super.rm(path);
        String realPath = getRealPath(path);

        File f = new File(realPath);
        if (!f.isFile()) {
            return;
        }
        f.delete();
    }
}
