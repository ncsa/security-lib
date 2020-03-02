package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.storage.sql.SQLDatabase;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;
import net.sf.json.JSONArray;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/28/20 at  5:09 PM
 */
public class VFSDatabase extends SQLDatabase {

    public static String PATH_NAME = "path";
    public static String FILE_NAME = "name";
    public static String EA = "ea";
    public static String CONTENT = "content";
    String fqTablename = "qdl-vfs";

    public FileEntry get(String path, String filename) {

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
            JSONArray array = JSONArray.fromObject(map.getString(CONTENT));
            t = new FileEntry(array, eas);
            releaseConnection(c);
        } catch (SQLException e) {
            destroyConnection(c);
            throw new GeneralException("Error getting file for \"" + path + "\" named \"" + filename + "\".", e);
        }
        return t;
    }
    protected void update(FileEntry fileEntry){

    }
    public void put(FileEntry fileEntry) {
        Connection c = getConnection();
        FileEntry t = null;
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
            stmt.setString(1, "path");
            stmt.setString(2, "fname");
            stmt.setString(3, stringWriter.toString());
            stmt.setString(4, array.toString());
            stmt.executeQuery();
            ResultSet rs = stmt.getResultSet();
            // Now we have to pull in all the values.
            if (!rs.next()) {
                rs.close();
                stmt.close();
                releaseConnection(c);
            }

            rs.close();
            stmt.close();

            releaseConnection(c);
        } catch (SQLException | IOException e) {
            destroyConnection(c);
            throw new GeneralException("Error saving file", e);
        }
    }
}
