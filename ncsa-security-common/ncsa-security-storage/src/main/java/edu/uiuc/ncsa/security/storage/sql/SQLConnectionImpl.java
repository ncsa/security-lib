package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;

/**
 * General connection parameters object for a relational database. These properties are standard for every such
 * database, the only difference really is how the JDBC urls are made, which is vendor specific.
 * <p>Created by Jeff Gaynor<br>
 * on Jun 23, 2010 at  8:01:59 AM
 */
abstract public class SQLConnectionImpl implements ConnectionParameters {

    protected SQLConnectionImpl(
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
        this.databaseName = databaseName;
        this.host = host;
        this.jdbcDriver = jdbcDriver;
        this.password = password;
        this.port = port;
        this.schema = schema;
        this.username = username;
        this.useSSL = useSSL;
        this.parameters = parameters;
        init();
    }

    /**
     * Add parameters from the configuration file if they exist. The parameter string is of the form
     * <pre>
     *     key0=value0&key1=value1&key2=value2...
     * </pre>
     * <b>NOTE:</b> this method does not set the ssl connection parameter -- tjhat should be done before invoking this method
     * because that is very vendor specific. This method passes along whatever parameters to the driver the user needs or skips them
     * if there are none.
     * @param jdbcURL
     * @return
     */
    protected String addParameters(String jdbcURL) {
        String p = null;
        if (getParameters() != null && !getParameters().isEmpty()) {
            p = getParameters();
        }
        if (p != null) {
            if (p.startsWith("&")) {
                p = p.substring(1); // drop initial "&"
            }
            if (p.endsWith("&")) {
                p = p.substring(0, p.length() - 1); //shave off final &
            }
            jdbcURL = jdbcURL + "&" + p;
        }

        return jdbcURL;
    }

    public String getParameters() {
        return parameters;
    }

    String parameters;

    @Override
    public String getUsername() {
        return username;
    }

    protected boolean useSSL = false;

    protected void init() {
        if (jdbcDriver == null) {
            throw new MyConfigurationException("Missing JDBC driver");
        }
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new MyConfigurationException("Class not found. Could not load JDBC driver \"" + jdbcDriver + "\"");
        }

    }


    protected String host;
    protected String username;
    protected String password;
    protected int port;
    protected String jdbcDriver;
    protected String databaseName;
    protected String schema;


    public String getDatabaseName() {
        return databaseName;
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        String x = getClass().getSimpleName() + "[";
        x = x + "username=" + username;
        x = x + ", password=" + password;
        x = x + ", database=" + databaseName;
        x = x + ", schema=" + schema;
        x = x + ", host=" + host;
        x = x + ", port=" + port;
        x = x + ", jdbcDriver=" + jdbcDriver;
        x = x + ", useSSL? " + useSSL;
        x = x + ", parameters? " + parameters;
        x = x + ", jdbcURL=" + getJdbcUrl();

        x = x + "]";
        return x;
    }

    boolean compareString(String x, String y) {
        if (x == null) {
            if (y == null) return true;
            return false;
        } else {
            if (y == null) return false;
        }
        return x.equals(y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof SQLConnectionImpl)) return false;
        SQLConnectionImpl z = (SQLConnectionImpl) obj;
        if (!compareString(username, z.username)) return false;
        if (!compareString(password, z.password)) return false;
        if (!compareString(schema, z.schema)) return false;
        if (!compareString(databaseName, z.databaseName)) return false;
        if (!compareString(host, z.host)) return false;
        if (!compareString(jdbcDriver, z.jdbcDriver)) return false;
        // does not compare parameters since that is not well-defined....
        if (port != z.port) return false;
        if (useSSL != z.useSSL) return false;
        return true;
    }
}
