package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils;
import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.parsing.QDLParserDriver;
import edu.uiuc.ncsa.qdl.parsing.QDLRunner;
import edu.uiuc.ncsa.qdl.scripting.QDLScript;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StemMultiIndex;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.variables.*;
import edu.uiuc.ncsa.qdl.vfs.VFSPaths;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.SCHEME_DELIMITER;

/**
 * For control structure in loops, conditionals etc.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  11:49 AM
 */
public class ControlEvaluator extends AbstractFunctionEvaluator {

    public static final String EXECUTE = "execute";
    public static final String FQ_EXECUTE = SYS_FQ + EXECUTE;

    public static final int CONTROL_BASE_VALUE = 5000;
    // Looping stuff
    public static final String CONTINUE = "continue";
    public static final String FQ_CONTINUE = SYS_FQ + CONTINUE;
    public static final int CONTINUE_TYPE = 1 + CONTROL_BASE_VALUE;

    public static final String BREAK = "break";
    public static final String FQ_BREAK = SYS_FQ + BREAK;
    public static final int BREAK_TYPE = 2 + CONTROL_BASE_VALUE;

    public static final String FOR_KEYS = "for_keys";
    public static final String FQ_FOR_KEYS = SYS_FQ + FOR_KEYS;
    public static final int FOR_KEYS_TYPE = 3 + CONTROL_BASE_VALUE;

    public static final String FOR_NEXT = "for_next";
    public static final String FQ_FOR_NEXT = SYS_FQ + FOR_NEXT;
    public static final int FOR_NEXT_TYPE = 4 + CONTROL_BASE_VALUE;

    public static final String CHECK_AFTER = "check_after";
    public static final String FQ_CHECK_AFTER = SYS_FQ + CHECK_AFTER;
    public static final int CHECK_AFTER_TYPE = 5 + CONTROL_BASE_VALUE;


    // function stuff
    public static final String RETURN = "return";
    public static final String FQ_RETURN = SYS_FQ + RETURN;
    public static final int RETURN_TYPE = 100 + CONTROL_BASE_VALUE;


    public static final String MODULE_IMPORT = "module_import";
    public static final String FQ_IMPORT = SYS_FQ + MODULE_IMPORT;
    public static final int IMPORT_TYPE = 203 + CONTROL_BASE_VALUE;

    public static final String MODULE_LOAD = "module_load";
    public static final String FQ_LOAD_MODULE = SYS_FQ + MODULE_LOAD;
    public static final int LOAD_MODULE_TYPE = 205 + CONTROL_BASE_VALUE;

    // For system constants
    public static final String CONSTANTS = "constants";
    public static final String FQ_CONSTANTS = SYS_FQ + CONSTANTS;
    public static final int CONSTANTS_TYPE = 206 + CONTROL_BASE_VALUE;

    // For system info
    public static final String SYS_INFO = "info";
    public static final String FQ_SYS_INFO = SYS_FQ + SYS_INFO;
    public static final int SYS_INFO_TYPE = 207 + CONTROL_BASE_VALUE;

    // for os environment
    public static final String OS_ENV = "os_env";
    public static final String FQ_OS_ENV = SYS_FQ + OS_ENV;
    public static final int OS_ENV_TYPE = 208 + CONTROL_BASE_VALUE;


    // try ... catch

    public static final String RAISE_ERROR = "raise_error";
    public static final String FQ_RAISE_ERROR = SYS_FQ + RAISE_ERROR;
    public static final int RAISE_ERROR_TYPE = 300 + CONTROL_BASE_VALUE;

    // For external programs

    public static final String RUN_COMMAND = "script_run";
    public static final String FQ_RUN_COMMAND = SYS_FQ + RUN_COMMAND;
    public static final int RUN_COMMAND_TYPE = 400 + CONTROL_BASE_VALUE;

    public static final String LOAD_COMMAND = "script_load";
    public static final String FQ_LOAD_COMMAND = SYS_FQ + LOAD_COMMAND;
    public static final int LOAD_COMMAND_TYPE = 401 + CONTROL_BASE_VALUE;

