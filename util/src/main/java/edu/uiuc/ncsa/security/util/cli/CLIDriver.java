package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.LoggerProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.batch.DDParser;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import edu.uiuc.ncsa.security.util.terminal.ISO6429IO;
import edu.uiuc.ncsa.security.util.terminal.ISO6429Terminal;
import net.sf.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

import static edu.uiuc.ncsa.security.util.cli.CLIReflectionUtil.invokeMethod;

/**
 * A driver program that does introspection on a set of CLICommands
 * <p>Created by Jeff Gaynor<br>
 * on 5/17/13 at  3:01 PM
 */
public class CLIDriver {
    /**
     * If a user enters this string at any point, the current operation should end. An ExitException
     * is thrown.
     */
    public static final String EXIT_COMMAND = "/exit";
    public static final String ECHO_COMMAND = "/echo";

    public CLIDriver() {
    }

    public String getLineCommentStart() {
        return lineCommentStart;
    }

    public void setLineCommentStart(String lineCommentStart) {
        this.lineCommentStart = lineCommentStart;
    }

    protected String lineCommentStart = "#";

    /**
     * If a comment delimiter has been set for this component. If so, then lines that
     * start with this are ignored.
     *
     * @return
     */
    public boolean hasComments() {
        return lineCommentStart != null;
    }

    public static final int OK_RC = 0;
    public static final int ABNORMAL_RC = -1;
    public static final int USER_EXIT_RC = 10;
    public static final int SHUTDOWN_RC = -10;
    public static final int HELP_RC = 100;
    public static final String HELP_SWITCH = "--help";


    public List<String> getCommandHistory() {
        return commandHistory;
    }

    public void setCommandHistory(List<String> commandHistory) {
        this.commandHistory = commandHistory;
    }

    List<String> commandHistory = new LinkedList<>();
    public static final String PRINT_HELP_COMMAND = "/?";
    public static final String CLEAR_BUFFER_COMMAND = "/c";
    public static final String LOAD_BUFFER_COMMAND = "/l";
    public static final String HISTORY_LIST_COMMAND = "/h";
    public static final String SHORT_EXIT_COMMAND = "/q";
    public static final String REPEAT_LAST_COMMAND = "/r";
    public static final String WRITE_BUFFER_COMMAND = "/w";
    public static final String COMPONENT_COMMAND = "//"; // For handing off *IF* there is a component manager.
    public static final String LIST_ALL_METHODS_COMMAND = "/commands";
    public static final String TRACE_COMMAND = "/trace";
    public static final String ONLINE_HELP_COMMAND = "/help";

    /*
    Added Environment support. This allows for having any extension od CommonCommands use the set_env call
    to pull in properties from either a Java properties file or from a JSON file. Type "set_env --help" at the command
    line to see what's what.
     */
    Map env;

    public Map getEnv() {
        if (env == null) {
            env = new XProperties();
        }
        return env;
    }

    public void setEnv(Map env) {
        this.env = env;
    }

    public boolean hasEnv() {
        return env != null && !env.isEmpty();
    }

    private Commands[] commands; // implementation of this abstract class.
    CommandLineTokenizer CLT = new CommandLineTokenizer();

    // couple of internal flags.
    boolean isDone = false;
    boolean debug = false;   // This is set manually here.

    public CLIDriver(Commands... cci) {
        super();
        addCommands(cci);
    }


    public CLIDriver(IOInterface ioInterface) {
        setIOInterface(ioInterface);
    }

    public void addCommands(Commands... cci) {
        for (Commands xxx : cci) {
            // Set exactly one instance of the component manager
            // This should be the zero-th element in cci
            if (xxx instanceof ComponentManager && componentManager == null) {
                componentManager = (ComponentManager) xxx;
            }
            if (xxx instanceof CommonCommands) {
                CommonCommands commonCommands = ((CommonCommands) xxx);
                commonCommands.setVerbose(isVerbose());
                commonCommands.setPrintOuput(!isOutputOn());
            }
            //xxx.setIOInterface(getIOInterface());
            xxx.setDriver(this);

        }
        setCLICommands(cci);

    }

    public Commands[] getCLICommands() {
        return commands;
    }

    protected void setCLICommands(Commands[] commands) {
        this.commands = commands;
    }


    protected String readline(String prompt) throws IOException {
        return getIOInterface().readline(prompt);
    }

