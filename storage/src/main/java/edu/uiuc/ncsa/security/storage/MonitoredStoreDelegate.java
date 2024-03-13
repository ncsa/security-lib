package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.storage.events.IDMap;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEvent;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEventListener;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *  This is the logic behind monitoring a store. It should be a class of any store that
 *  does monitoring and calls should be forwarded to it. 
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  6:22 AM
 */
public  class MonitoredStoreDelegate<V extends Identifiable> implements MonitoredStoreInterface<V> {

    @Override
    public List<LastAccessedEventListener> getLastAccessedEventListeners() {
        if(lastAccessedEventListeners == null){
            lastAccessedEventListeners = new ArrayList<>();
        }
        return lastAccessedEventListeners;
    }

    /**
     * A unique identifier for this instance, so we can stash this a hash table in the listener
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
    public void fireLastAccessedEvent(MonitoredStoreInterface store, Identifier identifier){
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

    public UpkeepConfiguration getUpkeepConfiguration() {
        return upkeepConfiguration;
    }

    public void setUpkeepConfiguration(UpkeepConfiguration upkeepConfiguration) {
        this.upkeepConfiguration = upkeepConfiguration;
    }

    UpkeepConfiguration upkeepConfiguration = null;

    public boolean hasUpkeepConfiguration(){
        return  upkeepConfiguration != null;
    }

    /**
     * This delegate does not do upkeep. The store itself must.
     * @return
     */
    @Override
    public UpkeepResponse doUpkeep(AbstractEnvironment environment) {
       throw new NotImplementedException("not implemented in facade.");
    }



    @Override
    public long updateHook(String action, AbstractEnvironment environment,  List<Identifier> identifiers) {
         return 0L;
    }


}
