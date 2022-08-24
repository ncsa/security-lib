package edu.uiuc.ncsa.sat.storage;

import edu.uiuc.ncsa.sat.client.ClientConverter;
import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

import javax.inject.Provider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:11 PM
 */
public class SATSQLStore<V extends SATClient> extends SQLStore<V> implements SATClientStore<V> {
    public SATSQLStore(ConnectionPool connectionPool, Table table, Provider<V> identifiableProvider, MapConverter<V> converter) {
        super(connectionPool, table, identifiableProvider, converter);
    }

    public SATSQLStore() {
    }

    @Override
    public ClientConverter getMapConverter() {
        return (ClientConverter) super.getMapConverter();
    }

    @Override
    public String getCreationTSField() {
        return getMapConverter().getKeys().creation_ts();
    }
}
