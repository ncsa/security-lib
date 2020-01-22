package edu.uiuc.ncsa.qdl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  12:09 PM
 */
public class StemVariable extends HashMap<String, Object> {
    // Convenience methods.
      public Long getLong(String key){
          return (Long) get(key);
      }
      public String getString(String key){
          return (String) get(key);
      }
      public Boolean getBoolean(String key){
          return (Boolean)get(key);
      }

    /**
     * If this is set, then any get with no key will return this value. Since
     * the basic unit of QDL is the stem, this gives us a way of basically turning
     * a scalar in to a stem without having to do complicated size and key matching.
     * <br/><br/>
     * <b>Note</b> that {@link #containsKey(Object)} still works as usual, so you can ask
     * if a key exists.
     * @return
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    Object defaultValue;
    public boolean hasDefaultValue(){
        return defaultValue != null;
    }

    @Override
    public Object get(Object key) {
        if(!containsKey(key) && defaultValue != null){
            return defaultValue;
        }
        return super.get(key);
    }

    /**
     * Initialize a stem variable with all the same value. Very useful when floating a single value
     * to a stem.
     * @param template
     * @param value
     * @return
     */
    public StemVariable fillStem(StemVariable template, Object value){
        clear();
        for(String key: template.keySet()){
            put(key, value);
        }
        return this;
    }

    /**
     * returns the set of keys common to this stem and its argument. The list may be empty.
     * @param x
     * @return
     */
    public List<String> getCommonKeys(StemVariable x){
        ArrayList<String> commonKeys = new ArrayList<>();
        for(String key : x.keySet()){
            if(containsKey(key)){
                commonKeys.add(key);
            }
        }
        return commonKeys;
    }

    /**
     * Make a shallow copy of this stem variable.
     * @return
     */
    @Override
    public Object clone() {
        StemVariable output = new StemVariable();
        for(String key : keySet()){
            output.put(key, get(key));
        }
        return output;
    }
}