    protected static final int USER_DEFINED_COMMAND = -1;
    protected static final int NO_OP_VALUE = 0;
    protected static final int REPEAT_COMMAND_VALUE = 10;
    protected static final int HISTORY_COMMAND_VALUE = 20;
    protected static final int WRITE_BUFFER_COMMAND_VALUE = 30;
    protected static final int READ_BUFFER_COMMAND_VALUE = 40;
    protected static final int CLEAR_BUFFER_COMMAND_VALUE = 50;
    protected static final int SHORT_EXIT_COMMAND_VALUE = 60;
    protected static final int PRINT_HELP_COMMAND_VALUE = 70;
    protected static final int LIST_ALL_METHODS_COMMAND_VALUE = 80;
    protected static final int TRACE_COMMAND_VALUE = 90;
    protected static final int COMPONENT_COMMAND_VALUE = 100;

    public static final int ONLINE_HELP_COMMAND_VALUE = 110;



    protected void printHelp() {
        String indent = "  ";
        // This is help for the built-in commands here
        say("--General commands:");
        say(indent + EXIT_COMMAND + " or " + SHORT_EXIT_COMMAND + " = exit this component");
        say(indent + PRINT_HELP_COMMAND + " = print this help");
        say(indent + LIST_ALL_METHODS_COMMAND + " = list all of the currently available commands.");
        say(indent + TRACE_COMMAND + " on | off = turn *low level* debugging on or off. Use with care.");
        say("--Command buffer: These are understood at all times and are interpreted before");
        say(indent + "any commands are issued.");
        say(indent + CLEAR_BUFFER_COMMAND + " =  clear the command history");
        say(indent + LOAD_BUFFER_COMMAND + " path = load the command history saved in the path");
        say(indent + WRITE_BUFFER_COMMAND + " path = write the command history to the given file");
        say("--Command history:");
        say(indent + HISTORY_LIST_COMMAND + " [index] = either print the entire command history (no argument)");
        say(indent + indent + " or re-execute the command at the given index.");
        say(indent + REPEAT_LAST_COMMAND + " = re-evaluate the most recent (0th index) command in the history");
        say(indent + indent + "This is equivalent to issuing " + HISTORY_LIST_COMMAND + " 0");
        if (getComponentManager() != null) {
            say(indent + COMPONENT_COMMAND + " = execute a command from a specific component, without switching to that component.");
            say(indent + indent + "No arguments simply switch to that component, arguments are fed to the component and evaluated.");
        }
        say(ONLINE_HELP_COMMAND + " topic = print help out on the given topic");
        say("E.g. #1");
        say(LOAD_BUFFER_COMMAND + "  /tmp/foo.txt would load the file \"/tmp/foo.txt\" in to the command history, replacing it");
        say("---");
        if (getComponentManager() != null) {
            say("E.g. #2");
            say(COMPONENT_COMMAND + "clients set_id  dev:test/no_cfg");
            say(COMPONENT_COMMAND + "clients ls >cfg");
            say("would set the id in the clients component, then list the cfg attribute.");
            say("---");
        }

    }

    ComponentManager componentManager = null;

    protected String doRepeatCommand(String cmdLine) {
        if (cmdLine.contains(HELP_SWITCH)) {
            say(REPEAT_LAST_COMMAND + " = repeat the last command. Identical to " + HISTORY_LIST_COMMAND + " 0");
            return null;
        }
        cmdLine = cmdLine.substring(REPEAT_LAST_COMMAND.length());
        if (0 < commandHistory.size()) {
            String current = commandHistory.get(0);
            if (cmdLine.trim().length() == 0) {
                return current;
            }
            current = current + " " + cmdLine;
            commandHistory.add(0, current);
            return current;
            //return commandHistory.get(0) + " " + cmdLine;
        }
        say("no commands found");
        return null;
    }

