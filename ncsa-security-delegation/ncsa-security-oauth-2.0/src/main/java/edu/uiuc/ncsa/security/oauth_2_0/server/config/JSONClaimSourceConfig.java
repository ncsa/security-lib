package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * All components that are configurations should probably
 * extend this. That means that there is a JSON object behind the
 * scenes that is used for all attributes and all the implementation does is front that.
 * <p>Created by Jeff Gaynor<br>
 * on 4/16/18 at  2:12 PM
 */
public abstract class JSONClaimSourceConfig {
    public JSONClaimSourceConfig(JSONObject jsonObject) {
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


    public abstract JSONArray getPostProcessing();

    public abstract void setPostProcessing(JSONArray postProcessing);

    /**
     * The <b>raw json</b> for the pre-processing directives. This has to be done this way since the directives
     * rely on being constructed with the claims at runtime (e.g. for replacement templates).
     * @return
     */
    public abstract JSONArray getPreProcessing();

    public abstract void setPreProcessing(JSONArray preProcessing);
}
