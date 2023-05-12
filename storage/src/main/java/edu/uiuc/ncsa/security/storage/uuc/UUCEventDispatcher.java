package edu.uiuc.ncsa.security.storage.uuc;

import edu.uiuc.ncsa.security.core.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/10/23 at  10:39 AM
 */
public class UUCEventDispatcher<V extends Identifiable> implements UUCStoreInterface<V> {
    List<UUCEventListener> uucEventListeners = new ArrayList<>();
    @Override
    public List<UUCEventListener> getUUCEventListeners() {
        return uucEventListeners;
    }

    @Override
    public void addUUCEventListener(UUCEventListener uucEventListener) {
              uucEventListeners.add(uucEventListener);
    }

    @Override
    public void fireUUCEvent(UUCStoreInterface uucStoreInterface) {
           if(!isEnabled()){
               return; // do nothing.
           }
    }

    boolean enabled = false;
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void processUnused() {

    }
}
