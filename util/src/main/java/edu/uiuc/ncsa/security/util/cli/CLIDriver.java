package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.*;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

    public static final int OK_RC = 0;
    public static final int ABNORMAL_RC = -1;
    public static final int USER_EXIT_RC = 10;
    public static final int SHUTDOWN_RC = -10;
    public static final int HELP_RC = 100;
    public static final String HELP_SWITCH = "--help";


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
        this.ioInterface = ioInterface;
    }

    public void addCommands(Commands... cci) {
        for (Commands xxx : cci) {
            // Set exactly one instance of the component manager
            if (xxx instanceof ComponentManager && componentManager == null) {
                componentManager = (ComponentManager) xxx;
            }
            if (xxx instanceof CommonCommands) {
                ((CommonCommands) xxx).setDriver(this);
            }
            xxx.setIOInterface(getIOInterface());
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


    // TODO - Add more CLI-level commands?
    // Might very well want to fold this into the standard flow rather than intercept it first.
    //  /i file = interpret all the commands in a file, i.e. run a script. No arg runs command buffer as batch file????
    protected int getCommandType(String cmdLine) {
        if (StringUtils.isTrivial(cmdLine.trim())) {
            return NO_OP_VALUE;
        }
        StringTokenizer st = new StringTokenizer(cmdLine.trim(), " ");
        String nextToken = st.nextToken();
        switch (nextToken) {
            case REPEAT_LAST_COMMAND:
                return REPEAT_COMMAND_VALUE;
            case HISTORY_LIST_COMMAND:
                return HISTORY_COMMAND_VALUE;
            case WRITE_BUFFER_COMMAND:
                return WRITE_BUFFER_COMMAND_VALUE;
            case LOAD_BUFFER_COMMAND:
                return READ_BUFFER_COMMAND_VALUE;
            case CLEAR_BUFFER_COMMAND:
                return CLEAR_BUFFER_COMMAND_VALUE;
            case SHORT_EXIT_COMMAND:
                return SHORT_EXIT_COMMAND_VALUE;
            case PRINT_HELP_COMMAND:
                return PRINT_HELP_COMMAND_VALUE;
            case LIST_ALL_METHODS_COMMAND:
                return LIST_ALL_METHODS_COMMAND_VALUE;
            case TRACE_COMMAND:
                return TRACE_COMMAND_VALUE;
            case COMPONENT_COMMAND:
                return COMPONENT_COMMAND_VALUE;
            case ONLINE_HELP_COMMAND:
                return ONLINE_HELP_COMMAND_VALUE;

        }
        return USER_DEFINED_COMMAND;
    }

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
     * Actual method that starts up this driver and sets out prompts etc.
     *
     * @throws Exception
     */

    public void start() {
        //commands[0].about(null);
        String cmdLine;
        String prompt = commands[0].getPrompt();
        try {
            getHelpUtil().load("/meta_command_help.xml");
        } catch (Throwable e) {
            if (isTraceOn()) {
                e.printStackTrace();
            }
            say("Could not load help for the metacommands.");
        }

        while (!isDone) {
            try {
                //say2(prompt);
                if (isTraceOn()) {
                    // getIoInterface().flush();
                    // System.err.flush();
                    // This is because there may be cruft left over from debug statements and
                    // if we don't do this, the cursor ends up in weird places.
                    say("");
                }
                cmdLine = readline(prompt);
                if (hasEnv()) {
                    cmdLine = TemplateUtil.replaceAll(cmdLine, getEnv());
                }
                boolean storeLine = true;
                int commandType = getCommandType(cmdLine);
                switch (commandType) {
                    case REPEAT_COMMAND_VALUE:
                        cmdLine = doRepeatCommand(cmdLine);
                        storeLine = cmdLine == null; // if there is nothing in the buffer, cmdLine is null, don't store it.
                        break;
                    case HISTORY_COMMAND_VALUE:
                        cmdLine = doHistory(cmdLine);
                        if (cmdLine == null) {
                            continue;// if there is nothing in the buffer, cmdLine is null, don't store it.
                        }
                        storeLine = false;
                        break;
                    case WRITE_BUFFER_COMMAND_VALUE:
                        doBufferWrite(cmdLine);
                        continue;
                    case READ_BUFFER_COMMAND_VALUE:
                        doBufferRead(cmdLine);
                        continue;
                    case CLEAR_BUFFER_COMMAND_VALUE:
                        if (cmdLine.contains(HELP_SWITCH)) {
                            say(CLEAR_BUFFER_COMMAND + " = clear the entire command history.");
                            continue;
                        }
                        commandHistory = new LinkedList<>();
                        say("Command history cleared.");
                        continue;
                    case SHORT_EXIT_COMMAND_VALUE:
                        quit(null);
                        return;
                    case PRINT_HELP_COMMAND_VALUE:
                        printHelp();
                        continue;
                    case LIST_ALL_METHODS_COMMAND_VALUE:
                        InputLine inputLine = new InputLine(CLT.tokenize(cmdLine));
                        listCLIMethods(inputLine);
                        continue;
                    case ONLINE_HELP_COMMAND_VALUE:
                        inputLine = new InputLine(CLT.tokenize(cmdLine));
                        doHelp(inputLine);
                        continue;
                    case TRACE_COMMAND_VALUE:
                        doTrace(cmdLine);
                        continue;

                    case NO_OP_VALUE:
                        continue;
                    case USER_DEFINED_COMMAND:
                    default:
                }
                if (commandType == NO_OP_VALUE) {
                    // The user entered a blank line or nothing but blanks. Skip it all.
                    continue;
                }
                if (storeLine) {
                    // Store it if it was not retrieved from the command history.
                    commandHistory.add(0, cmdLine);
                }
                if (cmdLine.startsWith(COMPONENT_COMMAND)) {
                    // execute a single command in another component.
                    if (componentManager == null) {
                        say("Sorry, this does not have components.");
                    } else {
                        cmdLine = "use " + cmdLine.substring(COMPONENT_COMMAND.length());
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
                            return;
                        case USER_EXIT_RC:
                            say("User exit encountered");
                            break;
                        case OK_RC:
                            // do nix.
                            break;
                        case ABNORMAL_RC:
                        default:
                            say("Command not found/understood. Try typing help or exit.");
                            say("To see all commands currently available, type " + LIST_ALL_METHODS_COMMAND);
                    }
                }
            } catch (Throwable ioe) {
                if (debug) {
                    ioe.printStackTrace();
                }
                say("Internal error reading line:" + (StringUtils.isTrivial(ioe.getMessage()) ? "\n" + ioe.getMessage() : ""));
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
     * Otherwise it returns false;
     *
     * @param cliAV
     * @return
     */

    public int execute(InputLine cliAV) {
        try {
            if (!cliAV.isEmpty()) {
                String cmdS = cliAV.getCommand();
                if (cmdS.toLowerCase().equals("exit") ||
                        cmdS.toLowerCase().equals("quit")) {
                    // This intercepts quitting so we don't have to jump through hoops to exit.
                    return SHUTDOWN_RC;
                }
                if (cmdS.toLowerCase().equals("help") || cmdS.toLowerCase().equals(HELP_SWITCH)) {
                    //    commands[0].help();
                    return HELP_RC;
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
                            say("Exception. The cause is: " + itx.getCause().getMessage());
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
            if (getHelpUtil() != null) {
                helpPrinted = getHelpUtil().printHelp(inputLine) || helpPrinted;
            }
            for (Commands command : commands) {
                if (command.getHelpUtil() != null) {
                    helpPrinted = command.getHelpUtil().printHelp(inputLine) || helpPrinted;
                }
            }
            if (!helpPrinted) {
                say("no help found for this topic");
            }
        }
    }

    protected void printHelpTopics(InputLine inputLine) {
        HelpUtil helpUtil1 = new HelpUtil();
        if (getHelpUtil() != null) {
            helpUtil1.getOnlineHelp().putAll(getHelpUtil().getOnlineHelp());
        }
        for (Commands command : commands) {
            if (command.getHelpUtil() != null) {
                helpUtil1.getOnlineHelp().putAll(command.getHelpUtil().getOnlineHelp());
            }
        }
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
     * For use with prompts.
     *
     * @param x
     */
    protected void say2(String x) {
        getIOInterface().print(x);
        //System.out.print(x);
    }

    public IOInterface getIOInterface() {
        if (ioInterface == null) {
            ioInterface = new BasicIO();
        }
        return ioInterface;
    }

    public void setIOInterface(IOInterface ioInterface) {
        this.ioInterface = ioInterface;
    }

    IOInterface ioInterface;
}
