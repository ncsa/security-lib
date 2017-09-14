package edu.uiuc.ncsa.security.oauth_2_0.server.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:47 PM
 */
public class AdminClientConfig extends BaseClientConfiguration {
    public boolean isApiEnabled() {
        return apiEnabled;
    }

    public void setApiEnabled(boolean apiEnabled) {
        this.apiEnabled = apiEnabled;
    }

    boolean apiEnabled;
}
