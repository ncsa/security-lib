package edu.uiuc.ncsa.security.installer;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Does the grunt work of importing from a configuration file.
 * <p>Created by Jeff Gaynor<br>
 * on 6/14/24 at  7:25 AM
 */
public class InstallConfigurationImporter {
    public InstallConfigurationImporter(List setup) {
        initSetup(setup);
    }

    public static String SOURCE_URL = "sourceURL";
    public static String DIRECTORIES = "directories";
    public static String FILES = "files";
    public static String IGNORED_FILES = "ignore";
    public static String SOURCE_FILE = "source";
    public static String TARGET_FILE = "target";
    public static String TARGET_DIRECTORY = "target_dir";
    public static String ATTR_PREPROCESS = "preprocess";
    public static String ATTR_EXECUTABLE = "exec";
    public static String ATTR_UPDATEABLE = "update";
    public static String ATTR_TYPE = "type";
    public static String TYPE_SETUP = "setup";
    public static String TYPE_FILE_SET = "file_set";
    public static String TYPE_ZIP = "zip";

    protected void initSetup(List setup) {
        Map setupMap = (Map) setup.get(0);
        if (setupMap.containsKey(ATTR_TYPE) && setupMap.get(ATTR_TYPE).equals(TYPE_SETUP)) {
            doSetup(setupMap);
        } else {
            throw new IllegalStateException("no configuration found in file");
        }
    }

    public void initDirs(List dirs) {
        List<SingleSourceSet> singleSourceSets = new ArrayList<>();
        for (int i = 0; i < dirs.size(); i++) {
            Object object = dirs.get(i);
            if (object instanceof Map) {
                Map map = (Map) object;
                if (map.containsKey(ATTR_TYPE)) {
                    singleSourceSets.add(doSingleDirectoryCase(map, (String) map.get(ATTR_TYPE)));
                }
            }
        }
        getInstallConfiguration().setSourceSets(singleSourceSets);
    }


    public static final String APP_NAME = "app_name";
    public static final String HELP_SET = "help";
    public static final String VERSION_SET = "versions";
    public static final String ATTR_FAIL_ON_ERROR = "fail_on_error";
    public static final String ATTR_CLEANUP_ON_FAIL = "cleanup_on_fail";
    public static final String ATTR_VERSION_FILE = "file";
    public static final String ATTR_VERSION_NAME = "name";
    public static final String ATTR_VERSION_DESCRIPTION = "description";
    public static final String ATTR_VERSION_NOTES = "notes";
    public static final String ATTR_HELP_ARGS = "args";
    public static final String ATTR_HELP_DEFAULT = "installer";
    public static final String ATTR_HELP_EXAMPLES = "examples";
    public static final String ATTR_HELP_SUCCESS = "success";

