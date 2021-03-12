package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.exceptions.QDLIOException;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.SQLDatabase;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;
import net.sf.json.JSONArray;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Note that all of the arguments to this database for keys are assumed to be normalized, resolved,
 * etc., etc. so that no such logic is present here. The {@link VFSFileProvider} implementation
 * that has an instance of this is charged with that.
 * <p>Created by Jeff Gaynor<br>
 * on 2/28/20 at  5:09 PM
 */
public class VFSDatabase extends SQLDatabase {
    /*
    These are the names of the columns in the database. They are used for constructing the queries and updates.
     */
    public static String PATH_NAME = "path";
    public static String FILE_NAME = "name";
    public static String EA = "ea";
    public static String CONTENT = "content";
    String fqTablename = "qdl-vfs";

    public VFSDatabase(ConnectionPool connectionPool, String fqTablename) {
        super(connectionPool);
        this.fqTablename = fqTablename;
    }

    /**
     * This takes a path of the form a/b/c/.../d/ (note normalization with ending slash) and returns
     * the entries (i.e. file names) for it. A single entry denotes this is a directory, effectively.
     * No entries means this is not in the database.
     *
     * @param path
     * @return
     */
    public List<String> selectByPath(String path) {
        ArrayList<String> paths = new ArrayList<>();
        String statement = " select " + FILE_NAME + " from " + fqTablename + " where " + PATH_NAME + " =?";
        Connection c = getConnection();
        FileEntry t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(statement);
            stmt.setString(1, path);

            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();

            // Now we have to pull in all the values.
            if (!rs.next()) {
                rs.close();
                stmt.close();
                releaseConnection(c);
                return paths;
            }else{
                // If there is exactly one row in the result, add it.
                // Could also have post-checked loop
                paths.add(rs.getString(FILE_NAME));
            }
            while (rs.next()) {
                paths.add(rs.getString(FILE_NAME));
            }
            rs.close();
            stmt.close();
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            throw new QDLIOException("Error getting paths from store table" + fqTablename, e);
        }
        return paths;
    }

    public List<String> getDistinctPaths() {
        String statement = " select DISTINCT(" + PATH_NAME + ") from " + fqTablename;
        ArrayList<String> paths = new ArrayList<>();
        Connection c = getConnection();
        FileEntry t = null;
        try {
            PreparedStatement stmt = c.prepareStatement(statement);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();

            // Now we have to pull in all the values.
            if (!rs.next()) {
                rs.close();
                stmt.close();
                releaseConnection(c);
                return paths;
            }
            while (rs.next()) {
                paths.add(rs.getString(PATH_NAME));
            }
            rs.close();
            stmt.close();
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            throw new QDLIOException("Error getting paths from store table" + fqTablename, e);
        }
        return paths;
    }

    public FileEntry get(String[] key) {
        String path = key[VFSMySQLProvider.PATH_INDEX];
        String filename = key[VFSMySQLProvider.FILENAME_INDEX];

        Connection c = getConnection();
        FileEntry t = null;
        try {
            String statement = "select * from " + fqTablename + " where " + PATH_NAME + " = ? AND " + FILE_NAME + " = ?";
            PreparedStatement stmt = c.prepareStatement(statement);
            stmt.setString(1, path);
            stmt.setString(2, filename);
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            if (!rs.next()) {
                rs.close();
                stmt.close();
                releaseConnection(c);
                return null;   // returning a null fulfills contract for this.
            }

            ColumnMap map = rsToMap(rs);
            rs.close();
            stmt.close();
            XProperties eas = new XProperties(map.getString(EA));
            JSONArray array;
            try {
                array = JSONArray.fromObject(map.getString(CONTENT));
            } catch (Throwable tt) {
                // if it is not a JSON array, then stick it in one.
                array = new JSONArray();
                array.add(map.getString(CONTENT));
            }
            t = new FileEntry(array, eas);
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            throw new GeneralException("Error getting file for \"" + path + "\" named \"" + filename + "\".", e);
        }
        return t;
    }

    public boolean containsEntry(String[] key) {
        String path = key[VFSMySQLProvider.PATH_INDEX];
        String fileName = key[VFSMySQLProvider.FILENAME_INDEX];
        String statment = "Select * from " + fqTablename +
                " where " + PATH_NAME + " = ? AND " + FILE_NAME + " = ?";
        Connection c = getConnection();
        boolean rc = false;
        try {
            PreparedStatement stmt = c.prepareStatement(statment);
            stmt.setString(1, path);
            stmt.setString(2, fileName);
            stmt.execute();// just execute() since executeQuery(x) would throw an exception regardless of content of x as per JDBC spec.
            ResultSet rs = stmt.getResultSet();
            rc = rs.next();
            rs.close();
            stmt.close();
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            e.printStackTrace();
        }
        return rc;

    }

    public void update(String[] key, VFSEntry fileEntry) {
        String path = key[VFSMySQLProvider.PATH_INDEX];
        String fileName = key[VFSMySQLProvider.FILENAME_INDEX];

        String updateStatement = "update " + fqTablename +
                " set " + EA + " = ?, " +
                CONTENT + " = ? " +
                "where " + PATH_NAME + " = ? AND " + FILE_NAME + " = ?";
        Connection c = getConnection();
        try {

            PreparedStatement stmt = c.prepareStatement(updateStatement);
            StringWriter stringWriter = new StringWriter();
            fileEntry.getProperties().store(stringWriter, "");
            JSONArray array = new JSONArray();
            array.addAll(fileEntry.getLines());
            stmt.setString(1, stringWriter.toString());
            stmt.setString(2, array.toString());
            stmt.setString(3, path);
            stmt.setString(4, fileName);
            stmt.executeUpdate();
            stmt.close();
            releaseConnection(c);
        } catch (SQLException | IOException e) {
            destroyConnection(c);
            throw new GeneralException("Error updating object with identifier = \"" + path + VFSPaths.PATH_SEPARATOR + fileName, e);
        }
    }

    public void put(String[] key, VFSEntry fileEntry) {
        String path = key[VFSMySQLProvider.PATH_INDEX];
        String fileName = key[VFSMySQLProvider.FILENAME_INDEX];

        Connection c = getConnection();
        try {
            String statement = "insert into " + fqTablename + " (" +
                    PATH_NAME + "," +
                    FILE_NAME + "," +
                    EA + ", " +
                    CONTENT + " ) values (?,?, ?, ?) ";
            PreparedStatement stmt = c.prepareStatement(statement);
            StringWriter stringWriter = new StringWriter();
            fileEntry.getProperties().store(stringWriter, "");
            JSONArray array = new JSONArray();
            array.addAll(fileEntry.getLines());
            stmt.setString(1, path);
            stmt.setString(2, fileName);
            stmt.setString(3, stringWriter.toString());
            stmt.setString(4, array.toString());

            stmt.execute();// just execute() since executeQuery(x) would throw an exception regardless of content of x as per JDBC spec.
            stmt.close();
            releaseConnection(c);
        } catch (SQLException | IOException e) {
            destroyConnection(c);
            throw new GeneralException("Error saving file", e);
        }
    }


    /**
     * Make a directory. NOTE you must check elsewhere if this exists before trying to create it.
     * @param key
     * @return
     */
    public boolean mkdir(String[] key) {
        String path = key[VFSMySQLProvider.PATH_INDEX];
        String fileName = key[VFSMySQLProvider.FILENAME_INDEX];
        if (!fileName.isEmpty()) {
            return false;
        }

        path = (path.startsWith(VFSPaths.PATH_SEPARATOR) ? "" : VFSPaths.PATH_SEPARATOR)
                + path
                + (path.endsWith(VFSPaths.PATH_SEPARATOR) ? "" : VFSPaths.PATH_SEPARATOR); // make SURE it starts and ends with /
        Connection c = getConnection();
        try {
            String statement = "insert into " + fqTablename + " (" +
                    PATH_NAME + "," +
                    FILE_NAME + "," +
                    EA + ", " +
                    CONTENT + " ) values (?,?, ?, ?) ";
            PreparedStatement stmt = c.prepareStatement(statement);
            stmt.setString(1, path);
            stmt.setString(2, "");
            stmt.setString(3, "");
            stmt.setString(4, "");

            stmt.execute();// just execute() since executeQuery(x) would throw an exception regardless of content of x as per JDBC spec.
            stmt.close();
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            throw new GeneralException("Error creating directory", e);
        }
        return true;
    }

    public FileEntry remove(String[] key) throws Exception {
        String path = key[VFSMySQLProvider.PATH_INDEX];
        String fileName = key[VFSMySQLProvider.FILENAME_INDEX];

        FileEntry oldEntry = null;
        try {
            oldEntry = get(key);
        } catch (Throwable x) {
            // fine. Return null. All we care about is whether the next operations work.
        }
        String query = "DELETE FROM " + fqTablename + " WHERE " + PATH_NAME + "=? AND " + FILE_NAME + " =?";
        Connection c = getConnection();
        try {
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setString(1, path);
            stmt.setString(2, fileName);
            stmt.execute();
            stmt.close();
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            throw new GeneralException("Error getting identity providers", e);
        }
        return oldEntry;
    }

    /**
     * Completely empty the store of all entries.
     * <br/><br/>
     * Use it wisely.
     */
    public void clear() {
        String query = "DELETE FROM " + fqTablename;
        Connection c = getConnection();
        try {
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.execute();
            stmt.close();
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            throw new GeneralException("Error getting identity providers", e);
        }
    }

}
