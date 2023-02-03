package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.sas.satclient.ClientKeys;
import edu.uiuc.ncsa.sas.satclient.ClientProvider;
import edu.uiuc.ncsa.sas.satclient.ClientTable;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;
import edu.uiuc.ncsa.security.storage.sql.SQLStoreProvider;
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

    /*
         public DSClientSQLStoreProvider(ConnectionPoolProvider<? extends ConnectionPool> cpp, String type, MapConverter converter,
                                     Provider<? extends Client> clientProvider) {
        super(null, cpp, type, OA4MPConfigTags.CLIENTS_STORE, SQLClientStore.DEFAULT_TABLENAME, converter);
         this.clientProvider = clientProvider;
    }
     */
    @Override
    public T get() {
        ClientTable clientTable = new ClientTable(new ClientKeys(), getSchema(), getPrefix(), getTablename());
        return newInstance(clientTable);
    }
}