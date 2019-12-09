package edu.uiuc.ncsa.security.util.cli;

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


    List<String> commandHistory = new LinkedList<>();
    public static final String PRINT_HELP_COMMAND = "/?";
    public static final String CLEAR_BUFFER_COMMAND = "/c";
    public static final String LOAD_BUFFER_COMMAND = "/l";
    public static final String HISTORY_LIST_COMMAND = "/h";
    public static final String SHORT_EXIT_COMMAND = "/q";
    public static final String REPEAT_LAST_COMMAND = "/r";
    public static final String WRITE_BUFFER_COMMAND = "/w";

    /*
    Added Environment support. This allows for having any extension od CommonCommands use the set_env call
    to pull in properties from either a Java properties file or from a JSON file. Type "set_env --help" at the command
    line to see what's what.
     */
    Map env;

    public Map getEnv() {
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
        for (Commands xxx : cci) {
            if (xxx instanceof CommonCommands) {
                ((CommonCommands) xxx).setDriver(this);
            }
        }
        setCLICommands(cci);
    }

    public Commands[] getCLICommands() {
        return commands;
    }

    protected void setCLICommands(Commands[] commands) {
        this.commands = commands;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }

    BufferedReader bufferedReader;

    protected String readline() throws IOException {
        return getBufferedReader().readLine();
    }

    protected static final int NEW_COMMAND_VALUE = 0;
    protected static final int REPEAT_COMMAND_VALUE = 10;
    protected static final int HISTORY_COMMAND_VALUE = 20;
    protected static final int WRITE_BUFFER_COMMAND_VALUE = 30;
    protected static final int READ_BUFFER_COMMAND_VALUE = 40;
    protected static final int CLEAR_BUFFER_COMMAND_VALUE = 50;
    protected static final int SHORT_EXIT_COMMAND_VALUE = 60;
    protected static final int PRINT_HELP_COMMAND_VALUE = 70;

    // TODO - Add more CLI-level commands?
    // Might very well want to fold this into the standard flow rather than intercept it first.
    //  /i file = interpret all the commands in a file, i.e. run a script. No arg runs command buffer as batch file????
    protected int getCommandType(String cmdLine) {
        StringTokenizer st = new StringTokenizer(cmdLine.trim(), " ");
        String nextToken = st.nextToken();
        if (nextToken.equals(REPEAT_LAST_COMMAND)) return REPEAT_COMMAND_VALUE;
        if (nextToken.equals(HISTORY_LIST_COMMAND)) return HISTORY_COMMAND_VALUE;
        if (nextToken.equals(WRITE_BUFFER_COMMAND)) return WRITE_BUFFER_COMMAND_VALUE;
        if (nextToken.equals(LOAD_BUFFER_COMMAND)) return READ_BUFFER_COMMAND_VALUE;
        if (nextToken.equals(CLEAR_BUFFER_COMMAND)) return CLEAR_BUFFER_COMMAND_VALUE;
        if (nextToken.equals(SHORT_EXIT_COMMAND)) return SHORT_EXIT_COMMAND_VALUE;
        if (nextToken.equals(PRINT_HELP_COMMAND)) return PRINT_HELP_COMMAND_VALUE;
        return NEW_COMMAND_VALUE;
    }

    protected void printHelp(){
        String indent = "  ";
        // This is help for the built in commands here
        say("Command buffer commands. These are understood at all times and are interpreted before");
        say(indent + "any commands are issued.");
        say(indent + PRINT_HELP_COMMAND + " = print this help");
        say(indent + CLEAR_BUFFER_COMMAND + " =  clear the command history");
        say(indent + LOAD_BUFFER_COMMAND + " path = load the command history saved in the path");
        say(indent + HISTORY_LIST_COMMAND + " [index] = either print the entire command history (no argument)");
        say(indent + indent + " or re-execute the command at the given index.");
        say(indent + SHORT_EXIT_COMMAND + " = shorthand to exit this component");
        say(indent + REPEAT_LAST_COMMAND + " = re-evaluate the most recent (0th index) command in the history");
        say(indent + indent + "This is equivalent to issuing " + HISTORY_LIST_COMMAND + " 0");
        say(indent + WRITE_BUFFER_COMMAND + " path = write the command history to the given file" );

        say("E.g.");
        say(LOAD_BUFFER_COMMAND + "  /tmp/foo.txt would load the file \"/tmp/foo.txt\" in to the command history, replacing it");
    }
    protected String doRepeatCommand() {
        if (0 < commandHistory.size()) {
            return commandHistory.get(0);
        }
        say("no commands found");
        return null;
    }

    protected String doHistory(String cmdLine) {
        // Either of the following work:
        // /h == print history with line numbers
        // /h int = execute line # int, or print history if that fails
        StringTokenizer st = new StringTokenizer(cmdLine, " ");
        st.nextToken(); // This is the "/h" which we already know about
        boolean printIt = true;
        if (st.hasMoreTokens()) {
            try {
                int lineNo = Integer.parseInt(st.nextToken());
                if (0 <= lineNo && lineNo < commandHistory.size()) {
                    return commandHistory.get(lineNo);
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

    /**
     * Actual method that starts up this driver and sets out prompts etc.
     *
     * @throws Exception
     */

    public void start() {
        //commands[0].about(null);
        String cmdLine;
        String prompt = commands[0].getPrompt();
        while (!isDone) {
            try {
                say2(prompt);
                cmdLine = readline();
                if (hasEnv()) {
                    cmdLine = TemplateUtil.replaceAll(cmdLine, getEnv());
                }
                boolean storeLine = true;
                switch (getCommandType(cmdLine)) {
                    case REPEAT_COMMAND_VALUE:
                        cmdLine = doRepeatCommand();
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
                        commandHistory = new LinkedList<>();
                        say("Command history cleared.");
                        continue;
                    case SHORT_EXIT_COMMAND_VALUE:
                        quit(null);
                        return;
                    case PRINT_HELP_COMMAND_VALUE:
                        printHelp();
                        continue;
                    case NEW_COMMAND_VALUE:
                    default:
                }
                if (storeLine) {
                    // Store it if it was not retrieved from the command history.
                    commandHistory.add(0, cmdLine);
                }
                switch (execute(cmdLine)) {
                    case HELP_RC:
                        listCLIMethods();
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
                        listCLIMethods();
                }
            } catch (Throwable ioe) {
                if (debug) {
                    ioe.printStackTrace();
                }
                say("Internal error reading line:/n" + ioe.getMessage());
            }
        }
    }


    /**
     * So that various other programs can call this as needed
     *
     * @param cmds
     * @return
     */
    public int execute(String[] cmds) {
        Vector cmdV = new Vector();
        for (String x : cmds) {
            cmdV.add(x);
        }
        return execute(cmdV);
    }

    public int execute(String cmdLine) {
        Vector cmdV = CLT.tokenize(cmdLine);
        return execute(cmdV);
    }

    /**
     * Returns a logical true if one of the command lines executes the line successfully. This will
     * also throw a shutdown exception if the user asks it to..
     * Otherwise it returns false;
     *
     * @param cmdV
     * @return
     */

    public int execute(Vector cmdV) {
        try {
            if (cmdV.size() > 0) {
                String cmdS = ((String) cmdV.elementAt(0));
                if (cmdS.toLowerCase().equals("exit") ||
                        cmdS.toLowerCase().equals("quit")) {
                    // This intercepts quitting so we don't have to jump through hoops to exit.
                    return SHUTDOWN_RC;
                }
                if (cmdS.toLowerCase().equals("help") || cmdS.toLowerCase().equals("--help")) {
                    //    commands[0].help();
                    return HELP_RC;
                }
                InputLine cliAV = new InputLine(cmdV);
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
                            say(" Could not execute command. Message:" + nsmx.getMessage());
                            nsmx.printStackTrace();
                        }
                    }
                }
            }
        } catch (MalformedCommandException mcx) {
            say("  >>Couldn't parse the command");
        }
        return ABNORMAL_RC;// shouldn't Happen.
    }


    protected void listCLIMethods() {
        say("Here are the commands available:");
        String[] tempCCIN = CLIReflectionUtil.getCommandsNameList(getCLICommands());
        Arrays.sort(tempCCIN); // make it in sorted order.
        for (int i = 0; i < tempCCIN.length; i++) {
            say(tempCCIN[i]);
        }
        say("To get more information on a command type\n");
        say("command --help");
    }


    public void quit(InputLine inputLine) {
        shutdown();
    }

    protected void shutdown() {
        say("exiting ...");
/*       Don't close the buffered reader since it will close System.in and
         make it impossible to run another CLI in this JVM.
*/
    }

    /**
     * For use with informational messages.
     *
     * @param x
     */
    protected void say(String x) {
        System.out.println(x);
    }

    /**
     * For use with prompts.
     *
     * @param x
     */
    protected void say2(String x) {
        System.out.print(x);
    }
}
