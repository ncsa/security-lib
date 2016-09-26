package edu.uiuc.ncsa.security.storage.sql.h2;

import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:46 PM
 */
public class H2ConnectionPoolProvider extends ConnectionPoolProvider<ConnectionPool> {
    /**
     * the default port is 8084 for localhost connections. Non-local-host connections should specify port 8085
     * (for a standard install) and use ssl.
     *
     */

    public H2ConnectionPoolProvider(String database, String schema, String host, int port, String driver, boolean useSSL) {
        super(database, schema, host, port, driver, useSSL);
    }

    public H2ConnectionPoolProvider(String database, String schema) {
        super(database, schema);
        driver = "org.h2.Driver";
        port = 8084;
        host = "localhost";
    }

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
        H2ConnectionParameters x = new H2ConnectionParameters(
                  checkValue(USERNAME),
                  checkValue(PASSWORD),
                  checkValue(DATABASE, database),
                  checkValue(SCHEMA, schema),
                  checkValue(HOST, host),
                  checkValue(PORT, port),
                  checkValue(DRIVER, driver),
                checkValue(USE_SSL, useSSL)
          );
          return  new ConnectionPool(x);
    }
}
