package edu.uiuc.ncsa.gui.p2;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import edu.uiuc.ncsa.qdl.exceptions.ParsingException;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import static edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/10/20 at  7:20 AM
 */
public class QDLGUIWorkspace {
    public static void main(String[] args) throws Throwable {
        Vector<String> vector = new Vector<>();
        vector.add("dummy"); // Dummy zero-th arg.
        for (String arg : args) {
            vector.add(arg);
        }
        InputLine argLine = new InputLine(vector); // now we have a bunch of utilities for this
        WorkspaceCommands workspaceCommands = new WorkspaceCommands();
        if (workspaceCommands.isRunScript()) {
            return;
        }
        QDLGUIWorkspace workspace = new QDLGUIWorkspace(workspaceCommands);
        workspace.init(argLine);
        workspace.run();
    }

    WorkspaceCommands workspaceCommands;
    String INDENT = "    "; // prompt is 4 spaces.

    public QDLGUIWorkspace(WorkspaceCommands workspaceCommands) {
        this.workspaceCommands = workspaceCommands;
    }

    protected void init(InputLine argLine) throws Throwable {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        terminal = factory.createTerminal();
        screen = new TerminalScreen(terminal);
        textGraphics = screen.newTextGraphics();
        screen.startScreen();
        screen.refresh();
        terminal.setCursorVisible(true);
        terminal.setCursorPosition(0, 0);

        terminal.setForegroundColor(TextColor.ANSI.YELLOW);
        // String is of form # RGB where each color is a hex number 0 - 256 aka x0 - xFF
        //terminal.setForegroundColor(TextColor.RGB.Factory.fromString("#0000FF"));
        terminal.enableSGR(SGR.BOLD);
//        terminal.wait(2000);
        System.out.println("Init complete");
        workspaceCommands.init(argLine);


    }

    Terminal terminal;
    Screen screen;
    TextGraphics textGraphics;
    protected void run() throws Throwable {
        boolean isExit = false;
        String lastCommand = "";

        // Main loop. The default is to be running QDL commands and if there is a
        // command to the workspace, then it gets forwarded.
        while (!isExit) {
            System.out.print(INDENT);
            //String input = workspaceCommands.readline().trim();
            String input = readInput();
            System.out.println(""); // advances cursor to next line in GUI.

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

    protected void handleException(Throwable t) {
        t.printStackTrace();
        if (getLogger() != null) {
            getLogger().error(t);
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

    protected MyLoggingFacade getLogger() {
        return workspaceCommands.logger;
    }

    ArrayList<String> commandBuffer = new ArrayList<>();

    protected String readInput() throws IOException {
        boolean keepRunning = true;
        int line = 0;
        StringBuffer stringBuffer = new StringBuffer();
        int currentBufferPosition = 0;


        while (keepRunning) {
            KeyStroke keyStroke = terminal.readInput(); //Block input or this does not draw right at all.
            if (keyStroke != null) {
                switch (keyStroke.getKeyType()) {
                    case MouseEvent:
                        System.out.println("Yo!" + keyStroke);
                        break;


                    case Escape:
                        return stringBuffer.toString() + "\n";
                    case EOF: // If there is some issue shutting down the JVM, it starts spitting these out. Just exit.
                        keepRunning = false;
                        break;
                    case Enter:
                        commandBuffer.add(0, stringBuffer.toString());
                        return stringBuffer.toString();

/*
                        terminal.setCursorPosition(0, ++line); // ++ so it advances on first return.
                        terminal.flush();
                        currentBufferPosition = 0;
                        stringBuffer = new StringBuffer();
                        break;
*/
                    case ArrowUp:
                        terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                        System.out.print(commandBuffer.get(currentBufferPosition));
                        currentBufferPosition = Math.min(currentBufferPosition + 1, commandBuffer.size() - 1);
                        System.out.println("Buff pos = " + currentBufferPosition);
                        terminal.flush();
                        break;
                    case ArrowDown:

                        terminal.setForegroundColor(TextColor.ANSI.YELLOW);
                        currentBufferPosition = Math.max(currentBufferPosition - 1, 0);
                        System.out.print(commandBuffer.get(currentBufferPosition));
                        terminal.flush();
                        break;
                    case Character:
                        currentBufferPosition = 0;

                        stringBuffer.append(keyStroke.getCharacter());
                        terminal.putCharacter(keyStroke.getCharacter());
                        terminal.flush();
                        break;
                    case ArrowLeft:
                        currentBufferPosition = 0;

                        terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case ArrowRight:
                        // Move cursor right, don't overrun end of line.
                        currentBufferPosition = 0;
                        terminal.setCursorPosition(
                                Math.min(stringBuffer.length() - 1, terminal.getCursorPosition().getColumn() + 1),
                                terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case Backspace:
                        if (stringBuffer != null && 0 < stringBuffer.length()) {
                            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                            terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            terminal.putCharacter(' '); // blank what was there
                            terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            terminal.flush();
                        }
                        break;
                    default:

                }
            }
        }
        return "";
    }
}

