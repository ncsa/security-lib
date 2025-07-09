package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.util.*;
import edu.uiuc.ncsa.security.util.configuration.XMLConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 * Basic class for writing command line tools. This allows for verbose, debug and help options automatically.
 * It supports logging to a file and specifying a configuration file names. In short, a lot
 * of the random grunt work for using the commons command line toolkit is here.
 * <p>Created by Jeff Gaynor<br>
 * on 9/7/11 at  12:31 PM
 */
public abstract class CLITool2 implements Logable {
    public static final String VERBOSE_OPTION = "v"; // if present do a backup of the target to a backup schema.
    public static final String VERBOSE_LONG_OPTION = "verbose"; // if present do a backup of the target to a backup schema.

    public static final String DEBUG_OPTION = "d"; // if present do a backup of the target to a backup schema.
    public static final String DEBUG_LONG_OPTION = "debug"; // if present do a backup of the target to a backup schema.


    public static final String LOG_FILE_OPTION = "log"; // if present do a backup of the target to a backup schema.
    public static final String LOG_FILE_LONG_OPTION = "logFile"; // if present do a backup of the target to a backup schema.

    // other options
    public static final String HELP_OPTION = "--h";
    public static final String HELP_LONG_OPTION = "--help";


    public static final String CONFIG_FILE_OPTION = "cfg";
    public static final String CONFIG_FILE_LONG_OPTION = "configFile";


    public static final String DEFAULT_LOG_FILE = "log.xml";


    public static final String CONFIG_NAME_OPTION = "name";
    public static final String CONFIG_NAME_LONG_OPTION = "name";
    public static final int CONFIG_NAME_ACTION = 100;
    /**
     * The default action for this tool at startup. Do nothing.
     */
    public static final int NO_ACTION = 0;

    boolean verbose = false;
    boolean debugOn = true;

    String logfileName = DEFAULT_LOG_FILE;


    public AbstractEnvironment getEnvironment() throws Exception {
        if (environment == null) {
            environment = getLoader().load();
        }
        return environment;
    }

    public void setEnvironment(AbstractEnvironment environment) {
        this.environment = environment;
    }

    /**
     * Returns the name of the configuration component (usually "server" or "client") that identifies the XML
     * elements which might have the configuration. These are resolved by their name attribute.
     *
     * @return
     */
    public abstract String getComponentName();

    public abstract ConfigurationLoader<? extends AbstractEnvironment> getLoader() throws Exception;

    AbstractEnvironment environment;

    public BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    BufferedReader bufferedReader;

    protected String readline() throws IOException {
        return getIoInterface().readline();
        //return getBufferedReader().readLine();
    }

    public static IOInterface getIoInterface() {
        if(ioInterface == null){
            ioInterface = new BasicIO();
        }
        return ioInterface;
    }

    public static void setIoInterface(IOInterface newIOInterface) {
        ioInterface = newIOInterface;
    }

   static IOInterface ioInterface = null;
    /**
     * Does the actual work. This is where you put your executable code.
     */
    public abstract void doIt() throws Exception;

    protected ConfigurationNode getConfigurationNode() {
        return configurationNode;
    }

    protected void setConfigurationNode(ConfigurationNode configurationNode) {
        this.configurationNode = configurationNode;
    }

    ConfigurationNode configurationNode;

    /**
     * Sets up the configuration and runtime environment. This is called typically in the constructors before anything
     * else is called. Always call super on this method if you override it! At the end of this method, the configuration
     * node has been found and set for you to call in your {@link #getLoader()} method.
     */
    public void initialize() throws Exception {
        // do not put logging messages (e.g. info) in this method since the actual
        // logger can't be loaded until the configuration file is found. All logging
        // messages before it is loaded will end up in the default log file, not
        // where the user wants them.
        if (!hasConfigFile() || !hasConfigName()) {
            throw new MyConfigurationException("Error: no configuration set");
        }
        setConfigurationNode(XMLConfigUtil.findConfiguration(getConfigFile(), getConfigName(), getComponentName()));
    }


    /**
     * This will be automatically invoked for you if the user supplies a help option. This should
     * just print out to the command line (using the {@link #say(String)} method), rather than logging
     * the help.
     */
    public abstract void help();

    /**
     * Convenience method. This prints out a short message describing the built-in options for this
     * class. Best called at the end of your custom help method.
     * Prints a header (informational line) if you wish.
     */
    public static void defaultHelp(boolean printHeader) {
        if (printHeader) {
            say("Standard options are:");
        }
        say("  -" + LOG_FILE_OPTION + " (-" + LOG_FILE_LONG_OPTION + ") -- set the name of the log file. Default is \"" + DEFAULT_LOG_FILE + "\" in the invocation directory.");
        say("  -" + CONFIG_FILE_OPTION + " (-" + CONFIG_FILE_LONG_OPTION + ") -- the configuration file for this application.");
        say("  -" + VERBOSE_OPTION + " (-" + VERBOSE_LONG_OPTION + ") -- set verbose on. This sends output to the console.");
        say("  -" + DEBUG_OPTION + " (-" + DEBUG_LONG_OPTION + ") -- set debug on. This will print out extra messages \n\t\t\tduring execution to the log file (and console, if verbose is enabled)");
        say("  -" + HELP_OPTION + " (-" + HELP_LONG_OPTION + ") -- this message");
    }

