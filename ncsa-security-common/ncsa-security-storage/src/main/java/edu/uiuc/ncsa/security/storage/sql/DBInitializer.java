package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.Initializable;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class that automates the creation of tables and indices for an SQL database. This implements the
 * {@link Initializable} interface to manage create, init and destroy correctly. Note that init and destroy
 * are scoped at the schema level, that is to say destroy does not remove a database, just a given schema
 * within the database.
 * <p>Created by Jeff Gaynor<br>
 * on May 9, 2010 at  1:05:53 PM
 */
public abstract class DBInitializer extends SQLDatabase implements Initializable {

    public String getDatabaseName() {
        return getAdminConnectionParameters().getDatabaseName();
    }

    protected AdminConnectionParameters getAdminConnectionParameters() {
        return (AdminConnectionParameters) getConnectionPool().getConnectionParameters();
    }

    /**
     * The schema for the database
     *
     * @return
     */
    public String getSchema() {
        //   return schema;
        return getAdminConnectionParameters().getSchema();
    }


    /**
     * The user who will be running the server -- this *should* in general be different from the administrator
     *
     * @return
     */
    public String getUser() {
        return getAdminConnectionParameters().getClientUsername();
    }


    /**
     * The permissions are automatically given to the admin, but the user needs to be able to access the database, schema and
     * tables as well. This sets these permissions.
     *
     * @param s
     * @throws SQLException
     */
    public abstract void setPermissions(Statement s) throws SQLException;

    /**
     * This should create the schema for the database. If the schema already exists, it should be
     * removed and recreated.
     *
     * @throws SQLException
     */
    abstract public void createSchema(Statement s) throws SQLException;

    /**
     * Drops the schema if it exists. Otherwise has no effect.
     *
     * @param s
     * @throws SQLException
     */
    abstract public void dropSchema(Statement s) throws SQLException;

    /**
     * Create the tables for a given database. This should also create any indices needed.
     *
     * @param s
     * @throws SQLException
     */
    public abstract void createTables(Statement s) throws SQLException;

    /**
     * A specific call to recreate only the transaction tables. This is usable by both client and server.
     *
     * @param s
     * @throws SQLException
     */
    public abstract void recreateTransactionTables(Statement s) throws SQLException;


    /**
     * Drops the tables within the schema.
     *
     * @throws SQLException
     */

    public abstract void dropTables(Statement s) throws SQLException;


    /**
     * This will call createSchema, then createTables and setPermissions.
     *
     * @throws SQLException
     */
    protected void init2() throws SQLException {
        Connection c = getConnection();
        Statement s = c.createStatement();
        try {
            createSchema(s);
            createTables(s);
            setPermissions(s);

        } finally {
            s.close();
            c.close();
        }
    }

    public boolean init() {
        try {
            init2();
            return true;
        } catch (SQLException x) {
            throw new GeneralException(x);
        }
    }

    /**
     * This really does what destroy does. Destroy() has to catch exceptions and return booleans.
     *
     * @throws SQLException
     */
    protected void destroy2() throws SQLException {
        Connection c = getConnection();
        Statement s = c.createStatement();
        try {
            dropTables(s);
            dropSchema(s);
        } finally {
            s.close();
            c.close();
        }

    }

    public boolean destroy() {
        try {
            destroy2();
            return false;
        } catch (SQLException x) {
            return true;
        }
    }

    public boolean createNew() {
        destroy();
        init();
        return false;
    }

    public boolean isCreated() {
        return true;
    }

    public boolean isInitialized() {
        return true;
    }

    public boolean isDestroyed() {
        return false;
    }
}


