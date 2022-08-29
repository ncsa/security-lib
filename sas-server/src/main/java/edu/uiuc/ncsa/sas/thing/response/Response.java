package edu.uiuc.ncsa.sas.thing.response;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.thing.action.Action;
import net.sf.json.JSONObject;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:29 AM
 */
public class Response implements SASConstants {
    public Response(String responseType) {
        this.responseType = responseType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    String responseType;

    public Response() {
    }

    public Response(String responseType, Action action) {
        this(responseType);
        init(action);
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    String actionType;

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    String state;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    String comment;

    public void init(Action action){
        setId(action.getId());
        setState(getState());
        setComment(getComment());
        setActionType(action.getType());
    }
    public void deserialize(JSONObject json){
        setActionType(json.getString(REQUEST_TYPE)); // must have
         setResponseType(json.getString(RESPONSE_TYPE)); // must have
        if(json.containsKey(KEYS_INTERNAL_ID)){
            setId(json.getString(KEYS_INTERNAL_ID));
        }
        if(json.containsKey(KEYS_STATE)){
            setState(json.getString(KEYS_STATE));
        }
        if(json.containsKey(KEYS_COMMENT)){
            setComment(json.getString(KEYS_COMMENT));
        }
    }
    public JSONObject serialize(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(REQUEST_TYPE, getActionType());
        jsonObject.put(RESPONSE_TYPE, getResponseType());
        if (!isTrivial(getState())) {
            jsonObject.put(KEYS_STATE, getState());
        }
        if (!isTrivial(getId())) {
            jsonObject.put(KEYS_INTERNAL_ID, getId());
        }
        if (!isTrivial(getComment())) {
            jsonObject.put(KEYS_COMMENT, getComment());
        }
        return jsonObject;
    }
}
