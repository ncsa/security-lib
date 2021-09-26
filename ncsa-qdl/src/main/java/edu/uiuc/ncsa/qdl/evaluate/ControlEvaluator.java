package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.config.QDLConfigurationLoaderUtils;
import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.extensions.QDLFunctionRecord;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.functions.FR_WithState;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.parsing.QDLParserDriver;
import edu.uiuc.ncsa.qdl.parsing.QDLRunner;
import edu.uiuc.ncsa.qdl.scripting.QDLScript;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.SIEntry;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StemMultiIndex;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.util.QDLFileUtil;
import edu.uiuc.ncsa.qdl.variables.*;
import edu.uiuc.ncsa.qdl.vfs.VFSPaths;
import edu.uiuc.ncsa.qdl.workspace.QDLWorkspace;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.net.URI;
import java.util.*;
import java.util.logging.Level;

import static edu.uiuc.ncsa.qdl.variables.StemUtility.axisWalker;
import static edu.uiuc.ncsa.qdl.vfs.VFSPaths.SCHEME_DELIMITER;
import static edu.uiuc.ncsa.security.core.util.DebugConstants.*;

/**
 * For control structure in loops, conditionals etc.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  11:49 AM
 */
public class ControlEvaluator extends AbstractFunctionEvaluator {

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

    public static final String INTERRUPT = "halt";
    public static final String FQ_INTERRUPT = SYS_FQ + INTERRUPT;
    public static final int INTERRUPT_TYPE = 6 + CONTROL_BASE_VALUE;

    public static final String EXECUTE = "execute";
    public static final String FQ_EXECUTE = SYS_FQ + EXECUTE;
    public static final int EXECUTE_TYPE = 10 + CONTROL_BASE_VALUE;

    public static final String CHECK_SYNTAX = "check_syntax";
    public static final String FQ_CHECK_SYNTAX = SYS_FQ + CHECK_SYNTAX;
    public static final int CHECK_SYNTAX_TYPE = 11 + CONTROL_BASE_VALUE;

    public static final String INPUT_FORM = "input_form";
    public static final String FQ_INPUT_FORM = SYS_FQ + INPUT_FORM;
    public static final int INPUT_FORM_TYPE = 12 + CONTROL_BASE_VALUE;

    public static final String REDUCE = "reduce";
    public static final String FQ_REDUCE = SYS_FQ + REDUCE;
    public static final int REDUCE_TYPE = 14 + CONTROL_BASE_VALUE;

    public static final String EXPAND = "expand";
    public static final String FQ_EXPAND = SYS_FQ + EXPAND;
    public static final int EXPAND_TYPE = 15 + CONTROL_BASE_VALUE;

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

    public static final String MODULE_PATH = "module_path";
    public static final String FQ_MODULE_PATH = SYS_FQ + MODULE_PATH;
    public static final int MODULE_PATH_TYPE = 211 + CONTROL_BASE_VALUE;


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

    // logging
    public static final String SYSTEM_LOG = "log_entry";
    public static final String FQ_SYSTEM_LOG = SYS_FQ + SYSTEM_LOG;
    public static final int SYSTEM_LOG_TYPE = 209 + CONTROL_BASE_VALUE;


