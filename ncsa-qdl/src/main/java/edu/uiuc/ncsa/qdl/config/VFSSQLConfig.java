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
    /*
      public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String DRIVER = "driver";
    public static final String USE_SSL = "useSSL";
    public static final String DATABASE = "database";
    public static final String PARAMETERS = "parameters";
     */
}
