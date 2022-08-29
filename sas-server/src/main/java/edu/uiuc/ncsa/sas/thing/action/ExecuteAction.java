package edu.uiuc.ncsa.sas.thing.action;

import edu.uiuc.ncsa.sas.SASConstants;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  10:51 AM
 */
public class ExecuteAction extends Action {
    public ExecuteAction() {
        super(SASConstants.ACTION_EXECUTE);
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    String arg;

    @Override
    public JSONObject serialize() {
        JSONObject json = super.serialize();
        String arg64 = arg == null ? "" : arg;
        arg64 = Base64.encodeBase64URLSafeString(arg64.getBytes(StandardCharsets.UTF_8));
        json.put(KEYS_ARGUMENT,arg64);
        return json;
    }

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if (json.containsKey(KEYS_ARGUMENT)) {
            arg = new String(Base64.decodeBase64(json.getString(KEYS_ARGUMENT))) ;
        }
    }
}