    // logging
    public static final String DEBUG = "debug";
    public static final String FQ_DEBUG = SYS_FQ + DEBUG;
    public static final int DEBUG_TYPE = 210 + CONTROL_BASE_VALUE;
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
            REDUCE, EXPAND,
            SCRIPT_PATH_COMMAND,
            SCRIPT_ARGS_COMMAND,
            SYS_INFO,
            OS_ENV,
            SYSTEM_LOG,
            DEBUG,
            CONSTANTS,
            CONTINUE,
            INTERRUPT,
            BREAK,
            EXECUTE,
            CHECK_SYNTAX,
            INPUT_FORM,
            FOR_KEYS,
            FOR_NEXT,
            CHECK_AFTER,
            RETURN,
            MODULE_IMPORT,
            MODULE_LOAD,
            MODULE_PATH,
            RAISE_ERROR,
            RUN_COMMAND,
            LOAD_COMMAND};

    public static String FQ_FUNC_NAMES[] = new String[]{
            FQ_REDUCE, FQ_EXPAND,
            FQ_SCRIPT_PATH_COMMAND,
            FQ_SCRIPT_ARGS_COMMAND,
            FQ_SYS_INFO,
            FQ_OS_ENV,
            FQ_SYSTEM_LOG,
            FQ_DEBUG,
            FQ_CONSTANTS,
            FQ_CONTINUE,
            FQ_INTERRUPT,
            FQ_EXECUTE,
            FQ_CHECK_SYNTAX,
            FQ_INPUT_FORM,
            FQ_BREAK,
            FQ_FOR_KEYS,
            FQ_FOR_NEXT,
            FQ_CHECK_AFTER,
            FQ_RETURN,
            FQ_IMPORT,
            FQ_LOAD_MODULE,
            FQ_MODULE_PATH,
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
            case EXPAND:
            case FQ_EXPAND:
                return EXPAND_TYPE;
            case REDUCE:
            case FQ_REDUCE:
                return REDUCE_TYPE;
            case SCRIPT_PATH_COMMAND:
            case FQ_SCRIPT_PATH_COMMAND:
                return SCRIPT_PATH_COMMAND_TYPE;
            case SCRIPT_ARGS_COMMAND:
            case FQ_SCRIPT_ARGS_COMMAND:
                return SCRIPT_ARGS_COMMAND_TYPE;
            case OS_ENV:
            case FQ_OS_ENV:
                return OS_ENV_TYPE;
            case DEBUG:
            case FQ_DEBUG:
                return DEBUG_TYPE;
            case SYSTEM_LOG:
            case FQ_SYSTEM_LOG:
                return SYSTEM_LOG_TYPE;
            case SYS_INFO:
            case FQ_SYS_INFO:
                return SYS_INFO_TYPE;
            case CONSTANTS:
            case FQ_CONSTANTS:
                return CONSTANTS_TYPE;
            case CONTINUE:
            case FQ_CONTINUE:
                return CONTINUE_TYPE;
            case EXECUTE:
            case FQ_EXECUTE:
                return EXECUTE_TYPE;
            case INPUT_FORM:
                return INPUT_FORM_TYPE;
            case CHECK_SYNTAX:
            case FQ_CHECK_SYNTAX:
                return CHECK_SYNTAX_TYPE;
            case INTERRUPT:
            case FQ_INTERRUPT:
                return INTERRUPT_TYPE;
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
            case MODULE_PATH:
            case FQ_MODULE_PATH:
                return MODULE_PATH_TYPE;
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
            case EXPAND:
            case FQ_EXPAND:
                doReduceOrExpand(polyad, state, false);
                return true;
            case REDUCE:
            case FQ_REDUCE:
                doReduceOrExpand(polyad, state, true);
                return true;
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
            case SYSTEM_LOG:
            case FQ_SYSTEM_LOG:
                doSysLog(polyad, state, false);
                return true;
            case DEBUG:
            case FQ_DEBUG:
                doSysLog(polyad, state, true);
                return true;
            case CONTINUE:
            case FQ_CONTINUE:
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                throw new ContinueException();
            case INTERRUPT:
            case FQ_INTERRUPT:
                doInterrupt(polyad, state);
                return true;
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
                doModuleImport(polyad, state);
                return true;
            case MODULE_LOAD:
            case FQ_LOAD_MODULE:
                doLoadModule(polyad, state);
                return true;
            case MODULE_PATH:
            case FQ_MODULE_PATH:
                doModulePaths(polyad, state);
                return true;
            case RAISE_ERROR:
            case FQ_RAISE_ERROR:
                doRaiseError(polyad, state);
                return true;
            case EXECUTE:
            case FQ_EXECUTE:
                doExecute(polyad, state);
                return true;
            case CHECK_SYNTAX:
            case FQ_CHECK_SYNTAX:
                doCheckSyntax(polyad, state);
                return true;
            case INPUT_FORM:
                doInputForm(polyad, state);
                return true;
        }
        return false;
    }

    /*
      times(x,y)->x*y
      reduce(@times(), 1+n(5))

     */
    private void doReduceOrExpand(Polyad polyad, State state0, boolean doReduce) {
        State state = state0.newStateWithImports();
        FunctionReferenceNode frn;
        StatementWithResultInterface arg0 = polyad.getArguments().get(0);

        frn = getFunctionReferenceNode(state, polyad.getArguments().get(0), true);
        Object arg1 = polyad.evalArg(1, state);
        checkNull(arg1, polyad.getArgAt(1), state);
        if (!isStem(arg1)) {
            polyad.setResult(arg1);
            polyad.setResultType(Constant.getType(arg1));
            polyad.setEvaluated(true);
            return;
        }
        StemVariable stemVariable = (StemVariable) arg1;
        if (!stemVariable.isList()) {
            throw new IllegalArgumentException("second argument of " + REDUCE + " must be a list");
        }
        int axis = 0; // default
        if (polyad.getArgCount() == 3) {
            Object axisObj = polyad.evalArg(2, state);
            checkNull(axisObj, polyad.getArgAt(2));
            if (!isLong(axisObj)) {
                throw new IllegalArgumentException("error: third argument of " + (doReduce ? REDUCE : EXPAND) + ", the axis, must be an integer");
            }
            axis = ((Long) axisObj).intValue();
        }
        if (stemVariable.size() == 0) {
            polyad.setEvaluated(true);
            if (doReduce) {
                // result is a scalar
                polyad.setResult(QDLNull.getInstance());
                polyad.setResultType(Constant.NULL_TYPE);
            } else {
                polyad.setResult(new StemVariable());
                polyad.setResultType(Constant.STEM_TYPE);
            }
            // result is an empty stem
            return;
        }
        if (stemVariable.size() == 1) {
            StemEntry stemEntry = stemVariable.getStemList().iterator().next();
            polyad.setEvaluated(true);

            if (doReduce) {
                // return scalar of the value
                polyad.setResult(stemEntry.entry);
                polyad.setResultType(Constant.getType(stemEntry.entry));

            } else {
                StemVariable output = new StemVariable();
                output.listAppend(stemEntry.entry);
                polyad.setResult(output);
                polyad.setResultType(Constant.STEM_TYPE);
            }
            return;
        }

        // newReduceOrExpand(polyad, state, doReduce, axis, (FunctionReferenceNode) arg0, stemVariable);
        // oldReduceOrExpand(polyad, state, doReduce, (FunctionReferenceNode) arg0, stemVariable);
        // oldReduceOrExpand2(polyad, state, doReduce, (FunctionReferenceNode) arg0, stemVariable);
        StemUtility.StemAxisWalkerAction1 axisWalker;
        if (doReduce) {
            axisWalker = this.new AxisReduce(getOperator(state, frn, 2), state);
        } else {
            axisWalker = this.new AxisExpand(getOperator(state, frn, 2), state);
        }
        Object result = axisWalker(stemVariable, axis, axisWalker);
        polyad.setResult(result);
        polyad.setResultType(Constant.getType(result));
        polyad.setEvaluated(true);
    }


    public class AxisReduce implements StemUtility.StemAxisWalkerAction1 {
        ExpressionImpl operator;
        State state;

        public AxisReduce(ExpressionImpl operator, State state) {
            this.operator = operator;
            this.state = state;
        }

        @Override
        public Object action(StemVariable inStem) {
            Object reduceOuput = null;
            // contract is that a single list is returned unaltered.
            // At this point, we know there are at least 2 entries.
            Iterator<StemEntry> iterator = inStem.getStemList().iterator();
            Object lastValue = iterator.next().entry;

            while (iterator.hasNext()) {
                Object currentValue = iterator.next().entry;
                ArrayList<StatementWithResultInterface> argList = new ArrayList<>();
                argList.add(new ConstantNode(lastValue, Constant.getType(lastValue)));
                argList.add(new ConstantNode(currentValue, Constant.getType(currentValue)));
                operator.setArguments(argList);
                operator.evaluate(state);
                reduceOuput = operator.getResult();
                lastValue = operator.getResult();
            }
            return reduceOuput;
        }
    }

    public class AxisExpand implements StemUtility.StemAxisWalkerAction1 {
        ExpressionImpl operator;
        State state;

        public AxisExpand(ExpressionImpl operator, State state) {
            this.operator = operator;
            this.state = state;
        }

        @Override
        public Object action(StemVariable inStem) {
            StemVariable output = new StemVariable();
            Set<String> keySet = inStem.keySet();
            Iterator<String> iterator = keySet.iterator();

            Object lastValue = inStem.get(iterator.next()); // grab one before loop starts
            output.listAppend(lastValue);

            while (iterator.hasNext()) {
                String key = iterator.next();
                Object currentValue = inStem.get(key);
                ArrayList<StatementWithResultInterface> argList = new ArrayList<>();
                argList.add(new ConstantNode(lastValue, Constant.getType(lastValue)));
                argList.add(new ConstantNode(currentValue, Constant.getType(currentValue)));
                operator.setArguments(argList);
                operator.evaluate(state);
                output.put(key, operator.getResult());
                lastValue = operator.getResult();
            }
            return output;
        }
    }


    private void doInputForm(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException(INPUT_FORM + " requires at least one argument");
        }
        if (polyad.getArguments().get(0) instanceof ConstantNode) {
            Object arg = polyad.evalArg(0, state);

            // Looking for a module, so this must be a string
            if (!isString(arg)) {
                throw new IllegalArgumentException(INPUT_FORM + " requires a module name (as a string)");
            }
            String out = InputFormUtil.inputFormModule(arg.toString(), state);
            if (out == null) {
                out = "";
            }
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setResult(out);
            return;
        }
        boolean gotOne = false;
        String argName = null;
        Module module = null;
        if (polyad.getArguments().get(0) instanceof ModuleExpression) {
            ModuleExpression moduleExpression = (ModuleExpression) polyad.getArguments().get(0);
            module = state.getImportedModules().get(moduleExpression.getAlias());
            if (module == null) {
                throw new IllegalArgumentException("no module named '" + moduleExpression.getAlias() + "' found.");
            }
            if (!(moduleExpression.getExpression() instanceof VariableNode)) {
                throw new IllegalArgumentException(INPUT_FORM + " requires a variable or function name");
            }

            argName = ((VariableNode) moduleExpression.getExpression()).getVariableReference();
            moduleExpression.setModuleState(module.getState());
            state = moduleExpression.getLocalState(state);
            gotOne = true;
        }

        if (polyad.getArguments().get(0) instanceof VariableNode) {
            argName = ((VariableNode) polyad.getArguments().get(0)).getVariableReference();
            gotOne = true;
        }

        if (!gotOne) {
            throw new IllegalArgumentException(INPUT_FORM + " requires a variable or function name");
        }

        if (argName == null) {
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(true);
            polyad.setResult(QDLNull.getInstance());
            return;

        }
        if (polyad.getArgCount() == 1) {
            // simple variable case, no indent
            String output = InputFormUtil.inputFormVar(argName, state);
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setEvaluated(true);
            polyad.setResult(output);
            return;
        }

        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException(INPUT_FORM + " requires the argument count or boolean as the second parameter");
        }
        Object arg2 = polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1), state);
        boolean doIndent = false;
        int argCount = -1;

        switch (Constant.getType(arg2)) {
            case Constant.LONG_TYPE:
                argCount = ((Long) arg2).intValue();
                break;
            case Constant.BOOLEAN_TYPE:
                doIndent = (Boolean) arg2;
                break;
            default:
                throw new IllegalArgumentException(INPUT_FORM + " requires an argument count for functions of a boolean ");
        }
        if (polyad.getArgCount() == 2 && isBoolean(arg2)) {
            // process as variable with indent factor
            String output = InputFormUtil.inputFormVar(argName, 2, state);
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setEvaluated(true);
            polyad.setResult(output);
            return;
        }
        // case here is that second arg is not a boolean (==> must be arg count) OR there is more than one arg.
        if (!isLong(arg2)) {
            throw new IllegalArgumentException(INPUT_FORM + " second argument must be an integer");
        }

        FR_WithState fr_withState = state.resolveFunction(argName, argCount, true);
        if (fr_withState == null) {
            // no such critter
            polyad.setResult("");
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        FunctionRecord fr = fr_withState.functionRecord;
        String output = "";
        if (fr != null) {
            if (fr instanceof QDLFunctionRecord) {
                QDLFunction qf = ((QDLFunctionRecord) fr).qdlFunction;
                if (qf != null) {
                    output = "java:" + qf.getClass().getCanonicalName();
                }
            } else {
                output = StringUtils.listToString(fr.sourceCode);
            }
        }
        polyad.setResult(output);
        polyad.setResultType(Constant.STRING_TYPE);
        polyad.setEvaluated(true);
    }


    private void doCheckSyntax(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("Error: the argument to " + CHECK_SYNTAX + " requires a single argument.");
        }
        Object arg0 = polyad.evalArg(0, state);
        checkNull(arg0, polyad.getArgAt(0), state);
        if (!isString(arg0)) {
            throw new IllegalArgumentException("Error: the argument to " + CHECK_SYNTAX + " must be a string.");
        }
        StringReader r = new StringReader((String) arg0);
        String message = "";
        QDLParserDriver driver = new QDLParserDriver(new XProperties(), state.newDebugState());
        try {
            QDLRunner runner = new QDLRunner(driver.parse(r));
        } catch (ParseCancellationException pc) {
            message = pc.getMessage();
        } catch (AssignmentException ax) {
            message = ax.getMessage();
        } catch (Throwable t) {
            message = "non-syntax error:" + t.getMessage();

        }

        polyad.setEvaluated(true);
        polyad.setResult(message);
        polyad.setResultType(Constant.STRING_TYPE);
    }

    protected void doInterrupt(Polyad polyad, State state) {
        if (state.isServerMode()) {
            // no interrupts in server mode.
            polyad.setResult(-1L);
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        String message = "";
        switch (polyad.getArgCount()) {
            case 0:
                // do nothing.
                break;
            case 1:
                Object obj = polyad.evalArg(0, state);
                if (obj == null || (obj instanceof QDLNull)) {
                    message = "(null)";
                } else {
                    message = obj.toString();
                }
                break;
            default:
                throw new IllegalArgumentException("Error: " + INTERRUPT + " accepts at most one argument.");
        }
        SIEntry sie = new SIEntry();
        sie.state = state;
        sie.message = message;

        throw new InterruptException(sie);
    }


    public static final int LOG_LEVEL_UNKNOWN = -100;
    public static final int LOG_LEVEL_NONE = DEBUG_LEVEL_OFF;
    public static final int LOG_LEVEL_INFO = DEBUG_LEVEL_INFO;
    public static final int LOG_LEVEL_WARN = DEBUG_LEVEL_WARN;
    public static final int LOG_LEVEL_ERROR = DEBUG_LEVEL_ERROR;
    public static final int LOG_LEVEL_SEVERE = DEBUG_LEVEL_SEVERE;
    public static final int LOG_LEVEL_TRACE = DEBUG_LEVEL_TRACE;

    private Level getLogLevel(Long myLevel) {
        return getLogLevel(myLevel.intValue());
    }

    private boolean isValidLoggingLevel(Long longLevel) {
        int value = longLevel.intValue();
        switch (value) {
            case LOG_LEVEL_NONE:
            case LOG_LEVEL_TRACE:
            case LOG_LEVEL_INFO:
            case LOG_LEVEL_WARN:
            case LOG_LEVEL_ERROR:
            case LOG_LEVEL_SEVERE:
                return true;
            default:
                return false;
        }
    }

    private Level getLogLevel(int myLevel) {
        switch (myLevel) {
            default:
            case LOG_LEVEL_NONE:
                return Level.OFF;
            case LOG_LEVEL_TRACE:
                return Level.FINEST;
            case LOG_LEVEL_INFO:
                return Level.INFO;
            case LOG_LEVEL_WARN:
                return Level.WARNING;
            case LOG_LEVEL_ERROR:
                return Level.ALL;
            case LOG_LEVEL_SEVERE:
                return Level.SEVERE;
        }
    }

    protected int getMyLogLevel(Level level) {
        int value = level.intValue();
        if (value == Level.OFF.intValue()) return LOG_LEVEL_NONE;
        if (value == Level.FINEST.intValue()) return LOG_LEVEL_TRACE;
        if (value == Level.INFO.intValue()) return LOG_LEVEL_INFO;
        if (value == Level.WARNING.intValue()) return LOG_LEVEL_WARN;
        if (value == Level.ALL.intValue()) return LOG_LEVEL_ERROR;
        if (value == Level.SEVERE.intValue()) return LOG_LEVEL_SEVERE;

        return LOG_LEVEL_UNKNOWN;
    }

    /**
     * Write to the system log.
     * log(message) - info
     * log(message, int) 0 - 5
     *
     * @param polyad
     * @param state
     */
    private void doSysLog(Polyad polyad, State state, boolean isDebug) {
        int currentIntLevel = LOG_LEVEL_NONE;
        if (isDebug) {
            currentIntLevel = state.getDebugUtil().getDebugLevel();
        } else {
            if (state.getLogger() != null) {
                currentIntLevel = getMyLogLevel(state.getLogger().getLogger().getLevel());
            }
        }
        Long currentLongLevel = new Long((long) currentIntLevel);

        if (polyad.getArgCount() == 0 || state.getLogger() == null) {
            polyad.setResult(currentLongLevel);
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        int newLogLevel = currentIntLevel;

        Object arg0 = polyad.evalArg(0, state);
        checkNull(arg0, polyad.getArgAt(0), state);
        String message = null;

        if (polyad.getArgCount() == 1) {
            if (isLong(arg0)) {
                // Cannot reset logging levels in server mode or server loses control of logging
                if (!state.isRestrictedIO()) {
                    // Then they are setting the logging level
                    if (!isValidLoggingLevel((Long) arg0)) {
                        throw new IllegalArgumentException("error: unknown logging level of " + arg0 + " encountered.");
                    }
                    newLogLevel = ((Long) arg0).intValue();

                    if (isDebug) {
                        state.getDebugUtil().setDebugLevel(newLogLevel);
                    } else {
                        state.getLogger().getLogger().setLevel(getLogLevel(newLogLevel)); // look up Java log level
                    }
                }
                polyad.setResult(currentLongLevel);
                polyad.setResultType(Constant.LONG_TYPE);
                polyad.setEvaluated(true);
                return;
            }
            // Use whatever the current level is at as the default for the message
            if (isString(arg0)) {
                message = (String) arg0;
                newLogLevel = LOG_LEVEL_INFO; // default if nothing supplied
            }
        }


        if (polyad.getArgCount() == 2) {
            // then the arguments are 0 is the level, 1 is the message
            Object arg1 = polyad.evalArg(1, state);
            checkNull(arg1, polyad.getArgAt(1), state);
            if (isLong(arg0)) {
                newLogLevel = ((Long) arg0).intValue();
                if (!isValidLoggingLevel((Long) arg0)) {
                    throw new IllegalArgumentException("error: unknown logging level of " + arg0 + " encountered.");
                }

            } else {
                // Now it will just fall through and print the message to the default level.
                // Used to do something else here. Changed contract. Still do something else??
                /*String msg = "requested logging level \"" + arg0 + "\"is unknown.";
                if (isDebug) {
                    DebugUtil.info(QDLWorkspace.class, msg);
                } else {
                    state.getLogger().info(msg);
                }*/
            }
            message = arg1.toString();
        }

        Boolean ok = Boolean.TRUE;
        switch (newLogLevel) {
            case LOG_LEVEL_NONE:
                // do nothing.
                break;
            case LOG_LEVEL_TRACE:
                if (isDebug) {
                    state.getDebugUtil().trace(QDLWorkspace.class, message);
                } else {
                    state.getLogger().debug(message);
                }
                break;
            case LOG_LEVEL_INFO:
                if (isDebug) {
                    state.getDebugUtil().info(QDLWorkspace.class, message);
                } else {
                    state.getLogger().info(message);
                }
                break;
            case LOG_LEVEL_WARN:
                if (isDebug) {
                    state.getDebugUtil().warn(QDLWorkspace.class, message);
                } else {
                    state.getLogger().warn(message);
                }
                break;
            case LOG_LEVEL_ERROR:
                if (isDebug) {
                    state.getDebugUtil().error(QDLWorkspace.class, message);
                } else {
                    state.getLogger().error(message);
                }
                break;
            case LOG_LEVEL_SEVERE:
                if (isDebug) {
                    state.getDebugUtil().severe(QDLWorkspace.class, message);
                } else {
                    state.getLogger().warn(message); // no other options
                }
                break;

            default:
                ok = Boolean.FALSE;
        }
        polyad.setResult(ok);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);

        //    state.getLogger().
    }

    protected void doModulePaths(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.STEM_TYPE);
            StemVariable stemVariable = new StemVariable();
            if (state.getModulePaths().isEmpty()) {
                polyad.setResult(stemVariable);
                return;
            }
            ArrayList<Object> sp = new ArrayList<>();
            sp.addAll(state.getModulePaths());
            stemVariable.addList(sp);
            polyad.setResult(stemVariable);
            return;
        }
        if (polyad.getArgCount() == 1) {
            Object obj = polyad.evalArg(0, state);
            checkNull(obj, polyad.getArgAt(0), state);
            if (isString(obj)) {
                state.setModulePaths(obj.toString());
                polyad.setEvaluated(true);
                polyad.setResult(Boolean.TRUE);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                return;
            }
            if (!isStem(obj)) {
                throw new IllegalArgumentException(MODULE_PATH + " requires a stem as its argument.");
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
            state.setModulePaths(sp);
            polyad.setEvaluated(true);
            polyad.setResult(Boolean.TRUE);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            return;
        }
        throw new IllegalArgumentException(SCRIPT_PATH_COMMAND + " requires at most one argument, not " + polyad.getArgCount());

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
            checkNull(obj, polyad.getArgAt(0), state);
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
            checkNull(obj, polyad.getArgAt(0), state);
            if (!isLong(obj)) {
                throw new IllegalArgumentException(SCRIPT_ARGS_COMMAND + " requires an integer argument.");
            }
            int index = ((Long) obj).intValue();
            if (index == -1L) {
                StemVariable args = new StemVariable();
                for (Object object : state.getScriptArgs()) {
                    args.listAppend(object);
                }
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.STEM_TYPE);
                polyad.setResult(args);
                return;
            }
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
            checkNull(obj, polyad.getArgAt(i));
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
        checkNull(obj, polyad.getArgAt(0), state);
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
     * <b>NOTE:</b> First resolution wins. <br/>
     * <b>NOTE:</b> If this returns a null, then there is no such script anywhere.
     *
     * @param name
     * @param state
     * @return
     */
    public static QDLScript resolveScript(String name, List<String> paths, State state) throws Throwable {
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
                return new QDLScript(QDLFileUtil.readFileAsLines(name), null);
            }
            // so its relative.
            for (String p : paths) {
                if (!p.contains(SCHEME_DELIMITER)) {
                    File test = new File(p, name);
                    if (test.exists() && test.isFile() && test.canRead()) {
                        return new QDLScript(QDLFileUtil.readFileAsLines(test.getCanonicalPath()), null);
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
            for (String p : paths) {
                if (p.startsWith(caput)) {
                    DebugUtil.trace(ControlEvaluator.class, " trying path = " + p + tempName);

                    QDLScript q = state.getScriptFromVFS(p + tempName);
                    if (q != null) {
                        DebugUtil.trace(ControlEvaluator.class, " got path = " + p + tempName);

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
                    return new QDLScript(QDLFileUtil.readFileAsLines(name), null);
                }
            }
            for (String p : paths) {
                String resourceName = p + name;
                DebugUtil.trace(ControlEvaluator.class, " path = " + resourceName);
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
                    testFile = new File(resourceName);
                    if (testFile.exists() && testFile.isFile() && testFile.canRead()) {
                        return new QDLScript(QDLFileUtil.readFileAsLines(testFile.getCanonicalPath()), null);
                    }
                }
            }
        }
        return null;
    }

    public static void runnit(Polyad polyad, State state, String commandName, boolean hasNewState) {
        runnit(polyad, state, commandName, state.getScriptPaths(), hasNewState);
    }

    public static void runnit(Polyad polyad, State state, String commandName, List<String> paths, boolean hasNewState) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException("The " + commandName + " requires at least a single argument");
        }
        State localState = state;
        if (hasNewState) {
            localState = state.newModuleState();
        }

        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0), state);
        Object[] argList = new Object[0];
