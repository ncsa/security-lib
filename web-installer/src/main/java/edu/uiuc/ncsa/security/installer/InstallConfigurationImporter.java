package edu.uiuc.ncsa.security.installer;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Does the grunt work of importing from a configuration file.
 * <p>Created by Jeff Gaynor<br>
 * on 6/14/24 at  7:25 AM
 */
public class InstallConfigurationImporter {
    public InstallConfigurationImporter(Map map) {
        this.map = map;
    }

    public static String SOURCE_URL = "sourceURL";
    public static String DIRECTORIES = "directories";
    public static String FILES = "files";
    public static String SOURCE_FILE = "source";
    public static String TARGET_FILE = "target";
    public static String TARGET_DIRECTORY = "target_dir";
    public static String ATTR_PREPROCESS = "preprocess";
    public static String ATTR_EXECUTABLE = "exec";
    public static String ATTR_UPDATEABLE = "update";

    public InstallConfiguration fromMap() {
        installConfiguration = new InstallConfiguration();
        if (map.containsKey(SOURCE_URL)) {
            installConfiguration.setBaseURL(getString(SOURCE_URL));
        }
        if (getMap().containsKey(DIRECTORIES)) {
            processDirectories(getList(DIRECTORIES));
        }
        return installConfiguration;
    }

    /**
     * The directories entry contains a list of directories
     *
     * @param dirList
     */
    protected void processDirectories(List dirList) {  // list of maps
        List<DirectoryEntry> directories = new ArrayList<>(dirList.size());
        for (Object object : dirList) {
            if (!(object instanceof Map)) {
                continue;
            }

            Map currentMap = (Map) object;
            DirectoryEntry directoryEntry = new DirectoryEntry();
            if (currentMap.containsKey(FILES)) {
                 Object x = currentMap.get(FILES);
                 if(x instanceof List){
                     List<FileEntry> files = processFileEntries((List)x);
                     directoryEntry.setFiles(files);
                 }
            }
            directoryEntry.setTargetDir((String) currentMap.get(TARGET_DIRECTORY));
            if (currentMap.containsKey(ATTR_EXECUTABLE)) {
                directoryEntry.setExecutable((Boolean) currentMap.get(ATTR_EXECUTABLE));
            }
            if (currentMap.containsKey(ATTR_UPDATEABLE)) {
                directoryEntry.setExecutable((Boolean) currentMap.get(ATTR_UPDATEABLE));
            }
            if (currentMap.containsKey(ATTR_PREPROCESS)) {
                directoryEntry.setExecutable((Boolean) currentMap.get(ATTR_PREPROCESS));
            }


            directories.add(directoryEntry);
        }
        installConfiguration.setDirectories(directories);
    }

    protected List<FileEntry> processFileEntries(List list) {
        List<FileEntry> fileEntries = new ArrayList<>(list.size());
        for (Object object : list) {
            FileEntry fileEntry = null;
            if (object instanceof String) {
                   fileEntry = new FileEntry((String) object);
            } else {
                if ((object instanceof Map)) {
                    Map currentMap = (Map) object;
                    String source = (String)currentMap.get(SOURCE_FILE);
                    String target = (String)currentMap.get(SOURCE_FILE);
                    if(target == null) target =source; // same name

                    fileEntry = new FileEntry(source,
                            target,
                            getBoolean(currentMap, ATTR_EXECUTABLE), // can be null
                            getBoolean(currentMap, ATTR_PREPROCESS), //   "   "
                            getBoolean(currentMap, ATTR_UPDATEABLE)); // "    "
                }
            }
             if(fileEntry != null){
                 fileEntries.add(fileEntry);
             }

        }
        return fileEntries;
    }

    /**
     * This grabs the boolean value from the map. Note that this is more complex than it
     * would seem. {@link Boolean#parseBoolean(String)} always returns, and if the argument
     * is gibberish, it just returns false. We need the values to be true, false or null (showing unset).
     * @param map
     * @param key
     * @return
     */
    protected Boolean getBoolean(Map map, String key){
        if(map.containsKey(key)){
              Object obj = map.get(key);
              if(obj instanceof Boolean){
                  return (Boolean) obj;
              }
              if(obj instanceof String){
                  String x = (String)obj;
                  if(x.equals("true")) return Boolean.TRUE;
                  if(x.equals("false")) return Boolean.FALSE;
              }
        }
        return null;
    }
    public Map getMap() {
        return map;
    }

    protected String getString(String key) {
        return (String) getMap().get(key);
    }

    /**
     * return the entry of the current configuration map cast to a map
     *
     * @param key
     * @return
     */
    protected Map getMap(String key) {
        return (Map) getMap().get(key);
    }

    protected List getList(String key) {
        return (List) getMap().get(key);
    }

    Map map;

    public InstallConfiguration getInstallConfiguration() {
        if (installConfiguration == null) {
            fromMap();
        }
        return installConfiguration;
    }

    InstallConfiguration installConfiguration;

    public static void main(String[] args) {
        try {
            FileInputStream fileInputStream = new FileInputStream("/home/ncsa/dev/ncsa-git/security-lib/web-installer/src/main/resources/test.yaml");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

/*
sourceURL: https://github.com/ncsa/oa4mp/releases/download/v5.5/
directories:
- files:
  - {source: oa4mp-derby.sql, exec: false, target: derby.sql}
  - mysql.sql
  - mariadb.sql
  target_dir: /etc
- preprocess: true
  files:
  - {source: clc}
  - {source: cli}
  - {source: jwt}
  update: false
  target_dir: /bin
  exec: true
 */