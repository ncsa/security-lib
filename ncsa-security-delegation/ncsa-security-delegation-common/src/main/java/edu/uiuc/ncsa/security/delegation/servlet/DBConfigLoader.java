package edu.uiuc.ncsa.security.delegation.servlet;

import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.LoggingConfigLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.sql.mariadb.MariaDBConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.postgres.PGConnectionPoolProvider;
import org.apache.commons.configuration.tree.ConfigurationNode;


/**
 * Configurations that deal with storage should extend this. Note that this is used extensively in OA4MP
 * though not in this module.
 * <p>Created by Jeff Gaynor<br>
 * on 1/31/13 at  3:16 PM
 */
public abstract class DBConfigLoader<T extends AbstractEnvironment> extends LoggingConfigLoader<T> {
    protected DBConfigLoader(ConfigurationNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    /**
     * Constructor to use default logging.
     *
     * @param node
     */
    protected DBConfigLoader(ConfigurationNode node) {
        super(node, null);
    }

    boolean disableDefaultStore = false;

    protected boolean isDefaultStoreDisabled(boolean... x) {
        if (x.length != 0) disableDefaultStore = x[0];
        return disableDefaultStore;
    }


    MySQLConnectionPoolProvider mySQLConnectionPoolProvider;

    // ALWAYS return a new connection provider or you will only get the same connection repeatedly (and if there
    // are multiple stores with multiple users you will get authentication errors!)
    // ALSO, these get no configuration here since this will be determined later and set by
    // the store provider. At this point which mysql instance is being used is undecidable!
    public MySQLConnectionPoolProvider getMySQLConnectionPoolProvider() {
        if (mySQLConnectionPoolProvider == null) {
            mySQLConnectionPoolProvider = getMySQLConnectionPoolProvider("oauth", "oauth");  // database, schema are set to default
        }
        return mySQLConnectionPoolProvider;
    }

    MariaDBConnectionPoolProvider mariaDBConnectionPoolProvider;

    public MariaDBConnectionPoolProvider getMariaDBConnectionPoolProvider() {
        if (mariaDBConnectionPoolProvider == null) {
            mariaDBConnectionPoolProvider = getMariaDBConnectionPoolProvider("oauth", "oauth");  // database, schema are set to default
        }
        return mariaDBConnectionPoolProvider;
    }

    public PGConnectionPoolProvider getPgConnectionPoolProvider() {
        return getPgConnectionPoolProvider("oauth", "oauth");  // database, schema are set to default
    }

    public MySQLConnectionPoolProvider getMySQLConnectionPoolProvider(String databaseName, String schema) {
        return new MySQLConnectionPoolProvider(databaseName, schema);  // database, schema are set to default
    }

    public MariaDBConnectionPoolProvider getMariaDBConnectionPoolProvider(String databaseName, String schema) {
        return new MariaDBConnectionPoolProvider(databaseName, schema);  // database, schema are set to default
    }


    public PGConnectionPoolProvider getPgConnectionPoolProvider(String databaseName, String schema) {
        return new PGConnectionPoolProvider(databaseName, schema);  // database, schema are set to default

    }


}
