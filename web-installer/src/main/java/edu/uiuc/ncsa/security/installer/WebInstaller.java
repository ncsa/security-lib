package edu.uiuc.ncsa.security.installer;

import edu.uiuc.ncsa.security.core.util.Pacer;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This installs from a GitHub release. The main feature is a YAML file
 * that tells where the files go. This is packaged as a complete application
 * with minimal dependencies.
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/24 at  7:38 AM
 */
public class WebInstaller {
    // This is boiler-plated from the QDL HTTP module.
    static protected final String UPDATE_OPTION = "update";
    static protected final String UPGRADE_OPTION = "upgrade"; // synonym for update
    static protected final String HELP_FLAG = "--help";
    static protected final String NO_PACER_FLAG = "-noPace";
    static protected final String HELP_OPTION = "help";
    static protected final String DIR_ARG = "-dir";
    static protected final String LOG_ARG = "-log";
    static protected final String DEBUG_FLAG = "-v";
    static protected final String INSTALL_OPTION = "install";
    static protected final String NOTES_OPTION = "notes";
    static protected final String LIST_OPTION = "list";
    static protected final String REMOVE_OPTION = "remove";
    static protected final String VERSIONS_OPTION = "versions";
    static protected final String OA4MP_FLAG = "-oa4mp";
    static protected final String ALL_FLAG = "-all";
    static protected final String VERSION_FLAG = "-version";
    static protected final String VERSION_LATEST = "latest";
    static public final Boolean USE_TEMPLATES_DEFAULT = false;
    static public final Boolean EXECUTABLE_DEFAULT = false;
    static public final Boolean UPDATEABLE_DEFAULT = true;

    static List<String> allOps = Arrays.asList(UPDATE_OPTION,
            UPGRADE_OPTION,
            REMOVE_OPTION,
            NOTES_OPTION,
            HELP_OPTION,
            INSTALL_OPTION,
            LIST_OPTION,
            VERSIONS_OPTION);

    public String getAppName() {
        return getInstallConfiguration().getAppName();
    }


