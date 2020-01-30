package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.parsing.QDLParserDriver;
import edu.uiuc.ncsa.qdl.parsing.QDLRunner;
import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.security.core.configuration.XProperties;

import java.io.File;
import java.io.FileReader;
import java.net.URI;

/**
 * For ocntrol structure in loops, conditionals etc.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  11:49 AM
 */
public class ControlEvaluator extends AbstractFunctionEvaluator {
    public static final int CONTROL_BASE_VALUE = 5000;
    // Looping stuff
    public static String CONTINUE = "continue";
    public static final int CONTINUE_TYPE = 1 + CONTROL_BASE_VALUE;

    public static String BREAK = "break";
    public static final int BREAK_TYPE = 2 + CONTROL_BASE_VALUE;

    public static String FOR_KEYS = "for_keys";
    public static final int FOR_KEYS_TYPE = 3 + CONTROL_BASE_VALUE;

    public static String FOR_NEXT = "for_next";
    public static final int FOR_NEXT_TYPE = 4 + CONTROL_BASE_VALUE;

    public static String CHECK_AFTER = "check_after";
    public static final int CHECK_AFTER_TYPE = 5 + CONTROL_BASE_VALUE;


    // function stuff
    public static String RETURN = "return";
    public static final int RETURN_TYPE = 100 + CONTROL_BASE_VALUE;


    public static String IMPORT = "import";
    public static final int IMPORT_TYPE = 203 + CONTROL_BASE_VALUE;

    /*  public static String SET_ALIAS = "set_alias";
      public static final int SET_ALIAS_TYPE = 204 + CONTROL_BASE_VALUE;
  */
    public static String LOAD_MODULE = "load_module";
    public static final int LOAD_MODULE_TYPE = 205 + CONTROL_BASE_VALUE;

    // try ... catch

    public static String RAISE_ERROR = "raise_error";
    public static final int RAISE_ERROR_TYPE = 300 + CONTROL_BASE_VALUE;

    // For external programs

    public static String RUN_COMMAND = "run_script";
    public static final int RUN_COMMAND_TYPE = 400 + CONTROL_BASE_VALUE;

    public static String LOAD_COMMAND = "load_script";
    public static final int LOAD_COMMAND_TYPE = 401 + CONTROL_BASE_VALUE;


    @Override
    public int getType(String name) {
        if (name.equals(CONTINUE)) return CONTINUE_TYPE;
        if (name.equals(BREAK)) return BREAK_TYPE;
        if (name.equals(RETURN)) return RETURN_TYPE;
        // NOTE NOTE NOTE!!! The next 3 functions are NOT evaluated here. Their type is set and in
        // the WhileLoop class they are picked apart for their contents and the correct looping strategy is
        // done.
        if (name.equals(FOR_KEYS)) return FOR_KEYS_TYPE;
        if (name.equals(FOR_NEXT)) return FOR_NEXT_TYPE;
        if (name.equals(CHECK_AFTER)) return CHECK_AFTER_TYPE;
        // Module stuff
        if (name.equals(IMPORT)) return IMPORT_TYPE;
        if (name.equals(LOAD_MODULE)) return LOAD_MODULE_TYPE;

        if (name.equals(RAISE_ERROR)) return RAISE_ERROR_TYPE;
        if (name.equals(RUN_COMMAND)) return RUN_COMMAND_TYPE;
        if (name.equals(LOAD_COMMAND)) return LOAD_COMMAND_TYPE;
        return EvaluatorInterface.UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getOperatorType()) {
            case BREAK_TYPE:
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                throw new BreakException();
            case CONTINUE_TYPE:
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                throw new ContinueException();
            case RETURN_TYPE:
                doReturn(polyad, state);
                return true;
            case RUN_COMMAND_TYPE:
                runScript(polyad, state);
                return true;
            case LOAD_COMMAND_TYPE:
                loadScript(polyad, state);
                return true;

            case IMPORT_TYPE:
                doImport(polyad, state);
                return true;
           /* case SET_ALIAS_TYPE:
                doSetAlias(polyad, state);
                return true;*/
            case LOAD_MODULE_TYPE:
                doLoadModule(polyad, state);
                return true;
            case RAISE_ERROR_TYPE:
                doRaiseError(polyad, state);
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

        Object arg1 = polyad.evalArg(0,state);;
        try {
            QDLParser interpreter = new QDLParser(localState);
            interpreter.execute(FileUtil.readFileAsString(arg1.toString()));
        } catch (Throwable t) {
            throw new QDLRuntimeException("Error running script \"" + arg1 + "\"", t);
        }
    }

