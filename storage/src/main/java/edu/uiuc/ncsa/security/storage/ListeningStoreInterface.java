package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.events.IDMap;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEventListener;

import java.util.List;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  7:19 AM
 */
public interface ListeningStoreInterface<V extends Identifiable> {
    List<LastAccessedEventListener> getLastAccessedEventListeners();

    UUID getUuid();

    void addLastAccessedEventListener(LastAccessedEventListener lastAccessedEventListener);

    void fireLastAccessedEvent(Identifier identifier);

    void lastAccessUpdate(IDMap idMap);
}
