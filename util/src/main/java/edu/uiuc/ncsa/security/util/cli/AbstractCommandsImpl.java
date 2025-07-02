package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.FileUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.Date;
import java.util.Map;

import static edu.uiuc.ncsa.security.util.cli.CLIDriver.EXIT_COMMAND;

/**
 * Class that collectes the startup logic for {@link Commands} implementations
 */
public abstract class AbstractCommandsImpl implements Commands {
    public AbstractCommandsImpl(CLIDriver driver) {
        this.driver = driver;
    }

    public static final String NO_LOGO = "-noLogo";
    public static final String NO_HEADER = "-noHeader";
    public static final String SILENT = "-silent";
    public static final String LOGO = "-logo";
    protected String CL_OUTPUT_FILE_FLAG = "-out";
    protected String CL_INPUT_FILE_FLAG = "-in";

    String ENV_ADD_FLAG = "-add";
    String ENV_OVERWRITE_FLAG = "-overwrite";
    String ENV_JSON_FLAG = "-json";
    String ENV_KEY_FLAG = "-key";
    String ENV_VALUE_FLAG = "-value";


    String curentEnvFile = null;


    /**
     * returns "true if the command has the flag --help in it. This is a cue from the user to show
     * the help for a given function. So it the function is called "X" and its help is in the function
     * "showXHelp" then a value of true from this should simply invoke "showXHelp" and return.
     *
     * @param inputLine
     * @return
     */
    protected boolean showHelp(InputLine inputLine) {
        if (inputLine.hasArg("--help")) return true;
        return false;
    }

    /**
     * <h2>
     * One of the most important help methods, but only used in subclasses.</h2>
     * <p>
     * Use this to get a property and also allow for getting help. This is just {@link #getInput(String, String)}
     * but with the optional lookup of help for the propertyName if the user enters --help or /help. It will then
     * re-prompt for the input.
     *
     * @param propertyName
     * @param prompt
     * @param defaultValue
     * @return
     * @throws IOException
     */
    protected String getPropertyHelp(String propertyName, String prompt, String defaultValue) throws IOException {
        boolean loopForever = true;
        String inLine = null;
        boolean hasNullDefault = (defaultValue == null);
        if (hasNullDefault) {
            defaultValue = CommonCommands2.DEFAULT_NULL_VALUE_PLACEHOLDER;
        } else {
            if (defaultValue.equals(CommonCommands2.DEFAULT_NULL_VALUE_PLACEHOLDER)) {
                defaultValue = "\"" + CommonCommands2.DEFAULT_NULL_VALUE_PLACEHOLDER + "\""; // try to dismabiguate the case where the actual value is --
            }
        }
        while (loopForever) {
            inLine = getInput(prompt, defaultValue);
            if (inLine == null) {
                return inLine; // If the property is not set, then the default value might be null.
            }
            String trimmedLine = inLine.trim();
            if (trimmedLine.startsWith("--help") || trimmedLine.startsWith("/help")) {
                InputLine inputLine = new InputLine(inLine);
                // make sure they are really prompting for help! we don't want to make it impossible to
                // enter something like /help_foo. We have two options to get around collisions.
                if (inputLine.getCommand().equals("--help") || inputLine.getCommand().equals("/help")) {
                    inputLine.appendArg(propertyName);
                    say("-----");
                    if (!getHelpUtil().printHelp(inputLine)) {
                        say("  no help for topic \"" + propertyName + "\"");
                    }
                    say("-----");
                }
            } else {
                break;
            }
        }
        if (hasNullDefault && inLine.equals(CommonCommands2.DEFAULT_NULL_VALUE_PLACEHOLDER)) {
            return null;
        }
        return inLine;
    }

    /**
     * Creates the input prompt and shows the supplied default value. This returns the default if the default value is chosen
     * and the input value otherwise. If supplied the default value is a null, then this is shown too.
     *
     * @param prompt
     * @param defaultValue
     * @return
     */
    protected String getInput(String prompt, String defaultValue) throws IOException {
        String inLine = readline(prompt + "[" + (defaultValue == null ? "(" + DEFAULT_NULL_VALUE_PLACEHOLDER + ")" : defaultValue) + "]:");
        if (StringUtils.isTrivial(inLine)) {
            // assumption is that the default value is required
            return defaultValue; // no input. User hit a return
        }
        return inLine;
    }

    // If a null valus is passted toa  prompt, replace it with this so the user knows the value is not
    // the string "null" or some such.
    protected static String DEFAULT_NULL_VALUE_PLACEHOLDER = "--";

    protected String readline(String prompt) throws IOException {
        return getIOInterface().readline(prompt);
    }

