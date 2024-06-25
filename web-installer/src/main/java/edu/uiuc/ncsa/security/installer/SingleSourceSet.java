package edu.uiuc.ncsa.security.installer;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/15/24 at  7:51 AM
 */
public class SingleSourceSet {
    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    String sourceURL;

    public List<DirectoryEntry> getDirectories() {
        return directories;
    }

    public void setDirectories(List<DirectoryEntry> directories) {
        this.directories = directories;
    }

    List<DirectoryEntry> directories;

    public int size(){
        int s = 0;
        if(directories == null) return 0;
        for(DirectoryEntry de : getDirectories()){
           s = s + de.size();
        }
        return s;
    }
}
