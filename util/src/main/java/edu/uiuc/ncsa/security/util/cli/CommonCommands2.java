package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.util.LoggingConfigLoader;

/**
 * Utilities that are used by any reasonable implementation of the Commands
 * interface.   You need to extend this for your command processor.
 * <p>Created by Jeff Gaynor<br>
 * on 10/30/13 at  4:14 PM
 */
public abstract class CommonCommands2 extends AbstractCommandsImpl {

    public CommonCommands2(CLIDriver driver) {
        super(driver);
    }



    @Override
    public IOInterface getIOInterface() {
        return getDriver().getIOInterface();
    }


    public void print_help() throws Exception {
        say("All commands have detailed help by typing:");
        say("command --help");
        say("--Environment commands: these control variables managed by this component");
        say("You may access these by enclosing them in delimters, e.g. ${var}");
        sayi("clear_env = clear the environment");
        sayi("print_env = print all variables in the environment");
        sayi("read_env = read either a properties or JSON file contain key value pairs ");
        sayi("save_env = write the current environment to a file for later use.");
        sayi("set_env = set a variable for use");

        say("--Other commands:");
        sayi("print_help = print this help out");
        sayi("set_output_on = turn on/off all output (used mostly in batch files)");
        sayi("set_verbose_on = turn verbose off or on");
        sayi("echo = print the argument. Useful in batch scripts.");
        sayi("version = the version of this component.");

    }


    /**
     * Double indent -- useful for lists.
     *
     * @param x
     */
    protected void sayii(String x) {
        say(INDENT + INDENT + x);
    }

    /**
     * Output the string without any linefeed. This is used for prompts.
     *
     * @param x
     */
    protected void say2(String x) {
        getIOInterface().print(defaultIndent + x);
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

      public boolean isVerbose() {
        return getDriver().isVerbose();
    }

    protected void setVerboseHelp() {
        say("set_verbose_on true | false | on | off  ");
        sayi("Chiefly for batch files to toggle whether or not there is verbose mode on.");
        say("See also set_output_on");
    }

    /**
     * So batch files can change whether or not they are verbose
     *
     * @param inputLine
     */
    public void set_verbose_on(InputLine inputLine) throws Exception {
        String raw = inputLine.getLastArg();
        Boolean b = getDriver().isTrue(raw );
        if(b != null){
            getDriver().setVerbose(b);
        }else{
            say("unknown verbose option \"" + raw + "\"");
        }

    }



    protected void setOutputOnHelp() {
        say("set_output_on true | on | false | off");
        sayi("This is used chiefly with batch files, to toggle whether or not there is output directed to the console,");
        sayi("true means to turn on output, false means to suspend it");
        say("See also: set_verbose_on");
    }

    public void set_output_on(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            setOutputOnHelp();
            return;
        }
        // A little bit trickier than it looks since we have an internal flag for the negation of this.
        // We also want to be sure they really want to turn off output, so we only test for logical true
        // That way if they screw it up they still at least get output...
        // Also, this will only set the output to off if it is explicitly told to, otherwise users can shut it
        // off by accident and not get any output and not know why.
        String raw = inputLine.getLastArg();
        Boolean b = getDriver().isTrue(raw);
        if(b!= null){
            getDriver().setOutputOn(b);
        }else{
            say("unknown value \"" + raw + "\"");
        }
    }


    protected void versionHelp() {
        say("version ");
        sayi("prints the current version number of this program.");
    }

    public void version(InputLine inputLine) {
        if (showHelp(inputLine)) {
            versionHelp();
            return;
        }
        say("* CLI (Command Line Interpreter) Version " + LoggingConfigLoader.VERSION_NUMBER);
    }

/*
    protected void echoHelp() {
        say("echo arg0 arg1...");
        sayi("Simply echos the arg(s) to the console . This is extremely useful in scripts.");
        say("See also set_output_on");
    }

    public void echo(InputLine inputLine) {
        if (showHelp(inputLine)) {
            echoHelp();
            return;
        }
        say(inputLine.format());
    }
*/


}
