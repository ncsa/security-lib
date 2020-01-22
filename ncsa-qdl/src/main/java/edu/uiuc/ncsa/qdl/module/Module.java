package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.state.State;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:03 AM
 */
public class Module {
    State state;

    public Module(URI namespace, String alias, State state) {
        this.state = state;
        this.alias = alias;
        this.namespace = namespace;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    String alias;


    public URI getName() {
        return namespace;
    }

    URI namespace;
}
