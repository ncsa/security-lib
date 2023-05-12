package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.events.IDMap;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEvent;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  6:22 AM
 */
// Should be renamed to something like LastAccessedStoreFacade
public  class AbstractListeningStore<V extends Identifiable> implements ListeningStoreInterface<V> {

    @Override
    public List<LastAccessedEventListener> getLastAccessedEventListeners() {
        if(lastAccessedEventListeners == null){
            lastAccessedEventListeners = new ArrayList<>();
        }
        return lastAccessedEventListeners;
    }

    /**
     * A unique identifier for this instance so we can stash this a hash table in the listener
     * @return
     */
    @Override
    public UUID getUuid() {
        if(uuid==null){
            uuid = UUID.randomUUID();
        }
        return uuid;
    }

    List<LastAccessedEventListener> lastAccessedEventListeners = null;
    UUID uuid = null;
    @Override
    public void addLastAccessedEventListener(LastAccessedEventListener lastAccessedEventListener){
                getLastAccessedEventListeners().add(lastAccessedEventListener);
    }
    @Override
    public void fireLastAccessedEvent(ListeningStoreInterface store, Identifier identifier){
        if(!isMonitorEnabled()){
            return;
        }
        LastAccessedEvent lastAccessedEvent = new LastAccessedEvent(store, identifier, new Date());
        for(LastAccessedEventListener lastAccessedEventListener:getLastAccessedEventListeners()){
            lastAccessedEventListener.itemAccessed(lastAccessedEvent);
        }
    }


    @Override
    public void lastAccessUpdate( IDMap idMap){
        // no op-- override.
    }

    @Override
    public boolean isMonitorEnabled() {
        return monitorEnabled;
    }

    @Override
    public void setMonitorEnabled(boolean monitorEnabled) {
        this.monitorEnabled = monitorEnabled;
    }

    boolean monitorEnabled = true;
}