    protected void doSetup(Map map) {
        if (map.containsKey(VERSION_SET)) {
            Versions versions = new Versions();
            List versionSet = (List) map.get(VERSION_SET);
            for (Object v : versionSet) {
                if (v instanceof Map) {
                    Map vv = (Map) v;
                    String file = getString(vv, ATTR_VERSION_FILE);
                    if (file == null) {
                        throw new IllegalStateException("missing " + ATTR_VERSION_FILE + " in a version");
                    }
                    String name = getString(vv, ATTR_VERSION_NAME);
                    name = (name == null) ? name = file : name;
                    String description = getString(vv, ATTR_VERSION_DESCRIPTION);
                    description = description == null ? file : description;
                    String notes = getString(vv, ATTR_VERSION_NOTES);
                    versions.put(new VersionEntry(name, file, description, notes));
                }
            }
            getInstallConfiguration().setVersions(versions);
        }
        if (map.containsKey(APP_NAME)) {
            getInstallConfiguration().setAppName(getString(map, APP_NAME));
        }
        if (map.containsKey(ATTR_FAIL_ON_ERROR)) {
            getInstallConfiguration().setFailOnError(getBoolean(map, ATTR_FAIL_ON_ERROR));
        }
        if (map.containsKey(ATTR_CLEANUP_ON_FAIL)) {
            getInstallConfiguration().setCleanupOnFail(getBoolean(map, ATTR_CLEANUP_ON_FAIL));
        }
        if (map.containsKey(HELP_SET)) {
            Map helpMap = (Map) map.get(HELP_SET);

            if (helpMap.containsKey(ATTR_HELP_ARGS)) {
                try {
                    getInstallConfiguration().setInstallerHelp(loadHelpFile(helpMap, ATTR_HELP_DEFAULT));
                    getInstallConfiguration().setArgHelp(loadHelpFile(helpMap, ATTR_HELP_ARGS));
                    getInstallConfiguration().setExampleHelp(loadHelpFile(helpMap, ATTR_HELP_EXAMPLES));
                    getInstallConfiguration().setSuccessMessage(loadHelpFile(helpMap, ATTR_HELP_SUCCESS));
                } catch (IOException ioException) {

                }
                String x = getString(helpMap, ATTR_HELP_ARGS);
                InputStream textStream = getClass().getResourceAsStream(x);
                if (textStream != null) {
                }
            }
        }
    }

    protected String loadHelpFile(Map helpMap, String keyName) throws IOException {
        String out = null;
        if (helpMap.containsKey(keyName)) {
            String x = getString(helpMap, keyName);
            InputStream textStream = getClass().getResourceAsStream(x);
            if (textStream != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                WebInstaller.copyStream(textStream, baos);
                baos.flush();
                textStream.close();
                out = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                baos.close();
            }
        }
        return out;
    }
/*
- app_name: NCSA sec-lib
  help: {app: /app_help.txt, installer: /help.txt, success: /success.txt}
  versions:
  - {file: /test-cfg1.yaml, name: v1, description: single script set}
  - {file: /test-cfg2.yaml, name: v2, description: two script sets}
  - {file: /v1, name: latest, description: latest release}
  cleanup_on_fail: false
  fail_on_error: false
  type: setup
 */

    /**
     * the YAML file may have a single directory or a list of them. This
     * processes the case that the top-level element is just a directory
     */
    protected SingleSourceSet doSingleDirectoryCase(Map map, String type) {
        SingleSourceSet singleSourceSet = new SingleSourceSet();
        if (map.containsKey(SOURCE_URL)) {
            singleSourceSet.setSourceURL((String) map.get(SOURCE_URL));
        }
        if (map.containsKey(DIRECTORIES)) {
            processDirectories(singleSourceSet, getList(map, DIRECTORIES), type);
        }
        return singleSourceSet;
    }

    private SingleSourceSet doJarDirectory(Map map) {
        SingleSourceSet sss = new SingleSourceSet();
        return sss;
    }

