package edu.uiuc.ncsa.sas.thing.response;

import edu.uiuc.ncsa.sas.thing.action.Action;
import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  7:25 AM
 */
public class LogoffResponse extends Response {
    public LogoffResponse() {
    }

    public LogoffResponse(Action action, String message) {
        super(RESPONSE_TYPE_LOGOFF, action);
        this.message = message;
    }



    // nothing really.
    public String message;

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
            json.put(RESPONSE_MESSAGE, message == null?"":message);
    }

    @Override
    public JSONObject serialize() {
        JSONObject json = super.serialize();
        if(json.containsKey(RESPONSE_MESSAGE)){
            message = json.getString(RESPONSE_MESSAGE);
        }
        return json;
    }
}
