package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants;
import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/4/20 at  11:31 AM
 */
public class VFSMemoryFileProvider extends AbstractVFSFileProvider {
    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_MEMORY;
    }

    public VFSMemoryFileProvider(String scheme, String mountPoint, boolean canRead, boolean canWrite) {
        super(scheme, mountPoint, canRead, canWrite);
    }

    public VFSMemoryFileProvider(String scheme, String mountPoint, String currentDir, boolean canRead, boolean canWrite) {
        super(scheme, mountPoint, currentDir, canRead, canWrite);
    }

    HashMap<String, VFSEntry> map = new HashMap<>();

    @Override
    public VFSEntry get(String path) throws Throwable {
        super.get(path);
        if (VFSPaths.isAbsolute(path)) {
            return map.get(path);
        }
        return map.get(resolvePath(path));
    }

    protected String resolvePath(String relativePath) {
        if (getCurrentDir() == null) {
            throw new QDLIOException("Error: No current directory is set for this store");
        }
        String p = VFSPaths.resolve(getCurrentDir(), relativePath);
        return p;
    }

    @Override
    public void put(String newPath, VFSEntry entry) throws Throwable {
        super.put(newPath, entry);
        if (VFSPaths.isAbsolute(newPath)) {
            entry.setPath(newPath);
            map.put(newPath, entry);
            return;
        }
        String p = resolvePath(newPath);
        entry.setPath(p);
        map.put(p, entry);

    }



    @Override
    public void delete(String path) throws Throwable {
        super.delete(path);
        if (VFSPaths.isAbsolute(path)) {
            map.remove(path);
        }
        map.remove(resolvePath(path));

    }

    @Override
    public boolean contains(String path) throws Throwable {
        super.contains(path);
        if (VFSPaths.isAbsolute(path)) {
            return map.containsKey(path);
        }
        return map.containsKey(resolvePath(path));
    }

    @Override
    public String[] dir(String path) {
        super.dir(path);
        ArrayList<String> fileList = new ArrayList<>();
        String realPath;
        if (VFSPaths.isAbsolute(path)) {
            realPath = path;
        } else {
            realPath = resolvePath(path);
        }
        for (String key : map.keySet()) {
            if (VFSPaths.startsWith(key, realPath)) {
                fileList.add(key);
            }
        }
        String[] output = new String[fileList.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = fileList.get(i);
        }
        return output;
    }
}
