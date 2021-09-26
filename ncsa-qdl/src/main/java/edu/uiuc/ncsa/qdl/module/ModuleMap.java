package edu.uiuc.ncsa.qdl.module;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The actual collection of modules, keyed by (unique) uri. These are templates (correspond to
 * classes in Java) vs instances.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:04 AM
 */
public class ModuleMap extends HashMap<URI, Module> {
    public void clearChangeList(){
        changeList = new ArrayList<>();
    }
    // On updates, the change list will track additions or replacements.
    // clear it before updates, read it it after, then clear it again.
    public List<URI> getChangeList() {
        return changeList;
    }

    public void setChangeList(List<URI> changeList) {
        this.changeList = changeList;
    }

    List<URI> changeList = new ArrayList<>();

    @Override
    public Module put(URI key, Module value) {
        changeList.add(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends URI, ? extends Module> m) {
        for(URI key : m.keySet()){
            getChangeList().add(key);
        }
        super.putAll(m);
    }
}
