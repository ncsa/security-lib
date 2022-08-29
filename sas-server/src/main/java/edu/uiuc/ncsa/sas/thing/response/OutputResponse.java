package edu.uiuc.ncsa.sas.thing.response;

import edu.uiuc.ncsa.sas.thing.action.Action;
import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:28 AM
 */
public class OutputResponse extends Response {
    public OutputResponse() {
    }

    public OutputResponse(Action action, String content) {
        super(RESPONSE_TYPE_OUTPUT, action);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String content;

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if (json.containsKey(RESPONSE_CONTENT)) {
            content = json.getString(RESPONSE_CONTENT);
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject jsonObject = super.serialize();
        jsonObject.put(RESPONSE_CONTENT, content == null ? "" : content); // must always be present
        return jsonObject;
    }
}