    protected void doRaiseError(Polyad polyad, State state) {
        if (polyad.getArgumments().size() == 0) {
            throw new IllegalArgumentException("Error:" + RAISE_ERROR + " requires at least a single argument");
        }
        Object arg1 = polyad.evalArg(0,state);;
        if (arg1 == null) {
            arg1 = "(no message)";
        }
        Object arg2 = null;
        state.setValue("error_message", arg1.toString());
        if (polyad.getArgumments().size() == 2) {
            arg2 = polyad.evalArg(1,state);;
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
                polyad.setResult(null);
                polyad.setResultType(Constant.NULL_TYPE);
                break;
            case 1:
                Object r = polyad.evalArg(0,state);;
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


    protected void doLoadModule(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error" + LOAD_MODULE + " requires a single argument. The full path to the module's file.");
        }
        Object arg = polyad.evalArg(0,state);;
        if (!isString(arg)) {
            throw new IllegalArgumentException("Error: The " + LOAD_MODULE + " command requires a string as its argument, not \"" + arg + "\"");
        }
        File file = new File(arg.toString());
        if (!file.exists()) {
            throw new IllegalArgumentException("Error: file, \"" + arg + "\" does not exist");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Error: \"" + arg + "\" is not a file");
        }

        if (!file.canRead()) {
            throw new IllegalArgumentException("Error: You do not have permission to read \"" + arg + "\".");
        }
        try {
            FileReader fileReader = new FileReader(file);
            QDLParserDriver parserDriver = new QDLParserDriver(new XProperties(), state);
            // Exceptional case where we just run it directly.
            QDLRunner runner = new QDLRunner(parserDriver.parse(fileReader));
            runner.setState(state);
            runner.run();
            //parserDriver.execute(fileReader);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setResult(Boolean.TRUE);
            polyad.setEvaluated(true);
        } catch (Throwable t) {
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setResult(Boolean.FALSE);
            polyad.setEvaluated(true);
            //throw new GeneralException("Error: could not load file \"" + arg + "\"", t);
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
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error" + IMPORT + " requires an argument");
        }
        Object arg = polyad.evalArg(0,state);;
        if (arg == null) {
            throw new MissingArgumentException("Error: You must supply a module name to import.");
        }
        String alias = null;
        if (polyad.getArgumments().size() == 2) {
            Object arg2 = polyad.evalArg(0,state);;
            if (arg2 == null || !isString(arg2)) {
                throw new MissingArgumentException("Error: You must supply a valid alias import.");
            }
            alias = arg2.toString();
        }

        URI moduleName = null;
        try {
            moduleName = URI.create(arg.toString());
        } catch (Throwable t) {
            polyad.setResult(null);
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(false);
            throw new QDLException("Error: the module name must be a uri");
        }

        if (!state.getModuleMap().containsKey(moduleName)) {
            throw new ImportException("Error: module \"" + moduleName + "\" not found. You must load it before importing it.");
        }
        Module module = state.getModuleMap().get(moduleName);

        NamespaceResolver resolver = state.getResolver();
        if (alias == null) {
            // No alias specified, so just import it with its default alias.
            resolver.addImport(moduleName, module.getAlias());
        } else {
            resolver.addImport(moduleName, alias);
        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }
}
