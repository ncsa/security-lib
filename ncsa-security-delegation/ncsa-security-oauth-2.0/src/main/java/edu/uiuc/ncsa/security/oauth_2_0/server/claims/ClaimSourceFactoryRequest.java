package edu.uiuc.ncsa.security.oauth_2_0.server.claims;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.util.Collection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 12/19/16 at  11:26 AM
 */
public class ClaimSourceFactoryRequest {
    public Collection<String> getScopes() {
        return scopes;
    }

    public void setScopes(Collection<String> scopes) {
        this.scopes = scopes;
    }

    public MyLoggingFacade getLogger() {
        return logger;
    }

    public void setLogger(MyLoggingFacade logger) {
        this.logger = logger;
    }

    Collection<String> scopes;
    MyLoggingFacade logger;


    public ClaimSourceFactoryRequest(MyLoggingFacade logger, Collection<String> scopes) {
        this.logger = logger;
        this.scopes = scopes;
    }
}