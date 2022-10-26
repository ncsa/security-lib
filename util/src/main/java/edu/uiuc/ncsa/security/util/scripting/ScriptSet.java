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
    public List<ScriptInterface> get(String key, String value) {
        // CIL-1541 -- return ALL the scripts so they may be processed.
        List<ScriptInterface> sss = new ArrayList<>();
        for (ScriptInterface s : scripts) {
            // if stored property a list, check and return first hit.

            if (s.getProperties().containsKey(key)) {
                // Special case so clients can specify all, post_all or pre_all and fold the processing
                // into QDL.
                // CIL-1301
                if (key.equals(SRE_EXEC_PHASE)) {
                    ArrayList<String> list = s.getProperties().getArrayList(key);
                    if (value.equals(ALL_PHASES)
                            || (value.startsWith(PRE_PREFIX) && list.contains(SRE_PRE_ALL))
                            || (value.startsWith(POST_PREFIX) && list.contains(SRE_POST_ALL))
                            || list.contains(value)
                            || s.getProperties().getString(key).equals(value)
                    ) {
                        sss.add(s);
                    }
                }
            }
        }
        return sss;
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
