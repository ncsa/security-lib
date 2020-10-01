package edu.uiuc.ncsa.security.util.scripting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * This contains a set of scripts. Scripts generally have attached properties which allows for selecting
 * which script to run.
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/20 at  12:26 PM
 */
public class ScriptSet<V extends ScriptInterface> implements Iterable<V>{
    public List<V> getScripts() {
        return scripts;
    }

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
            // if stored property a list, check and return first hit.
            if(s.getProperties().containsKey(key)){
                if(s.getProperties().isList(key)){
                    String[] list = s.getProperties().getList(key);
                    for(String x : list){
                        if(x.equals(value)){
                            return s;
                        }
                    }
                }else{
                    //stored property is not a list, check the value directly.
                    if(s.getProperties().getString(key).equals(value)){
                        return s;
                    }
                }
            }
        }
        return null;
    }
    public boolean isEmpty(){
        return scripts.isEmpty();
    }
    public int size(){
        return scripts.size();
    }

    @Override
    public void forEach(Consumer<? super V> action) {

    }

    @Override
    public Spliterator<V> spliterator() {
        return null;
    }

    @Override
    public Iterator<V> iterator() {
        return scripts.iterator();
    }

    @Override
    public String toString() {
        return "ScriptSet{" +
                "scripts=" + scripts +
                '}';
    }
}
