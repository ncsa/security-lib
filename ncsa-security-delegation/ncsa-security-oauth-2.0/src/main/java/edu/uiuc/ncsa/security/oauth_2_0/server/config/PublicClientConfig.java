package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

/**
 * Configuration of all public clients.
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:48 PM
 */
public class PublicClientConfig extends ClientConfiguration {
    @Override
    public void setClientSecretLength(int clientSecretLength) {
        throw new NotImplementedException("Error: client secrets not used in public clients");
    }
}
