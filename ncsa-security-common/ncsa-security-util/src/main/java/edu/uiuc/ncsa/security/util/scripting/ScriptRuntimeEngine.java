package edu.uiuc.ncsa.security.util.scripting;

import net.sf.json.JSONObject;

/**
 * This contains a {@link ScriptSet} and resolves requests based on information in the request. It then runs the
 * selected script(s), returning the response when done.
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/20 at  12:24 PM
 */
public abstract class ScriptRuntimeEngine {
    public StateInterface getState() {
        return state;
    }

    public void setState(StateInterface state) {
        this.state = state;
    }

    protected JSONObject config;
    StateInterface state;
    public abstract  String serializeState();
    public abstract   void deserializeState(String state);
    public ScriptRuntimeEngine(JSONObject config) {
        this.config = config;
    }

    public ScriptSet getScriptSet() {
        return scriptSet;
    }

    public void setScriptSet(ScriptSet scriptSet) {
        this.scriptSet = scriptSet;
    }

    ScriptSet scriptSet;

    public abstract ScriptRunResponse run(ScriptRunRequest request);
}
