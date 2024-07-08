package edu.uiuc.ncsa.security.storage.sql.derby;

import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;

import java.io.File;
import java.util.List;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.getFirstAttribute;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:46 PM
 */
public class DerbyConnectionPoolProvider extends ConnectionPoolProvider<ConnectionPool> implements StorageConfigurationTags {
    public DerbyConnectionPoolProvider() {
        // Wholly reasonable defaults for superclass.
        driver = "org.apache.derby.jdbc.EmbeddedDriver";
        port = 1527;
        host = "localhost";
        schema = DERBY_FS_DEFAULT_SCHEMA;
    }

    /**
     * Use this for builder/factory pattern.
     *
     * @return
     */
    public static DerbyConnectionPoolProvider newInstance() {
        return new DerbyConnectionPoolProvider();
    }

    public DerbyConnectionPoolProvider(String database,
                                       String schema,
                                       String host,
                                       int port,
                                       String driver,
                                       boolean useSSL) {
        super(database, schema, host, port, driver, useSSL);
    }


    public DerbyConnectionPoolProvider(String database) {
        this(database, DERBY_FS_DEFAULT_SCHEMA);
    }

    public DerbyConnectionPoolProvider(String database, String schema) {
        super(database, schema);
        driver = "org.apache.derby.jdbc.EmbeddedDriver";
        port = 1527;
        host = "localhost";
    }

    public String getStoreType() {
        return storeType;
    }

    public DerbyConnectionPoolProvider setStoreType(String storeType) {
        this.storeType = storeType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DerbyConnectionPoolProvider setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DerbyConnectionPoolProvider setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getBootPassword() {
        return bootPassword;
    }

    public DerbyConnectionPoolProvider setBootPassword(String bootPassword) {
        this.bootPassword = bootPassword;
        return this;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public DerbyConnectionPoolProvider setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
        return this;
    }

    String storeType = null;

    @Override
    protected boolean checkEvent(CfgEvent cfgEvent) {
        return false;
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        return null;
    }

    public String getCreateScriptPath() {
        return getFirstAttribute(getConfig(), DERBY_CREATE_SCRIPT);
    }

    String username = "";
    String password = "";
    String bootPassword = "";

    String rootDirectory = ""; // for file stores.

    @Override
    public ConnectionPool get() {
        // Reminder that these are pulling the values from the XML configuration directly,
        // hence the check and default values.
        if (storeType == null) {
            storeType = getFirstAttribute(getConfig(), DERBY_STORE_TYPE) == null ? DERBY_STORE_TYPE_MEMORY : getFirstAttribute(getConfig(), DERBY_STORE_TYPE);
        }
        if (DERBY_STORE_TYPE_FILE.equals(storeType)) {
            if (getCreateScriptPath() != null) {
                // Then they are injecting the script in the configuration. Use it.
                createScript = SQLStore.crappySQLParser(getCreateScriptPath());
            }
            username = getVar(username, USERNAME, DERBY_FS_DEFAULT_USER);
            password = getVar(password, PASSWORD, DERBY_FS_DEFAULT_PASSWORD);
            bootPassword = getVar(bootPassword, BOOT_PASSWORD, DERBY_FS_DEFAULT_BOOT_PASSWORD);
            rootDirectory = getVar(rootDirectory, FS_PATH, null);
            schema = getVar(schema, SCHEMA, DERBY_FS_DEFAULT_SCHEMA);
            if (rootDirectory == null) {
                throw new IllegalArgumentException("file stores require a path");
            }
            database = getVar(database, DATABASE, new File(rootDirectory, DERBY_STORE).getAbsolutePath());
            // options are to set the path and let the system manage everything
            // or set the databaseName which is the exact path to the database
            // They can set the root directory directly or we can pull it out of the
            // configuration as the value of the path attribute.
        }

        /*

        connect 'jdbc:derby:/home/ncsa/dev/derby/oa4mp;create=true;dataEncryption=true;bootPassword=o7MtXykd;user=oa4mp';

        CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.connection.requireAuthentication', 'true');
        CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.sqlAuthorization','true');
        CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.authentication.provider', 'BUILTIN');
        CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user.oa4mp', '6eXSbO_W');
        CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.propertiesOnly', 'true');

         */

        DerbyConnectionParameters x = new DerbyConnectionParameters(
                username,
                password,
                rootDirectory,
                checkValue(DATABASE, database),
                checkValue(SCHEMA, schema),
                checkValue(HOST, host),
                checkValue(PORT, port),
                checkValue(DRIVER, driver),
                checkValue(USE_SSL, useSSL),
                checkValue(DERBY_STORE_TYPE, storeType),
                bootPassword,
                checkValue(PARAMETERS, "")
        );
        return createNewPool(x);
    }

    protected String getVar(String var, String key, String defaultValue) {
        if (isTrivial(var)) {
            String x = getFirstAttribute(getConfig(), key);
            if (x == null) {
                return defaultValue;
            } else {
                return x;
            }
        }
        return var;
    }

    DerbyConnectionPool pool = null; // There can be only one.

    /**
     * So this may be overriden.
     *
     * @param x
     * @return
     */
    public DerbyConnectionPool createNewPool(DerbyConnectionParameters x) {
        if (pool == null) {
            pool = new DerbyConnectionPool(x, getCreateScript());
            // note that since the database might be created, there has to be a single pool.
            // otherwise this will make several copies of the database -- one per table. this will throw an exception
            // since such a database exists.
        }
        return pool;
    }

    /**
     * The default creation script is injected
     *
     * @return
     */
    public List<String> getCreateScript() {
        if (createScript == null) {
            String path = getCreateScriptPath();
            if (!isTrivial(path)) {
                createScript = SQLStore.crappySQLParser(path);
            }
        }
        return createScript;
    }

    public DerbyConnectionPoolProvider setCreateScript(List<String> createScript) {
        this.createScript = createScript;
        return this;
    }

    List<String> createScript = null;

    @Override
    public DerbyConnectionPoolProvider setHost(String host) {
        return (DerbyConnectionPoolProvider) super.setHost(host);
    }

    @Override
    public DerbyConnectionPoolProvider setDriver(String driver) {
        return (DerbyConnectionPoolProvider) super.setDriver(driver);
    }

    @Override
    public DerbyConnectionPoolProvider setPort(int port) {
        return (DerbyConnectionPoolProvider) super.setPort(port);
    }

    @Override
    public DerbyConnectionPoolProvider setSchema(String schema) {
        return (DerbyConnectionPoolProvider) super.setSchema(schema);
    }

    @Override
    public DerbyConnectionPoolProvider setDatabase(String database) {
        return (DerbyConnectionPoolProvider) super.setDatabase(database);
    }

    @Override
    public DerbyConnectionPoolProvider setUseSSL(boolean useSSL) {
        return (DerbyConnectionPoolProvider) super.setUseSSL(useSSL);
    }
}
