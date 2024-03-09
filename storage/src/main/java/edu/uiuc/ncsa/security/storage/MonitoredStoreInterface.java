package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.storage.events.IDMap;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEventListener;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepResponse;

import java.util.List;
import java.util.UUID;

/**
 * Part of the event mechanism for tracking the last access time of store objects.
 * This is used in OA4MP but has to be here for Java package visibility.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  7:19 AM
 */
public interface MonitoredStoreInterface<V extends Identifiable> {
    List<LastAccessedEventListener> getLastAccessedEventListeners();

    UUID getUuid();

    void addLastAccessedEventListener(LastAccessedEventListener lastAccessedEventListener);

    void fireLastAccessedEvent(MonitoredStoreInterface store, Identifier identifier);

    void lastAccessUpdate(IDMap idMap);

    /**
     * Generally this is enabled, except in things like the CLI where you do not want
     * to monitor access to clients or whatever.
     *
     * @return
     */
    boolean isMonitorEnabled();

    void setMonitorEnabled(boolean x);
     void setUpkeepConfiguration(UpkeepConfiguration upkeepConfiguration);
    UpkeepConfiguration getUpkeepConfiguration();

    /**
     * Do the upkeep. Note that some stores may have to update other stores. The
     * environment allows for this. E.g. Deleting a client should delete its approval
     * record and remove any permissions associated with it.
     * @param environment
     * @return
     */
    UpkeepResponse doUpkeep(AbstractEnvironment environment);
     void updateHook(String action, AbstractEnvironment environment,  List<Identifier> identifiers) ;


}
