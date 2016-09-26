package edu.uiuc.ncsa.security.delegation.server.storage;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.AggregateStore;

/**
 * An aggregate client approval store.
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  11:14 AM
 */
public class AggregateCAStore<V extends ClientApprovalStore> extends AggregateStore<V> implements ClientApprovalStore {
    public AggregateCAStore(V... stores) {
        super(stores);
    }

    @Override
    public boolean isApproved(Identifier identifier) {
        for (ClientApprovalStore s : stores) {
            if (s.isApproved(identifier)) return true;
        }
        return false;
    }

    @Override
    public int getUnapprovedCount() {
        int count = 0;
        for (ClientApprovalStore s : stores) {
            count = s.getUnapprovedCount();
        }
        return count;
    }
}
