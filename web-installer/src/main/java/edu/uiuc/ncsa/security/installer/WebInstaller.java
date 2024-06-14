package edu.uiuc.ncsa.security.installer;

import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    static protected final String HELP_FLAG = "--help";
    static protected final String HELP_OPTION = "help";
    static protected final String DIR_ARG = "-dir";
    static protected final String DEBUG_FLAG = "-v";
    static protected final String INSTALL_OPTION = "install";
    static protected final String LIST_OPTION = "list";
    static protected final String REMOVE_OPTION = "remove";
    static protected final String OA4MP_FLAG = "-oa4mp";
    static protected final String ALL_FLAG = "-all";
    static protected final String VERSION_FLAG = "-version";
    static protected final String VERSION_LATEST = "latest";
    static public final Boolean USE_TEMPLATES_DEFAULT = false;
    static public final Boolean EXECUTABLE_DEFAULT = false;
    static public final Boolean UPDATEABLE_DEFAULT = true;

    static List<String> allOps = Arrays.asList(UPDATE_OPTION, REMOVE_OPTION, HELP_OPTION, INSTALL_OPTION, LIST_OPTION);
    public String getAppName(){
        return "Sec-Lib";
    }
    protected void showHelp() {
        say("===============================================================");
        say("java -jar installer.jar install operation arguments* flags*");
        say("===============================================================");
        say("This will install " + getAppName() + " to your system. Options are:");
        say("(none) = same as help");
        say(INSTALL_OPTION + " = install");
        say(UPDATE_OPTION + " = upgrade");
        say(REMOVE_OPTION + " = remove");
        say(HELP_OPTION + " = show help and exit. Note you can also use the flag " + HELP_FLAG);
        say(LIST_OPTION + " = list all the files in the distribution. Nothing is done.");
        say("--------------");
        say("arguments are:");
        say(DIR_ARG + " root = install to the given directory. If omitted, you will be prompted.");
        printMoreHelp();
        say("--------------");
        say("Flags are:");
        say(DEBUG_FLAG + " = verbose mode -- print all messages from the installer as it runs. This is quite chatty.");
        say(HELP_FLAG + " = this help message");
        say(ALL_FLAG + " = do all components of" + getAppName());
        say("");
        say("E.g.");
        say("A fresh install, specifying the machine and port. This assumes OA4MP_HOME has been set.");
        say("The port is set to -1 meaning that no port will be specified for the endpoints. This is used, ");
        say("e.g., if this is behind another server (like Apache) that forwards requests.");

        printMoreExamplesHelp();
    }

    protected void printMoreHelp() {

    }


    protected void printMoreExamplesHelp() {

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

    /**
     * read the configuration file as an input stream.  This also creates and initializes
     * the {@link InstallConfiguration} for this installation. At the end of this method,
     * you should be able to start processing.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    protected void readConfig(InputStream inputStream) throws IOException {
        if (installConfiguration != null) {
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(inputStream, baos);
        String inString = baos.toString(StandardCharsets.UTF_8);
        inputStream.close();
        baos.close();

        Yaml yaml = new Yaml(new SafeConstructor());
        Map map = yaml.load(inString);
        InstallConfigurationImporter importer = new InstallConfigurationImporter(map);
        importer.fromMap();
        installConfiguration = importer.getInstallConfiguration();
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
    protected void init(String[] args) throws Throwable {
        if(argMap == null) {
            argMap = new ArgMap(args);
            setupArgMap(args);
        }
        if (getArgMap().isShowHelp()) {
            showHelp();
            return;
        }
        if (getArgMap().hasRootDir()) {
            setRoot(getArgMap().getRootDir());
        }

        String cfgFileName;
        if (getArgMap().getVersion().equals(VERSION_LATEST)) {
            cfgFileName = getLastestConfigFileName();
        } else {
            cfgFileName = getArgMap().getVersion();
        }

        InputStream textStream = getClass().getResourceAsStream(cfgFileName);
        readConfig(textStream);
        setSourceURL(installConfiguration.getBaseURL());
        setDebugOn(getArgMap().is(DEBUG_FLAG));
        setupTemplates();
    }

    /**
     * If you need to add more arguments to the {@link ArgMap}, rather than overriding that
     * class, just set them here. This is called immediately after it is created, but before
     * anything else accesses the arg map.

     * @param args
     */
       protected void setupArgMap(String[] args){

       }
    /**
     * Override this as needed to get the latest version of your configuration file
     * as a resource.
     *
     * @return
     */
    public String getLastestConfigFileName() {
        return "/test-cfg.yaml";
    }

    public ArgMap getArgMap() {
        return argMap;
    }

    ArgMap argMap = null;

    public static void main(String[] args) {
        try {
            WebInstaller webInstaller = new WebInstaller();
            webInstaller.init(args);
            System.out.println(webInstaller.installConfiguration);
            webInstaller.process();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    File root;

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    String sourceURL;

    protected void copyFile(String sourceName,
                            String targetName,
                            boolean isExecutable,
                            boolean preProcess,
                            boolean overWrite) {
        File targetFile = new File(getRoot(), targetName);
        if (!overWrite) return;
        URI.create(getSourceURL() + "/" + sourceName);
        targetFile.mkdirs();
    }

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

    protected void trace(String message) {
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

    protected void say(String x) {
        System.out.println(x);
    }

    protected void setupTemplates() throws IOException {
        templates = new HashMap<>();
    }


    public static final String operationKey = "operation";

    /**
     * Actually do the operation
     *
     * @throws Throwable
     */
    protected void process() throws Throwable {
        if (argMap.isRemove()) {
            doRemove();

            return;
        }
        doIt(argMap.isUpgrade());
        trace("total bytes downloaded: " + StringUtils.formatByteCount(totalDownloaded));
    }

    protected void doIt(boolean isUpgrade) throws Throwable {
        long startTime = System.currentTimeMillis();
        for (DirectoryEntry de : installConfiguration.getDirectories()) {
            File target = new File(getRoot(), de.getTargetDir());
            trace("processing " + target);
            if (!target.exists()) {
                trace("creating directory " + target);
                target.mkdirs();
            }
            for (FileEntry fe : de.getFiles()) {
                if (isUpgrade && isUpdateable(de, fe)) {
                    trace("skipping update on " + fe.getTargetName());
                    continue;
                }
                URL url = new URL(getSourceURL() + fe.getSourceName());
                File targetFile = new File(target, fe.getTargetName());
                try {
                    long t = download(url, targetFile);
                    trace("  downloaded " + url + " --> " + targetFile.getAbsolutePath() +
                            " (" + StringUtils.formatByteCount(t) + ")");
                } catch (IOException iox) {
                    say("warning -- could not download " + url);
                    trace("  message:" + iox.getMessage());

                }
                if (isUseTemplate(de, fe)) {
                    processTemplates(targetFile);
                }
                if (isExec(de, fe)) {
                    targetFile.setExecutable(true);
                }
            }
        }
       say("total elapsed time:" + StringUtils.formatElapsedTime(System.currentTimeMillis() - startTime));
    }

    protected boolean isUseTemplate(DirectoryEntry de, FileEntry fe) {
        if (fe.hasUseTemplate()) return fe.isUseTemplate();
        if (de.hasUseTemplates()) return de.isUseTemplates();
        return USE_TEMPLATES_DEFAULT;
    }

    protected boolean isUpdateable(DirectoryEntry de, FileEntry fe) {
        if (fe.hasUpdateable()) return fe.isUpdateable();
        if (de.hasUpdateable()) return de.isUpdateable();
        return UPDATEABLE_DEFAULT;
    }

    protected boolean isExec(DirectoryEntry de, FileEntry fe) {
        if (fe.hasExecutale()) return fe.isExecutable();
        if (de.hasUpdateable()) de.isExecutable();
        return EXECUTABLE_DEFAULT;
    }

    protected void doRemove() {
        if (getRoot() == null) {
            say("you must explicitly specify the directory to be removed. exiting...");
        } else {
            long startTime = System.currentTimeMillis();

            trace("removing" + getRoot() + " and its content");
            nukeDir(getRoot());
            getRoot().delete(); //adios muchacho
            say(getRoot() + " and all of its subdirectories have been removed.");
            say("time elapsed: " + StringUtils.formatElapsedTime(System.currentTimeMillis() - startTime));
        }
    }

    /**
     * Remove the contents of the  directory. At the end of this,
     * the directory is empty. It does not delete the directory,
     * however
     *
     * @param dir
     */
    protected void nukeDir(File dir) {
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
}
