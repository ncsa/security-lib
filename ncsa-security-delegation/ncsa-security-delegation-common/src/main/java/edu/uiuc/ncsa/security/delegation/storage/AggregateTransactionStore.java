package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.delegation.storage.impl.BasicTransaction;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.Verifier;
import edu.uiuc.ncsa.security.storage.AggregateStore;

/**
 * An aggregate store for transactions.
 * <p>Created by Jeff Gaynor<br>
 * on 5/24/12 at  11:19 AM
 */
public class AggregateTransactionStore<V extends TransactionStore> extends AggregateStore<V> implements TransactionStore {
    public AggregateTransactionStore(V... stores) {
        super(stores);
    }

    @Override
    public BasicTransaction get(AccessToken accessToken) {
        BasicTransaction t = null;
        for(TransactionStore s: stores){
           t = s.get(accessToken);
            if(t != null) return t;
        }
        return null;
    }

    @Override
    public BasicTransaction get(AuthorizationGrant authorizationGrant) {
        BasicTransaction t = null;
        for(TransactionStore s: stores){
           t = s.get(authorizationGrant);
            if(t != null) return t;
        }
        return null;
    }

    @Override
    public BasicTransaction get(Verifier verifier) {
        BasicTransaction t = null;
        for(TransactionStore s: stores){
           t = s.get(verifier);
            if(t != null) return t;
        }
        return null;
    }
}
