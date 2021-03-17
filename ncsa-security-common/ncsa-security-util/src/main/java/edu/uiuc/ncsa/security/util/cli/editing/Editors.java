package edu.uiuc.ncsa.security.util.cli.editing;

import java.util.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/12/21 at  8:09 AM
 */
public class Editors {
    Map<String, EditorEntry> editors = new HashMap<>();

    public void put(EditorEntry editor) {
        editors.put(editor.name, editor);
    }
    public EditorEntry get(String name) {
        return editors.get(name);
    }
    public boolean hasEntry(String name){return editors.containsKey(name);}
    public boolean isEmpty() {
        return editors.isEmpty();
    }
    public void remove(String name){
        editors.remove(name);
    }

    public List<String> listNames() {
        List<String> out = new ArrayList<>();
        TreeSet<String> treeSet = new TreeSet<>();
        treeSet.addAll(editors.keySet()); // sorts them
        out.addAll(treeSet);
        return out;
    }
}
