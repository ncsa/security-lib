package edu.uiuc.ncsa.security.util.scripting;

import java.util.*;
import java.util.function.Consumer;

import static edu.uiuc.ncsa.security.util.scripting.ScriptingConstants.*;

/**
 * This contains a set of scripts. Scripts generally have attached properties which allows for selecting
 * which script to run.
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/20 at  12:26 PM
 */
public class ScriptSet<V extends ScriptInterface> implements Iterable<V> {
    public List<V> getScripts() {
        return scripts;
    }

    protected List<V> scripts = new ArrayList();

    public void add(V script) {
        scripts.add(script);
    }

    /**
     * Finds a script for a given key pair or a  null if there is no such script.
     *
     * @param key
     * @param value
     * @return
     */
    public ScriptInterface get(String key, String value) {
        for (ScriptInterface s : scripts) {
            // if stored property a list, check and return first hit.

            if (s.getProperties().containsKey(key)) {
                // Special case so clients can specify all, post_all or pre_all and fold the processing
                // into QDL.
                // CIL-1301
                if (key.equals(SRE_EXEC_PHASE)) {
                    ArrayList<String> list = s.getProperties().getArrayList(key);
                    if(value.equals(ALL_PHASES)){ // if the phase is all, do it everywhere.
                        // match any
                        return s;
                    }
                    if (list.contains(value)) {
                        // match specific phase
                        return s;
                    }
                    if (value.startsWith(PRE_PREFIX) && list.contains(SRE_PRE_ALL)) {
                        return s;
                    }
                    if (value.startsWith(POST_PREFIX) && list.contains(SRE_POST_ALL)) {
                        return s;
                    }
                    return null; // no recognized phases. No such script
                }
                if (s.getProperties().isList(key)) {
                    ArrayList<String> list = s.getProperties().getArrayList(key);
                    if(list.contains(value)){
                        return s;
                    }
/*
                    for (String x : list) {
                        if (x.equals(value)) {
                            return s;
                        }
                    }
*/
                } else {
                    //stored property is not a list, check the value directly.
                    if (s.getProperties().getString(key).equals(value)) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return scripts.isEmpty();
    }

    public int size() {
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
