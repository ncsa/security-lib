package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.extensions.QDLFunctionRecord;
import edu.uiuc.ncsa.qdl.functions.*;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.statements.LocalBlockStatement;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.VThing;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static edu.uiuc.ncsa.qdl.state.QDLConstants.FUNCTION_REFERENCE_MARKER;
import static edu.uiuc.ncsa.qdl.state.QDLConstants.FUNCTION_REFERENCE_MARKER2;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:53 AM
 */
public class FunctionEvaluator extends AbstractFunctionEvaluator {
    public static long serialVersionUID = 0xcafed00d2L;
    public static final String FUNCTION_NAMESPACE = "function";

    @Override
    public String getNamespace() {
        return FUNCTION_NAMESPACE;
    }

    public static final int BASE_FUNCTION_VALUE = 6000;
    public static final String IS_FUNCTION = "is_function";
    public static final int IS_FUNCTION_TYPE = 1 + BASE_FUNCTION_VALUE;

    @Override
    public int getType(String name) {
        if (name.equals(IS_FUNCTION)) return IS_FUNCTION_TYPE;
        // At parsing time, the function definition class sets the value manually,
        // so call to this should ever get anything other than unknown value.
        return UNKNOWN_VALUE;
    }


    @Override
    public String[] getFunctionNames() {
        if (fNames == null) {
            fNames = new String[]{IS_FUNCTION};
        }
        return fNames;
    }

    @Override
    public boolean evaluate(String alias, Polyad polyad, State state) {
        switch (polyad.getName()) {
            case IS_FUNCTION:
                if (polyad.isSizeQuery()) {
                    polyad.setResult(new int[]{1, 2});
                    polyad.setEvaluated(true);
                    return true;
                }
                if (polyad.getArgCount() < 1) {
                    throw new MissingArgException(IS_FUNCTION + " requires at least 1 argument", polyad);
                }

                if (2 < polyad.getArgCount()) {
                    throw new ExtraArgException(IS_FUNCTION + " requires at most 2 argument", polyad.getArgAt(1));
                }
                int argCount = -1; // default -- get any
                if (polyad.getArgCount() == 2) {
                    Object object2 = polyad.evalArg(1, state);
                    if (!isLong(object2)) {
                        throw new BadArgException(" The argument count must be a number.", polyad.getArgAt(1));
                    }
                    argCount = ((Long) object2).intValue();
                }

                if (polyad.getArgAt(0) instanceof ModuleExpression) {
                    ModuleExpression me = (ModuleExpression) polyad.getArgAt(0);
                    State lastState = me.getModuleState(state);
                    ModuleExpression lastME = me;
                    while (lastME.getExpression() instanceof ModuleExpression) {
                        lastME = (ModuleExpression) lastME.getExpression();
                        lastState = lastME.getModuleState(lastState);
                    }
                    Polyad pp = new Polyad(IS_FUNCTION);
                    pp.addArgument(lastME.getExpression());
                    if (polyad.getArgCount() == 2) {
                        pp.addArgument(polyad.getArgAt(1));
                    }
                    lastState.getMetaEvaluator().evaluate(pp, lastState);
                    polyad.setResult(pp.getResult());
                    polyad.setResultType(pp.getResultType());
                    polyad.setEvaluated(true);
                    return true;

                }
                if (polyad.getArgAt(0) instanceof VariableNode) {
                    // they either are asking about a variable or it does not exist and by default, the parser thinks
                    // it was one
                    VariableNode vNode = (VariableNode) polyad.getArgAt(0);

                    try {
                        if (argCount < 0) {
                            polyad.setResult(state.getFTStack().containsKey(new FKey(vNode.getVariableReference(), argCount)));
                        } else {
                            polyad.setResult(state.resolveFunction(vNode.getVariableReference(), argCount, true).functionRecord != null);
                        }
                    } catch (UndefinedFunctionException ufx) {
                        polyad.setResult(Boolean.FALSE);
                    }
                    polyad.setResultType(Constant.BOOLEAN_TYPE);
                    polyad.setEvaluated(true);
                    return true;
                }
                polyad.setEvaluated(true);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setResult(false);
                return true;
        }
        try {
            figureOutEvaluation(polyad, state, !polyad.isInModule());
            return true;
        } catch (UndefinedFunctionException ufe) {
            // special case this one QDLException so it gives usedful user feedback.
            QDLExceptionWithTrace qq = new QDLExceptionWithTrace(ufe, polyad);
            throw qq;
        } catch (QDLException qe) {
            throw qe;
        } catch (Throwable t) {
            QDLExceptionWithTrace qq = new QDLExceptionWithTrace(t, polyad);
            throw qq;
        }
    }

