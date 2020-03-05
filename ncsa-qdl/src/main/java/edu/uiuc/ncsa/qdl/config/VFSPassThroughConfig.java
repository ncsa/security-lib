package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  12:51 PM
 */
public class VFSPassThroughConfig extends VFSAbstractConfig {
    public VFSPassThroughConfig(String rootDir,
                                String scheme,
                                String mountPoint,
                                boolean readable,
                                boolean writeable) {
        super(scheme, mountPoint, readable, writeable);
        this.rootDir = rootDir;
    }

    String rootDir;

    public String getRootDir() {
        return rootDir;
    }

    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_PASS_THROUGH;
    }

    @Override
    public String toString() {
        return "VFSPassThroughConfig{" +
                "type=\'" + getType() + '\'' +
                "scheme='" + scheme + '\'' +
                ", mountPoint='" + mountPoint + '\'' +
                ", rootDir='" + rootDir + '\'' +
                ", readable=" + readable +
                ", writeable=" + writeable +
                '}';
    }
}
