package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import java.util.Collection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:49 PM
 */
public class ClientConfiguration extends BaseClientConfiguration {
    public boolean isRefreshTokensEnabled() {
        return refreshTokensEnabled;
    }

    public void setRefreshTokensEnabled(boolean refreshTokensEnabled) {
        this.refreshTokensEnabled = refreshTokensEnabled;
    }

    public long getRefreshTokenLifetime() {
        return refreshTokenLifetime;
    }

    public void setRefreshTokenLifetime(long refreshTokenLifetime) {
        this.refreshTokenLifetime = refreshTokenLifetime;
    }

    public Collection<String> getEnabledScopes() {
        return enabledScopes;
    }

    public void setEnabledScopes(Collection<String> enabledScopes) {
        this.enabledScopes = enabledScopes;
    }

    public Collection<String> getDisabledScopes() {
        return disabledScopes;
    }

    public void setDisabledScopes(Collection<String> disabledScopes) {
        this.disabledScopes = disabledScopes;
    }

    boolean refreshTokensEnabled = false;
    long refreshTokenLifetime = 0L;
    Collection<String> enabledScopes = null;
    Collection<String> disabledScopes = null;
}