    protected String doHistory(String cmdLine) {
        if (cmdLine.contains(HELP_SWITCH)) {
            say(HISTORY_LIST_COMMAND + "[int] = either show the entire history (no argument)");
            say("  or execute the command at the given index. Note that signed indices are allowed,");
            say("  so the /h -1 would execute the very last command in the buffer, /h -2 executes");
            say("  the next to last, etc., Note that as ");
            say("  commands are added, the relative indices tail of the command history do not change.");
            say("See also:" + WRITE_BUFFER_COMMAND + ", " + LOAD_BUFFER_COMMAND + ", " + REPEAT_LAST_COMMAND);
            return null; // do nothing
        }
        // Either of the following work:
        //h == print history with line numbers
        // /h int = execute line # int, or print history if that fails
        StringTokenizer st = new StringTokenizer(cmdLine, " ");
        st.nextToken(); // This is the "/h" which we already know about
        boolean printIt = true;
        if (st.hasMoreTokens()) {
            try {
                int lineNo = Integer.parseInt(st.nextToken());
                String rest = "";
                while (st.hasMoreTokens()) {
                    rest = rest + " " + st.nextToken();
                }

                // allow signed command history numbers. so /h -1 is ok.
                lineNo = lineNo < 0 ? (commandHistory.size() + lineNo) : lineNo;
                if (rest.trim().length() == 0) {
                    if (0 <= lineNo && lineNo < commandHistory.size()) {
                        return commandHistory.get(lineNo);
                    }
                } else {
                    if (0 <= lineNo && lineNo < commandHistory.size()) {
                        String current = commandHistory.get(lineNo) + rest;
                        commandHistory.add(0, current);
                        return current;
                    }
                }
            } catch (Throwable t) {
                // do nothing, just print out the history.
            }
        }
        if (printIt) {
            for (int i = 0; i < commandHistory.size(); i++) {
                // an iterator actually prints these in reverse order. Print them in order.
                say(i + ": " + commandHistory.get(i));
            }
        }
        return null;
    }

    protected void doBufferWrite(String cmdLine) throws Exception {
        if (cmdLine.contains(HELP_SWITCH)) {
            say(WRITE_BUFFER_COMMAND + " file = write current command history to a file.");
            say("See also:" + LOAD_BUFFER_COMMAND);
            return;
        }
        String rawFile = cmdLine.substring(WRITE_BUFFER_COMMAND.length()).trim();
        if (rawFile == null || rawFile.isEmpty()) {
            say("Sorry, missing file path.");
            return;
        }
        if (commandHistory.isEmpty()) {
            say("(empty command buffer)");
            return;
        }
        File f = new File(rawFile);
        FileWriter fileWriter = new FileWriter(f);
        long byteCount = 0L;
        for (String newLine : commandHistory) {
            newLine = newLine + "\n";
            byteCount = byteCount + newLine.length();
            fileWriter.write(newLine);
        }
        fileWriter.flush();
        fileWriter.close();
        say("wrote " + byteCount + " bytes to " + rawFile);

    }

