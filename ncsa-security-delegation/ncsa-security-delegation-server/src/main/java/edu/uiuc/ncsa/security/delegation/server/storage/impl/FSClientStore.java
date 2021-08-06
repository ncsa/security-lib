package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.IdentifiableProviderImpl;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientApprovalStore;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientStore;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.storage.FileStore;
import edu.uiuc.ncsa.security.storage.data.MapConverter;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * File-based storage for clients.
 * <p>Created by Jeff Gaynor<br>
 * on 11/3/11 at  3:40 PM
 */
public abstract class FSClientStore<V extends Client> extends FileStore<V> implements ClientStore<V> {
    protected FSClientStore(File storeDirectory, File indexDirectory, IdentifiableProviderImpl<V> idp,MapConverter<V> cp,
                            boolean removeEmptyFiles) {
        super(storeDirectory, indexDirectory, idp, cp, removeEmptyFiles);
    }

    public FSClientStore(File f, IdentifiableProviderImpl<V> idp,MapConverter<V> cp, boolean removeEmptyFiles) {
        super(f, idp, cp, removeEmptyFiles);
    }

    @Override
    public MapConverter<V> getMapConverter() {
        return converter;
    }

    @Override
    public void realSave(boolean checkExists, V t) {
        t.setLastModifiedTS(new java.sql.Timestamp(new Date().getTime()));
        super.realSave(checkExists, t);
    }

    @Override
    public List<Identifier> getByStatus(String status, ClientApprovalStore clientApprovalStore) {
        throw new NotImplementedException();

    }
}
