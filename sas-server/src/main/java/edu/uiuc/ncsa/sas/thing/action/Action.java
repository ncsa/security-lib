package edu.uiuc.ncsa.sas.thing.action;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.thing.Thing;
import net.sf.json.JSONObject;

/**
 * Models an actions, such as logon, execute, logoff, etc.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  10:39 AM
 */
public class Action extends Thing implements SASConstants {
    public Action(String type) {
        super(type);
    }

    /**
     * An (optional) unique identifier that the client sends. This allows the client to
     * disambiguate like-named calls and dispatch them however it pleases. <br/><br/>
     * This is passed back unaltered if present.
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    /**
     * An optional state string that the client sends.<br/><br/> It is passed back unaltered.
     * @return
     */
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    String state;

    /**
     * the client may send a comment. This is passed along, and while it might be recorded in the logs,
     * is ignored. This is because JSON does not allow for comments, hence the client can have comments for
     * its own purposes.
     * @return
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    String comment;

    @Override
    public String toString() {
        return "Action{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", comment='" + comment + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    /**
     * Typically this is called by the {@link edu.uiuc.ncsa.sas.webclient.Client} during POST
     * @return
     */
    public JSONObject serialize(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEYS_ACTION, getType());
        jsonObject.put(KEYS_STATE, state == null?"":state);
        jsonObject.put(KEYS_INTERNAL_ID, id == null?"":id);
        jsonObject.put(KEYS_COMMENT, comment == null?"":comment);
        return jsonObject;
    }

    public void deserialize(JSONObject json){
       setType(json.getString(KEYS_ACTION));
       if(json.containsKey(KEYS_STATE)){
           state = json.getString(KEYS_STATE);
       }
       if(json.containsKey(KEYS_INTERNAL_ID)){
           state = json.getString(KEYS_INTERNAL_ID);
       }
       if(json.containsKey(KEYS_COMMENT)){
           comment = json.getString(KEYS_COMMENT);
       }
    }
}
