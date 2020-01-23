package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  10:59 AM
 */
public abstract class AbstractFunctionEvaluator implements EvaluatorInterface {


    public boolean isServerMode() {
        return serverMode;
    }

    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
    }

    boolean serverMode = false;
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

    protected boolean areAllBoolean(Object... objects){
        for(Object arg : objects){
            if(!isBoolean(arg)) return false;
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
       protected boolean isNumber(Object arg){
           return (arg instanceof Long) || (arg instanceof BigDecimal);
       }

       protected boolean isBigDecimal(Object obj){
        return obj instanceof BigDecimal;
       }
       protected boolean areAnyBigDecimals(Object... objects){
        for(Object arg : objects){
                if(isBigDecimal(arg)) return true;
        }
        return false;
       }

    /**
     * How to compare two big decimals requires some work.
     * @param a
     * @param b
     * @return
     */
       protected boolean bdEquals(BigDecimal a, BigDecimal b){
        BigDecimal r = a.subtract(b);
        return r.compareTo(BigDecimal.ZERO) == 0;
       }

    protected boolean areAllNumbers(Object... objects) {
          for (Object arg : objects) {
              if (!isNumber(arg)) return false;
          }
          return true;
      }
    protected BigDecimal toBD(Object obj){
        if(!isNumber(obj)) throw new IllegalArgumentException("Error: \"" + obj + "\" is not a number");
        if(obj instanceof BigDecimal) return (BigDecimal) obj;
        if(obj instanceof Long) return new BigDecimal((Long) obj);
        if(obj instanceof Integer) return new BigDecimal((Integer) obj);
        throw new IllegalArgumentException("Error: \"" + obj + "\" is not a number");
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

    // ToDO make a processN method from these. Just have to scratch head about certian bookkeeping.
    protected void process1(ExpressionImpl polyad,
                            fPointer pointer,
                            String name,
                            State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + name + " function requires 1 argument");
        }
        Object arg1 = polyad.getArgumments().get(0).evaluate(state);
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
                            State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + name + " function requires 2 arguments");
        }
        Object arg1 = polyad.getArgumments().get(0).evaluate(state);
        Object arg2 = polyad.getArgumments().get(1).evaluate(state);

        if (areNoneStems(arg1, arg2)) {
            fpResult result = pointer.process(arg1, arg2);
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
            fpResult r = pointer.process(stem1.get(key), stem2.get(key));
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
        Object arg1 = polyad.getArgumments().get(0).evaluate(state);
        Object arg2 = polyad.getArgumments().get(1).evaluate(state);
        Object arg3 = polyad.getArgumments().get(2).evaluate(state);

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
}
