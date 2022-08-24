package edu.uiuc.ncsa.sas.client;

import edu.uiuc.ncsa.security.storage.data.SerializationKeys;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnDescriptorEntry;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.TIMESTAMP;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  10:30 AM
 */
public class ClientTable extends Table {
    public ClientTable(SerializationKeys keys, String schema, String tablenamePrefix, String tablename) {
        super(keys, schema, tablenamePrefix, tablename);
    }

    protected ClientKeys getClientKeys(){
        return (ClientKeys) keys;
    }
    @Override
    public void createColumnDescriptors() {
        super.createColumnDescriptors();
        getColumnDescriptor().add(new ColumnDescriptorEntry(getClientKeys().name(), LONGVARCHAR));
        getColumnDescriptor().add(new ColumnDescriptorEntry(getClientKeys().config(), LONGVARCHAR));
        getColumnDescriptor().add(new ColumnDescriptorEntry(getClientKeys().creation_ts(), TIMESTAMP));
        getColumnDescriptor().add(new ColumnDescriptorEntry(getClientKeys().publicKey(), LONGVARCHAR));

    }
}
