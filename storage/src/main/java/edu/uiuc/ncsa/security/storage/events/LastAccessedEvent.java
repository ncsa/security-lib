package edu.uiuc.ncsa.security.storage.events;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.MonitoredStoreInterface;

import java.util.Date;
import java.util.EventObject;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/28/23 at  4:24 PM
 */
public class LastAccessedEvent extends EventObject {
    public LastAccessedEvent(Object source) {
        super(source);
    }


    public LastAccessedEvent(MonitoredStoreInterface store, Identifier identifier, Date lastAccessed) {
        super(store);
        this.lastAccessed = lastAccessed;
        this.identifier = identifier;
    }

    public MonitoredStoreInterface getStore() {
        return (MonitoredStoreInterface) source;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }


    Date lastAccessed = null;

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    Identifier identifier;

    public UUID getUUID() {
        return getStore().getUuid();
    }

}
