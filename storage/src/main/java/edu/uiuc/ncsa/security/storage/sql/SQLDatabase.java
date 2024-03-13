package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.PoolException;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * /**
 * Top-level SQL object. This simply maintains a connection pool to a database.
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  12:58:14 PM
 */
public class SQLDatabase {
    public SQLDatabase(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }


    public SQLDatabase() {
    }

    public ConnectionRecord getConnection() {
        return getConnectionPool().pop();
    }

    /**
     * Put the connection back on the stack for future use. This should be invoked in a finally clause at the end
     * of every method that uses a connection.
     *
     * @param c
     */
    public void releaseConnection(ConnectionRecord c) {
        c.lastAccessed = System.currentTimeMillis();
        getConnectionPool().push(c);
    }


    public void destroyConnection(ConnectionRecord c)  {
        try {
            connectionPool.doDestroy(c);
            DebugUtil.trace(this, "after destroyConnection for " + c + ", " + getConnectionPool());
        } catch(PoolException x) {
            throw new PoolException("pool failed to destroy connection",x);
        }
    }
    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    ConnectionPool connectionPool;

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    /**
       * Take the values in the current row and stash them in a map, keyed by column name.
       *
       * @param rs
       * @return
       * @throws SQLException
       */

    public static ColumnMap rsToMap(ResultSet rs) throws SQLException {
        ColumnMap map = new ColumnMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numberOfColumns = rsmd.getColumnCount();
        for (int i = 1; i <= numberOfColumns; i++) {
            String colName = rsmd.getColumnName(i);
            Object obj = null;
            obj = rs.getObject(colName);
            if(obj instanceof Clob){
             // some vendors return a clob which requires an open connection to the database to retrieve.
            // This will bomb later because we close connections. Get it now.
             Clob clob = (Clob)obj;
             if(clob.length() < Integer.MAX_VALUE) {
                 Reader r = clob.getCharacterStream();
                 BufferedReader bufferedReader = new BufferedReader(r);
                 try {
                     String lineIn = bufferedReader.readLine();
                     boolean isFirst = true;
                     StringBuffer stringBuffer = new StringBuffer();
                     while (lineIn != null) {
                          stringBuffer.append((isFirst?"":"\n") + lineIn);
                          if(isFirst) isFirst = false;
                          lineIn = bufferedReader.readLine();
                     }
                     map.put(colName, stringBuffer.toString());

                 }catch(IOException iox){
                     throw new IllegalStateException("Error: Could not read stream from database CLOB " + iox.getMessage());
                 }
             }else{
                 throw new IllegalStateException("error: the clob is too large to represent as a string");
             }
            }else {
                map.put(colName, obj);
            }
        }
        return map;
    }

}
