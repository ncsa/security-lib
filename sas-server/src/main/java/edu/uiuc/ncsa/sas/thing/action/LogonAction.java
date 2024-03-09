package edu.uiuc.ncsa.sas.thing.action;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  10:51 AM
 */
public class LogonAction extends Action {
    public LogonAction() {
        super(SASConstants.ACTION_LOGON);
    }

    public LogonAction(String type) {
        this();
        this.executableType = executableType;
    }

    public String getExecutableType() {
        return executableType;
    }

    public void setExecutableType(String executableType) {
        this.executableType = executableType;
    }

    String executableType = "";

    @Override
    public JSONObject serialize() {
        JSONObject json = super.serialize();
        if (!StringUtils.isTrivial(executableType)) {
            json.put(KEYS_EXECUTABLE_NAME, executableType);
        }
        return json;
    }

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if (json.containsKey(KEYS_EXECUTABLE_NAME)) {
            executableType = json.getString(KEYS_EXECUTABLE_NAME);
        }

    }
}
