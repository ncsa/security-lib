package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoader;
import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils;
import edu.uiuc.ncsa.qdl.config.QDLEnvironment;
import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.SystemEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.functions.FKey;
import edu.uiuc.ncsa.qdl.functions.FR_WithState;
import edu.uiuc.ncsa.qdl.functions.FStack;
import edu.uiuc.ncsa.qdl.module.MAliases;
import edu.uiuc.ncsa.qdl.module.MTemplates;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.parsing.QDLParserDriver;
import edu.uiuc.ncsa.qdl.parsing.QDLRunner;
import edu.uiuc.ncsa.qdl.state.SIEntry;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StateUtils;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.util.QDLFileUtil;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.AbstractVFSFileProvider;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;
import edu.uiuc.ncsa.qdl.vfs.VFSFileProvider;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.*;
import edu.uiuc.ncsa.security.util.cli.*;
import edu.uiuc.ncsa.security.util.cli.editing.EditorEntry;
import edu.uiuc.ncsa.security.util.cli.editing.EditorUtils;
import edu.uiuc.ncsa.security.util.cli.editing.Editors;
import edu.uiuc.ncsa.security.util.cli.editing.LineEditor;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import edu.uiuc.ncsa.security.util.terminal.ISO6429IO;
import net.sf.json.JSONArray;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.GZIPOutputStream;

import static edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants.*;
import static edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils.*;
import static edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator.FILE_OP_BINARY;
import static edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator.FILE_OP_TEXT_STRING;
import static edu.uiuc.ncsa.qdl.util.InputFormUtil.*;
import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.SCHEME_DELIMITER;
import static edu.uiuc.ncsa.security.core.util.StringUtils.*;
import static edu.uiuc.ncsa.security.util.cli.CLIDriver.EXIT_COMMAND;
import static edu.uiuc.ncsa.security.util.cli.CLIDriver.HELP_SWITCH;

/**
 * This is the helper class to the {@link QDLWorkspace} that does the grunt work of the ) commands.
 * <p>Created by Jeff Gaynor<br>
 * on 1/30/20 at  9:21 AM
 */
public class WorkspaceCommands implements Logable {


    public WorkspaceCommands() {
    }

    public WorkspaceCommands(IOInterface ioInterface) {
        setIoInterface(ioInterface);
    }

    public static final String SWITCH = "-";
    public static final String DISPLAY_WIDTH_SWITCH = SWITCH + "w";
    public static final String FQ_SWITCH = SWITCH + "fq";
    public static final String REGEX_SWITCH = SWITCH + "r";
    public static final String COMPACT_ALIAS_SWITCH = SWITCH + "compact";
    public static final String COLUMNS_VIEW_SWITCH = SWITCH + "cols"; // force single column view
    public static final String SHOW_FAILURES = SWITCH + "show_failures"; // for displaying workspaces that don't load
    public static final String SHOW_ONLY_FAILURES = SWITCH + "only_failures"; // for displaying only workspaces that don't load
    public static final String SAVE_AS_JAVA_FLAG = SWITCH + "java";
    public static final String KEEP_WSF = SWITCH + "keep_wsf";
    public static final String LINE_EDITOR_NAME = "line";

    public MyLoggingFacade getLogger() {
        return logger;
    }

    public MyLoggingFacade logger;

    XProperties env;

    public XProperties getEnv() {
        return env;
    }

    public void setEnv(XProperties xp) {
        env = xp;
    }

    CommandLineTokenizer CLT = new CommandLineTokenizer('\'');
    protected static final String FUNCS_COMMAND = ")funcs";
    protected static final String HELP_COMMAND = ")help"; // show various types of help
    protected static final String OFF_COMMAND = ")off";
    protected static final String BUFFER2_COMMAND = ")buffer";
    protected static final String SHORT_BUFFER2_COMMAND = ")b";
    protected static final String EXECUTE_COMMAND = ")";
    protected static final String RESUME_COMMAND = "))";
    protected static final String MODULES_COMMAND = ")modules";
    protected static final String LOAD_COMMAND = ")load"; // grab a file and run it
    protected static final String SAVE_COMMAND = ")save";
    protected static final String CLEAR_COMMAND = ")clear";
    protected static final String IMPORTS_COMMAND = ")imports";
    protected static final String VARS_COMMAND = ")vars";
    protected static final String ENV_COMMAND = ")env";
    protected static final String WS_COMMAND = ")ws";
    protected static final String LIB_COMMAND = ")lib";
    protected static final String EDIT_COMMAND = ")edit";
    protected static final String FILE_COMMAND = ")file";
    protected static final String HISTORY_COMMAND = ")h";
    protected static final String REPEAT_COMMAND = ")r";
    protected static final String STATE_INDICATOR_COMMAND = ")si";

