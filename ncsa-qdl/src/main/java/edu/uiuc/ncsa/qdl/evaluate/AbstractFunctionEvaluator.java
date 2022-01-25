package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.Dyad;
import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.functions.*;
import edu.uiuc.ncsa.qdl.module.MAliases;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemUtility;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;
import org.apache.commons.codec.binary.Base32;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  10:59 AM
 */
public abstract class AbstractFunctionEvaluator implements EvaluatorInterface {
    protected String[] fNames = null;

    public abstract String[] getFunctionNames();

    String[] fqNames = null;
    public String[] getFQFunctionNames(){
        if(fqNames == null){
            fqNames = new String[getFunctionNames().length];
            for(int i = 0; i < fqNames.length; i++){
                fqNames[i]= getNamespace() + MAliases.NS_DELIMITER + getFunctionNames()[i];
            }
        }

        return fqNames;
    }
    @Override
    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        String[] fnames = listFQ ? getFQFunctionNames() : getFunctionNames();

        for (String key : fnames) {
            names.add(key + "()");
        }
        return names;
    }
    public boolean isBuiltInFunction(String name) {
        for (String x : getFunctionNames()) {
            if (x.equals(name)) return true;
        }
        return false;
    }

    /**
     * This takes and expression (with an operator type) and returns true if is handled in this evaluator
     * and false otherwise.
     */


    public abstract boolean evaluate(Polyad polyad, State state);

    public boolean evaluate(String alias, Polyad polyad, State state) {
        if (alias.equals(getNamespace())) {
            return evaluate(polyad, state);
        }
        return false;
    }

    protected boolean isStem(Object obj) {
        return StemUtility.isStem(obj);
    }

    protected boolean isStemList(Object obj) {
        return isStem(obj) && ((StemVariable) obj).containsKey("0");
    }

    protected boolean isLong(Object obj) {
        return obj instanceof Long;
    }

    protected boolean isBoolean(Object obj) {
        return obj instanceof Boolean;
    }

    protected boolean areAllBoolean(Object... objects) {
        for (Object arg : objects) {
            if (!isBoolean(arg)) return false;
        }
        return true;
    }

    protected boolean areAllStems(Object... objects) {
        return StemUtility.areAllStems(objects);
    }

    protected boolean areNoneStems(Object... objects) {
        return StemUtility.areNoneStems(objects);
    }

    protected boolean isString(Object obj) {
        return obj instanceof String;
    }

    protected boolean areAllStrings(Object... objects) {
        for (Object arg : objects) {
            if (!isString(arg)) return false;
        }
        return true;
    }

    protected boolean areAllLongs(Object... objects) {
        for (Object arg : objects) {
            if (!(arg instanceof Long)) return false;
        }
        return true;
    }

    protected boolean isNumber(Object arg) {
        return (arg instanceof Long) || (arg instanceof BigDecimal);
    }

    protected boolean isBigDecimal(Object obj) {
        return obj instanceof BigDecimal;
    }

    protected boolean areAnyBigDecimals(Object... objects) {
        for (Object arg : objects) {
            if (isBigDecimal(arg)) return true;
        }
        return false;
    }

    protected boolean areAllBigDecimals(Object... objects) {
        for (Object arg : objects) {
            if (!isBigDecimal(arg)) return false;
        }
        return true;
    }

    /**
     * How to compare two big decimals requires some work.
     *
     * @param a
     * @param b
     * @return
     */
    protected boolean bdEquals(BigDecimal a, BigDecimal b) {
        BigDecimal r = a.subtract(b);
        return r.compareTo(BigDecimal.ZERO) == 0;
    }

    protected boolean areAllNumbers(Object... objects) {
        for (Object arg : objects) {
            if (!isNumber(arg)) return false;
        }
        return true;
    }

    protected BigDecimal toBD(Object obj) {
        if (!isNumber(obj)) throw new IllegalArgumentException("'" + obj + "' is not a number");
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Long) return new BigDecimal((Long) obj, OpEvaluator.getMathContext());
        if (obj instanceof Integer) return new BigDecimal((Integer) obj, OpEvaluator.getMathContext());
        throw new IllegalArgumentException("'" + obj + "' is not a number");
    }

    protected StemVariable toStem(Object object) {
        if (isStem(object)) return (StemVariable) object;
        StemVariable out = new StemVariable();
        out.setDefaultValue(object);
        return out;
    }


    /**
     * Function pointer class since this ishow youdo that in Java.
     */
    public static abstract class fPointer {
        public abstract fpResult process(Object... objects);

        public boolean isFirstArgumentMonadicMinus = false;
    }

    public static class fpResult {
        public Object result;
        public int resultType;
    }

    protected void finishExpr(ExpressionImpl node, fpResult r) {
        node.setResult(r.result);
        node.setResultType(r.resultType);
        node.setEvaluated(true);
    }

    // ToDO make a processN method from these. Just have to scratch head about certain bookkeeping.
    protected void process1(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException(name + " requires 1 argument");
        }
        Object arg1 = polyad.evalArg(0, state);

        checkNull(arg1, polyad.getArgAt(0), state);
        if (!isStem(arg1)) {
            fpResult r = pointer.process(arg1);
            finishExpr(polyad, r);
            return;
        }
        StemVariable stemVariable = (StemVariable) arg1;
        StemVariable outStem = new StemVariable();
        processStem1(outStem, stemVariable, pointer);
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * Processing stems for monadic functions
     *
     * @param outStem
     * @param stemVariable
     * @param pointer
     */
    protected void processStem1(StemVariable outStem, StemVariable stemVariable, fPointer pointer) {
        for (String key : stemVariable.keySet()) {
            Object object = stemVariable.get(key);
            if (object instanceof StemVariable) {
                StemVariable newOut = new StemVariable();
                processStem1(newOut, (StemVariable) object, pointer);
                if (!newOut.isEmpty()) {
                    outStem.put(key, newOut);
                }
            } else {
                outStem.put(key, pointer.process(stemVariable.get(key)).result);
            }
        }

    }

    protected void process2(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state
    ) {
        process2(polyad, pointer, name, state, false);
    }

    /**
     * NOTE optionalArguments means that the {@link fPointer} an take more than 2 arguments.
     * So the basic functionality requires 2 args and there may be more.
     *
     * @param polyad
     * @param pointer
     * @param name
     * @param state
     * @param optionalArgs
     */
    protected void process2(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state,
                            boolean optionalArgs) {
        if (!optionalArgs && polyad.getArgCount() != 2) {
            throw new IllegalArgumentException(name + " requires 2 arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0), state);

        // Short circuit dyadic logical && ||
        if (arg1 instanceof Boolean) {
            if (polyad.getOperatorType() == OpEvaluator.OR_VALUE) {
                if ((Boolean) arg1) {
                    polyad.setResult(Boolean.TRUE);
                    polyad.setResultType(Constant.BOOLEAN_TYPE);
                    polyad.setEvaluated(true);

                    return;
                }
            }
            if (polyad.getOperatorType() == OpEvaluator.AND_VALUE) {
                if (!((Boolean) arg1)) {
                    polyad.setResult(Boolean.FALSE);
                    polyad.setResultType(Constant.BOOLEAN_TYPE);
                    polyad.setEvaluated(true);
                    return;
                }
            }
        }
        Object arg2 = polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1), state);
        Object[] argList = new Object[polyad.getArgCount()];
        argList[0] = arg1;
        argList[1] = arg2;
        if (optionalArgs) {
            for (int i = 2; i < polyad.getArgCount(); i++) {
                argList[i] = polyad.getArguments().get(i).evaluate(state);
            }
        }
        if (areNoneStems(argList)) {
            fpResult result = pointer.process(argList);
            finishExpr(polyad, result);
            return;
        }
        StemVariable stem1 = toStem(arg1);
        StemVariable stem2 = toStem(arg2);
        StemVariable outStem = new StemVariable();
        processStem2(outStem, stem1, stem2, pointer, polyad, optionalArgs);
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /*
    For debugging
      d.0.0 := 5; d.0.1 := 4; d.1.0:=  3; d.1.1 := -2;
      c.0.0 := 2; c.0.1 := 1; c.1.0:= -7;  c.1.1 := 5;
      c. + d.
     */
    protected void processStem2(StemVariable outStem,
                                StemVariable stem1,
                                StemVariable stem2,
                                fPointer pointer,
                                ExpressionImpl polyad, boolean optionalArgs) {
        Set<String> keys = getCommonKeys(stem1, stem2);
        // now we loop -- note that we must still preserve which is the first and second argument
        // so all this is basically to figure out how to loop over what.
        for (String key : keys) {
            fpResult r = null;
            Object[] objects;
            if (optionalArgs) {
                objects = new Object[polyad.getArgCount()];

            } else {
                objects = new Object[2];
            }
            objects[0] = stem1.get(key);
            objects[1] = stem2.get(key);
            if (optionalArgs) {
                for (int i = 2; i < objects.length; i++) {
                    objects[i] = polyad.getArguments().get(i).getResult();
                }
            }
            if (isStem(objects[0]) || isStem(objects[1])) {
                StemVariable newOut = new StemVariable();
                processStem2(newOut, toStem(objects[0]), toStem(objects[1]), pointer, polyad, optionalArgs);
                if (!newOut.isEmpty()) {
                    outStem.put(key, newOut);
                }
            } else {
                r = pointer.process(objects);
                outStem.put(key, r.result);
            }
        }

    }

    protected void process3(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state,
                            boolean optionalArguments) {
        if (!optionalArguments && polyad.getArgCount() != 3) {
            throw new IllegalArgumentException(name + " requires at least 3  arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0), state);
        Object arg2 = polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1), state);

        Object arg3 = polyad.evalArg(2, state);
        checkNull(arg3, polyad.getArgAt(3), state);

        if (arg1 == null || arg2 == null || arg3 == null) {
            throw new UnknownSymbolException("unknown symbol");
        }
        Object[] argList = new Object[polyad.getArgCount()];
        argList[0] = arg1;
        argList[1] = arg2;
        argList[2] = arg3;
        if (optionalArguments) {
            for (int i = 2; i < polyad.getArgCount(); i++) {
                argList[i] = polyad.getArguments().get(i).evaluate(state);
            }
        }
        if (areNoneStems(argList)) {

            fpResult result = pointer.process(argList);
            finishExpr(polyad, result);
            return;
        }
        StemVariable stem1 = toStem(arg1);
        StemVariable stem2 = toStem(arg2);
        StemVariable stem3 = toStem(arg3);
        StemVariable outStem = new StemVariable();
        processStem3(outStem, stem1, stem2, stem3, pointer, polyad, true);
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }


    protected void processStem3(StemVariable outStem,
                                StemVariable stem1,
                                StemVariable stem2,
                                StemVariable stem3,
                                fPointer pointer,
                                ExpressionImpl polyad, boolean optionalArgs) {
        Set<String> keys = getCommonKeys(stem1, stem2, stem3);
        // now we loop -- note that we must still preserve which is the first and second argument
        // so all this is basically to figure out how to loop over what.
        for (String key : keys) {
            fpResult r = null;
            Object[] objects;
            if (optionalArgs) {
                objects = new Object[polyad.getArgCount()];

            } else {
                objects = new Object[3];
            }
            objects[0] = stem1.get(key);
            objects[1] = stem2.get(key);
            objects[2] = stem3.get(key);
            if (optionalArgs) {
                for (int i = 3; i < objects.length; i++) {
                    objects[i] = polyad.getArguments().get(i).getResult();
                }
            }

            if (objects[0] instanceof StemVariable) {
                StemVariable newOut = new StemVariable();
                processStem3(newOut,
                        toStem(objects[0]),
                        toStem(objects[1]),
                        toStem(objects[2]),
                        pointer, polyad, optionalArgs);
                if (!newOut.isEmpty()) {
                    outStem.put(key, newOut);
                }
                //r = pointer.process(objects);
            } else {
                r = pointer.process(objects);
                outStem.put(key, r.result);
            }
        }

    }

    /**
     * This checks each stem for a default value and then if there is not one, it checks
     * the key. Best we can do is jump out of the loop when we hit a dud.
     *
     * @param key
     * @param stems
     * @return
     */
    protected boolean allHaveKey(String key, StemVariable... stems) {
        for (StemVariable stem : stems) {
            if (!stem.isEmpty()) {
                if (!stem.containsKey(key)) return false;
            }
        }
        return true;
    }

    protected Set<String> getCommonKeys(StemVariable... stems) {
        Set<String> keys = null;
        // First find one that has some keys.
        for (StemVariable stem : stems) {
            if (!stem.isEmpty()) {
                if (keys == null) {
                    keys = stem.keySet();
                }
                break;
            }
        }
        Set<String> foundKeys = new HashSet<>();
        // now look for common keys.
        if (keys == null) {
            // no common keys found
            return foundKeys;
        }
        for (String key : keys) {
            if (allHaveKey(key, stems)) {
                foundKeys.add(key);
            }
        }
        return foundKeys;
    }

    /**
     * This will take an {@link ExpressionImpl} that should contain a stem, check the reference and it the stem
     * does not exist, create and put it in the symbol table. If the stem exists, it just returns it.
     * This lets you do things like issue:
     * <pre>
     *     foo. := null
     *     if[
     *        some_condition
     *     ]then[
     *        list_append(foo., 4);
     *     // ... other stuff
     *     ];
     * </pre>
     * and not get a null pointer exception. This is needed especially if a the command is issued in a different scope,
     * e.g. in a conditional block to assign the value.<br/><br/>
     * This <b><i>WILL</i></b> throw an exception if the argument is not a stem!! So this is invoked where
     * there is a required stem that is missing and should be there.
     *
     * @param node
     * @param state
     * @param informativeMessage
     * @return
     */
    protected StemVariable getOrCreateStem(StatementWithResultInterface node, State state, String informativeMessage) {
        StemVariable stem1 = null;
        if (node instanceof VariableNode) {
            VariableNode vn = (VariableNode) node;
            String varName = vn.getVariableReference();
            if (!state.isDefined(varName)) {
                if (!varName.endsWith(StemVariable.STEM_INDEX_MARKER)) {
                    throw new IllegalArgumentException(informativeMessage);
                }
                stem1 = new StemVariable();
                state.setValue(varName, stem1);
            } else {
                Object arg1 = node.evaluate(state);
                if (!isStem(arg1)) {
                    throw new IllegalArgumentException(informativeMessage);
                }
                stem1 = (StemVariable) arg1;
            }
        }
        if (stem1 == null) {
            throw new MissingArgumentException("the first argument is not a variable in this workspace.");
        }
        return stem1;
    }

    /**
     * This will look at the resource name and decide if it is in a VFS and resolve it against that.
     * If not, it will try to resolve it as a file name against the file system if this
     * is not in server mode. This merely returns a null if there is no such resource. It will
     * throw an exception if the resource refers to a virtual file and there are no providers
     * for that namespace.
     *
     * @param resourceName
     * @param state
     * @return
     */
    protected VFSEntry resolveResourceToFile(String resourceName, int type, State state) {
        if (state.isVFSFile(resourceName)) {
            if (!state.hasVFSProviders()) {
                throw new QDLException("unkonwn virtual file system for resource '" + resourceName + "'");
            }
            try {
                return state.getFileFromVFS(resourceName, type);
            } catch (Throwable t) {
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                throw new QDLException("could not file from VFS:" + t.getMessage(), t);
            }
        }
        return null;
    }

    /**
     * get a polyad or dyad (for the operator) from the {@link FunctionReferenceNode}.
     * You must still set any arguments, but the type and name should be correctly set.
     *
     * @param state
     * @param frNode
     * @return
     */
    public static ExpressionImpl getOperator(State state, FunctionReferenceNode frNode, int nAry) {
        ExpressionImpl operator;
        String operatorName = frNode.getFunctionName();
        if (state.getOpEvaluator().isMathOperator(operatorName)) {
            operator = new Dyad(state.getOperatorType(operatorName));
        } else {
            if (state.getMetaEvaluator().isBuiltInFunction(operatorName)) {
                operator = new Polyad(operatorName);
            } else {
                //FunctionRecord functionRecord = state.getFTStack().get(operatorName, nAry); // It's a dyad!
                FR_WithState fr_withState = state.resolveFunction(operatorName, nAry, true); // It's a dyad!

                if (fr_withState == null || fr_withState.functionRecord == null) {
                    throw new UndefinedFunctionException("'" + operatorName + "' is not defined with " + nAry + " arguments");
                }
                Polyad polyad1 = new Polyad(operatorName);
                polyad1.setBuiltIn(false); // or it will not execute!
                operator = polyad1;
            }
        }
        return operator;
    }

    public static final int FILE_OP_AUTO = -100; // Let the system determine it.
    public static final int FILE_OP_BINARY = 0; // file is treated as b64 string
    public static final int FILE_OP_TEXT_STEM = 1; //File is treated as a stem of lines
    public static final int FILE_OP_TEXT_INI = 2; //File is treated as an initialization file
    public static final int FILE_OP_TEXT_STRING = -1; // File is treated as one long string

    /**
     * Create an unused name for a function. Note that this <i>cannot</i>
     * produce a legal function name since the base 32 encoding slaps on trailing
     * "=". This assures there will never be a collision with the ambient
     * state.
     *
     * @param state
     * @return
     */
    public static String tempFname(State state) {
        Base32 base32 = new Base32((byte) '=');
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        String tempName = base32.encodeToString(bytes);
        for (int i = 0; i < 10; i++) {
            if (!state.getFTStack().containsKey(new FKey(tempName,-1))) {
                return tempName;
            }
            tempName = base32.encodeToString(bytes);

        }
        throw new IllegalStateException("cannot create anonymous function");
    }

    /**
     * This will take a node that is either a function reference, {@link FunctionDefinitionStatement}
     * or perhaps a {@link LambdaDefinitionNode} and determine the right {@link FunctionReferenceNode},
     * updating the state (including adding local state as needed for the duration of the evaluation).
     * It will also throw an exception if the argument is not of the right type.<p/><p/>
     * Any place you want to use a function as an argument, pass it to this and let
     * it do the work.
     *
     * @param state
     * @param arg0
     * @return
     */
    public FunctionReferenceNode getFunctionReferenceNode(State state, StatementWithResultInterface arg0, boolean pushNewState) {
        FunctionReferenceNode frn = null;
        if (arg0 instanceof LambdaDefinitionNode) {
            LambdaDefinitionNode lds = (LambdaDefinitionNode) arg0;
            if (!lds.hasName()) {
                lds.getFunctionRecord().name = tempFname(state);
            }
            if (pushNewState) {
                FTable ft = new FTable();
                ft.put(lds.getFunctionRecord());
                state.getFTStack().push(ft);

/*
                FunctionTableImpl ft = new FunctionTableImpl();
                ft.put(lds.getFunctionRecord());
                state.getFTStack().push(ft);
*/
            } else {
                lds.evaluate(state);
            }
            frn = new FunctionReferenceNode();
            frn.setFunctionName(lds.getFunctionRecord().name);
        }

        if ((arg0 instanceof FunctionDefinitionStatement)) {
            LambdaDefinitionNode lds = new LambdaDefinitionNode(((FunctionDefinitionStatement) arg0));
            if (!lds.hasName()) {
                lds.getFunctionRecord().name = tempFname(state);
            }
            if (pushNewState) {
                //FunctionTableImpl ft = new FunctionTableImpl();
                FTable ft = new FTable();
                ft.put(lds.getFunctionRecord());
                state.getFTStack().push(ft);
            } else {
                lds.evaluate(state);
            }
            frn = new FunctionReferenceNode();
            frn.setFunctionName(lds.getFunctionRecord().name);

        }
        if (arg0 instanceof FunctionReferenceNode) {
            frn = (FunctionReferenceNode) arg0;
        }

        if (frn == null) {
            throw new IllegalArgumentException("the argument is not a function reference or lambda");

        }
        return frn;
    }


    protected FunctionReferenceNode getFunctionReferenceNode(State state, StatementWithResultInterface arg0) {
        return getFunctionReferenceNode(state, arg0, false);
    }

    /**
     * If a function gets an argument whichs should not be a Java null, then this will
     * try to track down the variable reference.
     *
     * @param arg
     * @param swri
     */
    public static void checkNull(Object arg, StatementWithResultInterface swri) {
        if (arg != null) {
            return;
        }
        if (swri instanceof VariableNode) {
            VariableNode vNode = (VariableNode) swri;
            throw new UnknownSymbolException("unknown symbol '" + vNode.getVariableReference() + "'");
        }
        throw new UnknownSymbolException("unknown symbol");
    }

    /**
     * Check for nulls but log any error
     *
     * @param arg1
     * @param swri
     * @param state
     */
    public static void checkNull(Object arg1, StatementWithResultInterface swri, State state) {
        if (arg1 == null) {
            UnknownSymbolException unknownSymbolException;
            String message = "unknown symbol";
            if (swri instanceof VariableNode) {
                message = message + " '" + ((VariableNode) swri).getVariableReference() + "'";
                unknownSymbolException = new UnknownSymbolException(message);
            } else {
                unknownSymbolException = new UnknownSymbolException(message);
            }
            if (state.getLogger() != null) {
                // Check they have logging in the first place before writing to it.
                state.getLogger().error(message);
            }
            throw unknownSymbolException;
        }
    }
}
