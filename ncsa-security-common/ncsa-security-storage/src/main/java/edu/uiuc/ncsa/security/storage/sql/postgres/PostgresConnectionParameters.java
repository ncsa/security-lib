package edu.uiuc.ncsa.security.storage.sql.postgres;

import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Jun 23, 2010 at  7:56:23 PM
 */
public class PostgresConnectionParameters extends SQLConnectionImpl {

    public PostgresConnectionParameters(
            String username,
            String password,
            String databaseName,
            String schema,
            String host,
            int port,
            String jdbcDriver,
            boolean useSSL
    ) {
        super(username, password, databaseName, schema,  host, port,jdbcDriver, useSSL );
    }

    /**
     * The jdbc url for non-admin tasks. If no username and password is set, then don't use one.
     *
     * @return
     */
    public String getJdbcUrl() {
        if (username == null) {
            return String.format("jdbc:postgresql://%s:%d/%s", host, port, databaseName);
        }
        return String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s" + (useSSL?"&ssl=true":""),
                host, port, databaseName, username, password);
    }



}
