package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.exceptions.InterruptException;
import edu.uiuc.ncsa.qdl.exceptions.ParsingException;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.BasicIO;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import edu.uiuc.ncsa.security.util.terminal.ISO6429IO;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.*;

import static edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands.*;

/**
 * This has the machinery for getting lines of input from the user and then feeding them to the
 * parser. It will also hand off workspace commands as needed.
 * <p>Created by Jeff Gaynor<br>
 * on 1/11/20 at  4:21 PM
 */
public class QDLWorkspace {

    public QDLWorkspace(WorkspaceCommands workspaceCommands) {
        this.workspaceCommands = workspaceCommands;
    }

    WorkspaceCommands workspaceCommands;

    protected MyLoggingFacade getLogger() {
        return workspaceCommands.logger;
    }

    protected void handleException(Throwable t) {
        if (workspaceCommands.isDebugOn()) {
            t.printStackTrace();
        }
        if (getLogger() != null) {
            getLogger().error(t);
        }
        if (t instanceof StackOverflowError) {
            workspaceCommands.say("Error: Stack overflow.");
            return;
        }
        if (t instanceof UndefinedFunctionException) {
            workspaceCommands.say("Error: No such function.");
            return;
        }
        if (t instanceof InterruptException) {
            // add a bunch of stuff to state table.
            workspaceCommands.say("sorry, cannot interrupt main workspace process.");
            return;
        }
        if ((t instanceof ParseCancellationException) | (t instanceof ParsingException)) {
            if (t.getMessage().contains("extraneous input")) {
                workspaceCommands.say("syntax error: Unexpected or illegal character.");
            } else {
                workspaceCommands.say("syntax error:" + (workspaceCommands.isDebugOn() ? t.getMessage() : "could not parse input"));
            }
            return;
        }
        if (t instanceof IllegalStateException) {
            workspaceCommands.say("illegal state:" + t.getMessage());
            return;
        }
        if (t instanceof IllegalArgumentException) {
            workspaceCommands.say("illegal argument:" + t.getMessage());
            return;
        }
        if (t instanceof QDLException) {
            workspaceCommands.say(t.getMessage());
            return;
        }
        // In case a jar is corrupted (e.g. maven builds it wrong, partial upgrade failed, so missing dependency)
        // since the first symptom is this. Without this case, it falls through and the user just gets a random error
        // that whatever component failed, not that the component was actually missing.
        if (t instanceof NoClassDefFoundError) {
            workspaceCommands.say("internal error: Missing classes. Did you try an upgrade that failed? (" + t.getMessage() + ")");
            return;
        }
        // Final fall through case.
        if (t.getMessage() == null) {
            workspaceCommands.say("error!");
        } else {
            workspaceCommands.say("error: " + t.getMessage());
        }
    }

    public void run(InputLine inputLine) throws Throwable {
        boolean isExit = false;
        String lastCommand = "";


        // Main loop. The default is to be running QDL commands and if there is a
        // command to the workspace, then it gets forwarded. 
        while (!isExit) {
            String input = workspaceCommands.readline(INDENT).trim();
            boolean storeLine = true;
            String out = null;
            if(input.equals(HISTORY_COMMAND) || input.startsWith(HISTORY_COMMAND + " ")){
                 out = doHistory(input);
                 if(out == null){
                     // nothing in the buffer
                     continue;
                 }
                 storeLine = false;
            }
            if(input.equals(REPEAT_COMMAND) || input.startsWith(REPEAT_COMMAND + " ")){
                 out = doRepeatCommand(input);
                storeLine = out == null;
            }

            if (storeLine) {
                   // Store it if it was not retrieved from the command history.
                workspaceCommands.commandHistory.add(0, input);
               }else{
                input = out; // repeat the command
            }
/*            if (input.equals("%")) {
                input = lastCommand;
            } else {
                lastCommand = input;
            }*/

            if (input.startsWith(")")) {
                switch (workspaceCommands.execute(input)) {
                    case RC_EXIT_NOW:
                        isExit = true;
                        return; // exit now, darnit.
                    case RC_NO_OP:
                    case RC_CONTINUE:
                        continue;
                    case RC_RELOAD:
                        workspaceCommands.say("not quite ready for prime time. Check back later");
                }
            }
            boolean echoMode = workspaceCommands.isEchoModeOn();
            boolean prettyPrint = workspaceCommands.isPrettyPrint();

            try {
                if (input != null && !input.isEmpty()) {
                    // if you try to evaluate only a ";" then you will get a syntax exception from
                    // the parser for an empty statement.
                    if (workspaceCommands.isEchoModeOn() && !input.endsWith(";")) {
                        input = input + ";"; // add it since they forgot
                    }
                    workspaceCommands.getInterpreter().execute(input);
                }
            } catch (Throwable t) {

                // If there is an exception while local mode is running, we don't want to trash the user's
                // echo mode, since that causes every subsequent command to fail at least until they
                // figure it `out and turn it back on.
                workspaceCommands.setEchoModeOn(echoMode);
                workspaceCommands.setPrettyPrint(prettyPrint);
                handleException(t);
            }
        }
    }


