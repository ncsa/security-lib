package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.exceptions.ParsingException;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.Vector;

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

     protected MyLoggingFacade getLogger(){
         return workspaceCommands.logger;
     }

    protected void handleException(Throwable t) {
        if (getLogger() != null) {
            getLogger().error(t);
        }
        if ((t instanceof ParseCancellationException) | (t instanceof ParsingException)) {
            if(t.getMessage().contains("extraneous input")){
                workspaceCommands.say("syntax error: Unexpected or illegal character.");
            }else {
                workspaceCommands.say("syntax error:" + (workspaceCommands.isDebugOn() ? t.getMessage() : "could not parse input"));
            }
            return;
        }
        if (t instanceof IllegalStateException) {
            workspaceCommands.say("syntax error:" + t.getMessage());
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
   //         boolean executeLocalBuffer = false;
     //       boolean executeExternalFile = false;
            System.out.print(INDENT);
            String input = workspaceCommands.readline().trim();

            if (input.equals("%")) {
                input = lastCommand;
            } else {
                lastCommand = input;
            }
            if (input.startsWith(")")) {
                switch (workspaceCommands.execute(input)) {
                    case RC_EXIT_NOW:
                        isExit = true;
                        return; // exit now, darnit.
                    case RC_NO_OP:
                    case RC_CONTINUE:
                        continue;
/*
                    case RC_EXECUTE_LOCAL_BUFFER:
                        executeLocalBuffer = true;
                        break;
                    case RC_EXECUTE_EXTERNAL_BUFFER:
                        executeExternalFile = true;
                        break;*/
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


    public static void main(String[] args) throws Throwable {
        Vector<String> vector = new Vector<>();
        vector.add("dummy"); // Dummy zero-th arg.
        for (String arg : args) {
            vector.add(arg);
        }
        InputLine argLine = new InputLine(vector); // now we have a bunch of utilities for this
        WorkspaceCommands workspaceCommands = new WorkspaceCommands();
        workspaceCommands.init(argLine);
        if (workspaceCommands.isRunScript()) {
            return;
        }
        QDLWorkspace qc = new QDLWorkspace(workspaceCommands);

        qc.run(new InputLine(new Vector()));

    }

}
