package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.core.util.IdentifiableProviderImpl;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientStore;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.storage.FileStore;
import edu.uiuc.ncsa.security.storage.data.MapConverter;

import java.io.File;

/**
 * File-based storage for clients.
 * <p>Created by Jeff Gaynor<br>
 * on 11/3/11 at  3:40 PM
 */
public abstract class FSClientStore<V extends Client> extends FileStore<V> implements ClientStore<V> {
    protected FSClientStore(File storeDirectory, File indexDirectory, IdentifiableProviderImpl<V> idp,MapConverter<V> cp) {
        super(storeDirectory, indexDirectory, idp, cp);
    }

    public FSClientStore(File f, IdentifiableProviderImpl<V> idp,MapConverter<V> cp) {
        super(f, idp, cp);
    }


}
