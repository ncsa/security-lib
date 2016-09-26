package edu.uiuc.ncsa.security.storage.sql.mysql;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.PoolException;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A specific pool for working with MySQL. Since MySQL can accumulate stale connections
 * (and not all database drivers implement an isValid method) specific machinery for
 * testing for stale connections is included. Instantiate this as your pool when you
 * are talking to a MySQL database.
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on Jun 21, 2011 at  3:18:31 PM
 */
public class MySQLConnectionPool extends ConnectionPool {

    public static int IS_VALID_TIMEOUT = 100;
    public static int MAX_RETRIES = 10;


    public MySQLConnectionPool(SQLConnectionImpl connectionParameters) {
        super(connectionParameters);
    }

    // Fix for CIL-299. Possible race condition from partially synchronized pop method.
    synchronized protected Connection pop(int retries) {
        if (retries <= 0) {
            throw new GeneralException("Error: Max retries exceeded");
        }
        Connection c = super.pop();
        try {
            if (!c.isClosed() && c.isValid(IS_VALID_TIMEOUT)) {
                return c;
            }
            destroy(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pop(retries - 1);

    }

    @Override
    public Connection pop() throws PoolException {
        return pop(MAX_RETRIES);
    }
}
