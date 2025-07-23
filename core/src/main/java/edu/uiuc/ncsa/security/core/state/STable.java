package edu.uiuc.ncsa.security.core.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * A symbol table. This should hold variables, functions or whatever that needs to have scope managed..
 * Sequences of these are managed by {@link SStack}.
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/21 at  5:14 AM
 */
public abstract class STable<K extends SKey, V extends SThing> extends HashMap<K, V> implements Cloneable, Serializable {

    /**
     * Should add the {@link SThing} based on its {@link SThing#getName()} as the key.
     *
     * @param value
     * @return
     */
    public V put(SThing value) {
        return put((K) value.getKey(), (V) value);
    }

    UUID uuid = UUID.randomUUID();

    public UUID getID() {
        return uuid;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "uuid=" + uuid +
                "size=" + size() +
                '}';
    }


}
