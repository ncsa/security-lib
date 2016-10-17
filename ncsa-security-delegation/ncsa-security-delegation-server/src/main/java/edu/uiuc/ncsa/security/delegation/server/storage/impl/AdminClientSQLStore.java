package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.delegation.server.storage.AdminClientStore;
import edu.uiuc.ncsa.security.delegation.storage.AdminClient;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

import javax.inject.Provider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/12/16 at  1:35 PM
 */
public class AdminClientSQLStore<V extends AdminClient> extends SQLStore<V> implements AdminClientStore<V> {
    public AdminClientSQLStore() {
    }

    public AdminClientSQLStore(ConnectionPool connectionPool, Table table, Provider<V> identifiableProvider, MapConverter<V> converter) {
        super(connectionPool, table, identifiableProvider, converter);
    }
}
