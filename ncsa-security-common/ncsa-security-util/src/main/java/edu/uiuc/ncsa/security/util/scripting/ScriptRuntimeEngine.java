package edu.uiuc.ncsa.security.util.scripting;

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

    StateInterface state;
    public abstract  String serializeState();
    public abstract   void deserializeState(String state);

    public ScriptSet getScriptSet() {
        return scriptSet;
    }

    public void setScriptSet(ScriptSet scriptSet) {
        this.scriptSet = scriptSet;
    }

    public void clearScriptSet(){
        this.scriptSet = null;
    }
    ScriptSet scriptSet;

    public abstract ScriptRunResponse run(ScriptRunRequest request);

    /**
     * If the engine has had the scripts already injected, then this will return <code>true</code>.
     *
     * @return
     */
    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    boolean initialized = false;
}
