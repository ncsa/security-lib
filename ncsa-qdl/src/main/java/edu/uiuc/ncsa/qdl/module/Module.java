package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.state.State;
import net.sf.json.JSONArray;

import java.io.Serializable;
import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:03 AM
 */
public class Module implements Serializable {
    /**
     * This returns true only if the module is from another language than a QDL module.
     * @return
     */
    public boolean isExternal(){
        return false;
    }
    public Module() {
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

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


    public URI getNamespace() {
        return namespace;
    }

    public void setNamespace(URI namespace) {
        this.namespace = namespace;
    }

    URI namespace;

    public JSONArray toJSON() {
        JSONArray array = new JSONArray();
        array.add(getNamespace().toString());
        array.add(alias);
        return array;
    }

    public void fromJSON(JSONArray array) {
        namespace = URI.create(array.getString(0));
        alias = array.getString(1);
    }

    @Override
    public String toString() {
        return "Module{" +
                ", namespace=" + namespace +
                ", alias='" + alias + '\'' +
                '}';
    }
}
