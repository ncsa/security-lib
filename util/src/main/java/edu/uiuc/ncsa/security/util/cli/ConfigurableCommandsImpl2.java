package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Basic implementation of Commands that supports loading configurations.
 * It is intended as a the superclass for commands that have multiple components.
 * <b>NOTE</b> This does not actually run commands! It is a top-level class that delegates to its
 * command implementations (such as for stores, keys). Therefore, you should not put code here
 * that actually executes things. The contract of this is that it manages logging, loading configurations
 * and instantiating the actual {@link Commands} implementations that do everything.
 * <p>Created by Jeff Gaynor<br>
 * on 5/20/13 at  11:35 AM
 */
public abstract class ConfigurableCommandsImpl2 extends AbstractCommandsImpl {
    public ConfigurableCommandsImpl2(CLIDriver driver) {
        super(driver);
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
        // These override the like-named class variables so if there is a loading error,
        // current configuration is not munged. If the load works, then loadConfig resets
        // the class variables.
        String fileName = null;
        String configName = null;

        switch (inputLine.getArgCount()) {
            case 0: // query current config
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
            case 1: // either -list or set the name
                if (inputLine.hasArg(LIST_CFGS)) {
                    listConfigs(inputLine);
                    // no way to return a value and have it findable with introspection (since signature would change).
                    // Only way to send notification.
                    return;
                    //  throw new ListOnlyNotification();
                }
                if (StringUtils.isTrivial(getConfigFile())) {
                    say("no config file set");
                    return;
                }
                configName = inputLine.getArg(1);
                loadConfig(getConfigFile(), configName);
                say("loaded " + configName);
                return;
            case 2:
                if (inputLine.hasArg(LIST_CFGS)) {
                    listConfigs(inputLine);
                    // no way to return a value and have it findable with introspection (since signature would change).
                    // Only way to send notification.
                    throw new ListOnlyNotification();
                }
                configName = inputLine.getArg(1);
                fileName = inputLine.getArg(2);
                break;
            default:
                if (inputLine.hasArg(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION)) {
                    fileName = inputLine.getNextArgFor(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION);
                    inputLine.removeSwitchAndValue(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION);
                }
                if (inputLine.hasArg(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION)) {
                    configName = inputLine.getNextArgFor(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION);
                    inputLine.removeSwitchAndValue(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION);
                }
        }
        if (fileName == null) {
            say("no configuration file specified");
            return;
        }
        if (configName == null) {
            say("no configuration name specified");
            return;
        }

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

        sayv("loading configuration from " + fileName + ", named " + configName);
        loadConfig(fileName, configName);
        sayv("done!");
    }

    boolean traceOn = false;

/*
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
*/

    protected String CFG_FILE_SWITCH = "-cfg";

    protected void listConfigs(InputLine inputLine) throws Exception {

        String targetFilename = getConfigFile();
        if (inputLine.hasArg(CFG_FILE_SWITCH)) {
            targetFilename = inputLine.getNextArgFor(CFG_FILE_SWITCH);
            inputLine.removeSwitchAndValue(CFG_FILE_SWITCH);
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
        say("load  = list the currently set configurations");
        say("load config_name = Load the named configuration from the current file");
        say("load config_name config_file= Set the configuration file, then load the named configuration");
        say("load " + LIST_CFGS + "= list all configurations in the current file");
        say("load " + LIST_CFGS + " config_file= list all configurations in the given file. Does not change configuration file.");
        say("You can set these using the positions of the arguments. Optionally you can also designate them with switches:");
        say("load [" + CONFIG_FILE_OPTION + "|" + CONFIG_FILE_LONG_OPTION + "] config_file + [" +
                CONFIG_NAME_OPTION + " | " + CONFIG_NAME_LONG_OPTION + " ] config_name");
        say("The advantage of flags is that order does not matter. Note that if you use switches, you must use both.");
        say("\nExample\n");
        say("   load default /var/www/config/config.xml \n");
        say("loads the configuration named \"default\" from the file named \"config.xml\" in the directory \"/var/www/config\"\n");
        say("Note that after a load, any new configuration file becomes the default for future load operations.");
        say("Or the equivalent");
        say("   load " + CONFIG_NAME_OPTION + " default " + CONFIG_FILE_OPTION + " /var/www/config/config.xml \n");

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


    /**
     * For the configuration. This is the tag name (e.g. "client" or "server") in the XML file.
     * Note this is used by subclasses in other projects.
     *
     * @return
     */
    public abstract String getComponentName();

    public abstract ConfigurationLoader<? extends AbstractEnvironment> getLoader();

    public abstract void setLoader(ConfigurationLoader<? extends AbstractEnvironment> loader);

    public static final String CONFIG_FILE_OPTION = "-cfg";
    public static final String CONFIG_FILE_LONG_OPTION = "-configFile";

    public static final String CONFIG_NAME_OPTION = "-name";
    public static final String CONFIG_NAME_LONG_OPTION = "-configName";

    @Override
    public void initHelp() throws Throwable {
        super.initHelp();
        getHelpUtil().load("/cci_help.xml");

    }
/*
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

*/


    /**
     * Called at initialization to read and process the command line arguments.
     * NOTE that this is not called in this class, but by inheritors.
     */
    public void initialize() {
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

    /**
     * Loads the configuration and sets the filename and configuration given as the current ones.
     *
     * @param filename
     * @param configName
     * @throws Throwable
     */
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
        if (args.hasArg(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION)) {
            setConfigFile(args.getNextArgFor(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION));
            args.removeSwitchAndValue(CONFIG_FILE_OPTION, CONFIG_FILE_LONG_OPTION);
        }
        if (args.hasArg(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION)) {
            setConfigName(args.getNextArgFor(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION));
            args.removeSwitchAndValue(CONFIG_NAME_OPTION, CONFIG_NAME_LONG_OPTION);
        }
        return super.bootstrap(args);
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

    protected boolean switchOrRun(InputLine inputLine, CommonCommands2 commands) {
        boolean switchComponent = 1 < inputLine.getArgCount();

        if(!commandStack.isEmpty()){
            CLIDriver currentDriver = commandStack.peek();
            if (currentDriver.getCLICommands().length == 1) {
                if (currentDriver.getCLICommands()[0].getName().equals(commands.getName())) {
                    // already currently running.
                    return true;
                }
            }
        }

        // not in stack, add it and start.
        CLIDriver cli = commands.getDriver();
/*        CLIDriver cli = new CLIDriver();
        cli.setIOInterface(getIOInterface());
        cli.getHelpUtil().addHelp(getHelpUtil());
        cli.addCommands(commands);
        //cli.setEnv(getGlobalEnv());
        if (this instanceof ComponentManager) {
            cli.setComponentManager((ComponentManager) this);
        }*/
        commandStack.push(cli);
        if (switchComponent) {
            inputLine.removeArgAt(0); // removes original arg ("use")
            cli.execute(inputLine.removeArgAt(0)); // removes components before executing
        } else {
            cli.start();
        }
        commandStack.pop(); // remove it on exit
        return true;
    }


    /**
     * Zeroth element of the command stack is the root instance of this class. The should
     * never be empty.
     */
    Stack<CLIDriver> commandStack = new Stack<>();

    /**
     * Back reference to the driver for this class
     *
     * @param driver
     */
    @Override
    public void setDriver(CLIDriver driver) {
        commandStack.push(driver);
        super.setDriver(driver);
/*        if (driver.getLogger() != null) {
            setMyLogger(driver.getLogger());
        }*/
    }

}
