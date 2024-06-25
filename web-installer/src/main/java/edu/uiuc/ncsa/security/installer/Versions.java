package edu.uiuc.ncsa.security.installer;

import java.util.HashMap;

import static edu.uiuc.ncsa.security.installer.WebInstaller.VERSION_LATEST;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/16/24 at  6:40 AM
 */
public class Versions extends HashMap<String, VersionEntry> {
    public void put(VersionEntry ve){
        super.put(ve.name, ve);
    }

    /**
     * This returns the {@link VersionEntry} that the "latest" name
     * points to.
     * @return
     */
    public VersionEntry getLatestVE(){
        if(containsKey(VERSION_LATEST)){
            String rName =  get(VERSION_LATEST).resource;
            for(String key : keySet()){
                      VersionEntry ve = get(key);
                      if(ve.name.equals(rName)){
                          return ve;
                      }
            }
        }
        return null;

    }
    /**
     * Gets the resource set as the latest version. See comment
     * in {@link #getLatestVersionName()}.
     * @return
     */
    public String getLatestVersionResource(){
        if(containsKey(VERSION_LATEST)){
            return getLatestVE().resource;
        }
        return null;
    }

    /**
     * The name of the configuration that latest points to. So e.g.
     * latest has resource /foo and /foo has name "version 5.2".
     * {@link #getLatestVersionResource()} returns "/foo"
     * and {@link #getLatestVersionName()} returns "version 5.2".
     * @return
     */
    public String getLatestVersionName(){
        VersionEntry ve = getLatestVE();
        if(ve == null) return null;
        return ve.name;
    }

}
