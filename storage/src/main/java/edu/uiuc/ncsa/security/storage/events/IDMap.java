package edu.uiuc.ncsa.security.storage.events;

import edu.uiuc.ncsa.security.core.Identifier;

import java.util.HashMap;


/**
 * Very simple extension. Mostly for readability of code and ease of access.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  6:56 AM
 */
public class IDMap extends HashMap<Identifier, Long> {
    public void put(LastAccessedEvent lastAccessedEvent) {
        put(lastAccessedEvent.getIdentifier(), lastAccessedEvent.getLastAccessed().getTime());
    }
}
