package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.exceptions.ParsingException;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
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


    protected State getState() {
        return workspaceCommands.getState();
    }

    protected void handleException(Throwable t) {
        if((t instanceof ParseCancellationException) | (t instanceof ParsingException)){
            workspaceCommands.say("syntax error:" + (workspaceCommands.isDebugOn()?t.getMessage():"could not parse input"));
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
        workspaceCommands.say("error: " + t.getMessage());
    }


    public void run(InputLine inputLine) throws Throwable {
        boolean isExit = false;
        // Main loop. The default is to be running QDL commands and if there is a
        // command to the workspace, then it gets forwarded. 
        while (!isExit) {
            boolean executeLocalBuffer = false;
            boolean executeExternalFile = false;
            System.out.print(INDENT);
            String input = workspaceCommands.readline().trim();
            if (input.startsWith(")")) {
                switch (workspaceCommands.execute(input)) {
                    case RC_EXIT_NOW:
                        isExit = true;
                        return; // exit now, darnit.
                    case RC_NO_OP:
                    case RC_CONTINUE:
                        continue;

                    case RC_EXECUTE_LOCAL_BUFFER:
                        executeLocalBuffer = true;
                        break;
                    case RC_EXECUTE_EXTERNAL_BUFFER:
                        executeExternalFile = true;
                        break;
                }
            }
            try {
                // Turn off echo mode if running a buffer or very strange things can result
                if (executeLocalBuffer) {
                    if (workspaceCommands.getLocalBuffer() != null) {
                        boolean echoMode = workspaceCommands.isEchoModeOn();
                        workspaceCommands.setEchoModeOn(false);
                        workspaceCommands.getInterpreter().execute(workspaceCommands.getLocalBuffer().toString());
                        workspaceCommands.setEchoModeOn(echoMode);
                        continue;
                    }
                }
                if (executeExternalFile) {
                    if (workspaceCommands.getExternalBuffer() != null) {
                        try {
                            String xb = FileUtil.readFileAsString(workspaceCommands.getExternalBuffer().getAbsolutePath());
                            boolean echoMode = workspaceCommands.isEchoModeOn();
                            workspaceCommands.setEchoModeOn(false);
                            workspaceCommands.getInterpreter().execute(xb);
                            workspaceCommands.setEchoModeOn(echoMode);

                        } catch (IOException t) {
                            workspaceCommands.say("There was an error executing \"" + workspaceCommands.getExternalBuffer().getAbsolutePath() + "\"");
                        }
                    }
                    continue;
                }
                if(workspaceCommands.isEchoModeOn() && !input.endsWith(";")){
                    input = input + ";"; // add it since they forgot
                }
                workspaceCommands.getInterpreter().execute(input);
            } catch (Throwable t) {
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

        QDLWorkspace qc = new QDLWorkspace(workspaceCommands);

        qc.run(new InputLine(new Vector()));

    }

}
