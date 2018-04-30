package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import net.sf.json.JSONObject;

/**
 * This is a facade for JSON backed POJOs. All components that are configurations should probably
 * extend this. That means that there is a JSON object behind the
 * scenes that is used for all attributes and all the implementation does is front that.
 * <p>Created by Jeff Gaynor<br>
 * on 4/16/18 at  2:12 PM
 */
public abstract class JSONConfig {
    public JSONConfig(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    protected JSONObject jsonObject;

    public boolean hasJSONObject() {
        return jsonObject != null;
    }

    public JSONObject toJSON() {
        if (jsonObject == null) {
            return new JSONObject();

        }
        return jsonObject;
    }

    public void fromJSON(JSONObject json) {
        this.jsonObject = json;
    }

    public abstract String getName();
}
