package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/5/20 at  5:54 PM
 */
public class VFSZipFileConfig extends VFSAbstractConfig {
    public VFSZipFileConfig(String zipFilePath, String scheme, String mountPoint, boolean readable, boolean writeable) {
        super(scheme, mountPoint, readable, writeable);
        this.zipFilePath = zipFilePath;
    }

    public String getZipFilePath() {
        return zipFilePath;
    }

    String zipFilePath;
    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_ZIP;
    }
}
