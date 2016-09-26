package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.configuration.provider.HierarchicalConfigProvider;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;

/**
 * Creates a {@link ConnectionPool} from a configuration.
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/12 at  12:07 PM
 */
public abstract class ConnectionPoolProvider<T extends ConnectionPool> extends HierarchicalConfigProvider<T> {

    protected String host;
    protected String driver;
    protected int port = -1;
    protected String schema;
    protected String database;
    protected boolean useSSL = false;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String DRIVER = "driver";
    public static final String USE_SSL = "useSSL";
    public static final String DATABASE = "database";
    public static final String SCHEMA = SQLStoreProvider.SCHEMA; // since this is shared, really.


    protected ConnectionPoolProvider(String database, String schema, String host, int port, String driver, boolean useSSL) {
        this.database = database;
        this.host = host;
        this.driver = driver;
        this.port = port;
        this.schema = schema;
        this.useSSL = useSSL;
    }

    protected ConnectionPoolProvider(String database, String schema) {
        this.database = database;
        this.schema = schema;
    }


    /**
     * Check that the value associated with the key is not null. If it is, use the default value.
     * If there is no default value, then throw an exception.
     *
     * @param key
     * @param defaultValue
     * @return
     */
    protected String checkValue(String key, String defaultValue) {
        String x = getAttribute(key);
        if (x != null) return x;
        if (defaultValue != null) return defaultValue;

        throw new MyConfigurationException("Error: no value specified for " + key);
    }

    protected int checkValue(String key, int defaultValue) {
        String x = getAttribute(key);
        if (x != null) return Integer.parseInt(x);
        if (defaultValue != -1) return defaultValue;

        throw new MyConfigurationException("Error: no value specified for " + key);
    }

     protected boolean checkValue(String key, boolean defaultValue) {
        String x = getAttribute(key);
        if (x != null) return Boolean.parseBoolean(x);
        return defaultValue;
    }

    /**
     * Check the value associated with this key. If the value is missing, throw an exception.
     *
     * @param key
     * @return
     */
    protected String checkValue(String key) {
        String x = getAttribute(key);
        if (x == null) {
            throw new MyConfigurationException("Error: no value specified for " + key);
        }
        return x;
    }
}
