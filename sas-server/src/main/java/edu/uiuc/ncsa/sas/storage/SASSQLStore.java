package edu.uiuc.ncsa.sas.storage;

import edu.uiuc.ncsa.sas.satclient.ClientConverter;
import edu.uiuc.ncsa.sas.satclient.SATClient;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

import javax.inject.Provider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:11 PM
 */
public class SASSQLStore<V extends SATClient> extends SQLStore<V> implements SASClientStore<V> {
    public SASSQLStore(ConnectionPool connectionPool, Table table, Provider<V> identifiableProvider, MapConverter<V> converter) {
        super(connectionPool, table, identifiableProvider, converter);
    }

    public SASSQLStore() {
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
