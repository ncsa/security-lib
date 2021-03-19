package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.MissingArgumentException;
import edu.uiuc.ncsa.qdl.exceptions.RecursionException;
import edu.uiuc.ncsa.qdl.exceptions.ReturnException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Dyad;
import edu.uiuc.ncsa.qdl.expressions.Monad;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.extensions.QDLFunctionRecord;
import edu.uiuc.ncsa.qdl.functions.FR_WithState;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;

import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.state.QDLConstants.FUNCTION_REFERENCE_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:53 AM
 */
public class FunctionEvaluator extends AbstractFunctionEvaluator {
    public static long serialVersionUID = 0xcafed00d2L;
    public static final int BASE_FUNCTION_VALUE = 6000;
    public static final String IS_FUNCTION = "is_function";
    public static final String FQ_IS_FUNCTION = SYS_FQ + "is_function";
    public static final int IS_FUNCTION_TYPE = 1 + BASE_FUNCTION_VALUE;

    @Override
    public int getType(String name) {
        if (name.equals(IS_FUNCTION) || name.equals(FQ_IS_FUNCTION)) return IS_FUNCTION_TYPE;
        // At parsing time, the function definition class sets the value manually,
        // so call to this should ever get anything other than unknown value.
        return UNKNOWN_VALUE;
    }

    public static String FUNC_NAMES[] = new String[]{IS_FUNCTION};
    public static String FQ_FUNC_NAMES[] = new String[]{FQ_IS_FUNCTION};

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
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case IS_FUNCTION:
            case FQ_IS_FUNCTION:
                if (polyad.getArgCount() != 1) {
                    throw new IllegalArgumentException("You must supply at least one argument.");
                }
                Object object = polyad.evalArg(0, state);
                if (object == null) {
                    throw new MissingArgumentException(" You must supply an argument for the " + IS_FUNCTION + " command.");
                }
                if (!isString(object)) {
                    throw new IllegalArgumentException(" the " + IS_FUNCTION + " command requires a string as its first argument.");
                }
                String name = object.toString();
                int argCount = -1; // default -- get any
                if (polyad.getArgCount() == 2) {
                    Object object2 = polyad.evalArg(1, state);
                    ;
                    if (!isLong(object2)) {
                        throw new IllegalArgumentException(" The argument count must be a number.");
                    }
                    argCount = ((Long) object2).intValue();
                }

