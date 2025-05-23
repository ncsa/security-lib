package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.cache.SimpleEntryImpl;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.IllegalAccessException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.exceptions.UnregisteredObjectException;
import edu.uiuc.ncsa.security.core.util.*;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnDescriptorEntry;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnDescriptors;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

import javax.inject.Provider;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * Top-level SQL store object. A store is simply a logical analog of a hash table, where the key
 * is the primary key. This in practice may front multiple tables. This implements several of the
 * basic operations. You need to implement a couple of methods and supply a {@link Table} that models the
 * storage and a {@link MapConverter} that allows you to turn a java object's properties into
 * a map -- then you should be in business for using an SQL backend.
 * All of these statements are SQL 2003 compliant and should work without change for all major vendors.
 * This class also maintains a  {@link ConnectionPool}to a database.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  12:58:14 PM
 */
public abstract class SQLStore<V extends Identifiable> extends SQLDatabase implements Store<V> {
    protected SQLStore(ConnectionPool connectionPool,
                       Table table,
                       Provider<V> identifiableProvider,
                       MapConverter<V> converter) {
        super(connectionPool);
        this.table = table;
        this.identifiableProvider = identifiableProvider;
        this.converter = converter;
    }


    protected Provider<V> identifiableProvider;

    public SQLStore() {
    }

    @Override
    public V create() {
        return (V) identifiableProvider.get();
    }

    protected MapConverter<V> converter;

