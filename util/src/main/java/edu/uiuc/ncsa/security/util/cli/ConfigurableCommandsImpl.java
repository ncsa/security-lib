package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.util.*;
import net.sf.json.JSONObject;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;

/**
 * Basic implementation of Commands. This supports loading configurations.
 * <b>NOTE</b> This does not actually run commands! It is a top-level class that delegates to its
 * command implementations (such as for stores, keys). Therefore, you should not put code here
 * that actually executes things. The contract of this is that it manages logging, loading configurations
 * and instantiating the actual {@link Commands} implementations that do everything.
 * <p>Created by Jeff Gaynor<br>
 * on 5/20/13 at  11:35 AM
 */
public abstract class ConfigurableCommandsImpl implements Commands {
    protected ConfigurableCommandsImpl(MyLoggingFacade logger) {
        this.logger = logger;
    }

    /**
     * returns "true if the command has the flag --help in it. This is a cue from the user to show
     * the help for a given function. So it the function is called "X" and its help is in the function
     * "showXHelp" then a value of true from this should simply invoke "showXHelp" and return.
     *
     * @param inputLine
     * @return
     */
    protected boolean showHelp(InputLine inputLine) {
        if ((1 < inputLine.size()) && inputLine.getArg(1).equals("--help")) return true;
        return false;
    }

    @Override
    public IOInterface getIOInterface() {
        return getDriver().getIOInterface();
    }


    protected void say(String x) {
        getIOInterface().println(x);
    }

    protected void sayv(String x) {
        if (isVerbose()) {
            say(x);
        }
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    String configName;

    public static class ListOnlyNotification extends GeneralException {

    }

    public void load(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showLoadHelp();
            return;
        }
        if (inputLine.getArgCount() == 0) {
            if (StringUtils.isTrivial(configName) && StringUtils.isTrivial(getConfigFile())) {
                info("no configuration set");
                sayv("no configuration set");
                return;
            }
            String m = StringUtils.isTrivial(configName) ? "no config name" : "current config name= \"" + configName;
            m = m + " " + (StringUtils.isTrivial(getConfigFile()) ? "no file set" : ("from file " + getConfigFile()));
            info(m);
            sayv(m);
            return;
        }

        if (inputLine.hasArg(LIST_CFGS)) {
            listConfigs(inputLine);
            // no way to return a value and have it findable with introspection (since signature would change).
            // Only way to send notification. 
            throw new ListOnlyNotification();
        }

        String fileName = null;
        configName = inputLine.getArg(1);

        if (2 < inputLine.size()) {
            fileName = inputLine.getArg(2);
            File f = new File(fileName);
            if (!f.exists()) {
                throw new MyConfigurationException("sorry, file does not exist: " + fileName);
            }
            if (f.isDirectory()) {
                throw new MyConfigurationException("sorry, \" + fileName + \" is a directory");
            }
            if (!f.canRead()) {
                throw new MyConfigurationException("sorry, \" + fileName + \" is not readable");
            }
        } else {
            fileName = getConfigFile();
        }
        sayv("loading configuration from " + fileName + ", named " + configName);
        loadConfig(fileName, configName);
        sayv("done!");
    }

    boolean traceOn = false;

    public void trace(InputLine inputLine) throws Exception {
        if (inputLine.getArgCount() == 0 || showHelp(inputLine)) {
            say("trace [on  off] ");
            say("Turn on low level trace for all commands. This is only useful if there are ");
            say("issues with the running of the CLI itself and is a debugging tool for system programmers.");
            say("No argument displays the current status.");
            say("Trace is currently " + (traceOn ? "on" : "off"));
            return;
        }
        traceOn = inputLine.getArg(1).equals("on");
        DebugUtil.setIsEnabled(traceOn);
        setDebugOn(traceOn);
        if (traceOn) {
            DebugUtil.setDebugLevel(DebugUtil.DEBUG_LEVEL_TRACE);
        } else {
            DebugUtil.setDebugLevel(DebugUtil.DEBUG_LEVEL_OFF);
        }
        say("trace " + (traceOn ? "on" : "off"));
    }

    protected String FILE_SWITCH = "-file";

