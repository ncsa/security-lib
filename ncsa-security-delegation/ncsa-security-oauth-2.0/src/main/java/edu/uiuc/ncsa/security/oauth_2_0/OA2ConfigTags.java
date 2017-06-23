package edu.uiuc.ncsa.security.oauth_2_0;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/28/14 at  10:37 AM
 */
public interface OA2ConfigTags {
    /**
     * This overrides the discovery for the servlet and will be used globally. It may be overridden
     * on a per client basis as well.
     */
    public String ISSUER = "issuer";
    public String MAX_CLIENT_REFRESH_TOKEN_LIFETIME = "maxClientRefreshTokenLifetime"; // in seconds, convert to ms.
    public String REFRESH_TOKEN_LIFETIME = "refreshTokenLifetime"; // in seconds, convert to ms.
    public String REFRESH_TOKEN_ENABLED = "refreshTokenEnabled"; // Enable or disable refresh tokens for this server.
    public String CLIENT_SECRET_LENGTH= "clientSecretLength"; // in bytes.
    public String ENABLE_TWO_FACTOR_SUPPORT= "enableTwoFactorSupport"; // boolean for enabling two factor support.
    // Note -- enabling two factor support boils down to not testing the connection early since the
    // password that is generated is good for exactly one use. Another complicating factor is latency: The chain of events
    // is that the user authenticates and then a callback to the client is made, which gets an access token, then
    // gets the cert. It is possible that if the generated password has a short lifetime, it will have expired by the
    // time this happens. That cannot be helped.

    /*
     * Tags for scopes element of configuration
     */
    /**
     * Tope level tag for all scopes
     */
    public String SCOPES = "scopes";
    /**
     * Tag for an individual scope.
     */
    public String SCOPE = "scope";
    public String SCOPE_ENABLED="enabled";
    /**
     * (Optional) the fully qualified path and class name of the handler for these scopes. Note
     * that only one handler for all scopes is allowed. If this is not found in the classpath,
     * then an error will be raised. Alternately, you can simply override the configuration loader
     * and specify your handler directly.
     */
    public String SCOPE_HANDLER = "handler";

}
