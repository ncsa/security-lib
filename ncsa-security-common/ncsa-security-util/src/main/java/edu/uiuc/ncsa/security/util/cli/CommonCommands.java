package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.LoggingConfigLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.Date;
import java.util.Map;

import static edu.uiuc.ncsa.security.util.cli.CLIDriver.EXIT_COMMAND;

/**
 * Utilities that are used by any reasonable implementation of the Commands
 * interface.   You will probably want to extend this for your command processor.
 * <p>Created by Jeff Gaynor<br>
 * on 10/30/13 at  4:14 PM
 */
public abstract class CommonCommands implements Commands {
    boolean batchMode = false;

    /**
     * If this is invoked from the command line with the batch mode flag, then this should process
     * commands strictly without user intervention, failing if, for instance, some parameters are missing
     * rather than prompting for them. Typically, the {@link CLIDriver} instance sets this at the time of
     * invocation.
     *
     * @return
     */
    public boolean isBatchMode() {
        return batchMode;
    }

    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
    }

    public static String BATCH_MODE_FLAG = "-batch";


    protected CommonCommands(MyLoggingFacade logger) {
        this.logger = logger;
    }

    protected MyLoggingFacade logger;

    @Override
    public void debug(String x) {
        logger.debug(x);
    }

    @Override
    public void error(String x) {
        logger.error(x);
    }

    @Override
    public void info(String x) {
        logger.info(x);
    }

    @Override
    public boolean isDebugOn() {
        return logger.isDebugOn();
    }

    @Override
    public void setDebugOn(boolean setOn) {
        logger.setDebugOn(setOn);
    }

    @Override
    public void warn(String x) {
        logger.warn(x);
    }

    protected String defaultIndent = "";

    public int indentWidth() {
        return defaultIndent.length();
    }

    public static final String INDENT = "  "; // use this in implementations for consistent indenting.

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

    public IOInterface getIoInterface() {
        if(ioInterface == null){
            ioInterface = new BasicIO();
        }
        return ioInterface;
    }

    public void setIoInterface(IOInterface ioInterface) {
        this.ioInterface = ioInterface;
    }

    IOInterface ioInterface;
    protected String readline(String prompt) throws IOException {
                                          return getIoInterface().readline(prompt);
    }
    protected String readline() {
        try {
            String x = getBufferedReader().readLine();
            if (x.equals(EXIT_COMMAND)) {
                throw new ExitException(EXIT_COMMAND + " encountered");
            }
            return x;

        } catch (IOException iox) {
            throw new GeneralException("Error, could not read the input line due to IOException", iox);
        }
    }

    @Override
    public void print_help(InputLine inputLine) throws Exception{
        say("All commands have detailed help by typing:");
        say("command --help");
        say("--Environment commands: these control variables managed by this component");
        say("You may access these by enclosing them in delimters, e.g. ${var}");
        sayi("clear_env = clear the environment");
        sayi("print_env = print all variables in the environment");
        sayi("read_env = read either a properties or JSON file contain key value pairs ");
        sayi("save_env = write the current environment to a file for later use.");
        sayi("set_env = set a variable for use");

        say("--Other commands:");
        sayi("print_help = print this help out");
        sayi("set_output_on = turn on/off all output (used mostly in batch files)");
        sayi("set_verbose_on = turn verbose off or on");
        sayi("echo = print the argument. Useful in batch scripts.");
        sayi("version = the version of this component.");

    }

    /**
     * Prints with the default indent and a linefeed.
     *
     * @param x
     */
    protected void say(String x) {
        if (isPrintOuput()) {
            getIoInterface().println(defaultIndent + x);
        }
    }

    /**
     * prints with the current indent and a linefeed.
     *
     * @param x
     */
    protected void sayi(String x) {
        say(INDENT + x);
    }

    /**
     * Double indent -- useful for lists.
     *
     * @param x
     */
    protected void sayii(String x) {
        say(INDENT + INDENT + x);
    }

    /**
     * Output the string without any linefeed. This is used for prompts.
     *
     * @param x
     */
    protected void say2(String x) {
        getIoInterface().print(defaultIndent + x);
    }


  /*  protected void sayi2(String x) {
        say2(INDENT + x);
    }*/

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

    protected boolean isOk(String x) {
        if (x == null || x.length() == 0) return false;
        return x.trim().toLowerCase().equals("y");
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
        //sayi2(prompt + "[" + (defaultValue == null ? "(null)" : defaultValue) + "]:");
      //  sayi2(prompt + "[" + (defaultValue == null ? "(null)" : defaultValue) + "]:");
        String inLine = readline(prompt + "[" + (defaultValue == null ? "(null)" : defaultValue) + "]:");
        if (isEmpty(inLine)) {
            // assumption is that the default value is required
            return defaultValue; // no input. User hit a return
        }
        return inLine;
    }

    protected boolean isEmpty(String x) {
        return x == null || x.length() == 0;
    }


    /**
     * Gets the placeholder for missing values. E.g. if a value (like a last name) is missing this will be displayed.
     *
     * @return
     */
    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    /**
     * This is used wherever a missing value is.
     */
    public String placeHolder = "-";

    /**
     * Returns the value if it is not empty of a placeholder if it is.
     *
     * @param x
     * @return
     */
    protected String getValue(String x) {
        return isEmpty(x) ? getPlaceHolder() : x;
    }

    public CLIDriver getDriver() {
        return driver;
    }

    public void setDriver(CLIDriver driver) {
        this.driver = driver;
    }

    CLIDriver driver;
    protected String CL_OUTPUT_FILE_FLAG = "-out";
    protected String CL_INPUT_FILE_FLAG = "-in";

    String ENV_ADD_FLAG = "-add";
    String ENV_OVERWRITE_FLAG = "-overwrite";
    String ENV_JSON_FLAG = "-json";
    String ENV_KEY_FLAG = "-key";
    String ENV_VALUE_FLAG = "-value";


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
        if(inputline.hasArg(ENV_KEY_FLAG)){
            key = inputline.getNextArgFor(ENV_KEY_FLAG);
        }
        Object r = getDriver().getEnv().get(key);
        if(r == null){
            say("value not found for \"" + key + "\"");
            return;
        }
                   say(r.toString());
    }

    String curentEnvFile = null;

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
            map = readJSON(inputFile);
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
        say("stored env file to \"" + f.getAbsolutePath()+"\"");
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
            if (isBatch()) {
                sayv(msg);
                System.exit(1);
            }
            say(msg);
            return true;
        }
        return false;
    }

    /* If this is used, then each line of the file is read as an input and processed. It overrides the
     * {@link #BATCH_MODE_FLAG} if used and that is ignored.
     */
    public static String BATCH_FILE_MODE_FLAG = "-batchFile";
    /**
     * If a line contains this character, then the line is truncated at that point before processing.
     */
    //public static String BATCH_FILE_COMMENT_CHAR = "//";
    /**
     * If a line ends with this (after the comment is removed), then glow it on to the
     * next input line. In effect this lets you split commands across multiple lines, e.g.
     * <pre>
     * ls \//My comment
     * -la \
     * foobar
     * </pre>
     * is the same as entering the single line
     * <pre>ls -la foobar</pre>
     * Notice that the lines are concatenated and the comment is stripped out.
     */
    public static String BATCH_FILE_LINE_CONTINUES = "\\";

    public boolean isBatchFile() {
        return batchFile;
    }

    public void setBatchFile(boolean batchFile) {
        this.batchFile = batchFile;
    }

    protected boolean batchFile = false;

    /**
     * returns true if this is either a batch file or in batch mode. In that case only output is generated
     * on errors if verbosity is on.
     *
     * @return
     */
    protected boolean isBatch() {
        return isBatchFile() || isBatchMode();
    }

    /**
     * Use this for verbose mode.
     *
     * @param x
     */
    protected void sayv(String x) {
        // suppress output if this is run from the command line.
        logit(x);
        if (isPrintOuput() && isVerbose()) {
            say(x);
        }
    }

    protected void logit(String x) {
        if (logger != null) {
            logger.info(x);
        }
    }


    public boolean isVerbose() {
        return verbose;
    }

    protected void setVerboseHelp() {
        say("set_verbose_on true | false ");
        sayi("Chiefly for batch files to toggle whether or not there is verbose mode on.");
        say("See also set_output_on");
    }

    /**
     * So batch files can change whether or not they are verbose
     *
     * @param inputLine
     */
    public void set_verbose_on(InputLine inputLine) throws Exception {
        if (inputLine.hasArg("true")) {
            setVerbose(true);
        } else {
            setVerbose(false);
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    boolean verbose;

    /**
     * If this is set true, then no output is generated. This is usedul in batch mode or with a batch file.
     *
     * @return
     */
    public boolean isPrintOuput() {
        return printOuput;
    }

    public void setPrintOuput(boolean printOuput) {
        this.printOuput = printOuput;
    }

    boolean printOuput = true; // default is to always print output since this a command line tool.


    protected void setOuputOnHelp() {
        say("set_output_on true| false");
        sayi("This is used chiefly with batch files, to toggle whether or not there is output directed to the console,");
        sayi("true means to turn on output, false means to suspend it");
        say("See also: set_verbose_on");
    }

    public void set_output_on(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            setOuputOnHelp();
            return;
        }
        // A little bit trickier than it looks since we have an internal flag for the negation of this.
        // We also want to be sure they really want to turn off output, so we only test for logical true
        // That way if they screw it up they still at least get output...
        // Also, this will only set the output to off if it is explicitly told to, otherwise users can shut it
        // off by accident and not get any output and not know why.
        if (inputLine.hasArg("false")) {
            logit("Turning output off");
            setPrintOuput(false);
        } else {
            logit("Turning output on");
            setPrintOuput(true);
        }
    }


    protected void versionHelp() {
        say("version ");
        sayi("prints the current version number of this program.");
    }

    public void version(InputLine inputLine) {
        if (showHelp(inputLine)) {
            versionHelp();
            return;
        }
        say("* CLI (Command Line Interpreter) Version " + LoggingConfigLoader.VERSION_NUMBER);
    }

    protected void echoHelp() {
        say("echo arg0 arg1...");
        sayi("Simply echos the arg(s) to the console . This is extremely useful in scripts.");
        say("See also set_output_on");
    }

    public void echo(InputLine inputLine) {
        if (showHelp(inputLine)) {
            echoHelp();
            return;
        }
        say(inputLine.format());
    }

    /**
     * read a text file and return a single string of the content. This is intended for small files
     * which will be processed into something else (e.g. turned into a JSON object, key set etc.)
     *
     * @param filename
     * @return
     * @throws Exception
     */
    protected String readFile(String filename) throws Exception {
        File f = new File(filename);
        if (!f.exists()) {
            return null;
        }
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String output = "";
        String currentLine = br.readLine();
        while (currentLine != null) {
            output = output + currentLine;
            currentLine = br.readLine();
        }
        br.close();
        return output;
    }

    protected JSONObject readJSON(String filename) throws Exception {
        return JSONObject.fromObject(readFile(filename));
    }
}
