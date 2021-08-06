package edu.uiuc.ncsa.security.delegation.server.storage;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.delegation.storage.BaseClient;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/12/16 at  1:19 PM
 */
public interface BaseClientStore<V extends BaseClient> extends Store<V> {

    /**
     * Retrieve all clients of a given type from the approval store for the given status.
     * @return
     */
    List<Identifier> getByStatus(String status, ClientApprovalStore clientApprovalStore);

}
