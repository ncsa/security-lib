package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import edu.uiuc.ncsa.security.core.util.LoggerProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import org.apache.commons.cli.*;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;

/**
 * Basic implementation of Commands. This supports loading configurations.
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

    protected void say(String x) {
        System.out.println(x);
    }

    public void load(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            showLoadHelp();
            return;
        }
        String fileName = null;
        String configName = inputLine.getArg(1);

        if (2 < inputLine.size()) {
            fileName = inputLine.getArg(2);

        } else {
            fileName = getConfigFile();
        }
        say("loading configuration from " + fileName + ", named " + configName);
        info("loading configuration from " + fileName + ", named " + configName);
        loadConfig(fileName, configName);
        say("done!");
    }


    protected void showLoadHelp() {
        say("loads a configuration from the file. The options are");
        say("   load configName - Loads the named configuration from the currently active configuration file.");
        say("   load configName fileName - loads the configuration named \"configName\" from the fully qualified name of the file and sets it active");
        say("\nExample\n");
        say("   load default /var/www/config/config.xml \n");
        say("loads the configuration named \"default\" from the file named \"config.xml\" in the directory \"/var/www/config\"\n");
        say("Note that after a oad, any new configuration file becomes the default for future store operations.");
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

    public ConfigurationNode getConfigurationNode() {
        return configurationNode;
    }

    public void setConfigurationNode(ConfigurationNode configurationNode) {
        this.configurationNode = configurationNode;
    }

    ConfigurationLoader<? extends AbstractEnvironment> loader;

    public static final String VERBOSE_OPTION = "v"; // if present do a backup of the target to a backup schema.
    public static final String VERBOSE_LONG_OPTION = "verbose"; // if present do a backup of the target to a backup schema.

    public static final String DEBUG_OPTION = "d"; // if present do a backup of the target to a backup schema.
    public static final String DEBUG_LONG_OPTION = "debug"; // if present do a backup of the target to a backup schema.


    public static final String LOG_FILE_OPTION = "log"; // if present do a backup of the target to a backup schema.
    public static final String LOG_FILE_LONG_OPTION = "logFile"; // if present do a backup of the target to a backup schema.

    // other options
    public static final String HELP_OPTION = "h";
    public static final String HELP_LONG_OPTION = "help";


    public static final String CONFIG_FILE_OPTION = "cfg";
    public static final String CONFIG_FILE_LONG_OPTION = "configFile";

    public static final String USE_COMPONENT_OPTION = "use";
    public static final String USE_COMPONENT_LONG_OPTION = "use";


    public static final String DEFAULT_LOG_FILE = "log.xml";


    public static final String CONFIG_NAME_OPTION = "name";
    public static final String CONFIG_NAME_LONG_OPTION = "name";

    public void initialize() {
        if (getConfigFile() == null || getConfigFile().length() == 0) {
            say("Warning: no configuration file specified. type in 'load --help' to see how to load one.");
            return;
        }
        String cfgName = null;

        if (hasOption(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION)) {
            cfgName = getCommandLine().getOptionValue(CONFIG_NAME_OPTION);
        }
        LoggerProvider loggerProvider = null;
        if (hasOption(LOG_FILE_OPTION, LOG_FILE_LONG_OPTION)) {
            // create the logger for this
            loggerProvider = new LoggerProvider(getLogfileName(), "cli logger", 1, 1000000, false, true, true);
        } else {
            loggerProvider = new LoggerProvider("log.xml", "cli logger", 1, 1000000, false, true, true);
        }
        logger = loggerProvider.get();

        info("Config name = " + cfgName);

        if (cfgName == null) {
            info("no named for a configuration given");
        } else {
            info("getting named configuration \"" + cfgName + "\"");
        }
        try {
            loadConfig(getConfigFile(), cfgName);
        } catch (Exception x) {
            if (x instanceof RuntimeException) {
                throw (RuntimeException) x;
            }
            throw new GeneralException("Error initializing CLI:" + x.getMessage(), x);
        }
    }

    protected void loadConfig(String filename, String configName) throws Exception {
        if (filename == null) {
            throw new MyConfigurationException("Error: no configuration file specified");
        }
        setConfigurationNode(ConfigUtil.findConfiguration(filename, configName, getComponentName()));
        setEnvironment(null); //so it gets loaded next time it's needed.
        getEnvironment(); // reload it
    }


    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    String configFile;

    CommandLine cmd = null;

    protected void parseCommandLine(String[] args) throws UnrecognizedOptionException, ParseException {
        CommandLineParser clp = new BasicParser();
        try {
            cmd = clp.parse(getOptions(), args);
        } catch (UnrecognizedOptionException ux) {
            say("Error: unrecognized option  + " + ux.getMessage());
            say("Invoke with -help for more");
            if (isVerbose()) {
                ux.printStackTrace();
            }
            throw ux;
        } catch (ParseException e) {
            say("Error: could not parse a command line argument:" + e.getMessage());
            if (isVerbose()) {
                e.printStackTrace();
            }
            throw e;
        }
    }

    public CommandLine getCommandLine() {
        return cmd;
    }

    /**
     * Returns true if execution should continue, false if not. Call this first to see if execution should proceed.
     *
     * @param args
     * @return
     * @throws Exception
     */
    protected boolean getOptions(String[] args) throws Exception {
        getOptions();
        if (args.length == 0) {
            //     help();
            return false;
        }
        for (String z : args) {
            if (z.toLowerCase().endsWith("help")) {
                //        help();
                return false;
            }
        }
        parseCommandLine(args);
        if (hasOption(HELP_OPTION, HELP_LONG_OPTION)) {
            //      help();
            return false;
        }
        setVerbose(false);

        if (hasOption(VERBOSE_OPTION, VERBOSE_LONG_OPTION)) {
            setVerbose(true);
        }

        setDebugOn(false);
        if (hasOption(DEBUG_OPTION, DEBUG_LONG_OPTION)) {
            setDebugOn(true);
        }

        if (hasOption(LOG_FILE_OPTION, LOG_FILE_LONG_OPTION)) {
            setLogfileName(getCommandLine().getOptionValue(LOG_FILE_LONG_OPTION));
        }

        if (hasOption(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION)) {
            setConfigFile(getCommandLine().getOptionValue(CONFIG_FILE_OPTION));
        }
        return true;
    }

    boolean debugOn = true;

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean setOn) {
        this.debugOn = setOn;
    }

    /**
     * Checks if the long or short form is part of the command line options.
     *
     * @param shortForm
     * @param longForm
     * @return
     */
    protected boolean hasOption(String shortForm, String longForm) {
        return getCommandLine().hasOption(shortForm) || getCommandLine().hasOption(longForm);
    }

    /**
     * Override this to set up your options. You should also use this to check the action and
     * set it, .e.g.,<br><br>
     * <code>
     * &nbsp;&nbsp;&nbsp;&nbsp;Options options = super.getOptions();<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;options.addOptions(SHORT_FORM, LONG_FORM, ... // This is in the commons documentation <BR>
     * &nbsp;&nbsp;&nbsp;&nbsp;checkAction(SHORT_FORM, LONG_FORM, ACTION_CODE);<BR>
     * </code>
     * <br><br>
     *
     * @return
     */
    protected Options getOptions() {
        Options options = new Options();
        options.addOption(HELP_OPTION, HELP_LONG_OPTION, false, "Display the help message.");
        options.addOption(DEBUG_OPTION, DEBUG_LONG_OPTION, false, "Enable/disable debug mode.");
        options.addOption(VERBOSE_OPTION, VERBOSE_LONG_OPTION, false, "Set verbose mode on");
        options.addOption(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION, true, "Set the configuration file");
        options.addOption(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION, true, "Set the name of the configuration");
        options.addOption(LOG_FILE_OPTION, LOG_FILE_LONG_OPTION, true, "Set the log file");
        options.addOption(USE_COMPONENT_OPTION, USE_COMPONENT_LONG_OPTION, true, "Specify the component to use.");
        return options;
    }

    public void setMyLogger(MyLoggingFacade myLoggingFacade) {
        logger = myLoggingFacade;

    }

    public MyLoggingFacade getMyLogger() {
        if (logger == null) {
            //  LoggerProvider loggerProvider = new LoggerProvider(getLogfileName(), "cli logger", 1, 1000000, isDebugOn(), true);
            LoggerProvider loggerProvider = new LoggerProvider("log.xml", "cli logger", 1, 1000000, false, true, true);
            logger = loggerProvider.get();
        }
        return logger;
    }

    MyLoggingFacade logger;

    @Override
    public void debug(String x) {
        if (isDebugOn()) {
            say(x);
        }
        getMyLogger().debug(x);
    }

    public void info(String x) {
        if (isVerbose()) {
            say(x);
        }
        getMyLogger().info(x);
    }

    public void warn(String x) {
        if (isVerbose()) {
            say(x);
        }
        getMyLogger().warn(x);
    }

    public void error(String x) {
        if (isVerbose()) {
            say(x);
        }
        getMyLogger().error(x);
    }

    String logfileName;
    boolean verbose;

    public String getLogfileName() {
        return logfileName;
    }

    public void setLogfileName(String logfileName) {
        this.logfileName = logfileName;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * This will take a String and append the correct number of blanks on the
     * left so it is the right width. This is used for making the banner.
     *
     * @param x
     * @param width
     * @return
     */
    protected String padLineWithBlanks(String x, int width) {
        String xx = StringUtils.rightPad(x, width, " ");
        return xx;
    }

    public abstract void useHelp();

    /**
     * Override this to invoke the specific components that make up your CLI.
     * @param inputLine
     * @return
     * @throws Exception
     */
    public boolean use(InputLine inputLine) throws Exception {
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


}
