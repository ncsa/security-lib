package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.storage.sql.SQLDatabase;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/28/20 at  7:17 AM
 */
public class VFSMySQLProvider extends AbstractVFSFileProvider {
    VFSDatabase db;
    String scheme;
    @Override
    public String getScheme() {
        return scheme;
    }

    String mountPoint;
    @Override
    public String getMountPoint() {
        return mountPoint;
    }

    @Override
    public boolean checkScheme(String name) {
        return false;
    }

    @Override
    public VFSEntry get(String name) throws Throwable {
        if(!canRead()){
                throw new QDLException("Error: You do not have permission to read from the virtual file system");
            }
            String head = getScheme() + getMountPoint() ;
            if (!name.startsWith(head)) {
                return null;
            }

           return null;
    }

    @Override
    public void put(String name, VFSEntry script) throws Throwable {

    }

    @Override
    public boolean isScript(String name) {
        return false;
    }

    @Override
    public boolean canRead() {
        return false;
    }

    @Override
    public boolean canWrite() {
        return false;
    }
}