    public static final int RC_NO_OP = -1;
    public static final int RC_RELOAD = -2;
    public static final int RC_CONTINUE = 1;
    public static final int RC_EXIT_NOW = 0;

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
        } /*else {
            say("QDL Workspace, version " + QDLVersion.VERSION);
        }*/
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

    protected void showHelp4Help() {
        say(HELP_COMMAND + " syntax:");
        say(HELP_COMMAND + " - (no arg) print generic help for the workspace.");
        say(HELP_COMMAND + " * - print a short summary of help for every user defined function.");
        say(HELP_COMMAND + " -online - print a list of all online help topics.");
        say(HELP_COMMAND + " name - print short help for name. System functions will have a");
        say("        summary printed (read the manual for more).");
        say("        For user defined function, a summary of all calls with various argument counts will be shown.");
        say(HELP_COMMAND + " name arg_count - print out detailed information for the user-defined function and the given number of arguments.");

    }

    protected void showGeneralHelp() {
        say("This is the QDL (pronounced 'quiddle') workspace.");
        say("You may enter commands and execute them much like any other interpreter.");
        say("There are several commands available to help you manage this workspace.");
        say("Generally these start with a right parenthesis, e.g., ')off' (no quotes) exits this program.");
        say("Here is a quick summary of what they are and do.");
        int length = 8;
        sayi(RJustify(BUFFER2_COMMAND, length) + " - commands relating to using buffers. Alias is " + SHORT_BUFFER2_COMMAND);
        sayi(RJustify(CLEAR_COMMAND, length) + " - clear the state of the workspace. All variables, functions etc. will be lost.");
        sayi(RJustify(ENV_COMMAND, length) + " - commands relating to environment variables in this workspace.");
        sayi(RJustify(FUNCS_COMMAND, length) + " - list all of the imported and user defined functions this workspace knows about.");
        sayi(RJustify(RESUME_COMMAND, length) + " - short hand to resume execution for a halted process. Note the argument is the process id (pid), not the buffer number. ");
        sayi(RJustify(HELP_COMMAND, length) + " - this message.");
        sayi(RJustify(MODULES_COMMAND, length) + " - lists all the loaded modules this workspace knows about.");
        sayi(RJustify(OFF_COMMAND, length) + " - exit the workspace.");
        sayi(RJustify(LOAD_COMMAND, length) + " - Load a file of QDL commands and execute it immediately in the current workspace.");
        sayi(RJustify(STATE_INDICATOR_COMMAND, length) + " - commands relating to teh state indicator.");
        sayi(RJustify(VARS_COMMAND, length) + " - lists all of the variables this workspace knows about.");
        sayi(RJustify(WS_COMMAND, length) + " - commands relating to this workspace.");
        sayi(RJustify(EDIT_COMMAND, length) + " - commands relating to running the line editor.");
        sayi(RJustify(EXECUTE_COMMAND, length) + " - short hand to execute whatever is in the current buffer.");
        say("Generally, supplying --help as a parameter to a command will print out something useful.");
        say("Full documentation is available in the docs directory of the distro or at https://cilogon.github.io/qdl/docs/qdl_workspace.pdf");

    }

    /**
     * Replaces ) commands prefixed with a > by their value from the symbol table.
     *
     * @param inputLine
     * @return
     */
    protected InputLine variableLookup(InputLine inputLine) {
        for (int i = 1; i < inputLine.size(); i++) {
            String input = inputLine.getArg(i);
            if (input.startsWith(">")) {
                Object rawValue = getState().getValue(input.substring(1));
                if (rawValue != null && !(rawValue instanceof QDLNull)) {
                    inputLine.setArg(i, rawValue.toString());
                }
            }
        }
        return inputLine;
    }

    public QDLWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(QDLWorkspace workspace) {
        this.workspace = workspace;
    }

    QDLWorkspace workspace;
    /**
     * The workspace commands are here so they can be serialized with the rest of the workspace.
     * However, since the mechanism has to intercept every command before it gets forwarded
     * to {@link #execute(String)}, the logic for managing this list is in {@link QDLWorkspace}.
     */
    public List<String> commandHistory = new LinkedList<>();

    public int execute(String inline) {

        inline = TemplateUtil.replaceAll(inline, env); // allow replacements in commands too...
        InputLine inputLine = new InputLine(CLT.tokenize(inline));
        inputLine = variableLookup(inputLine);
        switch (inputLine.getCommand()) {
            case FILE_COMMAND:
                return doFileCommands(inputLine);
            case SHORT_BUFFER2_COMMAND:
            case BUFFER2_COMMAND:
                return _doNewBufferCommand(inputLine);
            case CLEAR_COMMAND:
                return _wsClear(inputLine);
            case EDIT_COMMAND:
                //  return doEditCommand(inputLine);
                return _doBufferEdit(inputLine);
            case EXECUTE_COMMAND:
                return _doBufferRun(inputLine);
            case RESUME_COMMAND:
                return _doSIResume(inputLine);
            case ENV_COMMAND:
                return doEnvCommand(inputLine);
            case FUNCS_COMMAND:
                return doFuncs(inputLine);
            case IMPORTS_COMMAND:
                return _moduleImports(inputLine);
            case HELP_COMMAND:
                return doHelp(inputLine);
            case MODULES_COMMAND:
                return doModulesCommand(inputLine);
            case STATE_INDICATOR_COMMAND:
                return doSICommand(inputLine);
            case OFF_COMMAND:
                if (inputLine.hasArg(HELP_SWITCH)) {
                    say(OFF_COMMAND + " [y||n] - exit the system. If you do not supply an argument, you will be prompted.");
                    sayi("y = exit immediately without saving");
                    return RC_NO_OP;
                }
                if (inputLine.hasArg("y")) {
                    shutdown();
                    return RC_EXIT_NOW;
                }
                if (readline("Do you want to exit?" + (bufferManager.anyEdited() ? " There are unsaved buffers. " : " ") + "(y/n)").equals("y")) {
                    shutdown();
                    return RC_EXIT_NOW;
                }
                say("System exit cancelled.");
                return RC_CONTINUE;
            case VARS_COMMAND:
                return doVars(inputLine);
            case WS_COMMAND:
                return doWS(inputLine);
            case LIB_COMMAND:
                inline = inline.replace(LIB_COMMAND, WS_COMMAND + " lib ");
                inputLine = new InputLine(CLT.tokenize(inline));
                return doWS(inputLine);
            //return _wsLibList(inputLine);
            case SAVE_COMMAND:
                inline = inline.replace(SAVE_COMMAND, WS_COMMAND + " save ");
                inputLine = new InputLine(CLT.tokenize(inline));
                return _wsSave(inputLine);
            case LOAD_COMMAND:
                inline = inline.replace(LOAD_COMMAND, WS_COMMAND + " load ");
                inputLine = new InputLine(CLT.tokenize(inline));
                return _wsLoad(inputLine);
        }
        say("Unknown command.");
        return RC_NO_OP;
    }


    protected void shutdown() {
        if (autosaveThread != null) {
            autosaveThread.setStopThread(true);
            autosaveThread.interrupt();
        }

    }

    public static class SIEntries extends TreeMap<Integer, SIEntry> {
        int maxKey = 100; // system pid is 0, but that is not stored here.

        @Override
        public SIEntry put(Integer key, SIEntry value) {
            int key0 = key; // since we have to do math with it and don't want the class
            if (key0 < 0) {
                // set the key to the next PID
                key0 = 1 + maxKey;
                maxKey++;
            } else {
                if (containsKey(key)) {
                    throw new IllegalArgumentException("Error: PID is in use");
                }
            }
            maxKey = Math.max(maxKey, key0);

            return super.put(key0, value);
        }

        public int nextKey() {
            return maxKey + 1;
        }
    }

    SIEntries siEntries = new SIEntries();

    public static class WSInternals implements Serializable {
        State defaultState;
        Integer currentPID;
        SIEntries siEntries;
        State activeState;
        Date startTimestamp;
        String id;
        String description;
        boolean echoOn;
        boolean prettyPrint;
        String saveDir = null;
        boolean debugOn;

        public void toXML(XMLStreamWriter xsw) throws XMLStreamException {

        }

        public void fromXML(XMLEventReader xer) throws XMLStreamException {

        }
    }

    private int doSICommand(InputLine inputLine) {
        if (inputLine.size() <= ACTION_INDEX) {
            say("Sorry, please supply an argument (e.g. --help)");
            return RC_NO_OP;
        }
        if (!inputLine.hasArgAt(ACTION_INDEX)) {
            return _doSIList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "--help":
                say("State indicator commands:");
                sayi("      --help - this messages");
                sayi("        list - list all current states in the indicator by process id (pid)");
                sayi("       reset - clear the entire state indicator, restoring the system process as the default");
                sayi("resume [pid] - resume running the given process. No arguments means restart the current one.");
                sayi("      rm pid - remove the given state from the system, losing all of it. If it is the");
                sayi("               current state, the system default will replace it.");
                sayi("   set [pid] - set the current process id. No argument means to display the current pid.");
                return RC_NO_OP;
            case "list":
                // list all current pids.
                return _doSIList(inputLine);
            case "reset":
                SIEntry sie = siEntries.get(new Integer(0));
                siEntries = new SIEntries();
                siEntries.put(0, sie);
                currentPID = 0;
                say("state indicator reset.");
                return RC_CONTINUE;
            case "resume":
                // resume the execution of a process by pid
                return _doSIResume(inputLine);
            case "rm":
                return _doSIRemove(inputLine);
            case "set":
                return _doSISet(inputLine);
            default:
                say("sorry, unknown command.");
        }

        return RC_NO_OP;
    }

    private int _doSIRemove(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("sorry, no pid given.");
            return RC_CONTINUE;
        }
        try {
            int pid = inputLine.getIntArg(FIRST_ARG_INDEX);
            if (pid == currentPID) {
                interpreter = defaultInterpreter;
                state = defaultState;
                currentPID = 0;
            }
            if (!siEntries.containsKey(pid)) {
                say("invalid pid " + pid);
                return RC_NO_OP;
            }

            siEntries.remove(pid);
            say("process id " + pid + " has been removed from the state indicator.");
            return RC_NO_OP;

        } catch (ArgumentNotFoundException ax) {
            say("Sorry, but that was not a valid pid");
        }

        return RC_NO_OP;
    }

    private int _doSISet(InputLine inputLine) {
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("currently active process id is " + currentPID);
            return RC_CONTINUE;
        }
        try {
            int pid = inputLine.getIntArg(FIRST_ARG_INDEX);
            if (pid == currentPID) {
                return RC_NO_OP;
            }
            if (!siEntries.containsKey(pid)) {
                say("invalid pid " + pid);
                return RC_NO_OP;
            }
            if (pid == 0) {
                interpreter = defaultInterpreter;
                state = defaultState;

            } else {
                SIEntry sie = siEntries.get(pid);
                interpreter = sie.interpreter;
                // bare bones what new
                interpreter.setEchoModeOn(isEchoModeOn());
                interpreter.setPrettyPrint(isPrettyPrint());
                sie.state.setIoInterface(state.getIoInterface());
                state = sie.state;
            }
            currentPID = pid;
            say("pid set to " + pid);
        } catch (ArgumentNotFoundException ax) {
            say("Sorry, but that was not a valid pid");
        }

        return RC_NO_OP;
    }

    private int _doSIResume(InputLine inputLine) {
        try {
            int pid = 0;
            if (inputLine.getCommand().equals(RESUME_COMMAND)) {
                if (inputLine.getArgCount() == 0) {
                    // They just passed in a ))
                    pid = currentPID;
                } else {
                    try {
                        pid = inputLine.getIntArg(1);
                    } catch (Throwable n) {
                        say("sorry but the pid could not be determined");
                        return RC_CONTINUE;
                    }
                }
            } else {
                if (inputLine.hasArgAt(FIRST_ARG_INDEX)) {
                    pid = inputLine.getIntArg(FIRST_ARG_INDEX);
                } else {
                    // no arg. Use current pid
                    pid = currentPID;
                }
            }
/*
            if (pid == 0) {
                // no resume on current thread
                return RC_NO_OP;
            }
*/
            if (!siEntries.containsKey(pid)) {
                say("invalid pid " + pid);
                return RC_NO_OP;
            }
            SIEntry sie = siEntries.get(pid);
            try {
                if (sie.qdlRunner == null) {
                    say("si damage"); // something is out of whack. Don't kill the workspace, just tell them.
                    return RC_NO_OP;
                }
                sie.qdlRunner.restart(sie);
                // if it finishes, then reset to default.
                state = defaultState;
                currentPID = 0;
                interpreter = defaultInterpreter;
                siEntries.remove(sie.pid);
                say("exit pid " + sie.pid);
            } catch (InterruptException ix) {
                sie.lineNumber = ix.getSiEntry().lineNumber;
                sie.message = ix.getSiEntry().message;
                sie.timestamp = ix.getSiEntry().timestamp;
            }
        } catch (ArgumentNotFoundException ax) {
            say("Sorry, but that was not a valid pid");
        }
        return RC_NO_OP;
    }

    protected QDLInterpreter cloneInterpreter(InputLine inputLine) {
        try {
            State newState = StateUtils.clone(getState());
            QDLInterpreter qdlParser = new QDLInterpreter(newState);
            newState.setIoInterface(getIoInterface()); // or IO won't work
            qdlParser.setEchoModeOn(interpreter.isEchoModeOn());
            qdlParser.setPrettyPrint(interpreter.isPrettyPrint());
            return qdlParser;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    // formatting constants for si. These are the width of the fields in the si list command.
    // One of these days, if workspaces get too big or somesuch,
    protected int ___SI_PID = 5;
    protected int ___SI_ACTIVE = 6;
    protected int ___SI_TIMESTAMP = 25;
    protected int ___SI_LINE_NR = 5;
    protected int ___SI_SIZE = 6;

    protected int _doSIList(InputLine inputLine) {
        // entry is
        // pid status  timestamp size message
        // pid is an integer
        // status is * for active -- for not
        // timestamp is when created
        // size is the size of the state object.
        // message is the message
        say(pad2("pid", ___SI_PID) +
                " | active | " +
                pad2("stmt", ___SI_LINE_NR) + " | " +
                pad2("time", ___SI_TIMESTAMP) + " | " +
                pad2("size", ___SI_SIZE) + " | " +
                "message");
        // do system separate since it is never stored in the si entries
        String lineOut = pad2(0, ___SI_PID) +
                " | " + pad2((0 == currentPID ? "  * " : " ---"), ___SI_ACTIVE) +
                " | " + pad2(" ", ___SI_LINE_NR) + // line number
                " | " + pad2(startTimeStamp, ___SI_TIMESTAMP) + // timestamp
                " | " + pad2(StateUtils.size(defaultState), ___SI_SIZE) +
                " | " + "system";
        say(lineOut);

        for (Integer key : siEntries.keySet()) {
            SIEntry siEntry = siEntries.get(key);
            int lineNr = siEntry.lineNumber;
            lineOut = pad2(key, ___SI_PID) +
                    " | " + pad2((siEntry.pid == currentPID ? "  * " : " ---"), ___SI_ACTIVE) +
                    " | " + pad2(lineNr, ___SI_LINE_NR) +
                    " | " + pad2(siEntry.timestamp, ___SI_TIMESTAMP) +
                    " | " + pad2(StateUtils.size(siEntry.state), ___SI_SIZE) +
                    " | " + siEntry.message;
            say(lineOut);
        }
        return RC_CONTINUE;
    }


    protected int doFileCommands(InputLine inputLine) {
        if (inputLine.size() <= ACTION_INDEX) {
            say("Sorry, please supply an argument (e.g. --help)");
            return RC_NO_OP;
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("File commands:");
                sayi("    copy source target - copy the source to the target, overwriting its contents.");
                sayi("delete or rm file_name - delete the given file");
                sayi("             ls or dir - list the contents of a directory. Directories end with a /");
                sayi("            mkdir path - make a directory. This makes all intermediate directories as needed.");
                sayi("            rmdir path - remove a single directory. This fails if there are any entries in the directory.");
                sayi("                   vfs - list information about all currently mounted virtual file systems.");
                return RC_NO_OP;
            case "copy":
                return _fileCopy(inputLine);
            case "edit":
                return _doFileEdit(inputLine);
            case "rm":
            case "delete":
                return _fileDelete(inputLine);
            case "ls":
            case "dir":
                return _fileDir(inputLine);
            case "mkdir":
                return _fileMkDir(inputLine);
            case "rmdir":
                return _fileRmDir(inputLine);
            case "vfs":
                return _fileVFS(inputLine);
            default:
                say("unknown file command");
                return RC_NO_OP;
        }
    }

    protected boolean useExternalEditor() {
        return getQdlEditors() != null && !getQdlEditors().isEmpty() && isUseExternalEditor() && !getExternalEditorName().equals(LINE_EDITOR_NAME);
    }

    private int _doFileEdit(InputLine inputLine) {
        String source = inputLine.getArg(FIRST_ARG_INDEX);
        File f = new File(source);
        // don't care if it exists, that's the editor's problem.
        if (!f.isAbsolute()) {
            f = new File(rootDir, f.getAbsolutePath());
        }
        if (useExternalEditor()) {
            _doExternalEdit(f);
        } else {

            try {
                List<String> content = _doLineEditor(FileUtil.readFileAsLines(f.getAbsolutePath()));
                FileWriter fileWriter = new FileWriter(f);
                for (String line : content) {
                    fileWriter.write(line + "\n");
                }
                fileWriter.flush();
                fileWriter.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return RC_CONTINUE;
    }

    BufferManager bufferManager = new BufferManager();

    private int _doNewBufferCommand(InputLine inputLine) {
        if (inputLine.size() <= ACTION_INDEX) {
            return _doBufferList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("buffer commands:");
                say("# refers to the buffer number");
                sayi("create {" + BUFFER_IN_MEMORY_ONLY_SWITCH + "} - create a new buffer.");
                sayi("check # - run the buffer through the parser and check for syntax errors. Do not execute.");
                sayi("delete or rm #- delete the buffer. This does not delete the file.");
                sayi("edit # - Start the built-in line editor and load the given buffer ");
                sayi("link source target - create a link for given source to the target. The target will be copied to source on save.");
                sayi("ls or list - display all the active buffers and their numbers");
                sayi("path {new_path} - (no arg) means to displa y current default save path, otherwise set it. Default is qdl temp dir.");
                say("reload # - reload the buffer from disk.");
                sayi("reset - deletes all buffers and sets the start number to zero. This clears the buffer state.");
                sayi("run # {&} -  Run the buffer. If & is there, do it in its own environment");
                sayi("show # - identical to ls. ");
                sayi("write or save # - save the buffer. If linked, source is copied to target.");
                return RC_NO_OP;
            case "reload":
                return _doBufferReload(inputLine);
            case "create":
                return _doBufferCreate(inputLine);
            case "check":
                return _doBufferCheck(inputLine);
            case "reset":
                return _doBufferReset(inputLine);
            case "delete":
            case "rm":  // for our unix friends
                return _doBufferDelete(inputLine);
            case "edit":
                return _doBufferEdit(inputLine);
            case "link":
                return _doBufferLink(inputLine);
            case "list":
            case "ls":
                return _doBufferList(inputLine);
            case "run":
                return _doBufferRun(inputLine);
            case "show":
                return _doBufferShow(inputLine);
            case "path":
                return _doBufferPath(inputLine);
            case "write":
            case "save":
                return _doBufferWrite(inputLine);
            default:
                say("unrecognized buffer command");
                return RC_NO_OP;
        }

    }

    private int _doBufferReload(InputLine inputLine) {
        BufferManager.BufferRecord br = getBR(inputLine);
        if (br == null) {
            say("buffer not found");
            return RC_NO_OP;
        }
        if (br.memoryOnly) {
            return RC_NO_OP;
        }
        if (br.isLink()) {
            br.setContent(bufferManager.readFile(br.linkSavePath));
        } else {
            br.setContent(bufferManager.readFile(br.srcSavePath));
        }
        say("done");
        return RC_CONTINUE;
    }

    public String getBufferDefaultSavePath() {
        if (bufferDefaultSavePath == null) {
            bufferDefaultSavePath = getTempDir().getAbsolutePath();
            if (!bufferDefaultSavePath.endsWith("/")) {
                bufferDefaultSavePath = bufferDefaultSavePath + "/";
            }
        }
        return bufferDefaultSavePath;
    }

    String bufferDefaultSavePath = null;

    private int _doBufferPath(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("path [new_path]");
            say("(no arg) - show the current default path for saving buffers. This is used ");
            say("           in the case that the buffer is not an absolute path.");
            say("new_path - sets the current path.");
            return RC_NO_OP;
        }
        if (1 == inputLine.getArgCount()) { // zeroth arg is "path" which got us here. Args start at index 1;
            say("current default save path for relative buffers is " + getBufferDefaultSavePath());
            return RC_CONTINUE;
        }
        String newPath = inputLine.getLastArg();
        if (newPath.contains(SCHEME_DELIMITER)) {
            // its a VFS entry
            try {
                AbstractVFSFileProvider vfs = (AbstractVFSFileProvider) getState().getVFS(newPath);
                if (vfs == null) {
                    say("\"" + newPath + "\" is not a mounted VFS. Please mount it first.");
                    return RC_NO_OP;

                }
                if (!AbstractVFSFileProvider.isAbsolute(newPath)) {
                    say("\"" + newPath + "\" must not be relative");
                    return RC_NO_OP;
                }
                if (!vfs.canWrite()) {
                    say("sorry but you do not have permission to write to \"" + newPath + "\".");
                    return RC_NO_OP;
                }
                if (vfs.get(newPath) != null) {
                    say("sorry but \"" + newPath + "\" is a file.");
                    return RC_NO_OP;
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            File f = new File(newPath);
            if (!f.isAbsolute()) {
                say("\"" + f.getAbsolutePath() + "\" must not be relative");
                return RC_NO_OP;

            }
            if (f.isFile()) {
                say("sorry but \"" + f.getAbsolutePath() + "\" is a file.");
                return RC_NO_OP;
            }
            if (!f.canWrite()) {
                say("sorry but you do not have permission to write to \"" + f.getAbsolutePath() + "\".");
                return RC_NO_OP;
            }
            if (!f.exists()) {
                say("warning. \"" + f.getAbsolutePath() + "\" does not exist. You should create it before saving.");
            }
            // just in case the OS does something to the path (like there are embedded .. that it resolves).
            newPath = f.getAbsolutePath();
        }
        String oldPath = getBufferDefaultSavePath();
        bufferDefaultSavePath = newPath;
        say("new default buffer save path is \"" + newPath + "\", was \"" + oldPath + "\"");
        return RC_CONTINUE;
    }

    private int _doBufferCheck(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("check n {-st} {-src} -- check the syntax of buffer n");
            say("-st - will print whole stack track if present, otherwise just the error message");
            say("-src - if the buffer is a link, will syntax check the source. Ignored otherwise.");
            return RC_NO_OP;
        }
        boolean showStackTrace = inputLine.hasArg("-st");
        List<String> content;
        boolean useSource = inputLine.hasArg("-src");
        inputLine.removeSwitch("-src");
        inputLine.removeSwitch("-st");
        BufferManager.BufferRecord br = getBR(inputLine);
        if (br.hasContent()) {
            content = br.getContent();
        } else {
            try {
                if (br.isLink()) {
                    if (useSource) {
                        content = bufferManager.read(inputLine.getIntArg(FIRST_ARG_INDEX), useSource);
                    } else {
                        content = bufferManager.readFile(br.link);
                    }
                } else {
                    content = bufferManager.readFile(br.src);
                }
            } catch (Throwable t) {
                say("sorry, could not read the file:" + t.getMessage());
                return RC_NO_OP;
            }
        }
        if (content.isEmpty()) {
            say("empty buffer");
            return RC_NO_OP;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (String x : content) {
            stringBuffer.append(x + "\n");
        }
        StringReader r = new StringReader(stringBuffer.toString());
        QDLParserDriver driver = new QDLParserDriver(new XProperties(), state.newDebugState());
        try {
            QDLRunner runner = new QDLRunner(driver.parse(r));
        } catch (ParseCancellationException pc) {
            if (showStackTrace) {
                pc.printStackTrace();
            }
            say("syntax error:" + pc.getMessage());
            return RC_CONTINUE;
        } catch (Throwable t) {
            if (showStackTrace) {
                t.printStackTrace();
            }
            say("there was a non-syntax error:" + t.getMessage());
            return RC_CONTINUE;
        }
        say("syntax ok");
        return RC_NO_OP;
    }

    protected int _doBufferReset(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("reset");
            sayi("Drops ALL buffers and the index resets to zero. Do this only if you have to.");
            return RC_NO_OP;
        }
        if (!"y".equals(readline("Are you SURE you want to delete all buffers and reset them?"))) {
            say("aborted.");
            return RC_NO_OP;
        }
        bufferManager = new BufferManager();
        say("buffers reset");
        return RC_CONTINUE;
    }

    boolean isSI = true;

    protected int _doBufferRun(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("run (index | name) [& | !]");
            sayi("Run the given buffer. This will execute as if you had types it in to the current session. ");
            sayi("If you supply an &, then the current workspace is cloned and the code is run in that. ");
            sayi("If you supply an !, then completely clean state is created (VFS and script path are still set,");
            sayi("but no imports etc.) and the code is run in that. ");
            sayi("See the state indicator documentation for more");
            sayi(" Synonyms: ");
            sayi("   ) index|name  - start running a buffer. You must start a process before it can be suspended.");
            sayi("   )) pid -- resume a suspended process by process if (pid)");
            return RC_NO_OP;
        }
        if (inputLine.getArgCount() == 0) {
            say("you must supply either a buffer index or name to run");
            return RC_NO_OP;
        }
        BufferManager.BufferRecord br = getBR(inputLine);
        List<String> content = null;

        if (br == null || br.deleted) {
            File f = new File(inputLine.getLastArg());
            if (f.exists() && f.isFile()) {
                try {
                    content = QDLFileUtil.readFileAsLines(f.getCanonicalPath());
                } catch (Throwable throwable) {
                }
            }
            if (content == null || content.isEmpty()) {
                say("no such file or buffer");
                return RC_NO_OP;
            }
        } else {
            if (br.hasContent()) {
                content = br.getContent();
            } else {
                try {
                    if (br.isLink()) {
                        content = bufferManager.readFile(br.link);
                    } else {
                        content = bufferManager.readFile(br.src);
                    }
                } catch (Throwable t) {
                    say("sorry, could not read the file:" + t.getMessage());
                    return RC_NO_OP;
                }
            }
        }
        // flag & = start new one
        // !
        int flag = (inputLine.hasArg("&") ? 1 : 0) + (inputLine.hasArg("!") ? 2 : 0);
        if (flag == 3) {
            say("sorry, you have specified both to clone the workspace and ignore it. You can only do one of these.");
            return RC_NO_OP;
        }

        boolean origEchoMode = getInterpreter().isEchoModeOn();
        QDLInterpreter interpreter = null;
        switch (flag) {
            case 0:
                interpreter = getInterpreter();
                break;
            case 1:
                interpreter = cloneInterpreter(inputLine);
                break;

            case 2:
                State newState = state.newDebugState();
                newState.setPID(state.getPID() + 1); // anything other than zero
                newState.setIoInterface(getIoInterface()); // Or IO fails
                interpreter = new QDLInterpreter(newState);
                interpreter.setPrettyPrint(isPrettyPrint());
                interpreter.setEchoModeOn(isEchoModeOn());
                break;
        }

        if (interpreter == null) {
            say("could not create debug instance.");
            return RC_NO_OP;
        }

        boolean ppOn = interpreter.isPrettyPrint();
        interpreter.setEchoModeOn(false);
        StringBuffer stringBuffer = new StringBuffer();
        for (String x : content) {
            stringBuffer.append(x + "\n");
        }
        try {
            interpreter.execute(stringBuffer.toString());
            interpreter.setEchoModeOn(origEchoMode);
            interpreter.setPrettyPrint(ppOn);
        } catch (Throwable t) {
            interpreter.setEchoModeOn(origEchoMode);
            interpreter.setPrettyPrint(ppOn);
            if (!isSI) {
                boolean isHalt = t instanceof InterruptException;
                if (isHalt) {
                    getState().getLogger().error("interrupt in main workspace " + stringBuffer, t);
                    say("sorry, you cannot halt the main workspace. Consider starting a separate process.");
                } else {
                    getState().getLogger().error("Could not interpret buffer " + stringBuffer, t);
                    say("sorry, but there was an error:" + ((t instanceof NullPointerException) ? "(no message)" : t.getMessage()));
                }
            } else {
                if (t instanceof InterruptException) {
                    InterruptException ie = (InterruptException) t;
                    int nextPID = siEntries.nextKey();
                    ie.getSiEntry().pid = nextPID;
                    // ie.getSiEntry().interpreter = interpreter;
                    siEntries.put(nextPID, ie.getSiEntry());
                    say(Integer.toString(nextPID));
                } else {
                    say("could not interpret buffer:" + t.getMessage());
                }
            }
        }
        return RC_CONTINUE;
    }

    QDLInterpreter defaultInterpreter;
    State defaultState;
    int currentPID = 0;

    protected int _doBufferWrite(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("(write | save) (index | name)");
            sayi("Write (aka save) the buffer. If there is a link, the target is written to the source.");
            return RC_NO_OP;
        }
        // If it is a link, br.link is read and written to br.src
        BufferManager.BufferRecord br = getBR(inputLine);
        if (br == null || br.deleted) {
            say("buffer not found");
            return RC_NO_OP;
        }
        if (br.memoryOnly) {
            say("buffer is in-memory only");
            return RC_NO_OP;
        }

        boolean ok = bufferManager.write(br);
        if (ok) {
            say("done");
        } else {
            say("nothing was found to write.");
        }
        return RC_CONTINUE;

    }

    public boolean isUseExternalEditor() {
        return useExternalEditor;
    }

    public void setUseExternalEditor(boolean useExternalEditor) {
        this.useExternalEditor = useExternalEditor;
    }

    public String getExternalEditorName() {
        return externalEditorName;
    }

    public void setExternalEditorName(String externalEditorName) {
        this.externalEditorName = externalEditorName;
    }

    boolean useExternalEditor = false;
    String externalEditorName = "";

    List<String> editorClipboard = new LinkedList<>();

    private int _doBufferEdit(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("edit (index | name)");
            sayi("invoke the editor on the given buffer");
            sayi("-list - list available editors");
            sayi("-add name exec - add a (basic) editor configuration.");
            sayi("-rm name - remove an editor: note it cannot be the currently active one");
            sayi("-use name - use this as the default. Implicitly enables using external editors if needed.");
            return RC_NO_OP;
        }
        if (inputLine.getArgCount() == 0) {
            say("you must supply either an buffer index or name to edit.");
            return RC_NO_OP;
        }
        if (inputLine.hasArg("-add")) {
            if (inputLine.getArgCount() != 3) {
                say("Sorry, wrong number of arguments for -add");
                return RC_NO_OP;
            }
            String name = inputLine.getNextArgFor("-use");
            if (isTrivial(name)) {
                say("no name specified.");
                return RC_NO_OP;
            }
            inputLine.removeSwitchAndValue("-add");
            String exec = inputLine.getLastArg();
            if (getQdlEditors().hasEntry(name)) {
                boolean ok = readline("The editor named \"" + name + "\" already exists. Do you want to over write it (y/n)?").equals("y");
                if (!ok) {
                    say("aborted.");
                    return RC_NO_OP;
                }
            }
            EditorEntry ee = new EditorEntry();
            ee.name = name;
            ee.exec = exec;
            getQdlEditors().put(ee);
            return RC_CONTINUE;
        }

        if (inputLine.hasArg("-use")) {
            String name = inputLine.getNextArgFor("-use");
            if (isTrivial(name)) {
                say("no name specified.");
                return RC_NO_OP;
            }
            setExternalEditorName(name);
            setUseExternalEditor(!name.equals(LINE_EDITOR_NAME));
            return RC_CONTINUE;
        }
        if (inputLine.hasArg("-rm")) {
            String name = inputLine.getNextArgFor("-use");
            if (isTrivial(name)) {
                say("no name specified.");
                return RC_NO_OP;
            }
            if (getQdlEditors().hasEntry(name)) {
                if (getExternalEditorName().equals(name)) {
                    say("removing default editor, reverting to line editor");
                    setExternalEditorName(LINE_EDITOR_NAME);
                }

                getQdlEditors().remove(name);
                say(name + " removed.");
            } else {
                say(name + " not found.");
            }
            return RC_CONTINUE;
        }
        if (inputLine.hasArg("-list")) {
            listEditors();
            return RC_NO_OP;
        }
        BufferManager.BufferRecord br = getBR(inputLine);
        if (br == null || br.deleted) {
            say("Sorry. No such buffer");
            return RC_CONTINUE;
        }
        List<String> content;
        if (br.hasContent()) {
            content = br.getContent();
        } else {
            String fName = br.isLink() ? br.linkSavePath : br.srcSavePath;
            try {
                if (br.memoryOnly) {
                    content = br.getContent();
                } else {
                    content = bufferManager.readFile(fName);
                }
            } catch (QDLException q) {
                getState().info("no file " + fName + " found, creating new one.");
                content = new ArrayList<>();
            }
            br.setContent(content);
        }
        // so no buffer. There are a couple ways to get it.
        List<String> result;
        if (useExternalEditor()) {
            result = _doExternalEdit(br.getContent());
        } else {
            result = _doLineEditor(br.getContent());
        }
        if (result == null || result.isEmpty()) {
            return RC_NO_OP;
        }
        br.setContent(result);
        br.edited = true;
        return RC_CONTINUE;
    }

    private List<String> _doLineEditor(List<String> content) {
        LineEditor lineEditor = new LineEditor(content);
        lineEditor.setClipboard(editorClipboard);
        lineEditor.setIoInterface(getIoInterface());
        try {
            lineEditor.execute();
            return lineEditor.getBuffer(); // Just to be sure it is the same.
        } catch (Throwable t) {
            t.printStackTrace();
            say("Sorry, there was an issue editing this buffer.");
            getState().warn("Error editing buffer:" + t.getMessage() + " for exception " + t.getClass().getSimpleName());
        }
        return content;
    }

    File tempDir = null;

    protected File getTempDir() {
        if (tempDir == null) {
            if (rootDir != null) {
                tempDir = new File(rootDir, "temp");
                if (!tempDir.exists()) {
                    if (!tempDir.mkdir()) {
                        return null;
                    }
                }
            }
        }
        return tempDir;
    }

    private List<String> _doExternalEdit(File tempFile) {

        EditorEntry qdlEditor = getQdlEditors().get(getExternalEditorName());
        List<String> content = new ArrayList<>();

        int exitCode = EditorUtils.editFile(qdlEditor, tempFile);
        if (exitCode == EditorUtils.EDITOR_RC_OK) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(tempFile));
                String currentLine = br.readLine();

                while (currentLine != null) {
                    content.add(currentLine);
                    currentLine = br.readLine();
                }
                br.close();

            } catch (Throwable e) {
                if (DebugUtil.isEnabled()) {
                    e.printStackTrace();
                }
                say("There was an error reading the file:" + e.getMessage());
            }
        }
        return content;

    }

    private List<String> _doExternalEdit(List<String> content) {
        File tempFile;
        File tempDir = getTempDir();

        try {
            if (tempDir == null) {
                tempFile = File.createTempFile("edit", ".qdl");
            } else {
                tempFile = File.createTempFile("edit", ".qdl", tempDir);
            }
            tempFile.deleteOnExit();
            FileWriter fw = new FileWriter(tempFile);
            if (content == null) {
                fw.write(""); // create empty file
            } else {
                for (String x : content) {
                    fw.write(x + "\n");
                }
            }
            fw.flush();
            fw.close();
        } catch (IOException iox) {
            say("could not create the temp file:'" + iox.getMessage() +"'");
            if(isDebugOn()){
                iox.printStackTrace();
            }
            return null;
        }
        return _doExternalEdit(tempFile);
    }

    protected BufferManager.BufferRecord getBR(InputLine inputLine) {
        String rawArg = null;
        if (inputLine.getCommand().equals(EXECUTE_COMMAND) || inputLine.getCommand().equals(EDIT_COMMAND)) {
            // Since this is a shorthand, the input line looks like
            // ) 2
            rawArg = inputLine.getArg(ACTION_INDEX);
        } else {
            if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
                return null;
            }
            rawArg = inputLine.getArg(FIRST_ARG_INDEX);
        }

        int index = -1;
        try {
            index = Integer.parseInt(rawArg);
            return bufferManager.getBufferRecord(index);

        } catch (NumberFormatException t) {
            // no problem, maybe they used its name
        } catch (ArrayIndexOutOfBoundsException ai) {
            return null;
        }
        return bufferManager.getBufferRecord(rawArg);

    }

    protected int _doBufferDelete(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("(delete | rm) )index | name)");
            sayi("removes the buffer.");
            return RC_NO_OP;
        }
        BufferManager.BufferRecord br = getBR(inputLine);
        if (br == null) {
            say("sorry, I didn't understand that");
            return RC_NO_OP;
        }
        if (br.hasContent()) {
            if (!"y".equalsIgnoreCase(readline("buffer has not been saved. Do you still want to remove it?[y/n]"))) {
                say("aborted.");
                return RC_NO_OP;
            }
        }
        bufferManager.remove(br.src);

        return RC_CONTINUE;
    }

    private int _fileDir(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("(dir | ls) path");
            sayi("List contents of the directory. Note if this is a single file, nothing will be listed.");
            return RC_NO_OP;
        }

        Polyad request = new Polyad(IOEvaluator.DIR);
        try {
            request.addArgument(new ConstantNode(inputLine.getArg(FIRST_ARG_INDEX), Constant.STRING_TYPE));
        } catch (Throwable t) {
            say("sorry. I didn't understand that.");
            return RC_CONTINUE;
        }
        getState().getMetaEvaluator().evaluate(request, getState());
        Object obj = request.getResult();
        int i = 0;
        if (obj instanceof StemVariable) {
            StemVariable stemVariable = (StemVariable) obj;
            for (String key : stemVariable.keySet()) {
                i++;
                say(stemVariable.get(key).toString());
            }
            say(i + " entries");
        } else {
        }
        return RC_CONTINUE;
    }

    private int _fileMkDir(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("mkdir path");
            sayi("Make a directory in the file system. This creates all intermediate paths too if needed.");
            return RC_NO_OP;
        }

        try {
            String raw = IOEvaluator.MKDIR + "('" + inputLine.getArg(FIRST_ARG_INDEX) + "');";
            getInterpreter().execute(raw);
        } catch (Throwable throwable) {
            say("Error" + (throwable instanceof NullPointerException ? "." : ":" + throwable.getMessage()));
        }
        return RC_CONTINUE;

    }

    private int _fileRmDir(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("rmdir path");
            sayi("Removes the given path (but not the intermediate paths.)");
            return RC_NO_OP;
        }

        try {
            String raw = IOEvaluator.RMDIR + "('" + inputLine.getArg(FIRST_ARG_INDEX) + "');";
            getInterpreter().execute(raw);
        } catch (Throwable throwable) {
            say("Error" + (throwable instanceof NullPointerException ? "." : ":" + throwable.getMessage()));
        }
        return RC_CONTINUE;

    }

    private int _fileDelete(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("(delete | rm) filename");
            sayi("Delete the file.");
            return RC_NO_OP;
        }

        try {
            String raw = IOEvaluator.RM_FILE + "('" + inputLine.getArg(FIRST_ARG_INDEX) + "');";
            getInterpreter().execute(raw);
        } catch (Throwable throwable) {
            say("Error" + (throwable instanceof NullPointerException ? "." : ":" + throwable.getMessage()));
        }
        return RC_CONTINUE;
    }

    /**
     * Copies <i>any</i> two files on the system including between VFS.
     */

    private int _fileCopy(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("copy source target [-binary]");
            sayi("Copy a file from the source to the target. Note that the workspace is VFS aware.");
            sayi("-binary = treat the files as binary.");
            return RC_NO_OP;
        }
        boolean isBinary = inputLine.hasArg("-binary");
        inputLine.removeSwitch("-binary");
        try {
            String source = inputLine.getArg(FIRST_ARG_INDEX);
            String target = inputLine.getArg(FIRST_ARG_INDEX + 1);
            String readIt = IOEvaluator.READ_FILE + "('" + source + "'," + (isBinary ? FILE_OP_BINARY : FILE_OP_TEXT_STRING) + ")";
            String raw = IOEvaluator.WRITE_FILE + "('" + target + "'," + readIt + ");";
            getInterpreter().execute(raw);
        } catch (Throwable throwable) {
            say("Sorry, I couldn't do that: " + throwable.getMessage());
        }
        return RC_CONTINUE;
    }

    private int _doBufferList(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("ls | list");
            sayi("list all buffers with information about them.");
            return RC_NO_OP;

        }
        int count = 0;
        for (int i = 0; i < bufferManager.getBufferRecords().size(); i++) {
            BufferManager.BufferRecord br = bufferManager.getBufferRecords().get(i);
            if (!br.deleted) {
                count++;
                say(formatBufferRecord(i, bufferManager.getBufferRecords().get(i)));
            }
        }
        say("there are " + count + " active buffers.");
        return RC_CONTINUE;
    }

    private int _doBufferShow(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("show (index | name) [-src]");
            sayi("Show the buffer. If the buffer is linked, it will show the target.");
            sayi("-src will only have an effect with links and will show the source rather than the target.");
            return RC_NO_OP;
        }
        BufferManager.BufferRecord br = null;
        try {
            br = getBR(inputLine);
            if (br == null || br.deleted) {
                say("buffer not found");
                return RC_NO_OP;
            }
        } catch (Throwable t) {
            say("I can't find that buffer. Sorry.");
            return RC_NO_OP;

        }
        if (br.hasContent()) {
            for (String x : br.getContent()) {
                say(x);
            }
            return RC_CONTINUE;
        }
        // So nothing in the buffer. So the file.
        List<String> lines = null;
        try {
            if (br.isLink()) {
                // If this is a link but the user really wants to see the source, they need to supply a flag
                lines = bufferManager.read(inputLine.getIntArg(FIRST_ARG_INDEX), inputLine.hasArg("-src"));
            } else {
                lines = bufferManager.read(inputLine.getIntArg(FIRST_ARG_INDEX), true);
            }

        } catch (Throwable t) {
            say("error reading buffer");
            return RC_NO_OP;
        }
        if (lines.isEmpty()) {
            say("");

        } else {
            for (String x : lines) {
                say(x);
            }
        }
        return RC_CONTINUE;
    }

    public String BUFFER_RECORD_SEPARATOR = "|";

    protected String formatBufferRecord(int ndx, BufferManager.BufferRecord br) {
        // so a ? is shown if its a link, a * if its a buffer that hasn't been saved.
        String saved = (br.isLink() ? "?" : "") + (br.hasContent() ? "*" : " ");

        String a = BUFFER_RECORD_SEPARATOR + saved + BUFFER_RECORD_SEPARATOR;
        if (br.memoryOnly) {
            a = a + "m" + BUFFER_RECORD_SEPARATOR;
        }
        return ndx + a + br;
    }

    private int _doBufferLink(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("link source target [-copy]");
            sayi("Creates a link (for external editing) between source and target.");
            sayi("A common pattern is that source is on a VFS and target is a local file that you edit");
            sayi("Generally this is only needed if for some reason your editor cannot be configured to");
            sayi("work as an external editor.");
            sayi("If the -copy flag is used, target will be overwritten. In subsequent commands, e.g. run, save this will resolve the link.");
            return RC_NO_OP;
        }
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("sorry, you must supply a file name.");
            return RC_CONTINUE;
        }
        String source = inputLine.getArg(FIRST_ARG_INDEX);
        if (bufferManager.hasBR(source)) {
            say("sorry but a buffer for " + source + " already exists.");
            return RC_NO_OP;
        }
        String target = null;
        if (inputLine.hasArgAt(FIRST_ARG_INDEX + 1)) {
            target = inputLine.getArg(FIRST_ARG_INDEX + 1);
        }
        int ndx = bufferManager.link(source, target);

        BufferManager.BufferRecord br = bufferManager.getBufferRecord(ndx);
        br.srcSavePath = bufferManager.figureOutSavePath(getBufferDefaultSavePath(), br.src);
        br.linkSavePath = bufferManager.figureOutSavePath(getBufferDefaultSavePath(), br.link);
        say(formatBufferRecord(ndx, br));

        if (inputLine.hasArg("-copy")) {
            try {
                _fileCopy(inputLine);
                say(inputLine.getArg(FIRST_ARG_INDEX) + " copied to " + inputLine.getArg(FIRST_ARG_INDEX + 1));
            } catch (Throwable t) {
                say("could not copy " + inputLine.getArg(FIRST_ARG_INDEX) + "to " + inputLine.getArg(FIRST_ARG_INDEX + 1));
            }
        }
        return RC_CONTINUE;

    }

    /**
     * Boolean values function that returns true if the inputline has some form of help in it.
     *
     * @param inputLine
     * @return
     */
    protected boolean _doHelp(InputLine inputLine) {
        if (inputLine.hasArg("help") || inputLine.hasArg("-help") || inputLine.hasArg("--help")) return true;
        return false;
    }

    protected static final String BUFFER_IN_MEMORY_ONLY_SWITCH = "-m";

    private int _doBufferCreate(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("create path [" + BUFFER_IN_MEMORY_ONLY_SWITCH + "]");
            sayi("create a new buffer for the path.");
            sayi(BUFFER_IN_MEMORY_ONLY_SWITCH + " (optional) set this to be an in-memory only buffer.");
            return RC_CONTINUE;
        }
        boolean inMemoryOnly = inputLine.hasArg(BUFFER_IN_MEMORY_ONLY_SWITCH);
        inputLine.removeSwitch(BUFFER_IN_MEMORY_ONLY_SWITCH);
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("sorry, you must supply a file name.");
            return RC_CONTINUE;
        }
        String source = inputLine.getArg(FIRST_ARG_INDEX);
        if (FIRST_ARG_INDEX + 2 <= inputLine.size()) { // +2 because there is the ) command and the action,
            say("warning: Additional arguments detected. Did you want to create a link instead?");
            return RC_NO_OP;
        }
        if (bufferManager.hasBR(source)) {
            say("sorry but a buffer for " + source + " already exists.");
            return RC_NO_OP;
        }
        int ndx = bufferManager.create(source);
        BufferManager.BufferRecord br = bufferManager.getBufferRecord(ndx);
        br.memoryOnly = inMemoryOnly;
        if (!inMemoryOnly) {
            if (getTempDir() == null) {
                say("You must set the buffer save path");
                return RC_NO_OP;
            }
            br.srcSavePath = bufferManager.figureOutSavePath(getBufferDefaultSavePath(), br.src);
        }
        say(formatBufferRecord(ndx, br));
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
            return _envList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("Environment commands");
                sayi("        clear - remove all entries in the current environment.");
                sayi("     drop var - remove the variable.");
                sayi("      get var - show the value of the variable");
                sayi("         list - show the entire contents of the environment");
                sayi("         name - list the name (i.e. file path) of the currently loaded environment if there is one.");
                sayi("    load file - load a saved environment from a file");
                sayi("    save file - save the entire current environment to the file");
                sayi("set var value - set the given variable to the given value");
                return RC_NO_OP;
            case "clear":
                if (_doHelp(inputLine)) {
                    say("clear");
                    sayi("Clear all entries in the environment");
                    return RC_NO_OP;
                }

                env = new XProperties();
                say("Environment cleared.");
            case "drop":
                return _envDrop(inputLine);
            case "get":
                return _envGet(inputLine);
            case "list":
                return _envList(inputLine);
            case "load":
                return _envLoad(inputLine);
            case "save":
                return _envSave(inputLine);
            case "set":
                return _envSet(inputLine);
            case "name":
                if (_doHelp(inputLine)) {
                    say("name");
                    sayi("list the file path and name (if any) of the current environment.");
                    return RC_NO_OP;
                }

                if (envFile == null) {
                    say("No environment file has been set.");
                } else {
                    say(envFile.getAbsolutePath());
                }
                return RC_CONTINUE;
            default:
                say("Unknown environment command.");
                return RC_CONTINUE;

        }
    }

    private int _envSave(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("save [filename]");
            sayi("Saves the environment. If there is a default file it will save to that. Specifying the name forces the save to that file.");
            return RC_NO_OP;
        }

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

    private int _envLoad(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("load file");
            sayi("Load the given file as the current environment. This adds to the current environment");
            return RC_NO_OP;
        }

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

    private int _envDrop(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("drop variable");
            say("Drop i.e. remove the named variable from the environment.");
            return RC_NO_OP;
        }

        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Sorry, you must supply an environment variable name to remove it.");
            return RC_CONTINUE;
        }
        String pName = inputLine.getArg(FIRST_ARG_INDEX);
        env.remove(pName);
        say(pName + " rempved from the environment");
        return RC_CONTINUE;
    }

    private int _envGet(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("get variable");
            sayi("get the value for the given variable,");
            return RC_NO_OP;
        }

        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Sorry, you must supply an environment variable name to get its value.");
            return RC_CONTINUE;
        }
        String pName = inputLine.getArg(FIRST_ARG_INDEX);
        say(env.getString(pName));
        return RC_CONTINUE;
    }

    private int _envSet(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("set variable value");
            sayi("sets the variable to the given value. Caution, only string values are " +
                    "allowed and if there are embedded blanks, surround it with double quotes");
            sayi("e.g.");
            sayi("set my_var \"mairzy doats\"");
            return RC_NO_OP;
        }

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

    private int _envList(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("-list");
            sayi("List all the variables and their values in the current environment.");
            return RC_NO_OP;
        }

        if (env == null || env.isEmpty()) {
            say("(empty)");
        } else {
            say("Current environment variables:");
            say(env.toString(1));
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
        if (inputLine.hasArg(HELP_SWITCH)) {
            say("Modules commands:");
            sayi("[list] - list all loaded modules and their aliases. Default is to list modules.");
            sayi("(uri | alias) [-help] - list all full information for that module, including any documentation.");
            sayi("      If the module has been loaded, you can use any of the aliases, otherwise you need the uri");
            return RC_NO_OP;
        }
        if (inputLine.hasArg("-help")) {
            inputLine.removeSwitch("-help");
            _moduleHelp(inputLine);
            return RC_CONTINUE;
        }
        return _modulesList(inputLine);
    }

    /**
     * Contract is that the argument(s) are either URIs for a module or aliases. Print out the complete
     * help for each
     *
     * @param inputLine
     */
    private void _moduleHelp(InputLine inputLine) {
        for (int i = ACTION_INDEX; i < inputLine.getArgCount() + 1; i++) {

            String arg = inputLine.getArg(i);
            Module module = null;
            String importedString = "";
            if (arg.endsWith(MAliases.NS_DELIMITER)) {
                arg = arg.substring(0, arg.length() - 1);
            }
            if (StringUtils.isTrivial(arg)) {
                // They are asking for documentation for the default module, and there is none.
            } else {
                if (state.getMInstances().containsKey(arg)) {
                    module = state.getMInstances().get(arg);
                    importedString = getImportString(module.getNamespace());
                }
            }
            if (module == null) {
                try {
                    URI uri = URI.create(arg);
                    importedString = getImportString(uri);
                    module = state.getMTemplates().get(uri);

                } catch (Throwable t) {
                }

            }
            if (module != null) {
                say(importedString);
                List<String> docs = module.getDocumentation();
                for (String x : docs) {
                    say(x);
                }
                if (1 < inputLine.getArgCount()) {
                    say("");
                } // spacer
            }
        }
    }

    private int _moduleImports(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("imports");
            sayi("A table of imported modules and their aliases. ");
            sayi("You must either directly create a module or load it with " + SystemEvaluator.MODULE_LOAD + " to make QDL aware of it before importing it");
            return RC_NO_OP;
        }

        if (!state.getMAliases().hasImports()) {
            say("(no imports)");
            return RC_CONTINUE;
        }
        TreeSet<String> aliases = new TreeSet<>();
        for (URI uri : state.getMAliases().keySet()) {
            aliases.add(uri + "->" + state.getMAliases().getAlias(uri));
        }
        return printList(inputLine, aliases);
    }

    private int _modulesList(InputLine inputLine) {
        TreeSet<String> m = new TreeSet<>();
        if (getState().getMTemplates().keySet() == null || getState().getMTemplates().keySet().isEmpty()) {
            say("(no imported modules)");
            return RC_CONTINUE;
        }
        for (URI key : getState().getMTemplates().keySet()) {

            String out = getImportString(key);
            m.add(out);
        }
        // so these are sorted. Print them
        //return printList(inputLine, m);
        for (String x : m) {
            say(x);
        }
        return RC_CONTINUE;
    }

    /**
     * For a given URI, make the entry that is listed for the )modules -list command.
     *
     * @param key
     * @return
     */
    private String getImportString(URI key) {
        String out = key.toString();
        if (state.getMAliases().hasImports()) {
            out = out + " " + state.getMAliases().getAlias(key);
        } else {
            out = out + " -";
        }
        return out;
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
        if ((!inputLine.hasArg(HELP_SWITCH)) && (!inputLine.hasArgs() || inputLine.getArg(ACTION_INDEX).startsWith(SWITCH))) {
            return _funcsList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "--help":
                say("Function commands:");
                sayi("  drop name - Remove the (user defined) function from the system");
                sayi("edit {args} - edit the function with args (an integer) arguments");
                sayi("              No arg means it defaults to 0.");
                sayi("              You can, of course, redefine the function as you wish.");
                sayi("       help - this menu");
                sayi("       list - list all known functions. Allows display options.");
                sayi("     system - list all known system functions. Allows display options.");
                sayi("    -system - same as system.");
                return RC_NO_OP;
            case "drop":
                return _funcsDrop(inputLine);
            case "help":
                return _funcsHelp(inputLine);
            case "list":
                return _funcsList(inputLine);
            case "system":
            case "-system":
                return _funcsListSystem(inputLine);
            case "edit":
                return _doFuncEdit(inputLine);
            default:
                say("sorry, unrecognized command.");
        }
        return RC_CONTINUE;
    }

    private int _doFuncEdit(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("edit {arg_count} - edit the function with arg_count arguments");
            say("arg_count - a zero or positive integer. If omitted, the default is 0");
            say("You may use this to define functions as well. Since the function");
            say("definition is re-interpreted on editor exit, you can change the signature");
            say("such as adding/removing arguments or even rename the function. All this does");
            say("is tell the editor where to start editing.");
            return RC_NO_OP;
        }
        String fName = inputLine.getArg(inputLine.getArgCount() - 1);
        int argCount = 0;
        try {
            argCount = Integer.parseInt(inputLine.getLastArg());
        } catch (Throwable t) {
//            say("Could not parse argument count of \"" + inputLine.getLastArg() + "\"");
//            return RC_NO_OP;
        }
        FR_WithState fr_withState = null;
        try {
            fr_withState = getState().resolveFunction(fName, argCount, true);
            if (fr_withState.isExternalModule) {
                say("cannot edit external functions.");
                return RC_NO_OP;
            }

        } catch (UndefinedFunctionException ufx) {
            // ok, they are defining it now
        }


        String inputForm = fr_withState == null ? "" : InputFormUtil.inputForm(fName, argCount, getState());

        if (isTrivial(inputForm)) {
            say("new function '" + fName + "'");
            StringBuilder argList = new StringBuilder();
            argList.append("(");
            boolean isFirst = true;
            for (int i = 0; i < argCount; i++) {
                if (isFirst) {
                    isFirst = false;
                    argList.append("x").append(i);
                } else {
                    argList.append(", x").append(i);
                }
            }
            argList.append(")");
            inputForm = fName + argList + "->null;";
//            return RC_NO_OP;
        }
        List<String> f = StringUtils.stringToList(inputForm);
        if (useExternalEditor()) {
            f = _doExternalEdit(f);
        } else {
            f = _doLineEditor(f);
        }
        try {
            getInterpreter().execute(f);
            fr_withState = getState().resolveFunction(fName, argCount, true); // get it again because it was overwritten
            fr_withState.functionRecord.sourceCode = f; // update source in the record.
        } catch (Throwable t) {
            if (DebugUtil.isEnabled()) {
                t.printStackTrace();
            }

            say("could not interpret function:" + t.getMessage());
        }
        return RC_CONTINUE;
    }

    private int _funcsDrop(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("drop fname [arg_count]");
            sayi("Removes the function from the current workspace. Note this applies to user-defined functions, not imported functions.");
            sayi("If you supply no arg_count, then *all* definitions of the functions are removed.");
            return RC_NO_OP;
        }

        String fName = inputLine.getArg(FIRST_ARG_INDEX);

        int argCount = -1;
        if (inputLine.getArgCount() == 3) {
            String rawCount = inputLine.getArg(FIRST_ARG_INDEX + 1);
            try {
                argCount = Integer.parseInt(rawCount);
            } catch (Throwable t) {
                say("sorry, but \"" + rawCount + "\" is not a number. Aborting...");
                return RC_NO_OP;
            }
        }

        getState().getFTStack().remove(new FKey(fName, argCount));
        if (getState().getFTStack().containsKey(new FKey(fName, -1))) {
            say(fName + " removed.");
        } else {
            say("Could not remove " + fName);
        }
        return RC_CONTINUE;
    }

    private int _funcsHelp(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("help [fname arg_count] [-r regex]");
            sayi("List help for functions.");
            sayi("help (no argument) - print off the first line of the embedded help.");
            sayi("help fname - print help for the given name ");
            sayi("help fname arg_count - print the complete embedded help for the function with the given argument count.");
            sayi("If the regex is included, apply that to the results per line.");
            return RC_NO_OP;
        }

        if (!inputLine.hasArgAt(FIRST_ARG_INDEX) || inputLine.getArg(FIRST_ARG_INDEX).startsWith("-")) {
            // so they entered )funcs help Print off first lines of help
            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.addAll(getState().listAllDocumentation());
            return printList(inputLine, treeSet);
        }
        String fName = inputLine.getArg(FIRST_ARG_INDEX);

        int argCount = -1; // means return every similarly named function.
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

    /**
     * Any list of strings (functions, variables, modules, etc.) is listed using this formatting function.
     * If understands command line switches for width, columns and does some regex's too.
     *
     * @param inputLine
     * @param list      A simple list items, e.g., names of functions or variables.
     * @return
     */
    protected int printList(InputLine inputLine, TreeSet<String> list) {
        if (list.isEmpty()) {
            return RC_CONTINUE;
        }
        if (list.size() == 1) {
            say(list.first());
            return RC_CONTINUE;
        }
        Pattern pattern = null;
        if (inputLine.hasArg(REGEX_SWITCH)) {
            try {
                pattern = Pattern.compile(inputLine.getNextArgFor(REGEX_SWITCH));
                TreeSet<String> list2 = new TreeSet<>();
                //pattern.
                for (String x : list) {
                    if (pattern.matcher(x).matches()) {
                        list2.add(x);
                    }
                }
                list.clear();
                list.addAll(list2);
                list = list2;
            } catch (Throwable t) {
                say("sorry but there was a problem with your regex \"" + inputLine.getNextArgFor(REGEX_SWITCH) + "\":" + t.getMessage());
            }

        }
        if (inputLine.hasArg(COLUMNS_VIEW_SWITCH)) {
            for (String func : list) {
                say(func); // one per line
            }
            return RC_CONTINUE;
        }
        int displayWidth = 120; // just to keep thing simple
        if (inputLine.hasArg(DISPLAY_WIDTH_SWITCH)) {
            try {
                displayWidth = Integer.parseInt(inputLine.getNextArgFor(DISPLAY_WIDTH_SWITCH));
            } catch (Throwable t) {
                say("sorry, but " + inputLine.getArg(0) + " is not a number. Formatting for default width of " + displayWidth);
            }
        }

        // Find longest entry
        int maxWidth = 0;
        for (String x : list) {
            maxWidth = Math.max(maxWidth, x.length());
        }
        // special case. If the longest element is too long, just print as columns
        if (displayWidth <= maxWidth) {
            for (String x : list) {
                say(x);
            }
            return RC_CONTINUE;
        }
        maxWidth = 2 + maxWidth; // so the widest + 2 chars to make it readable.
        // number of columns are display / width, possibly plus 1 if there is a remainder
        //int colCount = displayWidth / maxWidth + (displayWidth % maxWidth == 0 ? 0 : 1);
        int colCount = displayWidth / maxWidth;
        colCount = colCount + (colCount == 0 ? 1 : 0); // Make sure there is at least 1 columns
        int rowCount = list.size() / colCount;
        rowCount = rowCount + (rowCount == 0 ? 1 : 0); // and at least one row
        String[] output = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            output[i] = ""; // initialize it
        }
        int pointer = 0;
        for (String func : list) {
            int currentLine = pointer++ % rowCount;
            if (rowCount == 1) {
                // single row, so don't pad, just a blank between entries
                output[currentLine] = output[currentLine] + func + "  ";
            } else {
                output[currentLine] = output[currentLine] + LJustify(func, maxWidth);
            }
        }
        for (String x : output) {
            say(x);
        }

        return RC_CONTINUE;
    }

    protected int _funcsListSystem(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("system");
            sayi("List all system (built-in) functions.");
            return RC_NO_OP;
        }

        boolean listFQ = inputLine.hasArg(FQ_SWITCH);
        TreeSet<String> funcs = getState().getMetaEvaluator().listFunctions(listFQ);
        int rc = printList(inputLine, funcs);
        say(funcs.size() + " total functions");
        return rc;
    }

    public static final String LIST_MODULES_SWITCH = "-m";
    public static final String LIST_INTRINSIC_SWITCH = "-intrinsic";

    protected int _funcsList(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("list [" + COMPACT_ALIAS_SWITCH + "|" + LIST_MODULES_SWITCH + "]");
            sayi("List all user defined functions.");
            sayi(COMPACT_ALIAS_SWITCH + " will collapse all modules to show by alias.");
            sayi(LIST_MODULES_SWITCH + " List modules as well. Default is just what you've defined.");
            sayi(LIST_INTRINSIC_SWITCH + " List modules as well. Default is not to show them.");
            sayi("    Note that you cannot modify or query them, simply see what they are named.");
            return RC_NO_OP;
        }
        boolean listFQ = inputLine.hasArg(FQ_SWITCH);
        boolean includeModules = inputLine.hasArg(LIST_MODULES_SWITCH);
        inputLine.removeSwitch(LIST_MODULES_SWITCH);
        boolean showIntrinsic = inputLine.hasArg(LIST_INTRINSIC_SWITCH);
        inputLine.removeSwitch(LIST_INTRINSIC_SWITCH);
        boolean useCompactNotation = inputLine.hasArg(COMPACT_ALIAS_SWITCH);
        TreeSet<String> funcs = getState().listFunctions(useCompactNotation, null, includeModules, showIntrinsic);
        // These are fully qualified.
        int rc = -1;
        if (listFQ) {
            rc = printList(inputLine, funcs);
            say(funcs.size() + " total functions");
        } else {
            TreeSet<String> funcs2 = new TreeSet<>();
            for (String x : funcs) {
                int indexOf = x.lastIndexOf(SCHEME_DELIMITER);
                if (0 < indexOf) {
                    funcs2.add(x.substring(indexOf + 1));
                } else {
                    funcs2.add(x);
                }
            }
            rc = printList(inputLine, funcs2);
            say(funcs2.size() + " total functions");

        }
        return rc;
    }

    /**
     * Either show all variables (no arg or argument of "list") or <br/><br/>
     * drop name -- remove the given symbol from the local symbol table. This does not effect modules.
     *
     * @param inputLine
     * @return
     */
    private int doVars(InputLine inputLine) {
        if (!inputLine.hasArg(HELP_SWITCH) && (!inputLine.hasArgs() || inputLine.getArg(ACTION_INDEX).startsWith(SWITCH))) {
            return _varsList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("Variable commands");
                say("     drop - remove a variable from the system");
                say("     list - list all user defined variables. If you include the -m flag, module");
                say("            variables are shown too");
                say("     size - try to guestimate the size of all the symbols used.");
                sayi("  system - list all system variables.");
                sayi("edit [" + EDIT_TEXT_FLAG + "]- edit the given variable. Adding the " + EDIT_TEXT_FLAG);
                sayi("          will treat the contents as text and set the variable to that.");
                sayi("          Note that you won't need quotes around the string if you edit it with " + EDIT_TEXT_FLAG + ".");
                sayi("          and you can enter linefeeds etc. which will be converted.");
                return RC_NO_OP;
            case "system":
                return _varsSystem(inputLine);
            case "list":
                return _varsList(inputLine);
            case "drop":
                return _varsDrop(inputLine);
            case "size":
                say(state.getStackSize() + " symbols defined.");
                return RC_CONTINUE;
            case "edit":
                return _doVarEdit(inputLine);
            default:
                say("Unknown variable command.");

        }
        return RC_CONTINUE;
    }

    public static final String EDIT_TEXT_FLAG = "-x";

    private int _doVarEdit(InputLine inputLine) {
        // process flags first
        boolean isText = inputLine.hasArg(EDIT_TEXT_FLAG);
        inputLine.removeSwitch(EDIT_TEXT_FLAG);
        String varName = inputLine.getLastArg();
        List<String> content = new ArrayList<>();
        boolean isDefined = getState().isDefined(varName);
        boolean isStem = varName.endsWith(StemVariable.STEM_INDEX_MARKER);
        if (isDefined) {
            if (isText) {
                if (isStem) {
                    // convert stem to list
                    StemVariable v = (StemVariable) getState().getValue(varName);
                    if (!v.isList()) {
                        say("sorry, but only a list of strings can be edited as text");
                        return RC_NO_OP;
                    }
                    JSONArray jsonArray = (JSONArray) v.toJSON();
                    content = jsonArray;
                } else {
                    String v = getState().getValue(varName).toString();
                    v.replace("\n", "\\n");
                    content = StringUtils.stringToList(v);
                }

            } else {
                String inputForm = InputFormUtil.inputFormVar(varName, 2, getState());
                content.add(inputForm);
            }
        }

        if (useExternalEditor()) {
            content = _doExternalEdit(content);
        } else {
            content = _doLineEditor(content);
        }

        if (isText) {
            if (isStem) {
                StemVariable newStem = new StemVariable();
                newStem.addList(content);
                getState().setValue(varName, newStem);
            } else {
                String newValue = StringUtils.listToString(content);

                getState().setValue(varName, newValue);
            }
        } else {
            String newValue = StringUtils.listToString(content);

            newValue = newValue.trim();
            if (!newValue.endsWith(";")) {
                newValue = newValue + ";";
            }

            try {
                getInterpreter().execute(varName + " := " + newValue);
            } catch (Throwable throwable) {
                if (DebugUtil.isEnabled()) {
                    throwable.printStackTrace();
                }
                say("Sorry, could not update value of \"" + varName + "\"");
                return RC_NO_OP;
            }

        }
        return RC_CONTINUE;
    }

    protected int _varsSystem(InputLine inputLine) {
        TreeSet<String> sysVars = new TreeSet<>();
        //  sysVars.addAll(getState().getSystemVars().listVariables());
        return printList(inputLine, sysVars);
    }

    private int _varsDrop(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("drop name");
            sayi("Drops i.e. removes the given variable from the current workspace.");
            return RC_NO_OP;
        }

        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            say("Sorry. You did not supply a variable name to drop");
            return RC_NO_OP;
        }
        getState().remove(inputLine.getArg(FIRST_ARG_INDEX));
        say(inputLine.hasArgAt(FIRST_ARG_INDEX) + " has been removed.");
        return RC_CONTINUE;
    }

    private int _varsList(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("list [" + COMPACT_ALIAS_SWITCH + "]");
            sayi("Lists the variables in the current workspace.");
            sayi(COMPACT_ALIAS_SWITCH + " will collapse all modules to show by alias.");
            return RC_NO_OP;
        }
        boolean includeModules = inputLine.hasArg(LIST_MODULES_SWITCH);
        boolean useCompactNotation = inputLine.hasArg(COMPACT_ALIAS_SWITCH);
        boolean showIntrinsic = inputLine.hasArg(LIST_INTRINSIC_SWITCH);
        inputLine.removeSwitch(LIST_MODULES_SWITCH);
        inputLine.removeSwitch(COMPACT_ALIAS_SWITCH);
        inputLine.removeSwitch(LIST_INTRINSIC_SWITCH);
        return printList(inputLine, getState().listVariables(useCompactNotation, includeModules, showIntrinsic));
    }

    /**
     * Just print the general help
     * <pre>
     *     )help (* | name)  [arg_count]
     * </pre>
     * * will print the first line of all user defined functions in the workspace.<br/>
     * Otherwise name is the name of a function, system or user defined<br/>
     * The optional arg_count is ignored for system functions, but will call up detailed
     * information for the user functions.
     *
     * @param inputLine
     * @return
     */
    private int doHelp(InputLine inputLine) {
        if (!inputLine.hasArgs()) { // so no arguments
            showGeneralHelp();
            return RC_CONTINUE;
        }
        if (inputLine.getArg(ACTION_INDEX).equals("--help")) {
            showHelp4Help();
            return RC_CONTINUE;
        }
        String name = inputLine.getArg(ACTION_INDEX);
        if (name.equals("*")) {
            // so they entered )funcs help Print off first lines of help
            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.addAll(getState().listAllDocumentation());
            if (treeSet.isEmpty()) {
                say("(no user-defined functions)");
                return RC_CONTINUE;
            }
            return printList(inputLine, treeSet);
        }
        if (name.equals("-online")) {
            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.addAll(onlineHelp.keySet());
            if (treeSet.isEmpty()) {
                say("(no online help)");
                return RC_CONTINUE;
            }
            say("Help is available for the following (" + treeSet.size() + " topics):");
            return printList(inputLine, treeSet);

        }
        if (onlineHelp.containsKey(name)) {
            say(onlineHelp.get(name));
            return RC_CONTINUE;
        }
        if (name.endsWith(MAliases.NS_DELIMITER)) {
            List<String> doxx = getState().listModuleDoc(name);
            if (doxx.isEmpty()) {
                say("Sorry, no help for '" + name + "'");
                return RC_CONTINUE;
            }
            for (String x : doxx) {
                say(x);
            }
            return RC_CONTINUE;

        }
        // Not a system function, so see if it is user defined. Find any arg count first
        int argCount = -1; // means return every similarly named function.
        String rawArgCount = null;
        if (inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            rawArgCount = inputLine.getArg(FIRST_ARG_INDEX);
        }

        try {
            if (rawArgCount != null) {
                argCount = Integer.parseInt(rawArgCount);
            }
        } catch (Throwable t) {
            say("Sorry, but \"" + rawArgCount + "\" is not an integer");
            return RC_CONTINUE;
        }
        List<String> doxx = getState().listFunctionDoc(name, argCount);
        if (doxx.isEmpty()) {
            if (-1 < argCount) {
                say("sorry, no help for " + name + "(" + argCount + ")");
            } else {
                say("sorry, no help for \"" + name + "\"");
            }
        } else {
            for (String x : doxx) {
                say(x);
            }
        }

        return RC_CONTINUE;
    }


    HashMap<String, String> onlineHelp = new HashMap<>();

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
        if (!inputLine.hasArgs()) { // so no arguments
            say("no command found");
            return RC_CONTINUE;
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("Workspace commands");
                sayi(" load file  - load a saved workspace.");
                sayi("save [file] - save the current workspace to the given file. If the current workspace");
                sayi("              has been loaded or saved, you may omit the file.");
                sayi("      clear - removes all user defined variables and functions");
                sayi("        get -  get a workspace value.");
                sayi("        lib - list the entries in a library.");
                sayi("     memory - give the amount of memory available to the workspace.");
                sayi("       name - give the file name of the currently loaded workspace.");
                say("               If no workspace has been loaded, no name is returned.");
                sayi("        set -  set a workspace value.");
                return RC_NO_OP;
            case "load":
                return _wsLoad(inputLine);
            case "save":
                return _wsSave(inputLine);
            case "clear":
                return _wsClear(inputLine);
            case "get":
                return _wsGet(inputLine);
            case "set":
                return _wsSet(inputLine);
            case "lib":
                if (2 < inputLine.getArgCount() && inputLine.getArg(FIRST_ARG_INDEX).equals("drop")) {
                    return _wsListDrop(inputLine);
                }
                return _wsLibList(inputLine);
            case "name":
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
                return _fileVFS(inputLine);
            default:
                say("unrecognized workspace command.");
                return RC_NO_OP;
        }

    }

    private int _wsListDrop(InputLine inputLine) {
        // Drop a workspace or collection of them
        if (inputLine.hasArg("--help")) {
            say(")lib drop file | " + REGEX_SWITCH + " regex [-f]");
            sayi("Drop, i.e. delete, either a single file or collection of them");
            sayi("-r means to use the regex to determine the file list for deletion");
            sayi("-f = force deletion flag. If this is not present, each file will get prompted");
            sayi("     If present, all files will be deleted.");
            return RC_CONTINUE;
        }
        String regex = null;
        inputLine.removeSwitch("drop"); // so it is not interpreted as a file name.
        if (inputLine.hasArg(REGEX_SWITCH)) {
            regex = inputLine.getNextArgFor(REGEX_SWITCH);
            inputLine.removeSwitchAndValue(REGEX_SWITCH);
        }
        boolean forceDeletes = inputLine.hasArg("-f");
        inputLine.removeSwitch("-f");
        File currentFile = _resolveLibFile(inputLine);
        if (currentFile == null) {
            say("Sorry, no file specified and no default file.");
            return RC_NO_OP;
        }
        if (currentFile.isFile()) {
            if (!forceDeletes) {
                forceDeletes = readline("Are you sure you want to delete the workspace \"" + currentFile.getAbsolutePath() + "\" (y/n)?").equals("y");
            }
            if (forceDeletes) {
                boolean rc = currentFile.delete();
                if (rc) {
                    say("deleted: " + currentFile.getAbsolutePath());
                } else {
                    say(currentFile.getAbsolutePath() + " could not be deleted");
                }
                return RC_CONTINUE;
            } else {
                return RC_NO_OP;
            }
        }
        // it's a directory. Apply any regex.
        Pattern pattern = null;

        FilenameFilter regexff = null;
        if (regex != null) {
            try {
                pattern = Pattern.compile(regex);
            } catch (PatternSyntaxException patternSyntaxException) {
                say("sorry, there is a problem with your regex: \"" + regex + "\":" + patternSyntaxException.getMessage());
                return RC_NO_OP;
            }
            regexff = new RegexFileFilter(pattern);
        }
        File[] files;
        if (regexff == null) {
            files = currentFile.listFiles();
        } else {
            files = currentFile.listFiles(regexff);
        }
        TreeSet<String> deletedFiles = new TreeSet<>();
        if (forceDeletes) {
            for (File file : files) {
                if (file.isFile()) { // don't delete directories!
                    if (file.delete()) {
                        deletedFiles.add(file.getAbsolutePath());
                    }
                }
            }
            for (String x : deletedFiles) {
                say("deleted " + x);
            }
            return RC_CONTINUE;
        }
        for (File f : files) {
            if (f.isFile()) {
                boolean doDelete = readline("Are you sure you want to delete the workspace \"" + f.getAbsolutePath() + "\" (y/n)?").equals("y");
                if (doDelete) {
                    if (f.delete()) {
                        deletedFiles.add(f.getAbsolutePath());
                    }
                }
            }
        }
        if (!deletedFiles.isEmpty()) {
            for (String x : deletedFiles) {
                say("deleted: " + x);
            }

        }
        //currentFile.l
        return RC_CONTINUE;
    }


    private int _fileVFS(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("vfs");
            sayi("Print any information about mounted virtual file systems.");
            return RC_NO_OP;
        }

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

    String makeColumn(String spaces, String text) {
        if (spaces.length() < text.length()) {
            return text;
        }
        return text + spaces.substring(0, spaces.length() - text.length());
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    boolean prettyPrint = false;

    protected boolean isOnOrTrue(String x) {
        return x.equals("on") || x.equals("true");
    }

    protected String onOrOff(boolean b) {
        return b ? "on" : "off";
    }

    protected void printAllWSVars() {
        Map<String, Object> allVars = new HashMap<>();
        for (String s : ALL_WS_VARS) {
            Object obj = getWSVariable(s);
            allVars.put(s, obj);
        }
        List<String> list = StringUtils.formatMap(allVars, null, true, true, 0, 72);
        for (String s : list) {
            say(s);
        }

    }

    String NOT_SET = "(not set)";

    protected Object getWSVariable(String key) {
        switch (key) {
            case PRETTY_PRINT:
            case PRETTY_PRINT_SHORT:
                return isPrettyPrint();
            case ECHO:
                return isEchoModeOn();
            case DEBUG:
                return isDebugOn();
            case UNICODE_ON:
                return State.isPrintUnicode();
            case ASSERTIONS_ON:
                return isAssertionsOn();
            case ANSI_MODE_ON:
                return isAnsiModeOn();
            case RUN_INIT_ON_LOAD:
                return runInitOnLoad;
            case START_TS:
                if (startTimeStamp != null) {
                    return Iso8601.date2String(startTimeStamp);
                }
                return NOT_SET;
            case EXTERNAL_EDITOR:
                return getExternalEditorName();
            case USE_EXTERNAL_EDITOR:
                return isUseExternalEditor();
            case ENABLE_LIBRARY_SUPPORT:
                return getState().isEnableLibrarySupport();
            case LIB_PATH_TAG:
                return getState().getLibPath();
            case DESCRIPTION:
                if (isTrivial(getDescription())) {
                    return NOT_SET;
                }
                return getDescription();
            case CURRENT_WORKSPACE_FILE:
                if (currentWorkspace == null) {
                    return NOT_SET;
                }
                return currentWorkspace.getAbsolutePath();
            case WS_ID:
                if (isTrivial(getWSID())) {
                    return NOT_SET;
                }
                return getWSID();
            case COMPRESS_XML:
                return isCompressXML();
            case SAVE_DIR:
                if (saveDir == null) {
                    return NOT_SET;
                }
                return saveDir.getAbsolutePath();
            case AUTOSAVE_ON:
                return isAutosaveOn();
            case AUTOSAVE_MESSAGES_ON:
                return isAutosaveMessagesOn();
            case AUTOSAVE_INTERVAL:
                return getAutosaveInterval();
            case ROOT_DIR:
                if (rootDir == null) {
                    return NOT_SET;
                }
                return rootDir.getAbsolutePath();
            default:
                return "unknown workspace variable";
        }
    }

    protected int _wsGet(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("get [ws_variable]");
            sayi("Retrieve the value of the given variable for the workspace.");
            sayi("No value means to return a list of supported variables.");
            sayi("You can get online help using the )help facility.");
            return RC_NO_OP;
        }
        // remember that the input line reads )ws get so the 1st argument is the name of this command
        if (inputLine.getArgCount() == 1) {
            printAllWSVars();
            return RC_CONTINUE;
        }
        String variable = inputLine.getArg(2);
        Object value = getWSVariable(variable);
        if (value instanceof Boolean) {
            say(variable + " is " + onOrOff((Boolean) value));
        } else {
            say(variable + " is " + value);

        }

        switch (inputLine.getArg(2)) {
            case PRETTY_PRINT:
            case PRETTY_PRINT_SHORT:
                say(onOrOff(isPrettyPrint()));
                break;
            case ECHO:
                say(onOrOff(isEchoModeOn()));
                break;
            case DEBUG:
                say(onOrOff(isDebugOn()));
                break;
            case UNICODE_ON:
                say(onOrOff(State.isPrintUnicode()));
                break;
            case ASSERTIONS_ON:
                say(onOrOff(isAssertionsOn()));
                break;
            case RUN_INIT_ON_LOAD:
                say(onOrOff(runInitOnLoad));
                break;
            case ANSI_MODE_ON:
                say(onOrOff(ansiModeOn));
                break;
            case START_TS:
                if (startTimeStamp != null) {
                    say("startup time at " + Iso8601.date2String(startTimeStamp));
                } else {
                    say("(not set)");
                }
                break;
            case EXTERNAL_EDITOR:
                say(getExternalEditorName());
                break;
            case USE_EXTERNAL_EDITOR:
                say(isUseExternalEditor() ? "on" : "off");
                break;
            case ENABLE_LIBRARY_SUPPORT:
                say(getState().isEnableLibrarySupport() ? "on" : "off");
                break;
            case LIB_PATH_TAG:
                say("current " + LIB_PATH_TAG + "=" + getState().getLibPath());
                break;
            case DESCRIPTION:
                if (isTrivial(getDescription())) {
                    say("(no description set)");
                } else {
                    say(getDescription());
                }
                break;
            case CURRENT_WORKSPACE_FILE:
                if (currentWorkspace == null) {
                    say("not set");
                } else {
                    say(currentWorkspace.getAbsolutePath());
                }
                break;
            case WS_ID:
                if (isTrivial(getWSID())) {
                    say("(workspace id not set)");
                } else {
                    say(getWSID());
                }
                break;
            case COMPRESS_XML:
                say(onOrOff(isCompressXML()));
                break;
            case SAVE_DIR:
                if (saveDir == null) {
                    say("(save directory not set)");
                } else {
                    say(saveDir.getAbsolutePath());
                }
                break;
            case AUTOSAVE_ON:
                say("autosave is " + (isAutosaveOn() ? "on" : "off"));
                break;
            case AUTOSAVE_MESSAGES_ON:
                say("autosave messages are " + (isAutosaveMessagesOn() ? "on" : "off"));
                break;
            case AUTOSAVE_INTERVAL:
                say("autosave interval is " + getAutosaveInterval() + " ms.");
                break;
            case ROOT_DIR:
                if (rootDir == null) {
                    say("(root directory not set)");
                } else {
                    say(rootDir.getAbsolutePath());
                }
                break;
            default:
                say("unknown workspace variable");
                break;
        }
        return RC_CONTINUE;
    }

    public static final String PRETTY_PRINT_SHORT = "pp";
    public static final String PRETTY_PRINT = "pretty_print";
    public static final String ECHO = "echo";
    public static final String UNICODE_ON = "unicode";
    public static final String DEBUG = "debug";
    public static final String START_TS = "start_ts";
    public static final String ROOT_DIR = "root_dir";
    public static final String SAVE_DIR = "save_dir";
    public static final String COMPRESS_XML = "compress_xml";
    public static final String WS_ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String CURRENT_WORKSPACE_FILE = "ws_file";
    public static final String AUTOSAVE_ON = "autosave_on";
    public static final String AUTOSAVE_MESSAGES_ON = "autosave_messages_on";
    public static final String AUTOSAVE_INTERVAL = "autosave_interval";
    public static final String EXTERNAL_EDITOR = "external_editor";
    public static final String USE_EXTERNAL_EDITOR = "use_external_editor";
    public static final String ENABLE_LIBRARY_SUPPORT = "enable_library_support";
    public static final String ASSERTIONS_ON = "assertions_on";
    public static final String ANSI_MODE_ON = "ansi_mode";

    /**
     * This will either print out the information about a single workspace (if a file is given)
     * or every workspace in a directory. It accepts regexes as a file filter too
     * <br/>
     * <pre>
     * )ws lib /home/me/qdl/var/ws -r ws.*\\.*
     * </pre>
     * Prints out all the ws info for ws*.* in the directory. If a file is given, the regex is ignored.
     *
     * @param inputLine
     * @return
     */
    protected int _wsLibList(InputLine inputLine) {
        int displayWidth = 120; //default

        if (_doHelp(inputLine)) {
            say("lib [file] " + CLA_VERBOSE_ON + " | " + CLA_LONG_FORMAT_ON + " | "
                    + SHOW_FAILURES + " | [" + DISPLAY_WIDTH_SWITCH + " cols] | [" +
                    REGEX_SWITCH + " regex]");
            say("display information about the given file. If no file is specified, a listing of everything is printed");
            say(CLA_VERBOSE_ON + " = print out a very long listing");
            say(CLA_LONG_FORMAT_ON + " - print out a listing restricting everything property to a single line");
            say(SHOW_FAILURES + " = show output for files that cannot be deserialized and why.");
            say(SHOW_ONLY_FAILURES + " = show only output for files that cannot be deserialized and why.");
            say(DISPLAY_WIDTH_SWITCH + " cols = set the printed output to the given number of columns. Default is " + displayWidth);
            say(REGEX_SWITCH + " regex = filter output using the regex.");
            say("E.g.");
            say(")lib " + CLA_LONG_FORMAT_ON + " " + DISPLAY_WIDTH_SWITCH + " 80 " + REGEX_SWITCH + " wlcg.*");
            say("shows all workspaces that start with wlcg, restricting the per attributes output to a single line");
            say("(truncation is possible and denoted with an ellipsis), restricting the total width to 80 characters");
            say("Note that if you just ask it to list the directory, every file will be read, so for a very large");
            say("directory this may take some time");
            say("E.g.");
            say(")lib " + REGEX_SWITCH + " .*\\.ws");
            say("shows all files ending in .ws Since a period is special character in regexes, it must be escaped.");
            say(".* means match any character, \\.ws means it must end in '.ws'.");

            return RC_CONTINUE;
        }
        //       String fileName = null;
        File currentFile = null;
        boolean showOnlyFailures = inputLine.hasArg(SHOW_ONLY_FAILURES);
        boolean isVerbose = inputLine.hasArg(CLA_VERBOSE_ON); // print everything
        boolean isLongFormat = inputLine.hasArg(CLA_LONG_FORMAT_ON); // print long format
        boolean isShortFormat = !(isVerbose || isLongFormat);
        boolean showFailures = inputLine.hasArg(SHOW_FAILURES);
        if (showOnlyFailures) {
            showFailures = true;// so it gets ignored later
        }
        if (inputLine.hasArg(DISPLAY_WIDTH_SWITCH)) {
            displayWidth = inputLine.getNextIntArg(DISPLAY_WIDTH_SWITCH);
        }
        // remove any switch so we can figure out what the arguments are.
        inputLine.removeSwitch(CLA_VERBOSE_ON);
        inputLine.removeSwitch(CLA_LONG_FORMAT_ON);
        inputLine.removeSwitch(SHOW_FAILURES);
        inputLine.removeSwitch(SHOW_ONLY_FAILURES);
        inputLine.removeSwitchAndValue(DISPLAY_WIDTH_SWITCH);
        Pattern pattern = null;
        //   String regex = null;

        FilenameFilter regexff = null;
        if (inputLine.hasArg(REGEX_SWITCH)) {
            String rx = inputLine.getNextArgFor(REGEX_SWITCH);
            try {
                pattern = Pattern.compile(rx);
            } catch (PatternSyntaxException patternSyntaxException) {
                say("sorry, there is a problem with your regex: \"" + rx + "\":" + patternSyntaxException.getMessage());
                return RC_NO_OP;
            }
            regexff = new RegexFileFilter(pattern);
            inputLine.removeSwitchAndValue(REGEX_SWITCH);
        }
        currentFile = _resolveLibFile(inputLine);
        if (currentFile == null) {
            say("Sorry could not determine what the current library directory is. Did you set the " + SAVE_DIR + "?");
            return RC_NO_OP;
        }
        // That's been resolved.
        // currentFile is either a single file or a directory.
        int failureCount = 0;
        int successCount = 0;
        if (currentFile.isFile()) {
            say("processing file " + currentFile.getAbsolutePath());
            WSLibEntry w = _getWSLibEntry(currentFile);
            if (w != null) {
                if (showOnlyFailures && !w.failed) {
                    return RC_CONTINUE;
                }

                if (!showFailures && w.failed) {
                    return RC_CONTINUE;
                }
                if (isShortFormat) {
                    say(w.shortFormat(displayWidth));
                    successCount++;
                } else {
                    List<String> out = formatMap(w.toMap(),
                            null,
                            true, isVerbose, 1, displayWidth);
                    for (String x : out) {
                        say(x);
                        successCount++;
                    }
                }
            }
            if (successCount == 0 && 0 < failureCount) {
                say("(there were " + failureCount + " failures. Rerun with " + SHOW_FAILURES + " switch to see them.");
            }
            return RC_CONTINUE;
        }
        File[] wsFileList;

        wsFileList = currentFile.listFiles(regexff);
        if (wsFileList == null || wsFileList.length == 0) {
            say("no workspaces found");
            return RC_CONTINUE;
        }
        TreeMap<String, File> sortedFiles = new TreeMap<>();
        for (File file : wsFileList) {
            sortedFiles.put(file.getAbsolutePath(), file);
        }

        boolean firstPass = true;
        say("showing files for " + currentFile.getAbsolutePath());
        for (String absPath : sortedFiles.keySet()) {
            File fff = sortedFiles.get(absPath);
            WSLibEntry w = _getWSLibEntry(fff);
            if (w == null) continue;
            if (showOnlyFailures && !w.failed) {
                failureCount++;
                continue;
            }

            if (!showFailures && w.failed) {
                failureCount++;
                continue;
            }
            if (isShortFormat) {
                successCount++;
                say(w.shortFormat(displayWidth));
            } else {
                if (firstPass) {
                    firstPass = false;
                } else {
                    say("-----");
                }
                List<String> out = formatMap(w.toMap(),
                        null,
                        true, isVerbose, 1, displayWidth);
                for (String x : out) {
                    successCount++;
                    say(x);
                }

            }

        }
        if (showOnlyFailures) {
            say("found " + successCount + " failures");

        } else {
            say("found " + successCount + " workspaces" + (0 < failureCount ? (", " + failureCount + " failures") : ""));
        }
        return RC_CONTINUE;
    }

    protected File _resolveLibFile(InputLine inputLine) {
        String fileName = null;
        File currentFile;
        if (1 < inputLine.getArgCount()) {
            fileName = inputLine.getArg(FIRST_ARG_INDEX);
            currentFile = new File(fileName);
        } else {
            if (saveDir != null) {
                currentFile = saveDir;
            } else {
                currentFile = rootDir;
            }
            if (currentFile == null) {
                return null;
            }
        }
        // current file absolute means its been resolved.
        if (!currentFile.isAbsolute()) {
            if (saveDir == null) {
                if (rootDir == null) {
                    return null;
                }
                if (fileName == null) {
                    return null;
                }
                currentFile = new File(rootDir, fileName);
            } else {
                currentFile = new File(saveDir, fileName);
            }
        }
        return currentFile;
    }

    public static class RegexFileFilter implements FilenameFilter {
        public RegexFileFilter(Pattern pattern) {
            this.pattern = pattern;
        }

        Pattern pattern;

        @Override
        public boolean accept(File dir, String name) {
            return pattern.matcher(name).matches();
        }
    }

    public static class WSLibEntry {
        Date ts;
        String id;
        String description;
        boolean isCompressed = false;
        String filename;
        String filepath;
        Date lastSaved_ts;
        String fileFormat;
        long length = -1L;
        boolean failed = false; // only set true when it actually fails.
        String failMessage;
        Throwable exception;

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            if (failed) {
                map.put("create_ts", ts);
                map.put("length", length);
                map.put("file_name", filename);
                map.put("file_path", filepath);
                map.put("last_modified", lastSaved_ts);
                map.put("message", failMessage);
                map.put("status", "FAILED");
                return map;
            }
            map.put("create_ts", ts);
            map.put(WS_ID, id);
            map.put(DESCRIPTION, description);
            map.put("compressed", isCompressed);
            map.put("file_name", filename);
            map.put("file_path", filepath);
            map.put("last_modified", lastSaved_ts);
            map.put("length", length);
            map.put("format", fileFormat);
            return map;
        }

        @Override
        public String toString() {
            return "WSLibEntry{" +
                    "ts=" + ts +
                    ", id='" + id + '\'' +
                    ", description='" + description + '\'' +
                    ", isCompressed=" + isCompressed +
                    ", filename='" + filename + '\'' +
                    ", filepath='" + filepath + '\'' +
                    ", lastSaved_ts=" + lastSaved_ts +
                    ", length=" + length +
                    '}';
        }

        public String shortFormat(int displayWidth) {
            if (failed) {
                String out = pad2(isTrivial(filename) ? "(no file)" : filename, 15);
                out = out + "   failed:" + pad2(failMessage, displayWidth - 23);
                return out;
            }
            String out = pad2(isTrivial(filename) ? "(no file)" : filename, 15);
            out = out + " " + (isCompressed ? "*" : " ");
            String lengthToken = "";
            NumberFormat formatter = new DecimalFormat("#0.000");
            double oneK = 1024.0;
            if (0 <= length && length < oneK) {
                lengthToken = length + "b";

            }
            if (oneK <= length && length < Math.pow(oneK, 2)) {
                lengthToken = formatter.format((length / oneK)) + "k";

            }
            if (Math.pow(oneK, 2) <= length && length < Math.pow(oneK, 3)) {
                lengthToken = formatter.format(length / Math.pow(oneK, 2)) + "m";
            }
            if (Math.pow(oneK, 3) <= length && length < Math.pow(oneK, 4)) {
                lengthToken = formatter.format(length / Math.pow(oneK, 3)) + "g";
            }


            out = out + " " + pad2(lengthToken, 10);

            if (isTrivial(id)) {
                //out = out + " " + pad2("(no id)", 10);
                out = out + " " + pad2("    -", 15);
            } else {
                out = out + " " + pad2(id, 15);
            }
            if (ts == null) {
                out = out + " " + pad2("(no date)", 25);
            } else {
                out = out + " " + pad2(ts, false, 30);
            }
            if (isTrivial(description)) {
                //out = out + " " + pad2("(no description)", displayWidth - 55);
                out = out + " " + pad2("    ----", displayWidth - 55);
            } else {
                out = out + " " + pad2(description, displayWidth - 55);
            }

            return out;
        }

    }

    /**
     * Reads a file and tries to figure out how it was serialized, then returns the information needed to
     * display basic information. Since there may be many files that have nothing to do with QDL, these are just skipped.
     *
     * @param currentFile
     * @return
     */
    private WSLibEntry _getWSLibEntry(File currentFile) {

        if (!currentFile.isFile()) {
            return null;
        }
        WSLibEntry wsLibEntry = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(currentFile);
        } catch (FileNotFoundException e) {
            return null; // just skip it if can't open the file
        }
        wsLibEntry = new WSLibEntry();
        if (currentFile.getAbsolutePath().endsWith(DEFAULT_QDL_DUMP_FILE_EXTENSION)) {
            wsLibEntry.ts = new Date(currentFile.lastModified());
            wsLibEntry.lastSaved_ts = wsLibEntry.ts;
            wsLibEntry.fileFormat = "QDL";
            wsLibEntry.length = currentFile.length();
            wsLibEntry.filename = currentFile.getName();
            wsLibEntry.filepath = currentFile.getParent();
            return wsLibEntry;
        }
        try {
            WSInternals wsInternals = (WSInternals) StateUtils.loadObject(fis);
            fis.close();
            wsLibEntry.ts = wsInternals.startTimestamp;
            wsLibEntry.id = wsInternals.id;
            wsLibEntry.description = wsInternals.description;
            wsLibEntry.isCompressed = true;
            wsLibEntry.filename = currentFile.getName();
            wsLibEntry.length = currentFile.length();
            Date lastMod = new Date();
            lastMod.setTime(currentFile.lastModified());
            wsLibEntry.lastSaved_ts = lastMod;
            wsLibEntry.filepath = currentFile.getParent();
            wsLibEntry.fileFormat = "java";
            return wsLibEntry;
        } catch (java.io.InvalidClassException icx) {
            // Means it was indeed serialized, but that blew up (probably due serialization change).
            // Just kick it back.
            wsLibEntry.failed = true;
            wsLibEntry.failMessage = icx.getMessage();
            wsLibEntry.exception = icx;
            wsLibEntry.filename = currentFile.getName();
            wsLibEntry.filepath = currentFile.getParent();

            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return wsLibEntry;
        } catch (Throwable t) {
            // grab bag. This failed usually because it was not gzipped ==> no serialized objects
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        XMLEventReader xer = null;

        boolean gotCompressed = false;
        if (isCompressXML()) {
            xer = XMLUtils.getZippedReader(currentFile);
            gotCompressed = true;
            // user is dictating to use compress.
        } else {
            xer = XMLUtils.getReader(currentFile);
            gotCompressed = false;
        }

        if (xer == null) {
            // reverse these
            if (isCompressXML()) {
                xer = XMLUtils.getReader(currentFile);
                gotCompressed = false;
            } else {
                xer = XMLUtils.getZippedReader(currentFile);
                gotCompressed = true;
            }
        }
        if (xer == null) {
            return null; // probably empty file???
        }

        WSXMLSerializer serializer = new WSXMLSerializer();
        try {
            WorkspaceCommands tempWSC = serializer.fromXML(xer, true);
            xer.close();
            wsLibEntry = getWsLibEntry(currentFile, gotCompressed, tempWSC);
            wsLibEntry.fileFormat = "xml";
            return wsLibEntry;
        } catch (Throwable t) {
            wsLibEntry.failed = true;
            wsLibEntry.failMessage = t.getMessage().replace('\n', ' ');// some messages have embedded line feeds
            wsLibEntry.exception = t;
            wsLibEntry.filename = currentFile.getName();
            wsLibEntry.filepath = currentFile.getParent();

            try {
                xer.close();
            } catch (XMLStreamException e) {
                // fail silently, go to next.
            }

        }
        return wsLibEntry;
    }

    private WSLibEntry getWsLibEntry(File currentFile, boolean gotCompressed, WorkspaceCommands tempWSC) {
        WSLibEntry wsLibEntry;
        wsLibEntry = new WSLibEntry();
        wsLibEntry.id = tempWSC.getWSID();
        wsLibEntry.description = tempWSC.getDescription();
        wsLibEntry.ts = tempWSC.startTimeStamp;
        wsLibEntry.isCompressed = gotCompressed;
        wsLibEntry.filename = currentFile.getName();
        wsLibEntry.length = currentFile.length();
        Date lastMod = new Date();
        lastMod.setTime(currentFile.lastModified());
        wsLibEntry.lastSaved_ts = lastMod;
        wsLibEntry.filepath = currentFile.getParent();
        return wsLibEntry;
    }

    private int _wsSet(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("set ws_variable value");
            sayi("Set the value of the given workspace variable topo the given value.");
            sayi("Remember that strings should be in double quotes and you may also pipe in QDL variables.");
            return RC_NO_OP;
        }
        if (inputLine.getArgCount() == 1) {
            printAllWSVars();
            return RC_CONTINUE;
        }

        if (inputLine.getArgCount() < 3) {
            say("Missing argument. This requires two arguments.");
            return RC_NO_OP;

        }
        String value = inputLine.getArg(3);
        switch (inputLine.getArg(2)) {
            case PRETTY_PRINT:
            case PRETTY_PRINT_SHORT:
                setPrettyPrint(isOnOrTrue(value));
                getInterpreter().setPrettyPrint(isPrettyPrint());
                say("pretty print " + (prettyPrint ? "on" : "off"));
                break;
            case ECHO:
                setEchoModeOn(isOnOrTrue(value));
                getInterpreter().setEchoModeOn(isEchoModeOn());
                say("echo mode " + (echoModeOn ? "on" : "off"));
                break;
            case DEBUG:
                setDebugOn(isOnOrTrue(value));
                say("debug " + (debugOn ? "on" : "off"));
                break;
            case UNICODE_ON:
                State.setPrintUnicode(isOnOrTrue(value));
                say("unicode printing of system constants is now " + (State.isPrintUnicode() ? "on" : "off"));
                break;
            case ANSI_MODE_ON:

/*          Can't actually do this since the system InputStream gets munged and
            the whole JVM shuts down. There is probably a way to do it, but that is
            highly non-obvious, so I'll leave this here now as a later improvement
            if this gets important.
            Another consideration is that there is no way to know if turning on ansi mode
            will crash the JVM (some terminal types cannot use it), so at best being able
            to toggle this is dicey.
                if (isOnOrTrue(value)) {
                    try {
                        QDLTerminal qdlTerminal = new QDLTerminal(null);
                        ISO6429IO iso6429IO = new ISO6429IO(qdlTerminal, true);
                        setIoInterface(iso6429IO);
                        getIoInterface().setBufferingOn(true);
                        ansiModeOn = true;
                    } catch (IOException iox) {
                        say("sorry, could not switch to ansi mode:\"" + iox.getMessage() + "\"");
                    }
                } else {
                    setIoInterface(new BasicIO());
                    ansiModeOn = false;
                }*/
                say("ansi mode is read only and " + (ansiModeOn ? "on" : "off"));
                break;
            case USE_EXTERNAL_EDITOR:
                setUseExternalEditor(isOnOrTrue(value));
                say("use external editor " + (isUseExternalEditor() ? "on" : "off"));
                break;
            case EXTERNAL_EDITOR:
                if (!value.equals(LINE_EDITOR_NAME)) {
                    EditorEntry x = getQdlEditors().get(value);
                    if (x == null) {
                        say("Sorry, but there is no such editor \"" + value + "\" available. Make sure it is configured.");
                        listEditors();
                        break;
                    }

                }
                String oldName = getExternalEditorName();
                setExternalEditorName(value);
                say("external editor was " + (isTrivial(oldName) ? "(null)" : oldName) + " now is '" + getExternalEditorName() + "'");
                break;
            case ENABLE_LIBRARY_SUPPORT:
                getState().setEnableLibrarySupport(isOnOrTrue(value));
                say("library support is now " + (getState().isEnableLibrarySupport() ? "on" : "off"));
                break;
            case ASSERTIONS_ON:
                getState().setAssertionsOn(isOnOrTrue(value));
                say("assertions are now " + (getState().isAssertionsOn() ? "on" : "off"));
                break;
            case RUN_INIT_ON_LOAD:
                runInitOnLoad = isOnOrTrue(value);
                say("run " + DEFAULT_BOOT_FUNCTION_ON_LOAD_NAME + " on loading this workspace is " + (getState().isAssertionsOn() ? "on" : "off"));
                break;
            case LIB_PATH_TAG:
                getState().setLibPath(value);
                say("library path updated");
                break;
            case START_TS:
                try {
                    Long rawDate = Long.parseLong(value);
                    startTimeStamp = new Date();
                    startTimeStamp.setTime(rawDate);
                } catch (NumberFormatException nfx) {
                    try {
                        Iso8601.string2Date(value);
                    } catch (ParseException e) {
                        say("sorry but \"" + value + "\" could not be parsed as a date");
                    }
                }
                say("start time for workspace changed to " + Iso8601.date2String(startTimeStamp));
                break;
            case DESCRIPTION:
                setDescription(value);
                say("description updated");
                break;
            case CURRENT_WORKSPACE_FILE:
                File temp = new File(value);
                if (temp.exists()) {
                    if (!temp.isFile()) {
                        say("sorry, " + temp.getAbsolutePath() + " is not a file.");
                        return RC_NO_OP;
                    }
                } else {
                    say("warning " + temp.getAbsolutePath() + " does not exist yet.");
                }
                currentWorkspace = temp;
                break;
            case WS_ID:
                setWSID(value);
                say("workspace id set to '" + getWSID() + "'");
                break;
            case COMPRESS_XML:
                setCompressXML(isOnOrTrue(value));
                say("xml compression " + (compressXML ? "on" : "off"));
                break;
            case SAVE_DIR:
                saveDir = new File(value);
                if (!saveDir.exists()) {
                    say("warning the directory \"" + saveDir.getAbsolutePath() + "\" does not exist");
                    return RC_NO_OP;
                }
                if (!saveDir.isDirectory()) {
                    say("warning  \"" + saveDir.getAbsolutePath() + "\" is not a directory");
                    return RC_NO_OP;
                }
                say("default save directory is now " + saveDir.getAbsolutePath());
                break;
            case ROOT_DIR:
                rootDir = new File(value);
                if (!rootDir.exists()) {
                    say("warning the directory \"" + rootDir.getAbsolutePath() + "\" does not exist");
                    return RC_NO_OP;
                }
                if (!rootDir.isDirectory()) {
                    say("warning  \"" + rootDir.getAbsolutePath() + "\" is not a directory");
                    return RC_NO_OP;
                }
                say("root directory is now " + rootDir.getAbsolutePath());

                break;
            case AUTOSAVE_ON:
                if (currentWorkspace == null) {
                    say("warning: you have not a set a file for saves. Please set " + CURRENT_WORKSPACE_FILE + " first.");
                } else {
                    setAutosaveOn(isOnOrTrue(value));
                    if (autosaveThread != null) {
                        autosaveThread.interrupt();
                        autosaveThread.setStopThread(true);
                        autosaveThread = null; // old one gets garbage collected, force a new one
                    }
                    if (isAutosaveOn()) {
                        initAutosave();
                    }
                    say("autosave is now " + (isAutosaveOn() ? "on" : "off"));

                }
                break;
            case AUTOSAVE_MESSAGES_ON:
                setAutosaveMessagesOn(isOnOrTrue(value));
                say("autosave messages are now " + (isAutosaveMessagesOn() ? "on" : "off"));
                break;
            case AUTOSAVE_INTERVAL:
                String rawTime = value;
                if (4 <= inputLine.getArgCount()) {
                    rawTime = rawTime + " " + inputLine.getArg(4);
                }
                setAutosaveInterval(ConfigUtil.getValueSecsOrMillis(rawTime));
                say("autosave interval is now " + getAutosaveInterval() + " ms.");
                break;
            default:
                say("unknown ws variable '" + inputLine.getArg(2) + "'");
                break;
        }

        return RC_CONTINUE;

    }

    protected void listEditors() {
        say("Available editors:");
        say(LINE_EDITOR_NAME);
        for (String name : getQdlEditors().listNames()) {
            say(name + (name.equals(getExternalEditorName()) ? " (active)" : ""));
        }
    }

    protected String[] ALL_WS_VARS = new String[]{
            ANSI_MODE_ON,
            ASSERTIONS_ON,
            AUTOSAVE_INTERVAL,
            AUTOSAVE_MESSAGES_ON,
            AUTOSAVE_ON,
            COMPRESS_XML,
            CURRENT_WORKSPACE_FILE,
            DEBUG,
            DESCRIPTION,
            ECHO,
            ENABLE_LIBRARY_SUPPORT,
            EXTERNAL_EDITOR,
            LIB_PATH_TAG,
            PRETTY_PRINT,
            PRETTY_PRINT_SHORT,
            ROOT_DIR,
            RUN_INIT_ON_LOAD,
            SAVE_DIR,
            START_TS,
            UNICODE_ON,
            USE_EXTERNAL_EDITOR,
            WS_ID
    };
    String wsID;

    public String getDescription() {
        return description;
    }

    /**
     * Human readable dscription of this workspace.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    String description;

    public String getWSID() {
        return wsID;
    }

    public void setWSID(String wsID) {
        this.wsID = wsID;
    }


    private int _wsEchoMode(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("echo (on | off) [-pp (on | off)]");
            sayi("Toggle the echo mode so every command is printed if it has output.");
            sayi("-pp = pretty print on or off. Stems should be printed horizontal by default.");
            return RC_NO_OP;
        }
        if (!inputLine.hasArgAt(FIRST_ARG_INDEX) || inputLine.getArg(FIRST_ARG_INDEX).startsWith("-")) {
            if (inputLine.hasArg("-pp")) {
                String pp = inputLine.getNextArgFor("-pp").toLowerCase();
                prettyPrint = pp.equals("true") || pp.equals("on");
                getInterpreter().setPrettyPrint(prettyPrint);
            }
            say("echo mode currently " + (isEchoModeOn() ? "on" : "off") + ", pretty print = " + (isPrettyPrint() ? "on" : "off"));
            return RC_CONTINUE;
        }
        String onOrOff = inputLine.getArg(FIRST_ARG_INDEX);
        if (inputLine.hasArg("-pp")) {
            String pp = inputLine.getNextArgFor("-pp").toLowerCase();
            prettyPrint = pp.equals("true") || pp.equals("on");
        }

        if (onOrOff.toLowerCase().equals("on")) {
            setEchoModeOn(true);
            getInterpreter().setEchoModeOn(true);
            getInterpreter().setPrettyPrint(prettyPrint);
            say("echo mode on, pretty print = " + (prettyPrint ? "on" : "off"));
        } else {
            setEchoModeOn(false);
            getInterpreter().setEchoModeOn(false);
            getInterpreter().setPrettyPrint(prettyPrint);
            say("echo mode off, pretty print = " + (prettyPrint ? "on" : "off"));
        }
        return RC_CONTINUE;
    }

    private int _wsClear(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("clear [" + RELOAD_FLAG + "]");
            sayi("Clear the state *completely*. This includes all virtual file systems and buffers.");
            sayi(RELOAD_FLAG + " = reload the current workspace from the configuration. Nothing current will be saved.");
            return RC_NO_OP;
        }
        if (inputLine.hasArg(RELOAD_FLAG)) {
            boolean clearIt = readline("Are you sure you want to lose all state and revert the workspace back to the initial load? (Y/n)[n]").equals("Y");
            if (clearIt) {
                return RC_RELOAD;
            }
        }
        boolean clearIt = readline("Are you sure you want to clear the workspace state? (Y/n)[n]").equals("Y");
        if (!clearIt) {
            say("WS clear aborted.");
            return RC_NO_OP;
        }
        clearWS();
        say("workspace cleared");
        return RC_CONTINUE;
    }

    private void clearWS() {
        State oldState = state;
        // Must preserve the IOInterface since whatever it is has hold of the system input stream
        // and that cannot be really transferred between instances -- attempts to do so will
        // result in the stream malfunctioning or simply throwing Exceptions on every use.
        // The most common sign is an almost silent JVM exit.
        IOInterface currentIOI = getIoInterface();
        // Get rid of everything.
        state = null;
        MAliases.setmInstances(null); // zero this out or we have bogus entries.
        state = getState();
        state.setIoInterface(currentIOI);
        setIoInterface(currentIOI);
        state.createSystemConstants();
        state.setSystemInfo(oldState.getSystemInfo());
        commandHistory = new ArrayList<>();
        interpreter = new QDLInterpreter(state);
    }

    String JAVA_FLAG = SAVE_AS_JAVA_FLAG;
    String COMPRESS_FLAG = "-compress";
    String SHOW_FLAG = "-show";
    String QDL_DUMP_FLAG = "-qdl";
    public static String SILENT_SAVE_FLAG = "-silent";

    /*
    Has to be public so save thread can access it.
     */
    protected int _wsSave(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("save [filename] [" + JAVA_FLAG + "] | [" + SHOW_FLAG + "] | [" + COMPRESS_FLAG + " on|off] [" +
                    KEEP_WSF + "] | [" + SILENT_SAVE_FLAG + "]");
            sayi("Saves the current state (variables, loaded functions but not pending buffers of VFS) to a file.");
            sayi("If you have already loaded (or saved) a file, that is remembered in the " + KEEP_WSF + " variable");
            sayi("and you do not need to specify it henceforth.");
            sayi("The file should be either a relative path (resolved against the default save location) or an absolute path.");
            sayi(QDL_DUMP_FLAG + " = dump the contents of the workspace to a QDL file. You can just reload it using " + SystemEvaluator.LOAD_COMMAND);
            sayi(JAVA_FLAG + " = save using Java serialization format. The default is XML.");
            sayi(SHOW_FLAG + " = (XML format only) dump the (uncompressed) result to the console instead. No file is needed.");
            sayi(COMPRESS_FLAG + " = compress the output. The resulting file will be a binary file. This overrides the configuration file setting.");
            sayi(KEEP_WSF + " = keep the current " + CURRENT_WORKSPACE_FILE + " rather than automatically updating it");
            sayi(SILENT_SAVE_FLAG + " = print no messages when saving.");
            sayi("Note that a dump does not save any of the current workspace state, just the variables, functions and modules.");
            sayi("See the corresponding load command to recover it. It will print error messages, however.");
            say("See also: autosave_on (ws variable)");
            return RC_NO_OP;
        }

        boolean showFile = inputLine.hasArg(SHOW_FLAG);
        boolean doJava = inputLine.hasArg(SAVE_AS_JAVA_FLAG);
        boolean keepCurrentWS = inputLine.hasArg(KEEP_WSF);
        boolean silentMode = inputLine.hasArg(SILENT_SAVE_FLAG);
        boolean qdlDump = inputLine.hasArg(QDL_DUMP_FLAG);
        boolean compressionOn = true;

        if (inputLine.hasArg(COMPRESS_FLAG)) {
            compressionOn = inputLine.getNextArgFor(COMPRESS_FLAG).equalsIgnoreCase("on");
        }

        if (qdlDump) {
            doJava = false; // QDL has preference, so if the user provides both, use QDL
        }
        inputLine.removeSwitch(SHOW_FLAG);
        inputLine.removeSwitch(COMPRESS_FLAG);
        inputLine.removeSwitchAndValue(SAVE_AS_JAVA_FLAG);
        inputLine.removeSwitch(KEEP_WSF);
        inputLine.removeSwitch(SILENT_SAVE_FLAG);
        inputLine.removeSwitch(QDL_DUMP_FLAG);


        // Remove switches before looking at positional arguments.

        File target = null;
        String fName = null;


        if (showFile) {
            try {
                long uncompressedXMLSize = _xmlSave(target, compressionOn, showFile);
                say("size is " + uncompressedXMLSize);
                return RC_CONTINUE;
            } catch (Throwable throwable) {
                say("warning. could not show file \"" + throwable.getMessage() + "\"");
                return RC_NO_OP;
            }

        }
        try {

            if (inputLine.hasArgAt(FIRST_ARG_INDEX)) {
                fName = inputLine.getArg(FIRST_ARG_INDEX);
                try {
                    int index = Integer.parseInt(fName);
                    if (bufferManager.hasBR(index)) {
                        say("warning: There is a buffer \"" + index + "\". If you want to save that, then use the buffer save command.");
                        if (!readline("save as workspace? (y/n)").equals("y")) {
                            say("ok. aborting save.");
                            return RC_NO_OP;
                        }
                    }

                } catch (NumberFormatException nfx) {
                    // rock on
                }
                // Figure out extension.
                if (!fName.contains(".")) {
                    if (qdlDump) {
                        fName = fName + DEFAULT_QDL_DUMP_FILE_EXTENSION;
                    } else {
                        if (doJava) {
                            fName = fName + DEFAULT_JAVA_SERIALIZATION_FILE_EXTENSION;
                        } else {
                            fName = fName + DEFAULT_XML_SAVE_FILE_EXTENSION;
                        }
                    }
                }
                target = new File(fName);
            } else {
                if (currentWorkspace == null) {
                    say("sorry, no default file set.");
                    return RC_NO_OP;
                } else {
                    target = currentWorkspace;
                }
            }
            if (!target.isAbsolute()) {
                if (saveDir == null) {
                    target = new File(rootDir, fName);
                } else {
                    target = new File(saveDir, fName);
                }
            }
            if (target.exists() && target.isDirectory()) {
                say("sorry, but " + target.getAbsolutePath() + " is not a directory.");
                return RC_NO_OP;
            }


            if (qdlDump || target.getAbsolutePath().endsWith(QDLVersion.DEFAULT_FILE_EXTENSION)) {
                _doQDLDump(target);
                say("dumped " + target.length() + " bytes to \"" + target.getAbsolutePath() + "\"");
                return RC_CONTINUE;
            }
            long uncompressedXMLSize = -1L;
            if (doJava) {
                _realSave(target);
            } else {
                uncompressedXMLSize = _xmlSave(target, compressionOn, showFile);
            }
            String head = 0 <= uncompressedXMLSize ? (", uncompressed size = " + uncompressedXMLSize + " ") : "";
            if (!silentMode) {
                say("Saved " + target.length() + " bytes to " + target.getCanonicalPath() + " on " + (new Date()) + head);
            }
            if (!keepCurrentWS) {
                currentWorkspace = target;
            }

        } catch (Throwable t) {
            logger.error("could not save workspace.", t);
            say("could not save the workspace:" + t.getMessage());
        }
        return RC_NO_OP;
    }

    public static final String DEFAULT_QDL_DUMP_FILE_EXTENSION = ".qdl";
    public static final String DEFAULT_XML_SAVE_FILE_EXTENSION = ".ws";
    public static final String ALTERNATE_XML_SAVE_FILE_EXTENSION = ".zml"; // for reads
    public static final String DEFAULT_JAVA_SERIALIZATION_FILE_EXTENSION = ".wsj";
    public static final String ALTERNATE_JAVA_SERIALIZATION_FILE_EXTENSION = ".ser"; // for reads

    private void _doQDLDump(File target) throws Throwable {
        FileWriter fileWriter = new FileWriter(target);
        fileWriter.write("// QDL workspace " + (isTrivial(getWSID()) ? "" : getWSID()) + " dump, saved on " + (new Date()) + "\n");
        fileWriter.write("\n/* ** module definitions ** */\n");

        for (URI key : getState().getMTemplates().keySet()) {
            String output = inputFormModule(key, state);
            if (output.startsWith(JAVA_CLASS_MARKER)) {
                output = SystemEvaluator.MODULE_LOAD + "('" + output.substring(JAVA_CLASS_MARKER.length())
                        + "' ,'" + SystemEvaluator.MODULE_TYPE_JAVA + "');";
            }
            fileWriter.write(output + "\n");

        }
        // now do the imports
        fileWriter.write("\n/* ** module imports ** */\n");

        for (URI key : getState().getMTemplates().keySet()) {
            List<String> aliases = getState().getMAliases().getAlias(key);
            for (String alias : aliases) {
                String output = SystemEvaluator.MODULE_IMPORT + "('" + key + "','" + alias + "');";
                fileWriter.write(output + "\n");
            }
        }

        /**
         * Have to be careful in listing only what is in the actual state, not
         * stuff in modules too since that is saved elsewhere and both bloats
         * the output and can make for NS conflicts on reload.
         */
        fileWriter.write("\n/* ** user defined variables ** */\n");
        for (String varName : state.getSymbolStack().listVariables()) {
            String output = inputFormVar(varName, 2, state);
            fileWriter.write(varName + " := " + output + ";\n");
        }

        fileWriter.write("\n/* ** user defined functions ** */\n");
        for (String fWithArg : state.getFTStack().listFunctions(null)) {
            // This gives back functions of the form fName(argCount). Since the
            // logic involves jumping through a lot of hoops (e.g. getting them from
            // modules, it is vastly easier to simply parse them to get the source code.
            // The alternative is a slog through every component with a function.

            int lpIndex = fWithArg.indexOf("(");
            int rpIndex = fWithArg.indexOf(")");
            String fName = fWithArg.substring(0, lpIndex);
            String rawCount = fWithArg.substring(lpIndex + 1, rpIndex);

            String output = inputForm(fName, Integer.parseInt(rawCount), state);
            if (!output.startsWith(JAVA_CLASS_MARKER)) {
                // Do not write java functions, since this makes no sense -- the must live in
                // a module at this point.
                fileWriter.write(output + "\n");
            }
        }
        fileWriter.flush();
        fileWriter.close();
    }

    private long _xmlSave(File f, boolean compressSerialization, boolean showIt) throws Throwable {

        Writer w = new StringWriter();

        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(w);
        long uncompressedSize = -1L;

        toXML(xsw);
        if (showIt) {
            String xml = XMLUtils.prettyPrint(w.toString());
            say(xml);
            return xml.length();
        } else {

            if (compressSerialization || isCompressXML()) {
                String xml2 = XMLUtils.prettyPrint(w.toString()); // We do this because whitespace matters. This controls it.
                uncompressedSize = xml2.length();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);
                gzipOutputStream.write(xml2.getBytes("UTF-8"));
                gzipOutputStream.flush();
                gzipOutputStream.close();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();

            } else {
                FileWriter fw = new FileWriter(f);
                fw.write(XMLUtils.prettyPrint(w.toString()));
                fw.flush();
                fw.close();
                uncompressedSize = f.length();
            }
        }
        w.flush();
        w.close();
        return uncompressedSize;
    }

    private void _realSave(File target) throws IOException {
        FileOutputStream fos = new FileOutputStream(target);
        logger.info("saving workspace '" + target.getAbsolutePath() + "'");
        WSInternals wsInternals = new WSInternals();
        wsInternals.defaultState = defaultState;
        wsInternals.currentPID = currentPID;
        wsInternals.activeState = state;
        wsInternals.siEntries = siEntries;
        wsInternals.startTimestamp = startTimeStamp;
        wsInternals.id = wsID;
        wsInternals.description = description;
        wsInternals.echoOn = echoModeOn;
        wsInternals.prettyPrint = prettyPrint;
        wsInternals.debugOn = debugOn;
        if (saveDir != null) {
            wsInternals.saveDir = saveDir.getAbsolutePath();
        }
        StateUtils.saveObject(wsInternals, fos);
    }

    private boolean _xmlLoad(File f) {
        // The file may be in XML format. If not, then it is assumed to be
        // zipped and binary.
        // First attempt is to assume no compression
        XMLEventReader xer = null;
        QDLWorkspace qdlWorkspace = getWorkspace(); // for later
        if (isCompressXML()) {
            xer = XMLUtils.getZippedReader(f);
            // user is dictating to use compress.
        } else {
            xer = XMLUtils.getReader(f);
        }
        if (xer != null) {
            try {
                fromXML(xer);
                xer.close();
                currentWorkspace = f;
                getState().setWorkspaceCommands(this);
                if (runInitOnLoad && state.getFTStack().containsKey(new FKey(DEFAULT_BOOT_FUNCTION_ON_LOAD_NAME, 0))) {
                    String runnit = DEFAULT_BOOT_FUNCTION_ON_LOAD_NAME + "();";
                    getInterpreter().execute(runnit);
                }
                return true;
            } catch (Throwable t) {
                // First attempt can fail for, e.g., the default is compression but the file is not compressed.
                // So this might be benign.
                // A derserialization exception though means the structure of the file was
                // bad (e.g. missing java classes)
                if (t instanceof DeserializationException) {
                    throw (DeserializationException) t;
                }
            }
        }

        if (xer == null) {
            // so that didn't work, most likely because the file is or is not compressed,
            // try the other way
            if (isCompressXML()) {
                // user is dictating to use compress.
                xer = XMLUtils.getReader(f);
            } else {
                xer = XMLUtils.getZippedReader(f);
            }

        }
        if (xer == null) {
            //say("sorry, cannot get the file \"" + f.getAbsolutePath() + "\"");
            return false;
        }

        try {
            fromXML(xer);
            xer.close();
            currentWorkspace = f;
            return true;
        } catch (XMLStreamException e) {
            say("error reading XML at line " + e.getLocation().getLineNumber() + ", col " + e.getLocation().getColumnNumber()
                    + ":\"" + e.getMessage() + "\"");
        } catch (Throwable t) {
            say("error reading XML file: " + t.getMessage());
        }
        return false;
    }

    /*
    Does the actual work of loading a serialized file once the logic for what to do has been done.
     */
    private boolean _javaLoad(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            WSInternals wsInternals = (WSInternals) StateUtils.loadObject(fis);

            //State newState = StateUtils.load(fis);
            State newState = wsInternals.activeState;
            currentPID = wsInternals.currentPID;
            defaultState = wsInternals.defaultState;
            siEntries = wsInternals.siEntries;
            startTimeStamp = wsInternals.startTimestamp;
            wsID = wsInternals.id;
            description = wsInternals.description;
            echoModeOn = wsInternals.echoOn;
            prettyPrint = wsInternals.prettyPrint;
            debugOn = wsInternals.debugOn;
            if (wsInternals.saveDir != null) {
                saveDir = new File(wsInternals.saveDir);
            }
            /*
            Now set the stuff that cannot be serialized.
             */
            newState.injectTransientFields(getState());
            //defaultState.injectTransientFields(getState());
            defaultState = newState;
            for (Integer key : siEntries.keySet()) {
                SIEntry sie = siEntries.get(key);
                sie.state.injectTransientFields(getState());
            }
            interpreter = new QDLInterpreter(env, newState);
            interpreter.setEchoModeOn(isEchoModeOn());
            interpreter.setDebugOn(isDebugOn());
            state = newState;
            currentWorkspace = f;
            if (runInitOnLoad && state.getFTStack().containsKey(new FKey(DEFAULT_BOOT_FUNCTION_ON_LOAD_NAME, 0))) {
                String runnit = DEFAULT_BOOT_FUNCTION_ON_LOAD_NAME + "();";
                getInterpreter().execute(runnit);
            }
            return true;
        } catch (Throwable t) {
            if (DebugUtil.isEnabled()) {
                t.printStackTrace();
            }
        }
        return false;
    }

    String DEFAULT_BOOT_FUNCTION_ON_LOAD_NAME = "__init";
    boolean runInitOnLoad = true;

    File currentWorkspace;
    public final String RELOAD_FLAG = SWITCH + "reload";

    private int _wsLoad(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("load [filename] [" + KEEP_WSF + "] ");

            sayi("Loads a saved workspace. If the name is relative, it will be resolved against " +
                    "the default location or it may be an absolute path.");
            sayi(KEEP_WSF + " = keep the current " + CURRENT_WORKSPACE_FILE + " rather than automatically updating it");
            sayi(QDL_DUMP_FLAG + " = the format of the file is QDL. This loads it into the current workspace.");
            sayi(JAVA_FLAG + " = the format of the file is serialized java. default is XML");
            sayi("If there is no file given, the current workspace is used.");
            sayi("If you dumped a workspace to QDL, you may simply load it as any other script");
            sayi("e.g.");
            say(")load my_ws -qdl");
            sayi("would load a file named my_ws.qdl ");
            sayi("See also: save, setting the current workspace.");
            return RC_NO_OP;
        }

        File target = null;
        String fName = null;
        boolean keepCurrentWS = inputLine.hasArg(KEEP_WSF);
        inputLine.removeSwitch(KEEP_WSF);
        boolean isQDLDump = inputLine.hasArg(QDL_DUMP_FLAG);
        inputLine.removeSwitch(QDL_DUMP_FLAG);
        boolean isJava = inputLine.hasArg(JAVA_FLAG) && !isQDLDump; // QDL has right of way
        inputLine.removeSwitch(JAVA_FLAG);

        if (inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            fName = inputLine.getArg(FIRST_ARG_INDEX);
        } else {
            if (currentWorkspace == null) {
                say("sorry, no default file set.");
                return RC_NO_OP;
            } else {
                target = currentWorkspace;
            }
        }

        if (target == null && fName.contains(".")) {
            // If there is an extension, we are done.
            target = new File(fName);
        }
        boolean loadOK = false;
        if (target == null) {
            target = new File(fName);

            // At this point, the fName has no extension. Check for standard extensions
            File parentDir = null; // null parent is ignored in the File constructor below
            if (!target.isAbsolute()) {
                if (saveDir == null) {
                    parentDir = rootDir;
                } else {
                    parentDir = saveDir;
                }
            }
            if (isQDLDump) {
                target = new File(parentDir, fName); // try it raw
                if (!target.exists() || !target.isFile()) {
                    target = new File(parentDir, fName + DEFAULT_QDL_DUMP_FILE_EXTENSION); // only  possible extension
                }

            } else {
                if (isJava) {
                    target = new File(parentDir, fName + DEFAULT_JAVA_SERIALIZATION_FILE_EXTENSION);
            /*        if (!target.exists()) {
                        target = new File(fName + ALTERNATE_JAVA_SERIALIZATION_FILE_EXTENSION);
                    }*/

                } else {
                    target = new File(parentDir, fName + DEFAULT_XML_SAVE_FILE_EXTENSION);
                    if (!target.exists()) {
                        target = new File(parentDir, fName + ALTERNATE_XML_SAVE_FILE_EXTENSION);
                    }

                }
            }

        } else {
            if (!target.isAbsolute()) {

                if (saveDir == null) {
                    target = new File(rootDir, fName);
                } else {
                    target = new File(saveDir, fName);
                }
                if (!target.isFile()) {
                    say("sorry, but " + target.getAbsolutePath() + " is not a file.");
                    return RC_NO_OP;
                }
            }

        }         //     file_write('/tmp/data.csv',  to_cvs([['x','y']]~y.))

        if (target == null) {
            say("sorry, could not determine file for \"" + fName + "\"");
            return RC_NO_OP;
        }
        if (!target.exists()) {
            say("sorry, the target file \"" + target.getAbsolutePath() + "\" does not exist");
            return RC_NO_OP;
        }
        if (!target.isFile()) {
            say("sorry, the target  \"" + target.getAbsolutePath() + "\" is not a file");
            return RC_NO_OP;
        }
        if (!target.canRead()) {
            say("sorry, cannot read  \"" + target.getAbsolutePath() + "\"");
            return RC_NO_OP;
        }
        if (isQDLDump || target.getAbsolutePath().endsWith(QDLVersion.DEFAULT_FILE_EXTENSION)) {
            // Other load methods clear the workspace first. We do that here:
            // User experience is that if it was in echo mode and pretty print before the car
            // it should remain so, since QDL does not save WS state.
            boolean echo = isEchoModeOn();
            boolean pp = isPrettyPrint();
            boolean debugOn = isDebugOn();
            File saveDir = this.saveDir;
            clearWS();
            String command = SystemEvaluator.LOAD_COMMAND + "('" + target.getAbsolutePath() + "');";
            try {
                // Don't barf out everything to the command line when it loads.
                getInterpreter().setPrettyPrint(false);
                getInterpreter().setEchoModeOn(false);
                getInterpreter().execute(command);
                setPrettyPrint(pp);
                setEchoModeOn(echo);
                setDebugOn(debugOn);
                getInterpreter().setEchoModeOn(echo);
                getInterpreter().setPrettyPrint(pp);
                getInterpreter().setDebugOn(debugOn);
                this.saveDir = saveDir;

                say(target.getAbsolutePath() + " loaded (" + target.length() + " bytes)");
                return RC_CONTINUE;
            } catch (Throwable throwable) {
                if (DebugUtil.isEnabled()) {
                    throwable.printStackTrace();
                }
                say("sorry, could not load QDL \"" + target.getAbsolutePath() + "\": " + throwable.getMessage());
                return RC_NO_OP;
            }
        }
        loadOK = _javaLoad(target);
        if (!loadOK) {
            try {
                loadOK = _xmlLoad(target);
            } catch (DeserializationException deserializationException) {
                say("Could not deserialize workspace: " + deserializationException.getMessage());
                return RC_NO_OP;
            }
        }
        if (loadOK) {
            say(target.getAbsolutePath() + " loaded " + target.length() + " bytes. Last saved on " +
                    Iso8601.date2String(rootDir.lastModified()));

            if (!isTrivial(getWSID())) {
                say(getWSID() + " loaded.");
            }
            if (!isTrivial(getDescription())) {
                say(getDescription());
            }
            if (!keepCurrentWS) {
                currentWorkspace = target;
            }
        } else {
            say("Could not load workspace for file " + target.getAbsolutePath());
        }
        return RC_CONTINUE;
    }

    QDLInterpreter interpreter = null;

    public QDLInterpreter getInterpreter() {
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
        DebugUtil.setIsEnabled(debugOn);
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
    public void say(String x) {
        getIoInterface().println(defaultIndent + x);
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
            SymbolStack stack = new SymbolStack();
            state = new State(MAliases.newMInstances(),
                    stack,
                    new OpEvaluator(),
                    MetaEvaluator.getInstance(),
                    new FStack(),
                    new MTemplates(),
                    logger,
                    false,
                    false,
                    isAssertionsOn()
            );// workspace is never in server mode, nor restricted IO
            state.setPID(0);
            state.setWorkspaceCommands(this);
        }
        return state;

    }

    public void runMacro(List<String> commands) {
        try {
            getWorkspace().runMacro(commands);
        } catch (Throwable t) {
            say("could not execute macro");
        }
    }

    protected void setupJavaModule(State state, QDLLoader loader, boolean importASAP) {
        for (Module m : loader.load()) {
            state.addModule(m); // done!  Add it to the templates
            if (importASAP) {
                // Add it to the imported modules, i.e., create an instance.
                state.getMAliases().addImport(m.getNamespace(), m.getAlias());
                state.getMInstances().put(m.getAlias(), m);
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
    public static final String CLA_LONG_FORMAT_ON = "-l";
    public static final String CLA_NO_BANNER = "-noBanner";
    public static final String CLA_DEBUG_ON = "-debug";
    public static final String CLA_RUN_SCRIPT_ON = "-run";
    public static final String CLA_SCRIPT_PATH = "-script_path";
    public static final String CLA_LIB_PATH = "-lib_path";

    protected File resolveAgainstRoot(String file) {
        File f = new File(file);
        if (!f.isAbsolute()) {
            // then we need to resolve it against the root.
            return new File(rootDir, file);
        }
        return f;
    }

    File envFile; // this is the name of the file holding the environment variables
    // turns on some low-level tracing of this class with DebugUtil when it initializes. Not for public use.
    String TRACE_ARG = "-trace";

    public boolean isCompressXML() {
        return compressXML;
    }

    public void setCompressXML(boolean compressXML) {
        this.compressXML = compressXML;
    }

    boolean compressXML = true;


    public Editors getQdlEditors() {
        return qdlEditors;
    }

    public void setQdlEditors(Editors qdlEditors) {
        this.qdlEditors = qdlEditors;
    }

    protected void fromConfigFile(InputLine inputLine) throws Throwable {
        String cfgname = inputLine.hasArg(CONFIG_NAME_FLAG) ? inputLine.getNextArgFor(CONFIG_NAME_FLAG) : "default";
//      Old style -- single inheritance
        ConfigurationNode node = ConfigUtil.findConfiguration(
                inputLine.getNextArgFor(CONFIG_FILE_FLAG),
                cfgname, CONFIG_TAG_NAME);

        // New style -- multi-inheritance.
   //     ConfigurationNode node = ConfigUtil.findMultiNode(inputLine.getNextArgFor(CONFIG_FILE_FLAG), cfgname, CONFIG_TAG_NAME );
        QDLConfigurationLoader loader = new QDLConfigurationLoader(inputLine.getNextArgFor(CONFIG_FILE_FLAG), node);

        QDLEnvironment qe = loader.load();
        // The state probably exists at this point if the user had to set the terminal type.
        // Make sure the logger ends up in the actual state.
        // The logger is created in the loader, so it happens automatically if there is a logging block in the config.
        if (state != null) {
            state.setLogger(qe.getMyLogger());
        }
        if (inputLine.hasArg("-home_dir")) {
            // The user might set the home directory here.
            // This is overrides configuration file.
            rootDir = new File(inputLine.getNextArgFor("-home_dir"));
        }
        compressXML = qe.isCompressionOn();
        // Setting this flag at the command line will turn on lower level debugging.
        // The actual option in the configuration file turns on logging debug (so info and trace are enabled).
        if (inputLine.hasArg(TRACE_ARG)) {
            say("trace enabled");
            setDebugOn(true);
            DebugUtil.setIsEnabled(true);
            DebugUtil.setDebugLevel(DebugConstants.DEBUG_LEVEL_TRACE);

        }
        MetaDebugUtil du = new MetaDebugUtil();
        du.setDebugLevel(qe.getDebugLevel());
        state.setDebugUtil(du);

        // Next is for logging, which is not the same as debug.
        if (qe.isDebugOn()) {
            setDebugOn(true);
        }
        if (rootDir != null) {
            // This is where we let the command line override the configuration.
            qe.setWsHomeDir(rootDir.getAbsolutePath());
            qe.getMyLogger().info("Overriding the root directory in the configuration with the argument from the command line.");
        }
        setEchoModeOn(qe.isEchoModeOn());
        setPrettyPrint(qe.isPrettyPrint());
        isRunScript = inputLine.hasArg(CLA_RUN_SCRIPT_ON);
        state.setAssertionsOn(qe.isAssertionsOn());
        assertionsOn = qe.isAssertionsOn();
        if (isRunScript) {
            runScriptPath = inputLine.getNextArgFor(CLA_RUN_SCRIPT_ON);

        }
        boolean isVerbose = qe.isWSVerboseOn();
        showBanner = qe.isShowBanner();
        logger = qe.getMyLogger();

        if (qe.getWSHomeDir().isEmpty() && rootDir == null) {
            // So no home directory was set on the command line either. Use the invocation directory
            rootDir = new File(System.getProperty("user.dir"));
            qe.setWsHomeDir(System.getProperty("user.dir"));
        } else {
            rootDir = new File(qe.getWSHomeDir());
        }
        File testSaveDir;
        if (qe.getSaveDir() != null) {
            testSaveDir = new File(qe.getSaveDir());
        } else {
            testSaveDir = new File(rootDir, "var/ws");
        }
        if (testSaveDir.exists() && testSaveDir.isDirectory()) {
            saveDir = testSaveDir;
        }
        State state = getState(); // This sets it for the class it will be  put in the interpreter below.
        state.createSystemConstants();
        state.createSystemInfo(qe);
        state.getOpEvaluator().setNumericDigits(qe.getNumericDigits());
        bufferManager.state = getState();

        env = new XProperties();
        String loadEnv = null;
        if (inputLine.hasArg("-env")) {
            // overrides config file. Used by scripts e.g.
            loadEnv = inputLine.getNextArgFor("-env");
        } else {
            loadEnv = qe.getWSEnv();
        }
        if (loadEnv != null && !loadEnv.isEmpty()) {
            envFile = resolveAgainstRoot(loadEnv);
            if (envFile.exists()) {
                if (envFile.isFile()) {
                    if (envFile.canRead()) {
                        env.load(envFile);
                    } else {
                        warn("The specified environment file " + loadEnv + " is not readable!");

                    }
                } else {
                    warn("The specified environment file " + loadEnv + " is not a file!");

                }
            } else {
                warn("The specified environment file " + loadEnv + " does not exist");

            }
        }
        if (testSaveDir == null) {
            env.put("save_dir", "(empty)");

        } else {
            env.put("save_dir", testSaveDir.getCanonicalPath());

        }
        if (!isRunScript()) {
            splashScreen();
        }
        QDLConfigurationLoaderUtils.setupVFS(qe, getState());

        setEchoModeOn(qe.isEchoModeOn());
        setPrettyPrint(qe.isPrettyPrint());
        String[] foundModules = setupModules(qe, getState());
        // Just so the user can see it in the properties after load.
        if (foundModules[JAVA_MODULE_INDEX] != null && !foundModules[JAVA_MODULE_INDEX].isEmpty()) {
            if (showBanner && !isRunScript && isVerbose) {
                say("loaded java modules:");
                StringTokenizer t = new StringTokenizer(foundModules[JAVA_MODULE_INDEX], ",");
                while (t.hasMoreTokens()) {
                    sayi(t.nextToken().trim());
                }
            }
            env.put("java_modules", foundModules[JAVA_MODULE_INDEX]);
        }
        if (foundModules[QDL_MODULE_INDEX] != null && !foundModules[QDL_MODULE_INDEX].isEmpty()) {
            if (showBanner && !isRunScript && isVerbose) {
                say("loaded QDL modules:");
                StringTokenizer t = new StringTokenizer(foundModules[QDL_MODULE_INDEX], ",");
                while (t.hasMoreTokens()) {
                    sayi(t.nextToken().trim());
                }
            }

            env.put("qdl_modules", foundModules[QDL_MODULE_INDEX]);
        }
        if (foundModules[MODULE_FAILURES_INDEX] != null && !foundModules[MODULE_FAILURES_INDEX].isEmpty()) {
            if (!isRunScript && isVerbose) {
                say("failed to load modules:");
                StringTokenizer t = new StringTokenizer(foundModules[MODULE_FAILURES_INDEX], ",");
                while (t.hasMoreTokens()) {
                    sayi(t.nextToken().trim());
                }
            }
            say("Check the log " + getLogger().getFileName() + " for more information");
        }
        String bf = QDLConfigurationLoaderUtils.runBootScript(qe, getState());
        if (bf != null) {
            if (isVerbose) {
                say("loaded boot script \"" + bf + "\"");
            }
            env.put("boot_script", bf);
        }
        interpreter = new QDLInterpreter(env, getState());
        interpreter.setEchoModeOn(qe.isEchoModeOn());
        interpreter.setPrettyPrint(qe.isPrettyPrint());
        getState().setScriptPaths(qe.getScriptPath());
        getState().setEnableLibrarySupport(qe.isEnableLibrarySupport());
        getState().setLibPath(qe.getLibPath());
        getState().setModulePaths(qe.getModulePath());
        defaultInterpreter = interpreter;
        getState().setEnableLibrarySupport(qe.isEnableLibrarySupport());
        defaultState = state;
        runScript(inputLine); // run any script if that mode is enabled.
        setAutosaveOn(qe.isAutosaveOn());
        setAutosaveMessagesOn(qe.isAutosaveMessagesOn());
        setAutosaveInterval(qe.getAutosaveInterval());
        setExternalEditorName(qe.getExternalEditorPath());
        setUseExternalEditor(qe.isUseExternalEditor());
        setQdlEditors(qe.getQdlEditors());
        initAutosave();
    }

    AutosaveThread autosaveThread;

    protected void initAutosave() {
        if (getState().isServerMode() || getState().isRestrictedIO()) {
            return; // absolutely refuse to turn this feature on in server or restrict IO mode.
        }
        if (isAutosaveOn()) {
            if (autosaveThread == null) {
                autosaveThread = new AutosaveThread(this);
                autosaveThread.start();
            }
        }
    }

    private void testXMLWriter(boolean doFile, String filename) throws Throwable {
        Writer w = null;
        if (doFile) {
            if (isTrivial(filename)) {
                filename = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/ws-test.xml";
            }
            File file = new File(filename);
            w = new FileWriter(file);
        } else {

            w = new StringWriter();
        }
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(w);
        toXML(xsw);
        if (doFile) {
            System.out.println("wrote file " + filename);
        } else {
            System.out.println(XMLUtils.prettyPrint(w.toString()));
        }
        xsw.flush();
        xsw.close();
    }

    Date startTimeStamp = new Date(); // default is now

    /**
     * Bootstraps the whole thing.
     *
     * @param inputLine
     */
    public void init(InputLine inputLine) throws Throwable {
        if (getIoInterface() != null) {
            getIoInterface().setBufferingOn(true);
        }
        // Set up the help.
        InputStream helpStream = getClass().getResourceAsStream("/func_help.xml");
        if (helpStream == null) {
            say("No help available. Could not load help file.");
        } else {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(helpStream);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("entry");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getAttribute("id");
                    Node x = eElement.getElementsByTagName("body")
                            .item(0);
                    Node child = x.getFirstChild().getNextSibling();
                    CharacterData cd = (CharacterData) child;
                    if (cd != null && cd.getTextContent() != null) {
                        onlineHelp.put(name, cd.getTextContent());
                    }
                }
            }

        }
        if (inputLine.hasArg(CONFIG_FILE_FLAG)) {
            fromConfigFile(inputLine);
            return;
        }
        // Old input line  Jan. 2020
        // -ext "edu.uiuc.ncsa.qdl.extensions.QDLLoaderImpl" -qdlroot /home/ncsa/dev/qdl -env etc/qdl.properties -log log/qdl.log -v
        fromCommandLine(inputLine);

    }

    public boolean isRunScript() {
        return isRunScript;
    }

    boolean isRunScript = false;
    String runScriptPath = null;
    Editors qdlEditors;

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
        state.createSystemConstants();
        state.createSystemInfo(null);
        bufferManager.state = getState();  // make any file operations later will succeed.

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
        if (inputLine.hasArg(TRACE_ARG)) {
            say("trace enabled");
            setDebugOn(true);
            DebugUtil.setIsEnabled(true);
            DebugUtil.setDebugLevel(DebugConstants.DEBUG_LEVEL_TRACE);

        }
        interpreter = new QDLInterpreter(env, getState());
        interpreter.setEchoModeOn(true);
        if (inputLine.hasArg(CLA_BOOT_SCRIPT)) {
            String bootFile = inputLine.getNextArgFor(CLA_BOOT_SCRIPT);
            try {
                String bootScript = QDLFileUtil.readFileAsString(bootFile);
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
        if (inputLine.hasArg(CLA_SCRIPT_PATH)) {
            getState().setScriptPaths(inputLine.getNextArgFor(CLA_SCRIPT_PATH));
        }
        if (inputLine.hasArg(CLA_LIB_PATH)) {
            getState().setLibPath(inputLine.getNextArgFor(CLA_LIB_PATH));

        }
        runScript(inputLine); // If there is a script, run it.
    }

    /**
     * Runs the script from the command line if the -run argument is passed.
     * Contract is that there is the argument is of the form
     * <pre>-run path_to_script x y z ... </pre>
     * path_to_script is the name of QDL file. Below it is referred to as {@link #runScriptPath}
     * and x,y,z,... are passed to the script.
     * <br/><br/>
     * Note especially that {@link #runScriptPath} is set as state for the workspace, but normally not settable by
     * the user.
     *
     * @param inputLine
     */
    private void runScript(InputLine inputLine) {
        if (isRunScript) {

            ArrayList<String> argList = new ArrayList<>();

            boolean addArg = false;
            for (int i = 0; i < inputLine.size(); i++) {
                if (addArg) {
                    argList.add(inputLine.getArg(i));
                }
                if (inputLine.getArg(i).equals(CLA_RUN_SCRIPT_ON)) {
                    i++;
                    addArg = true;
                }

            }
            String[] args = argList.toArray(new String[0]);
            getState().setScriptArgs(args);
            try {
                List<String> lines = QDLFileUtil.readFileAsLines(runScriptPath);
                StringBuffer stringBuffer = new StringBuffer();
                for (String line : lines) {
                    if (!line.matches(VFSEntry.SHEBANG_REGEX)) {
                        stringBuffer.append(line + "\n");
                    }
                }
                //String runScript = QDLFileUtil.readFileAsString(runScriptPath);
                String runScript = stringBuffer.toString();
                if (runScript != null && !runScript.isEmpty()) {
                    interpreter.execute(runScript);
                    System.exit(0); // make sure to use this so external programs (like shell scripts) know all is ok
                }
            } catch (Throwable t) {
                if (t instanceof ReturnException) {
                    // script cammed return(X), so return the agument.
                    ReturnException rx = (ReturnException) t;
                    if (rx.resultType != Constant.NULL_TYPE) {
                        getIoInterface().println(rx.result);
                        getIoInterface().flush();
                    }
                    System.exit(0); // Best we can do. Java does not allow for returned values.
                }
                getState().getLogger().error(t);
                say("Error executing script '" + runScriptPath + "'" + (t.getMessage() == null ? "." : ":" + t.getMessage()));
                System.exit(1); // So external programs can tell that something didn't work right.
            }
        }
    }

    File rootDir = null;
    File saveDir = null;


    public String readline(String prompt) {
        try {
            return getIoInterface().readline(prompt);
        } catch (IOException iox) {
            if (DebugUtil.isEnabled()) {
                iox.printStackTrace();
            }
            throw new QDLException("Error reading input:" + iox.getMessage());
        } catch (ArrayIndexOutOfBoundsException ax) {
            if (DebugUtil.isEnabled()) {
                ax.printStackTrace();
            }
            throw new QDLException("Error reading input:" + ax.getMessage());
        }
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
            String x = getIoInterface().readline(null);
            if (x.equals(EXIT_COMMAND)) {
                throw new ExitException(EXIT_COMMAND + " encountered");
            }
            return x;

        } catch (IOException iox) {
            throw new GeneralException("Error, could not read the input line due to IOException", iox);
        }
    }

    public IOInterface getIoInterface() {
        return getState().getIoInterface();
    }

    public void setIoInterface(IOInterface ioInterface) {
        getState().setIoInterface(ioInterface);
    }

    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        WSXMLSerializer serializer = new WSXMLSerializer();
        serializer.toXML(this, xsw);
    }

    public boolean fromXML(XMLEventReader xer) throws XMLStreamException {
        WSXMLSerializer serializer = new WSXMLSerializer();
        WorkspaceCommands newCommands = null;
        try {
            IOInterface ioInterface = getIoInterface();
            newCommands = serializer.fromXML(xer);
            State oldState = getState();
            newCommands.getState().injectTransientFields(oldState);
            // later this is injected into the state. Set it here or custom IO fails later.
            newCommands.setIoInterface(ioInterface);
            state = newCommands.getState();
            state.setIoInterface(ioInterface);
            // now setup the workspace constants
            setDebugOn(newCommands.isDebugOn());
            setEchoModeOn(newCommands.isEchoModeOn());
            setPrettyPrint(newCommands.isPrettyPrint());
            state.createSystemInfo(null);
            state.createSystemConstants();

            currentPID = newCommands.currentPID;
            wsID = newCommands.wsID;
            description = newCommands.description;
            currentWorkspace = newCommands.currentWorkspace;
            rootDir = newCommands.rootDir;
            saveDir = newCommands.saveDir;
            commandHistory = newCommands.commandHistory;
            autosaveInterval = newCommands.getAutosaveInterval();
            autosaveMessagesOn = newCommands.isAutosaveMessagesOn();
            autosaveOn = newCommands.isAutosaveOn();
            runInitOnLoad = newCommands.runInitOnLoad;
            if (autosaveOn) {
                if (currentWorkspace == null) {
                    say("warning you need to set " + CURRENT_WORKSPACE_FILE + " then enable autosave. Autosave is off.");
                    autosaveOn = false;
                } else {
                    if (autosaveThread == null) {
                        autosaveThread = new AutosaveThread(this);
                        autosaveThread.setStopThread(false);
                        autosaveThread.start();
                    }
                }

            }
            startTimeStamp = newCommands.startTimeStamp;
            interpreter = new QDLInterpreter(env, newCommands.getState());
            interpreter.setEchoModeOn(newCommands.isEchoModeOn());
            interpreter.setPrettyPrint(newCommands.isPrettyPrint());
            bufferManager = newCommands.bufferManager;
            bufferManager.state = state;
            bufferDefaultSavePath = newCommands.bufferDefaultSavePath;
            if (ioInterface instanceof ISO6429IO) {
                ISO6429IO iso6429IO = (ISO6429IO) ioInterface;
                iso6429IO.clearCommandBuffer();
                iso6429IO.addCommandHistory(newCommands.commandHistory);
            }
            return true;
        } catch (Throwable t) {
            // This should return a nice message to display.
            getState().getLogger().error("Could not deserialize file", t);
            //return false;
            throw t;
        }

    }

    public boolean isAutosaveOn() {
        return autosaveOn;
    }

    public void setAutosaveOn(boolean autosaveOn) {
        this.autosaveOn = autosaveOn;
    }

    boolean autosaveOn;

    public long getAutosaveInterval() {
        return autosaveInterval;
    }

    public void setAutosaveInterval(long autosaveInterval) {
        this.autosaveInterval = autosaveInterval;
    }

    long autosaveInterval;

    boolean autosaveMessagesOn;

    public boolean isAutosaveMessagesOn() {
        return autosaveMessagesOn;
    }

    public void setAutosaveMessagesOn(boolean autosaveMessagesOn) {
        this.autosaveMessagesOn = autosaveMessagesOn;
    }

    public boolean isAssertionsOn() {
        return assertionsOn;
    }

    public void setAssertionsOn(boolean assertionsOn) {
        this.assertionsOn = assertionsOn;
        getState().setAssertionsOn(assertionsOn);
    }

    boolean assertionsOn;

    public boolean isAnsiModeOn() {
        return ansiModeOn;
    }

    public void setAnsiModeOn(boolean ansiModeOn) {
        this.ansiModeOn = ansiModeOn;
    }

    boolean ansiModeOn = false;

}
