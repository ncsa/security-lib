package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.util.Pool;
import edu.uiuc.ncsa.security.core.util.PoolException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A pool for jdbc connections. All this needs is configuration for the right jdbc url to use.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  4:06:15 PM
 */
public class ConnectionPool extends Pool<Connection> {

    /**
     * If this number of connections is exceeded, the pool will throw an exception.
     * This is a way to determine if the pool is leaking connections.
     */

    public ConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }

    ConnectionParameters connectionParameters;

    public ConnectionPool(SQLConnectionImpl connectionParameters) {
        this.connectionParameters = connectionParameters;
    }


    public Connection create() throws PoolException {
        try {
            Connection con = DriverManager.getConnection(getConnectionParameters().getJdbcUrl());
            return con;
        } catch (Exception x) {
            x.printStackTrace();
            throw new PoolException(x);
        }
    }

    public void destroy(Connection c) throws PoolException {
        try {
            if (!c.isClosed()) {
                c.close();
            }
        } catch (SQLException x) {
            throw new PoolException(x);
        }
    }
}
