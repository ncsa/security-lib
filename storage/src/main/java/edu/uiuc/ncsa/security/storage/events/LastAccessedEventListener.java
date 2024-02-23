package edu.uiuc.ncsa.security.storage.events;

import edu.uiuc.ncsa.security.storage.MonitoredStoreInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/28/23 at  4:39 PM
 */
public class LastAccessedEventListener {
    public void itemAccessed(LastAccessedEvent lastAccessedEvent) {
        storeMap.put(lastAccessedEvent.getUUID(), lastAccessedEvent.getStore());

        // ensures that we have a map of store.id.event
        if (idsByStoreMap.containsKey(lastAccessedEvent.getUUID())) {
            IDMap idMap = idsByStoreMap.get(lastAccessedEvent.getUUID());
            idMap.put(lastAccessedEvent); // fine to overwrite.
        } else {
            IDMap idMap = new IDMap();
            idMap.put(lastAccessedEvent);
            idsByStoreMap.put(lastAccessedEvent.getUUID(), idMap);
        }

    }

    public Map<UUID, IDMap> getIdsByStoreMap() {
        return idsByStoreMap;
    }

    public Map<UUID, MonitoredStoreInterface> getStoreMap() {
        return storeMap;
    }

    Map<UUID, IDMap> idsByStoreMap = new HashMap<>();
    Map<UUID, MonitoredStoreInterface> storeMap = new HashMap<>();

    public void clear() {
        idsByStoreMap = new HashMap<>();
        storeMap = new HashMap<>();
    }
}
