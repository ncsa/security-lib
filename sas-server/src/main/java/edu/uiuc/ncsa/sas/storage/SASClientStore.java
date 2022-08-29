package edu.uiuc.ncsa.sas.storage;

import edu.uiuc.ncsa.sas.satclient.SATClient;
import edu.uiuc.ncsa.security.core.Store;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:02 PM
 */
public interface SASClientStore<V extends SATClient> extends Store<V> {
}
