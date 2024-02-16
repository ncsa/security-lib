package edu.uiuc.ncsa.security.storage.events;

import edu.uiuc.ncsa.security.core.Identifier;

import java.util.HashMap;


/**
 * Very simple extension used by the {@link edu.uiuc.ncsa.security.storage.monitored.Monitored}
 * object, this keeps a running list of identifiers and their most recent access times (as longs).
 * Mostly this is for readability of code and ease of access.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  6:56 AM
 */
public class IDMap extends HashMap<Identifier, Long> {
    public void put(LastAccessedEvent lastAccessedEvent) {
        put(lastAccessedEvent.getIdentifier(), lastAccessedEvent.getLastAccessed().getTime());
    }
}
