package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants;

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
        return map.get(getRealPath(path));
    }

    @Override
    public void put(String newPath, VFSEntry entry) throws Throwable {
        super.put(newPath, entry);
        String rPath = getRealPath(newPath);
        entry.setPath(VFSPaths.normalize(newPath));
        map.put(rPath, entry);
    }

    @Override
    public void delete(String path) throws Throwable {
        super.delete(path);
        map.remove(getRealPath(path));
    }

    @Override
    public boolean contains(String path) throws Throwable {
        super.contains(path);
        return map.containsKey(getRealPath(path));
    }

    @Override
    public String[] dir(String path) throws Throwable{
        super.dir(path);
        ArrayList<String> fileList = new ArrayList<>();
        String realPath = getRealPath(path);

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