    /*


    

      m := '/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/modules/test.mdl'
  q :=module_load(m)
  )ws set debug on
   module_import(q)
     X#get_private()

     */
    @Override
    public boolean evaluate(Polyad polyad, State state) {
        return evaluate2(polyad, state);
    }

    public boolean evaluate2(Polyad polyad, State state) {
        return evaluate(null, polyad, state);
    }

    protected boolean isFDef(Statement statement) {
        return (statement instanceof LambdaDefinitionNode) || (statement instanceof FunctionDefinitionStatement) || (statement instanceof FunctionReferenceNode);
    }

    protected void doJavaFunction(Polyad polyad, State state, FR_WithState frs) {
        // Contains a java function that is wrapped in a QDLFunction. The polyad here contains the
        // arguments that are needed to unpack this.
        Object[] argList = new Object[polyad.getArgCount()];
        for (int i = 0; i < polyad.getArgCount(); i++) {
            if (isFDef(polyad.getArguments().get(i))) {
                // Can't do getOperator since we do not know how many other arguments
                // are functions or constants.
                argList[i] = getFunctionReferenceNode(state, polyad.getArguments().get(i));
            } else {
                argList[i] = polyad.getArguments().get(i).evaluate(state);
            }
        }
        QDLFunctionRecord qfr = (QDLFunctionRecord) frs.functionRecord;
        // This is the direct analog of func(polyad, state):
        if (qfr == null) {
            throw new UndefinedFunctionException("this function is not defined", polyad);
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
        Polyad tryScript = new Polyad(SystemEvaluator.RUN_COMMAND);
        ConstantNode constantNode = new ConstantNode(scriptName, Constant.STRING_TYPE);
        tryScript.getArguments().add(constantNode);
        for (int i = 0; i < polyad.getArgCount(); i++) {
            tryScript.getArguments().add(polyad.getArguments().get(i));
        }
        try {
            SystemEvaluator.runnit(tryScript, state, SystemEvaluator.RUN_COMMAND, state.getLibPath(), true);
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

    protected void figureOutEvaluation(Polyad polyad, State state, boolean checkForDuplicates) throws Throwable {
        FR_WithState frs;
        try {
            if (state.isIntrinsic(polyad.getName()) && polyad.isInModule()) {
                // if it is in a module and at the top of the stack, then this is an access violation
                if (state.getFTStack().localHas(new FKey(polyad.getName(), polyad.getArgCount()))) {
                    throw new IntrinsicViolation("cannot access intrinsic function directly.", polyad);
                }
            }
            frs = state.resolveFunction(polyad, checkForDuplicates);
        } catch (UndefinedFunctionException udx) {
            if (!state.isEnableLibrarySupport()) {
                throw udx; // don't try to resolve libraries if support is off.
            }
            if (!tryScript(polyad, state)) {
                throw udx;
            }
            return; // if it gets here, then the script worked, exit gracefully.
        }
        if (frs.isJavaFunction()) {
            doJavaFunction(polyad, state, frs);
        } else {
            doFunctionEvaluation(polyad, state, frs);
        }
    }

    protected void doFunctionEvaluation(Polyad polyad, State state, FR_WithState frs) throws Throwable {
        FunctionRecord functionRecord = frs.functionRecord;
        State moduleState = null;
        if (frs.isModule) {
            moduleState = (State) frs.state;
        }
        if (functionRecord == null) {
            // see if its a reference instead
            functionRecord = state.getFTStack().getFunctionReference(polyad.getName());
            if (functionRecord == null) {
                throw new UndefinedFunctionException(" the function '" + polyad.getName() + "' with "
                        + polyad.getArgCount() + " arguments was not found.", polyad);
            }
        }
        if (!functionRecord.isFuncRef) {
            functionRecord = functionRecord.newInstance();
        }
        State localState;
        if (moduleState == null) {

            if (functionRecord.isLambda() || functionRecord.isFuncRef) {
                localState = state.newLocalState();
            } else {
                localState = state.newFunctionState();
            }

        } else {
            if (functionRecord.isLambda()) {
                localState = state.newLocalState(moduleState);
            } else {
                localState = state.newFunctionState();
            }
        }
        localState.setWorkspaceCommands(state.getWorkspaceCommands());

        // we are going to write local variables here and the MUST get priority over already exiting ones
        // but without actually changing them (or e.g., recursion is impossible). 
        for (int i = 0; i < polyad.getArgCount(); i++) {
            if (polyad.getArguments().get(i) instanceof LambdaDefinitionNode) {
                LambdaDefinitionNode ldn = (LambdaDefinitionNode) polyad.getArguments().get(i);
                if (!ldn.hasName()) {
                    ldn.getFunctionRecord().name = tempFname(state);
                    // This is anonymous
                }
                ldn.evaluate(localState);
            }
        }

        // now we populate the local state with the variables.

        /*
        Note that the paramList is a listing of all variables and possible overloaded functions
        There may be lots of overloaded functions. These are then systematically added to the
        states later. This is not the argument list passed in to the function -- that is not changed.
         */
        ArrayList<XThing> foundParameters = resolveArguments(functionRecord, polyad, state, localState);


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
            if (statement instanceof StatementWithResultInterface) {
                ((StatementWithResultInterface) statement).setAlias(polyad.getAlias());
            }
            try {
                if(statement instanceof LocalBlockStatement){
                    // Can't tell when you get a function block, so have to do this
                    ((LocalBlockStatement)statement).setFunctionParameters(foundParameters);
                }
                statement.evaluate(localState);
            } catch (ReturnException rx) {
                polyad.setResult(rx.result);
                polyad.setResultType(rx.resultType);
                polyad.setEvaluated(true);
                for (int i = 0; i < functionRecord.getArgCount(); i++) {
                    localState.getVStack().localRemove(new XKey(functionRecord.argNames.get(i)));
                }
                return;
            } catch (java.lang.StackOverflowError sx) {
                throw new RecursionException();
            }
        }

        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * This will take the function record and polyad and find the arguments that are requested
     * in the function record vs. what's in the polyad and stash them in the correct state
     * objects.
     *
     * @param functionRecord
     * @param polyad
     * @param state
     * @param localState
     */
    protected ArrayList<XThing> resolveArguments(FunctionRecord functionRecord,
                                    Polyad polyad,
                                    State state,
                                    State localState) {
        ArrayList<XThing> paramList = new ArrayList<>();
        if (functionRecord.isFuncRef) {
            return paramList;// implicit parameter list since this is an operator or built in function.
        }

        HashMap<UUID, UUID> localStateLookup = new HashMap<>();
        localStateLookup.put(state.getUuid(), localState.getUuid());

        HashMap<UUID, State> referencedStates = new HashMap<>();
        referencedStates.put(localState.getUuid(), localState);
        for (int i = 0; i < functionRecord.getArgCount(); i++) {
            // note that the call evaluates the state in the non-local environment as per contract,
            // but the named result goes in to the localState.
            String localName = functionRecord.argNames.get(i);

            if (isFunctionReference(localName)) {
                localName = dereferenceFunctionName(localName);
                // This is the local name of the function.
                FunctionReferenceNode frn = getFunctionReferenceNode(state, polyad.getArguments().get(i), false);

                String xname = frn.getFunctionName(); // dereferenced in the parser
                boolean isOp = state.getOpEvaluator().isMathOperator(xname);
                boolean isFunc = state.getMetaEvaluator().isBuiltInFunction(xname);
                List<FR_WithState> functionRecordList = null;
                if (isOp || isFunc) {
                    functionRecordList = new ArrayList<>();
                    int airity[];
                    if (isOp) {
                        // operator like + or *
                        airity = state.getOpEvaluator().getArgCount(xname);
                    } else {
                        airity = state.getMetaEvaluator().getArgCount(xname);
                    }
                    for (int j = 0; j < airity.length; j++) {
                        FunctionRecord functionRecord1 = new FunctionRecord();
                        functionRecord1.name = localName;
                        functionRecord1.fRefName = xname;
                        functionRecord1.isFuncRef = true;
                        functionRecord1.isOperator = isOp;
                        functionRecord1.setArgCount(airity[j]);
                        FR_WithState frs0 = new FR_WithState();
                        frs0.functionRecord = functionRecord1;
                        functionRecordList.add(frs0);
                    }

                } else {
                    try {
                        // function records come back cloned
                        functionRecordList = localState.getAllFunctionsByName(xname);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        throw new NFWException("Somehow function records are no longer clonable. Check signatures.");
                    }


                }
                // The next block only gets used if there are references to function in modules
                // E.g. @w#z#f
                for (FR_WithState fr_withState : functionRecordList) {
                    fr_withState.functionRecord.name = localName;
                    paramList.add(fr_withState);
                    if (fr_withState.hasState()) {
                        State s = (State) fr_withState.state;
                        if (!localStateLookup.containsKey(s.getUuid())) {
                            State ss = s.newLocalState();
                            localStateLookup.put(s.getUuid(), ss.getUuid());
                            referencedStates.put(ss.getUuid(), ss);
                        }
                    }
                }
                // This had better be a function reference or this should blow up.
            } else {
                VThing vThing = new VThing(new XKey(functionRecord.argNames.get(i)), polyad.getArguments().get(i).evaluate(state));
                paramList.add(vThing);
            }
        }
        // Add the arguments to the state object(s). At the least, there is always
        // localState (derived from the original state argument to this method).
        for (UUID uuid : referencedStates.keySet()) {
            State s = referencedStates.get(uuid);
            for (XThing xThing : paramList) {
                if (xThing instanceof VThing) {
                    s.getVStack().localPut(xThing);
                } else {
                    s.getFTStack().localPut(xThing);
                }
            }
        }
        return paramList;
    }

    /*

          g(@h(), x, y)->h(x+'pqr', y+1) + h(x+'tuv', y)
          define[g(@h(), x, y)][return(h(x+'pqr', y+1) + h(x+'tuv', y));]
  g(@substring, 'abcd', 2); // result == dpqrcdtuv
  
  h(@g,x)->g(x)
  g(@substring, 'abcd', 2)
    Test function references to things in modules.

      define[f(x)]body[return(x+100);];
  module['a:/t','a']body[define[f(x)]body[return(x+1);];];
  module['q:/z','w']body[zz:=17;module_import('a:/t');g(x)->a#f(x)+zz;];
  module_import('q:/z');
  w#a#f(3); // returns 20
  w#g(2); // returns 6
  hh(@g, x)->g(x)

  hh(@w#g, 2); // == w#g(2)
  hh(@w#a#f, 3); // == w#a#f(3)

    
      qq(x)->x^2
      qq(3); // returns 9
     ww(@p, x)->p(x)
     ww(@-, 2); // returns -2
     ww((x)->x^3, 4)

     */
    protected boolean isFunctionReference(String name) {
        return name.startsWith(FUNCTION_REFERENCE_MARKER) || name.startsWith(FUNCTION_REFERENCE_MARKER2);
    }

    protected String dereferenceFunctionName(String name) {
        String x = name.substring(FUNCTION_REFERENCE_MARKER.length());
        if (x.endsWith("()")) {
            x = x.substring(0, x.length() - 2); // * ... ( are bookends for the reference
        }
        return x;
    }
}
