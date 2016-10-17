package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.server.storage.AdminClientStore;
import edu.uiuc.ncsa.security.delegation.storage.AdminClient;
import edu.uiuc.ncsa.security.storage.FileStore;
import edu.uiuc.ncsa.security.storage.data.MapConverter;

import java.io.File;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/12/16 at  1:33 PM
 */
public class AdminClientFileStore<V extends AdminClient> extends FileStore<V> implements AdminClientStore<V> {
    public static final String DEFAULT_TABLENAME = "admins";

    public AdminClientFileStore(File directory, IdentifiableProvider<V> idp, MapConverter<V> cp) {
        super(directory, idp, cp);
    }

    public AdminClientFileStore(File storeDirectory, File indexDirectory, IdentifiableProvider<V> identifiableProvider, MapConverter<V> converter) {
        super(storeDirectory, indexDirectory, identifiableProvider, converter);
    }
}
