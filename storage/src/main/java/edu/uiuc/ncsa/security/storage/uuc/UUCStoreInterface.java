package edu.uiuc.ncsa.security.storage.uuc;

import edu.uiuc.ncsa.security.core.Identifiable;

import java.util.List;

/**
 * Client stores that support this need to implement it. There is a facade though that
 * dispatches these calls.
 * <p>Created by Jeff Gaynor<br>
 * on 5/10/23 at  9:03 AM
 */
public interface UUCStoreInterface<V extends Identifiable> {
    List<UUCEventListener> getUUCEventListeners();
    void addUUCEventListener(UUCEventListener uucEventListener);
    void fireUUCEvent(UUCStoreInterface uucStoreInterface);
    boolean isEnabled();
    void setEnabled(boolean enabled);
    void processUnused();
}
