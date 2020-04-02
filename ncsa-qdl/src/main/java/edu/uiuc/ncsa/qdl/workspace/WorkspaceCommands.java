package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoader;
import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils;
import edu.uiuc.ncsa.qdl.config.QDLEnvironment;
import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.*;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.vfs.VFSFileProvider;
import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.LoggerProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.CommandLineTokenizer;
import edu.uiuc.ncsa.security.util.cli.ExitException;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import edu.uiuc.ncsa.security.util.cli.LineEditor;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants.*;
import static edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils.*;
import static edu.uiuc.ncsa.security.util.cli.CLIDriver.EXIT_COMMAND;

/**
 * This is the helper class to the {@link QDLWorkspace} that does the grunt work of the ) commands.
 * <p>Created by Jeff Gaynor<br>
 * on 1/30/20 at  9:21 AM
 */
public class WorkspaceCommands implements Logable {
    MyLoggingFacade logger;

    XProperties env;

    CommandLineTokenizer CLT = new CommandLineTokenizer();
    protected static final String FUNCS_COMMAND = ")funcs";
    protected static final String HELP_COMMAND = ")help"; // show various types of help
    protected static final String OFF_COMMAND = ")off";
    protected static final String BUFFER_COMMAND = ")buffer";
    protected static final String EXECUTE_COMMAND = ")";
    protected static final String MODULES_COMMAND = ")modules";
    protected static final String LOAD_COMMAND = ")load"; // grab a file and run it
    protected static final String SAVE_COMMAND = ")save";
    protected static final String CLEAR_COMMAND = ")clear";
    protected static final String RUN_COMMAND = ")";
    protected static final String VARS_COMMAND = ")vars";
    protected static final String ENV_COMMAND = ")env";
    protected static final String WS_COMMAND = ")ws";
    protected static final String EDIT_COMMAND = ")edit";

    public static final int RC_NO_OP = -1;
    public static final int RC_CONTINUE = 1;
    public static final int RC_EXIT_NOW = 0;
    public static final int RC_EXECUTE_LOCAL_BUFFER = 10;
    public static final int RC_EXECUTE_EXTERNAL_BUFFER = 101;

    /*
    The pattern is
    command action args
    e.g.
    )ws load filename
    The next constants just name them so there aren't any boo-boos
     */
    protected int COMMAND_INDEX = 0; // e.g:   )ws
    protected int ACTION_INDEX = 1; // e.g:    load
    protected int FIRST_ARG_INDEX = 2; //e.g:  filename

    protected void splashScreen() {
        if (showBanner) {
            say(banner);
            say("*****************************************");
            say("Welcome to the QDL Workspace");
            say("Version " + QDLVersion.VERSION);
            say("Type " + HELP_COMMAND + " for help.");
            say("*****************************************");
        } else {
            say("QDL Workspace, version " + QDLVersion.VERSION);
        }
    }

    boolean showBanner = true;
    String banner =
            "- - . -  / - . .  / . - . . \n" +  // morse code for Q D L
                    "(  ___  )(  __  \\ ( \\      \n" +
                    "| (   ) || (  \\  )| (      \n" +
                    "| |   | || |   | || |      \n" +
                    "| | /\\| || |   ) || |      \n" +
                    "| (_\\ \\ || (__/  )| (____/\\\n" +
                    "(____\\/_)(______/ (_______/\n" +
                    "- - . -  / - . .  / . - . . ";

    protected void showRunHelp() {
        say("This is the QDL (pronounced 'quiddle') workspace.");
        say("You may enter commands and execute them much like any other interpreter.");
        say("There are several commands available to help you manage this workspace.");
        say("Generally these start with a right parenthesis, e.g., ')off' (no quotes) exits this program.");
        say("Here is a quick summary of what they are and do.");
        sayi(BUFFER_COMMAND + " -- commands relating to using buffers.");
        sayi(CLEAR_COMMAND + " -- clear the state of the workspace. All variables, functions etc. will be lost.");
        sayi(EDIT_COMMAND + " -- commands relating to running the line editor.");
        sayi(ENV_COMMAND + " -- commands relating to environment variables in this workspace.");
        sayi(EXECUTE_COMMAND + "     -- short hand to execute whatever is in the current buffer.");
        sayi(FUNCS_COMMAND + " -- list all of the imported and user defined functions this workspace knows about.");
        sayi(HELP_COMMAND + " -- this message.");
        sayi(MODULES_COMMAND + " -- lists all the imported modules this workspace knows about.");
        sayi(OFF_COMMAND + " -- exit the workspace.");
        sayi(LOAD_COMMAND + " file -- Load a file of QDL commands and execute it immediately in the current workspace.");
        sayi(VARS_COMMAND + " -- lists all of the variables this workspace knows about.");
        sayi(WS_COMMAND + " -- commands relating to this workspace.");
    }

