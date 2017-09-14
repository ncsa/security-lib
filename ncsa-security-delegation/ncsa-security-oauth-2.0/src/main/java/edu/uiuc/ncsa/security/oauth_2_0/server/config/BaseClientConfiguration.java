package edu.uiuc.ncsa.security.oauth_2_0.server.config;

/**
 * This contains what the server should do as default behaviors for all clients of a given type.
 * At this point, there are at least 3 types of client, admin, secure (the original) and public.
 * Clients cannot override the server configuration, so, for instance, if a client requests scopes
 * that have been disallowed at the server level, they will not be honored. Disabling a type of client will
 * cause the server to reject all requests for that type of client. Disabling registration means
 * that clients of that type cannot be registered, even if the registration servlet has been deployed.
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:37 PM
 */
public abstract class BaseClientConfiguration {
    boolean registrationEnabled = true;
    boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxAllowedNewClientRequests() {
        return maxAllowedNewClientRequests;
    }

    public void setMaxAllowedNewClientRequests(int maxAllowedNewClientRequests) {
        this.maxAllowedNewClientRequests = maxAllowedNewClientRequests;
    }

    public boolean isRegistrationEnabled() {
        return registrationEnabled;
    }

    public void setRegistrationEnabled(boolean registrationEnabled) {
        this.registrationEnabled = registrationEnabled;
    }

    int maxAllowedNewClientRequests = 100;

    public int getClientSecretLength() {
        return clientSecretLength;
    }

    public void setClientSecretLength(int clientSecretLength) {
        this.clientSecretLength = clientSecretLength;
    }

    int clientSecretLength = 64;
}
