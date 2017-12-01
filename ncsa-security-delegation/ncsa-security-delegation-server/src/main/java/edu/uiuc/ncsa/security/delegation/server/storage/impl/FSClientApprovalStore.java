package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableProviderImpl;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientApproval;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientApprovalStore;
import edu.uiuc.ncsa.security.storage.FileStore;
import edu.uiuc.ncsa.security.storage.data.MapConverter;

import java.io.File;

/**
 * A store for client approvals.
 * <p>Created by Jeff Gaynor<br>
 * on 11/3/11 at  3:43 PM
 */
public abstract class FSClientApprovalStore<V extends ClientApproval> extends FileStore<V> implements ClientApprovalStore<V> {
    protected FSClientApprovalStore(File storeDirectory, File indexDirectory,
                                    IdentifiableProviderImpl<V> idp,
                                    MapConverter<V> cp) {
        super(storeDirectory, indexDirectory, idp, cp);
    }

    protected FSClientApprovalStore(File file,
                                    IdentifiableProviderImpl<V> idp,
                                    MapConverter<V> cp) {
        super(new File(file, "cas"), idp, cp);
    }


    @Override
    public boolean isApproved(Identifier identifier) {
        ClientApproval ca = get(identifier);
        if (ca == null) {
            return false;
        }

        return ca.isApproved();
    }

    @Override
    public int getUnapprovedCount() {
        int count = 0;
        for (Identifier key : keySet()) {
            if (!isApproved(key)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getPendingCount() {
        int count = 0;
        for (Identifier key : keySet()) {
            ClientApproval approval = get(key);

            if (approval==null || (approval.getStatus() == ClientApproval.Status.PENDING)) {
                count++;
            }
        }
        return count;
    }
}