    public int execute(String inline) {
        inline = TemplateUtil.replaceAll(inline, env); // allow replacements in commands too...
        InputLine inputLine = new InputLine(CLT.tokenize(inline));

        switch (inputLine.getCommand()) {
            case BUFFER_COMMAND:
                return doBufferCommand(inputLine);
            case CLEAR_COMMAND:
                return doWSClear(inputLine);
            case EDIT_COMMAND:
                return doEditCommand(inputLine);
            case EXECUTE_COMMAND:
                if (useLocalBuffer) return RC_EXECUTE_LOCAL_BUFFER;
                if (externalBuffer != null) return RC_EXECUTE_EXTERNAL_BUFFER;
                say("No buffers to execute");
                return RC_CONTINUE;
            case ENV_COMMAND:
                return doEnvCommand(inputLine);
            case FUNCS_COMMAND:
                return doFuncs(inputLine);
            case HELP_COMMAND:
                return doHelp(inputLine);
            case MODULES_COMMAND:
                return doModulesCommand(inputLine);
            case OFF_COMMAND:
                if (inputLine.hasArg("y")) {
                    return RC_EXIT_NOW;
                }
                if (readline("Do you want to exit? (y/n)").equals("y")) {
                    return RC_EXIT_NOW;
                }
                say("System exit cancelled.");
                return RC_CONTINUE;
            case VARS_COMMAND:
                return doVars(inputLine);
            case WS_COMMAND:
                return doWS(inputLine);
        }
        say("Unknown command.");
        return RC_NO_OP;
    }

