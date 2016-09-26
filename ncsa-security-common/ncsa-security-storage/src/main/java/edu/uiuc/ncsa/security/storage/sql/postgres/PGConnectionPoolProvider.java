package edu.uiuc.ncsa.security.storage.sql.postgres;

import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/12 at  8:20 AM
 */
public class PGConnectionPoolProvider extends ConnectionPoolProvider<ConnectionPool> {
    public PGConnectionPoolProvider(String database, String schema) {
        super(database, schema);
        driver = "org.postgresql.Driver";
        port = 5432;
        host = "localhost";
    }

    public PGConnectionPoolProvider(String database, String schema, String host, int port, String driver, boolean useSSL) {
        super(database, schema, host, port, driver, useSSL);
    }

    @Override
    protected boolean checkEvent(CfgEvent cfgEvent) {
        return true;
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        return null;
    }

    ConnectionPool cp;

    @Override
    public ConnectionPool get() {
        if (cp == null) {
            PostgresConnectionParameters x = new PostgresConnectionParameters(
                    checkValue(USERNAME),
                    checkValue(PASSWORD),
                    checkValue(DATABASE, database),
                    checkValue(SCHEMA, schema),
                    checkValue(HOST, host),
                    checkValue(PORT, port),
                    checkValue(DRIVER, driver),
                    checkValue(USE_SSL, useSSL));
            cp = new ConnectionPool(x);
        }
        return cp;
    }
}
