package edu.uiuc.ncsa.security.util.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

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


    private Commands[] commands; // implementation of this abstract class.
    CommandLineTokenizer CLT = new CommandLineTokenizer();

    // couple of internal flags.
    boolean isDone = false;
    boolean debug = false;   // This is set manually here.

    public CLIDriver(Commands... cci) {
        super();
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
     * Returns a logical true if one of the command lines executes the line successfully. This will
     * also throw a shutdown exception if the user asks it to..
     * Otherwise it returns false;
     *
     * @param cmdLine
     * @return
     */

    protected int execute(String cmdLine) {
        try {
            Vector cmdV = CLT.tokenize(cmdLine);
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
                    }catch(InvocationTargetException itx){
                        if(debug){
                            itx.printStackTrace();
                        }
                        // this is the most likely way to get and exception
                        if((itx.getTargetException()!=null) && (itx.getTargetException() instanceof ExitException)){
                            return USER_EXIT_RC;
                        }
                        if(itx.getCause() != null){
                            say("Exception. The cause is: " + itx.getCause().getMessage());
                        }else{
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
