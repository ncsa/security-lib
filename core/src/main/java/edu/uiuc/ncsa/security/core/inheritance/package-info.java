/**
 * A generic multiple inheritance package. This is mostly used by the configurations
 * but in point of fact could be used for other projects as well. The major point
 * is its simple inheritance model: You provide a list of prototypes (aka parents,
 * antecessors) and these are processed <i>in order</i> for resolution. This means that
 * there is no "diamond problem" or others. When in doubt, override to the implementation
 * you want.
 * <p>Created by Jeff Gaynor<br>
 * on 4/18/23 at  6:18 AM
 */
package edu.uiuc.ncsa.security.core.inheritance;