    protected String readline() {
        try {
            //String x = getBufferedReader().readLine();
            String x = getIOInterface().readline();
            if (x.equals(EXIT_COMMAND)) {
                throw new ExitException(EXIT_COMMAND + " encountered");
            }
            return x;

        } catch (IOException iox) {
            throw new GeneralException("Error, could not read the input line due to IOException", iox);
        }
    }

    public CLIDriver getDriver() {
        return driver;
    }

    public void setDriver(CLIDriver driver) {
        this.driver = driver;
    }

    transient CLIDriver driver;

    public InputLine bootstrap(String[] args) throws Throwable {
        return bootstrap(new InputLine(getClass().getSimpleName(), args));
    }

    /**
     * Bootstrap this implementation. This calls {@link CLIDriver#bootstrap(String[])}.
     * The sequence this follows is
     * <ol>
     *     <li>call {@link CLIDriver#bootstrap(InputLine)}</li>
     *     <li>Turn off console ouput (loggin unaffected) if not verbose mode</li>
     *     <li>Call {@link #load(InputLine)}</li>
     *     <li>Turn back on output</li>
     *     <li>Show header with {@link #about(boolean, boolean)}</li>
     *     <li>Call {@link #initialize()}</li>
     *     <li>Call {@link #initHelp()}</li>
     * </ol>
     * The reason output is turned off is to allow for loading components, libraries that may or may
     * not play so nicely and spit out tons of confusing messages to the user. If verbose is enabled,
     * everything is printed.
     * @param inputLine
     * @throws Throwable
     */
    @Override
    public InputLine bootstrap(InputLine inputLine) throws Throwable {
        inputLine = getDriver().bootstrap(inputLine); // has to be first to get IO right.
        IOInterface ioInterface = null;
        PrintStream out = System.out;
        PrintStream err = System.err;
        if (!getDriver().isVerbose()) {
            ioInterface = getIOInterface();
            BasicIO basicIO = new BasicIO();
            getDriver().setIOInterface(basicIO);
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));
            System.setErr(new PrintStream(OutputStream.nullOutputStream()));
        }
        try {
            load(inputLine);
        }catch(Throwable t) {
            if(getDriver().isVerbose()){
                t.printStackTrace();
            }
            warn("Error loading " + t.getMessage());
        }
        if (ioInterface != null) {
            System.setOut(out);
            System.setErr(err);
            getDriver().setIOInterface(ioInterface);
        }
        // now do header/logos setup
        setShowLogo(!inputLine.hasArg(NO_LOGO));
        setShowHeader(!inputLine.hasArg(NO_HEADER));
        if (inputLine.hasArg(SILENT)) {
            setShowHeader(false);
            setShowLogo(false);
        }
        if (inputLine.hasArg(LOGO)) {
            setLogoName(inputLine.getNextArgFor(LOGO).toLowerCase());
        }
        about(isShowLogo(), isShowHeader());
        initialize();
        initHelp();
        return inputLine;
    }

    /**
     * Show the header, if any, at startup?
     *
     * @return
     */
    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    /**
     * Show the logo, if any, at startup?
     *
     * @return
     */
    public boolean isShowLogo() {
        return showLogo;
    }

    public void setShowLogo(boolean showLogo) {
        this.showLogo = showLogo;
    }

    protected boolean showHeader = true;
    protected boolean showLogo = true;

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    protected String logoName = null;

    /**
     * This will show the boot up logos, header (additional info) etc. if any
     */
    abstract public void about(boolean showBanner, boolean showHeader);

    /**
     * Finish initializing this implementation.
     *
     * @throws Throwable
     */
    abstract public void initialize() throws Throwable;

    /**
     * Load resources before showing logos etc. This is typically called on your behalf in {@link #bootstrap(InputLine)}
     * amd should load resources needed. Note that an exception in this does not stop booting. This allows you to
     * start a system with a missing configuration file (for instnace) and prompt for it.
     * <br/><br/>
     * See {@link #initialize()} which is called after this and is charged with actually starting
     *
     * @param inputLine
     * @throws Throwable
     */
    abstract public void load(InputLine inputLine) throws Throwable;

    protected boolean hasLogger() {
        return getDriver().getLogger() != null;
    }

    @Override
    public boolean isDebugOn() {
        return getDriver().isTraceOn();
    }

    @Override
    public void setDebugOn(boolean setOn) {
        // no op
    }

    @Override
    public void debug(String x) {
        if (hasLogger()) {
            getDriver().getLogger().debug(x);
        }
    }

    @Override
    public void info(String x) {
        if (hasLogger()) {
            getDriver().getLogger().info(x);
        }
    }

    @Override
    public void warn(String x) {
        if (hasLogger()) {
            getDriver().getLogger().warn(x);
        }
    }

    @Override
    public void error(String x) {
        if (hasLogger()) {
            getDriver().getLogger().error(x);
        }
    }
    public void error(String x, Throwable t) {
        if (hasLogger()) {
            getDriver().getLogger().error(x,t);
        }
    }
    /**
     * Used when reading lines to check is the user typed a "y" for yes.
     *
     * @param x
     * @return
     */
    protected boolean isOk(String x) {
        if (x == null || x.length() == 0) return false;
        return x.trim().equalsIgnoreCase("y");
    }


    public HelpUtil getHelpUtil() {
        return getDriver().getHelpUtil();
    }

    protected void initHelp() throws Throwable {
        getHelpUtil().load("/common_commands_help.xml");
        getHelpUtil().load("/meta_command_help.xml");
    }

    protected void printReadEnvHelp() {
        say("read_env [" + ENV_ADD_FLAG + "] " + CL_INPUT_FILE_FLAG + " file");
        sayi("This takes a properties file of key/value pairs and stores them for use.");
        sayi("You may access these values in any command with a reaplacement template ${key} is replaced by its value");
        sayi("This allows you to set environment variables externally and manage them.");
        sayi(ENV_ADD_FLAG + " will simply add the properties to any existing properties rather than over-writing them");
        sayi("This lets you pull in many properties from differen sources. Not adding this flag replaces the current environment.");
        sayi(ENV_OVERWRITE_FLAG + " Implies the add flag. This means that the new properties overwrite the old");
        sayi("Otherwise, the old properties are preserved.");
        sayi(ENV_JSON_FLAG + " The properties are in a JSON object, rather than just a flat file of key=value pairs.");
        sayi("Only strings are supported and there is no guarantee as to behavior if other objects are used.");
        say("See also: set_env, save_env, clear_env, print_env");
    }

    protected void printSetEnvHelp() {
        say("set_env [" + ENV_KEY_FLAG + " key " + ENV_VALUE_FLAG + " value");
        sayi("Set a single environment value for a given key.");
        sayi("E.g.");
        sayi("set_env " + ENV_KEY_FLAG + " foo " + ENV_VALUE_FLAG + " \"The quick brown fox\"");
        sayi("would result in an environment variable named \"foo\" with the phrase \"the quick brown fox\"");
        sayi("as its value.");
        say("See also: set_env, clear_env");
    }

    public void set_env(InputLine inputline) throws Exception {
        if (showHelp(inputline)) {
            printSetEnvHelp();
            return;
        }
        if (!inputline.hasArg(ENV_KEY_FLAG)) {
            say("Sorry, no key flag specified.");
            return;
        }
        if (!inputline.hasArg(ENV_VALUE_FLAG)) {
            say("Sorry, no value flag specified.");
            return;
        }
        getDriver().getEnv().put(inputline.getNextArgFor(ENV_KEY_FLAG), inputline.getNextArgFor(ENV_VALUE_FLAG));

    }

    protected void printGetEnvHelp() {
        say("get_env [" + ENV_KEY_FLAG + "] key ");
        sayi("Gets a single environment value for a given key. The flag is optional.");
        sayi("E.g.\n\n");
        sayi("get_env " + ENV_KEY_FLAG + " foo ");
        sayi("the quick brown fox");
        sayi("\n\nwhich is the value stored in \"foo\"");
        say("See also: set_env, clear_env");
    }

    public void get_env(InputLine inputline) throws Exception {
        if (showHelp(inputline)) {
            printGetEnvHelp();
            return;
        }
        String key = inputline.getLastArg();
        if (inputline.hasArg(ENV_KEY_FLAG)) {
            key = inputline.getNextArgFor(ENV_KEY_FLAG);
        }
        Object r = getDriver().getEnv().get(key);
        if (r == null) {
            say("value not found for \"" + key + "\"");
            return;
        }
        say(r.toString());
    }

    public void read_env(InputLine inputline) throws Exception {
        if (showHelp(inputline)) {
            printReadEnvHelp();
            return;
        }
        if (gracefulExit(!inputline.hasArg(CL_INPUT_FILE_FLAG), "Missing properties file: no " + CL_INPUT_FILE_FLAG + " switch.")) {
            return;
        }
        Map map = null;
        String inputFile = inputline.getNextArgFor(CL_INPUT_FILE_FLAG);
        curentEnvFile = inputFile;
        File f = new File(inputFile);
        if (!f.isFile()) {
            sayv("Sorry but the file \"" + f + "\" does not exist.");
            return;
        }
        if (inputline.hasArg(ENV_JSON_FLAG)) {
            sayv("loading json properties file");
            try {
                map = readJSON(inputFile);
            } catch (Throwable e) {
                if (getDriver().isTraceOn()) {
                    e.printStackTrace();
                }
                say("Could not read json properties file:" + e.getMessage());
                return;
            }
        } else {
            sayv("loading Java properties file");
            map = new XProperties(f);
        }
        sayv("loaded properties: " + map);
        if (inputline.hasArg(ENV_ADD_FLAG)) {
            Map oldProps = getDriver().getEnv();
            if (oldProps != null) {
                XProperties xp = new XProperties();
                xp.add(oldProps, false);

                xp.add(map, inputline.hasArg(ENV_OVERWRITE_FLAG));
                map = xp;
            }
        }
        getDriver().setEnv(map);
    }

    protected void printSaveEnvHelp() {
        say("save_env " + CL_OUTPUT_FILE_FLAG + " filePath + [" + ENV_JSON_FLAG + "]");
        sayi("Write the current environment to the given file. If you specify the " + ENV_JSON_FLAG);
        sayi("the contents of the file will be a JSON object. Otherwise, this will be a java properties file");
        say("See also read_env, set_env");
    }

    public void save_env(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            printSaveEnvHelp();
            return;
        }
        String envFile = curentEnvFile;
        if (inputLine.hasArg(CL_OUTPUT_FILE_FLAG)) {
            envFile = inputLine.getNextArgFor(CL_OUTPUT_FILE_FLAG);
        }

        if (!getDriver().hasEnv()) {
            // nix to do
            return;
        }
        if (envFile == null) {
            say("Sorry, no file specified.");
            return;
        }
        File f = new File(envFile);
        boolean isJSON = inputLine.hasArg(ENV_JSON_FLAG);

        if (isJSON) {
            JSONObject json = new JSONObject();
            json.putAll(getDriver().getEnv());
            String out = json.toString(2);
            FileWriter fileWriter = new FileWriter(f);
            fileWriter.write(out);
            fileWriter.flush();
            fileWriter.close();
        } else {
            XProperties xp = new XProperties();
            xp.putAll(getDriver().getEnv());
            FileOutputStream fos = new FileOutputStream(f);
            xp.store(fos, "OA4MP environment serialized on " + (new Date()));
            fos.flush();
            fos.close();
        }
        say("stored env file to \"" + f.getAbsolutePath() + "\"");
    }

    protected void printEnvHelp() {
        say("print_env ");
        sayi("This will print out the current list of environment variables, '(empty)' if there are none.");
        say("See also: set_env, clear_env");
    }

    public void print_env(InputLine inputLine) throws Exception {
        if (getDriver().hasEnv()) {
            if (curentEnvFile == null) {
                say("no current default environment file");
            } else {
                say("current environment file is:" + curentEnvFile);
            }
            XProperties xProperties = new XProperties();
            xProperties.putAll(getDriver().getEnv());
            say(xProperties.toString(2));
        } else {
            say("(empty)");
        }
    }

    protected void clearEnvHelp() {
        say("clear_env [" + ENV_KEY_FLAG + " key]");
        sayi("clear a specific environment variable. If there is no argument, it will clear all values.");
        say("See also: set_env, print_env");
    }

    public void clear_env(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            clearEnvHelp();
            return;
        }
        if (inputLine.hasArg(ENV_KEY_FLAG)) {
            getDriver().getEnv().remove(inputLine.getNextArgFor(ENV_KEY_FLAG));
            return;
        }
        getDriver().setEnv(null);
    }

    /**
     * Exit gracefully. That is to say, if the exitNow flag is true, do a system shutdown in batch mode and otherwise,
     * do nothing. If the flag is true and the mode is interactive, then print the message and return true, prompting
     * the system to return;
     *
     * @param exitNow
     * @param msg
     * @return
     */
    protected boolean gracefulExit(boolean exitNow, String msg) {
        if (exitNow) {
            say(msg);
            return true;
        }
        return false;
    }

    /**
     * Prints with the default indent and a linefeed.
     *
     * @param x
     */
    protected void say(String x) {
        if (getDriver().isOutputOn()) {
            getIOInterface().println(defaultIndent + x);
        }
    }

    /**
     * Linefeed.
     */
    protected void say() {
        say("");
    }

    /**
     * Use this for verbose mode.
     *
     * @param x
     */
    protected void sayv(String x) {
        // suppress output if this is run from the command line.
        if (getDriver().isOutputOn() && getDriver().isVerbose()) {
            say(x);
        }
    }

    protected JSONObject readJSON(String filename) throws Throwable {
        return JSONObject.fromObject(FileUtil.readFileAsString(filename));
    }

    /**
     * prints with the current indent and a linefeed.
     *
     * @param x
     */
    protected void sayi(String x) {
        say(INDENT + x);
    }

    protected String defaultIndent = "";

    public int indentWidth() {
        return defaultIndent.length();
    }

    public static final String INDENT = "  "; // use this in implementations for consistent indenting.}

    @Override
    public IOInterface getIOInterface() {
        return getDriver().getIOInterface();
    }
}