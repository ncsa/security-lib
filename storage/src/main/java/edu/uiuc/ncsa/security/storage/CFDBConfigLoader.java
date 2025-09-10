package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.CFLoggingConfigLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.sql.derby.DerbyConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.mariadb.MariaDBConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.postgres.PGConnectionPoolProvider;

/**
 * Configurations that deal with storage should extend this. Note that this is used extensively in OA4MP
 * though not in this module.
 * <p>Created by Jeff Gaynor<br>
 * on 1/31/13 at  3:16 PM
 */
public abstract class CFDBConfigLoader<T extends AbstractEnvironment> extends CFLoggingConfigLoader<T> {
    protected CFDBConfigLoader(CFNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    /**
     * Constructor to use default logging.
     *
     * @param node
     */
    protected CFDBConfigLoader(CFNode node) {
        super(node, null);
    }

    boolean disableDefaultStore = true; // Fix for CIL-502: Require that database explicitly enable in-memory stores to use them.

    protected boolean isDefaultStoreDisabled(boolean... x) {
        if (x.length != 0) disableDefaultStore = x[0];
        return disableDefaultStore;
    }


    protected  MySQLConnectionPoolProvider mySQLConnectionPoolProvider;


    public MySQLConnectionPoolProvider getMySQLConnectionPoolProvider() {
        if (mySQLConnectionPoolProvider == null) {
            mySQLConnectionPoolProvider = getMySQLConnectionPoolProvider("oauth", "oauth");  // database, schema are set to default
        }
        return mySQLConnectionPoolProvider;
    }

    protected  MariaDBConnectionPoolProvider mariaDBConnectionPoolProvider;

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
        if (mySQLConnectionPoolProvider == null) {

            mySQLConnectionPoolProvider = new MySQLConnectionPoolProvider(databaseName, schema);  // database, schema are set to default
        }
        return mySQLConnectionPoolProvider;
    }

    public MariaDBConnectionPoolProvider getMariaDBConnectionPoolProvider(String databaseName, String schema) {
        if (mariaDBConnectionPoolProvider == null) {
            mariaDBConnectionPoolProvider = new MariaDBConnectionPoolProvider(databaseName, schema);  // database, schema are set to default
        }
        return mariaDBConnectionPoolProvider;
    }

    protected DerbyConnectionPoolProvider derbyConnectionPoolProvider;
    // need no-arg constructor whose values will be overriden later from configuration.

    public DerbyConnectionPoolProvider getDerbyConnectionPoolProvider() {
        return getDerbyConnectionPoolProvider("oa4mp", DerbyConnectionPoolProvider.DERBY_FS_DEFAULT_SCHEMA);
    }

    public DerbyConnectionPoolProvider getDerbyConnectionPoolProvider(String databaseName, String schema) {
        if (derbyConnectionPoolProvider == null) {
            derbyConnectionPoolProvider = new DerbyConnectionPoolProvider(databaseName, schema);
        }
        return derbyConnectionPoolProvider;
    }

    protected  PGConnectionPoolProvider pgConnectionPoolProvider = null;

    public PGConnectionPoolProvider getPgConnectionPoolProvider(String databaseName, String schema) {
        if (pgConnectionPoolProvider == null) {
            pgConnectionPoolProvider = new PGConnectionPoolProvider(databaseName, schema);  // database, schema are set to default
        }
        return pgConnectionPoolProvider;
    }
}