    protected String doRepeatCommand(String cmdLine) {
           if (cmdLine.contains("--help")) {
               workspaceCommands.say(REPEAT_COMMAND + " = repeat the last command. Identical to " + HISTORY_COMMAND + " 0");
               return null;
           }
           if (0 < workspaceCommands.commandHistory.size()) {
               return workspaceCommands.commandHistory.get(0);
           }
        workspaceCommands.say("no commands found");
           return null;
       }
       protected String doHistory(String cmdLine) {
           if (cmdLine.contains("--help")) {
               workspaceCommands.say(HISTORY_COMMAND + "[int] = either show the entire history (no argument) or execute the one at the given index.");
               workspaceCommands.say("See also:" + REPEAT_COMMAND);
               return null; // do nothing
           }
           // Either of the following work:             getNamedConfig(
           // /h == print history with line numbers
           // /h int = execute line # int, or print history if that fails
           StringTokenizer st = new StringTokenizer(cmdLine, " ");
           st.nextToken(); // This is the "/h" which we already know about
           boolean printIt = true;
           if (st.hasMoreTokens()) {
               try {
                   int lineNo = Integer.parseInt(st.nextToken());
                   if (0 <= lineNo && lineNo < workspaceCommands.commandHistory.size()) {
                       return workspaceCommands.commandHistory.get(lineNo);
                   }
               } catch (Throwable t) {
                   // do nothing, just print out the history.
               }
           }
           if (printIt) {
               for (int i = 0; i < workspaceCommands.commandHistory.size(); i++) {
                   // an iterator actually prints these in reverse order. Print them in order.
                   workspaceCommands.say(i + ": " + workspaceCommands.commandHistory.get(i));
               }
           }
           return null;
       }
    //  {'x':{'a':'b'},'c':'d'} ~ {'y':{'p':'q'},'r':'s'}
    public static void main(String[] args) throws Throwable {
        Vector<String> vector = new Vector<>();
        vector.add("dummy"); // Dummy zero-th arg.
        for (String arg : args) {
            vector.add(arg);
        }
        InputLine argLine = new InputLine(vector); // now we have a bunch of utilities for this
        WorkspaceCommands workspaceCommands;
        boolean isoTerminal = false;
        //System.setProperty("org.jline.terminal.dumb", "true"); // kludge for jline
        ISO6429IO iso6429IO = null; // only make one of these if you need it because jLine takes over all IO!
        if (argLine.hasArg("-ansi")) {
            iso6429IO = new ISO6429IO();
            workspaceCommands = new WorkspaceCommands(iso6429IO);
            isoTerminal = true;
        } else {
            workspaceCommands = new WorkspaceCommands(new BasicIO());
        }
        workspaceCommands.init(argLine);

        if (workspaceCommands.isRunScript()) {
            return;
        }
        if(isoTerminal){
            System.out.println("ISO 6429 terminal:" + iso6429IO.getTerminal().getName());

        }else{
            System.out.println("using generic terminal");
        }

        QDLWorkspace qc = new QDLWorkspace(workspaceCommands);
        ArrayList<String> functions = new ArrayList<>();
        functions.addAll(qc.workspaceCommands.getState().getMetaEvaluator().listFunctions(false));
        functions.addAll(qc.workspaceCommands.getState().listFunctions(true, null));
        if (isoTerminal) {
            // set up command completion
            iso6429IO.setCommandCompletion(functions);
            iso6429IO.getScreenSize(); // Figure this out.
            iso6429IO.setLoggingFacade(workspaceCommands.logger);
        }

        qc.run(new InputLine(new Vector()));

    }

}
