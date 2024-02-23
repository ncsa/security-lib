package edu.uiuc.ncsa.security.storage.sql.derby;

import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;

import java.io.File;
import java.util.List;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.getFirstAttribute;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:46 PM
 */
public class DerbyConnectionPoolProvider extends ConnectionPoolProvider<ConnectionPool> implements StorageConfigurationTags {

    public DerbyConnectionPoolProvider(String database,
                                       String schema,
                                       String host,
                                       int port,
                                       String driver,
                                       boolean useSSL) {
        super(database, schema, host, port, driver, useSSL);
    }

    public DerbyConnectionPoolProvider(String database, String schema) {
        super(database, schema);
        driver = "org.apache.derby.jdbc.EmbeddedDriver";
        port = 1527;
        host = "localhost";

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
        storeType = getFirstAttribute(getConfig(), DERBY_STORE_TYPE)==null?"DERBY_STORE_TYPE_MEMORY":getFirstAttribute(getConfig(), DERBY_STORE_TYPE);
        if (DERBY_STORE_TYPE_FILE.equals(storeType)) {
            // It is possible some of these are missing
            String x = getFirstAttribute(getConfig(), USERNAME);
            if (x == null) {
                username = DERBY_FS_DEFAULT_USER;
            } else{
                username = x;
            }
            x = getFirstAttribute(getConfig(), PASSWORD);
            if (x == null) {
                password = DERBY_FS_DEFAULT_PASSWORD;
            }else{
                password = x;
            }
            x = getFirstAttribute(getConfig(), BOOT_PASSWORD);
            if (x == null) {
                bootPassword = DERBY_FS_DEFAULT_BOOT_PASSWORD;
            }else{
                bootPassword = x;
            }
            // options are to set the path and let the system manage everything
            // or set the databaseName which is the exact path to the database
            x = getFirstAttribute(getConfig(), FS_PATH);
            if(x == null){
                throw new IllegalArgumentException("file stores require a path");
            }
            String db = getFirstAttribute(getConfig(), DATABASE);
            if (x != null) {
                if (StringUtils.isTrivial(db)) {
                    rootDirectory = x;
                    File f = new File(x, DERBY_STORE); // let the file system figure out the path
                    database = f.getAbsolutePath();
                }
            }
            x = getFirstAttribute(getConfig(), SCHEMA);
            if(x==null){
                schema = DERBY_FS_DEFAULT_SCHEMA;
            }else{
                schema = x;
            }
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

    public List<String> getCreateScript() {
        if (createScript == null) {
            String path = getCreateScriptPath();
            if (!StringUtils.isTrivial(path)) {
                createScript = SQLStore.crappySQLParser(path);
            }
        }
        return createScript;
    }

    public void setCreateScript(List<String> createScript) {
        this.createScript = createScript;
    }

    List<String> createScript = null;
}
