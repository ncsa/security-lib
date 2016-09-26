package edu.uiuc.ncsa.security.storage.sql.postgres;

import edu.uiuc.ncsa.security.storage.sql.AdminConnectionParameters;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Jun 23, 2010 at  8:10:02 PM
 */
public class PostgresAdminConnectionParameters extends AdminConnectionParameters {
  public PostgresAdminConnectionParameters(
          String username,
          String password,
          String databaseName,
          String schema,
          String host,
          int port,
          String jdbcDriver,
          String clientUsername,
          boolean useSSL) {
        super(username, password,databaseName,schema,  host,  port, jdbcDriver, clientUsername, useSSL);
    }

    public String getJdbcUrl() {
        String url;
        if (username == null) {
            return String.format("jdbc:postgresql://%s:%d/",
                    host,
                    port);
        }
        return String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s" + (useSSL?"&ssl=true":""),// &security=true
                host,
                port,
                databaseName,
                username,
                password);
    }
}
