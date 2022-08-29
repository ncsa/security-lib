package edu.uiuc.ncsa.sas.thing.action;

import edu.uiuc.ncsa.sas.Executable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Invoke a specific method in the {@link Executable} implementation.
 * This is to allow for back channel communication, so {@link Executable#execute(Action)}
 * is the main entry point. An example would be QDL, where execute is used to simply forward the user's
 * input to the interpreter, but invoke would call a workplace function for, e.g. populating the auto
 * complete feature at startup.
 * <p>Created by Jeff Gaynor<br>
 * on 8/20/22 at  11:04 PM
 */
public class InvokeAction extends Action {
    public InvokeAction() {
        super(ACTION_INVOKE);
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONArray getArgs() {
        return args;
    }

    public void setArgs(JSONArray args) {
        this.args = args;
    }

    JSONArray args;

    @Override
    public JSONObject serialize() {
        JSONObject jsonObject = super.serialize();
        jsonObject.put(KEYS_METHOD, name == null ? "" : name);
        jsonObject.put(KEYS_ARGUMENT, args == null ? new JSONArray() : args);
        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if (json.containsKey(KEYS_METHOD)) {
            name = json.getString(KEYS_METHOD);
        }
        if (json.containsKey(KEYS_ARGUMENT)) {
            args = JSONArray.fromObject(json.getJSONArray(KEYS_ARGUMENT));
        }
    }
}
