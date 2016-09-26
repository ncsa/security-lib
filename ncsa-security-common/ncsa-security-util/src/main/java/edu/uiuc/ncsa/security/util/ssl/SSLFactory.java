package edu.uiuc.ncsa.security.util.ssl;

/**
 * A Factory with SSL support for those that need it.
 * <p>Created by Jeff Gaynor<br>
 * on Aug 25, 2010 at  4:01:54 PM
 */
public interface SSLFactory {
    public abstract SSLConfiguration newSSLConfiguration();
}
