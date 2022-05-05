package edu.uiuc.ncsa.qdl.extensions.database;

import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.extensions.QDLModuleMetaClass;
import edu.uiuc.ncsa.qdl.extensions.QDLVariable;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.QDLSet;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.PoolException;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionRecord;
import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;
import edu.uiuc.ncsa.security.storage.sql.derby.DerbyConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;
import edu.uiuc.ncsa.security.storage.sql.mariadb.MariaDBConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.postgres.PostgresConnectionParameters;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider.*;
import static edu.uiuc.ncsa.security.storage.sql.SQLDatabase.rsToMap;
import static java.sql.Types.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/5/22 at  7:11 AM
 */
/*
   A test file is in ~/dev/csd/config/test/db-test.qdl that has connection information and
   samples for using this class.
 */
public class QDLDB implements QDLModuleMetaClass {
    public static String CONNECT_COMMAND = "connect";
    public static final String MYSQL_TYPE = "mysql";
    public static final String MARIADB_TYPE = "mariadb";
    public static final String POSTGRES_TYPE = "postgres";
    public static final String DERBY_TYPE = "derby";
    public static final String TYPE_ARG = "type";
    ConnectionPool connectionPool;
    boolean isConnected = false;

    public class Connect implements QDLFunction {

        @Override
        public String getName() {
            return CONNECT_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (objects.length != 1) {
                throw new IllegalArgumentException("missing argument.");
            }
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException(getName() + " requires a stem as its argument");
            }
            StemVariable stemVariable = (StemVariable) objects[0];
            if (!stemVariable.containsKey(TYPE_ARG)) {
                throw new IllegalArgumentException("missing " + TYPE_ARG + " argument");
            }
            String type = stemVariable.getString(TYPE_ARG);
            JSONObject json = (JSONObject) stemVariable.toJSON();
            json.remove(TYPE_ARG); // don't confuse connector
            SQLConnectionImpl connection;
            switch (type) {
                case MARIADB_TYPE:
                    connection = new MariaDBConnectionParameters(json);
                    connectionPool = new ConnectionPool(connection, ConnectionPool.CONNECTION_TYPE_MARIADB);
                    break;
                case MYSQL_TYPE:
                    connection = new MySQLConnectionParameters(json);
                    connectionPool = new MySQLConnectionPool(connection);
                    break;
                case DERBY_TYPE:
                    connection = new DerbyConnectionParameters(json);
                    connectionPool = new ConnectionPool(connection, ConnectionPool.CONNECTION_TYPE_DEBRY);
                    break;
                case POSTGRES_TYPE:
                    connection = new PostgresConnectionParameters(json);
                    connectionPool = new ConnectionPool(connection, ConnectionPool.CONNECTION_TYPE_POSTGRES);

                    break;
                default:
                    throw new IllegalArgumentException("unknown database type");
            }
            isConnected = true;
            return Boolean.TRUE;
        }

