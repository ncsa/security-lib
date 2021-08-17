package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.configuration.provider.HierarchicalConfigProvider;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;

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
    public static final String PARAMETERS = "parameters";
    public static final String SCHEMA = SQLStoreProvider.SCHEMA; // since this is shared, really.
    public static final String BOOT_PASSWORD = "bootPassword"; // for derby databases
    public static final String IN_MEMORY = "inMemory"; // for derby databases
    public static final String CONNECTION_MAX = "maxConnections";
    public static final String CONNECTION_IDLE_TIMEOUT = "idleTimeout";
    public static final String CONNECTION_CLEANUP_INTERVAL = "cleanupInterval";
    public static final String CONNECTION_CLEANUP_ENABLE = "cleanupEnable";
    public static final String CONNECTION_ENABLE_QUEUE = "queueEnable";
    public static final String CONNECTION_QUEUE_INTERVAL = "queueInterval";



    protected ConnectionPoolProvider(String database, String schema, String host, int port, String driver, boolean useSSL) {
        this.database = database;
        this.host = host;
        this.driver = driver;
        this.port = port;
        this.schema = schema;
        this.useSSL = useSSL;
    }

    public static int DEFAULT_MAX_CONNECTIONS = 10;  // Default max connections == 0 <==> no max
    public static long DEFAULT_IDLE_TIMEOUT = 10 * 60 * 1000L; // default idle timeout in ms.
    public static long DEFAULT_CLEANUP_INTERVAL = 60 * 1000L; // default cleanup thread sleep interval
    public static boolean DEFAULT_CLEANUP_ENABLE = true; // enable cleanup?
    public static long DEFAULT_QUEUE_INTERVAL =  1000L; // default queue thread sleep interval

    protected ConnectionPoolProvider(String database, String schema) {
        this.database = database;
        this.schema = schema;
    }

    /**
     * Called exactly once during {@link #get()} per (new) pool to set the parameters (max size etc.).
     *
     * @param pool
     */
    protected void setPoolParameters(ConnectionPool pool) {
        pool.setCleanupEnabled(checkValue(CONNECTION_CLEANUP_ENABLE, DEFAULT_CLEANUP_ENABLE));
        pool.setCleanupInterval(checkTime(CONNECTION_CLEANUP_INTERVAL, DEFAULT_CLEANUP_INTERVAL));
        pool.setIdelLifetime(checkTime(CONNECTION_IDLE_TIMEOUT, DEFAULT_IDLE_TIMEOUT));
        pool.setMaxSize(checkValue(CONNECTION_MAX, DEFAULT_MAX_CONNECTIONS));
        pool.setEnableQueue(checkValue(CONNECTION_ENABLE_QUEUE, false));
        pool.setQueueInterval(checkValue(CONNECTION_QUEUE_INTERVAL, DEFAULT_QUEUE_INTERVAL));
        if(0<pool.getMaxSize()){
            pool.setStackMap(new StackMap<>(pool.getMaxSize()));
        }
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

        throw new MyConfigurationException("Error: no int value specified for " + key);
    }

    /**
     * Checks long value assuming it is time. No units means it is passed back, but look at
     * the documentation in {@link ConfigUtil#getValueSecsOrMillis(String)}. IOf you just need
     * a long value, use {@link #checkValue(String, long)}.
     * @param key
     * @param defaultValue
     * @return
     */
    protected long checkTime(String key, long defaultValue) {
        String x = getAttribute(key);
        if(x!= null) return ConfigUtil.getValueSecsOrMillis(x);
        if (defaultValue != -1) return defaultValue;
        throw new MyConfigurationException("Error: no long value specified for " + key);
    }

    protected long checkValue(String key, long defaultValue) {
        String x = getAttribute(key);
        if (x != null) return Long.parseLong(x);
        if (defaultValue != -1) return defaultValue;
        throw new MyConfigurationException("Error: no long value specified for " + key);
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
