package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static edu.uiuc.ncsa.security.util.cli.CLIDriver.EXIT_COMMAND;

/**
 * Utilities that are used by any reasonable implementation of the Commands
 * interface.   You will probably want to extend this for your command processor.
 * <p>Created by Jeff Gaynor<br>
 * on 10/30/13 at  4:14 PM
 */
public abstract class CommonCommands implements Commands {
    protected CommonCommands(MyLoggingFacade logger) {
        this.logger = logger;
    }

   protected MyLoggingFacade logger;

    @Override
    public void debug(String x) {
        logger.debug(x);
    }

    @Override
    public void error(String x) {
        logger.error(x);
    }

    @Override
    public void info(String x) {
        logger.info(x);
    }

    @Override
    public boolean isDebugOn() {
        return logger.isDebugOn();
    }

    @Override
    public void setDebugOn(boolean setOn) {
        logger.setDebugOn(setOn);
    }

    @Override
    public void warn(String x) {
        logger.warn(x);
    }

    protected String defaultIndent = "";
    public static final String INDENT = "  "; // use this in implementations for consistent indenting.

    public BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    BufferedReader bufferedReader;

    protected String readline() {
        try {
            String x = getBufferedReader().readLine();
            if (x.equals(EXIT_COMMAND)) {
                throw new ExitException(EXIT_COMMAND + " encountered");
            }
            return x;

        } catch (IOException iox) {
            throw new GeneralException("Error, could not read the input line due to IOException", iox);
        }
    }


    /**
     * Prints with the default indent and a linefeed.
     *
     * @param x
     */
    protected void say(String x) {
        System.out.println(defaultIndent + x);
    }

    /**
     * prints with the current indent and a linefeed.
     *
     * @param x
     */
    protected void sayi(String x) {
        say(INDENT + x);
    }

    /**
     * Output the string without any linefeed. This is used for prompts.
     *
     * @param x
     */
    protected void say2(String x) {
        System.out.print(defaultIndent + x);
    }

    /**
     * Output a line without a linefeed and using the indent currently in force.
     * Generally this is used a part of a prompt and is followed by a call
     * to {@link #readline}.
     *
     * @param x
     */
    protected void sayi2(String x) {
        say2(INDENT + x);
    }

    /**
     * returns "true if the command has the flag --help in it. This is a cue from the user to show
     * the help for a given function. So it the function is called "X" and its help is in the function
     * "showXHelp" then a value of true from this should simply invoke "showXHelp" and return.
     *
     * @param inputLine
     * @return
     */
    protected boolean showHelp(InputLine inputLine) {
        if ((1 < inputLine.size()) && inputLine.getArg(1).equals("--help")) return true;
        return false;
    }

    protected boolean isOk(String x) {
        if (x == null || x.length() == 0) return false;
        return x.trim().toLowerCase().equals("y");
    }

    /**
     * Creates the input prompt and shows the supplied default value. This returns the default if the default value is chosen
     * and the input value otherwise. If supplied the default value is a null, then this is shown too.
     *
     * @param prompt
     * @param defaultValue
     * @return
     */
    protected String getInput(String prompt, String defaultValue) {
        sayi2(prompt + "[" + (defaultValue == null ? "(null)" : defaultValue) + "]:");
        String inLine = readline();
        if (isEmpty(inLine)) {
            // assumption is that the default value is required
            return defaultValue; // no input. User hit a return
        }
        return inLine;
    }

    protected boolean isEmpty(String x) {
        return x == null || x.length() == 0;
    }


    /**
     * Gets the placeholder for missing values. E.g. if a value (like a last name) is missing this will be displayed.
     *
     * @return
     */
    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    /**
     * This is used wherever a missing value is.
     */
    public String placeHolder = "-";

    /**
     * Returns the value if it is not empty of a placeholder if it is.
     *
     * @param x
     * @return
     */
    protected String getValue(String x) {
        return isEmpty(x) ? getPlaceHolder() : x;
    }

}
