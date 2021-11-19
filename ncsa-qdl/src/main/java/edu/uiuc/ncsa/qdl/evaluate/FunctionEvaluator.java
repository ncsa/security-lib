package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.extensions.QDLFunctionRecord;
import edu.uiuc.ncsa.qdl.functions.*;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;

import java.util.List;

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
        if(fNames == null){
            fNames = new String[]{IS_FUNCTION};
        }
        return fNames;
    }

    @Override
    public boolean evaluate(String alias, Polyad polyad, State state) {
        switch (polyad.getName()) {
               case IS_FUNCTION:
                   if (polyad.getArgCount() == 0) {
                       throw new IllegalArgumentException("You must supply at least one argument.");
                   }
                   if (polyad.getArguments().get(0) instanceof VariableNode) {
                       // they either are asking about a variable or it does not exist and by default, the parser thinks
                       // it was one
                       polyad.setEvaluated(true);
                       polyad.setResultType(Constant.BOOLEAN_TYPE);
                       polyad.setResult(false);
                       return true;
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

                   polyad.setResult(state.resolveFunction(name, argCount, true).functionRecord != null);
                   polyad.setResultType(Constant.BOOLEAN_TYPE);
                   polyad.setEvaluated(true);
                   return true;
           }
           if (polyad.isInModule() || !polyad.isBuiltIn()) {
               try {
                   figureOutEvaluation(polyad, state, !polyad.isInModule());
               } catch(UndefinedFunctionException ufe){
                   // special case this one QDLException so it gives usedful user feedback.
                   QDLStatementExecutionException qq = new QDLStatementExecutionException(ufe, polyad);
                   throw qq;
               }catch(QDLException qe){
                   throw qe;
               }catch (Throwable t) {
                   QDLStatementExecutionException qq = new QDLStatementExecutionException(t, polyad);
                   throw qq;
               }
               return true;
           }
           return false;
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
        try{
            return evaluate2(polyad, state);
        }catch(QDLException q){
              throw q;
        }catch(Throwable t){
            QDLStatementExecutionException qq = new QDLStatementExecutionException(t, polyad);
            throw qq;
        }
    }
    public boolean evaluate2(Polyad polyad, State state) {
          return evaluate(null, polyad, state);
    }
     protected boolean isFDef(Statement statement){
        return (statement instanceof LambdaDefinitionNode) || (statement instanceof FunctionDefinitionStatement) || (statement instanceof FunctionReferenceNode);
     }
    protected void doJavaFunction(Polyad polyad, State state, FR_WithState frs) {
        // Contains a java function that is wrapped in a QDLFunction. The polyad here contains the
        // arguments that are needed to unpack this.
        Object[] argList = new Object[polyad.getArgCount()];
        for (int i = 0; i < polyad.getArgCount(); i++) {
            if(isFDef(polyad.getArguments().get(i))){
                // Can't do getOperator since we do not know how many other arguments
                // are functions or constants.
                argList[i] = getFunctionReferenceNode(state, polyad.getArguments().get(i));
            }else{
                argList[i] = polyad.getArguments().get(i).evaluate(state);
            }
        }
        QDLFunctionRecord qfr = (QDLFunctionRecord) frs.functionRecord;
        //Object result = qfr.qdlFunction.getInstance().evaluate(argList);
        // This is the direct analog of func(polyad, state):
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
            if(state.isPrivate(polyad.getName()) && polyad.isInModule()){
                // if it is in a module and at the top of the stack, then this is an access violation
                if(state.getFTStack().getFtables().get(0).containsKey(polyad.getName(),polyad.getArgCount())){
                    throw new IntrinsicViolation("cannot access intrinsic function directly.");
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

    /*   f(x)->ln(x-2); f(sin(pi()/4))
      module['a:/b','X'][__f(x,y)->x*y;]
  module_import('a:/b', 'A')
         A#__f(2,3)
         // last should fail

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
    //  sum(x.)->reduce(*+, x.);
    //fork(*sum(), */, *size(), [2,5,7])

    /*
       module['a:a', 'a'][f(x)->x+1;g(x)->f(x+2);];
       module_import('a:a');
       g(1);

       y. := pplot(@cos(),@sin(),-pi(),pi(),50);
       y. := pplot(@sin(),@cos(),-1,1,100);
         lissy(x)->sin(x)*cosh(x)
  y. := pplot(@lissx(), @lissy(), -pi(), pi(), 500

  search >home_url  -out [home_url, email] -r  .*ncifcrf\.gov

     */

    protected void doFunctionEvaluation(Polyad polyad, State state, FR_WithState frs) throws Throwable {
        FunctionRecord functionRecord = frs.functionRecord;
        State moduleState = null;
        if(frs.isModule){
            moduleState = (State) frs.state;
        }
        if (functionRecord == null) {
            // see if its a reference instead
            functionRecord = state.getFTStack().getFunctionReference(polyad.getName());
            if (functionRecord == null) {
                throw new UndefinedFunctionException(" the function '" + polyad.getName() + "' with "
                        + polyad.getArgCount() + " arguments was not found.");
            }
        }
        if (!functionRecord.isFuncRef) {
            functionRecord = functionRecord.newInstance();
        }
        State localState;
         if(moduleState == null){
            localState = state.newStateWithImports();

        }else{
            localState = state.newStateWithImports(moduleState);
        }
         localState.setWorkspaceCommands(state.getWorkspaceCommands());
       // State localState = state.newStateWithImports();

   //     localState.getSymbolStack().addParent((SymbolTableImpl) moduleState.getSymbolStack().getLocalST());
   //     localState.getFTStack().push(frs.state.getFTStack().peek());
        // we are going to write local variables here and the MUST get priority over already exiting ones
        // but without actually changing them (or e.g., recursion is impossible). 
        SymbolTable symbolTable = localState.getSymbolStack().getLocalST();
        //   boolean hasLocalFunctionTable = false;
        for (int i = 0; i < polyad.getArgCount(); i++) {
            if (polyad.getArguments().get(i) instanceof LambdaDefinitionNode) {
                LambdaDefinitionNode ldn = (LambdaDefinitionNode) polyad.getArguments().get(i);
                ldn.evaluate(localState);
            }
        }
        /*
            Two ways to do test
             g(@f, x)-> f(x)^3
             g(v(x)->x^2, 5)
            w(x)->x^2
            g(@w, 5)

         */
        //  g(@f, x)-> x^3
//         g(v(x)->x^2, 5)
        // now we populate the local state with the variables.
        for (int i = 0; i < functionRecord.getArgCount(); i++) {
            // note that the call evaluates the state in the non-local environment as per contract,
            // but the named result goes in to the localState.
            String localName = functionRecord.argNames.get(i);

            if (isFunctionReference(localName)) {
                localName = dereferenceFunctionName(localName);
                localState.getFTStack().pushNew();
                // This is the local name of the function.
                FunctionReferenceNode frn = getFunctionReferenceNode(state, polyad.getArguments().get(i), false);

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
                symbolTable.setValue(functionRecord.argNames.get(i), polyad.getArguments().get(i).evaluate(localState));
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
            if(statement instanceof StatementWithResultInterface){
                ((StatementWithResultInterface)statement).setAlias(polyad.getAlias());
            }
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
