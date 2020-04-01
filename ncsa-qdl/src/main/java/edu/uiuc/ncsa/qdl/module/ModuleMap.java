package edu.uiuc.ncsa.qdl.module;

import java.net.URI;
import java.util.HashMap;

/**
 * The actual collection of modules, keyed by (unique) uri. These are templates (correspond to
 * classes in Java) vs instances.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:04 AM
 */
public class ModuleMap extends HashMap<URI, Module> {
  /*  public void put(Module module){
        super.put(module.getNamespace(), module);
    }*/
  /*  public JSONObject toJSON(){
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
    }*/
}
