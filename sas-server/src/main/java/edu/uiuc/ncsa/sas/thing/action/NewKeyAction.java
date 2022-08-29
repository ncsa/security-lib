package edu.uiuc.ncsa.sas.thing.action;

import edu.uiuc.ncsa.sas.SASConstants;
import net.sf.json.JSONObject;

/**
 * Request a new symmetric key from the server with the given bit size. The format is
 * <pre>
 *     {"action":"new_key", "arg":int}
 * </pre>
 * <p>Created by Jeff Gaynor<br>
 * on 8/24/22 at  7:14 AM
 */
public class NewKeyAction extends Action{
    public NewKeyAction() {
        super(SASConstants.ACTION_NEW_KEY);
    }

    public NewKeyAction(int size) {
        this();
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    int size = 1024;

    @Override
    public JSONObject serialize() {
        JSONObject json = super.serialize();
        json.put(KEYS_ARGUMENT, size);
        return json;
    }

    @Override
    public void deserialize(JSONObject json) {
        super.deserialize(json);
        if(json.containsKey(KEYS_ARGUMENT)){
            size = json.getInt(KEYS_ARGUMENT);
        }
    }
}
