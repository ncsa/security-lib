package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/4/20 at  3:13 PM
 */
public class VFSMemoryConfig extends VFSAbstractConfig {
    public VFSMemoryConfig(String scheme, String mountPoint, boolean readable, boolean writeable) {
        super(scheme, mountPoint, readable, writeable);
    }

    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_MEMORY;
    }
}
