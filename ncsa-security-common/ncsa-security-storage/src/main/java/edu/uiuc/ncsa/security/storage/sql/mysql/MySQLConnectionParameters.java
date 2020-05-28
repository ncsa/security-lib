package edu.uiuc.ncsa.security.storage.sql.mysql;

import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;

import java.util.TimeZone;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 19, 2011 at  12:40:13 PM
 */
public class MySQLConnectionParameters extends SQLConnectionImpl {
    public MySQLConnectionParameters(
            String username,
            String password,
            String databaseName,
            String schema,
            String host,
            int port,
            String jdbcDriver,
            boolean useSSL,
            String parameters
    ) {
        super(username, password, databaseName, schema, host, port, jdbcDriver, useSSL,parameters);
    }

    @Override
    public String getJdbcUrl() {
        String jdbcURL = addParameters("jdbc:mysql://%s:%d/%s?characterEncoding=utf8&user=%s&password=%s"
                       + "&serverTimezone=" + TimeZone.getDefault().getID()
                       + (useSSL?"&useSSL=true":""));

        return String.format(jdbcURL,
                host, port, schema, username, password);
    }

}
