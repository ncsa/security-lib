package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  12:51 PM
 */
public class VFSPassThroughConfig implements VFSConfig {
    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_PASS_THRU;
    }
    String scheme;
    String mountPoint;
    String rootDir;
    boolean readable = false;
    boolean writeable = false;
    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getMountPoint() {
        return mountPoint;
    }

    public String getRootDir() {
        return rootDir;
    }

    @Override
    public boolean canRead() {
        return readable;
    }

    @Override
    public boolean canWrite() {
        return writeable;
    }

    @Override
    public String toString() {
        return "VFSPassThroughConfig{" +
                "scheme='" + scheme + '\'' +
                ", mountPoint='" + mountPoint + '\'' +
                ", rootDir='" + rootDir + '\'' +
                ", readable=" + readable +
                ", writeable=" + writeable +
                '}';
    }
}
