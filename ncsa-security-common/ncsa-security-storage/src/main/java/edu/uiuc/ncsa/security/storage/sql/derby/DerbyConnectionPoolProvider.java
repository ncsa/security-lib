package edu.uiuc.ncsa.security.storage.sql.derby;

import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:46 PM
 */
public class DerbyConnectionPoolProvider extends ConnectionPoolProvider<ConnectionPool> {

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

    boolean inMemory = false;
    String bootPassword = "";
    @Override
    protected boolean checkEvent(CfgEvent cfgEvent) {
        return false;
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        return null;
    }


    @Override
    public ConnectionPool get() {
        DerbyConnectionParameters x = new DerbyConnectionParameters(
                checkValue(USERNAME),
                checkValue(PASSWORD),
                checkValue(DATABASE, database),
                checkValue(SCHEMA, schema),
                checkValue(HOST, host),
                checkValue(PORT, port),
                checkValue(DRIVER, driver),
                checkValue(USE_SSL, useSSL),
                checkValue(IN_MEMORY, inMemory),
                checkValue(BOOT_PASSWORD, bootPassword),
                checkValue(PARAMETERS, "")
        );
        return new ConnectionPool(x, ConnectionPool.CONNECTION_TYPE_DEBRY);
    }
}
