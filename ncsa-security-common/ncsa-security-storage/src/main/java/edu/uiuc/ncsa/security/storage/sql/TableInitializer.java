package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.Initializable;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

import java.sql.Connection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/3/12 at  3:21 PM
 */
public class TableInitializer extends SQLDatabase implements Initializable {
    public TableInitializer(ConnectionPool connectionPool, Table table) {
        super(connectionPool);
        this.table = table;
    }

    Table table;
   boolean isNew = false;
    boolean isDestroyed = false;
    boolean isInit = false;
    @Override
    public boolean createNew() {
        isNew = false;
        Connection c = getConnection();
        try {
            ConnectionParameters cp = getConnectionPool().getConnectionParameters();
            String username = cp.getUsername();
            if(cp instanceof AdminConnectionParameters){
                AdminConnectionParameters acp = (AdminConnectionParameters) cp;
                username = ((AdminConnectionParameters) cp).getClientUsername();
            }

            java.sql.Statement stmt = c.createStatement();
            stmt.execute(table.createTableStatement());
            stmt.execute("GRANT SELECT,INSERT,UPDATE TO " + table.getFQTablename() + " TO " + username);
            stmt.close();
            isNew = true;
            isInit = true;
            isDestroyed = false;
        } catch (Exception x) {
            destroyConnection(c);
        }finally {
            releaseConnection(c);
        }
        return isNew;
    }

    @Override
    public boolean destroy() {
        isDestroyed = false;
        Connection c = getConnection();
        try {
            java.sql.Statement stmt = c.createStatement();
            String x = "Drop Table " + table.getFQTablename(); // blow it all away.
            stmt.execute(x);
            stmt.close();
            isDestroyed = true;
            isNew = false;
            isInit = false;
        } catch (Exception x) {
            destroyConnection(c);
        }finally {
            releaseConnection(c);
        }
        return isDestroyed;
    }

    @Override
    public boolean init() {
        isInit = true;
        isInit = isInit && destroy() && createNew();
        return isInit;
    }

    @Override
    public boolean isCreated() {
        return isNew;
    }

    @Override
    public boolean isInitialized() {
        return isInit;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}