    /**
     * The directories entry contains a list of directories
     *
     * @param dirList
     */
    protected void processDirectories(SingleSourceSet singleSourceSet,
                                      List dirList,
                                      String type) {  // list of maps
        List<DirectoryEntry> directories = new ArrayList<>(dirList.size());
        for (Object object : dirList) {
            if (!(object instanceof Map)) {
                continue;
            }

            Map currentMap = (Map) object;
            DirectoryEntry directoryEntry = new DirectoryEntry();
            if (currentMap.containsKey(FILES)) {
                Object x = currentMap.get(FILES);
                if (x instanceof List) {
                    List<FileEntryInterface> files = null;
                    if (type.equals(TYPE_FILE_SET)) {

                        files = processFileEntries((List) x);
                    }
                    if (type.equals(TYPE_ZIP)) {
                        files = processJarEntries((List) x);
                    }

                    if (files == null) {
                        directoryEntry.setFiles(new ArrayList<>());

                    } else {
                        directoryEntry.setFiles(files);
                    }
                }
            }
            if (currentMap.containsKey(IGNORED_FILES)) {
                Object x = currentMap.get(IGNORED_FILES);
                if (x instanceof List) {
                    List<String> ignoredFiles = new ArrayList<>();
                    List list = (List)x;
                    for(Object obj : list){
                        if(obj instanceof String){
                            ignoredFiles.add((String)obj);
                        }
                    }
                    directoryEntry.setIgnoredFiles(ignoredFiles);
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
        singleSourceSet.setDirectories(directories);
    }

    protected List<FileEntryInterface> processJarEntries(List list) {
        List<FileEntryInterface> fileEntries = new ArrayList<>(list.size());
        for (Object object : list) {
            FileEntryInterface fe;
            if (object instanceof String) {
                fe = new ZipArchive((String) object);
                fileEntries.add(fe);
            }
        }
        return fileEntries;
    }

    protected List<FileEntryInterface> processFileEntries(List list) {
        List<FileEntryInterface> fileEntries = new ArrayList<>(list.size());
        for (Object object : list) {
            FileEntryInterface fileEntry = null;
            if (object instanceof String) {
                fileEntry = new FileEntry((String) object);
            } else {
                if ((object instanceof Map)) {
                    Map currentMap = (Map) object;
                    String source = (String) currentMap.get(SOURCE_FILE);
                    String target = (String) currentMap.get(TARGET_FILE);
                    if (target == null) target = source; // same name

                    fileEntry = new FileEntry(source,
                            target,
                            getBoolean(currentMap, ATTR_EXECUTABLE), // can be null
                            getBoolean(currentMap, ATTR_PREPROCESS), //   "   "
                            getBoolean(currentMap, ATTR_UPDATEABLE)); // "    "
                }
            }
            if (fileEntry != null) {
                fileEntries.add(fileEntry);
            }

        }
        return fileEntries;
    }

    /**
     * This grabs the boolean value from a map. Note that this is more complex than it
     * would seem. {@link Boolean#parseBoolean(String)} always returns, and if the argument
     * is gibberish, it just returns false. We need the values to be true, false or null (showing unset).
     *
     * @param map
     * @param key
     * @return
     */
    protected Boolean getBoolean(Map map, String key) {
        if (map.containsKey(key)) {
            Object obj = map.get(key);
            if (obj instanceof Boolean) {
                return (Boolean) obj;
            }
            if (obj instanceof String) {
                String x = (String) obj;
                if (x.equals("true")) return Boolean.TRUE;
                if (x.equals("false")) return Boolean.FALSE;
            }
        }
        return null;
    }


    protected String getString(Map map, String key) {
        return (String) map.get(key);
    }

    /**
     * return the entry of the current configuration map cast to a map
     *
     * @param key
     * @return
     */
    protected Map getMap(Map map, String key) {
        return (Map) map.get(key);
    }

    protected List getList(Map map, String key) {
        return (List) map.get(key);
    }


    public InstallConfiguration getInstallConfiguration() {
        if (installConfiguration == null) {
            installConfiguration = new InstallConfiguration();
        }
        return installConfiguration;
    }

    InstallConfiguration installConfiguration;

    /**
     * Just for testing. Only loads everything into an instance of this object.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            FileInputStream dirIS = new FileInputStream("/home/ncsa/dev/ncsa-git/security-lib/web-installer/src/main/resources/base/test-cfg2.yaml");
            FileInputStream setupIS = new FileInputStream("/home/ncsa/dev/ncsa-git/security-lib/web-installer/src/main/resources/setup.yaml");
            Yaml yaml = new Yaml(new SafeConstructor());
            Object dirs = yaml.load(dirIS);
            yaml = new Yaml(new SafeConstructor());
            Object setup = yaml.load(setupIS);
            InstallConfigurationImporter icm = new InstallConfigurationImporter((List) setup);
            icm.initDirs((List) dirs);
            System.out.println(icm);
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