    protected void listConfigs(InputLine inputLine) throws Exception {

        String targetFilename = getConfigFile();
        if (inputLine.hasArg(FILE_SWITCH)) {
            targetFilename = inputLine.getNextArgFor(FILE_SWITCH);
            inputLine.removeSwitchAndValue(FILE_SWITCH);
        }
        if (StringUtils.isTrivial(targetFilename)) {
            say("Sorry no configuration file specified.");
            return;
        }
        File target = new File(targetFilename);

        if (!target.exists()) {
            say("Sorry but \"" + target.getAbsolutePath() + "\" does not exist.");
            return;
        }
        if (!target.isFile()) {
            say("Sorry but \"" + target.getAbsolutePath() + "\" is not a file.");
            return;
        }
        List<String> names = doListNames(target);

        if (names.isEmpty()) {
            say("no configurations found.");
        } else {
            FormatUtil.formatList(inputLine, names);
            say("found " + names.size() + " entries. Done!");
        }
    }

    /**
     * Figures out by file extension what to list. Override as needed. Default is .xml.
     *
     * @param target
     * @return
     * @throws Exception
     */
    protected List<String> doListNames(File target) throws Exception {
        List<String> names = new ArrayList<>();
        if (target.getName().endsWith(".xml")) {
            names = listXMLConfigs(target);
        }
        return names;
    }

    protected List<String> listXMLConfigs(File target) throws Exception {

        XMLConfiguration xmlConfiguration = Configurations.getConfiguration(target);
        ConfigurationNode rootNode = xmlConfiguration.getRoot();
        List<String> names = new ArrayList<>(); // To keep sorted
        for (ConfigurationNode node : rootNode.getChildren()) {
            String name = Configurations.getFirstAttribute(node, "name");
            if (name != null) {
                names.add(name);
            }
        }

        return names;
    }


    String LIST_CFGS = "-list";

    protected void showLoadHelp() {
        say("load [" + FILE_SWITCH + " config_file] " + LIST_CFGS + " = list the configurations by name in the file or current file");
        say("load [config_name config_file]  a configuration from the file. The options are");
        say("   load - displays current configuration file and name of the current configuration.");
        say("   load configName - loads the named configuration from the currently active configuration file.");
        say("   load configName fileName - loads the configuration named \"configName\" from the fully qualified name of the file and sets it active");
        say("\nExample\n");
        say("   load default /var/www/config/config.xml \n");
        say("loads the configuration named \"default\" from the file named \"config.xml\" in the directory \"/var/www/config\"\n");
        say("Note that after a load, any new configuration file becomes the default for future load operations.");
    }


    public AbstractEnvironment getEnvironment() throws Exception {
        if (environment == null) {
            environment = getLoader().load();
        }
        return environment;
    }

    public void setEnvironment(AbstractEnvironment environment) {
        this.environment = environment;
    }


    AbstractEnvironment environment;

    ConfigurationNode configurationNode;

    /**
     * For the configuration. This is the tag name (e.g. "client" or "server") in the XML file.
     *
     * @return
     */
    public abstract String getComponentName();

    public abstract ConfigurationLoader<? extends AbstractEnvironment> getLoader();

    public abstract void setLoader(ConfigurationLoader<? extends AbstractEnvironment> loader);


    public static final String VERBOSE_OPTION = "v"; // if present do a backup of the target to a backup schema.
    public static final String VERBOSE_LONG_OPTION = "verbose"; // if present do a backup of the target to a backup schema.

    public static final String DEBUG_OPTION = "d"; // if present do a backup of the target to a backup schema.
    public static final String DEBUG_LONG_OPTION = "debug"; // if present do a backup of the target to a backup schema.


    public static final String LOG_FILE_OPTION = "log"; // if present do a backup of the target to a backup schema.
    public static final String LOG_FILE_LONG_OPTION = "logFile"; // if present do a backup of the target to a backup schema.

    // other options
    public static final String HELP_OPTION = "h";
    public static final String HELP_LONG_OPTION = "help";

    public static final String INPUT_OPTION = "in";
    public static final String OUTPUT_OPTION = "out";
    public static final String COMMENT_START_OPTION = "comment";
    public static String COMMENT_START = "#";


    public static final String CONFIG_FILE_OPTION = "-cfg";
    public static final String CONFIG_FILE_LONG_OPTION = "-configFile";

    public static final String ENV_OPTION = "-set_env";
    public static final String ENV_LONG_OPTION = "-set_env";

    public static final String CONFIG_NAME_OPTION = "-name";
    public static final String CONFIG_NAME_LONG_OPTION = "-name";

