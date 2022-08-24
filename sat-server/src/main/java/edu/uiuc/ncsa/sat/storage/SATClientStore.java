package edu.uiuc.ncsa.sat.storage;

import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.security.core.Store;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:02 PM
 */
public interface SATClientStore<V extends SATClient> extends Store<V> {
}
