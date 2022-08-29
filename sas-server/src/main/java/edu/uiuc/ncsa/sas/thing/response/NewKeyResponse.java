package edu.uiuc.ncsa.sas.thing.response;

import edu.uiuc.ncsa.sas.thing.action.Action;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/24/22 at  7:18 AM
 */
public class NewKeyResponse extends Response {
    public NewKeyResponse() {
    }

    public NewKeyResponse(Action action, byte[] key) {
        super(RESPONSE_TYPE_NEW_KEY, action);
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    byte[] key;

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if (json.containsKey(RESPONSE_SYMMETRIC_KEY)) {
            key = Base64.decodeBase64(json.getString(RESPONSE_SYMMETRIC_KEY));
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject json = super.serialize();
        json.put(RESPONSE_SYMMETRIC_KEY, key == null ? "" : Base64.encodeBase64URLSafeString(key));
        return json;
    }
}
