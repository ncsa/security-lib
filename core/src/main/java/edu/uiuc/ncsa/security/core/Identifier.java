package edu.uiuc.ncsa.security.core;

import java.io.Serializable;
import java.net.URI;

/**
 * Marker interface for identifiers. Identifiers conform to the semantics of URIs.
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
