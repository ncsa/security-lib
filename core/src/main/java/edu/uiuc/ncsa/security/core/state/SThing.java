package edu.uiuc.ncsa.security.core.state;

import java.io.Serializable;

/**
 * Top-level object for a thing (object) that has a name in that is unique in a local
 * scope. These are the basic object in local state. {@link STable}s are populated with
 * these and kept in {@link SStack}s. Requests to the stack be name return the first
 * so-named thing. This allows, for instance, a local variable, <it>x</it>, to
 * override another variable <it>x</it>. It is the basis for encapsulation.
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/21 at  5:15 AM
 */
public interface SThing extends Serializable, Cloneable {
    String getName();
    SKey getKey();
}
