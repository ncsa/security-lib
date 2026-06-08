package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Convenience mao that just has an {@link Identifiable} as the value and
 * its key is the {@link Identifier}. Very common idiom.
 * <h3>Comment</h3>
 * To add this to another store with {@link java.util.Map#putAll(java.util.Map)},
 * especially {@link edu.uiuc.ncsa.security.core.Store#putAll(Map)}, you may
 * need to get around issues with generics. You can always pass this in the constructor of
 * another mao which will resolve it, E.g.
 * <pre>
 *     IdentifiableMap im = new IdentifiableMap();
 *     // stuff
 *     myOtherMap.putAll(new HashMap(im));
 * </pre>
 */
public class IdentifiableMap<V extends Identifiable> extends HashMap<Identifier, V> {
    public void put(V identifiable){
        put(identifiable.getIdentifier(), identifiable);
    }

    public IdentifiableMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IdentifiableMap(int initialCapacity) {
        super(initialCapacity);
    }

    public IdentifiableMap() {
    }

    public IdentifiableMap(Map<? extends Identifier, ? extends V> m) {
        super(m);
    }
}
