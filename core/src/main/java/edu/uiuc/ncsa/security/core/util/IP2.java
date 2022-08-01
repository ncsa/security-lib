package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Identifier;

import java.net.URI;

/**
 * An identifier provider for OAuth 2 tokens.
 * <p>Created by Jeff Gaynor<br>
 * on 11/29/20 at  7:41 AM
 */
public class IP2<V extends Identifier> extends IdentifierProvider<V> {
    public IP2(String component) {
        super(component);
    }

    public IP2(URI uri, String component, boolean useTimestamps) {
        super(uri, component, useTimestamps);
    }

    public IP2(URI uri, String component, String versionString, boolean useTimestamps) {
        super(uri, component, versionString, useTimestamps);
    }

    public IP2(String component, boolean useTimestamps) {
        super(component, useTimestamps);
    }

    public IP2(String scheme, String schemeSpecificPart, String component, boolean useTimestamps) {
        super(scheme, schemeSpecificPart, component, useTimestamps);
    }

    public URI get(long lifetime) {
        return Identifiers.uniqueIdentifier(getCaput(), component, "v2.0", lifetime);
    }
}
