package edu.uiuc.ncsa.qdl.gui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.screen.VirtualScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorColorConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorPalette;
import edu.uiuc.ncsa.qdl.exceptions.ParsingException;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.IOInterface;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import groovyjarjarantlr4.v4.runtime.misc.ParseCancellationException;

import java.awt.*;
import java.io.IOException;
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
        workspaceCommands.setIoInterface(workspace.ioInterface);
        workspaceCommands.init(argLine);
        workspace.run();
    }

    IOInterface ioInterface;
    WorkspaceCommands workspaceCommands;
    String INDENT = "    "; // prompt is 4 spaces.

    public QDLGUIWorkspace(WorkspaceCommands workspaceCommands) {
        this.workspaceCommands = workspaceCommands;
    }
    protected void setupTerminal() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        terminal = factory.createTerminal();
        screen = new TerminalScreen(terminal);
        textGraphics = screen.newTextGraphics();
        screen.startScreen();
        screen.refresh();



        // String is of form # RGB where each color is a hex number 0 - 255 aka x0 - xFF
        //terminal.setForegroundColor(TextColor.RGB.Factory.fromString("#0000FF"));

        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        terminal.setCursorVisible(true);
        terminal.setCursorPosition(0, 0);
        terminal.exitPrivateMode();
        terminal.enableSGR(SGR.BOLD);

      //  terminal.flush();
        ioInterface = new LanternaIO(terminal, screen);

    }

    protected void init(InputLine argLine) throws Throwable {
        // for terminals
        setupTerminal();

        // For screens --  still in debug stage. 
      // setupScreen();
    }

    protected void setupScreen() throws IOException {
        TerminalEmulatorPalette myPallette = new TerminalEmulatorPalette(
                Color.WHITE, // sets default
                Color.RED, // sets default Bright
                Color.BLUE, // sets background. This is the only reason for this constructor
                Color.black,
                Color.BLACK,
                Color.red,
                Color.RED,
                Color.green,
                Color.GREEN,
                Color.yellow,
                Color.YELLOW,
                Color.blue,
                Color.BLUE,
                Color.magenta,
                Color.MAGENTA,
                Color.cyan,
                Color.CYAN,
                Color.white,
                Color.WHITE

        );

        //TerminalEmulatorPalette palette = new TerminalEmulatorPalette()
        DefaultTerminalFactory factory = new DefaultTerminalFactory();

        TerminalEmulatorColorConfiguration colorConfig = TerminalEmulatorColorConfiguration.newInstance(myPallette);
        factory.setTerminalEmulatorColorConfiguration(colorConfig);
        //DefaultTerminalFactory factory = new DefaultTerminalFactory();
        terminal = factory.createTerminal();
        screen = new VirtualScreen(new TerminalScreen(terminal));
        textGraphics = screen.newTextGraphics();
        terminal.addResizeListener((terminal1, newSize) -> {
            // Be careful here though, this is likely running on a separate thread. Lanterna is threadsafe in
            // a best-effort way so while it shouldn't blow up if you call terminal methods on multiple threads,
            // it might have unexpected behavior if you don't do any external synchronization
            textGraphics.drawLine(5, 3, newSize.getColumns() - 1, 3, ' ');
            textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
            textGraphics.putString(5 + "Terminal Size: ".length(), 3, newSize.toString());
            try {
                terminal1.flush();
            } catch (IOException e) {
                // Not much we can do here
                throw new RuntimeException(e);
            }
        });
        int x = terminal.getTerminalSize().getRows();
        textGraphics.setForegroundColor(TextColor.RGB.Factory.fromString("#FFFF00"));
        textGraphics.setBackgroundColor(TextColor.RGB.Factory.fromString("#0000FF"));
        textGraphics.enableModifiers(SGR.BOLD);
        
        ioInterface = new LanternaScreenIO(terminal,screen,textGraphics);
     //   screen.scrollLines(0, x, x); // scroll in units of 25 lines.
        //screen.
        screen.startScreen();
        screen.refresh();
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
            String input = ioInterface.readline(INDENT).trim();

            if (input.equals("%")) {
                input = lastCommand;
            } else {
                lastCommand = input;
            }
            if (input.startsWith(")")) {
                switch (workspaceCommands.execute(input)) {
                    case RC_EXIT_NOW:
                        isExit = true;
                        terminal.flush();
                        terminal.close();
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

}

