package edu.uiuc.ncsa.security.storage.sql.internals;

import edu.uiuc.ncsa.security.storage.data.DataDescriptors;

/**
 * Descriptors for the columns in an SQL database. One of the issues with JDBC is
 * that certain statements are order dependent -- the order in which columns are
 * described must be carefully followed. This sorts all the columns alphabetically
 * and strictly follows this canonical ordering for all subsequent operations.
 * You need merely make a relationship between a column name and a {@link java.sql.Types}
 * value. Logically this is a {@link java.util.TreeSet}.
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/11 at  2:50 PM
 */
public class ColumnDescriptors extends DataDescriptors<ColumnDescriptorEntry> {
    public ColumnDescriptorEntry getPrimaryKey() {
        for (ColumnDescriptorEntry cde : this) {
            if (cde.isPrimaryKey()) {
                return cde;
            }
        }
        return null;
    }
}