    public static final String SCRIPT_ARGS_COMMAND = "script_args";
    public static final String FQ_SCRIPT_ARGS_COMMAND = SYS_FQ + SCRIPT_ARGS_COMMAND;
    public static final int SCRIPT_ARGS_COMMAND_TYPE = 402 + CONTROL_BASE_VALUE;

    public static final String SCRIPT_PATH_COMMAND = "script_path";
    public static final String FQ_SCRIPT_PATH_COMMAND = SYS_FQ + SCRIPT_PATH_COMMAND;
    public static final int SCRIPT_PATH_COMMAND_TYPE = 403 + CONTROL_BASE_VALUE;


    public static String FUNC_NAMES[] = new String[]{
            SCRIPT_PATH_COMMAND,
            SCRIPT_ARGS_COMMAND,
            SYS_INFO,
            OS_ENV,
            CONSTANTS,
            CONTINUE,
            BREAK,
            FOR_KEYS,
            FOR_NEXT,
            CHECK_AFTER,
            RETURN,
            MODULE_IMPORT,
            MODULE_LOAD,
            RAISE_ERROR,
            RUN_COMMAND,
            LOAD_COMMAND};

    public static String FQ_FUNC_NAMES[] = new String[]{
            FQ_SCRIPT_PATH_COMMAND,
            FQ_SCRIPT_ARGS_COMMAND,
            FQ_SYS_INFO,
            FQ_OS_ENV,
            FQ_CONSTANTS,
            FQ_CONTINUE,
            FQ_BREAK,
            FQ_FOR_KEYS,
            FQ_FOR_NEXT,
            FQ_CHECK_AFTER,
            FQ_RETURN,
            FQ_IMPORT,
            FQ_LOAD_MODULE,
            FQ_RAISE_ERROR,
            FQ_RUN_COMMAND,
            FQ_LOAD_COMMAND};

