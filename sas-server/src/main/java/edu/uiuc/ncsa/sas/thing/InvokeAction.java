package edu.uiuc.ncsa.sas.thing;

import edu.uiuc.ncsa.sas.Executable;
import edu.uiuc.ncsa.sas.SASConstants;
import net.sf.json.JSONArray;

/**
 * Invoke a specific method in the {@link Executable} implementation.
 * This is to allow for back channel communication, so {@link Executable#execute(Action)}
 * is the main entry point. An example would be QDL, where execute is used to simply forward the user's
 * input to the interpreter, but invoke would call a workplace function for, e.g. populating the auto
 * complete feature at startup.
 * <p>Created by Jeff Gaynor<br>
 * on 8/20/22 at  11:04 PM
 */
public class InvokeAction extends Action{
    public InvokeAction() {
        super(SASConstants.ACTION_INVOKE);
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
}
