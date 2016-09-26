package edu.uiuc.ncsa.security.delegation.server.storage;

import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.delegation.storage.Client;

/**
 * Marker interface for client stores
 * <p>Created by Jeff Gaynor<br>
 * on May 24, 2011 at  4:02:39 PM
 */
public interface ClientStore<V extends Client> extends Store<V> {

}
