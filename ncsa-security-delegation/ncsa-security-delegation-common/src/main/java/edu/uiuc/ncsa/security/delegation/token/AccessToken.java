package edu.uiuc.ncsa.security.delegation.token;


/**
 * An access token for delegation.  This is used later to retrieve the {@link ProtectedAsset}.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 11, 2011 at  4:10:17 PM
 */
public interface AccessToken extends Token {
    /**
     * An <b>optional</b> shared secret for those implementations that support or require it. Ignored if
     * unused.
     *
     */
    String getSharedSecret();
    void setSharedSecret(String sharedSecret);
}
