package edu.uiuc.ncsa.security.storage.sql.mysql;

import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/12 at  11:54 AM
 */
public class MySQLConnectionPoolProvider extends ConnectionPoolProvider<MySQLConnectionPool> {


    /**
     * Sets the defaults for this connection.
     * @param database
     * @param schema
     * @param host
     * @param port
     * @param driver
     */
    public MySQLConnectionPoolProvider(String database, String schema,  String host, int port, String driver, boolean useSSL) {
        super(database, schema, host, port, driver, useSSL);
    }

    /**
     * Another constructor, accepting the standard mysql defaults for driver, host and port.
     * @param database
     * @param schema
     */
     public MySQLConnectionPoolProvider(String database, String schema) {
         super(database, schema);
         driver = "com.mysql.jdbc.Driver";
         port = 3306;
         host = "localhost";
    }


    @Override
    public Object componentFound(CfgEvent configurationEvent) {

        return null;
    }

    @Override
    public MySQLConnectionPool get() {
          MySQLConnectionParameters x = new MySQLConnectionParameters(
                    checkValue(USERNAME),
                    checkValue(PASSWORD),
                    checkValue(DATABASE, database),
                    checkValue(SCHEMA, schema),
                    checkValue(HOST, host),
                    checkValue(PORT, port),
                    checkValue(DRIVER, driver),
                  checkValue(USE_SSL, useSSL)
            );
            return  new MySQLConnectionPool(x);
    }

    @Override
    protected boolean checkEvent(CfgEvent cfgEvent) {
        return false;
    }
}