    public Map<Object, Object> getGlobalEnv() {
        return globalEnv;
    }

    Map<Object, Object> globalEnv;
    String currentEnvFile = null;

    protected void readEnv(String path, boolean verbose) {
        // All errors loading the environment are benign.
        File f = new File(path);
        if (!f.exists()) {
            if (verbose) say("Cannot read environment file \"" + path + "\"");
            return;
        }
        if (!f.isFile()) {
            if (verbose) say("\"" + path + "\" is not  file and cannot be read to set the environment.");
            return;
        }
        String allLines = "";
        try {
            FileReader fileReader = new FileReader(f);
            BufferedReader bf = new BufferedReader(fileReader);
            String lineIn = bf.readLine();
            while (lineIn != null) {
                allLines = allLines + lineIn;
                lineIn = bf.readLine();
            }
            bf.close();
        } catch (Throwable t) {
            if (verbose) say("Error loading environment: \"" + t.getMessage() + "\"");
            if (isVerbose()) {
                t.printStackTrace();
            }

        }
        try {
            JSONObject jsonObject = JSONObject.fromObject(allLines);
            if (jsonObject != null && !jsonObject.isEmpty()) {
                globalEnv = jsonObject;
                return;
            }
        } catch (Throwable tt) {
            // Must be a properties file...
        }
        // now figure out what the format is.
        try {
            XProperties xp = new XProperties();
            xp.load(f);
            if (!xp.isEmpty()) {
                globalEnv = xp;
            }
        } catch (Throwable t) {
            if (isVerbose()) {
                t.printStackTrace();
            }
            if (verbose) say("Could not parse envirnoment file.");
        }
        currentEnvFile = path;
    }

/*    @Override
    public void print_help() throws Exception {
        say("Need to write help.");
    }*/

    /**
     * Called at initialization to read and process the command line arguments.
     * NOTE that this is not called in this class, but by inheritors.
     */
    protected void initialize() {
        if (getConfigFile() == null || getConfigFile().length() == 0) {
            say("Warning: no configuration file specified. type in 'load --help' to see how to load one.");
            return;
        }
        String cfgName = null;

        info("Config name = " + cfgName);

        if (getConfigName() == null) {
            info("no named for a configuration given");
        } else {
            info("getting named configuration \"" + getConfigName() + "\"");
        }
        try {
            loadConfig(getConfigFile(), getConfigName());
        } catch (Throwable x) {
            if (x instanceof RuntimeException) {
                throw (RuntimeException) x;
            }
            throw new GeneralException("Error initializing CLI:" + x.getMessage(), x);
        }
    }

    protected void loadConfig(String filename, String configName) throws Throwable {
        if (filename == null) {
            throw new MyConfigurationException("Error: no configuration file specified");
        }
        setLoader(figureOutLoader(filename, configName));
        setEnvironment(null); //so it gets loaded next time it's needed.
        getEnvironment(); // reload it
        this.configName = configName;
        this.configFile = filename;

    }

    /**
     * This is done so configurations can be loaded by inheritors.
     *
     * @param fileName
     * @param configName
     * @return
     */
    protected abstract ConfigurationLoader<? extends AbstractEnvironment> figureOutLoader(String fileName, String configName) throws Throwable;

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    String configFile;

    /**
     * Parses command line arguments. This is usually called after {@link CLIDriver#bootstrap(String[])},
     * which pulls off its startup options
     *
     * @param args
     * @throws Exception
     */
    @Override
    public InputLine bootstrap(InputLine args) throws Throwable {
        if (args.hasArg(VERBOSE_OPTION, VERBOSE_LONG_OPTION)) {
            setVerbose(true);
            args.removeSwitch(VERBOSE_OPTION, VERBOSE_LONG_OPTION);
        }
        if (args.hasArg(DEBUG_OPTION, DEBUG_LONG_OPTION)) {
            setDebugOn(true);
            args.removeSwitch(DEBUG_OPTION, DEBUG_LONG_OPTION);
        }
        if (args.hasArg(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION)) {
            setConfigFile(args.getNextArgFor(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION));
            args.removeSwitchAndValue(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION);
        }
        if (args.hasArg(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION)) {
            setConfigName(args.getNextArgFor(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION));
            args.removeSwitchAndValue(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION);
        }
        if (args.hasArg(ENV_OPTION, ENV_LONG_OPTION)) {
            String envFile = args.getNextArgFor(ENV_OPTION, ENV_LONG_OPTION);
            args.removeSwitchAndValue(ENV_OPTION, ENV_LONG_OPTION);
            readEnv(envFile, false); // on init, silently ignore unless -v option enabled.
            currentEnvFile = envFile;
        }
        initialize();
        return args;// now try to get everything.
    }


