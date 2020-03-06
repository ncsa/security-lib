package edu.uiuc.ncsa.qdl.config;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/4/20 at  3:24 PM
 */
public class VFSSQLConfig extends VFSAbstractConfig {
    public VFSSQLConfig(String scheme, String mountPoint, boolean readable, boolean writeable) {
        super(scheme, mountPoint, readable, writeable);
    }

    public Map<String, String> getConnectionParameters() {
        return connectionParameters;
    }

    Map<String,String>  connectionParameters = new HashMap<>();

    @Override
    public String getType() {
        return QDLConfigurationConstants.VFS_TYPE_MYSQL;
    }

}
