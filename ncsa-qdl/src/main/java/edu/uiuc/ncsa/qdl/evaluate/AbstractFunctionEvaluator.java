package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.UnknownSymbolException;
import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  10:59 AM
 */
public abstract class AbstractFunctionEvaluator implements EvaluatorInterface {
    public static final String SYS_NAMESPACE = "sys";
    public static final String SYS_FQ = SYS_NAMESPACE + ImportManager.NS_DELIMITER;

    public abstract String[] getFunctionNames();
    public boolean isBuiltInFunction(String name){
         for(String x : getFunctionNames()){
             if(x.equals(name)) return  true;
         }
         return false;
    }
    /**
     * This takes and expression (with an operator type) and returns true if is handled in this evaluator
     * and false otherwise.
     */


    public abstract boolean evaluate(Polyad polyad, State state);

    protected boolean isStem(Object obj) {
        return obj instanceof StemVariable;
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
        for (Object arg : objects) {
            if (!isStem(arg)) return false;
        }
        return true;
    }

    protected boolean areNoneStems(Object... objects) {
        for (Object arg : objects) {
            if (isStem(arg)) return false;
        }
        return true;
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
        if (!isNumber(obj)) throw new IllegalArgumentException("Error: '" + obj + "' is not a number");
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Long) return new BigDecimal((Long) obj);
        if (obj instanceof Integer) return new BigDecimal((Integer) obj);
        throw new IllegalArgumentException("Error: '" + obj + "' is not a number");
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
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + name + " function requires 1 argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if(arg1 == null){
             throw new UnknownSymbolException("Error: Unknown symbol");
         }
        if (!isStem(arg1)) {
            fpResult r = pointer.process(arg1);
            finishExpr(polyad, r);
            return;
        }
        StemVariable stemVariable = (StemVariable) arg1;
        StemVariable outStem = new StemVariable();
        for (String key : stemVariable.keySet()) {
            outStem.put(key, pointer.process(stemVariable.get(key)).result);
        }
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    protected void process2(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state
    ) {
        process2(polyad, pointer, name, state, false);
    }

    protected void process2(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state,
                            boolean optionalArgs) {
        if (!optionalArgs && polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + name + " function requires 2 arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        Object arg2 = polyad.evalArg(1, state);
        if(arg1 == null || arg2 == null){
            throw new UnknownSymbolException("Error: Unknown symbol");
        }
        Object[] argList = new Object[polyad.getArgumments().size()];
        argList[0] = arg1;
        argList[1] = arg2;
        if (optionalArgs) {
            for (int i = 2; i < polyad.getArgumments().size(); i++) {
                argList[i] = polyad.getArgumments().get(i).evaluate(state);
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
        Set<String> keys = getCommonKeys(stem1, stem2);
        // now we loop -- note that we must still preserve which is the first and second argument
        // so all this is basically to figure out how to loop over what.
        for (String key : keys) {
            fpResult r;
            if (optionalArgs) {
                Object[] objects = new Object[polyad.getArgumments().size()];
                objects[0] = stem1.get(key);
                objects[1] = stem2.get(key);
                for (int i = 2; i < objects.length; i++) {
                    objects[i] = polyad.getArgumments().get(i).getResult();
                }
                r = pointer.process(objects);

            } else {
                r = pointer.process(stem1.get(key), stem2.get(key));
            }
            outStem.put(key, r.result);
        }
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    protected void process3(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state) {
        if (polyad.getArgumments().size() != 3) {
            throw new IllegalArgumentException("Error: the " + name + " function requires 3 arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        Object arg2 = polyad.evalArg(1, state);
        Object arg3 = polyad.evalArg(2, state);
        if(arg1 == null || arg2 == null || arg3 == null){
             throw new UnknownSymbolException("Error: Unknown symbol");
         }
        if (areNoneStems(arg1, arg2, arg3)) {
            fpResult result = pointer.process(arg1, arg2, arg3);
            finishExpr(polyad, result);
            return;
        }
        StemVariable stem1 = toStem(arg1);
        StemVariable stem2 = toStem(arg2);
        StemVariable stem3 = toStem(arg3);
        StemVariable outStem = new StemVariable();
        Set<String> keys = getCommonKeys(stem1, stem2, stem3);
        for (String key : keys) {
            fpResult r = pointer.process(stem1.get(key), stem2.get(key), stem3.get(key));
            outStem.put(key, r.result);
        }
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
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
    protected StemVariable getOrCreateStem(ExpressionNode node, State state, String informativeMessage) {
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
            throw new NFWException("Internal error: the supplied node is not a variable node. You probably supplied the wrong argument");
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
    protected VFSEntry resolveResourceToFile(String resourceName, State state) {
        if (state.isVFSFile(resourceName)) {
            if (!state.hasVFSProviders()) {
                throw new QDLException("Error: unkonwn virtual file system for resource '" + resourceName + "'");
            }
            try {
                return state.getFileFromVFS(resourceName);
            }catch (Throwable t){
                if(t instanceof RuntimeException){
                    throw (RuntimeException)t;
                }
                throw new QDLException("Error: could not file from VFS:" + t.getMessage(), t);
            }
        }
        return null;
    }

    public static final int FILE_OP_BINARY = 0; // file is treated as b64 string
    public static final int FILE_OP_TEXT_STEM = 1; //File is treated as a stem of lines
    public static final int FILE_OP_TEXT_STRING = -1; // File is treated as one long string
}
