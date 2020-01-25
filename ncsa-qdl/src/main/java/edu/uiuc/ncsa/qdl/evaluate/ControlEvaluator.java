package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.QDLParserDriver;
import edu.uiuc.ncsa.qdl.exceptions.RaiseErrorException;
import edu.uiuc.ncsa.qdl.exceptions.ReturnException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.parsing.QDLRunner;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

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

    public static String HAS_KEYS = "has_keys";
    public static final int HAS_KEYS_TYPE = 3 + CONTROL_BASE_VALUE;

    public static String FOR_NEXT = "for_next";
    public static final int FOR_NEXT_TYPE = 4 + CONTROL_BASE_VALUE;

    public static String CHECK_AFTER = "check_after";
    public static final int CHECK_AFTER_TYPE = 5 + CONTROL_BASE_VALUE;


    // function stuff
    public static String RETURN = "return";
    public static final int RETURN_TYPE = 100 + CONTROL_BASE_VALUE;


    // Module stuff
  /*  public static String CREATE_MODULE = "create_module";
    public static final int CREATE_MODULE_TYPE = 200 + CONTROL_BASE_VALUE;

    public static String BEGIN_MODULE = "begin_module";
    public static final int BEGIN_MODULE_TYPE = 201 + CONTROL_BASE_VALUE;

    public static String END_MODULE = "end_module";
    public static final int END_MODULE_TYPE = 202 + CONTROL_BASE_VALUE;
*/
    public static String IMPORT = "import";
    public static final int IMPORT_TYPE = 203 + CONTROL_BASE_VALUE;

    public static String SET_ALIAS = "set_alias";
    public static final int SET_ALIAS_TYPE = 204 + CONTROL_BASE_VALUE;

    public static String LOAD_MODULE = "load_module";
    public static final int LOAD_MODULE_TYPE = 205 + CONTROL_BASE_VALUE;

    // try ... catch

    public static String RAISE_ERROR = "raise_error";
    public static final int RAISE_ERROR_TYPE = 300 + CONTROL_BASE_VALUE;

   /* public static String DROP_MODULE = "drop_module";
    public static final int DROP_MODULE_TYPE = 206 + CONTROL_BASE_VALUE;*/

    @Override
    public int getType(String name) {
        if (name.equals(CONTINUE)) return CONTINUE_TYPE;
        if (name.equals(BREAK)) return BREAK_TYPE;
        if (name.equals(RETURN)) return RETURN_TYPE;
        // NOTE NOTE NOTE!!! The next 3 functions are NOT evaluated here. Their type is set and in
        // the WhileLoop class they are picked apart for their contents and the correct looping strategy is
        // done.
        if (name.equals(HAS_KEYS)) return HAS_KEYS_TYPE;
        if (name.equals(FOR_NEXT)) return FOR_NEXT_TYPE;
        if (name.equals(CHECK_AFTER)) return CHECK_AFTER_TYPE;
        //   if (name.equals(CREATE_MODULE)) return CREATE_MODULE_TYPE;
        if (name.equals(IMPORT)) return IMPORT_TYPE;
//        if (name.equals(BEGIN_MODULE)) return BEGIN_MODULE_TYPE;
  //      if (name.equals(END_MODULE)) return END_MODULE_TYPE;
        if (name.equals(SET_ALIAS)) return SET_ALIAS_TYPE;
        //   if (name.equals(DROP_MODULE)) return DROP_MODULE_TYPE;
        if (name.equals(LOAD_MODULE)) return LOAD_MODULE_TYPE;
        if (name.equals(RAISE_ERROR)) return RAISE_ERROR_TYPE;
        return EvaluatorInterface.UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getOperatorType()) {
            case BREAK_TYPE:
            case CONTINUE_TYPE:
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(Boolean.TRUE);
                return true;
            case RETURN_TYPE:
                doReturn(polyad, state);
                return true;
    /*        case BEGIN_MODULE_TYPE:
                doBeginModule(polyad, state);
                return true;
            case END_MODULE_TYPE:
                doEndModule(polyad, state);
                return true;
    */        case IMPORT_TYPE:
                doImport(polyad, state);
                return true;
            case SET_ALIAS_TYPE:
                doSetAlias(polyad, state);
                return true;
            case LOAD_MODULE_TYPE:
                if (isServerMode()) return true;
                doLoadModule(polyad, state);
                return true;
            case RAISE_ERROR_TYPE:
                doRaiseError(polyad, state);
                return true;
        }
        return false;
    }

    protected void doRaiseError(Polyad polyad, State state) {
        if (polyad.getArgumments().size() == 0) {
            throw new IllegalArgumentException("Error:" + RAISE_ERROR + " requires at least a single argument");
        }
        Object arg1 = polyad.getArgumments().get(0).evaluate(state);
        if (arg1 == null) {
            arg1 = "(no message)";
        }
        Object arg2 = null;
        state.getSymbolStack().setStringValue("error_message", arg1.toString());
        if (polyad.getArgumments().size() == 2) {
            arg2 = polyad.getArgumments().get(1).evaluate(state);
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
                Object r = polyad.getArgumments().get(0).evaluate(state);
                polyad.setResult(r);
                polyad.setResultType(polyad.getArgumments().get(0).getResultType());
                polyad.setEvaluated(true);
                break;
            default:
                throw new IllegalArgumentException("Error. Wrong number of arguments. You can only return at most a single value");
        }

        // Magic. Sort of. Since it is impossible to tell when or how a function will invoke its
        // return, we throw an exception and catch it at the right time.
        ReturnException rx =new ReturnException();
        rx.result = polyad.getResult();
        rx.resultType = polyad.getResultType();
        throw rx;
    }

   /* protected void doBeginModule(Polyad polyad, State state) {
        if (!(0 < polyad.getArgumments().size() && polyad.getArgumments().size() < 3)) {
            throw new IllegalArgumentException("Error: Wrong number of arguments. The " + BEGIN_MODULE +
                    " command requires a namespace and an optional alias. You supplied " + polyad.getArgumments().size());
        }
        Object arg1 = null;
        Object arg2 = null;
        String namespace = null;
        String alias = null;
        switch (polyad.getArgumments().size()) {
            case 2:
                arg2 = polyad.getArgumments().get(1).evaluate(state);
                if (!isString(arg2)) throw new IllegalArgumentException("Error: The alias must be a string");
                alias = arg2.toString();
            case 1:
                arg1 = polyad.getArgumments().get(0).evaluate(state);
                if (!isString(arg1)) throw new IllegalArgumentException("Error: The namespace must be a string");
                namespace = arg1.toString();
                break;
        }

        Module module = null;
        if (alias != null && state.getModuleMap().containsKey(namespace)) {
            throw new IllegalArgumentException("Error: You cannot set the alias if the module has been defined. Use the set_alias command.");
        }
        if (!state.getModuleMap().containsKey(namespace)) {
            if (alias != null && !alias.isEmpty()) {
                module = new Module(new SymbolTableImpl(
                        state.getResolver()),
                        state.getResolver(),
                        namespace);

            } else {
                module = new Module(new SymbolTableImpl(
                        state.getResolver()),
                        state.getResolver(),
                        namespace, alias);
            }
            state.getModuleMap().put(module);
        }
        state.getResolver().setActiveNamespace(namespace);
        polyad.setEvaluated(true);
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
    }
*/
    protected void doSetAlias(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: The " + SET_ALIAS + " command requires two arguments.");
        }
        Object arg1 = polyad.getArgumments().get(0).evaluate(state);
        Object arg2 = polyad.getArgumments().get(2).evaluate(state);
        if (!areAllStrings(arg1, arg2)) {
            throw new IllegalArgumentException("Error: Set alias both arguments be simple strings.");
        }
        String namespace = arg1.toString();
        String alias = arg2.toString();
        if (!state.getModuleMap().containsKey(namespace)) {
            throw new IllegalArgumentException("Error: The module \"" + namespace + "\" must exist before setting an alias");
        }
        Module module = state.getModuleMap().get(namespace);
        module.setAlias(alias);
        polyad.setResult(module.getAlias());
        polyad.setResultType(Constant.STRING_TYPE);
        polyad.setEvaluated(true);
    }

   /* protected void doEndModule(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 0) {
            throw new IllegalArgumentException("Error: the " + END_MODULE + "  command requires no arguments");
        }
        NamespaceResolver resolver = state.getResolver();
        resolver.setActiveNamespace(resolver.getDefaultUserNamespace());
        polyad.setResult(resolver.getActiveNamespace());
        polyad.setEvaluated(true);
        polyad.setResultType(Constant.STRING_TYPE);
    }
*/
    protected void doLoadModule(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error" + LOAD_MODULE + " requires a single argument. The full path to the module's file.");
        }
        Object arg = polyad.getArgumments().get(0).evaluate(state);
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
            QDLParserDriver parserDriver = new QDLParserDriver(new HashMap<>(), state);
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
        Object arg = polyad.getArgumments().get(0).evaluate(state);
        if (!isString(arg)) {
            throw new IllegalArgumentException("Error: the argument to " + IMPORT + " must be a string");
        }
        String moduleName = arg.toString();

        if (!state.getModuleMap().containsKey(moduleName)) {
            throw new IllegalArgumentException("Error: could not find module \"" + moduleName + " to import");
        }
        SymbolTable st = state.getSymbolStack().getTopST();
        Module module = state.getModuleMap().get(moduleName);
        st.addModule(module);
    }
}