    /**
     * This will get every entry in the database. For sparing use since this may be a huge load.
     *
     * @return
     */
    public List<V> getAll() {
        LinkedList<V> allEntries = new LinkedList<>();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        V t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(getTable().createSelectAllStatement());
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            while (rs.next()) {
                ColumnMap map = rsToMap(rs);
                t = create();
                populate(map, t);
                allEntries.add(t);
            }

            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting all entries.", e);
        }
        return allEntries;
    }

    /**
     * For an existing entry in the store. This will select it based on the primary key
     * and change all other values.
     *
     * @param value
     */
    public void update(V value) {
        update(value, false);
    }

    public void update(V value, boolean existenceChecked) {

        if (!existenceChecked && !containsValue(value)) {
            throw new UnregisteredObjectException("Error: cannot update non-existent entry for\"" +
                    value.getIdentifierString() + "\". Register it first or call save.");
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        try {

            PreparedStatement stmt = c.prepareStatement(getTable().createUpdateStatement());
            setUpdateValues(value, stmt);
            stmt.executeUpdate();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error updating object with identifier = \"" + value.getIdentifierString() + " (" + e.getMessage() + ")", e);
        }
    }

    @Override
    public void update(List<Identifier> ids, Map<String, Object> map) throws UnregisteredObjectException {
        List<String> keys = new ArrayList<>(map.size());
        // we peel the keys off since we have to manage the order of them and it is not guaranteed in Java
        keys.addAll(map.keySet());
        List<Object> values = new ArrayList<>(map.size());
        for(String key : keys){
            // lay them out in order and make sure SQL can understand them
            values.add(getAsSQLObject(map.get(key)));
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        /*
        String insertEmployeeSQL = "INSERT INTO EMPLOYEE(ID, NAME, DESIGNATION) "
 + "VALUES (?,?,?)";
PreparedStatement employeeStmt = connection.prepareStatement(insertEmployeeSQL);
for(int i = 0; i < EMPLOYEES.length; i++){
    String employeeId = UUID.randomUUID().toString();
    employeeStmt.setString(1,employeeId);
    employeeStmt.setString(2,EMPLOYEES[i]);
    employeeStmt.setString(3,DESIGNATIONS[i]);
    employeeStmt.addBatch();
}
employeeStmt.executeBatch();
         */
        try {

            PreparedStatement stmt = c.prepareStatement(getTable().createUpdateStatement(keys));
            for(int i = 0; i < ids.size(); i++){
                for(int j = 0; j < values.size(); j++){
                    stmt.setObject(j+1, values.get(j));
                }
                stmt.setString(values.size(), ids.get(i).toString());
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error in mass update :"   + e.getMessage() , e);
        }

    }

    public void setUpdateValues(V value, PreparedStatement stmt) throws SQLException {
        ColumnMap map = depopulate(value);
        int i = 1;
        for (ColumnDescriptorEntry cde : getTable().getColumnDescriptor()) {
            // now we loop through the table and set each and every one of these
            if (!cde.isPrimaryKey()) {
                Object obj = getAsSQLObject(map.get(cde.getName()));
                stmt.setObject(i++, obj);
            }
        }

        // now set the primary key
        stmt.setString(i++, value.getIdentifierString());
    }

    /**
     * Turn into an object that can be set in an SQL statement.
     * @param oldObject
     * @return
     */
    private static Object getAsSQLObject(Object oldObject) {
        Object obj = oldObject;
        // Dates confuse setObject, so turn it into an SQL Timestamp object.
        if (obj instanceof Date) {
            obj = new Timestamp(((Date) obj).getTime());
        }
        if (obj instanceof BasicIdentifier) {
            obj = obj.toString();
        }
        if (obj instanceof StatusValue) {
            obj = ((StatusValue) obj).getStatus();
        }
        return obj;
    }


    /**
     * Take a *new* value and populate it from the given mapping of column names and values.
     * All values must be accounted for since
     * the result set generally will come from a <code> SELECT * FROM... </code>
     *
     * @param map
     * @param t
     */
    public void populate(ColumnMap map, V t) {
        converter.fromMap(map, t);
    }


    /**
     * Takes the object, V and returns a map of column name, value. This is used to construct various statements
     * This is where the columns and object properties are put in correspondence. We could try to do this with some
     * sort of introspection, but that is very, very slow and not always clear on how it should be done.
     *
     * @param t
     * @return
     * @throws SQLException
     */
    public ColumnMap depopulate(V t) throws SQLException {
        ColumnMap map = new ColumnMap();
        converter.toMap(t, map);
        return map;
    }


    public void save(V value) {
        if (containsKey(value.getIdentifier())) {
            update(value, true); // pass flag so we don't redo the call to check it
        } else {
            register(value);
        }
    }

    /**
     * Sets the values in the {@link PreparedStatement} to those in the value. This allows
     * other utilities to create batch statements for this store.
     *
     * @param stmt
     * @param value
     * @throws SQLException
     */
    public void doRegisterStatement(PreparedStatement stmt, V value) throws SQLException {
        Map<String, Object> map = depopulate(value);
        int i = 1;
        for (ColumnDescriptorEntry cde : getTable().getColumnDescriptor()) {
            // now we loop through the table and set each and every one of these
            // OAUTH-148 fix: MariaDB driver does not accept longvarchar as a type in setObject (known bug for
            // them. Workaround is to explicitly test for this and carry out a setString call instead.)
            if (cde.getType() == Types.LONGVARCHAR) {
                Object obj = map.get(cde.getName());
                stmt.setString(i++, obj == null ? null : obj.toString());
            } else {
                stmt.setObject(i++, map.get(cde.getName()), cde.getType());
            }
        }

    }

    public void register(V value) {
        if (value.isReadOnly()) {
            throw new IllegalAccessException(value.getIdentifierString() + " is read only");
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        try {
            PreparedStatement stmt = c.prepareStatement(getTable().createInsertStatement());
            doRegisterStatement(stmt, value);
            stmt.execute();// just execute() since executeQuery(x) would throw an exception regardless of content of x as per JDBC spec.
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error: could not register object with id \"" + value.getIdentifierString() + "\"", e);
        } finally {
        }
    }

    /**
     * Retrieve a single row from a table then populate an instance. <br/>
     * Note: If you need to jazz this up, it is probably better to override the {@link #rsToMap(java.sql.ResultSet)}
     * method in this class. For instance, if the select statement is a join and there are multiple rows to
     * process. The basic version of this class presupposed one row per object, but there is no reason this
     * cannot be extended.
     *
     * @param o
     * @return
     */
    public V get(Object o) {
        if (o == null) {
            throw new IllegalStateException("Error: a null identifier was supplied");
        }

        Identifier key = null;
        try {
            key = (Identifier) o;
        } catch (ClassCastException c) {
            throw new NFWException("Error casting object of type \"" + o.getClass().getName() + "\" to an Identifier.\nThis is an implementation error", c);
        }

        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        V t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(getTable().createSelectStatement());
            stmt.setString(1, key.toString());
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            if (!rs.next()) {
                rs.close();
                stmt.close();
                releaseConnection(cr);
                return null;   // returning a null fulfills contract for this being a map.
            }

            ColumnMap map = rsToMap(rs);
            rs.close();
            stmt.close();
            releaseConnection(cr); // CIL-1833
            t = create();
            populate(map, t);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting object with identifier \"" + key + "\"", e);
        }
        return t;
    }


    public List<V> search(String key, String condition,
                          boolean isRegEx,
                          List<String> attr,
                          String dateField,
                          Date before,
                          Date after) {

        String attributes = null;
        if (attr == null || attr.isEmpty()) {
            attributes = "*";
        } else {
            attributes = "";
            boolean isFirst = true;
            for (String a : attr) {
                attributes = attributes + (isFirst ? "" : ",") + a;
                if (isFirst) {
                    isFirst = false;
                }
            }
        }
        String searchString = "select " + attributes +
                " from " + getTable().getFQTablename();

        boolean hasKey = !StringUtils.isTrivial(key);
        if (hasKey) {
            searchString = searchString + " where " + key + " " + (isRegEx ? "regexp" : "=") + " ? ";
        }
        boolean hasBefore = before != null;
        boolean hasAfter = after != null;

        if (hasAfter) {
            if (hasBefore) {
                // between two dates.
                searchString = searchString + (hasKey ? " and " : " where ") + dateField + " between ? and ?";
            } else {
                // only after
                searchString = searchString + (hasKey ? " and " : " where ") + "? <= " + dateField;
            }
        } else {
            if (hasBefore) {
                searchString = searchString + (hasKey ? " and " : " where ") + dateField + "  <= ? ";
                // only before
            } else {
                // no time clause
            }
        }
        List<V> values = new ArrayList<>();
        if (!hasKey && !hasBefore && !hasAfter) {
            // If they munged their query and didn't ask for anything, don't return anything.
            return values;
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        V t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(searchString);
            int pIndex = 1;
            if (hasKey) {
                stmt.setString(pIndex++, condition);
            }
            if (hasAfter) {
                stmt.setDate(pIndex++, new java.sql.Date(after.getTime()));
            }
            if (hasBefore) {
                stmt.setDate(pIndex++, new java.sql.Date(before.getTime()));
            }
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            while (rs.next()) {
                ColumnMap map = rsToMap(rs);
                t = create();
                populate(map, t);
                values.add(t);
            }

            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting object with identifier \"" + key + "\"", e);
        }
        return values;
    }

    public List<V> search(String key, String condition, boolean isRegEx, List<String> attr) {

        String attributes = null;
        if (attr == null || attr.isEmpty()) {
            attributes = "*";
        } else {
            attributes = "";
            boolean isFirst = true;
            for (String a : attr) {
                attributes = attributes + (isFirst ? "" : ",") + a;
                if (isFirst) {
                    isFirst = false;
                }
            }
        }
        String searchString;
        if (isRegEx) {
            if (getConnectionPool().getType() == ConnectionPool.CONNECTION_TYPE_DEBRY) {
                searchString = "select " + attributes +
                        " from " + getTable().getFQTablename() +
                        " where " + key + " LIKE ?";

            } else {
                searchString = "select " + attributes +
                        " from " + getTable().getFQTablename() +
                        " where " + key + " regexp  ?";
            }

        } else {
            searchString = "select " + attributes +
                    " from " + getTable().getFQTablename() +
                    " where " + key + " =  ?";

        }
        List<V> values = new ArrayList<>();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        V t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(searchString);
            stmt.setString(1, condition);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            while (rs.next()) {
                ColumnMap map = rsToMap(rs);
                t = create();
                populate(map, t);
                values.add(t);
            }

            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting object with identifier \"" + key + "\"", e);
        }
        return values;

    }

    // Fix https://github.com/ncsa/security-lib/issues/49
    @Override
    public List<V> search(String key, boolean isNull) {
        String searchString = "select *  from " + getTable().getFQTablename() +
                " where ? is " + (isNull ? "" : " not ") + " null " ;

        List<V> values = new ArrayList<>();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        V t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(searchString);
            stmt.setString(1, key);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            while (rs.next()) {
                ColumnMap map = rsToMap(rs);
                t = create();
                populate(map, t);
                values.add(t);
            }

            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting object with identifier \"" + key + "\"", e);
        }
        return values;
    }

    public List<V> search(String key, String condition, boolean isRegEx) {
        return search(key, condition, isRegEx, null);
    }


    public Table getTable() {
        return table;
    }


    Table table;


    public int size() {
        return size(getTable().getFQTablename(), true);
    }

    @Override
    public int size(boolean includeVersions) {
        return size(getTable().getFQTablename(), includeVersions);
    }

    /**
     * Utility that gets the total number of rows in a given table, given the name.
     *
     * @param tablename
     * @return
     */
    protected int size(String tablename, boolean includeVersions) {
        String query = null;
        if (includeVersions) {
            query = "SELECT COUNT(*)  from " + tablename;
        } else {
            XMLConverter xmlConverter = getXMLConverter();
            if (xmlConverter instanceof MapConverter) {
                MapConverter mapConverter = (MapConverter) xmlConverter;
                query = "SELECT COUNT(*)  from " + tablename + " WHERE " + mapConverter.getKeys().identifier() + " NOT LIKE '%" + VERSION_TAG + "%'";
            } else {
                query = "SELECT COUNT(*)  from " + tablename;
            }
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        int rowCount = 0; // default size

        try {
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            if (rs.next()) {
                rowCount = rs.getInt(1); // *trick* to get the row count
            }
            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting the size.", e);
        }
        return rowCount;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
        //Fix for CIL-350: If a non-identifier is passed in, this returns null. It should only accept identifiers.
        Identifier identifier = null;
        try {
            identifier = (Identifier) key;
        } catch (ClassCastException c) {
            throw new NFWException("Error casting object of type \"" + key.getClass().getName() + "\" to an Identifier.\nThis is an implementation error", c);
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        boolean rc = false;
        try {
            PreparedStatement stmt = c.prepareStatement(getTable().createSelectStatement());
            stmt.setString(1, identifier.toString());
            stmt.execute();// just execute() since executeQuery(x) would throw an exception regardless of content of x as per JDBC spec.
            ResultSet rs = stmt.getResultSet();
            rc = rs.next();
            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            e.printStackTrace();
        }
        return rc;

    }


    public boolean containsValue(Object o) {
        V foo;
        try {
            foo = (V) o;
        } catch (ClassCastException c) {
            // $&*&%% Java generics
            return false;
        }
        return containsKey(foo.getIdentifier());
    }


    public V put(Identifier k, V v) {
        if (v.getIdentifier().equals(k)) {
            save(v);
        }
        return null;
    }

    /**
     * remove a collection of items by primary key. This uses SQL batching and should be used for large
     * sets. {@link #remove(List)} takes a list of objects and makes a very long list of things to remove.
     *
     * @param ids
     * @return
     */
    @Override
    public boolean removeByID(List<Identifier> ids) {
        String query = "DELETE FROM " + getTable().getFQTablename() + " WHERE " + getTable().getPrimaryKeyColumnName() + "=?";
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        try {
            PreparedStatement stmt = c.prepareStatement(query);
            for (Identifier id : ids) {
                // Reminder that parameters have index origin 1
                stmt.setString(1, id.toString());
                stmt.addBatch();
            }
            int[] rcs = stmt.executeBatch();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error removing list", e);
        }
        return true;

    }

    // Fix https://github.com/ncsa/security-lib/issues/44
    public boolean remove(List<V> objects) {
        if (objects.size() == 0) return true; // do nothing
        List<Identifier> ids = new ArrayList<>(objects.size());
        for (int i = 0; i < objects.size(); i++) {
            ids.add(objects.get(i).getIdentifier());
        }
        return removeByID(ids); // do it as a batch call.
    }

    public V remove(Object key) {
        V oldObject = null;
        try {
            oldObject = get(key);
        } catch (GeneralException x) {
            // fine. Return null. All we care about is whether the next operations work.
        }
        String query = "DELETE FROM " + getTable().getFQTablename() + " WHERE " + getTable().getPrimaryKeyColumnName() + "=?";
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        try {
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setString(1, key.toString());
            stmt.execute();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting identity providers", e);
        }
        return oldObject;
    }

    /**
     * A terrifically inefficient way to add these since it loops. If you need this to work better, override and optimize.
     *
     * @param m
     */
    @Override
    public void putAll(Map<? extends Identifier, ? extends V> m) {
        for (Map.Entry e : m.entrySet()) {
            register((V) e.getValue());
        }
    }


    public void clear() {
        String query = "DELETE FROM " + getTable().getFQTablename();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        try {
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.execute();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting identity providers", e);
        }
    }


    public Set<Identifier> keySet() {
        HashSet<Identifier> keys = new HashSet<Identifier>();

        String query = "Select " + getTable().getPrimaryKeyColumnName() + " from " + getTable().getFQTablename();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        try {
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            // Figure out the type of argument. Can't do this in java without annoying reflection
            while (rs.next()) {
                keys.add(new BasicIdentifier(rs.getString(1)));
            }
            rs.close();
            stmt.close();
            releaseConnection(cr);

        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting the user ids", e);
        }
        return keys;
    }

    /**
     * Again, this is basic functionality for the map interface. Do you really need to get everything in the database?
     * If the database is large, this might fail for various unrelated reasons. If you really need to use a call like
     * this, then you should probably over-ride it and optimize, say with partial retrievals or some such.
     *
     * @return
     */
    public Collection<V> values() {
        Collection<V> allOfThem = new ArrayList<V>();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        try {
            PreparedStatement stmt = c.prepareStatement("select * from " + getTable().getFQTablename());
            stmt.execute();// just execute() since executeQuery(x) would throw an exception regardless of content per JDBC spec.

            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                V newOne = create();
                ColumnMap map = rsToMap(rs);
                populate(map, newOne);
                allOfThem.add(newOne);
            }
            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error: could not get database object", e);
        }
        return allOfThem;
    }

    public Set<Entry<Identifier, V>> entrySet() {
        Set<Entry<Identifier, V>> entries = new HashSet<Entry<Identifier, V>>();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        try {
            PreparedStatement stmt = c.prepareStatement("select * from " + getTable().getFQTablename());
            stmt.execute();// just execute() since executeQuery(x) would throw an exception regardless of content per JDBC spec.

            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                V newOne = create();
                ColumnMap map = rsToMap(rs);
                populate(map, newOne);
                entries.add(new SimpleEntryImpl<Identifier, V>(newOne.getIdentifier(), newOne));
            }
            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error: could not get database object", e);
        }
        return entries;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[table=" + getTable() + "]";
    }

    /**
     * When invoked this will loop through the columns of the table and add columns as needed with the correct type.
     * NOTE that this should only be run once as a utility at, say, servlet loading time before any data access can
     * occur. Also, all the added columns are allowed to be null. You will have to change that (along with setting
     * a defaul) if you do not want that.
     */
    public void checkColumns() throws SQLException {
        ColumnDescriptors cds = getTable().getColumnDescriptor();
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;

        Statement stmt = c.createStatement();
        // We need a result set, but we do not want to get any elements from the database
        // Therefore, a trick is to add a where clause that is always false.
        ResultSet rs = stmt.executeQuery("SELECT * from " + getTable().getFQTablename() + " WHERE 1=2");
        ResultSetMetaData metaData = rs.getMetaData();
        Hashtable<String, Integer> foundCols = new Hashtable<String, Integer>();
        // have to loop through these and get the column names and types first since we will
        // be altering the table which will change order of the columns potentially and
        // impact all subsequent table structure updates.

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            foundCols.put(metaData.getColumnName(i).toLowerCase(), metaData.getColumnType(i));
        }

        // Grrrrr.... JDBC should really do this. This grabs the field names from the
        // java.sql.Types class and shoves them in a hashmap for later lookup. Annoying.
        // tres annoying. Needs a final kludge too.
        Map<Integer, String> jdbcMappings = new HashMap<Integer, String>();

        for (Field field : java.sql.Types.class.getFields()) {
            try {
                jdbcMappings.put((Integer) field.get(null), field.getName());
            } catch (java.lang.IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // LONGVARCHAR and LONGNVARCHAR are Java SQL types but not SQL-standard.
        // Since every SQL engine uses the more modern type of TEXT instead, we special case this.
        if (getConnectionPool().getType() == ConnectionPool.CONNECTION_TYPE_DEBRY) {
            jdbcMappings.put(Types.LONGNVARCHAR, "CLOB");
            jdbcMappings.put(Types.LONGVARCHAR, "CLOB");
        } else {
            jdbcMappings.put(Types.LONGNVARCHAR, "TEXT");
            jdbcMappings.put(Types.LONGVARCHAR, "TEXT");
        }
        for (ColumnDescriptorEntry cde : cds) {
            if (!foundCols.containsKey(cde.getName().toLowerCase())) {
                // create the column
                String rawStmt = null;
                if (cde.getType() == Types.TIMESTAMP) {
                    DebugUtil.trace(this, "Adding column " + cde.getName() + " of type TIMESTAMP = " + cde.getType());
                    // Timestamps must be explicitly allowed to be negative or have a default time or there will be an exception.
                    // Note that the next statement should work for versions of MySQL and for PostgreSQL. It may need
                    // modification for other database types.
                    rawStmt = "Alter Table " + getTable().getFQTablename() + " add Column " + cde.getName() + " " + jdbcMappings.get(cde.getType()) + " DEFAULT CURRENT_TIMESTAMP";
                } else {
                    System.err.println("Adding column " + cde.getName() + " of type " + cde.getType());
                    rawStmt = "Alter Table " + getTable().getFQTablename() + " add Column " + cde.getName() + " " + jdbcMappings.get(cde.getType());
                }
                DebugUtil.trace(this, "Executing update statement \"" + rawStmt + "\"");

                stmt.executeUpdate(rawStmt);
            }
        }
        rs.close();
        stmt.close();
        releaseConnection(cr);
    }

    public void checkTable() {
        if (getConnectionPool().getType() == ConnectionPool.CONNECTION_TYPE_DEBRY) {
            System.err.println("Warning: Derby does not support database meta data for tables. Cannot check tables.");
            return;
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        try {
            DatabaseMetaData md = c.getMetaData();
            ResultSet tables = md.getTables(null, getTable().getSchema(), getTable().getTablename(), new String[]{"TABLE"});
            Statement stmt = null;
            if (tables.next()) {
                // table exists
            } else {
                // table does not exist.
                // At issue is that the user that access the database may not have create permissions to make the table.
                // So we will try this and just log how it works out.
                System.err.println("Table " + getTable().getTablename() + " does not exist. Attempting to create");
                stmt = c.createStatement();
                System.err.println("Executing\"" + getTable().getCreateTableStatement() + "\"");
                boolean rc = stmt.execute(getTable().getCreateTableStatement());
                if (stmt != null) {
                    stmt.close();
                }

            }
            releaseConnection(cr);

        } catch (SQLException x) {
            if (DebugUtil.isEnabled()) {
                x.printStackTrace();
            }
            System.err.println("failed to create " + getTable().getTablename() + " msg=" + x.getMessage());
        }
    }

    public XMLConverter<V> getXMLConverter() {
        return converter;
    }

    public MapConverter getMapConverter() {
        return converter;
    }

    public abstract String getCreationTSField();

    @Override
    public List<V> getMostRecent(int n, List<String> attr) {
        if (getCreationTSField() == null) {
            throw new UnsupportedOperationException("This store does not support this operation.");
        }
        List<V> values = new ArrayList<>();
        if (n == 0) {
            return values;
        }
        String attributes;

        if (attr == null || attr.isEmpty()) {
            attributes = "*";
        } else {
            attributes = "";
            boolean isFirst = true;
            for (String a : attr) {
                attributes = attributes + (isFirst ? "" : ",") + a;
                if (isFirst) {
                    isFirst = false;
                }
            }
        }
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        V t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(getMostRecentStatement(attributes, 0 < n));
            stmt.setInt(1, Math.abs(n));
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            while (rs.next()) {
                ColumnMap map = rsToMap(rs);
                t = create();
                populate(map, t);
                values.add(t);
            }

            rs.close();
            stmt.close();
            releaseConnection(cr);
        } catch (SQLException e) {
            destroyConnection(cr);
            throw new GeneralException("Error getting " + (0 < n ? " first " : " last ") + Math.abs(n) + " objects", e);
        }
        return values;
    }

    protected String getMostRecentStatement(String attributes, boolean desc) {
        if (getConnectionPool().getType() == ConnectionPool.CONNECTION_TYPE_DEBRY) {
            return getDerbyMostRecent(attributes, desc);
        }
        return "select " + attributes + " from " + getTable().getFQTablename() + " order by " + getCreationTSField() + (desc ? " desc " : " asc ") + "  limit ?";
    }

    protected String getDerbyMostRecent(String attrbutes, boolean desc) {
        return "select " + attrbutes + " from " + getTable().getFQTablename() + " order by " + getCreationTSField() + (desc ? " desc " : " asc ") + " fetch first ? rows only";
    }

    /**
     * This is to take an SQL script for e.g. creating a database and return a list of
     * statements. It is not a clever parser at all. It strips out comments
     * and anything that ends with a ; is a statement. Mostly the assumption
     * is that this is a set of table create statements and ceate index statements.
     * Anything else is at your own risk.
     *
     * @param fileName either the name of a resource or the fully qualified path to the file.
     * @return
     * @throws Throwable
     */
    public static List<String> crappySQLParser(String fileName) {
        try {
            // try to get it as a resource first. Then as a file
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(fileName);
            List<String> raw = null;
            if (is == null) {
                raw = FileUtil.readFileAsLines(fileName);
            } else {
                raw = new ArrayList<>();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String currentLine = br.readLine();
                while (currentLine != null) {
                    raw.add(currentLine);
                    currentLine = br.readLine();
                }
                br.close();
            }
            return crappySQLParser(raw);
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new GeneralException("error reading SQL script \"" + fileName + "\" (" + t.getClass().getSimpleName() + "):" + t.getMessage());
        }
    }

    public static List<String> crappySQLParser(List<String> script) {
        String stmtEnd = ";";
        String lineComment = "--";
        String startMLC = "/*";
        String endMLC = "*/";
        StringBuilder currentStatement = new StringBuilder();
        boolean inMultilineComment = false;
        ArrayList<String> statements = new ArrayList<>();
        for (String x : script) {
            x = x.trim();
            // skip any comments
            if (x.endsWith(endMLC)) {
                inMultilineComment = false;
                continue;
            }
            if (x.startsWith(lineComment) || inMultilineComment) {
                continue;
            }
            if (x.startsWith(startMLC)) {
                inMultilineComment = true;
                continue;
            }

            if (x.isEmpty() || x.isBlank()) {
                continue;
            }
            // finally, start accumulating things
            if (x.endsWith(stmtEnd)) {
                currentStatement.append(x.substring(0, x.length() - 1)); // when executing SQL statements, JDBC requires they do NOT end in ;, so strip it.
                statements.add(currentStatement.toString());
                currentStatement = new StringBuilder();
            } else {
                currentStatement.append(x + "\n");
            }
        }
        return statements;
    }
}