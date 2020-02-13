package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.FunctorScript;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/25/18 at  10:51 AM
 */
public class ParserCommands extends CommonCommands {
    public ParserCommands(MyLoggingFacade logger, JFunctorFactory functorFactory) {
        super(logger);
        this.functorFactory = functorFactory;
    }

    @Override
    public String getPrompt() {
        return "parser>";
    }

    File file;

    protected void showSetFileHelp() {
        say("set_file file");
        say("   sets the file to be used for all subsequent operations. You can clear it with get file");
        say("   Note that if you supply a file name as an argument, that overrides this.");
        say("   See also show_file, clear_file");
    }

    public void set_file(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showSetFileHelp();
            return;
        }
        if (inputLine.hasArgs()) {
            file = new File(inputLine.getLastArg());
        } else {
            say("uh-oh! You seem to have forgotten the argument...");
        }
    }

    protected void showRunHelp() {
        say("run [filename]");
        say("   Executes the given file (includes path). This enables output of the given file");
        say("   If you have not set a current file and havenot supplied one you will be prompted");
        say("   Note that this runs the file every time and therefore reads it. This also gives you");
        say("   the option of editing it elsewhere and just debugging it here. ");
        say("   See also edit, set_file");
    }

    protected JFunctorFactory functorFactory;

    protected JFunctorFactory getFunctorFactory() {
        if (functorFactory == null) {
            //functorFactory = new JFunctorFactory(true);
            throw new IllegalStateException("Error: No functor factory has been configured.");
        }
        return functorFactory;
    }

    public void run(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showRunHelp();
            return;
        }

        File currentFile = file;
        if (0 < inputLine.getArgs().size()) {
            // use the file on the command line
            currentFile = new File(inputLine.getArg(0));
        } else {
            if (file == null) {
                String newFile = getInput("Enter file name", "");
                currentFile = new File(newFile);
                if (currentFile.exists()) {
                    say("Sorry, but the file \"" + currentFile.getAbsolutePath() + "\" does not exist...");
                    return;
                }
                if (!currentFile.isFile()) {
                    say("Sorry, but  \"" + currentFile.getAbsolutePath() + "\" is not a file...");
                    return;
                }
            }
        }
        FunctorScript script = new FunctorScript(getFunctorFactory());
        script.execute(currentFile);
    }

    public void show_file(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showShowFileHelp();
            return;
        }
        say("current file is \"" + (file == null ? "(none)" : file.getAbsolutePath()) + "\"");
    }

    protected void showShowFileHelp() {
        say("show_file");
        say("   Show the currently set file.");
        say("   See also: set_file, clear_file");
    }

    protected void showEditHelp() {
        say("edit [file]");
        say("   Edit a script. Note that if you do not supply a name, the current file is used. If that is not set");
        say("   If that is not set, you will be prompted. Exiting the editor, you will be asked to save the file ");
        say("   You originally specified, so there is no need to save it in the editor.");
        say("   Note that if the file you entered does not exist, it will be created when you save it.");
    }

    public void edit(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showEditHelp();
            return;
        }
        File currentFile = file;
        if (inputLine.hasArgs()) {
            currentFile = new File(inputLine.getLastArg());

        } else {
            if (currentFile == null) {
                String newFile = getInput("Enter file to edit", "");
                currentFile = new File(newFile);
            }
        }
        if (file == null) {
            String setFile = getInput("No default file. Set this to be the default (y/n)?", "y");
            if (setFile.equals("y")) {
                file = currentFile;
            }
        }

        try {
            List<String> buffer = new LinkedList<>();
            if (currentFile.exists()) {
                // Note we are getting this for the editor which means we need to get everything,
                // including comments and end of line markers (;'s). Just read it in.
                FileReader reader = new FileReader(currentFile);
                BufferedReader br = new BufferedReader(reader);
                String linein = br.readLine();
                while (linein != null) {
                    buffer.add(linein);
                    linein = br.readLine();
                }
                br.close();
            }

            LineEditor editor = new LineEditor(buffer);
            editor.execute();
            String saveIt = getInput("save it (y/n)?", "y");

            if (saveIt.equals("y")) {
                FileWriter fw = new FileWriter(currentFile);
                BufferedWriter bw = new BufferedWriter(fw);
                for (String x : buffer) {
                    bw.write(x);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                say("saved");
            }

        } catch (Throwable t) {
            throw new GeneralException("Error editing file \"" + t.getMessage() + "\"", t);
        }
    }

    protected void showClearFileHelp() {
        say("clear_file");
        say("   Clears any current file. This will have no effect if no file has been set.");
        say("   See also set_file, show_file");
    }

    public void clear_file(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showClearFileHelp();
            return;
        }
        file = null; // adios...
        say("done!");
    }
}
