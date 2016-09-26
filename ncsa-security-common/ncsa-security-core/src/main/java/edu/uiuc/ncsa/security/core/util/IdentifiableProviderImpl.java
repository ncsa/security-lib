package edu.uiuc.ncsa.security.core.util;


import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.Identifier;

import javax.inject.Provider;

/**
 * Use this to create various identifiable things, i.e., objects that have globally unique
 * ids.
 * <p>Created by Jeff Gaynor<br>
 * on 4/3/12 at  2:53 PM
 */
public abstract class IdentifiableProviderImpl<V extends Identifiable> implements IdentifiableProvider<V> {
    protected Provider<Identifier> idProvider;

    protected IdentifiableProviderImpl(Provider<Identifier> idProvider) {
        this.idProvider = idProvider;
    }

    @Override
    public V get() {
        return get(true);
    }

    /**
     * This utility method returns a new identifier if the argument is true and a null otherwise.
     * Call this to do the identifier creation logic when implementing {@link #get(boolean)}.
     * @param createNewIdentifier
     * @return
     */
    protected Identifier createNewId(boolean createNewIdentifier){
        if(createNewIdentifier) return idProvider.get();
        return null;
    }
}