                polyad.setResult(state.resolveFunction(name, argCount).functionRecord != null);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setEvaluated(true);
                return true;
        }
        if (!polyad.isBuiltIn()) {
            figureOutEvaluation(polyad, state);
            return true;
        }
        return false;
    }

    protected void doJavaFunction(Polyad polyad, State state, FR_WithState frs) {
        // Contains a java function that is wrapped in a QDLFunction. The polyad here contains the
        // arguments that are needed to unpack this.
        Object[] argList = new Object[polyad.getArgCount()];
        for (int i = 0; i < polyad.getArgCount(); i++) {
            argList[i] = polyad.getArguments().get(i).evaluate(state);
        }
        QDLFunctionRecord qfr = (QDLFunctionRecord) frs.functionRecord;
        //Object result = qfr.qdlFunction.getInstance().evaluate(argList);
        // Direct analog of func(polyad, state):
        if (qfr == null) {
            throw new UndefinedFunctionException("this function is not defined");
        }
        Object result = qfr.qdlFunction.evaluate(argList, state);
        polyad.setResult(result);
        polyad.setEvaluated(true);
        polyad.setResultType(Constant.getType(result));
        return;


    }

    protected boolean tryScript(Polyad polyad, State state) {
        if (!state.isEnableLibrarySupport() || state.getLibPath().isEmpty()) {
            return false;
        }
        String scriptName = polyad.getName() + QDLVersion.DEFAULT_FILE_EXTENSION;
        Polyad tryScript = new Polyad(ControlEvaluator.RUN_COMMAND);
        ConstantNode constantNode = new ConstantNode(scriptName, Constant.STRING_TYPE);
        tryScript.getArguments().add(constantNode);
        for (int i = 0; i < polyad.getArgCount(); i++) {
            tryScript.getArguments().add(polyad.getArguments().get(i));
        }
        try {
            ControlEvaluator.runnit(tryScript, state, ControlEvaluator.RUN_COMMAND, state.getLibPath(), true);
            tryScript.evaluate(state);
            polyad.setResult(tryScript.getResult());
            polyad.setResultType(tryScript.getResultType());
            polyad.setEvaluated(true);
            return true;
        } catch (Throwable t) {
            DebugUtil.trace(this, ".tryScript failed:");
            if (DebugUtil.isEnabled()) {
                t.printStackTrace();
            }
        }
        return false;
    }

    protected void figureOutEvaluation(Polyad polyad, State state) {
        FR_WithState frs;
        try {
            frs = state.resolveFunction(polyad);
        } catch (UndefinedFunctionException udx) {
            if (!state.isEnableLibrarySupport()) {
                throw udx; // don't try to resolve libraries if support is off.
            }
            if (!tryScript(polyad, state)) {
                throw udx;
            }
            return; // if it gets here, then the script worked, exit gracefully.
        }
        if (frs.isExternalModule) {
            doJavaFunction(polyad, state, frs);
        } else {
            doFunctionEvaluation(polyad, state, frs);
        }
    }

    /*
      r(x)->x^2 + 1;
  f(*h(), x) -> h(x);
            f(*r(), 2)


  g(*h(), x, y)->h(x+'pqr', y+1) + h(x+'tuv', y)
  g(*substring(), 'abcd', 2)

  g1(x,y,*h())->h(x+'pqr', y+1) + h(x+'tuv', y)
  g1(*substring(), 'abcd', 2) // fails on purpose -- shoudl check for right location of f ref in arg list.
  g1('abcd', 2, *substring())

  op(*h(), x, y) -> h(x,y)
  op(*+, 2, 3)
   op(**, 2, 3)
  op(*^, 2, 3)

     */
    protected void doFunctionEvaluation(Polyad polyad, State state, FR_WithState frs) {
        FunctionRecord functionRecord = frs.functionRecord;

        if (functionRecord == null) {
            // see if its a reference instead
            functionRecord = state.getFTStack().getFunctionReference(polyad.getName());
            if (functionRecord == null) {
                throw new UndefinedFunctionException(" the function '" + polyad.getName() + "' with "
                        + polyad.getArgCount() + " arguments was not found.");
            }
        }
        State localState = frs.state.newStateWithImports();
        // we are going to write local variables here and the MUST get priority over already exiting ones
        // but without actually changing them (or e.g., recursion is impossible). 
        SymbolTable symbolTable = localState.getSymbolStack().getLocalST();
        boolean hasLocalFunctionTable = false;
        // now we populate the local state with the variables.
        for (int i = 0; i < functionRecord.getArgCount(); i++) {
            // note that the call evaluates the state in the non-local environment as per contract,
            // but the named result goes in to the localState.
            String localName = functionRecord.argNames.get(i);
            if (isFunctionReference(localName)) {
                localName = dereferenceFunctionName(localName);
                if (!hasLocalFunctionTable) {
                    localState.getFTStack().pushNew();
                    hasLocalFunctionTable = true;
                }
                // This is the local name of the function.
                if (!(polyad.getArguments().get(i) instanceof FunctionReferenceNode)) {
                    throw new IllegalArgumentException("error: The supplied argument was not a function reference");
                }
                FunctionReferenceNode frn = (FunctionReferenceNode) polyad.getArguments().get(i);
                String xname = frn.getFunctionName(); // dereferenced in the parser
                boolean isBuiltin = state.getMetaEvaluator().isBuiltInFunction(xname) || state.getOpEvaluator().isMathOperator(xname);

                if (isBuiltin) {
                    FunctionRecord functionRecord1 = new FunctionRecord();
                    functionRecord1.name = localName;
                    functionRecord1.fRefName = xname;
                    functionRecord1.isFuncRef = true;
                    localState.getFTStack().peek().put(functionRecord1);
                } else {
                    List<FunctionRecord> functionRecordList = localState.getFTStack().getByAllName(xname);
                    for (FunctionRecord functionRecord1 : functionRecordList) {
                        FunctionRecord clonedFR = null;
                        try {
                            clonedFR = functionRecord1.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                            throw new NFWException("Somehow function records are no longer clonable. Check signatures.");
                        }
                        clonedFR.name = localName;
                        localState.getFTStack().peek().put(clonedFR);
                    }
                }

                // This had better be a function reference or this should blow up.
            } else {
                symbolTable.setValue(functionRecord.argNames.get(i), polyad.getArguments().get(i).evaluate(state));
            }
        }
        if (functionRecord.isFuncRef) {
            String realName = functionRecord.fRefName;
            if (state.getOpEvaluator().isMathOperator(realName)) {
                // Monads and Dyads are reserved for math operations and are smart enough
                // to grab the OpEvaluator and invoke it, so if the user is sending along
                // a polyad, that requires going through the evaluator.
                // Note that monads, dyads and polyads are all subclasses of
                // ExpressionImpl and so we can't cast from one to the other, we
                // have to copy stuff.
                if (polyad.getArgCount() == 1) {
                    Monad monad = new Monad(localState.getOperatorType(realName), false);
                    monad.setArgument(polyad.getArguments().get(0));
                    monad.evaluate(localState);
                    polyad.setResultType(monad.getResultType());
                    polyad.setEvaluated(true);
                    polyad.setResult(monad.getResult());
                    polyad.getArguments().set(0, monad.getArgument());
                    return;
                } else {
                    Dyad dyad = new Dyad(localState.getOperatorType(realName));
                    dyad.setLeftArgument(polyad.getArguments().get(0));
                    dyad.setRightArgument(polyad.getArguments().get(1));
                    dyad.evaluate(localState);
                    polyad.setResultType(dyad.getResultType());
                    polyad.setEvaluated(true);
                    polyad.setResult(dyad.getResult());
                    polyad.getArguments().set(0, dyad.getLeftArgument());
                    polyad.getArguments().set(1, dyad.getRightArgument());
                    return;
                }
            } else {
                // Easy case, just run it as a polyad.
                polyad.setName(realName);
                polyad.evaluate(localState);
                return;
            }
        }
        for (Statement statement : functionRecord.statements) {
            try {
                statement.evaluate(localState);
            } catch (ReturnException rx) {
                polyad.setResult(rx.result);
                polyad.setResultType(rx.resultType);
                polyad.setEvaluated(true);
                for (int i = 0; i < functionRecord.getArgCount(); i++) {
                    symbolTable.remove(functionRecord.argNames.get(i));
                }
                return;
            } catch (java.lang.StackOverflowError sx) {
                throw new RecursionException();
            }
        }
        // Now remove the variables we created from the stack since they are no longer needed AND there is no
        // way to otherwise be rid of them.
        for (int i = 0; i < functionRecord.getArgCount(); i++) {
            symbolTable.remove(functionRecord.argNames.get(i));
        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    protected boolean isFunctionReference(String name) {
        return name.startsWith(FUNCTION_REFERENCE_MARKER);
    }

    protected String dereferenceFunctionName(String name) {
        String x = name.substring(FUNCTION_REFERENCE_MARKER.length());
        x = x.substring(0, x.indexOf("(")); // * ... ( are bookends for the reference
        return x;
    }
}