    protected void showHelp() {
        try {
            if (getInstallConfiguration().getInstallerHelp() != null) {
                // let them override it themselves
                say(getInstallConfiguration().getInstallerHelp());
                if (getInstallConfiguration().getExampleHelp() != null) {
                    say(getInstallConfiguration().getExampleHelp());
                }

                return;
            }

            say("===============================================================");
            say("java -jar installer.jar install operation arguments* flags*");
            say("===============================================================");
            say("This will install " + getAppName() + " to your system. Options are:");
            say("(none) = same as help");
            say(HELP_OPTION + " = show help and exit. Note you can also use the flag " + HELP_FLAG);
            say(INSTALL_OPTION + " = install");
            say(LIST_OPTION + " = list all the files in the distribution. Nothing is done.");
            say(NOTES_OPTION + " = show the release notes (if any) for a version. If you do not use the " + VERSION_FLAG + ", ");
            say("          you will get the notes  for the latest version.");
            say(REMOVE_OPTION + " = remove");
            say(UPDATE_OPTION + " | " + UPGRADE_OPTION + " = upgrade/update existing distribution");
            say(VERSIONS_OPTION + " = list all the versions available for install. Nothing is done.");
            say("--------------");
            say("arguments are:");
            say(DIR_ARG + " root = install to the given directory. If omitted, you will be prompted.");
            say(LOG_ARG + " file = file for logging. It will be overwritten as needed.");
            say(VERSION_FLAG + " = Specify a version for " + getAppName());
            printMoreArgHelp();
            say("--------------");
            say("Flags are:");
            say(DEBUG_FLAG + " = verbose mode -- print all messages from the installer as it runs. This is quite chatty.");
            say(HELP_FLAG + " = this help message");
            say(NO_PACER_FLAG + "  = disable the pacer (status bar). Note " + DEBUG_FLAG + " implies no pacer");
            say("");
            say("E.g.");
            say("A fresh install, specifying the machine and port. This assumes OA4MP_HOME has been set.");
            say("The port is set to -1 meaning that no port will be specified for the endpoints. This is used, ");
            say("e.g., if this is behind another server (like Apache) that forwards requests.");

            printMoreExamplesHelp();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * Printed as part of the flags section in the default help. If you have custom flags, put
     * help for them here.
     */
    protected void printMoreArgHelp() throws IOException {
        if (getInstallConfiguration().getMoreArgHelp() != null) {
            say(getInstallConfiguration().getMoreArgHelp());
        }
    }

    /**
     * Printed at the very end of help. This is where you can put spscific examples of
     * help for your application.
     */

    protected void printMoreExamplesHelp() throws IOException {
        if (getInstallConfiguration().getExampleHelp() != null) {
            say(getInstallConfiguration().getExampleHelp());
        }
    }

    long totalDownloaded = 0L;

    protected Long download(URL url, File targetFile) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        DataInputStream dis = new DataInputStream(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(targetFile);
        long totalBytes = copyStream(dis, fos);
        dis.close();
        fos.flush();
        fos.close();
        totalDownloaded = totalDownloaded + totalBytes;
        return totalBytes;
    }

    /**
     * Copies one stream to another. Does <b>not</b> close or flush! This returns the
     * total number of bytes copied.
     *
     * @param inputStream
     * @param outputStream
     * @return
     * @throws IOException
     */
    public static long copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        long totalBytes = 0;
        byte[] buffer = new byte[1024];
        int offset = 0;
        int bytes;
        while (0 < (bytes = inputStream.read(buffer, offset, buffer.length))) {
            outputStream.write(buffer, 0, bytes);
            totalBytes = totalBytes + bytes;
        }
        return totalBytes;
    }

    protected long processZipFile(URL sourceURL, DirectoryEntry de, boolean isUpdate) throws IOException {
        long totalSize = 0L;
        HttpURLConnection connection = (HttpURLConnection) sourceURL.openConnection();
        DataInputStream dis = new DataInputStream(connection.getInputStream());
        String targetName = getRoot() + de.getTargetDir();
        targetName = targetName.endsWith(File.separator) ? targetName : (targetName + File.separator);
        ZipInputStream stream = new ZipInputStream(dis);
        List<String> ignoredDirectores = de.getIgnoredDirectories();
        byte[] buffer = new byte[2048];
        try {
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                String entryFileName = entry.getName();
                if (isUpdate && de.getFilePermissions().containsKey(entryFileName)) {
                    continue;
                }
                boolean skip = false;
                if (!ignoredDirectores.isEmpty()) {
                    for (String d : ignoredDirectores) {
                        if (("/" + entryFileName).startsWith(d)) {
                            skip = true;
                            break;
                        }
                    }
                }
                if (skip) continue;
                if (de.hasExcludedFiles()) {
                    if (de.getIgnoredFiles().contains(entryFileName)) {
                        continue;
                    }
                }
                File outFile = new File(targetName + entry.getName());
                if (entryFileName.endsWith("/")) {
                    outFile.mkdirs();
                    continue;
                }
                if(entry.isDirectory()){
                    outFile.getParentFile().mkdirs();
                    outFile.setLastModified(entry.getLastModifiedTime().toMillis());
                    continue;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    int len = 0;
                    while ((len = stream.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                } catch (Throwable t) {
                    if (getInstallConfiguration().isFailOnError()) {
                        throw t; // end of story.
                    }
                    if (isDebugOn()) {
                        t.printStackTrace();
                    }
                } finally {
                    // we must always close the output file
                    if (baos != null) baos.close();
                }
                String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                boolean setExec = false;
                if (de.hasFilePermssions() && de.getFilePermissions().containsKey(entryFileName)) {
                    FileEntry fei = de.getFilePermissions().get(entryFileName);
                    if (fei.isUseTemplate()) {
                        content = doReplace(content);
                    }
                    setExec = fei.isExecutable();
                }
                Files.writeString(outFile.toPath(), content);
                if(setExec){
                    outFile.setExecutable(true);
                }
                outFile.setLastModified(entry.getLastModifiedTime().toMillis());
                totalSize = totalSize + outFile.length();

            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // we must always close the zip file.
            stream.close();
        }
        return totalSize;
    }

    /**
     * read the configuration file as an input stream.  This also creates and initializes
     * the {@link InstallConfiguration} for this installation. At the end of this method,
     * you should be able to start processing.
     *
     * @param cfgStream
     * @return
     * @throws IOException
     */
    protected InstallConfigurationImporter readConfig(InputStream cfgStream) throws IOException {
        if (installConfiguration != null) {
            return null;
        }
        List setup = ingestYaml(cfgStream);
        InstallConfigurationImporter importer;
        importer = new InstallConfigurationImporter(setup);
        installConfiguration = importer.getInstallConfiguration();
        return importer;
    }

    // list -dir /tmp/oa4mp-test -v -version v2

    /**
     * The inputStream contains YAML. This normalizes it so the result is
     * a list with entries of maps.
     *
     * @param is
     * @return
     * @throws IOException
     */
    protected List ingestYaml(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(is, baos);
        String inString = baos.toString(StandardCharsets.UTF_8);
        is.close();
        baos.close();

        Yaml yaml = new Yaml(new SafeConstructor());
        Object obj = yaml.load(inString);
        List outList;
        if (obj instanceof Map) {
            outList = new ArrayList();
            outList.add(obj);
        } else {
            if (obj instanceof List) {
                outList = (List) obj;
            } else {
                throw new IllegalStateException("unknown configuration file structure.");
            }
        }
        return outList;
    }

    /**
     * Prints a message (e.g. post install instructions, post update). This takes
     * the path as a resource. Note that this does replacements on the contents of the
     * message unless skipTemplates is false
     * @param resourcePath
     * @param skipTemplates
     * @return
     * @throws IOException
     */
    protected String getMessage(String resourcePath, boolean skipTemplates) throws IOException {
        InputStream setupStream = getClass().getResourceAsStream(resourcePath);
        if (setupStream == null) {
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(setupStream, baos);
        String inString = baos.toString(StandardCharsets.UTF_8);
        setupStream.close();
        baos.close();
        if(!skipTemplates) {
            inString = doReplace(inString);
        }
        return inString;
    }

    /**
     * Prints a message from a resource, doing all template replacements on the content.
     * @param resourcePath
     * @return
     * @throws IOException
     */
    protected String getMessage(String resourcePath) throws IOException {
        return getMessage(resourcePath, false);


    }

        public InstallConfiguration getInstallConfiguration() {
        return installConfiguration;
    }

    InstallConfiguration installConfiguration;

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }

    Map<String, String> templates;

    /**
     * You can override this. Note that this only creates the {@link ArgMap} if it does not exist.
     *
     * @param args
     * @throws Throwable
     */
    protected boolean init(String[] args) throws Throwable {
        boolean doProcessing = true;
        if (argMap == null) {
            argMap = new ArgMap(args);
            setupArgMap(args);
        }

        if (getArgMap().hasRootDir()) {
            setRoot(getArgMap().getRootDir());
        }
        if (getArgMap().isPacerOn()) {
            pacer = new Pacer(20);
        }
        if (getArgMap().isLoggingEnabled()) {
            File log = getArgMap().logFile();
            boolean didDelete = false;
            if (log.exists() && log.isFile()) {
                didDelete = log.delete();
            }
            logger = new FileWriter(getArgMap().logFile());
            if (didDelete) {
                logger.write("removed existing log file \"" + log.getAbsolutePath() + "\n");
            }
        }
        InputStream setupStream = getSetupIS();

        InstallConfigurationImporter importer = readConfig(setupStream);
        setDebugOn(getArgMap().is(DEBUG_FLAG));
        if (getArgMap().isShowHelp()) {
            //   showHelp();
            return false;
        }
        if (getArgMap().isVersions()) {
            // only a query for versions of a list of files.
            return true;
        }

        String dirList = getVersionFileName(getArgMap().getVersion());
        if (getArgMap().getVersion() != null) {
            getInstallConfiguration().setActualVersion(getArgMap().getVersion());
        } else {
            getInstallConfiguration().setActualVersion(VERSION_LATEST);
        }

        if (getArgMap().isShowReleaseNotes()) {
            return true;
        }
        if (dirList == null) {
            // so this is not registered as a version
            throw new FileNotFoundException("no such version \"" + getArgMap().getVersion() + "\"");
        }
        InputStream textStream = getClass().getResourceAsStream(dirList);
        if (textStream == null) {
            // It is registered as a version, but the corresponding file is not found.
            say(dirList + " not found!");
            throw new FileNotFoundException(dirList + " not found!");
        }
        importer.initDirs(ingestYaml(textStream));
        //   setSourceURL(installConfiguration.getBaseURL());
        setDebugOn(getArgMap().is(DEBUG_FLAG));
        // don't run the template setup since that may require the root dir. Allow the user
        // as minimal a set of arguments as possible to show the file list.
        if (!getArgMap().isList()) {
            setupTemplates();
        }
        return doProcessing;
    }

    protected void showReleaseNotes(String versionName) throws IOException {
        VersionEntry ve;
        if (versionName.equals(VERSION_LATEST)) {
            ve = getInstallConfiguration().getVersions().getLatestVE();
        } else {
            ve = getInstallConfiguration().getVersions().get(versionName);
        }
        if (ve.notes == null) {
            say("(no release notes)");
            return;
        }
        InputStream stream = getClass().getResourceAsStream(ve.notes);
        if (stream == null) {
            throw new FileNotFoundException(stream + " not found!");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(stream, baos);
        String notes = new String(baos.toString(StandardCharsets.UTF_8));
        if (StringUtils.isTrivial(notes)) {
            say("(no release notes)");
        } else {
            say(notes);
        }

    }

    /**
     * If you need to add more arguments to the {@link ArgMap}, rather than overriding that
     * class, just set them here. This is called immediately after it is created, but before
     * anything else accesses the arg map.
     *
     * @param args
     */
    protected void setupArgMap(String[] args) {

    }

    /**
     * Override this as needed to get the latest version of your configuration file
     * as a resource.
     *
     * @return
     */
    public String getLastestConfigFileName() {
        return getInstallConfiguration().getVersions().getLatestVersionResource();
    }

    public ArgMap getArgMap() {
        return argMap;
    }

    ArgMap argMap = null;

    public static void main(String[] args) {
        WebInstaller webInstaller = new WebInstaller();
        try {
            System.out.println(webInstaller.getID(1));
            System.out.println(webInstaller.getID(2));
            System.out.println(webInstaller.getID(3));
/*
            if (webInstaller.init(args)) {
                webInstaller.process();
                webInstaller.shutdown();
            } else {
                webInstaller.showHelp();
            }
*/
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            if (webInstaller.isDebugOn()) {
                t.printStackTrace();
            }
        }
    }

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    File root;


    protected String doReplace(String currentLine) {
        for (String key : templates.keySet()) {
            if (currentLine.contains(key)) {
                currentLine = currentLine.replace(key, templates.get(key));
            }
        }
        return currentLine;
    }

    private void processTemplates(File f) throws IOException {
        trace("  processing templates for " + f.getAbsolutePath());
        List<String> lines = Files.readAllLines(f.toPath());
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, doReplace(lines.get(i)));
        }
        Files.write(f.toPath(), lines, Charset.defaultCharset());
    }

    public Writer getLogger() {
        return logger;
    }

    Writer logger;

    protected void trace(String message) throws IOException {
        if (getArgMap().isLoggingEnabled()) {
            logger.write(message + "\n");
        }
        if (isDebugOn()) {
            say(message);
        }
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    boolean debugOn = false;

    protected void say(String x) throws IOException {
        if (getArgMap().isLoggingEnabled() && !getArgMap().isShowHelp()) {
            getLogger().write(x + "\n");
        }
        System.out.println(x);
    }

    /**
     * Sets up templates. Override to add your own. This sets exactly one, ${}ROOT} which is
     * the directory the user specified as the root of the install.
     *
     * @throws IOException
     */
    protected Map<String, String> setupTemplates() throws IOException {
        templates = new HashMap<>();
        templates.put("${ROOT}", getArgMap().getRootDir().getAbsolutePath() + File.separator);
        return templates;
    }


    public static final String operationKey = "operation";

    /**
     * Actually do the operation
     *
     * @throws Throwable
     */
    protected void process() throws Throwable {
        switch (getArgMap().getOperation()) {
            case REMOVE_OPTION:
                doRemove(false);
                return;
            case INSTALL_OPTION:
                doInstallOrUpdate(false);
                break;
            case UPGRADE_OPTION:
            case UPDATE_OPTION:
                doInstallOrUpdate(true);
                break;
            case LIST_OPTION:
                doList();
                return;
            case VERSIONS_OPTION:
                listVersions();
                return;
            case HELP_OPTION:
                showHelp();
                return;
            case NOTES_OPTION:
                showReleaseNotes(getInstallConfiguration().getActualVersion());
                return;
            default:
                say("unknown option.");
                return;
        }
        say("total bytes downloaded: " + StringUtils.formatByteCount(totalDownloaded));
    }

    protected void doInstallOrUpdate(boolean isUpdate) throws Throwable {
        long startTime = System.currentTimeMillis();
        int doneCount = 0;
        int totalFileCount = getInstallConfiguration().getTotalFileCount();
        if(totalFileCount == 0){
            say("no files found to process");
            return;
        }
        for (SingleSourceSet sss : installConfiguration.getSourceSets()) {
            for (DirectoryEntry de : sss.getDirectories()) {
                File target = new File(getRoot(), de.getTargetDir());
                if (!target.exists()) {
                    trace("creating directory " + target);
                    if (!target.mkdirs()) {
                        say("warning: could not create \"" + target.getAbsolutePath() + "\". Do you have permission to do this?");
                    }
                }
                if (!de.hasFiles()) {
                    continue;
                }
                for (FileEntryInterface fe2 : de.getFiles()) {
                    if (fe2 instanceof FileEntry) {
                        doneCount = processFileEntry(sss, de, (FileEntry) fe2,
                                target, isUpdate,
                                doneCount, totalFileCount);
                    }
                    if (fe2 instanceof ZipArchive) {
                        // Remember that a ZipArchive points to an entire zip archive.
                        doneCount = processZipArchive(sss, de, (ZipArchive) fe2,
                                target, isUpdate, doneCount, totalFileCount);
                    }
                }
            }
        }
        if (getArgMap().isPacerOn()) {
            pacer.pace(doneCount, " files processed  of " + totalFileCount + " (" + ((100 * doneCount) / totalFileCount) + "%)");
            say("\n"); // spacer after last pacer call
        }
        if (doneCount != totalFileCount) {
            if (getArgMap().isLoggingEnabled()) {
                say("warning -- some files skipped. See the log " + getArgMap().logFile().getAbsolutePath());
            } else {
                say("warning -- some files skipped. ");
            }
        }
        say("total elapsed time:" + StringUtils.formatElapsedTime(System.currentTimeMillis() - startTime));
        if (!isUpdate) {
            // If they have a success message, do template replacements and print it.
            if (getInstallConfiguration().getSuccessMessage() != null) {
                say(doReplace(getInstallConfiguration().getSuccessMessage()));
            }
        }
    }

    protected int processZipArchive(SingleSourceSet sss,
                                    DirectoryEntry de,
                                    ZipArchive zfe,
                                    File target,
                                    boolean isUpdate,
                                    int doneCount,
                                    int totalFileCount) throws IOException {
        String source = sss.getSourceURL();
        source = source + (source.endsWith("/") ? "" : "/");
        source = source + zfe.getSourceName();
        URL url = new URL(source);
        processZipFile(url, de, isUpdate);

        return doneCount + 1;
    }

    protected int processFileEntry(SingleSourceSet sss,
                                   DirectoryEntry de,
                                   FileEntry fe,
                                   File target,
                                   boolean isUpdate,
                                   int doneCount,
                                   int totalFileCount) throws IOException {
        if ((isUpdate && isNotUpdateable(de, fe)) || applySkipUpdate(target)) {
            trace("skipping update on " + fe.getTargetName());
            return doneCount++;
        }
        URL url = new URL(sss.getSourceURL() + fe.getSourceName());
        File targetFile = new File(target, fe.getTargetName());
        try {
            long t = download(url, targetFile);
            if (getArgMap().isPacerOn()) {
                pacer.pace(doneCount, " files processed  of " + totalFileCount + " (" + ((100 * doneCount) / totalFileCount) + "%)");
                doneCount++;
            }
            trace("  downloaded " + url + " --> " + targetFile.getAbsolutePath() +
                    " (" + StringUtils.formatByteCount(t) + ")");
        } catch (IOException iox) {
            if (getInstallConfiguration().isFailOnError()) {
                if (getInstallConfiguration().isCleanupOnFail()) {
                    doRemove(true);
                }
                throw iox; // just bail on install
            }
            trace("warning -- could not download " + url);
            trace("  message:" + iox.getMessage());

        }
        if (isUseTemplate(de, fe) || applyTemplate(targetFile)) {
            processTemplates(targetFile);
        }
        if (isExec(de, fe) || applyExecutable(targetFile)) {
            targetFile.setExecutable(true);
        }
        return doneCount;
    }

    protected boolean isUseTemplate(DirectoryEntry de, FileEntry fe) {
        if (fe.hasUseTemplate()) return fe.isUseTemplate();
        if (de.hasUseTemplates()) return de.isUseTemplates();
        return USE_TEMPLATES_DEFAULT;
    }

    protected boolean isNotUpdateable(DirectoryEntry de, FileEntry fe) {
        if (fe.hasUpdateable()) return fe.isUpdateable();
        if (de.hasUpdateable()) return de.isUpdateable();
        return UPDATEABLE_DEFAULT;
    }

    protected boolean isExec(DirectoryEntry de, FileEntry fe) {
        if (fe.hasExecutale()) return fe.isExecutable();
        if (de.hasUpdateable()) de.isExecutable();
        return EXECUTABLE_DEFAULT;
    }

    protected void doRemove(boolean isCleanup) throws IOException {
        if (getRoot() == null) {
            say("you must explicitly specify the directory to be removed. exiting...");
        } else {
            long startTime = System.currentTimeMillis();
            if (isCleanup) {
                trace("install failed, cleanup...");
            } else {

                trace("removing" + getRoot() + " and its content");
            }
            nukeDir(getRoot());
            getRoot().delete(); //adios muchacho
            if (!isCleanup) {
                say(getRoot() + " and all of its subdirectories have been removed.");
                say("time elapsed: " + StringUtils.formatElapsedTime(System.currentTimeMillis() - startTime));
            }
        }
    }

    /**
     * Remove the contents of the  directory. At the end of this,
     * the directory is empty. It does not delete the directory,
     * however
     *
     * @param dir
     */
    protected void nukeDir(File dir) throws IOException {
        if (!dir.isDirectory()) return; //
        File[] contents = dir.listFiles();
        for (File f : contents) {
            trace("found " + f);

            if (f.isFile()) {
                trace("   deleting file:" + f);
                f.delete();
            }
            if (f.isDirectory()) {
                trace("   deleting dir:" + f);
                nukeDir(f);
                f.delete();
            }
        }
    }

    protected String getVersionFileName(String version) {
        if (version.equals(VERSION_LATEST)) {
            return getInstallConfiguration().getVersions().getLatestVersionResource();
        }
        VersionEntry ve = getInstallConfiguration().getVersions().get(version);
        if (ve == null) {
            return null;
        }
        return ve.resource;
    }


    protected void listVersions() throws IOException {
        Versions versions = getInstallConfiguration().getVersions();
        for (String key : versions.keySet()) {
            VersionEntry ve = versions.get(key);
            if (!key.equals(VERSION_LATEST)) {
                say(key + " : " + ve.description);
            }
        }
        // just always print the latest last.
        say("latest = " + versions.getLatestVersionName());
    }

    protected void doList() throws IOException {
        say("printing file listing for version \"" + getInstallConfiguration().getActualVersion() + "\"");
        for (SingleSourceSet sss : getInstallConfiguration().sourceSets) {
            //say(sss.sourceURL);
            for (DirectoryEntry de : sss.getDirectories()) {
                say(de.getTargetDir());
                if (!de.hasFiles()) {
                    say("  (empty)");
                    continue;
                }
                for (FileEntryInterface fe2 : de.getFiles()) {
                    if (fe2 instanceof FileEntry) {
                        FileEntry fe = (FileEntry) fe2;
                        String attributes = (isExec(de, fe) ? "x" : "-") + "|";
                        attributes = attributes + (!isNotUpdateable(de, fe) ? "u" : "-") + "|";
                        attributes = attributes + (isUseTemplate(de, fe) ? "p" : "-");
                        say("  " + attributes + "  " + fe.getTargetName());
                    }
                    if (fe2 instanceof ZipArchive) {
                        ZipArchive jfe = (ZipArchive) fe2;
                        say(jfe.getSourceName());
                    }
                }
            }
        }


        int size = 0;
        for (SingleSourceSet sss : getInstallConfiguration().getSourceSets()) {
            size = size + sss.size();
        }
        say(size + " total files");
        say("Key:");
        say("x = executable, u = updateable, p = preprocessed");
    }

    /**
     * Set the resource name (default is /setup.yaml) which will be read.
     * Alternately, you can override {@link #getSetupIS()} which would read
     * the setup file from anywhere as long as it ends up in an {@link InputStream}.
     *
     * @return
     */
    protected String getSetup() {
        return "/ncsa/setup.yaml";
    }

    protected InputStream getSetupIS() throws FileNotFoundException {
        String setupFile = getSetup();
        InputStream setupStream = getClass().getResourceAsStream(setupFile);
        if (setupStream == null) {
            throw new FileNotFoundException(setupStream + " not found!");
        }
        return setupStream;
    }

    public Pacer getPacer() {
        return pacer;
    }

    Pacer pacer;

    /**
     * For shut down tasks.
     *
     * @throws Throwable
     */
    public void shutdown() throws Throwable {
        if (getArgMap().isLoggingEnabled()) {
            getLogger().flush();
            getLogger().close();
        }
    }

    public boolean applyTemplate(File file) {
        return false;
    }

    public boolean applyExecutable(File file) {
        return false;
    }

    /**
     * returns true if shold be skipped.
     *
     * @param file
     * @return
     */
    public boolean applySkipUpdate(File file) {
        return false;
    }

    SecureRandom secureRandom = new SecureRandom();

    /**
     * Creates a random secret that is url-encoding safe.
     * @param length The number of bytes for the secret
     * @return
     */
    protected String createSecret(int length) {
        byte[] ba = new byte[length];
        secureRandom.nextBytes(ba);
        String out = Base64.getEncoder().encodeToString(ba);
        /*
        a URL-safe
         */
        out = out.replace("+","-");
        out = out.replace("/","_");
        out = out.replace("=","");
        return out;
    }

    /**
     * A hexadecimal identifier, all upper case. May start with a digit.
     * @param length the number of bytes for this identifier. size is 2*length.
     * @return
     */
    protected String createID(int length) {
        byte[] ba = new byte[length];
        secureRandom.nextBytes(ba);
        BigInteger bi = new BigInteger(ba);
        return bi.abs().toString(16).toUpperCase();
    }

    /**
     * Creates a base 64 encoded, URL safe secret. The length refers to bytes so the
     * size of the secret is at most ⌈4*length/3. Does not pad.
     * @param length
     * @return
     */
    protected String getSecret(int length) {
        byte[] ba = new byte[length];
        secureRandom.nextBytes(ba);
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(ba);
    }

    /**
     * Creates a hexadecimal identifier. The length is the number of bytes and the size
     * of the ID is 2*length.
     * @param length
     * @return
     */
    protected String getID(int length) {
        byte[] ba = new byte[length];
        secureRandom.nextBytes(ba);
        BigInteger bi = new BigInteger(ba);
        return bi.abs().toString(16).toUpperCase();
    }
}
