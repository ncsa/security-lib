package edu.uiuc.ncsa.security.util.scripting;

import java.util.ArrayList;
import java.util.List;

/**
 * This contains a set of scripts. Scripts generally have attached properties which allows for selecting
 * which script to run.
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/20 at  12:26 PM
 */
public class ScriptSet<V extends ScriptInterface> {
    protected List<V> scripts = new ArrayList();
    public void add(V script){scripts.add(script);}

    /**
     * Finds a script for a given key pair or a  null if there is no such script.
     * @param key
     * @param value
     * @return
     */
    public ScriptInterface get(String key, String value){
        for(ScriptInterface s : scripts ){
            if(s.getProperties().containsKey(key) && s.getProperties().getString(key).equals(value)){
                return s;
            }
        }
        return null;
    }
}
