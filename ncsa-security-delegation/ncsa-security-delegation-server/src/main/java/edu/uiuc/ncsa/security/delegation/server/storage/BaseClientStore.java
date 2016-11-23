package edu.uiuc.ncsa.security.delegation.server.storage;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.delegation.storage.BaseClient;
import edu.uiuc.ncsa.security.delegation.storage.impl.BaseClientConverter;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/12/16 at  1:19 PM
 */
public interface BaseClientStore<V extends BaseClient> extends Store<V> {

    IdentifiableProvider getACProvider();
    BaseClientConverter getACConverter();
}
