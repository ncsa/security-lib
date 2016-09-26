package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/17/13 at  3:34 PM
 */
public class CommandsTest implements Commands {

    public static class FooCommands implements Commands {
        @Override
        public void debug(String x) {
            say(x);
        }

        @Override
        public boolean isDebugOn() {
            return true;
        }

        @Override
        public void setDebugOn(boolean setOn) {

        }

        @Override
        public void info(String x) {
            say(x);
        }

        @Override
        public void warn(String x) {
            say(x);
        }

        @Override
        public void error(String x) {
            say(x);
        }

        public FooCommands() {
        }

        void say(String x) {
            System.out.println(x);
        }

        @Override
        public String getPrompt() {
            return "foo >";
        }


        public void query(InputLine inputLine) {
            say("query invoked");
        }
    }

    @Override
    public String getPrompt() {
        return "cli test>";
    }

    public static void main(String[] args) throws Exception {
        CommandsTest c = new CommandsTest();
        FooCommands foo = new FooCommands();
        CLIDriver cliDriver = new CLIDriver(c, foo);
        cliDriver.start();
    }

    protected void say(String x) {
        System.out.println(x);
    }

    @Override
    public void debug(String x) {
        say(x);

    }

    @Override
    public boolean isDebugOn() {
        return true;
    }

    @Override
    public void setDebugOn(boolean setOn) {

    }

    @Override
    public void info(String x) {
        say(x);
    }

    @Override
    public void warn(String x) {
        say(x);
    }

    @Override
    public void error(String x) {
        say(x);
    }

    public void load(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showLoadHelp();
            return;
        }
        say("load something");
    }


    protected void showLoadHelp() {
        say("loads a configuration from the file. The options are");
        say("   load fileName - Assumes a single configuration in the fully qualified name of the file and loads it, setting it active");
        say("   load fileName configName - loads the configuration named \"configName\" from the fully qualified name of the file and sets it active");
        say("\nExample\n");
        say("   load /var/www/config/config.xml default\n");
        say("loads the configuration named \"default\" from the file named \"config.xml\" in the directory \"/var/www/config\"\n");
    }

    public AbstractEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(AbstractEnvironment environment) {
        this.environment = environment;
    }

    AbstractEnvironment environment;


    public void show(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showHelp();
            return;
        }
        say("show command should do something");
    }

    protected void showHelp() {
        say("help for the show command");
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

    public void about(InputLine inputLine) {
        say("**********************************************************");
        say("* OA4MP CLI (Command Line Interpreter)                   *");
        say("* Version 1.0                                            *");
        say("* By Jeff Gaynor  NCSA                                   *");
        say("*  (National Center for Supercomputing Applications)     *");
        say("*                                                        *");
        say("* type 'help' for a list of commands                     *");
        say("*      'exit/quit' to end this session.                  *");
        say("**********************************************************");
    }

    public void help() {
        say("");
        say("Supported Commands\n=======================");
        say("cd folderTitle -- Change directory to the given folder.");
        say("  The usual \".\" and \"..\" are supported");
        say("cp (-r) object targetFolder -- copy an object. If the flag is used,");
        say("  the copy will be recursive on any directories.");
        say("createAnother (-title title) objectTitle [destination] -- Creates another object");
        say("  of the same type as the given object in the destination folder. If the -title is");
        say("  given, the new object has the given title, otherwise it has the same title as the");
        say("  current object.");
        say("edit objectName -- Invokes the editor for the objects settings.");
        say("exit/quit -- Stop execution and quit.");
        say("help -- More or less this help.");
        say("ls objectName -- Lists the object by title, along with other information.");
        say("  If the object is a folder, the contents are listed, much like a standard directory listing,");
        say("  but with an additional col. showing the size of the system level information.");
        say("mkdir folderName -- Make a new folder");
        say("move -r source target -- Moves an object from one location to another.");
        say("  The \" - r \" flag indicates that this should be done recursively if possible.");
        say("rm object -- Deletes an object. If invoked on a folder without the switch");
        say("  folder, an error is returned. The switch permits this");
        say("  to be recursive, deleting all subobject and folders.");
        say("rmdir (-r) folder -- Removes the (empty) folder.");
        say("\nYou can type the name of a command and --help to get more information, e.g. \n\ncp --help\n");
        say("Would print detailed on the copy command.");
    }
}
