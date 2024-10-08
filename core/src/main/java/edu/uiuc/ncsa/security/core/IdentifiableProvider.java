package edu.uiuc.ncsa.security.core;

import javax.inject.Provider;
import java.io.Serializable;

/**
 * Interface for a class that is to create an identifier.
 * <p>Created by Jeff Gaynor<br>
 * on 11/4/13 at  12:56 PM
 */
public interface IdentifiableProvider<V> extends  Provider<V>, Serializable {
    /**
     * Create a new Identifiable object and create a new identifier if true.
     * The no-arg call to {@link #get()} should be the same as <code>get(true)</code>.
     * @param createNewIdentifier
     * @return
     */
    public V get(boolean createNewIdentifier);
}