/*        if (2 == polyad.getArgCount()) {
            Object arg2 = polyad.evalArg(1, state);
            if (arg2 instanceof StemVariable) {
                StemList stemList = ((StemVariable) arg2).getStemList();
                ArrayList<Object> aa = new ArrayList<>();
                for (int i = 0; i < stemList.size(); i++) {
                    Object object = stemList.get(i);
                    if (object != null && !(object instanceof QDLNull)) {
                        aa.add(object);
                    }
                }
                argList = aa.toArray(new Object[0]);
            } else {
                argList = new Object[]{arg2}; // pass back single non-stem argument
            }
        }*/
        if (2 <= polyad.getArgCount()) {
            ArrayList<Object> aa = new ArrayList<>();
            // zero-th argument is the name of the script, so start with element 1.
            for (int i = 1; i < polyad.getArgCount(); i++) {
                Object arg = polyad.evalArg(i, state);
                checkNull(arg, polyad.getArgAt(i), state);
                aa.add(arg);
            }
            argList = aa.toArray(new Object[0]);
        }
        String resourceName = arg1.toString();
        QDLScript script;
        try {
            script = resolveScript(resourceName, paths, state);
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
                if (t instanceof ReturnException) {
                    ReturnException rx = (ReturnException) t;
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
            checkNull(arg2, polyad.getArgAt(1), state);
            if (isLong(arg2)) {
                state.getSymbolStack().setLongValue("error_code", (Long) arg2);
            }

        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
        // set the message in the exception to the message from the error, so if it happens in the course of execution
        // they get a message
        throw new RaiseErrorException(polyad, arg1.toString());
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
                checkNull(r, polyad.getArgAt(0), state);
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

    public final static String MODULE_TYPE_JAVA = "java";
    public final static String MODULE_DEFAULT_EXTENSION = ".mdl";

    protected void doLoadModule(Polyad polyad, State state) {
        doNewLoadModule(polyad, state);
    }

    protected void doNewLoadModule(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLServerModeException("reading files is not supported in server mode");
        }
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException(MODULE_LOAD + " requires at least one argument");
        }
        Object arg = polyad.evalArg(0, state);

        if (arg == QDLNull.getInstance()) {
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(true);
            return;
        }

        StemVariable argStem = convertArgsToStem(polyad, arg, state, MODULE_LOAD);
        StemVariable outStem = new StemVariable();
        for (String key : argStem.keySet()) {
            Object value = argStem.get(key);
            int loadTarget = LOAD_FILE;
            String resourceName = null;
            if (isString(value)) {
                resourceName = (String) value;
            } else {
                StemVariable q = (StemVariable) value;
                resourceName = q.get(0L).toString();
                loadTarget = q.get(1L).toString().equals(MODULE_TYPE_JAVA) ? LOAD_JAVA : LOAD_FILE;

            }
            List<String> loadedQNames = null;
            if (loadTarget == LOAD_JAVA) {
                loadedQNames = doJavaModuleLoad(state, resourceName);


            } else {
                loadedQNames = doQDLModuleLoad(state, resourceName);

            }
            Object newEntry = null;
            if (loadedQNames == null || loadedQNames.isEmpty()) {
                newEntry = QDLNull.getInstance();
            } else {
                if (loadedQNames.size() == 1) {
                    newEntry = loadedQNames.get(0);
                } else {
                    StemVariable innerStem = new StemVariable();
                    innerStem.addList(loadedQNames);
                    newEntry = innerStem;
                }
            }
            outStem.put(key, newEntry);
        }
        polyad.setEvaluated(true);
        if (outStem.size() == 1) {


            polyad.setResult(outStem.get(outStem.keySet().iterator().next()));
            polyad.setResultType(Constant.STRING_TYPE);
        } else {
            polyad.setResult(outStem);
            polyad.setResultType(Constant.STEM_TYPE);
        }
    }

    /**
     * Load a single java module, returning a null if it failed or the FQ name if it worked.
     *
     * @param state
     * @param resourceName
     */
    private List<String> doJavaModuleLoad(State state, String resourceName) {
        try {
            Class klasse = state.getClass().forName(resourceName);
            Object newThingy = klasse.newInstance();
            QDLLoader qdlLoader;
            if (newThingy instanceof JavaModule) {
                // For a single instance, just create a barebones loader on the fly.
                qdlLoader = new QDLLoader() {
                    @Override
                    public List<Module> load() {
                        List<Module> m = new ArrayList<>();
                        JavaModule javaModule = (JavaModule) newThingy;
                        State newState = state.newModuleState();
                        javaModule = (JavaModule) javaModule.newInstance(newState);
                        javaModule.init(newState); // set it up
                        m.add(javaModule);
                        return m;
                    }
                };
            } else {
                if (!(newThingy instanceof QDLLoader)) {
                    throw new IllegalArgumentException("Error: \"" + resourceName + "\" is neither a module nor a loader.");
                }
                qdlLoader = (QDLLoader) newThingy;
            }
            List<String> names = QDLConfigurationLoaderUtils.setupJavaModule(state, qdlLoader, false);
            if (names.isEmpty()) {
                return null;
            }
            return names;
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Could not load Java class " + resourceName + ":" + t.getMessage() + ". Be sure it is in the classpath.", t);
        }
    }

    /**
     * Load a the module(s) in a single resource.
     *
     * @param state
     * @param resourceName
     * @return
     */
    private List<String> doQDLModuleLoad(State state, String resourceName) {
        QDLScript script = null;
        try {
            script = resolveScript(resourceName, state.getModulePaths(), state);
            if (script == null) {
                script = resolveScript(resourceName + MODULE_DEFAULT_EXTENSION, state.getModulePaths(), state);
            }
        } catch (Throwable t) {
            state.warn("Could not find module:" + t.getMessage());
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLRuntimeException("Could not find  '" + resourceName + "'. Is your module path set?", t);
        }

        File file = null;

        if (script == null) {
            file = new File(resourceName);
            if (!file.exists()) {
                throw new IllegalArgumentException("file, '" + file.getAbsolutePath() + "' does not exist");
            }
            if (!file.isFile()) {
                throw new IllegalArgumentException("'" + file.getAbsolutePath() + "' is not a file");
            }

            if (!file.canRead()) {
                throw new IllegalArgumentException("You do not have permission to read '" + file.getAbsolutePath() + "'.");
            }

        }
        try {
            QDLParserDriver parserDriver = new QDLParserDriver(new XProperties(), state);
            // Exceptional case where we just run it directly.
            // note that since this is QDL there may be multiple modules, etc.
            // in a single file, so there is no way to know what the user did except
            // to look at the state before, then after. This should return the added
            // modules fq paths.
            state.getModuleMap().clearChangeList();
/*
            List<String> b4load = new ArrayList<>();
            for (URI uri : state.getModuleMap().keySet()) {
                b4load.add(uri.toString());
            }
*/
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
            List<String> afterLoad = new ArrayList<>();
            for (URI uri : state.getModuleMap().getChangeList()) {
                    afterLoad.add(uri.toString());
            }
            state.getModuleMap().clearChangeList();
            return afterLoad;
        } catch (Throwable t) {
            if (DebugUtil.isEnabled()) {
                t.printStackTrace();
            }
        }
        return null;
    }

    protected void doOldLoadModule(Polyad polyad, State state) {
        if (state.isServerMode()) {
            throw new QDLServerModeException("reading files is not supported in server mode");
        }
        if (0 == polyad.getArgCount() || 3 <= polyad.getArgCount()) {
            throw new IllegalArgumentException("Error" + MODULE_LOAD + " requires one or two arguments.");
        }
        int loadTarget = LOAD_FILE;
        if (polyad.getArgCount() == 2) {
            // Then this is probably a Java module
            Object arg2 = polyad.evalArg(1, state);
            checkNull(arg2, polyad.getArgAt(1), state);
            if (isString(arg2)) {
                loadTarget = arg2.toString().equals(MODULE_TYPE_JAVA) ? LOAD_JAVA : LOAD_FILE;
            } else {
                throw new IllegalArgumentException(MODULE_LOAD + " requires a string as a second argument");
            }

        }
        Object arg = polyad.evalArg(0, state);
        checkNull(arg, polyad.getArgAt(0), state);

        if (!isString(arg)) {
            throw new IllegalArgumentException(MODULE_LOAD + " requires a string as its argument, not '" + arg + "'");
        }
        String resourceName = arg.toString();
        if (loadTarget == LOAD_JAVA) {
            // first arg is the class name.
            try {
                Class klasse = state.getClass().forName(resourceName);
                Object newThingy = klasse.newInstance();
                QDLLoader qdlLoader;
                if (newThingy instanceof JavaModule) {
                    // For a single instance, just create a barebones loader on the fly.
                    qdlLoader = new QDLLoader() {
                        @Override
                        public List<Module> load() {
                            List<Module> m = new ArrayList<>();
                            JavaModule javaModule = (JavaModule) newThingy;
                            State newState = state.newModuleState();
                            javaModule = (JavaModule) javaModule.newInstance(newState);
                            javaModule.init(newState); // set it up
                            m.add(javaModule);
                            return m;
                        }
                    };
                } else {
                    if (!(newThingy instanceof QDLLoader)) {
                        throw new IllegalArgumentException("Error: \"" + resourceName + "\" is neither a module nor a loader.");
                    }
                    qdlLoader = (QDLLoader) newThingy;
                }
                List<String> names = QDLConfigurationLoaderUtils.setupJavaModule(state, qdlLoader, false);
                switch (names.size()) {
                    case 0:
                        polyad.setResultType(Constant.NULL_TYPE);
                        polyad.setResult(QDLNull.getInstance());
                        break;
                    case 1:
                        polyad.setResultType(Constant.STRING_TYPE);
                        polyad.setResult(names.get(0));
                        break;
                    default:
                        StemVariable stemVariable = new StemVariable();
                        stemVariable.addList(names);
                        polyad.setResult(names);
                        polyad.setResultType(Constant.STEM_TYPE);

                }
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
        try {
            script = resolveScript(resourceName, state.getModulePaths(), state);
            if (script == null) {
                script = resolveScript(resourceName + MODULE_DEFAULT_EXTENSION, state.getModulePaths(), state);
            }
        } catch (Throwable t) {
            state.warn("Could not find module:" + t.getMessage());
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLRuntimeException("Could not find  '" + resourceName + "'. Is your module path set?", t);
        }
  /*      if (script == null) {
            throw new QDLRuntimeException("Could not find  '" + resourceName + "'. Is your module path set?");
        }*/

        File file = null;

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
            // note that since this is QDL ther emay be multiple modules, etc.
            // in a single file, so there is no way to know what the user did except
            // to look at the state before, then after. This should return the added
            // modules fq paths.
            List<String> b4load = new ArrayList<>();
            for (URI uri : state.getModuleMap().keySet()) {
                b4load.add(uri.toString());
            }
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
            List<String> afterLoad = new ArrayList<>();
            for (URI uri : state.getModuleMap().keySet()) {
                String s = uri.toString();
                if (!b4load.contains(s)) {
                    afterLoad.add(s);
                }
            }
            switch (afterLoad.size()) {
                case 0:
                    polyad.setResultType(Constant.NULL_TYPE);
                    polyad.setResult(QDLNull.getInstance());
                    break;
                case 1:
                    polyad.setResultType(Constant.STRING_TYPE);
                    polyad.setResult(afterLoad.get(0));
                    break;
                default:
                    StemVariable stemVariable = new StemVariable();
                    stemVariable.addList(afterLoad);
                    polyad.setResult(afterLoad);
                    polyad.setResultType(Constant.STEM_TYPE);

            }
            polyad.setEvaluated(true);
        } catch (Throwable t) {
            if (DebugUtil.isEnabled()) {
                t.printStackTrace();
            }
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setResult(QDLNull.getInstance());
            polyad.setEvaluated(true);
        }
    }

    /**
     * The general contract for import. You may import everything in a module by using the uri.
     * You may import items using the FQ name (uri + # + name). <br/><br/>
     * Imported items are added to the current stack only. it is not possible to un-import items since
     * it is impossible to track down references to them and it is dangerous to have programs be able
     * to unload things willy-nilly since that can cause hard to track down failures.<br/><br/>
     * If you need to unload, your best bet is to clear the workspace.
     *
     * @param polyad
     */
    protected void doModuleImport(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException("Error" + MODULE_IMPORT + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        checkNull(arg, polyad.getArgAt(0), state);
        if (arg == QDLNull.getInstance()) {
            // case that user tries to import q QDL null module. This can happen is
            // the load fails. Don't have it as an error, return QDl null to show nothing
            // happened (so user can check with conditional).
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable argStem = convertArgsToStem(polyad, arg, state, MODULE_IMPORT);
        boolean gotOne;
        // The arguments has been regularized so it is a stem of lists of the form
        // [[fq0{,alias0}],[fq1{,alias1}],...,[fqn{,aliasn}]]
        // {,alias} means that may or may not be present. If missing or null, use default.
        // If there is no aliasj, then [fqj] can be replaced with fqj,
        // E.g.
        // [['fq:0','alias0'],'fq:1',['fq:2'],['fq:3','alias3']]
        // should work
        gotOne = false;
        StemVariable outputStem = new StemVariable();
        for (Object key : argStem.keySet()) {
            Object value = argStem.get(key);
            URI moduleNS = null;
            String alias = null;
            if (isString(value)) {
                moduleNS = URI.create((String) value); // if this bombs it throws an illega argument exception, which is ok.
                gotOne = true;
            }
            if (isStem(value)) {
                StemVariable innerStem = (StemVariable) value;
                Object q = innerStem.get(0L);

                if (!isString(q)) {
                    throw new IllegalArgumentException(MODULE_IMPORT + ": the fully qualified name must be a string");
                }
                moduleNS = URI.create((String) q);

                if (innerStem.containsKey(1L)) {
                    q = innerStem.get(1L);
                    if (!isString(q)) {
                        throw new IllegalArgumentException(MODULE_IMPORT + ": the alias for \"" + moduleNS + " must be a string");
                    }
                    alias = (String) q;
                }
                gotOne = true;
            }
            if (!gotOne) {
                throw new IllegalArgumentException(MODULE_IMPORT + ": unknown argument type");
            }
            Module m = state.getModuleMap().get(moduleNS);
            if (m == null) {
                throw new IllegalStateException("no such module '" + moduleNS + "'");
            }

            State newModuleState = state.newModuleState();
            Module newInstance = m.newInstance(newModuleState);
            if (newInstance instanceof JavaModule) {
                ((JavaModule) newInstance).init(newModuleState);
            }
            if (alias == null) {
                alias = m.getAlias();
            }
            ImportManager resolver = state.getImportManager();
            resolver.addImport(moduleNS, alias);
            state.getImportedModules().put(alias, newInstance);
            if (isLong(key)) {
                outputStem.put((Long) key, alias);
            } else {
                outputStem.put((String) key, alias);
            }

        }
        switch (outputStem.size()) {
            case 0:
                polyad.setResult(QDLNull.getInstance());
                polyad.setResultType(Constant.NULL_TYPE);
                break;
            case 1:
                polyad.setResult(outputStem.get(0L));
                polyad.setResultType(Constant.STRING_TYPE);
                break;
            default:
                polyad.setResult(outputStem);
                polyad.setResultType(Constant.STEM_TYPE);
        }
        polyad.setEvaluated(true);

    }

    /**
     * Converts a couple of different arguments to the form
     * [[a0{,b0}],[a1{,b1}],...,[an{,bn}] or (if a single argument that is
     * a stem) can pass back:
     * <p>
     * {key0:[[a0{,b0}], key1:[a1{,b1}],...}
     * <p>
     * where the bk are optional. All ak, bk are strings.
     * a,b -> [[a,b]] (pair of arguments, function is dyadic
     * [a,b] ->[[a,b]] (simple list, convert to nested list
     * [a0,a1,...] -> [[a0],[a1],...] allow for scalars
     * Use in both module import and load for consistent arguments
     *
     * @param polyad
     * @param state
     * @param component
     * @return
     */
    private StemVariable convertArgsToStem(Polyad polyad, Object arg, State state, String component) {
//        Object arg = polyad.evalArg(0, state);
        StemVariable argStem = null;

        boolean gotOne = false;

        switch (polyad.getArgCount()) {
            case 0:
                throw new IllegalArgumentException(component + " requires an argument");
            case 1:
                // single string arguments
                if (isString(arg)) {
                    argStem = new StemVariable();
                    argStem.listAppend(arg);
                    gotOne = true;
                }
                if (isStem(arg)) {
                    argStem = (StemVariable) arg;
                    gotOne = true;
                }
                break;
            case 2:
                if (!isString(arg)) {
                    throw new IllegalArgumentException("Dyadic " + component + " requires string arguments only");
                }
                Object arg2 = polyad.evalArg(1, state);
                checkNull(arg2, polyad.getArgAt(1), state);
                if (!isString(arg2)) {
                    throw new IllegalArgumentException("Dyadic " + component + " requires string arguments only");
                }

                argStem = new StemVariable();
                StemVariable innerStem = new StemVariable();
                innerStem.listAppend(arg);
                innerStem.listAppend(arg2);
                argStem.put(0L, innerStem);
                gotOne = true;
                break;
            default:
                throw new IllegalArgumentException(component + ": too many arguments");
        }
        if (!gotOne) {
            throw new IllegalArgumentException(component + ": unknown argument type");
        }
        return argStem;
    }

    protected void doExecute(Polyad polyad, State state) {
        // execute a string.
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("Error. Wrong number of arguments. " +
                    "This requires a single argument that is a string or a list of them.");
        }
        Object result = polyad.evalArg(0, state);
        checkNull(result, polyad.getArgAt(0), state);
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


        QDLInterpreter p = new QDLInterpreter(new XProperties(), state);
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
