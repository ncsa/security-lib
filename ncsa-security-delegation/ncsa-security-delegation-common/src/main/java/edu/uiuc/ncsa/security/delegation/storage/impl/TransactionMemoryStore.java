package edu.uiuc.ncsa.security.delegation.storage.impl;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.storage.TransactionStore;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.Verifier;
import edu.uiuc.ncsa.security.storage.MemoryStore;

import java.util.HashMap;

/**
 * In-memory transaction storage. This does nto persist between server restarts.
 * <p>Created by Jeff Gaynor<br>
 * on 11/4/11 at  1:32 PM
 */
public  class TransactionMemoryStore<V extends BasicTransaction> extends MemoryStore<V> implements TransactionStore<V> {
    public TransactionMemoryStore(IdentifiableProvider<V> vIdentifiableProvider) {
        super(vIdentifiableProvider);
    }

    /**
     * Used internally to track transactions by an identifier (such as a verifier).
     */
    protected class TokenIndex extends HashMap<String, V>{
        public TokenIndex() {
            super();
        }

    }


    TokenIndex agIndex;
    TokenIndex atIndex;
    TokenIndex vIndex;


    public TokenIndex getAgIndex() {
        if (agIndex == null) {
            agIndex = new TokenIndex();
        }
        return agIndex;
    }


    public TokenIndex getAtIndex() {
        if (atIndex == null) {
            atIndex = new TokenIndex();
        }
        return atIndex;
    }


    public TokenIndex getvIndex() {
        if (vIndex == null) {
            vIndex = new TokenIndex();
        }
        return vIndex;
    }


    @Override
    public V get(AuthorizationGrant authorizationGrant) {
        return getAgIndex().get(authorizationGrant.getToken());
    }

    @Override
    public V get(AccessToken accessToken) {
        return getAtIndex().get(accessToken.getToken());
    }

    @Override
    public V get(Verifier verifier) {
        return getvIndex().get(verifier.getToken());
    }

    /**
     * Override this as needed to update any and all indices.
     * @param v
     */
    protected void updateIndices(V v) {
        if (v.getAccessToken() != null) {
            getAtIndex().put(v.getAccessToken().getToken(), v);
        }

        if (v.getAuthorizationGrant() != null) {
            getAgIndex().put(v.getAuthorizationGrant().getToken(), v);
        }

        if (v.getVerifier() != null) {
            getvIndex().put(v.getVerifier().getToken(), v);
        }
    }

    @Override
    public void register(V value) {
        super.register(value);
        updateIndices(value);
    }

    @Override
    public void save(V value) {
        super.save(value);
        updateIndices(value);
    }

    @Override
    public void update(V value) {
        super.update(value);
        updateIndices(value);
    }

    @Override
    public void clear() {
        super.clear();
        clearIndices();
    }

    protected void clearIndices() {
        agIndex = null;
        atIndex = null;
        vIndex = null;
    }

    /**
     * Override this as needed to remove an item from all stores.
     * @param value
     */
    protected void removeItem(V value) {
        getAgIndex().remove(value.getAuthorizationGrant());
        getAtIndex().remove(value.getAccessToken());
        getvIndex().remove(value.getVerifier());
    }


    @Override
    public V remove(Object key) {
        V item = super.remove(key);
        if (item != null) {
            removeItem(item);
        }
        return item;
    }


}
