package edu.uiuc.ncsa.security.storage.sql.internals;

import edu.uiuc.ncsa.security.storage.data.DataDescriptorEntry;

/**
 * Describes a column in an SQL database in a platform and vendor neutral way.
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/11 at  2:34 PM
 */
public class ColumnDescriptorEntry extends DataDescriptorEntry {
    public ColumnDescriptorEntry(String name, int type) {
        super(name, type);
    }

    public ColumnDescriptorEntry(String name, int type, boolean nullable, boolean primaryKey) {
        super(name, type, nullable);
        this.primaryKey = primaryKey;
    }


    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    boolean primaryKey = false;

}
