package edu.uiuc.ncsa.security.core;

import java.io.Serializable;
import java.net.URI;

/**
 * Marker interface for identifiers. Identifiers conform to the semantics of URIs.
 * Why use URIs as the basis? because they are parseable and can be overloaded. Most
 * identifiers are "opaque" meaning any system that uses them simply ignores their content
 * and passes them along. These identifiers can be structured in a well-documented way,
 * allowing for self-describing identifiers that can be used in a wide-variety of situations.
 * <p>Created by Jeff Gaynor<br>
 * on 4/2/12 at  10:35 AM
 */
public interface Identifier extends Comparable, Serializable {
    /**
     * Return this identifier as a {@link URI}.
     * @return
     */
    URI getUri();
}
