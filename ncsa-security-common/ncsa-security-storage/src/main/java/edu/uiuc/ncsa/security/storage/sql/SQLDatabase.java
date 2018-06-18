package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.util.PoolException;

import java.sql.Connection;

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
        getConnectionPool().push(c);
    }


    protected void destroyConnection(Connection c)  {
        try {
            connectionPool.doDestroy(c);
        } catch(PoolException x) {
            throw new PoolException("pool failed to destroy connection",x);
        }
    }
    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    ConnectionPool connectionPool;

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

}