    @Override
    public String[] getFunctionNames() {
        return FUNC_NAMES;
    }

    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        String[] fnames = listFQ ? FQ_FUNC_NAMES : FUNC_NAMES;
        for (String key : fnames) {
            names.add(key + "()");
        }
        return names;
    }

    @Override
    public int getType(String name) {
        switch (name) {
            case SCRIPT_PATH_COMMAND:
            case FQ_SCRIPT_PATH_COMMAND:
                return SCRIPT_PATH_COMMAND_TYPE;
            case SCRIPT_ARGS_COMMAND:
            case FQ_SCRIPT_ARGS_COMMAND:
                return SCRIPT_ARGS_COMMAND_TYPE;
            case OS_ENV:
            case FQ_OS_ENV:
                return OS_ENV_TYPE;
            case SYS_INFO:
            case FQ_SYS_INFO:
                return SYS_INFO_TYPE;
            case CONSTANTS:
            case FQ_CONSTANTS:
                return CONSTANTS_TYPE;
            case CONTINUE:
            case FQ_CONTINUE:
                return CONTINUE_TYPE;
            case BREAK:
            case FQ_BREAK:
                return BREAK_TYPE;
            case RETURN:
            case FQ_RETURN:
                return RETURN_TYPE;
            case FOR_KEYS:
            case FQ_FOR_KEYS:
                return FOR_KEYS_TYPE;
            case FOR_NEXT:
            case FQ_FOR_NEXT:
                return FOR_NEXT_TYPE;
            case CHECK_AFTER:
            case FQ_CHECK_AFTER:
                return CHECK_AFTER_TYPE;
            // Module stuff
            case MODULE_IMPORT:
            case FQ_IMPORT:
                return IMPORT_TYPE;
            case MODULE_LOAD:
            case FQ_LOAD_MODULE:
                return LOAD_MODULE_TYPE;
            case RAISE_ERROR:
            case FQ_RAISE_ERROR:
                return RAISE_ERROR_TYPE;
            case RUN_COMMAND:
            case FQ_RUN_COMMAND:
                return RUN_COMMAND_TYPE;
            case LOAD_COMMAND:
            case FQ_LOAD_COMMAND:
                return LOAD_COMMAND_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        // NOTE NOTE NOTE!!! The for_next, has_keys, check_after functions are NOT evaluated here. In
        // the WhileLoop class they are picked apart for their contents and the correct looping strategy is
        // done. Look at the WhileLoop's evaluate method. All this evaluator
        // does is mark them as built in functions.

        switch (polyad.getName()) {
            case SCRIPT_PATH_COMMAND:
            case FQ_SCRIPT_PATH_COMMAND:
                doScriptPaths(polyad, state);
                return true;
            case SCRIPT_ARGS_COMMAND:
            case FQ_SCRIPT_ARGS_COMMAND:
                doScriptArgs(polyad, state);
                return true;
            case BREAK:
            case FQ_BREAK:
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                throw new BreakException();
            case CONSTANTS:
            case FQ_CONSTANTS:
                doConstants(polyad, state);
                return true;
            case SYS_INFO:
            case FQ_SYS_INFO:
                doSysInfo(polyad, state);
                return true;
            case OS_ENV:
            case FQ_OS_ENV:
                doOSEnv(polyad, state);
                return true;
            case CONTINUE:
            case FQ_CONTINUE:
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                throw new ContinueException();
            case RETURN:
            case FQ_RETURN:
                doReturn(polyad, state);
                return true;
            case RUN_COMMAND:
            case FQ_RUN_COMMAND:
                runScript(polyad, state);
                return true;
            case LOAD_COMMAND:
            case FQ_LOAD_COMMAND:
                loadScript(polyad, state);
                return true;

            case MODULE_IMPORT:
            case FQ_IMPORT:
                doImport(polyad, state);
                return true;
            case MODULE_LOAD:
            case FQ_LOAD_MODULE:
                doLoadModule(polyad, state);
                return true;
            case RAISE_ERROR:
            case FQ_RAISE_ERROR:
                doRaiseError(polyad, state);
                return true;
            case EXECUTE:
            case FQ_EXECUTE:
                doExecute(polyad, state);
                return true;
        }
        return false;
    }

    /**
     * This accepts either a stem of paths or a single string that is parsed.
     *
     * @param polyad
     * @param state
     */
    protected void doScriptPaths(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.STEM_TYPE);
            StemVariable stemVariable = new StemVariable();
            if (state.getScriptPaths().isEmpty()) {
                polyad.setResult(stemVariable);
                return;
            }
            ArrayList<Object> sp = new ArrayList<>();
            sp.addAll(state.getScriptPaths());
            stemVariable.addList(sp);
            polyad.setResult(stemVariable);
            return;
        }
        if (polyad.getArgCount() == 1) {
            Object obj = polyad.evalArg(0, state);
            if (isString(obj)) {
                state.setScriptPaths(obj.toString());
                polyad.setEvaluated(true);
                polyad.setResult(Boolean.TRUE);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                return;
            }
            if (!isStem(obj)) {
                throw new IllegalArgumentException(SCRIPT_PATH_COMMAND + " requires a stem as its argument.");
            }
            StemVariable stemVariable = (StemVariable) obj;
            // now we have to turn it in to list, tending to the semantics.
            StemList stemList = stemVariable.getStemList();
            List<String> sp = new ArrayList<>();
            for (int i = 0; i < stemList.size(); i++) {
                Object entry = stemList.get(i);
                if (entry != null && !(entry instanceof QDLNull)) {
                    String newPath = entry.toString();
                    newPath = newPath + (newPath.endsWith(VFSPaths.PATH_SEPARATOR) ? "" : VFSPaths.PATH_SEPARATOR);
                    sp.add(newPath);
                }
            }
            state.setScriptPaths(sp);
            polyad.setEvaluated(true);
            polyad.setResult(Boolean.TRUE);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            return;
        }
        throw new IllegalArgumentException(SCRIPT_PATH_COMMAND + " requires at most one argument, not " + polyad.getArgCount());

    }

    /**
     * This will return a given script arg for a given index. No arg returns then number of arguments;
     *
     * @param polyad
     * @param state
     */
    protected void doScriptArgs(Polyad polyad, State state) {

        if (polyad.getArgCount() == 0) {
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setResult(state.hasScriptArgs() ? new Long(state.getScriptArgs().length) : 0L);
            return;
        }
        if (polyad.getArgCount() == 1) {
            Object obj = polyad.evalArg(0, state);
            if (!state.hasScriptArgs()) {
                throw new IllegalArgumentException("index out of bounds for " + SCRIPT_ARGS_COMMAND + "-- no arguments found.");

            }
            if (!isLong(obj)) {
                throw new IllegalArgumentException(SCRIPT_ARGS_COMMAND + " requires an integer argument.");
            }
            int index = ((Long) obj).intValue();
            if (index < 0) {
                throw new IllegalArgumentException(SCRIPT_ARGS_COMMAND + " requires a non-negative integer argument.");
            }
            if (state.getScriptArgs().length <= index) {
                throw new IllegalArgumentException("index out of bounds for " + SCRIPT_ARGS_COMMAND);
            }

            polyad.setEvaluated(true);
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setResult(state.getScriptArgs()[index]);
            return;

        }
        throw new IllegalArgumentException(SCRIPT_ARGS_COMMAND + " requires zero or one argument");

    }


    // Contract is
    // no arg -- return all as stem
    // 1 or more, return environment variable for each. Single one returns the value, otherwise a stem
    // Empty result at all times in server mode.
    protected void doOSEnv(Polyad polyad, State state) {
        if (state.isServerMode()) {
            polyad.setResult("");
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable env = new StemVariable();
        QDLCodec codec = new QDLCodec();

        int argCount = polyad.getArgCount();
        if (argCount == 0) {
            // system variables.
            Map<String, String> map = System.getenv();
            for (String key : map.keySet()) {
                env.put(codec.encode(key), System.getenv(key));
            }
            polyad.setResult(env);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        for (int i = 0; i < argCount; i++) {
            Object obj = polyad.evalArg(i, state);
            if (!isString(obj)) {
                throw new IllegalArgumentException("argument with index " + i + " was not a string.");
            }

            String arg = (String) obj;
            String value = System.getenv(arg);

            if (argCount == 1) {
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.STRING_TYPE);
                polyad.setResult(value == null ? "" : value); // Don't return java null objects.
                return;
            }
            if (value != null) {
                env.put(codec.encode(arg), value);
            }
        }

        polyad.setEvaluated(true);
        polyad.setResult(env);
        polyad.setResultType(Constant.STEM_TYPE);
    }

    protected void doSysInfo(Polyad polyad, State state) {
        getConst(polyad, state, state.getSystemInfo());
    }

    protected void doConstants(Polyad polyad, State state) {
        getConst(polyad, state, state.getSystemConstants());
    }

    protected void getConst(Polyad polyad, State state, StemVariable values) {
        if (polyad.getArgCount() == 0) {
            polyad.setEvaluated(true);
            polyad.setResult(values);
            polyad.setResultType(Constant.STEM_TYPE);
            return;
        }
        Object obj = polyad.evalArg(0, state);
        if (!isString(obj)) {
            throw new IllegalArgumentException("This requires a string as its argument.");
        }
        // A little trickery here. The multi-index is assumed to be of the form
        // stem.index0.index1....  so it will parse the first component as the name of the
        // variable. Since the variable here is un-named, we put in a dummy value of sys. for it which is ignored.
        StemMultiIndex multiIndex = new StemMultiIndex("sys." + obj.toString());
        Object rc = null;
        try {
            rc = values.get(multiIndex);

        } catch (IndexError ie) {
            // The user requested a non-existent property. Don't blow up, just return an empty string.
        }
        polyad.setEvaluated(true);

        if (rc == null) {
            polyad.setResult("");
            polyad.setResultType(Constant.STRING_TYPE);
        } else {
            polyad.setResult(rc);
            polyad.setResultType(Constant.getType(rc));
        }
    }

    /**
     * Run an external qdl script with its own state. Ouput goes to the current console.
     *
     * @param polyad
     * @param state
     */
    protected void runScript(Polyad polyad, State state) {
        runnit(polyad, state, RUN_COMMAND, true);
    }

    protected void loadScript(Polyad polyad, State state) {
        runnit(polyad, state, LOAD_COMMAND, false);

    }

    /**
     * This will use the path information and try to resolve the script based on that.
     * Contract is
     * <ol>
     *     <li>name start with a # ==> this is a file. If server mode, fail. if abs, do it, if rel resolve against file path</li>
     *     <li>name is scheme qualified: if absolute, just get it, if relative, resolve against same scheme</li>
     *     <li>no qualification: if absolute path and server mode, fail, if relative, try against each path  </li>
     * </ol>
     * <b>NOTE</b> First resolution wins.
     *
     * @param name
     * @param state
     * @return
     */
    protected QDLScript resolveScript(String name, State state) throws Throwable {
        /*
               path.1 := 'vfs#/mysql/init2'; path.0 := 'vfs#/mysql'; path.2 := 'vfs#/pt/';script_paths(path.);
               run_script('temp/hw.qdl')
         */
        // case 1. This starts with a # so they want to force getting a regular file
        if (name.startsWith(SCHEME_DELIMITER)) {
            if (state.isServerMode()) {
                throw new IllegalArgumentException("File access forbidden in server mode.");
            }
            name = name.substring(1);
            File file = new File(name);
            if (file.isAbsolute()) {
                return new QDLScript(FileUtil.readFileAsLines(name), null);
            }
            // so its relative.
            for (String p : state.getScriptPaths()) {
                if (!p.contains(SCHEME_DELIMITER)) {
                    File test = new File(p, name);
                    if (test.exists() && test.isFile() && test.canRead()) {
                        return new QDLScript(FileUtil.readFileAsLines(test.getCanonicalPath()), null);
                    }
                }
            }
            return null;
        }
        // case 2: Scheme qualified.
        if (name.contains(SCHEME_DELIMITER)) {
            String tempName = name.substring(1 + name.indexOf(SCHEME_DELIMITER));
            File testFile = new File(tempName);
            if (testFile.isAbsolute()) {
                if (state.hasVFSProviders()) {
                    return state.getScriptFromVFS(name);
                }
            }
            String caput = name.substring(0, name.indexOf(SCHEME_DELIMITER));
            for (String p : state.getScriptPaths()) {
                if (p.startsWith(caput)) {
                    DebugUtil.trace(this, " trying path = " + p + tempName);

                    QDLScript q = state.getScriptFromVFS(p + tempName);
                    if (q != null) {
                        DebugUtil.trace(this, " got path = " + p + tempName);

                        return q;
                    }
                }
            }

        } else {
            // case 3: No qualifications, just a string. Try everything.
            File testFile = new File(name);
            if (testFile.isAbsolute()) {
                if (state.isServerMode()) {
                    throw new IllegalArgumentException("File access forbidden in server mode.");
                } else {
                    return new QDLScript(FileUtil.readFileAsLines(name), null);
                }
            }
            for (String p : state.getScriptPaths()) {
                String resourceName = p + name;
                DebugUtil.trace(this, " path = " + resourceName);
                if (state.isVFSFile(resourceName)) {
                    if (state.isVFSFile(resourceName)) {
                        if (state.hasVFSProviders()) {
                            QDLScript script = state.getScriptFromVFS(resourceName);
                            if (script != null) {
                                return script;
                            }
                        }
                    }
                } else {
                    if (testFile.exists() && testFile.isFile() && testFile.canRead()) {
                        return new QDLScript(FileUtil.readFileAsLines(testFile.getCanonicalPath()), null);
                    }
                }
            }
        }
        return null;
    }

    protected void runnit(Polyad polyad, State state, String commandName, boolean hasNewState) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException("The " + commandName + " requires at least a single argument");
        }
        State localState = state;
        if (hasNewState) {
            localState = state.newModuleState();
        }

        Object arg1 = polyad.evalArg(0, state);
        Object[] argList = new Object[0];
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (isStem(arg2)) {
                StemList stemList = ((StemVariable) arg2).getStemList();
                ArrayList<Object> aa = new ArrayList<>();
                for (int i = 0; i < stemList.size(); i++) {
                    Object object = stemList.get(i);
                    if (object != null && !(object instanceof QDLNull)) {
                        aa.add(object);
                    }
                }
                argList = aa.toArray(new Object[0]);
            }
        }
        // try {
        String resourceName = arg1.toString();
        QDLScript script = null;
        try {
            script = resolveScript(resourceName, state);
        } catch (Throwable t) {
            state.warn("Could not find script:" + t.getMessage());
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLRuntimeException("Could not find  '" + resourceName + "'. Is your script path set?", t);
        }
        if (script == null) {
            throw new QDLRuntimeException("Could not find '" + resourceName + "'. Is your script path set?");
        } else {
            try {
                Object[] oldArgs = localState.getScriptArgs();
                localState.setScriptArgs(argList);
                script.execute(localState);
                localState.setScriptArgs(oldArgs);
            } catch (Throwable t) {
                if(t instanceof ReturnException){
                    ReturnException rx = (ReturnException)t;
                    polyad.setEvaluated(true);
                    polyad.setResultType(rx.resultType);
                    polyad.setResult(rx.result);
                    return;
                }
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                throw new QDLRuntimeException("Error running script '" + arg1 + "'", t);
            }
        }
        polyad.setEvaluated(true);
        polyad.setResultType(Constant.NULL_TYPE);
        polyad.setResult(QDLNull.getInstance());


    }

    protected void doRaiseError(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException(RAISE_ERROR + " requires at least a single argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (arg1 == null) {
            arg1 = "(no message)";
        }
        Object arg2 = null;
        state.setValue("error_message", arg1.toString());
        if (polyad.getArgCount() == 2) {
            arg2 = polyad.evalArg(1, state);
            if (isLong(arg2)) {
                state.getSymbolStack().setLongValue("error_code", (Long) arg2);
            }

        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
        throw new RaiseErrorException(polyad);
    }

    protected void doReturn(Polyad polyad, State state) {
        switch (polyad.getArgCount()) {
            case 0:
                polyad.setEvaluated(true);
                polyad.setResult(QDLNull.getInstance());
                polyad.setResultType(Constant.NULL_TYPE);
                break;
            case 1:
                Object r = polyad.evalArg(0, state);
                polyad.setResult(r);
                polyad.setResultType(polyad.getArguments().get(0).getResultType());
                polyad.setEvaluated(true);
                break;
            default:
                throw new IllegalArgumentException("Error. Wrong number of arguments. You can only return at most a single value");
        }

        // Magic. Sort of. Since it is impossible to tell when or how a function will invoke its
        // return, we throw an exception and catch it at the right time.
        ReturnException rx = new ReturnException();
        rx.result = polyad.getResult();
        rx.resultType = polyad.getResultType();
        throw rx;
    }

    static final int LOAD_FILE = 0;
    static final int LOAD_JAVA = 1;

    protected void doLoadModule(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLServerModeException("reading files is not supported in server mode");
        }
        if (0 == polyad.getArgCount() || 3 <= polyad.getArgCount()) {
            throw new IllegalArgumentException("Error" + MODULE_LOAD + " requires one or two arguments.");
        }
        int loadTarget = LOAD_FILE;
        if (polyad.getArgCount() == 2) {
            // Then this is probably a Jva module
            Object arg2 = polyad.evalArg(1, state);
            if (isString(arg2)) {
                loadTarget = arg2.toString().equals("java") ? LOAD_JAVA : LOAD_FILE;
            } else {
                throw new IllegalArgumentException(MODULE_LOAD + " requires a string as a second argument");
            }

        }
        Object arg = polyad.evalArg(0, state);

        if (!isString(arg)) {
            throw new IllegalArgumentException( MODULE_LOAD + " requires a string as its argument, not '" + arg + "'");
        }
        String resourceName = arg.toString();
        if (loadTarget == LOAD_JAVA) {
            // first arg is the class name.
            try {
                Class klasse = state.getClass().forName(resourceName);
                QDLLoader qdlLoader = (QDLLoader) klasse.newInstance();
                QDLConfigurationLoaderUtils.setupJavaModule(state, qdlLoader, false);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                polyad.setEvaluated(true);
                return;
            } catch (Throwable t) {
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                throw new QDLException("Could not load Java class " + resourceName + ":" + t.getMessage() + ". Be sure it is in the classpath.", t);
            }
        }
        QDLScript script = null;
        File file = null;
        if (state.hasVFSProviders() && resourceName.contains(ImportManager.NS_DELIMITER)) {
            try {
                script = state.getScriptFromVFS(resourceName);
            } catch (Throwable t) {
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                throw new QDLException("error reading script from VFS:" + t.getMessage(), t);
            }
        }
        if (script == null) {
            file = new File(resourceName);
            if (!file.exists()) {
                throw new IllegalArgumentException("file, '" + arg + "' does not exist");
            }
            if (!file.isFile()) {
                throw new IllegalArgumentException("'" + arg + "' is not a file");
            }

            if (!file.canRead()) {
                throw new IllegalArgumentException("You do not have permission to read '" + arg + "'.");
            }

        }
        try {
            QDLParserDriver parserDriver = new QDLParserDriver(new XProperties(), state);
            // Exceptional case where we just run it directly.
            if (script == null) {
                if (state.isServerMode()) {
                    throw new QDLServerModeException("File operations are not permitted in server mode");
                }
                FileReader fileReader = new FileReader(file);
                QDLRunner runner = new QDLRunner(parserDriver.parse(fileReader));
                runner.setState(state);
                runner.run();
            } else {
                script.execute(state);
            }
            //parserDriver.execute(Reader);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setResult(Boolean.TRUE);
            polyad.setEvaluated(true);
        } catch (Throwable t) {
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setResult(Boolean.FALSE);
            polyad.setEvaluated(true);
        }
    }

    /**
     * The general contract for import. You may import everything in a module by using the uri.
     * You may import items using the FQ name (uri + # + name). <br/><br/>
     * Imported items are added to the current stack only. it is nto possible to un-import items since
     * it is impossible to track down references to them and it is dangerous to have programs be able
     * to unload things willy-nilly since that can cause hard to track down failures.
     *
     * @param polyad
     */
    protected void doImport(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException("Error" + MODULE_IMPORT + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        if (arg == null) {
            throw new MissingArgumentException("You must supply at least the module namespace to import.");
        }
        URI moduleNS = null;

        try {
            moduleNS = URI.create(arg.toString());
        } catch (Throwable t) {
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(false);
            throw new QDLException("the module name '" + arg.toString() + "' must be a uri");
        }
        Module m = state.getModuleMap().get(moduleNS);

        if (m == null) {
            throw new IllegalStateException("no such module '" + moduleNS + "'");
        }
        String alias = null;
        Module newInstance = m.newInstance(state.newModuleState());
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (arg2 == null || !isString(arg2)) {
                throw new MissingArgumentException("You must supply a valid alias import.");
            }
            alias = arg2.toString();
        } else {
            // no new alias supplied, so use the default in the module definition.

            alias = m.getAlias();
        }

        ImportManager resolver = state.getImportManager();
        resolver.addImport(moduleNS, alias);
        state.getImportedModules().put(alias, newInstance);
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    protected void doExecute(Polyad polyad, State state) {
        // execute a string.
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("Error. Wrong number of arguments. " +
                    "This requires a single argument that is a string or a list of them.");
        }
        Object result = polyad.evalArg(0, state);
        StemVariable stem = null;

        if (isString(result)) {
            stem = new StemVariable();
            stem.put("0", result); // dummy argument
        }
        if (isStemList(result)) {
            stem = (StemVariable) result;
        }
        if (stem == null) {
            throw new IllegalArgumentException("No executable argument found.");
        }


        QDLParser p = new QDLParser(new XProperties(), state);
        for (int i = 0; i < stem.size(); i++) {
            String currentIndex = Integer.toString(i);
            if (!stem.containsKey(currentIndex)) {
                return;
            }
            try {
                p.execute(stem.getString(Integer.toString(i)));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new GeneralException("Error during execution at index " + i + ".", throwable);
            }
        }
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setResult(Boolean.TRUE);
        polyad.setEvaluated(true);

    }

}