    /**
     * This will invoke the line editor. Note that this has the following modes:
     * <ul>
     *     <li>)edit (no arg) -- invoke the editor on the local buffer (if that is enabled) or the file (if that is
     *     enabled. Fail otherwise</li>
     *     <li>)edit local -- force editing the local buffer</li>
     *     <li>)edit file [path] -- edit an external file. If none is given, this will attempt to edit the one set by the
     *     )buffer command.</li>
     *
     * </ul>
     * Note that no )edit commands imply changing the current state of what buffer is being used.
     *
     * @param inputLine
     * @return
     */
    private int doEditCommand(InputLine inputLine) {
        if (!inputLine.hasArgAt(ACTION_INDEX)) {
            if (useLocalBuffer) {
                return doEditLocal();
            } else {
                return doEditExternal();
            }
        }// end case where there is no explicit argument.
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "local":
                // explicit call to edit the local buffer
                return doEditLocal();
            case "file":
                if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
                    // explicit call to edit the external file.
                    return doEditExternal();
                }
                return doEditFile(new File(inputLine.getArg(FIRST_ARG_INDEX)));
        }
        return RC_CONTINUE;
    }

    /**
     * Edit an external file.
     *
     * @param file
     * @return
     */
    private int doEditFile(File file) {
        try {
            LineEditor lineEditor = new LineEditor(FileUtil.readFileAsString(file.getAbsolutePath()));
            lineEditor.execute();
            // The user has to save their changes in the editor.
            if (!lineEditor.isSaved()) {
                String inLine = readline("Did you want to save the buffer? (y/n):");
                if (inLine.equals("y")) {
                    FileUtil.writeStringToFile(file.getAbsolutePath(), lineEditor.bufferToString());
                }
            }

        } catch (Throwable t) {
            say("There was a problem editing the external buffer:" + t.getMessage());
            return RC_CONTINUE;
        }
        return RC_CONTINUE;
    }

    /**
     * Edits the {@link #externalBuffer} that has been set and issues an error message if it is not set.
     *
     * @return
     */
    private int doEditExternal() {
        if (externalBuffer == null) {
            say("Sorry, there is no external buffer set. You must specify one or invoke the editor with the file name.");
            return RC_CONTINUE;
        }
        return doEditFile(externalBuffer);

    }

    private int doEditLocal() {
        if (localBuffer == null) {
            say("New local buffer created.");
        }
        LineEditor lineEditor = new LineEditor(localBuffer.toString());
        try {
            lineEditor.execute();
            localBuffer = new StringBuffer();
            localBuffer.append(lineEditor.bufferToString());
        } catch (Throwable t) {
            say("There was an error during editing: " + t.getMessage());
        }
        return RC_CONTINUE;
    }

    /**
     * This has several states:
     * <ul>
     *     <li>clear (no args) -- clear the environment</li>
     *     <li>drop name -- remove a variable</li>
     *     <li>get name - show the value of a variable</li>
     *     <li>list (no args) - print the entire environment</li>
     *     <li>load filename - add the variables in the file to the current state</li>
     *     <li>save [file] - save the environment to a file. No arg means to use the current env file.</li>
     *     <li>set key value - set a variable to a given value.</li>
     *     <li></li>
     * </ul>
     *
     * @param inputLine
     * @return
     */
    private int doEnvCommand(InputLine inputLine) {
        if (!inputLine.hasArgAt(ACTION_INDEX)) {
            return doEnvList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "clear":
                env = new XProperties();
                say("Environment cleared.");
            case "drop":
                return doEnvDrop(inputLine);
            case "get":
                return doEnvGet(inputLine);
            case "list":
                return doEnvList(inputLine);
            case "load":
                return doEnvLoad(inputLine);
            case "save":
                return doEnvSave(inputLine);
            case "set":
                return doEnvSet(inputLine);
            case "name":
                if (envFile == null) {
                    say("No environment file has been set.");
                } else {
                    say(envFile.getAbsolutePath());
                }
                return RC_CONTINUE;
        }
        say("Unknown environment command.");
        return RC_CONTINUE;
    }

    private int doEnvSave(InputLine inputLine) {
        File currentFile = envFile;
        if (inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            currentFile = new File(inputLine.getArg(FIRST_ARG_INDEX));
        }

        try {
            FileWriter fileWriter = new FileWriter(currentFile);
            String message = "Environment saved to \"" + currentFile.getAbsolutePath() + "\" at " + Iso8601.date2String(new Date());
            env.store(fileWriter, message);
            say(message);
        } catch (Throwable t) {
            say("Saving the environment to \"" + currentFile.getAbsolutePath() + "\" failed:" + t.getMessage());
        }

        return RC_CONTINUE;
    }

    private int doEnvLoad(InputLine inputLine) {
        // load a file
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Sorry but you must specify a file to load it");
            return RC_CONTINUE;
        }
        File f = resolveAgainstRoot(inputLine.getArg(FIRST_ARG_INDEX));
        if (!f.exists()) {
            say("Sorry, but +\"" + f.getAbsolutePath() + "\" does not exist.");
            return RC_CONTINUE;
        }
        if (!f.isFile()) {
            say("Sorry, but +\"" + f.getAbsolutePath() + "\" is not a file.");
            return RC_CONTINUE;
        }
        if (!f.canRead()) {
            say("Sorry, but +\"" + f.getAbsolutePath() + "\" is not readable.");
            return RC_CONTINUE;
        }
        env.load(f);
        say(f.getAbsolutePath() + " loaded.");
        return RC_CONTINUE;
    }

    private int doEnvDrop(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Sorry, you must supply an environment variable name to remove it.");
            return RC_CONTINUE;
        }
        String pName = inputLine.getArg(FIRST_ARG_INDEX);
        env.remove(pName);
        say(pName + " rempved from the environment");
        return RC_CONTINUE;
    }

    private int doEnvGet(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Sorry, you must supply an environment variable name to get its value.");
            return RC_CONTINUE;
        }
        String pName = inputLine.getArg(FIRST_ARG_INDEX);
        say(env.getString(pName));
        return RC_CONTINUE;
    }

    private int doEnvSet(InputLine inputLine) {
        String pName = inputLine.getArg(FIRST_ARG_INDEX);
        if (!inputLine.hasArgAt(1 + FIRST_ARG_INDEX)) {
            say("Sorry, no value supplied.");
            return RC_CONTINUE;
        }
        StringBuffer value = new StringBuffer();
        // REMEMBER that the getArgCount is the number of arguments and the 0th element is the command
        boolean isFirstPass = true;
        for (int i = FIRST_ARG_INDEX + 1; i < inputLine.getArgCount() + 1; i++) {
            if (isFirstPass) {
                value.append(inputLine.getArg(i));
                isFirstPass = false;
            } else {
                value.append(" " + inputLine.getArg(i));
            }
        }
        env.put(pName, value.toString());
        return RC_CONTINUE;
    }

    private int doEnvList(InputLine inputLine) {
        if (env == null || env.isEmpty()) {
            say("(empty)");
        } else {
            say("Current environment variables:");
            say(env.toString(1));
        }

        return RC_CONTINUE;
    }

    public File getExternalBuffer() {
        return externalBuffer;
    }

    File externalBuffer;

    public StringBuffer getLocalBuffer() {
        return localBuffer;
    }

    StringBuffer localBuffer = new StringBuffer();

    /**
     * This has a few options.<br/><br/>
     * )buffer local on|off|clear|save|load|append|show -- turn on or off local buffering<br/>
     * )buffer file path -- sets an external file as the buffer<br/><br/>
     *
     * @param inputLine
     * @return
     */
    private int doBufferCommand(InputLine inputLine) {
        if (!inputLine.hasArgAt(ACTION_INDEX)) {
            say("You must specify an action for this command. Options are local or file.");
            return RC_CONTINUE;
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "local":
                return doLocalBuffer(inputLine);
            case "file":
                return doFileBuffer(inputLine);
        }
        say("Unrecognized buffer command.");
        return RC_CONTINUE;
    }

    private int doFileBuffer(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("You must supply a file name.");
        } else {
            externalBuffer = new File(inputLine.getArg(FIRST_ARG_INDEX));
            say("Buffer set to \"" + externalBuffer.getAbsolutePath() + (externalBuffer.exists() ? "." : ". Warning -- it does not exist."));
        }
        useLocalBuffer = false;
        return RC_CONTINUE;
    }

    boolean useLocalBuffer = true; // default at startup so )edit command just works

    private int doLocalBuffer(InputLine inputLine) {

        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Unknown buffer command.");
            return RC_CONTINUE;
        }
        switch (inputLine.getArg(FIRST_ARG_INDEX)) {
            case "clear":
                localBuffer = new StringBuffer();
                say("Local buffer cleared");
                return RC_CONTINUE;
            case "off":
                useLocalBuffer = false;
                say("Local buffer disabled.");
                return RC_CONTINUE;
            case "on":
                useLocalBuffer = true;
                say("Local buffer enabled.");
                return RC_CONTINUE;
            case "prepend":
                if (!inputLine.hasArgAt(1 + FIRST_ARG_INDEX)) {
                    say("You must supply a file name for this operation.");
                    return RC_CONTINUE;
                }
                StringBuffer tempSB = new StringBuffer();
                try {
                    tempSB.append(FileUtil.readFileAsString(inputLine.getArg(1 + FIRST_ARG_INDEX)));
                    tempSB.append("\n" + localBuffer.toString());
                    localBuffer = tempSB;
                    say("Buffer updated.");
                } catch (Throwable t) {
                    say("Sorry, could not read the file \"" + inputLine.getArg(1 + FIRST_ARG_INDEX) + "\":" + t.getMessage());
                    break;
                }
                useLocalBuffer = true;
                return RC_CONTINUE;
            case "show":
                if (localBuffer != null) {
                    say(localBuffer.toString());
                }
                return RC_CONTINUE;
            case "save":
                return doBufferSave(inputLine);
            case "load":
                // case falls through!!
                localBuffer = new StringBuffer();
            case "append":
                if (!inputLine.hasArgAt(1 + FIRST_ARG_INDEX)) {
                    say("You must supply a file name for this operation.");
                    return RC_CONTINUE;
                }
                try {
                    localBuffer.append(FileUtil.readFileAsString(inputLine.getArg(1 + FIRST_ARG_INDEX)));
                    say("Buffer updated.");
                } catch (Throwable t) {
                    say("Sorry, could not read the file \"" + inputLine.getArg(1 + FIRST_ARG_INDEX) + "\":" + t.getMessage());
                    break;
                }
                useLocalBuffer = true;
                return RC_CONTINUE;


        }
        say("say unknown buffer command.");
        return RC_CONTINUE;
    }

    private int doBufferSave(InputLine inputLine) {
        if (!inputLine.hasArgAt(1 + FIRST_ARG_INDEX)) {
            say("Sorry, you need to supply a file path to save the local buffer.");
            return RC_CONTINUE;
        }
        File targetFile = resolveAgainstRoot(inputLine.getArg(1 + FIRST_ARG_INDEX));
        if (targetFile.exists()) {
            if (!targetFile.isFile()) {
                say("Sorry, but \"" + targetFile.getAbsolutePath() + "\" is not a file and cannot be overwritten. Aborting...");
                return RC_CONTINUE;
            }
            if (!readline("\"" + targetFile.getAbsolutePath() + "\" exists. Do you want to overwrite it? (y/n):").equals("y")) {
                say("aborting...");
                return RC_CONTINUE;
            }
        }
        try {
            if (localBuffer == null) {
                say("Sorry, the buffer is empty. There is nothing to save.");
            } else {
                FileUtil.writeStringToFile(targetFile.getAbsolutePath(), localBuffer.toString());
            }
        } catch (Throwable t) {
            say("Sorry, there was a problem saving the buffer \"" + targetFile.getAbsolutePath() + "\": " + t.getMessage());
        }
        return RC_CONTINUE;
    }

    /**
     * Optional list argument. Print out the current modules the system knows about
     *
     * @param inputLine
     * @return
     */
    private int doModulesCommand(InputLine inputLine) {
        if (!inputLine.hasArgAt(ACTION_INDEX)) {
            return doModulesList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "list":
                return doModulesList(inputLine);
            case "imports":
                return doModuleImports(inputLine);
        }
        say("Unknown modules command");
        return RC_CONTINUE;
    }

    private int doModuleImports(InputLine inputLine) {
        if (!state.getImportManager().hasImports()) {
            say("(no imports)");
        } else {
            for (URI uri : state.getImportManager().keySet()) {
                say(uri + " = " + state.getImportManager().getAlias(uri));
            }
        }
        return RC_CONTINUE;
    }

    private int doModulesList(InputLine inputLine) {
        TreeSet<String> m = new TreeSet<>();
        for (URI key : getState().getImportManager().keySet()) {
            m.add(key + " = " + getState().getImportManager().getAlias(key));
        }
        String modules = m.toString();
        modules = modules.substring(1); // chop off lead [
        modules = modules.substring(0, modules.length() - 1);
        say(modules);
        return RC_CONTINUE;
    }

    /**
     * Modes are
     * <ul>
     *     <li>[list] - list all of the variables</li>
     *     <li>help [name argCount] - no arguments means list all, otherwise, find the function with the signature.</li>
     *     <li>drop name - remove a local function. This does not remove a function from a module.</li>
     * </ul>
     *
     * @param inputLine
     * @return
     */
    private int doFuncs(InputLine inputLine) {
        if (!inputLine.hasArgAt(ACTION_INDEX)) {
            return doFuncsList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "drop":
                return doFuncsDrop(inputLine);
            case "help":
                return doFuncsHelp(inputLine);
            case "list":
                return doFuncsList(inputLine);
            case "system":
                return doSystemFuncsList(inputLine);
            default:
                say("sorry, unrecognized command.");
        }
        return RC_CONTINUE;
    }

    private int doFuncsDrop(InputLine inputLine) {
        String fName = inputLine.getArg(FIRST_ARG_INDEX);
        getState().getFunctionTable().remove(fName);
        if (getState().getFunctionTable().containsKey(fName)) {
            say(fName + " removed.");
        } else {
            say("Could not remove " + fName);
        }
        return RC_CONTINUE;
    }

    private int doFuncsHelp(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            // so they entered )funcs help  -- same as )funcs or )funcs list
            return doFuncsList(inputLine);
        }
        String fName = inputLine.getArg(FIRST_ARG_INDEX);

        int argCount = 0;
        String rawArgCount = null;
        if (inputLine.hasArgAt(1 + FIRST_ARG_INDEX)) {
            rawArgCount = inputLine.getArg(1 + FIRST_ARG_INDEX);
        }

        try {
            if (rawArgCount != null) {
                argCount = Integer.parseInt(rawArgCount);
            }
        } catch (Throwable t) {
            say("Sorry, but \"" + rawArgCount + "\" is not an integer");
            return RC_CONTINUE;
        }
        List<String> doxx = getState().listFunctionDoc(fName, argCount);
        for (String x : doxx) {
            say(x);
        }
        return RC_CONTINUE;
    }

    protected int doSystemFuncsList(InputLine inputLine) {
        int displayWidth = 120; // just to keep thing simple
        if (inputLine.hasArg("width")) {
            try {
                displayWidth = Integer.parseInt(inputLine.getNextArgFor("width"));
            } catch (Throwable t) {
                say("sorry, but " + inputLine.getArg(0) + " is not a number. Formatting for default width of " + displayWidth);
            }
        }

        String blanks = "                                                                          "; // padding
        TreeSet<String> funcs = getState().getMetaEvaluator().listFunctions();
        // Find longest entry
        int width = 0;
        for (String x : funcs) {
            width = Math.max(width, x.length());
        }
        width = 2 + width; // so the widest + 2 chars to make it readable.
        // number of columns are display / width, possibly plus 1 if there is a remainder
        int colCount = displayWidth / width + (displayWidth % width == 0 ? 0 : 1);
        int rowCount = funcs.size() / colCount;
        String[] output = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            output[i] = ""; // initialize it
        }
        int pointer = 0;
        for (String func : funcs) {
            int currentLine = pointer++ % rowCount;
            output[currentLine] = output[currentLine] + func + blanks.substring(0, width - func.length());
        }
        for (String x : output) {
            say(x);
        }

        return RC_CONTINUE;

    }

    protected int doFuncsList(InputLine inputLine) {
        String regex = null;
        if (FIRST_ARG_INDEX < inputLine.size()) {
            regex = inputLine.getArg(FIRST_ARG_INDEX);
        }
        String funs = getState().listFunctions(regex).toString().trim();
        funs = funs.substring(1); // chop off lead [
        funs = funs.substring(0, funs.length() - 1);
        say(funs);
        return RC_CONTINUE;
    }

    /**
     * Either show all variables (no arg or argument of "list") or <br/><br/>
     * drop name -- remove the given symbol from the local symbol table. This does not effect modules.
     *
     * @param inputLine
     * @return
     */
    private int doVars(InputLine inputLine) {
        if (!inputLine.hasArgs()) {
            return doVarsList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "list":
                return doVarsList(inputLine);
            case "drop":
                return doVarsDrop(inputLine);
            case "size":
                say(state.getStackSize() + " symbols defined.");
                return RC_CONTINUE;
        }
        say("Unknown variable command.");
        return RC_CONTINUE;
    }

    private int doVarsDrop(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Sorry. You did not supply a variable name to drop");
            return RC_NO_OP;
        }
        getState().remove(inputLine.getArg(FIRST_ARG_INDEX));
        say(inputLine.hasArgAt(FIRST_ARG_INDEX) + " has been removed.");
        return RC_CONTINUE;
    }

    private int doVarsList(InputLine inputLine) {
        String vars = getState().listVariables().toString().trim();
        vars = vars.substring(1); // chop off lead [
        vars = vars.substring(0, vars.length() - 1);
        say(vars);
        return RC_CONTINUE;
    }

    /**
     * Just print the general help
     *
     * @param inputLine
     * @return
     */
    private int doHelp(InputLine inputLine) {
        showRunHelp();
        return RC_CONTINUE;
    }

    /**
     * Commands are:<br>
     * <ul>
     *     <li>load filename -- loads the given file, replacing the current state</li>
     *     <li>save filename -- serializes  the current workspace to the file</li>
     *     <li>clear -- clears the state completely.</li>
     *     <li>id -- print the currently named id, i.e., the name of the file if loaded</li>
     * </ul>
     *
     * @param inputLine
     * @return
     */
    protected int doWS(InputLine inputLine) {
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "load":
                return doWsLoad(inputLine);
            case "save":
                return doWSSave(inputLine);
            case "clear":
                return doWSClear(inputLine);
            case "echo":
                return doEchoMode(inputLine);
            case "id":
                if (currentWorkspace == null) {
                    say("No workspace loaded");

                } else {
                    say(currentWorkspace.getName());
                }
                return RC_CONTINUE;
            case "memory":
                say("memory used = " + (Runtime.getRuntime().totalMemory() / (1024 * 1024)) +
                        " MB, free = " + (Runtime.getRuntime().freeMemory() / (1024 * 1024)) +
                        " MB, processors = " + Runtime.getRuntime().availableProcessors());
                return RC_CONTINUE;
            case "vfs":
                if (state.getVfsFileProviders().isEmpty()) {
                    say("No installed virtual file systems");
                    return RC_CONTINUE;
                }
                say("Installed virtual file systems");
                String indent = "                           "; // 25 blanks
                String shortSpaces = "           "; // 12 blanks
                for (String x : state.getVfsFileProviders().keySet()) {
                    String output = "";
                    VFSFileProvider y = state.getVfsFileProviders().get(x);
                    output += makeColumn(indent, "type:" + y.getType());
                    output += makeColumn(shortSpaces, "access:" + (y.canRead() ? "r" : "") + (y.canWrite() ? "w" : ""));
                    output += makeColumn(indent, "scheme: " + y.getScheme());
                    output += makeColumn(indent, "mount point:" + y.getMountPoint());
                    output += makeColumn(indent, "current dir:" + (y.getCurrentDir() == null ? "(none)" : y.getCurrentDir()));
                    sayi(output);
                }
                return RC_CONTINUE;

        }
        return RC_NO_OP;

    }

    String makeColumn(String spaces, String text) {
        if (spaces.length() < text.length()) {
            return text;
        }
        return text + spaces.substring(0, spaces.length() - text.length());
    }

    private int doEchoMode(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("echo mode currently " + (isEchoModeOn() ? "on" : "off"));
            return RC_CONTINUE;
        }
        String onOrOff = inputLine.getArg(FIRST_ARG_INDEX);
        if (onOrOff.toLowerCase().equals("on")) {
            setEchoModeOn(true);
            getInterpreter().setEchoModeOn(true);
            say("echo mode on");
        } else {
            setEchoModeOn(false);
            getInterpreter().setEchoModeOn(false);
            say("echo mode off");
        }
        return RC_CONTINUE;
    }

    private int doWSClear(InputLine inputLine) {
        state = null;
        // Get rid of everything.
        interpreter = new QDLParser(getState());
        say("workspace cleared");
        return RC_CONTINUE;
    }

    private int doWSSave(InputLine inputLine) {
        try {
            if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
                say("sorry, no file given");
                return RC_CONTINUE;
            }
            String fName = inputLine.getArg(FIRST_ARG_INDEX);
            FileOutputStream fos = new FileOutputStream(fName);
            StateUtils.save(state, fos);
            say("workspace saved at " + (new Date()));
        } catch (Throwable t) {
            say("would not save the workspace");
            t.printStackTrace();
        }
        return RC_NO_OP;
    }

    private void doRealLoad(File f) {
        try {
            long lastModified = f.lastModified();
            FileInputStream fis = new FileInputStream(f);
            state = StateUtils.load(fis);
            interpreter = new QDLParser(env, state);
            interpreter.setEchoModeOn(isEchoModeOn());
            interpreter.setDebugOn(isDebugOn());
            currentWorkspace = f;
            say("last saved " + new Date(lastModified));
        } catch (Throwable t) {
            say("workspace not loaded.");
            t.printStackTrace();
        }
    }

    File currentWorkspace;

    private int doWsLoad(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            if (currentWorkspace == null) {
                say("Sorry, no file given and no workspace has been loaded.");
            } else {
                doRealLoad(currentWorkspace);
            }
            return RC_CONTINUE;
        }

        String fName = inputLine.getArg(FIRST_ARG_INDEX);
        File f = new File(fName);
        doRealLoad(f);
        return RC_CONTINUE;
    }

    QDLParser interpreter = null;

    public QDLParser getInterpreter() {
        return interpreter;
    }

    boolean debugOn = false;

    @Override
    public boolean isDebugOn() {
        return debugOn;
    }

    @Override
    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    @Override
    public void debug(String x) {
        logger.debug(x);
    }

    @Override
    public void info(String x) {
        logger.info(x);
    }

    @Override
    public void warn(String x) {
        logger.warn(x);
    }

    @Override
    public void error(String x) {
        logger.error(x);
    }

    public void error(String x, Throwable t) {
        logger.error(x, t);
    }

    /**
     * Prints with the default indent and a linefeed.
     *
     * @param x
     */
    protected void say(String x) {
        System.out.println(defaultIndent + x);
    }

    public static final String INDENT = "  "; // use this in implementations for consistent indenting.
    protected String defaultIndent = "";

    /**
     * prints with the current indent and a linefeed.
     *
     * @param x
     */
    protected void sayi(String x) {
        say(INDENT + x);
    }

    /**
     * Double indent -- useful for lists.
     *
     * @param x
     */
    protected void sayii(String x) {
        say(INDENT + INDENT + x);
    }

    State state;

    protected State getState() {
        if (state == null) {
            ImportManager namespaceResolver = ImportManager.getResolver();
            SymbolTableImpl symbolTable = new SymbolTableImpl();
            SymbolStack stack = new SymbolStack();
            stack.addParent(symbolTable);
            state = new State(ImportManager.getResolver(),
                    stack,
                    new OpEvaluator(),
                    MetaEvaluator.getInstance(),
                    new FunctionTable(),
                    new ModuleMap(),
                    logger,
                    false);// workspace is never in server mode
            // Experiment for a Java module
        }
        return state;

    }

    protected void setupJavaModule(State state, QDLLoader loader, boolean importASAP) {
        for (Module m : loader.load()) {
            state.addModule(m); // done!
            if (importASAP) {
                state.getImportManager().addImport(m.getNamespace(), m.getAlias());
                state.getImportedModules().put(m.getAlias(), m);
            }
        }
    }

    /*

    CLA = Command Line Args. These are the switches used on the command line
     */
    public static final String CLA_EXTENSIONS = "-ext"; // multiple classes comma separated allowed
    public static final String CLA_ENVIRONMENT = "-env";
    public static final String CLA_HOME_DIR = "-qdlroot";
    public static final String CLA_LOG_DIR = "-log";
    public static final String CLA_BOOT_SCRIPT = "-boot_script";
    public static final String CLA_VERBOSE_ON = "-v";
    public static final String CLA_NO_BANNER = "-noBanner";
    public static final String CLA_DEBUG_ON = "-debug";
    public static final String CLA_RUN_SCRIPT_ON = "-run";

    protected File resolveAgainstRoot(String file) {
        File f = new File(file);
        if (!f.isAbsolute()) {
            // then we need to resolve it against the root.
            return new File(rootDir, file);
        }
        return f;
    }

    File envFile; // this is the name of the file holding the environment variables

    protected void fromConfigFile(InputLine inputLine) throws Throwable {
        String cfgname = inputLine.hasArg(CONFIG_NAME_FLAG) ? inputLine.getNextArgFor(CONFIG_NAME_FLAG) : "default";
        ConfigurationNode node = ConfigUtil.findConfiguration(
                inputLine.getNextArgFor(CONFIG_FILE_FLAG),
                cfgname, CONFIG_TAG_NAME);
        if (inputLine.hasArg("-home_dir")) {
            // The user might set the home directory here.
            // This is overridden by the configuration file.
            rootDir = new File(inputLine.getNextArgFor("-home_dir"));
        }
        QDLConfigurationLoader loader = new QDLConfigurationLoader(node);

        QDLEnvironment qe = loader.load();
        setEchoModeOn(qe.isEchoModeOn());
        isRunScript = inputLine.hasArg(CLA_RUN_SCRIPT_ON);
        if (isRunScript) {
            runScriptPath = inputLine.getNextArgFor(CLA_RUN_SCRIPT_ON);
        }
        boolean isVerbose = qe.isWSVerboseOn();
        showBanner = qe.isShowBanner();
        logger = qe.getMyLogger();
        if (!qe.getWSHomeDir().isEmpty()) {
            rootDir = new File(qe.getWSHomeDir());
        } else {
            if (rootDir == null) {
                // So no home directory was set on the command line either. Use the invocation directory
                String currentDirectory = System.getProperty("user.dir");
                rootDir = new File(inputLine.getNextArgFor(currentDirectory));
            }
        }
        State state = getState(); // This sets it for the class it will be  put in the interpreter below.
        state.getOpEvaluator().setNumericDigits(qe.getNumericDigits());
        env = new XProperties();

        if (!qe.getWSEnv().isEmpty()) {
            // try and see if the file resolves first.
            envFile = resolveAgainstRoot(qe.getWSEnv());

            if (envFile.exists()) {
                env.load(envFile);
            }
            // set some useful things.
        }
        env.put("home_dir", rootDir.getAbsolutePath());
        if (!isRunScript()) {
            splashScreen();
        }
        QDLConfigurationLoaderUtils.setupVFS(qe, getState());

        setEchoModeOn(qe.isEchoModeOn());
        String[] foundModules = setupModules(qe, getState());
        // Just so the user can see it in the properties after load.
        if (foundModules[JAVA_MODULE_INDEX] != null && !foundModules[JAVA_MODULE_INDEX].isEmpty()) {
            if (isVerbose) {
                say("loaded java modules: " + foundModules[JAVA_MODULE_INDEX]);
            }
            env.put("java_modules", foundModules[JAVA_MODULE_INDEX]);
        }
        if (foundModules[QDL_MODULE_INDEX] != null && !foundModules[QDL_MODULE_INDEX].isEmpty()) {
            if (isVerbose) {
                say("loaded QDL modules: " + foundModules[QDL_MODULE_INDEX]);
            }

            env.put("qdl_modules", foundModules[QDL_MODULE_INDEX]);
        }
        String bf = QDLConfigurationLoaderUtils.runBootScript(qe, getState());
        if (bf != null) {
            if (isVerbose) {
                say("loaded boot script \"" + bf + "\"");
            }
            env.put("boot_script", bf);
        }
        interpreter = new QDLParser(env, getState());
        interpreter.setEchoModeOn(qe.isEchoModeOn());
        //   interpreter.setDebugOn(true);
        runScript(); // run any script if that mode is enabled.
    }

    /**
     * Bootstraps the whole thing.
     *
     * @param inputLine
     */
    public void init(InputLine inputLine) throws Throwable {
        if (inputLine.hasArg(CONFIG_FILE_FLAG)) {
            fromConfigFile(inputLine);
            return;
        }
        // Old input line
        // -ext "edu.uiuc.ncsa.qdl.extensions.QDLLoaderImpl" -qdlroot /home/ncsa/dev/qdl -env etc/qdl.properties -log log/qdl.log -v
        fromCommandLine(inputLine);
    }

    public boolean isRunScript() {
        return isRunScript;
    }

    boolean isRunScript = false;
    String runScriptPath = null;

    protected void fromCommandLine(InputLine inputLine) throws Throwable {
        boolean isVerbose = inputLine.hasArg(CLA_VERBOSE_ON);
        if (isVerbose) {
            say("Verbose mode enabled.");
        }
        boolean isDebug = inputLine.hasArg(CLA_DEBUG_ON);
        if (isDebug) {
            say("Debug mode enabled.");
        }
        isRunScript = inputLine.hasArg(CLA_RUN_SCRIPT_ON);
        if (isRunScript) {
            runScriptPath = inputLine.getNextArgFor(CLA_RUN_SCRIPT_ON);
        }
        showBanner = !inputLine.hasArg(CLA_NO_BANNER);
        // Make sure logging is in place before actually setting up the state,
        // so the state has logging.
        LoggerProvider loggerProvider = null;
        if (inputLine.hasArg(CLA_LOG_DIR)) {
            // create the logger for this
            File f = resolveAgainstRoot(inputLine.getNextArgFor(CLA_LOG_DIR));
            loggerProvider = new LoggerProvider(f.getAbsolutePath(),
                    "qdl logger",
                    1,
                    1000000,
                    false,
                    true,
                    true);
        } else {
            File f = resolveAgainstRoot("qdl_log.xml");
            loggerProvider = new LoggerProvider(f.getAbsolutePath(),
                    "cli logger",
                    1,
                    1000000,
                    false,
                    true,
                    true);
        }
        logger = loggerProvider.get();
        State state = getState();
        if (inputLine.hasArg(CLA_HOME_DIR)) {
            rootDir = new File(inputLine.getNextArgFor(CLA_HOME_DIR));
        } else {
            String currentDirectory = System.getProperty("user.dir");
            rootDir = new File(inputLine.getNextArgFor(currentDirectory));
        }

        if (inputLine.hasArg(CLA_ENVIRONMENT)) {
            // try and see if the file resolves first.
            envFile = resolveAgainstRoot(inputLine.getNextArgFor(CLA_ENVIRONMENT));
            env = new XProperties();

            if (envFile.exists()) {
                env.load(envFile);
            }
            // set some useful things.
            env.put("qdl_root", rootDir.getAbsolutePath());
        }


        // Do the splash screen here so any messages from a boot script are obvious.
        if (!isRunScript()) {
            // But no screen of any sort if running a single script.
            splashScreen();
        }

        if (inputLine.hasArg(CLA_EXTENSIONS)) {
            // -ext "edu.uiuc.ncsa.qdl.extensions.QDLLoaderImpl"
            String loaderClasses = inputLine.getNextArgFor("-ext");
            StringTokenizer st = new StringTokenizer(loaderClasses, ",");
            String loaderClass;
            String foundClasses = "";
            boolean isFirst = true;
            while (st.hasMoreTokens()) {
                loaderClass = st.nextToken();
                try {
                    Class klasse = state.getClass().forName(loaderClass);
                    QDLLoader loader = (QDLLoader) klasse.newInstance();
                    // Do not import everything on start as default so user can set up aliases.
                    setupJavaModule(state, loader, false);
                    if (isVerbose) {
                        say("loaded module:" + klasse.getSimpleName());
                    }
                    if (isFirst) {
                        isFirst = false;
                        foundClasses = loaderClass;
                    } else {
                        foundClasses = foundClasses + "," + loaderClass;
                    }
                } catch (Throwable t) {
                    if (isDebug) {
                        t.printStackTrace();
                    }
                    say("WARNING: module \"" + loaderClass + "\" could not be loaded:" + t.getMessage());
                }
            }
            if (!foundClasses.isEmpty()) {
                env.put("externalModules", foundClasses);
            }
        }
        interpreter = new QDLParser(env, getState());
        interpreter.setEchoModeOn(true);
        if (inputLine.hasArg(CLA_BOOT_SCRIPT)) {
            String bootFile = inputLine.getNextArgFor(CLA_BOOT_SCRIPT);
            try {
                String bootScript = FileUtil.readFileAsString(bootFile);
                interpreter.execute(bootScript);
                if (isVerbose) {
                    say("loaded boot script " + bootFile);
                }
                env.put("boot_script", bootFile);
            } catch (Throwable t) {
                if (isDebug) {
                    t.printStackTrace();
                }
                say("warning: Could not load boot script\"" + bootFile + "\": " + t.getMessage());
            }
        }
        runScript(); // If there is a script, run it.
    }

    private void runScript() {
        if (isRunScript) {
            try {
                String runScript = FileUtil.readFileAsString(runScriptPath);
                if (runScript != null && !runScript.isEmpty()) {
                    interpreter.execute(runScript);
                    System.exit(0); // make sure to use this so external programs (like shell scripts) know all is ok
                }
            } catch (Throwable t) {
                say("Error executing script \"" + runScriptPath + "\".");
                System.exit(1); // So external prgrams can tell that something didn't work right.
            }
        }
    }

    File rootDir = null;

    protected BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }

    protected void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    BufferedReader bufferedReader;

    public String readline(String prompt) {
        System.out.print(prompt);
        return readline();
    }

    public boolean isEchoModeOn() {
        return echoModeOn;
    }

    public void setEchoModeOn(boolean echoModeOn) {
        this.echoModeOn = echoModeOn;
    }

    boolean echoModeOn = true;

    public String readline() {
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
}