        List<String> doc = new ArrayList<>();

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doc.isEmpty()) {
                doc.add(getName() + "(arg.) - creates a connection to the given database with the given connection information.");
                doc.add("This is");
                doc.add(USERNAME + " = the username");
                doc.add(PASSWORD + " = the password");
                doc.add(SCHEMA + " = the database schema");
                doc.add(DATABASE + " = the database name");
                doc.add(HOST + " = the host where this lives");
                doc.add(PORT + " = the port");
                doc.add(PARAMETERS + " = (optional) extra connection parameters");
                doc.add(USE_SSL + " = use ssl. Make sure your database is properly configured for SSL first.");
                doc.add(BOOT_PASSWORD + " = the boot password (Derby only)");
                doc.add(IN_MEMORY + " = in memory only (Derby only)");
                doc.add(TYPE_ARG + " = the type. One of " + MYSQL_TYPE + ", " + MARIADB_TYPE + ", " + POSTGRES_TYPE + " or " + DERBY_TYPE);

            }
            return doc;
        }
    }


    public static final String QUERY_COMMAND = "read";

    public class Read implements QDLFunction {
        @Override
        public String getName() {
            return QUERY_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if(!isConnected){
                throw new IllegalStateException("No databasse connection. Please run " + CONNECT_COMMAND + " first.");
            }
            // This provides
            // #1 a statement. If prepared, then
            // #1 List of values
            StemVariable outStem = new StemVariable();
            String rawStatement = (String) objects[0];
            List args = null;
            if (objects.length == 2) {
                if (objects[1] instanceof StemVariable) {
                    StemVariable stemVariable = (StemVariable) objects[1];
                    if (stemVariable.isList()) {
                        args = stemVariable.getStemList().toJSON();
                    } else {
                        throw new IllegalArgumentException(QUERY_COMMAND + " requires its second argument, if present to be a list");
                    }
                } else {
                    throw new IllegalArgumentException(QUERY_COMMAND + " requires its second argument, if present to be a list");
                }
            }
            // Args are list of form
            /*
                [a0,a1,...]
                where a's are either simple types - long, big decimal, string, boolean or null
                or are an explicit record
                [value, type]
                In which case the type will be asserted (and the value may be changed too).
                E.g.
                ['foo',[12223,DATE],['3dgb3ty24fgf',BINARY]]
                would assert the first is a string, convert the second into a date and the 3rd is assumed
                to be base 64 encoded and would be decoded and asserted as a byte[]
             */

            ConnectionRecord connectionRecord = connectionPool.pop();
            Connection c = connectionRecord.connection;
            try {
                PreparedStatement stmt = c.prepareStatement(rawStatement);
                if (args != null) {
                    int i = 1;
                    for (Object entry : args) {
                        setParam(stmt, i, entry);
                    }
                }
                stmt.executeQuery();
                ResultSet rs = stmt.getResultSet();
                // Now we have to pull in all the values.
                if (!rs.next()) {
                    rs.close();
                    stmt.close();
                    releaseConnection(connectionRecord);
                    return null;   // returning a null fulfills contract for this being a map.
                }

                ColumnMap map = rsToMap(rs);
                // Have to convert this map since it is col name + Java object.
                for (String key : map.keySet()) {
                    if (map.get(key) != null) {
                        outStem.put(key, sqlConvert(map.get(key)));
                    }
                }
                rs.close();
                stmt.close();
                releaseConnection(connectionRecord);
            } catch (SQLException e) {
                destroyConnection(connectionRecord);
                throw new GeneralException("Error executing SQL: " + e.getMessage(), e);
            }
            return outStem;
        }

        List<String> doc = new ArrayList<>();

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doc.isEmpty()) {
                doc.add(getName() + "(statement{,arg_list}) - execute a query (so a select, query, count or anything else that");
                doc.add("has  a result. The statement may be simply a statement or it may be a prepared statement.");
                doc.add("If it is prepared, then arg_list is a list of either scalars or pairs of the form [value, type]");
                doc.add("where type is one of the types in the variable " + TYPE_VAR_NAME);
                doc.add("E.g.");
                doc.add("db#query('select * from my_table where user_id=?',['2355',types.SMALLINT]);");
                doc.add("Note that the types are specific to the table structure of the database! ");
                doc.add("If you do not supply them then the default will be ");
                doc.add("int -> BIGINT");
                doc.add("decimal -> NUMERIC");
                doc.add("string -> STRING");
                doc.add("boolean -> BOOLEAN");
                doc.add("stem, set -> STRING (as input form)");
                doc.add("null -> LONGVARCHAR as a  NULL");
            }
            return doc;
        }
    }

    private void setParam(PreparedStatement stmt, int i, Object entry) throws SQLException {
        Object value;
        int type = Integer.MIN_VALUE;
        if (entry instanceof JSONArray) {
            JSONArray array = (JSONArray) entry;
            value = array.get(0);
            type = array.getInt(1);
        } else {
            value = entry;
        }
        if (type == Integer.MIN_VALUE) {
            if (value instanceof String) {
                stmt.setString(i, (String) value);
                return;
            }
            if (value instanceof Long) {
                stmt.setObject(i, value, BIGINT);
                return;
            }
            if (value instanceof BigDecimal) {
                stmt.setObject(i, value, NUMERIC);
                return;
            }
            if (value instanceof Boolean) {
                stmt.setBoolean(i, (Boolean) value);
                return;
            }
            if ((value instanceof StemVariable) || (value instanceof QDLSet)) {
                stmt.setString(i, InputFormUtil.inputForm(value));
                return;
            }
            if (value instanceof QDLNull) {
                stmt.setNull(NULL, LONGVARCHAR);
                return;
            }

            throw new IllegalArgumentException("unknoen argument type for " + value + " of type  " + value.getClass().getCanonicalName());
        }
        stmt.setObject(i, value, type);
    }

    /**
     * Converts a generic object to a QDL object.
     *
     * @param obj
     * @return
     */
    protected Object sqlConvert(Object obj) {
        if (Constant.getType(obj) != Constant.UNKNOWN_TYPE) {
            return obj; // nixx to do
        }
        if (obj instanceof Integer) {
            return new Long((Integer) obj);
        }
        if (obj instanceof byte[]) {
            return Base64.encodeBase64URLSafeString((byte[]) obj);
        }
        if ((obj instanceof Date)) {
            Date date = (Date) obj;
            return Iso8601.date2String(date.getTime());
        }
        if (obj instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) obj;
            return Iso8601.date2String(timestamp.getTime());
        }

        if ((obj instanceof Double) || (obj instanceof Float)) {
            return new BigDecimal(obj.toString());
        }
        throw new IllegalArgumentException("unknown SQLtype for " + obj.getClass().getCanonicalName());
    }

    public void releaseConnection(ConnectionRecord c) {
        c.setLastAccessed(System.currentTimeMillis());
        connectionPool.push(c);
    }


    protected void destroyConnection(ConnectionRecord c) {
        try {
            connectionPool.doDestroy(c);
            DebugUtil.trace(this, "after destroyConnection for " + c + ", " + connectionPool);
        } catch (PoolException x) {
            throw new PoolException("pool failed to destroy connection", x);
        }
    }

    public static String UPDATE_COMMAND = "update";

    public class Update implements QDLFunction {
        @Override
        public String getName() {
            return UPDATE_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if(!isConnected){
                throw new IllegalStateException("No databasse connection. Please run " + CONNECT_COMMAND + " first.");
            }
            String rawStatement = (String) objects[0];
            List args = null;
            if (objects.length == 2) {
                if (objects[1] instanceof StemVariable) {
                    StemVariable stemVariable = (StemVariable) objects[1];
                    if (stemVariable.isList()) {
                        args = stemVariable.getStemList().toJSON();
                    } else {
                        throw new IllegalArgumentException(QUERY_COMMAND + " requires its second argument, if present to be a list");
                    }
                } else {
                    throw new IllegalArgumentException(QUERY_COMMAND + " requires its second argument, if present to be a list");
                }
            }

            ConnectionRecord connectionRecord = connectionPool.pop();
            Connection c = connectionRecord.connection;
            try {
                PreparedStatement stmt = c.prepareStatement(rawStatement);
                if (args != null) {
                    int i = 1;
                    for (Object entry : args) {
                        setParam(stmt, i, entry);
                    }
                }
                stmt.executeUpdate();
                // Nothing is ever returned from an update
                stmt.close();
                releaseConnection(connectionRecord);
            } catch (SQLException e) {
                destroyConnection(connectionRecord);
                throw new GeneralException("Error executing SQL: " + e.getMessage(), e);
            }
            return Boolean.TRUE;
        }

        List<String> doc = new ArrayList();

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doc.isEmpty()) {
                doc.add(getName() + "(statement{,args}) - update an existing row or table in an SQL database");
                doc.add("");
                doc.add("");
                doc.add("");
                doc.add("");
            }
            return doc;
        }
    }

    public static String EXECUTE_COMMAND = "execute";

    public class Execute implements QDLFunction {
        @Override
        public String getName() {
            return EXECUTE_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            return doSQLExecute(objects, getName());
        }

        List<String> doc = new ArrayList<>();

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doc.isEmpty()) {
                doc.add(getName() + "(statement{,arg_list}) - executes a statement that has no return values.");
                doc.add("This is used for inserts and deletes in particular.");
                doc.add("");
            }
            return doc;
        }
    }



    public static String TYPE_VAR_NAME = "types.";

    public class SQLTypes implements QDLVariable {
        @Override
        public String getName() {
            return TYPE_VAR_NAME;
        }

        StemVariable types = null;

        @Override
        public Object getValue() {
            if (types == null) {
                types = new StemVariable();
                types.put("VARCHAR", new Long(VARCHAR));
                types.put("CHAR", new Long(CHAR));
                types.put("LONGVARCHAR", new Long(LONGVARCHAR));
                types.put("BIT", new Long(BIT));
                types.put("NUMERIC", new Long(NUMERIC));
                types.put("TINYINT", new Long(TINYINT));
                types.put("SMALLINT", new Long(SMALLINT));
                types.put("INTEGER", new Long(INTEGER));
                types.put("BIGINT", new Long(BIGINT));
                types.put("REAL", new Long(REAL));
                types.put("FLOAT", new Long(FLOAT));
                types.put("DOUBLE", new Long(DOUBLE));
                types.put("VARBINARY", new Long(VARBINARY));
                types.put("BINARY", new Long(BINARY));
                types.put("DATE", new Long(DATE));
                types.put("TIME", new Long(TIME));
                types.put("TIMESTAMP", new Long(TIMESTAMP));
                types.put("CLOB", new Long(CLOB));
                types.put("BLOB", new Long(BLOB));
                types.put("ARRAY", new Long(ARRAY));
                types.put("REF", new Long(REF));
                types.put("STRUCT", new Long(STRUCT));
                types.put("SQLXML", new Long(SQLXML));
            }
            return types;
        }


    }

    /**
     * Used for both create an delete.
     *
     * @param objects
     * @param name
     * @return
     */
    public Object doSQLExecute(Object[] objects, String name) {
        if(!isConnected){
            throw new IllegalStateException("No databasse connection. Please run " + CONNECT_COMMAND + " first.");
        }
        String rawStatement = (String) objects[0];
        List args = null;
        if (objects.length == 2) {
            if (objects[1] instanceof StemVariable) {
                StemVariable stemVariable = (StemVariable) objects[1];
                if (stemVariable.isList()) {
                    args = stemVariable.getStemList().toJSON();
                } else {
                    throw new IllegalArgumentException(name + " requires its second argument, if present to be a list");
                }
            } else {
                throw new IllegalArgumentException(name + " requires its second argument, if present to be a list");
            }
        }

        ConnectionRecord connectionRecord = connectionPool.pop();
        Connection c = connectionRecord.connection;
        try {
            PreparedStatement stmt = c.prepareStatement(rawStatement);
            if (args != null) {
                int i = 1;
                for (Object entry : args) {
                    setParam(stmt, i, entry);
                }
            }
            stmt.execute();
            stmt.close();
            releaseConnection(connectionRecord);
        } catch (SQLException e) {
            destroyConnection(connectionRecord);
            throw new GeneralException("Error executing SQL: " + e.getMessage(), e);
        }
        return Boolean.TRUE;
    }
    /* Handy dandy table of SQL types and calls.
    SQL 	        JDBC/Java 	            setXXX 	        updateXXX
    VARCHAR 	    java.lang.String 	    setString 	    updateString
    CHAR 	        java.lang.String 	    setString 	    updateString
    LONGVARCHAR 	java.lang.String 	    setString 	    updateString
    BIT 	        boolean 	            setBoolean 	    updateBoolean
    NUMERIC 	    java.math.BigDecimal 	setBigDecimal 	updateBigDecimal
    TINYINT 	    byte 	                setByte         updateByte
    SMALLINT 	    short 	                setShort        updateShort
    INTEGER 	    int 	                setInt 	        updateInt
    BIGINT 	        long 	                setLong         updateLong
    REAL 	        float 	                setFloat        updateFloat
    FLOAT 	        float 	                setFloat 	    updateFloat
    DOUBLE 	        double 	                setDouble 	    updateDouble
    VARBINARY    	byte[ ] 	            setBytes 	    updateBytes
    BINARY 	        byte[ ] 	            setBytes 	    updateBytes
    DATE  	        java.sql.Date 	        setDate 	    updateDate
    TIME 	        java.sql.Time 	        setTime 	    updateTime
    TIMESTAMP    	java.sql.Timestamp 	    setTimestamp 	updateTimestamp
    CLOB 	        java.sql.Clob 	        setClob 	    updateClob
    BLOB 	        java.sql.Blob 	        setBlob 	    updateBlob
    ARRAY 	        java.sql.Array 	        setARRAY 	    updateARRAY
    REF 	        java.sql.Ref 	        SetRef 	        updateRef
    STRUCT 	        java.sql.Struct     	SetStruc
     */
}
