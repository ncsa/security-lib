package edu.uiuc.ncsa.qdl.module;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.net.URI;
import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:04 AM
 */
public class ModuleMap extends HashMap<URI, Module> {
    public void put(Module module){
        super.put(module.getNamespace(), module);
    }
    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        for(URI key : keySet()){
            jsonObject.put(key.toString(), get(key).toJSON());
        }
        return jsonObject;
    }

    public void fromJSON(JSONObject jsonObject){
        for(Object key: jsonObject.keySet()){
            JSONArray array = jsonObject.getJSONArray(key.toString());
            Module module = new Module();
            module.fromJSON(array);
            put(module);
        }
    }
}