    boolean debugOn = true;

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean setOn) {
        this.debugOn = setOn;
    }

    public void setMyLogger(MyLoggingFacade myLoggingFacade) {
        logger = myLoggingFacade;

    }

    public MyLoggingFacade getMyLogger() {
        if (logger == null) {
            //  LoggerProvider loggerProvider = new LoggerProvider(getLogfileName(), "cli logger", 1, 1000000, isDebugOn(), true);
            LoggerProvider loggerProvider = new LoggerProvider("log.xml", "cli logger", 1, 1000000, true, true, Level.INFO);
            logger = loggerProvider.get();
        }
        return logger;
    }

    transient MyLoggingFacade logger;

    protected boolean hasLogger() {
        return logger != null;
    }
    @Override
    public void debug(String x) {
        if (isVerbose()) {
            say(x);
        }
        if(hasLogger()) {
            getMyLogger().debug(x);
        }
    }

    public void info(String x) {
        if (isVerbose()) {
            say(x);
        }
        if(hasLogger()) {
            getMyLogger().info(x);
        }
    }

    public void warn(String x) {
        if (isVerbose()) {
            say(x);
        }
        if(hasLogger()) {
            getMyLogger().warn(x);
        }
    }

    public void error(String x) {
        if (isVerbose()) {
            say(x);
        }
        if(hasLogger()) {
            getMyLogger().error(x);
        }
    }

    boolean verbose;
    
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * This will take a String and append the correct number of blanks on the
     * left so it is the right width. This is used for making the banner.
     * <h2>Used extensively in subclasses for formatting help</h2>
     *
     * @return
     * @deprecated use {@link StringUtils#pad2(String, int)}
     */
    protected String padLineWithBlanks(String x, int width) {
        // argh!
        return StringUtils.pad2(x, width);
    }

    public abstract void useHelp();

    /**
     * Override this to invoke the specific components that make up your CLI.
     *
     * @param inputLine
     * @return
     * @throws Exception
     */
    public boolean use(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            useHelp();
            return true;
        }

        if (1 == inputLine.size()) {
            say("Sorry, you need to give the name of the component to invoke it.");
            return true;
        }
        return false;
    }

    /**
     * Either switch to another component or (if there are arguments) simply run the
     * single command and return. Note that each component has stored state, so
     * these will be run with whatever is in that state.
     * Requires {@link #use(InputLine)} be overridden.
     *
     * @param inputLine
     * @param commands
     * @return
     */

    protected boolean switchOrRun(InputLine inputLine, CommonCommands commands) {
        boolean switchComponent = 1 < inputLine.getArgCount();

        CLIDriver currentDriver = commandStack.peek();


        if (currentDriver.getCLICommands().length == 1) {
            if (currentDriver.getCLICommands()[0].getName().equals(commands.getName())) {
                // already currently running.
                return true;
            }
        }
        CLIDriver cli = new CLIDriver();
        cli.setIOInterface(getIOInterface());
        cli.addCommands(commands);
        cli.setEnv(getGlobalEnv());
        if (this instanceof ComponentManager) {
            cli.setComponentManager((ComponentManager) this);
        }
        commandStack.push(cli);
        if (switchComponent) {
            inputLine.removeArgAt(0); // removes original arg ("use")
            cli.execute(inputLine.removeArgAt(0)); // removes components before executing
        } else {
            cli.start();
        }
        commandStack.pop(); // remove previous driver
        return true;
    }

    /**
     * Zeroth element of the command stack is the root instance of this class. The should
     * never be empty.
     */
    Stack<CLIDriver> commandStack = new Stack<>();

    @Override
    public CLIDriver getDriver() {
        return driver;
    }

    /**
     * Back reference to the driver for this class
     *
     * @param driver
     */
    @Override
    public void setDriver(CLIDriver driver) {
        commandStack.push(driver);
        if (driver.getLogger() != null) {
            setMyLogger(driver.getLogger());
        }
        this.driver = driver;
    }

    CLIDriver driver;
}
