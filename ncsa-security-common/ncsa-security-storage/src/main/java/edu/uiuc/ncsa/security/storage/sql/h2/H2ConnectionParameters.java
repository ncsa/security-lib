package edu.uiuc.ncsa.security.storage.sql.h2;

import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;

/**
 * NOTE that the database name is the complete file path to the directory, e.g. "~/test"
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:43 PM
 */
public class H2ConnectionParameters extends SQLConnectionImpl {
    @Override
    public String getJdbcUrl() {
        //
        return "jdbc:h2:" + (useSSL?"ssl":"tcp") + "://" + host  + (0<port?":"+port:"")  + "/" + databaseName;
    }

    public H2ConnectionParameters(String username,
                                  String password,
                                  String databaseName,
                                  String schema,
                                  String host,
                                  int port,
                                  String jdbcDriver,
                                  boolean useSSL) {
        super(username, password, databaseName, schema, host, port, jdbcDriver, useSSL);

    }


}
