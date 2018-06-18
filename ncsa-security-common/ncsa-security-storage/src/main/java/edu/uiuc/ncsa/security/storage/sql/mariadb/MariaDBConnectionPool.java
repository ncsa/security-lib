package edu.uiuc.ncsa.security.storage.sql.mariadb;

import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;

/**
 * Original readme: A specific pool for working with MariaDB. Since MySQL can accumulate stale connections
 * and MariaDB is based on MySQL
 * (and not all database drivers implement an isValid method) specific machinery for
 * testing for stale connections is included. Instantiate this as your pool when you
 * are talking to a MariaDB database. <br/><br/>
 * This is now almost deprecated. improvements to the JDBC driver now do allow for testing against valid connections
 * properly so the machinery that was here is no longer needed. This will be kept for a bit longer though will
 * probably just be removed at some point.
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on Jun 21, 2011 at  3:18:31 PM
 */
public class MariaDBConnectionPool extends ConnectionPool {

    public MariaDBConnectionPool(SQLConnectionImpl connectionParameters) {
        super(connectionParameters);
    }
}
