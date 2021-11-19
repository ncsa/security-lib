package edu.uiuc.ncsa.qdl.functions;

import java.io.Serializable;

/**
 * Top-level object for a thing (object) that has a name in that is unique in a local
 * scope. These are the basic object in local state. {@link XTable}s are populated with
 * these and kept in {@link XStack}s. Requests to the stack be name return the first
 * so-named thing. This allows, for instance, a local variable, <it>x</it>, to
 * override another variable <it>x</it>. It is the basis for encapsulation.
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/21 at  5:15 AM
 */
public interface QDLStateThing extends Serializable, Cloneable {
    String getName();
}
