package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.delegation.storage.impl.BasicTransaction;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.Verifier;

/**
 * A store for delegation transactions.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 18, 2011 at  3:38:20 PM
 */
public interface TransactionStore<V extends BasicTransaction> extends Store<V> {

    V get(AuthorizationGrant authorizationGrant);

    V get(AccessToken accessToken);

    V get(Verifier verifier);

}
