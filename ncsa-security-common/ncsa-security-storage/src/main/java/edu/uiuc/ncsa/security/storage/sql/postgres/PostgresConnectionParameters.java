package edu.uiuc.ncsa.security.storage.sql.postgres;

import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;
import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Jun 23, 2010 at  7:56:23 PM
 */
public class PostgresConnectionParameters extends SQLConnectionImpl {
    public PostgresConnectionParameters(JSONObject jsonObject) {
        super(jsonObject);
    }

    public PostgresConnectionParameters(
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
        super(username, password, databaseName, schema,  host, port,jdbcDriver, useSSL, parameters );
    }

    /**
     * The jdbc url for non-admin tasks. If no username and password is set, then don't use one.
     *
     * @return
     */
    public String getJdbcUrl() {
        if (username == null) {
            // note that use SSL is ignored if there is no username, since it makes no sense in that case.
            return String.format("jdbc:postgresql://%s:%d/%s", host, port, databaseName);
        }
        String jdbcURL = "jdbc:postgresql://%s:%d/%s?user=%s&password=%s" + (useSSL?"&ssl=true":"");
        jdbcURL = addParameters(jdbcURL);
        return String.format(jdbcURL,host, port, databaseName, username, password);
    }



}