    public boolean hasConfigFile() {
        return configFile != null && configFile.length() > 0;
    }
    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    String configFile;


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
     * Returns true if execution should continue, false if not. Call this first to see if execution should proceed.
     *
     * @param args
     * @return
     * @throws Exception
     */
    protected boolean getOptions(String[] args) {
        InputLine inputLine = new InputLine(getClass().getSimpleName(), args);
        setInputLine(inputLine);
        if (args.length == 0 || inputLine.hasArg(HELP_LONG_OPTION, HELP_LONG_OPTION)) {
            help();
            return false;
        }
        setVerbose(false);

        if (inputLine.hasArg(VERBOSE_OPTION, VERBOSE_LONG_OPTION)) {
            setVerbose(true);
        }

        setDebugOn(false);
        if (inputLine.hasArg(DEBUG_OPTION, DEBUG_LONG_OPTION)) {
            setDebugOn(true);
        }

        if (inputLine.hasArg(LOG_FILE_OPTION, LOG_FILE_LONG_OPTION)) {
            setLogfileName(inputLine.getNextArgFor(LOG_FILE_OPTION, LOG_FILE_LONG_OPTION));
        }

        if (inputLine.hasArg(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION)) {
            setConfigFile(inputLine.getNextArgFor(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION));
        }


        if (inputLine.hasArg(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION)) {
            setConfigName(inputLine.getNextArgFor(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION));
        }

        return true;
    }

    /**
     * Get the original input line.
     * @return
     */
    public InputLine getInputLine() {
        return inputLine;
    }

    public void setInputLine(InputLine inputLine) {
        this.inputLine = inputLine;
    }

    InputLine inputLine;
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    String configName;

public boolean hasConfigName() {
    return configName != null && configName.length() > 0;
}


    /**
     * What this tool should do.
     */

     /**
     * Prints to the console only if verbose is enabled.
     *
     * @param x
     */
    public void sayv(String x) {
        info(x);
    }

    /**
     * Prints to the console.
     *
     * @param x
     */
    public static void say(String x) {
        getIoInterface().println(x);
    }

    /**
     * Prints to the console WITHOUT a carriage return. Useful for imput prompts rather than informational messages.
     *
     * @param x
     */
    public void say2(String x) {
     //   System.out.print(x);
        getIoInterface().print(x);
    }

    public void setMyLogger(MyLoggingFacade myLoggingFacade) {
        logger = myLoggingFacade;

    }

    public MyLoggingFacade getMyLogger() {

        if (logger == null) {
            LoggerProvider loggerProvider = new LoggerProvider(getLogfileName(),
                    "cli logger", 1, 1000000,
                    true, true,  isDebugOn()? Level.FINEST:MyLoggingFacade.DEFAULT_LOG_LEVEL);
            logger = loggerProvider.get();
        }
        return logger;
    }

   transient MyLoggingFacade logger;

    public void debug(String x) {
        if (isVerbose() && isDebugOn()) {
            say(x);
        }
        getMyLogger().debug(x);
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean setOn) {
        this.debugOn = setOn;
    }

    public void info(String x) {
        if (isVerbose()) {
            say(x);
        }
        if(getMyLogger() == null) return;
        getMyLogger().info(x);
    }

    public void warn(String x) {
        if (isVerbose()) {
            say(x);
        }
        if(getMyLogger() == null) return;
        getMyLogger().warn(x);
    }

    public void error(String x) {
        if (isVerbose()) {
            say(x);
        }
        if(getMyLogger() == null) return;
        getMyLogger().error(x);
    }

    public void error(String x, Throwable t) {
        if (isVerbose()) {
            say(x);
        }
        if(getMyLogger() == null) return;
        getMyLogger().error(x,t);
    }

    /**
     * Calls the {@link #initialize()} method and then runs the main {@link #doIt()} method. Call this if you set
     * the target and source in the constructor.
     * @throws Throwable
     */
    public void run() throws Throwable {
        initialize();
        doIt();
    }

    /**
     * Main call. This will grab the command line arguments, run {@link #initialize()}
     * and then invoke {@link #doIt()}.
     *
     * @param args
     * @throws Throwable
     */
    public void run(String[] args) throws Throwable {
        if (!getOptions(args)) {
            return;
        }
        run();
    }


}
