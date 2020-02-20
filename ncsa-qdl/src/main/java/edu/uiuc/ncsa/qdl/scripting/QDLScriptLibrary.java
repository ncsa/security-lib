package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  8:06 AM
 */
public class QDLScriptLibrary implements ScriptProvider {
    public QDLScriptLibrary(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public boolean checkScheme(String name) {
        return name.startsWith(scheme);
    }


    @Override
    public LibraryEntry get(String name) {
        return map.get(name);
    }

    @Override
    public void put(String name, LibraryEntry script) {
        if (!checkScheme(name)) {
            throw new QDLException("Error: The script must have an FQ (fully qualified with the scheme) name to be added.");
        }
        map.put(name, script);
    }

    @Override
    public boolean isScript(String name) {
        if (map.containsKey(name) && map.get(name).getType().equals(Scripts.SCRIPT)) {
            return true;
        }
        return false;
    }

    HashMap<String, LibraryEntry> map = new HashMap<>();
    String scheme;
}
