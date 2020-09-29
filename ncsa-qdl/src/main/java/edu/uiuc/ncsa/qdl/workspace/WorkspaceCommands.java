package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoader;
import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils;
import edu.uiuc.ncsa.qdl.config.QDLEnvironment;
import edu.uiuc.ncsa.qdl.evaluate.ControlEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.ReturnException;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.*;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.VFSFileProvider;
import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.LoggerProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.cli.*;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants.*;
import static edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils.*;
import static edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator.FILE_OP_BINARY;
import static edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator.FILE_OP_TEXT_STRING;
import static edu.uiuc.ncsa.security.core.util.StringUtils.LJustify;
import static edu.uiuc.ncsa.security.core.util.StringUtils.RJustify;
import static edu.uiuc.ncsa.security.util.cli.CLIDriver.EXIT_COMMAND;

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
    public static final String COLUMNS_VIEW_SWITCH = SWITCH + "cols";
    public MyLoggingFacade logger;

    XProperties env;

    CommandLineTokenizer CLT = new CommandLineTokenizer();
    protected static final String FUNCS_COMMAND = ")funcs";
    protected static final String HELP_COMMAND = ")help"; // show various types of help
    protected static final String OFF_COMMAND = ")off";
    protected static final String BUFFER2_COMMAND = ")buffer";
    protected static final String SHORT_BUFFER2_COMMAND = ")b";
    protected static final String EXECUTE_COMMAND = ")";
    protected static final String MODULES_COMMAND = ")modules";
    protected static final String LOAD_COMMAND = ")load"; // grab a file and run it
    protected static final String SAVE_COMMAND = ")save";
    protected static final String CLEAR_COMMAND = ")clear";
    protected static final String IMPORTS_COMMAND = ")imports";
    protected static final String VARS_COMMAND = ")vars";
    protected static final String ENV_COMMAND = ")env";
    protected static final String WS_COMMAND = ")ws";
    protected static final String EDIT_COMMAND = ")edit";
    protected static final String FILE_COMMAND = ")file";

    public static final int RC_NO_OP = -1;
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

    protected void showHelp4Help() {
        say(HELP_COMMAND + " syntax:");
        say(HELP_COMMAND + " - (no arg) print generic help for the workspace.");
        say(HELP_COMMAND + " * - print a short summary of help for every user defined function.");
        say(HELP_COMMAND + " name - print help. System functions will have a summary printed (read the manual for more).");
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
        sayi(RJustify(BUFFER2_COMMAND, length) + "- commands relating to using buffers. Alias is " + SHORT_BUFFER2_COMMAND);
        sayi(RJustify(CLEAR_COMMAND, length) + "- clear the state of the workspace. All variables, functions etc. will be lost.");
        sayi(RJustify(EDIT_COMMAND, length) + "- commands relating to running the line editor.");
        sayi(RJustify(ENV_COMMAND, length) + "- commands relating to environment variables in this workspace.");
        sayi(RJustify(EXECUTE_COMMAND, length) + "- short hand to execute whatever is in the current buffer.");
        sayi(RJustify(FUNCS_COMMAND, length) + "- list all of the imported and user defined functions this workspace knows about.");
        sayi(RJustify(HELP_COMMAND, length) + "- this message.");
        sayi(RJustify(MODULES_COMMAND, length) + "- lists all the loaded modules this workspace knows about.");
        sayi(RJustify(OFF_COMMAND, length) + "- exit the workspace.");
        sayi(RJustify(LOAD_COMMAND, length) + "- Load a file of QDL commands and execute it immediately in the current workspace.");
        sayi(RJustify(VARS_COMMAND, length) + "- lists all of the variables this workspace knows about.");
        sayi(RJustify(WS_COMMAND, length) + "- commands relating to this workspace.");
        say("Generally, supplying --help as a parameter to a command will print out something useful.");
        say("Full documentation is available in the docs directory of the distro or at https://cilogon.github.io/qdl/docs/qdl_workspace.pdf");

    }


    public int execute(String inline) {
        inline = TemplateUtil.replaceAll(inline, env); // allow replacements in commands too...
        InputLine inputLine = new InputLine(CLT.tokenize(inline));

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
/*
                old way had a single buffer that ran in the QDLWorkspace.
                if (useLocalBuffer) return RC_EXECUTE_LOCAL_BUFFER;
                if (externalBuffer != null) return RC_EXECUTE_EXTERNAL_BUFFER;
*/
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
            case OFF_COMMAND:
                if (inputLine.hasArg("y")) {
                    return RC_EXIT_NOW;
                }
                if (readline("Do you want to exit?" + (bufferManager.anyEdited() ? " There are unsaved buffers. " : " ") + "(y/n)").equals("y")) {
                    return RC_EXIT_NOW;
                }
                say("System exit cancelled.");
                return RC_CONTINUE;
            case VARS_COMMAND:
                return doVars(inputLine);
            case WS_COMMAND:
                return doWS(inputLine);
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


    protected int doFileCommands(InputLine inputLine) {
        if (inputLine.size() <= ACTION_INDEX) {
            say("Sorry, please supply an argument (e.g. --help)");
            return RC_NO_OP;
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("File commands:");
                sayi("copy");
                sayi("delete or rm");
                sayi("ls or dir");
                sayi("mkdir");
                sayi("rmdir");
                sayi("vfs");
                return RC_NO_OP;
            case "copy":
                return _fileCopy(inputLine);
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

    BufferManager bufferManager = new BufferManager();

    private int _doNewBufferCommand(InputLine inputLine) {
        if (inputLine.size() <= ACTION_INDEX) {
            say("Sorry, you need an argument, (e.g. --help)");
            return RC_NO_OP;
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("buffer commands:");
                sayi("create");
                sayi("delete or rm");
                sayi("edit");
                sayi("link");
                sayi("ls or list");
                sayi("reset");
                sayi("run");
                sayi("show");
                sayi("write or save");
                return RC_NO_OP;
            case "create":
                return _doBufferCreate(inputLine);
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
            case "write":
            case "save":
                return _doBufferWrite(inputLine);
            default:
                say("unrecognized buffer command");
                return RC_NO_OP;
        }

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

    protected int _doBufferRun(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("run (index | name)");
            sayi("Run the given buffer. This will execute as if you had types it in to the current session. ");
            sayi(" Synonym is ) index|name.");
            return RC_NO_OP;
        }
        BufferManager.BufferRecord br = getBR(inputLine);
        List<String> content = null;

        if (br == null || br.deleted) {
            File f = new File(inputLine.getLastArg());
            if (f.exists() && f.isFile()) {
                try {
                    content = FileUtil.readFileAsLines(f.getCanonicalPath());
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
                if (br.isLink()) {
                    content = bufferManager.readFile(br.link);
                } else {
                    content = bufferManager.readFile(br.src);
                }
            }
        }

        boolean origEchoMode = getInterpreter().isEchoModeOn();
        boolean ppOn = getInterpreter().isPrettyPrint();
        getInterpreter().setEchoModeOn(false);
        StringBuffer stringBuffer = new StringBuffer();
        for (String x : content) {
            stringBuffer.append(x + "\n");
        }
        try {
            getInterpreter().execute(stringBuffer.toString());
            getInterpreter().setEchoModeOn(origEchoMode);
            getInterpreter().setPrettyPrint(ppOn);
        } catch (Throwable t) {
            getInterpreter().setEchoModeOn(origEchoMode);
            getInterpreter().setPrettyPrint(ppOn);

            getState().getLogger().error("Could not interpret buffer " + stringBuffer, t);
            say("sorry, but there was an error:" + ((t instanceof NullPointerException) ? "(no message)" : t.getMessage()));
        }
        return RC_CONTINUE;
    }

    protected int _doBufferWrite(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("(write | save) (index | name)");
            sayi("Write (aka save) the buffer. If there is a link, the target is written to the source.");
            return RC_NO_OP;
        }

        BufferManager.BufferRecord br = getBR(inputLine);
        if (br == null || br.deleted) {
            say("buffer not found");
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

    LinkedList<String> editorClipboard = new LinkedList<>();

    private int _doBufferEdit(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("edit (index | name)");
            sayi("invoke the line editor on the given buffer");
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
            String fName = br.isLink() ? br.link : br.src;
            try {
                content = bufferManager.readFile(fName);
            } catch (QDLException q) {
                getState().info("no file " + fName + " found, creating new one.");
                content = new ArrayList<>();
            }
            br.setContent(content);
        }
        // so no buffer. There are a couple ways to get it.

        LineEditor lineEditor = new LineEditor(br.getContent());
        lineEditor.setClipboard(editorClipboard);
        lineEditor.setIoInterface(getIoInterface());
        try {
            lineEditor.execute();
            br.setContent(lineEditor.getBuffer()); // Just to be sure it is the same.
            br.edited = true;
        } catch (Throwable t) {
            t.printStackTrace();
            say("Sorry, there was an issue editing this buffer.");
            getState().warn("Error editing buffer:" + t.getMessage() + " for exception " + t.getClass().getSimpleName());
        }

        return RC_CONTINUE;
    }

    protected BufferManager.BufferRecord getBR(InputLine inputLine) {
        String rawArg = null;
        if (inputLine.getCommand().equals(EXECUTE_COMMAND) || inputLine.getCommand().equals(EDIT_COMMAND)) {
            // Since this is a shorthand, the input line looks like
            // ) 2
            rawArg = inputLine.getArg(ACTION_INDEX);
        } else {
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
            say("copy source target");
            sayi("Copy a file from the source to the target. Note that the workspace is VFS aware.");
            return RC_NO_OP;
        }
        boolean isBinary = inputLine.hasArg("-binary");
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

        BufferManager.BufferRecord br = getBR(inputLine);
        if (br == null || br.deleted) {
            say("buffer not found");
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
        String a = BUFFER_RECORD_SEPARATOR + (br.hasContent() ? "*" : " ") + BUFFER_RECORD_SEPARATOR;
        return ndx + a + br;
    }

    private int _doBufferLink(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("link source target [-copy]");
            sayi("Creates a link (for external editing) between source and target.");
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

    protected boolean _doHelp(InputLine inputLine) {
        if (inputLine.hasArg("help") || inputLine.hasArg("-help") || inputLine.hasArg("--help")) return true;
        return false;
    }

    private int _doBufferCreate(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("create path");
            sayi("create a new buffer for the path.");
            return RC_CONTINUE;
        }
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
                sayi("clear");
                sayi("drop");
                sayi("get");
                sayi("list");
                sayi("load");
                sayi("save");
                sayi("set");
                sayi("name");
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
        }
        say("Unknown environment command.");
        return RC_CONTINUE;
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
            say("list");
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
        if (!inputLine.hasArgs() || inputLine.getArg(ACTION_INDEX).startsWith(SWITCH)) {
            return _modulesList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("Modules commands:");
                sayi("list");
                sayi("imports");
                return RC_NO_OP;
            case "list":
                return _modulesList(inputLine);
            case "imports":
                return _moduleImports(inputLine);
        }
        say("Unknown modules command");
        return RC_CONTINUE;
    }

    private int _moduleImports(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("imports");
            sayi("A table of imported modules and their aliases. ");
            sayi("You must load a module with " + ControlEvaluator.MODULE_LOAD + " to make QDL aware of it before importing it");
            return RC_NO_OP;
        }

        if (!state.getImportManager().hasImports()) {
            say("(no imports)");
            return RC_CONTINUE;
        }
        TreeSet<String> aliases = new TreeSet<>();
        for (URI uri : state.getImportManager().keySet()) {
            aliases.add(uri + "->" + state.getImportManager().getAlias(uri));
        }
        return printList(inputLine, aliases);
    }

    private int _modulesList(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("list");
            sayi("Lists the modules available (via " + ControlEvaluator.MODULE_LOAD + ") in to this workspace. " +
                    "Note that to use one, you must import it with the " + ControlEvaluator.MODULE_IMPORT + " command.");
            return RC_NO_OP;
        }

        TreeSet<String> m = new TreeSet<>();
        for (URI key : getState().getModuleMap().keySet()) {
            m.add(key.toString());
        }
        return printList(inputLine, m);
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
        if (!inputLine.hasArgs() || inputLine.getArg(ACTION_INDEX).startsWith(SWITCH)) {
            return _funcsList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "--help":
                say("Function commands:");
                sayi("drop");
                sayi("help");
                sayi("list");
                sayi("system");
                return RC_NO_OP;
            case "drop":
                return _funcsDrop(inputLine);
            case "help":
                return _funcsHelp(inputLine);
            case "list":
                return _funcsList(inputLine);
            case "system":
                return _funcsListSystem(inputLine);
            default:
                say("sorry, unrecognized command.");
        }
        return RC_CONTINUE;
    }

    private int _funcsDrop(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("drop fname");
            sayi("Removes the function from the current workspace. Note this applies to user-defined functions, not imported functions.");
            return RC_NO_OP;
        }

        String fName = inputLine.getArg(FIRST_ARG_INDEX);
        getState().getFunctionTable().remove(fName);
        if (getState().getFunctionTable().containsKey(fName)) {
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
     * Any list of string s(functions, variables, modules, etc.) is listed using this formatting function.
     * If understands command line switches for width, columns and does some regex's too.
     *
     * @param inputLine
     * @param list
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
        return printList(inputLine, funcs);
    }

    protected int _funcsList(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("list");
            sayi("List all user defined functions.");
            return RC_NO_OP;
        }

        boolean useCompactNotation = inputLine.hasArg(COMPACT_ALIAS_SWITCH);
        TreeSet<String> funs = getState().listFunctions(useCompactNotation, null);
        return printList(inputLine, funs);
    }

    /**
     * Either show all variables (no arg or argument of "list") or <br/><br/>
     * drop name -- remove the given symbol from the local symbol table. This does not effect modules.
     *
     * @param inputLine
     * @return
     */
    private int doVars(InputLine inputLine) {
        if (!inputLine.hasArgs() || inputLine.getArg(ACTION_INDEX).startsWith(SWITCH)) {
            return _varsList(inputLine);
        }
        switch (inputLine.getArg(ACTION_INDEX)) {
            case "help":
            case "--help":
                say("Variable commands");
                sayi("system");
                say("list");
                say("drop");
                say("size");
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
        }
        say("Unknown variable command.");
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
            say("list");
            sayi("Lists the variables in the current workspace.");
            return RC_NO_OP;
        }

        boolean useCompactNotation = inputLine.hasArg(COMPACT_ALIAS_SWITCH);
        return printList(inputLine, getState().listVariables(useCompactNotation));
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
           if(inputLine.getArg(ACTION_INDEX).equals("--help")){
               showHelp4Help();
               return RC_CONTINUE;
           }
        String name = inputLine.getArg(ACTION_INDEX);
        if (name.equals("*")) {
            // so they entered )funcs help Print off first lines of help
            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.addAll(getState().listAllDocumentation());
            if(treeSet.isEmpty()){
                say("(no user-defined functions)");
                return RC_CONTINUE;
            }
            return printList(inputLine, treeSet);
        }

        if (onlineHelp.containsKey(name)) {
            say(onlineHelp.get(name));
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
    /*
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
     */

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
                sayi("load");
                sayi("save");
                sayi("clear");
                sayi("echo");
                sayi("id");
                sayi("memory");
                return RC_NO_OP;
            case "load":
                return _wsLoad(inputLine);
            case "save":
                return _wsSave(inputLine);
            case "clear":
                return _wsClear(inputLine);
            case "echo":
                return _wsEchoMode(inputLine);
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
                return _fileVFS(inputLine);
            default:
                say("unrecognized workspace command.");
                return RC_NO_OP;
        }

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
            say("clear");
            sayi("Clear the state *completely*. This includes all virtual file systems and buffers.");
            return RC_NO_OP;
        }
        boolean clearIt = readline("Are you sure you want to clear the worskapce state? (Y/n)[n]").equals("Y");
        if (!clearIt) {
            say("WS clear aborted.");
            return RC_NO_OP;
        }
        state = null;
        // Get rid of everything.

        interpreter = new QDLParser(getState());
        say("workspace cleared");
        return RC_CONTINUE;
    }

    private int _wsSave(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("save filename");
            sayi("Saves the current state (variables, loaded functions but not pending buffers of VFS to a file.");
            sayi("This should be either a relative path (resolved against the default save location) or an absolute path.");
            sayi("See the corresponding load command to recover it.");
            return RC_NO_OP;
        }

        try {
            if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
                say("sorry, no file given");
                return RC_CONTINUE;
            }
            String fName = inputLine.getArg(FIRST_ARG_INDEX);
            File target = new File(fName);
            if (target.isAbsolute()) {
                if (saveDir == null) {
                    saveDir = target.getParentFile();
                    say("Default save path updated to " + saveDir.getCanonicalPath());
                }
            } else {
                if (saveDir == null) {
                    target = new File(rootDir, fName);
                } else {
                    target = new File(saveDir, fName);
                }
            }
            FileOutputStream fos = new FileOutputStream(target);
            logger.info("saving workspace '" + target.getAbsolutePath() + "'");

            StateUtils.save(state, fos);
            say("Saved " + target.length() + " bytes to " + target.getCanonicalPath() + " on " + (new Date()));
        } catch (Throwable t) {
            logger.error("could not save workspace.", t);
            say("could not save the workspace:" + t.getMessage());
        }
        return RC_NO_OP;
    }

    /*
    Does the actual work of loading a file once the logic for what to do has been done.
     */
    private void _realLoad(File f) {
        try {
            long lastModified = f.lastModified();
            FileInputStream fis = new FileInputStream(f);
            State newState = StateUtils.load(fis);
            /*
            Now set the stuff that cannot be serialized.
             */
            newState.injectTransientFields(getState());

            interpreter = new QDLParser(env, newState);
            interpreter.setEchoModeOn(isEchoModeOn());
            interpreter.setDebugOn(isDebugOn());
            state = newState;
            currentWorkspace = f;
            say(f.getCanonicalPath() + " loaded " + f.length() + " bytes. Last saved " + new Date(lastModified));
        } catch (Throwable t) {
            say("workspace not loaded.");
            t.printStackTrace();
        }
    }

    File currentWorkspace;

    private int _wsLoad(InputLine inputLine) {
        if (_doHelp(inputLine)) {
            say("load filename");
            sayi("Loads a saved workspace. If the name is relative, it will be resolved against " +
                    "the default location or it may be an absolute path.");
            return RC_NO_OP;
        }

        if (!inputLine.hasArgAt(FIRST_ARG_INDEX)) {
            if (currentWorkspace == null) {
                say("Sorry, no file given and no workspace has been loaded.");
            } else {
                _realLoad(currentWorkspace);
            }
            return RC_CONTINUE;
        }

        String fName = inputLine.getArg(FIRST_ARG_INDEX);
        File f = new File(fName);
        if (f.isAbsolute()) {
            // don't change save directory on load. (???) May change this
        } else {
            if (saveDir == null) {
                f = new File(rootDir, fName);
            } else {
                f = new File(saveDir, fName);
            }
        }
        _realLoad(f);
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
    public void say(String x) {
        getIoInterface().println(defaultIndent + x);
        // getPrintStream().println(defaultIndent + x);
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
    public static final String CLA_SCRIPT_PATH = "-script_path";

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

        QDLConfigurationLoader loader = new QDLConfigurationLoader(inputLine.getNextArgFor(CONFIG_FILE_FLAG), node);

        QDLEnvironment qe = loader.load();
        if (inputLine.hasArg("-home_dir")) {
            // The user might set the home directory here.
            // This is overrides configuration file.
            rootDir = new File(inputLine.getNextArgFor("-home_dir"));
        }

        if(inputLine.hasArg("-debug")){
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
        File testSaveDir = new File(rootDir, "var/ws");
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
            if (!isRunScript && isVerbose) {
                say("loaded java modules:");
                StringTokenizer t = new StringTokenizer(foundModules[JAVA_MODULE_INDEX], ",");
                while (t.hasMoreTokens()) {
                    sayi(t.nextToken().trim());
                }
            }
            env.put("java_modules", foundModules[JAVA_MODULE_INDEX]);
        }
        if (foundModules[QDL_MODULE_INDEX] != null && !foundModules[QDL_MODULE_INDEX].isEmpty()) {
            if (!isRunScript && isVerbose) {
                say("loaded QDL modules:");
                StringTokenizer t = new StringTokenizer(foundModules[QDL_MODULE_INDEX], ",");
                while (t.hasMoreTokens()) {
                    sayi(t.nextToken().trim());
                }
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
        interpreter.setPrettyPrint(qe.isPrettyPrint());
        getState().setScriptPaths(qe.getScriptPath());

        runScript(inputLine); // run any script if that mode is enabled.

    }

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
        if (inputLine.hasArg(CLA_SCRIPT_PATH)) {
            getState().setScriptPaths(inputLine.getNextArgFor(CLA_SCRIPT_PATH));
        }
        runScript(inputLine); // If there is a script, run it.
    }

    private void runScript(InputLine inputLine) {
        if (isRunScript) {

            // get the args for the script
            // contract is that there is the -run file x y z ...
            // so x,y,z,... are passed tot the script.
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
                String runScript = FileUtil.readFileAsString(runScriptPath);
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
            throw new QDLException("Error reading input.");
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

}
