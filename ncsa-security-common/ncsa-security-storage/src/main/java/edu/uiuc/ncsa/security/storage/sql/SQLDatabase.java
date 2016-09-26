package edu.uiuc.ncsa.security.storage.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * /**
 * Top-level SQL object. This simply maintains a connection pool to a database.
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  12:58:14 PM
 */
public class SQLDatabase {
    public SQLDatabase(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    public SQLDatabase() {
    }

    public Connection getConnection() {
        return getConnectionPool().pop();
    }

    /**
     * Put the connection back on the stack for future use. This should be invoked in a finally clause at the end
     * of every method that uses a connection.
     *
     * @param c
     */
    public void releaseConnection(Connection c) {
        boolean  skipIt = false;
        try {
            skipIt = c.isClosed();
        } catch (SQLException x) {

        }
        if(!skipIt){
            getConnectionPool().push(c);
        }
    }

    /**
     * Actually destroy the connection. This is normally called if an {@link java.sql.SQLException}
     * is encountered since it will be unclear in general if the exception is caused by a stale connection
     * or an SQL error.
     *
     * @param c
     */
    protected void destroyConnection(Connection c) {
        getConnectionPool().realDestroy(c);

    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    ConnectionPool connectionPool;

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

}
