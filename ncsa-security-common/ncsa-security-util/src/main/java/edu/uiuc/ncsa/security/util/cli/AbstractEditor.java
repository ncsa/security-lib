package edu.uiuc.ncsa.security.util.cli;

import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/20 at  10:23 AM
 */
public abstract class AbstractEditor {
    public static String PROMPT = "edit>";
    public static final String QUIT_COMMAND = "q"; // quit the editor
    public static final String QUIT_COMMAND_LONG = "quit"; // quit the editor
    public static final String VERBOSE_COMMAND = "v"; // toggles verbose mode on or off.
    public static final String VERBOSE_COMMAND_LONG = "verbose"; // toggles verbose mode on or off.
    public static final String HELP_COMMAND = "?"; // Help command


    protected boolean verboseOn = false;
    protected boolean isDone = false;

    /**
     * For use with informational messages.
     *
     * @param x
     */
    protected void say(String x) {
        getIoInterface().println(x);
    }

    String INDENT = "  ";
    /*
    Indent the line.
     */
    protected void sayi(String x){
        say(INDENT + x);
    }
    /**
     * Used for spitting out extra messages in verbose mode.
     *
     * @param x
     */
    protected void sayv(String x) {
        if (verboseOn) {
            say(x);
        }
    }

    public IOInterface getIoInterface() {
        if (ioInterface == null) {
            ioInterface = new BasicIO();
        }
        return ioInterface;
    }

    public void setIoInterface(IOInterface ioInterface) {
        this.ioInterface = ioInterface;
    }

    IOInterface ioInterface;


    protected String readline(String x) throws IOException {
        return getIoInterface().readline(x);
    }
    protected void verboseHelp() {
        say(VERBOSE_COMMAND + "|" + VERBOSE_COMMAND_LONG + " = toggles verbose mode on or off. This also will cause extra prompting for actions.");
        say("        The default is OFF");
    }
    public void execute() throws Throwable {
        while (!isDone) {
            String command = readline(PROMPT);
            EditorInputLine eil = null;
            boolean isUnkownCommand = true;
            try {
                eil = new EditorInputLine(command);
            } catch (Throwable t) {
                say("Sorry, there was an error processing that command");
                continue;
            }

            if (eil.isCommand(QUIT_COMMAND, QUIT_COMMAND_LONG)) {
                isDone = true;

                sayv("bye-bye...");
                break;
            }
            if (eil.isCommand(VERBOSE_COMMAND, VERBOSE_COMMAND_LONG)) {
                if (showHelp(eil)) {
                    verboseHelp();
                } else {
                    verboseOn = !verboseOn;
                    say("verbose mode is now " + (verboseOn ? "ON" : "OFF"));
                }
            }
        }
    }
    protected boolean showHelp(EditorInputLine eil) {
          return eil.hasArg(HELP_COMMAND);
      }

}