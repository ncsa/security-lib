package edu.uiuc.ncsa.sas.thing.response;

import edu.uiuc.ncsa.sas.thing.action.Action;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  7:22 AM
 */
public class LogonResponse extends Response {
    public LogonResponse() {
    }

    public LogonResponse(Action action, UUID sessionID, byte[] sKey) {
        super(RESPONSE_TYPE_LOGON, action);
        this.sessionID = sessionID;
        this.sKey = sKey;
    }

    public UUID getSessionID() {
        return sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public UUID sessionID;

    public byte[] getsKey() {
        return sKey;
    }

    public void setsKey(byte[] sKey) {
        this.sKey = sKey;
    }

    byte[] sKey;

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if (json.containsKey(RESPONSE_SESSION_ID)) {
            sessionID = UUID.fromString(json.getString(RESPONSE_SESSION_ID));
        }
        if (json.containsKey(RESPONSE_SYMMETRIC_KEY)) {
            sKey = Base64.decodeBase64(json.getString(RESPONSE_SYMMETRIC_KEY));
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject json = super.serialize();
        json.put(RESPONSE_SESSION_ID, sessionID.toString());
        json.put(RESPONSE_SYMMETRIC_KEY, Base64.encodeBase64URLSafeString(sKey));
        return json;
    }
}
