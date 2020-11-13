package edu.uiuc.ncsa.security.delegation.token.impl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/9/20 at  9:00 AM
 */
public interface OA1Token {
    String getSharedSecret();
    void setSharedSecret(String sharedSecret);
    String SHARED_SECRET_KEY = "shared_secret";
}
