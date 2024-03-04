package edu.uiuc.ncsa.security.storage.sql.derby;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionRecord;
import edu.uiuc.ncsa.security.storage.sql.StackMap;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags.DERBY_STORE_TYPE_FILE;
import static edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags.DERBY_STORE_TYPE_MEMORY;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/22/24 at  10:43 AM
 */
public class DerbyConnectionPool extends ConnectionPool {
    public DerbyConnectionPool(DerbyConnectionParameters connectionParameters, List<String> createScript) {
        setConnectionParameters(connectionParameters);
        this.createScript = createScript;
        setStackMap(new StackMap<>());
        setType(CONNECTION_TYPE_DEBRY);
        setupDriverManager();
    }

    @Override
    public DerbyConnectionParameters getConnectionParameters() {
        return (DerbyConnectionParameters) super.getConnectionParameters();
    }

    public List<String> getCreateScript() {
        return createScript;
    }

    public void setCreateScript(List<String> createScript) {
        this.createScript = createScript;
    }

    List<String> createScript;

    public boolean hasCreateScript() {
        return createScript != null && !createScript.isEmpty();
    }


    boolean alreadyCreated = false;
    boolean DEEP_DEBUG = false;

    void dbg(String x) {
        if (DEEP_DEBUG) {
            System.out.println(getClass().getSimpleName() + ":" + x);
        }
    }

    /*
    There is an argument that this should be a static method in SQLStore. However, at this point having
    the system create non-Derby databases seems like remarkably bad store management. Derby only creates
    file stores and memory stores.   N.B. This is used in OA4MP during servlet initialization.
     */
    public void createStore() {
        dbg("starting");
        dbg("parameters:\n" + getConnectionParameters());
        if (alreadyCreated || !getConnectionParameters().isCreateOne()) {
            dbg("returning");
            return;
        }
        if (!hasCreateScript()) {
            throw new IllegalStateException("missing creation script for derby. Cannot initialize store");
        }
        ConnectionRecord cr = null;
        Connection c;
        try {
            if (isFileStore()) {
                /*
                 default connection to any created filestore from Derby's ij tool (SET THE RIGHT DATABASE PATH!)

                 connect 'jdbc:derby:/tmp/test/derby;user=oa4mp;dataEncryption=true;password=Asdfghjkl123;bootPassword=Qwertyuiop321';

                 Derby requires a special type of connection when making a database,
                 It does not use this for general connections.
                 The following parameters will be set in the database and do not need to be set later.
                 This creates the user and sets the password for this database.

                 */
                File f = new File(getConnectionParameters().rootDirectory);
                if (f.exists()) {
                    if (!f.isDirectory()) {
                        throw new IllegalArgumentException("the path \"" + getConnectionParameters().rootDirectory + "\" for the store exists and is not a directory.");
                    }
                } else {
                    f.mkdirs();
                    if (!f.exists()) {
                        // a return code of true says the all directories were made -- not that quite that thye exit
                        throw new IllegalArgumentException("the path \"" + getConnectionParameters().rootDirectory + "\" for the store exists and is not a directory.");
                    }
                }
                // Derby makes the database directory.


                c = DriverManager.getConnection(getConnectionParameters().getCreateURL());

            } else {
                cr = pop();
                c = cr.connection;
            }
            Statement stmt = c.createStatement();
            for (String s : createScript) {
                dbg(" executing:\n" + s);
                boolean rc = stmt.execute(s);
                dbg("update count = " + stmt.getUpdateCount());
                //System.out.println("update result set = " + stmt.getResultSet());
                dbg("rc = " + rc);
            }
            alreadyCreated = true;
            stmt.close();
            Properties oldProperties;
            if (isFileStore()) {
                // If you close the connection to an in-memory database, it goes away!
                // but you need to close it if creating an embedded database on the local file system.
                oldProperties = setProperties();

                c.close();
                getConnectionParameters().setCreateOne(false);
                getConnectionParameters().setCreateURL(null);
                // shut it down properly to get all the right database attributes
                // or the system will try to create it on the next access.
                shutdown();
                unsetProperties(oldProperties);
                setupDriverManager(); // re-initialize the driver or all calls will fail.
            } else {
                getConnectionParameters().setCreateOne(false);
                getConnectionParameters().setCreateURL(null);
                push(cr);
            }
        } catch (Throwable sqlx) {
            sqlx.printStackTrace();
            if (!isFileStore()) {
                destroy(cr);
            }
            if (sqlx instanceof RuntimeException) {
                throw (RuntimeException) sqlx;
            }
            throw new GeneralException("error creating derby store:" + sqlx.getMessage());
        }

    }