    protected void doBufferRead(String cmdLine) throws Exception {
        if (cmdLine.contains(HELP_SWITCH)) {
            say(LOAD_BUFFER_COMMAND + " file = load a saved command history, replacing the current one.");
            say("See also: " + WRITE_BUFFER_COMMAND);
            return;
        }

        String rawFile = cmdLine.substring(LOAD_BUFFER_COMMAND.length()).trim();
        if (rawFile == null || rawFile.isEmpty()) {
            say("Sorry, missing file path.");
            return;
        }
        File f = new File(rawFile);
        if (!f.exists()) {
            say("Sorry, the file \"" + rawFile + "\" does not exist");
            return;
        }
        if (!f.isFile()) {
            say("Sorry but \"" + rawFile + "\" is not a file");
            return;
        }

        if (!f.canRead()) {
            say("Sorry but \"" + rawFile + "\" cannot be read.");
            return;
        }
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String newLine = br.readLine();
        long byteCount = 0L;
        int lineCount = 0; //read one
        commandHistory = new LinkedList();
        while (newLine != null) {
            lineCount++;
            byteCount = byteCount + newLine.length();
            commandHistory.add(newLine);
            newLine = br.readLine();
        }
        br.close();
        say(byteCount + " bytes read, " + lineCount + " line" + (lineCount == 1 ? "" : "s") + " added.");
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public void setComponentManager(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    /**
     * Actual method that starts up this driver and sets out prompts etc. for the user to use interactively.
     *
     * @throws Exception
     */

    public void start() {
        NEWstart();
    }

    protected void NEWstart() {
        if (isRunLineMode()) {
            processRunLine(); // contract is that it runs the single line, then continues.
        }
        if (hasInputFile() && (commands == null || commands.length == 0)) {
            // catches case that there is an input file and we hit the EOF,
            say("(empty command list, exiting...)");
            return;
        }
        String prompt = commands[0].getPrompt();
        String cmdLine;
        while (!isDone) {
            try {
                cmdLine = readline(prompt);
                if (cmdLine == null) {
                    return;
                }
                processLine(cmdLine);
            } catch (ExitException ee) {
                return;
            } catch (Throwable t) {
                if (debug) {
                    t.printStackTrace();
                }
                say("Internal error reading line:" + (StringUtils.isTrivial(t.getMessage()) ? "\n" + t.getMessage() : ""));
            }
        }
    }


    protected void processLine(String cmdLine) throws Throwable {

        if (hasComments()) {
            if (cmdLine.trim().startsWith(getLineCommentStart())) {
                return;
            }
        }
        if (hasEnv()) {
            cmdLine = TemplateUtil.replaceAll(cmdLine, getEnv());
        }
        boolean storeLine = true;
        cmdLine = cmdLine.trim();
        String command;
        // If they send something like //foo split it as // foo
        if (cmdLine.startsWith(COMPONENT_COMMAND) && !cmdLine.startsWith(COMPONENT_COMMAND + " ")) {
            cmdLine = COMPONENT_COMMAND + " " + cmdLine.substring(COMPONENT_COMMAND.length());
        }
        StringTokenizer st = new StringTokenizer(cmdLine, " ");
        command = st.nextToken().trim();
        if (StringUtils.isTrivial(command)) {
            return;
        }
        switch (command) {
            case REPEAT_LAST_COMMAND:
                cmdLine = doRepeatCommand(cmdLine);
                storeLine = cmdLine == null; // if there is nothing in the buffer, cmdLine is null, don't store it.
                return;
            case HISTORY_LIST_COMMAND:
                cmdLine = doHistory(cmdLine);
                if (cmdLine == null) {
                    return;// if there is nothing in the buffer, cmdLine is null, don't store it.
                }
                storeLine = false;
                return;
            case WRITE_BUFFER_COMMAND:
                doBufferWrite(cmdLine);
                return;
            case LOAD_BUFFER_COMMAND:
                doBufferRead(cmdLine);
                return;
            case CLEAR_BUFFER_COMMAND:
                if (cmdLine.contains(HELP_SWITCH)) {
                    say(CLEAR_BUFFER_COMMAND + " = clear the entire command history.");
                    return;
                }
                commandHistory = new LinkedList<>();
                say("Command history cleared.");
                return;
            case SHORT_EXIT_COMMAND:
                quit(null);
                throw new ExitException();
                //return;
            case PRINT_HELP_COMMAND:
                printHelp();
                return;
            case LIST_ALL_METHODS_COMMAND:
                InputLine inputLine = new InputLine(CLT.tokenize(cmdLine));
                listCLIMethods(inputLine);
                return;
            case ONLINE_HELP_COMMAND:
                inputLine = new InputLine(CLT.tokenize(cmdLine));
                doHelp(inputLine);
                return;
            case TRACE_COMMAND:
                doTrace(cmdLine);
                return;
        }

        if (storeLine) {
            // Store it if it was not retrieved from the command history.
            commandHistory.add(0, cmdLine);
        }
        if (cmdLine.startsWith(COMPONENT_COMMAND) && !command.equals(COMPONENT_COMMAND)) {
            say("unknown command: " + cmdLine);
            return;
        }
        if (command.equals(COMPONENT_COMMAND)) {
            // execute a single command in another component.
            if (componentManager == null) {
                say("Sorry, this does not have components.");
            } else {
                cmdLine = "use " + cmdLine.substring(cmdLine.indexOf(COMPONENT_COMMAND) + COMPONENT_COMMAND.length() + 1); // might have leading whitespace
                InputLine inputLine = new InputLine(CLT.tokenize(cmdLine));
                componentManager.use(inputLine);
            }
        } else {
            switch (execute(cmdLine)) {
                case HELP_RC:
                    InputLine inputLine = new InputLine(CLT.tokenize(cmdLine));
                    listCLIMethods(inputLine);
                    break;
                case SHUTDOWN_RC:
                    isDone = false; // just in case.
                    quit(null);
                    throw new ExitException();
                case USER_EXIT_RC:
                    say("User exit encountered");
                    throw new ExitException();
                case OK_RC:
                    // do nix.
                    break;
                case ABNORMAL_RC:
                default:
                    say("Command \"" + cmdLine + "\" not found/understood. Try typing help or exit.");
                    say("To see all commands currently available, type " + LIST_ALL_METHODS_COMMAND);
            }
        }

    }

    public boolean isTraceOn() {
        return traceOn;
    }

    public void setTraceOn(boolean traceOn) {
        this.traceOn = traceOn;
    }

    boolean traceOn = false;

    private void doTrace(String cmdLine) {
        if (cmdLine.contains(HELP_SWITCH)) {
            say(TRACE_COMMAND + " [on | off] Turn on or off low-level debugging for the client. Mostly for system programmers.");
            return;
        }
        StringTokenizer st = new StringTokenizer(cmdLine, " ");
        st.nextToken(); // This is the "/trace" which we already know about
        if (st.hasMoreTokens()) {
            traceOn = st.nextToken().equalsIgnoreCase("on");
            debug = traceOn;
            DebugUtil.setIsEnabled(traceOn);
            if (traceOn) {
                DebugUtil.setDebugLevel(DebugUtil.DEBUG_LEVEL_TRACE);
            } else {
                DebugUtil.setDebugLevel(DebugUtil.DEBUG_LEVEL_OFF);
            }
            say("trace " + (traceOn ? "on" : "off"));
        } else {
            say("trace is currently " + (traceOn ? "on" : "off"));
        }
    }


    public int execute(String cmdLine) {
        Vector cmdV = CLT.tokenize(cmdLine);
        InputLine cliAV = new InputLine(cmdV);
        cliAV.setOriginalLine(cmdLine);
        return execute(cliAV);
    }

    /**
     * Returns a logical true if one of the command lines executes the line successfully. This will
     * also throw a shutdown exception if the user asks it to...
     * Otherwise, it returns false;
     *
     * @param cliAV
     * @return
     */

    public int execute(InputLine cliAV) {
        try {
            if (!cliAV.isEmpty()) {
                String cmdS;
                cmdS = cliAV.getCommand();

                if (cmdS.toLowerCase().equals("exit") ||
                        cmdS.toLowerCase().equals("quit")) {
                    // This intercepts quitting so we don't have to jump through hoops to exit.
                    return SHUTDOWN_RC;
                }
                if (cmdS.toLowerCase().equals("help") || cmdS.toLowerCase().equals(HELP_SWITCH)) {
                    //    commands[0].help();
                    return HELP_RC;
                }
                if (cmdS.toLowerCase().equals("echo") || cmdS.toLowerCase().equals(ECHO_COMMAND)) {
                    say(cliAV.getOriginalLine().trim().substring(cmdS.length()).trim());
                    return OK_RC;
                }
                for (int i = 0; i < getCLICommands().length; i++) {
                    try {
                        invokeMethod(commands[i], cmdS, cliAV);
                        return OK_RC; // it worked
                    } catch (InvocationTargetException itx) {
                        if (debug) {
                            itx.printStackTrace();
                        }
                        // this is the most likely way to get and exception
                        if ((itx.getTargetException() != null) && (itx.getTargetException() instanceof ExitException)) {
                            return USER_EXIT_RC;
                        }
                        if (itx.getCause() != null) {
                            say(itx.getCause().getMessage());
                        } else {
                            say("Invocation target exception encountered:" + itx.getTargetException());
                        }

                    } catch (Exception nsmx) {
                        if (debug) {
                            say("Could not execute command. Message:" + nsmx.getMessage());
                        }
                    }
                }
            }
        } catch (MalformedCommandException mcx) {
            say("  >>Couldn't parse the command");
        }
        return ABNORMAL_RC;// shouldn't Happen.
    }

    public HelpUtil getHelpUtil() {
        if (helpUtil == null) {
            helpUtil = new HelpUtil();
        }
        return helpUtil;
    }

    public void setHelpUtil(HelpUtil helpUtil) {
        this.helpUtil = helpUtil;
    }

    HelpUtil helpUtil = null;

    protected void doHelp(InputLine inputLine) {
        Commands[] commands = getCLICommands();
        boolean helpPrinted = false;
        if (commands.length == 0) {
        } else {
            // special case, no arguments so print out every topic
            if (inputLine.getArgCount() == 0) {
                printHelpTopics(inputLine);
                return;
            }
            //if (getHelpUtil() != null) {
                helpPrinted = getHelpUtil().printHelp(inputLine) || helpPrinted;
           // }
            if(!helpPrinted) { // don't reprint help repeatedly. Help keys are unique.
                if(commands != null){
                for (Commands command : commands) {
                    //       if (getHelpUtil() != null) {
                    helpPrinted = getHelpUtil().printHelp(inputLine) || helpPrinted;
                    //     }
                }
                }
            }
            if (!helpPrinted) {
                say("no help found for this topic");
            }
        }
    }

    protected void printHelpTopics(InputLine inputLine) {
        HelpUtil helpUtil1 = new HelpUtil();
   //     if (getHelpUtil() != null) {
            helpUtil1.getOnlineHelp().putAll(getHelpUtil().getOnlineHelp());
    //    }
        if(commands != null) {
        for (Commands command : commands) {
            //    if (getHelpUtil() != null) {
            helpUtil1.getOnlineHelp().putAll(getHelpUtil().getOnlineHelp());
            //       }
        }        }
        helpUtil1.printHelp(inputLine);
    }

    protected void listCLIMethods(InputLine inputLine) {
        // trick is that even though this is made to list out all the methods, in practice
        // there is never more than a single active Command object.
        String[] tempCCIN = CLIReflectionUtil.getCommandsNameList(getCLICommands());
        List<String> list = new ArrayList<>();
        for (String x : tempCCIN) {
            list.add(x);
        }
        FormatUtil.formatList(inputLine, list);

        say("\nTo get help on the CLI, type /?");
        say("To get general information on the current component in use, type --help at the prompt.");
        say("To get more information on a specific command\n");
        say("command --help");
    }


    public void quit(InputLine inputLine) {
        shutdown();
    }

    protected void shutdown() {
        say("exiting ...");
/*       Don't close the buffered reader since it will close System.in and
         make it impossible to run another CLI in this JVM or anything else that
         needs input for that matter.
*/
    }

    /**
     * For use with informational messages.
     *
     * @param x
     */
    protected void say(String x) {
        getIOInterface().println(x);
        //System.out.println(x);
    }

    /**
     * Conditional output if in verbose mode only.
     *
     * @param x
     */
    protected void sayv(String x) {
        if (isVerbose()) {
            getIOInterface().print(x);
        }
    }

    public IOInterface getIOInterface() {
        if (ioInterface == null) {
            ioInterface = new BasicIO();
        }
        return ioInterface;
    }

    /**
     * This also sets any components you added.
     *
     * @param ioInterface
     */
    public void setIOInterface(IOInterface ioInterface) {
        this.ioInterface = ioInterface;
    }

    IOInterface ioInterface;
    protected static String DUMMY_FUNCTION = "dummy0"; // used to create initial command line

    /**
     * Process a line to run. At invocation you may use something like
     * <pre>
     *     cli {@link #RUN_COMMAND_FLAG} A B C
     * </pre>
     * and what happens is that everything after the flag  is fed tot he processor and executed
     * as a single command, as if you'd type in
     * <pre>
     *     A B C
     * </pre>
     * This lets you do things like
     * <pre>
     *     cli -run use clients
     * </pre>
     * to start a component right away.
     */
    protected void processRunLine() {
        // need to tease out the intended line to execute. The arg line looks like
        // cli -batch | -run A B C
        // And we added a dummy argument to make it parse right,
        // so we need to drop the name of the function and the -batch flag.
        // The net effect is to run the command
        // A B C
        execute(new InputLine(getRunLine()));
    }

    protected void batchFileHelp() {
        say("Running batch files.");
        say("You can run scripts from the command line by passing in a file,");
        say("any line that starts with a # is a comment.");
        say("All lines MUST end with a ; (which is discarded at processing).");
        say("You may have commands across multiple lines with all the whitespace you want, but");
        say("at processing each line will be concatenated with a space, so don't break tokens over ");
        say("lines. See the readme.txt for more and look at any .cmd file in this distro for examples.");
    }


    protected static final String INPUT_FILE_FLAG = "-in";
    protected static final String OUTPUT_FILE_FLAG = "-out";
    protected static final String RUN_COMMAND_FLAG = "-run";
    protected static final String TERMINAL_TYPE_FLAG = "-tty";
    protected static final String TERMINAL_TYPE_ANSI = "ansi";
    protected static final String TERMINAL_TYPE_TEXT = "text";
    protected static final String TERMINAL_TYPE_ASCII = "ascii";
    protected static final String SHORT_VERBOSE_FLAG = "-v";
    protected static final String LONG_VERBOSE_FLAG = "-verbose";
    protected static final String SHORT_SET_OUTPUT_FLAG = "-setOutput";
    protected static final String LONG_SET_OUTPUT_FLAG = "-setOutput";
    protected static final String SILENT_FLAG = "-silent";
    protected static final String ENVIRONMENT_FLAG = "-env";
    protected static final String LOG_FLAG = "-log";

    public static void main(String[] args) {
        CLIDriver cli = getCLIDriver();
        cli.bootstrap(args);
        cli.start();
    }

    public boolean hasOutputFile() {
        return outputFile != null;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    String outputFile = null;
    boolean verbose = false;
    boolean outputOn = true;

    public MyLoggingFacade getLogger() {
        return logger;
    }

    public void setLogger(MyLoggingFacade logger) {
        this.logger = logger;
    }

    MyLoggingFacade logger;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isOutputOn() {
        return outputOn;
    }

    public void setOutputOn(boolean outputOn) {
        this.outputOn = outputOn;
    }

    public boolean isRunLineMode() {
        return runLineMode;
    }

    public void setRunLineMode(boolean runLineMode) {
        this.runLineMode = runLineMode;
    }

    public String getRunLine() {
        return runLine;
    }

    public void setRunLine(String runLine) {
        this.runLine = runLine;
    }

    String runLine = null;


    boolean runLineMode = false;


    /**
     * This method is charged with interpreting the command line arguments and setting up
     * the state for this CLI driver. State includes
     * <ul>
     *     <li>{@link #getLogger()} - the logger, if specified</li>
     *     <li>{@link #isRunLineMode()} - if the {@link #RUN_COMMAND_FLAG} was used</li>
     *     <li>{@link #isOutputOn()} - suppress all output if true</li>
     *     <li>{@link #isVerbose()} - how chatty this should be</li>
     *     <li>{@link IOInterface}</li>
     * </ul>
     * These properties are injected into any component when set. However, this is a one-time
     * undertaking and the {@link Commands} are not altered after that. So
     * either create a {@link CLIDriver}, set your components and call this to inject
     * the state, or create this, call this method and then add your components. You call
     * <ul>
     *     <li>{@link #start()} to being interactive processing</li>
     *     <li>{@link #processRunLine()} to run the single command.</li>
     * </ul>
     *
     * @param args
     * @return The {@link InputLine} after all recognized flags have been processed. This means they are
     * stripped.
     */
    // For testing: in
    // -in /home/ncsa/dev/ncsa-git/oa4mp/server-admin/src/main/scripts/jwt-scripts/ex_hello_world.cmd
    public InputLine bootstrap(String[] args) {
        return bootstrap(new InputLine(getClass().getSimpleName(), args));
    }

    public boolean hasInputFile() {
        return hasInputFile;
    }

    public void setHasInputFile(boolean hasInputFile) {
        this.hasInputFile = hasInputFile;
    }

    boolean hasInputFile = false;
    public InputLine bootstrap(InputLine argLine) {

        if(argLine.hasArg(SILENT_FLAG)) {
            setOutputOn(false);
            setVerbose(false);
        }else{
            if(argLine.hasArg(SHORT_SET_OUTPUT_FLAG, LONG_SET_OUTPUT_FLAG)){
                String raw = argLine.getNextArgFor(SHORT_SET_OUTPUT_FLAG, LONG_SET_OUTPUT_FLAG);
                Boolean bool = isTrue(raw);
                if(bool != null){
                    setOutputOn(bool);
                }
            }
            setVerbose(argLine.hasArg(SHORT_VERBOSE_FLAG, LONG_VERBOSE_FLAG));
        }
        argLine.removeSwitchAndValue(SHORT_SET_OUTPUT_FLAG, LONG_SET_OUTPUT_FLAG);
        argLine.removeSwitch(SHORT_VERBOSE_FLAG, LONG_VERBOSE_FLAG);


        MyLoggingFacade myLoggingFacade = null;

        if (argLine.hasArg(LOG_FLAG)) {
            String logFileName = argLine.getNextArgFor(LOG_FLAG);
            File logFile = new File(logFileName);
            argLine.removeSwitchAndValue(LOG_FLAG);
            LoggerProvider loggerProvider = new LoggerProvider(logFileName,
                    logFile.getName() + " logger", 1, 1000000, true, true, Level.INFO);
            myLoggingFacade = loggerProvider.get(); // if verbose
        }
        setLogger(myLoggingFacade);
        boolean hasInputFile = argLine.hasArg(INPUT_FILE_FLAG);
        if (argLine.hasArg(ENVIRONMENT_FLAG)) {
            String envFile = argLine.getNextArgFor(ENVIRONMENT_FLAG);
            argLine.removeSwitchAndValue(ENVIRONMENT_FLAG);
            readEnv(envFile, false); // on init, silently ignore unless -v option enabled.
            currentEnvFile = envFile;
        }
        if (argLine.hasArg(OUTPUT_FILE_FLAG)) {
            setOutputFile(argLine.getNextArgFor(OUTPUT_FILE_FLAG));
            argLine.removeSwitchAndValue(OUTPUT_FILE_FLAG);
        }
        if (hasInputFile) {
            String inputFileName = argLine.getNextArgFor(INPUT_FILE_FLAG);
            argLine.removeSwitchAndValue(INPUT_FILE_FLAG);
            BasicIO basicIO = new BasicIO();
            setHasInputFile(true);
            try {
                FileInputStream fis = new FileInputStream(inputFileName);
                if (inputFileName.endsWith(".cmd")) {
                    // If it's a command file, snarf it up and use it.
                    DDParser ddp = new DDParser();
                    List<String> commands = ddp.parse(fis);
                    String inputString = StringUtils.listToString(commands);
                    ByteArrayInputStream bais = new ByteArrayInputStream(inputString.getBytes());
                    System.setIn(bais);
                    basicIO.setInputStream(System.in);
                } else {
                    basicIO.setInputStream(fis);
                }

                if (hasOutputFile()) {
                    FileOutputStream fos = new FileOutputStream(getOutputFile());
                    PrintStream ps = new PrintStream(fos);
                    System.setOut(ps);
                    basicIO.setPrintStream(System.out);
                }
                setIOInterface(basicIO);
                return argLine;
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        // Batch mode means that the rest of command line after flag is interpreted as a single command.
        // This executes one command.
        setRunLineMode(argLine.hasArg(RUN_COMMAND_FLAG));
        if (isRunLineMode()) {
            String runLine = argLine.getOriginalLine();
            runLine = runLine.substring(runLine.indexOf(RUN_COMMAND_FLAG) + RUN_COMMAND_FLAG.length() + 1);
            setRunLine(runLine);
            argLine.setOriginalLine(runLine);
            argLine.reparse();
            return argLine;
        }

        // Lastt hing to set is the IO if they specify it.
        if (argLine.hasArg(TERMINAL_TYPE_FLAG)) {
            String tty = argLine.getNextArgFor(TERMINAL_TYPE_FLAG);
            argLine.removeSwitchAndValue(TERMINAL_TYPE_FLAG);
            switch (tty) {
                case TERMINAL_TYPE_ANSI:
                    try {
                        ISO6429Terminal iso6429Terminal = new ISO6429Terminal(getLogger());
                        ISO6429IO iso6429IO = new ISO6429IO(iso6429Terminal, false);
                        iso6429IO.addCommandHistory(getCommandHistory());
                        setIOInterface(iso6429IO);
                    } catch (IOException e) {
                        if (isVerbose()) {
                            e.printStackTrace();
                        }
                        say("error loading terminal type " + TERMINAL_TYPE_ANSI);
                    }
                    break;
                case TERMINAL_TYPE_ASCII:
                case TERMINAL_TYPE_TEXT:
                    setIOInterface(new BasicIO());
                    break;
                default: // They passed in a terminal type, and we don't recognize it
                    sayv("unknown terminal type: " + tty);
            }

        }

        return argLine;
    }

    static CLIDriver driver = null;

    public static boolean hasCLIDriver() {
        return driver != null;
    }

    public static void setCLIDriver(CLIDriver driver) {
        CLIDriver.driver = driver;
    }

    public static CLIDriver getCLIDriver() {
        if (driver == null) {
            driver = new CLIDriver(); // plain vanilla instance
        }
        return driver;
    }

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
                env = jsonObject;
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
                env = xp;
            }
        } catch (Throwable t) {
            if (isVerbose()) {
                t.printStackTrace();
            }
            if (verbose) say("Could not parse envirnoment file.");
        }
        currentEnvFile = path;
    }

    public String getCurrentEnvFile() {
        return currentEnvFile;
    }

    public void setCurrentEnvFile(String currentEnvFile) {
        this.currentEnvFile = currentEnvFile;
    }

    String currentEnvFile = null;

    /**
     * Strings that this will treat as equivalent to logical true.
     */
    public static String[] LOGICAL_TRUES = new String[]{"true", "ok", "yes", "1", "on", "yup", "yeah", "enable", "enabled"};

    /**
     * Strings this will treat as equivalent to logical false.
     */
    public static String[] LOGICAL_FALSES = new String[]{"false", "no", "0", "off", "nope", "nay", "disable", "disabled"};

    /**
     * This checks that the string is one of the allowed trues or false. Anything else returns a null.
     * @param raw
     * @return
     */
    public  Boolean isTrue(String raw) {
        for(String s : LOGICAL_TRUES){
            if(s.equals(raw)) return Boolean.TRUE;
        }
        for(String s : LOGICAL_FALSES){
            if(s.equals(raw)) return Boolean.FALSE;
        }
        return null;
    }
}
