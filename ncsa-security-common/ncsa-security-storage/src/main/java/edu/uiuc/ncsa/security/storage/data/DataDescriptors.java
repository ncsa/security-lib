package edu.uiuc.ncsa.security.storage.data;

import edu.uiuc.ncsa.security.storage.sql.internals.ColumnDescriptorEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

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
public class DataDescriptors<V extends DataDescriptorEntry> extends TreeSet<V> {
    public HashMap<String, V> getEntries() {
        if (entries == null) {
            entries = new HashMap<String, V>();
        }
        return entries;
    }



    @Override
    public boolean add(V columnDescriptorEntry) {
        boolean rc = super.add(columnDescriptorEntry);
        getEntries().put(columnDescriptorEntry.getName(), columnDescriptorEntry);
        return rc;
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        boolean rc = super.addAll(c);
        for (V cde : c) {
            getEntries().put(cde.getName(), cde);

        }
        return rc;
    }

    @Override
    public boolean remove(Object o) {
        boolean rc = super.remove(o);
        if (o instanceof ColumnDescriptorEntry) {
            getEntries().remove(((ColumnDescriptorEntry) o).getName());
        }
        return rc;
    }

    HashMap<String, V> entries;


    public V get(String name) {
        if (name == null) return null;
        for (V cde : this) {
            if (name.equals(cde.getName())) {
                return cde;
            }
        }
        return null;
    }
}
