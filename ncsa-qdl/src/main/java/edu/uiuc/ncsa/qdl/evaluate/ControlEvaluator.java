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
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.TreeSet;

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


    public static final String IMPORT = "import";
    public static final String FQ_IMPORT = SYS_FQ + IMPORT;
    public static final int IMPORT_TYPE = 203 + CONTROL_BASE_VALUE;

    public static final String LOAD_MODULE = "load_module";
    public static final String FQ_LOAD_MODULE = SYS_FQ + LOAD_MODULE;
    public static final int LOAD_MODULE_TYPE = 205 + CONTROL_BASE_VALUE;

    // try ... catch

    public static final String RAISE_ERROR = "raise_error";
    public static final String FQ_RAISE_ERROR = SYS_FQ + RAISE_ERROR;
    public static final int RAISE_ERROR_TYPE = 300 + CONTROL_BASE_VALUE;

    // For external programs

    public static final String RUN_COMMAND = "run_script";
    public static final String FQ_RUN_COMMAND = SYS_FQ + RUN_COMMAND;
    public static final int RUN_COMMAND_TYPE = 400 + CONTROL_BASE_VALUE;

    public static final String LOAD_COMMAND = "load_script";
    public static final String FQ_LOAD_COMMAND = SYS_FQ + LOAD_COMMAND;
    public static final int LOAD_COMMAND_TYPE = 401 + CONTROL_BASE_VALUE;
    public static String FUNC_NAMES[] = new String[]{
            CONTINUE,
            BREAK,
            FOR_KEYS,
            FOR_NEXT,
            CHECK_AFTER,
            RETURN,
            IMPORT,
            LOAD_MODULE,
            RAISE_ERROR,
            RUN_COMMAND,
            LOAD_COMMAND};

    public static String FQ_FUNC_NAMES[] = new String[]{
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
            case IMPORT:
            case FQ_IMPORT:
                return IMPORT_TYPE;
            case LOAD_MODULE:
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
            case BREAK:
            case FQ_BREAK:
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                throw new BreakException();
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

            case IMPORT:
            case FQ_IMPORT:
                doImport(polyad, state);
                return true;
            case LOAD_MODULE:
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

    protected void runnit(Polyad polyad, State state, String commandName, boolean hasNewState) {
        if (polyad.getArgumments().size() == 0) {
            throw new IllegalArgumentException("Error: The" + commandName + " requires at least a single argument");
        }
        State localState = state;
        if (hasNewState) {
            localState = state.newModuleState();
        }

        Object arg1 = polyad.evalArg(0, state);
        try {
            QDLParser interpreter = new QDLParser(localState);
            String resourceName = arg1.toString();
            QDLScript script = null;
            if (state.isVFSFile(resourceName)) {
                if (state.hasVFSProviders()) {
                    script = state.getScriptFromVFS(resourceName);
                }
            }
            if (script != null) {
                script.execute(state);
            } else {
                if (state.isServerMode()) {
                    throw new QDLServerModeException("File operations are not permitted in server mode");
                }
                interpreter.execute(FileUtil.readFileAsString(resourceName));
            }
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setResult(Boolean.TRUE);

        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLRuntimeException("Error running script \"" + arg1 + "\"", t);
        }
    }

    protected void doRaiseError(Polyad polyad, State state) {
        if (polyad.getArgumments().size() == 0) {
            throw new IllegalArgumentException("Error:" + RAISE_ERROR + " requires at least a single argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (arg1 == null) {
            arg1 = "(no message)";
        }
        Object arg2 = null;
        state.setValue("error_message", arg1.toString());
        if (polyad.getArgumments().size() == 2) {
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
        switch (polyad.getArgumments().size()) {
            case 0:
                polyad.setEvaluated(true);
                polyad.setResult(new QDLNull());
                polyad.setResultType(Constant.NULL_TYPE);
                break;
            case 1:
                Object r = polyad.evalArg(0, state);
                polyad.setResult(r);
                polyad.setResultType(polyad.getArgumments().get(0).getResultType());
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
            throw new QDLServerModeException("Error: reading files is not supported in server mode");
        }
        if (0 == polyad.getArgumments().size() ||3 <= polyad.getArgumments().size() ) {
            throw new IllegalArgumentException("Error" + LOAD_MODULE + " requires one or two arguments.");
        }
        int loadTarget = LOAD_FILE;
        if (polyad.getArgumments().size() == 2) {
            // Then this is probably a Jva module
            Object arg2 = polyad.evalArg(1, state);
            if (isString(arg2)) {
                loadTarget = arg2.toString().equals("java") ? LOAD_JAVA : LOAD_FILE;
            } else {
                throw new IllegalArgumentException("Error: " + LOAD_MODULE + " requires a string as a second argument");
            }

        }
        Object arg = polyad.evalArg(0, state);

        if (!isString(arg)) {
            throw new IllegalArgumentException("Error: The " + LOAD_MODULE + " command requires a string as its argument, not \"" + arg + "\"");
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
                throw new QDLException("Error: Could not load Java class " + resourceName + ":" + t.getMessage() + ". Be sure it is in the classpath.", t);
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
                throw new QDLException("Error reading script from VFS:" + t.getMessage(), t);
            }
        }
        if (script == null) {
            file = new File(resourceName);
            if (!file.exists()) {
                throw new IllegalArgumentException("Error: file, \"" + arg + "\" does not exist");
            }
            if (!file.isFile()) {
                throw new IllegalArgumentException("Error: \"" + arg + "\" is not a file");
            }

            if (!file.canRead()) {
                throw new IllegalArgumentException("Error: You do not have permission to read \"" + arg + "\".");
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
        if (polyad.getArgumments().size() == 0) {
            throw new IllegalArgumentException("Error" + IMPORT + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        if (arg == null) {
            throw new MissingArgumentException("Error: You must supply at least the module namespace to import.");
        }
        URI moduleNS = null;

        try {
            moduleNS = URI.create(arg.toString());
        } catch (Throwable t) {
            polyad.setResult(new QDLNull());
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(false);
            throw new QDLException("Error: the module name must be a uri");
        }
        Module m = state.getModuleMap().get(moduleNS);

        if (m == null) {
            throw new IllegalStateException("Error: no such module \"" + moduleNS + "\"");
        }
        String alias = null;
        Module newInstance = m.newInstance(state.newModuleState());
        if (polyad.getArgumments().size() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (arg2 == null || !isString(arg2)) {
                throw new MissingArgumentException("Error: You must supply a valid alias import.");
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
        if (polyad.getArgumments().size() != 1) {
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
