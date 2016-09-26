package edu.uiuc.ncsa.security.oauth_1_0a;

import net.oauth.OAuth;

/**
 * Constants that are used for OAuth. Since these are scattered all over the place, they have
 * been centralized here.
 * <p>Created by Jeff Gaynor<br>
 * on May 29, 2010 at  9:36:13 AM
 */
public interface OAuthConstants {
    public static final String FORM_ENCODING = "application/x-www-form-urlencoded";
    public static final String OAUTH_CALLBACK = OAuth.OAUTH_CALLBACK;
    public static final String OAUTH_CONSUMER_KEY = OAuth.OAUTH_CONSUMER_KEY;

    /**
     * Useful for clients that are anonymous.
     */
    public static final String PRIVATE_KEY = net.oauth.signature.RSA_SHA1.PRIVATE_KEY;
    public static final String PUBLIC_KEY = net.oauth.signature.RSA_SHA1.PUBLIC_KEY;
    public static final String RSA_SHA1 = OAuth.RSA_SHA1;
    public static final String CERT_REQUEST = "certreq";
    public static final String CERT_LIFETIME = "certlifetime";


}
