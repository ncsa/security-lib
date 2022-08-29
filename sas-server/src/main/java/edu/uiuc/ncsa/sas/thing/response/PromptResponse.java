package edu.uiuc.ncsa.sas.thing.response;

import edu.uiuc.ncsa.sas.thing.action.Action;
import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:30 AM
 */
public class PromptResponse extends Response {
    public PromptResponse() {
    }

    public PromptResponse(Action action, String prompt) {
        super(RESPONSE_TYPE_PROMPT, action);
        this.prompt = prompt;
    }

    String prompt = null;

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if (json.containsKey(RESPONSE_PROMPT)) {
            prompt = json.getString(RESPONSE_PROMPT);
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject json = super.serialize();
        json.put(RESPONSE_PROMPT, prompt == null ? "" : prompt);
        return json;
    }
}
