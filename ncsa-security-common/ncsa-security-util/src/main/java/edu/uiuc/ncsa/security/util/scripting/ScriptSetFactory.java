package edu.uiuc.ncsa.security.util.scripting;

import net.sf.json.JSONObject;

import javax.inject.Provider;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/7/20 at  6:47 AM
 */
public abstract class ScriptSetFactory<V extends ScriptSet> implements Provider<V> {
    public ScriptSetFactory(JSONObject config) {
        this.config = config;
    }

    public JSONObject getConfig() {
        return config;
    }

    JSONObject config;

    abstract public V get();
}
