package edu.uiuc.ncsa.qdl.config;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/12/21 at  8:09 AM
 */
public class QDLEditors {
    Map<String, QDLEditor> editors = new HashMap<>();

    public void put(QDLEditor editor) {
        editors.put(editor.name, editor);
    }

    public QDLEditor get(String name) {
        return editors.get(name);
    }

    public boolean isEmpty() {
        return editors.isEmpty();
    }
    public void remove(String name){
        editors.remove(name);
    }
}
