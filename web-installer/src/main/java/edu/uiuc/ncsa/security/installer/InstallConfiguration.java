package edu.uiuc.ncsa.security.installer;

import java.util.List;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/24 at  11:23 AM
 */
public class InstallConfiguration {
    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    String baseURL;

    public List<DirectoryEntry> getDirectories() {
        return directories;
    }

    public void setDirectories(List<DirectoryEntry> directories) {
        this.directories = directories;
    }

    List<DirectoryEntry> directories;

    public void fromMap(Map map){
        
    }

    @Override
    public String toString() {
        return "InstallConfiguration{" +
                "baseURL='" + baseURL + '\'' +
                ", directories=" + directories +
                '}';
    }
}
