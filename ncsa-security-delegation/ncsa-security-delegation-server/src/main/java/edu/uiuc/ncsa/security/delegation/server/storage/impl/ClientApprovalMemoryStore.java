package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableProviderImpl;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientApproval;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientApprovalStore;
import edu.uiuc.ncsa.security.storage.MemoryStore;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/12 at  11:04 AM
 */
public  class ClientApprovalMemoryStore<V extends ClientApproval> extends MemoryStore<V> implements ClientApprovalStore<V> {

    public ClientApprovalMemoryStore(IdentifiableProviderImpl<V> vIdentifiableProvider) {
        super(vIdentifiableProvider);
    }

    @Override
    public boolean isApproved(Identifier identifier) {
        ClientApproval ca = get(identifier);
        if(ca == null){
            return false;
        }
        return get(identifier).isApproved();
    }

    @Override
    public int getUnapprovedCount() {
        int count = 0;
        for(Identifier key: keySet()){
           if(isApproved(key)){
               count++;
           }
        }
        return count;
    }
}
