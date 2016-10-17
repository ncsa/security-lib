package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.server.storage.AdminClientStore;
import edu.uiuc.ncsa.security.delegation.storage.AdminClient;
import edu.uiuc.ncsa.security.storage.MemoryStore;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/12/16 at  1:21 PM
 */
public class AdminMemoryStore<V extends AdminClient> extends MemoryStore<V> implements AdminClientStore<V> {
    public AdminMemoryStore(IdentifiableProvider<V> identifiableProvider) {
        super(identifiableProvider);
    }
}