    protected Properties getDerbySecurityProperties() {
        Properties derbySecurityProperties = new Properties();
        derbySecurityProperties.setProperty("derby.connection.requireAuthentication", "true");
        derbySecurityProperties.setProperty("derby.database.sqlAuthorization", "true");
        derbySecurityProperties.setProperty("derby.authentication.provider", "BUILTIN");
        derbySecurityProperties.setProperty("derby.user." + getConnectionParameters().getUsername(), getConnectionParameters().getPassword());  // sets user name and password
        derbySecurityProperties.setProperty("derby.database.propertiesOnly", "true");
        return derbySecurityProperties;
    }

    /**
     * Sets ths derby properties for creating the database. Once these are set the newly created database has
     * the correct security internal to the database, including setting the user name and password.
     * However, as long as these are set, this means any subsequent access
     * can only be to this database, unless this is undone by {@link #unsetProperties(Properties)}.
     * When creating a database, set these for the minimum time required, then unset them or you
     * will get all manner of authentication failures! <br/><br/>
     * <b>In particular</b> if these are set, then you can only use a single database and attempts to
     * connect to another database (unless it has identical password, bootpassword and user) will
     * always fail.
     *
     * @return
     */
    protected Properties setProperties() {
        Properties oldProperties = new Properties();
        Properties derbySP = getDerbySecurityProperties();
        Properties systemProperties = System.getProperties();

        for (String key : derbySP.stringPropertyNames()) {
            Object oldValue = systemProperties.setProperty(key, derbySP.getProperty(key));
            if (oldValue != null) {
                oldProperties.put(key, oldValue); // can't set a null property value
            }
        }
        return oldProperties;
    }

    protected void unsetProperties(Properties oldProperties) {
        Properties derbySP = getDerbySecurityProperties();
        Properties systemProperties = System.getProperties();

        for (String key : derbySP.stringPropertyNames()) {
            if (oldProperties.containsKey(key)) {
                systemProperties.put(key, oldProperties.get(key));
            } else {
                systemProperties.remove(key);
            }
        }
    }

    public boolean isMemoryStore() {
        return getConnectionParameters().getStoreType().equals(DERBY_STORE_TYPE_MEMORY);
    }

    public boolean isFileStore() {
        return getConnectionParameters().getStoreType().equals(DERBY_STORE_TYPE_FILE);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (alreadyShutdown) return;
        // close all connections, then connect with the shutdown URL.
        // This is necessary since for file stores, lock files will remain
        // unless cleaned up and this will prevent the system from restarting.
        for (Object x : getStackMap().getMap().values()) {
            if (x instanceof ConnectionRecord) {
                ConnectionRecord cr = (ConnectionRecord) x;
                try {
                    if (!cr.connection.isClosed()) {
                        cr.connection.close();
                    }
                } catch (Exception t) {
                    DebugUtil.trace("error closing connection :" + t.getMessage());
                }
            }
        }
        try {
            Connection con = DriverManager.getConnection(getConnectionParameters().getShutdownURL());
            con.close();
        } catch (SQLException sqlException) {
            DebugUtil.trace("error shutting down database\"" + getConnectionParameters().getDatabaseName() + "\"  :" + sqlException.getMessage());

        }


    }
}
