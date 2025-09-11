package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.sas.client.ClientKeys;
import edu.uiuc.ncsa.sas.client.ClientProvider;
import edu.uiuc.ncsa.sas.client.ClientTable;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;
import edu.uiuc.ncsa.security.storage.sql.SQLStoreProvider;
import edu.uiuc.ncsa.security.storage.sql.derby.DerbyConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:39 PM
 */
public class SQLClientStoreProvider<T extends SQLStore> extends SQLStoreProvider<T> {
    public SQLClientStoreProvider() {
    }

    public SQLClientStoreProvider(ConnectionPoolProvider<? extends ConnectionPool> cpp, String type, MapConverter converter, ClientProvider clientProvider) {
        super(cpp, type, "clients", "clients",  converter);
        this.clientProvider = clientProvider;
    }

    ClientProvider clientProvider;
    @Override
    public T newInstance(Table table) {
        return null;
    }

    @Override
    public T get() {
        ClientTable clientTable = new ClientTable(new ClientKeys(), getSchema(), getPrefix(), getTablename());
        return newInstance(clientTable);
    }

    @Override
    public String getSchema() {
        return ((DerbyConnectionParameters)getConnectionPool().getConnectionParameters()).getSchema();
    }
}
