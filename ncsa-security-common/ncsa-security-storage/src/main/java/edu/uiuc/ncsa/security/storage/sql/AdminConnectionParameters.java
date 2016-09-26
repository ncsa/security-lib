package edu.uiuc.ncsa.security.storage.sql;

/**
 * Databases might require access as a specific administrator. This allows for that.
 * <p>Created by Jeff Gaynor<br>
 * on Jun 23, 2010 at  8:09:12 AM
 */
abstract public class AdminConnectionParameters extends SQLConnectionImpl {


    protected AdminConnectionParameters(
            String username,
            String password,
            String databaseName,
            String schema,
            String host,
            int port,
            String jdbcDriver,
            String clientUsername,
            boolean useSSL) {
        super(username, password,databaseName,schema,  host,  port, jdbcDriver, useSSL);
        this.clientUsername = clientUsername;
    }

    /**
     * The name of the user who will be accessing the database. This is used to set permissions.
     * Contrast to the username here which in this case is the name of the administrator.
     *
     * @return
     */
    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    String clientUsername;
}
