package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:19 AM
 */
public class StemEvaluator extends AbstractFunctionEvaluator {
    public static final int STEM_FUNCTION_BASE_VALUE = 2000;
    public static String SIZE = "size";
    public static final int SIZE_TYPE = 1 + STEM_FUNCTION_BASE_VALUE;


    public static String TO_STEM = "to_stem";
    public static final int TO_STEM_TYPE = 3 + STEM_FUNCTION_BASE_VALUE;

    public static String TO_LIST = "to_list";
    public static final int TO_LIST_TYPE = 4 + STEM_FUNCTION_BASE_VALUE;

    public static String REMOVE = "remove";
    public static final int REMOVE_TYPE = 5 + STEM_FUNCTION_BASE_VALUE;


    public static String IS_DEFINED = "is_defined";
    public static final int IS_DEFINED_TYPE = 6 + STEM_FUNCTION_BASE_VALUE;

    public static String SET_DEFAULT = "set_default";
    public static final int SET_DEFAULT_TYPE = 7 + STEM_FUNCTION_BASE_VALUE;


    // Key functions
    public static String COMMON_KEYS = "common_keys";
    public static final int COMMON_KEYS_TYPE = 100 + STEM_FUNCTION_BASE_VALUE;

    public static String EXCLUDE_KEYS = "exclude_keys";
    public static final int EXCLUDE_KEYS_TYPE = 101 + STEM_FUNCTION_BASE_VALUE;

    public static String GET_KEYS = "get_keys";
    public static final int GET_KEYS_TYPE = 102 + STEM_FUNCTION_BASE_VALUE;

    public static String HAS_KEYS = "has_keys";
    public static final int HAS_KEYS_TYPE = 103 + STEM_FUNCTION_BASE_VALUE;


    public static String INCLUDE_KEYS = "include_keys";
    public static final int INCLUDE_KEYS_TYPE = 104 + STEM_FUNCTION_BASE_VALUE;


    public static String RENAME_KEYS = "rename_keys";
    public static final int RENAME_KEYS_TYPE = 105 + STEM_FUNCTION_BASE_VALUE;

    public static String MASK = "mask";
    public static final int MASK_TYPE = 106 + STEM_FUNCTION_BASE_VALUE;


    @Override
    public int getType(String name) {
        if (name.equals(SIZE)) return SIZE_TYPE;
        if (name.equals(MASK)) return MASK_TYPE;
        if (name.equals(SET_DEFAULT)) return SET_DEFAULT_TYPE;
        if (name.equals(COMMON_KEYS)) return COMMON_KEYS_TYPE;
        if (name.equals(EXCLUDE_KEYS)) return EXCLUDE_KEYS_TYPE;
        if (name.equals(GET_KEYS)) return GET_KEYS_TYPE;
        if (name.equals(HAS_KEYS)) return HAS_KEYS_TYPE;
        if (name.equals(INCLUDE_KEYS)) return INCLUDE_KEYS_TYPE;
        if (name.equals(RENAME_KEYS)) return RENAME_KEYS_TYPE;
        if (name.equals(TO_STEM)) return TO_STEM_TYPE;
        if (name.equals(TO_LIST)) return TO_LIST_TYPE;
        if (name.equals(REMOVE)) return REMOVE_TYPE;
        if (name.equals(IS_DEFINED)) return IS_DEFINED_TYPE;
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getOperatorType()) {
            case SIZE_TYPE:
                doSize(polyad, state);
                return true;
            case SET_DEFAULT_TYPE:
                doSetDefault(polyad, state);
                return true;
            case MASK_TYPE:
                doMask(polyad, state);
                return true;
            case COMMON_KEYS_TYPE:
                doCommonKeys(polyad, state);
                return true;
            case GET_KEYS_TYPE:
                doGetKeys(polyad, state);
                return true;
            case HAS_KEYS_TYPE:
                doHasKeys(polyad, state);
                return true;
            case INCLUDE_KEYS_TYPE:
                doIncludeKeys(polyad, state);
                return true;
            case EXCLUDE_KEYS_TYPE:
                doExcludeKeys(polyad, state);
                return true;
            case RENAME_KEYS_TYPE:
                doRenamekeys(polyad, state);
                return true;
            case TO_STEM_TYPE:
                doToStem(polyad, state);
                return true;
            case TO_LIST_TYPE:
                doMakeIndex(polyad, state);
                return true;
            case REMOVE_TYPE:
                doRemove(polyad, state);
                return true;
            case IS_DEFINED_TYPE:
                isDefined(polyad, state);
                return true;
        }
        return false;
    }

    protected void doSize(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + SIZE + " function requires 1 argument");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        long size = 0;
        if (isStem(arg)) {
            size = new Long(((StemVariable) arg).size());
        }
        if (arg instanceof String) {
            size = new Long(arg.toString().length());
        }
        polyad.setResult(size);
        polyad.setResultType(Constant.LONG_TYPE);
        polyad.setEvaluated(true);

    }

    protected void doGetKeys(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + GET_KEYS + " function requires 1 argument");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        long size = 0;
        if (!isStem(arg)) {
            polyad.setResult(new StemVariable()); // just an empty stem
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable stemVariable = (StemVariable) arg;
        StemVariable out = new StemVariable();
        int i = 0;
        for (String key : stemVariable.keySet()) {
            out.put(Integer.toString(i++), key);
        }
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);

    }

    /**
     * has_keys(stem. var | keysList.) returns a var or  boolean stem if the key in the list is a key in the stem
     *
     * @param polyad
     * @param state
     */
    protected void doHasKeys(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + HAS_KEYS + " function requires 2 arguments");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + HAS_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.getArgumments().get(1).evaluate(state);
        Object arg2 = polyad.getArgumments().get(1).getResult();

        if (!isStem(arg2)) {
            polyad.setResult(target.containsKey(arg2.toString()));
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable keyList = (StemVariable) arg2;
        StemVariable result = new StemVariable();
        for (int i = 0; i < keyList.size(); i++) {
            // for loop to be sure that everything is done in order.
            String index = Integer.toString(i);
            if (!keyList.containsKey(index)) {
                throw new IllegalArgumentException("Error: the set of supplied keys is not a list");
            }
            result.put(index, target.containsKey(keyList.get(index)));
        }
        polyad.setResult(result);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }


    protected void doMakeIndex(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + TO_LIST + " function requires 1 argument");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!(arg instanceof Long)) {
            polyad.setResult(new StemVariable()); // just an empty stem
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        long size = (Long) arg;

        StemVariable out = new StemVariable();
        for (int i = 0; i < size; i++) {
            out.put(Integer.toString(i), new Long(i));
        }
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);

    }

    /**
     * Takes a list of arguments (possible stem variables) and creates a new stem from it.
     * The scalars are added with cardinal indexes and stems will have their values added as well in this fashion.
     * (Their keys are not added because you ocould have just added to the existing stem variable).
     *
     * @param polyad
     */
    protected void doToStem(Polyad polyad, State state) {
        StemVariable out = new StemVariable();
        Long index = 0L;
        for (ExpressionNode arg : polyad.getArgumments()) {
            Object r = arg.evaluate(state);
            if (!isStem(r)) {
                out.put(index.toString(), r);
                index++;
            } else {
                StemVariable tempStem = (StemVariable) r;
                for (String key : tempStem.keySet()) {
                    out.put(index.toString(), tempStem.get(key));
                    index++;
                }
            }
        }
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    /**
     * Remove the entire stem from the symbol table.
     *
     * @param polyad
     * @param state
     */
    protected void doRemove(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + REMOVE + " function requires 1 argument");
        }
        VariableNode variableNode = (VariableNode) polyad.getArgumments().get(0);
        variableNode.evaluate(state);
        state.getSymbolStack().remove(variableNode.getVariableReference());
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    protected void isDefined(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + IS_DEFINED + " function requires 1 argument");
        }

        VariableNode variableNode = (VariableNode) polyad.getArgumments().get(0);
        variableNode.evaluate(state);
        boolean isDef = state.getSymbolStack().isDefined(variableNode.getVariableReference());
        polyad.setResult(isDef);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * <code>include_keys(stem., var | list.);</code><br/<br/> include keys on the right in the resulting stem
     *
     * @param polyad
     * @param state
     */
    protected void doIncludeKeys(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + INCLUDE_KEYS + " function requires 2 arguments");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + INCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.getArgumments().get(1).evaluate(state);
        Object arg2 = polyad.getArgumments().get(1).getResult();

        if (!isStem(arg2)) {
            StemVariable result = new StemVariable();
            if (target.containsKey(arg2.toString())) {
                result.put(arg2.toString(), target.get(arg2.toString()));
            }
            polyad.setResult(result);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable keyList = (StemVariable) arg2;
        StemVariable result = new StemVariable();
        for (int i = 0; i < keyList.size(); i++) {
            // for loop to be sure that everything is done in order.
            String index = Integer.toString(i);
            if (!keyList.containsKey(index)) {
                throw new IllegalArgumentException("Error: the set of supplied keys is not a list");
            }
            String currentKey = keyList.getString(index);
            if (target.containsKey(currentKey)) {
                result.put(currentKey, target.get(currentKey));
            }
        }
        polyad.setResult(result);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * <code>exclude_keys(stem., var | list.)</code><br/><br/> remove all the keys in list. from the stem.
     *
     * @param polyad
     * @param state
     */
    protected void doExcludeKeys(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + EXCLUDE_KEYS + " function requires 2 arguments");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + EXCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.getArgumments().get(1).evaluate(state);
        Object arg2 = polyad.getArgumments().get(1).getResult();

        if (!isStem(arg2)) {
            StemVariable result = new StemVariable();
            String excluded = arg2.toString();
            for (String ndx : target.keySet()) {
                result.put(ndx, target.get(ndx));
            }
            result.remove(excluded);
            polyad.setResult(result);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable keyList = (StemVariable) arg2;
        StemVariable result = new StemVariable();
        for (String key : target.keySet()) {
            if (!keyList.containsValue(key)) {
                result.put(key, target.get(key));
            }
        }
        polyad.setResult(result);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * <code>rename_keys(stem., var | list., );</code><br/><br/> list. contains entries of the form list.old = new.
     * The result is that each key in the stem. is renamed, but the values are not changed.
     * If a key is in the list at the right and does not correspond to one on the left, it is skipped.
     *
     * @param polyad
     * @param state
     */
    protected void doRenamekeys(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + RENAME_KEYS + " function requires 2 arguments");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + RENAME_KEYS + " command requires a stem as its first argument.");
        }
        polyad.getArgumments().get(1).evaluate(state);

        Object arg2 = polyad.getArgumments().get(1).getResult();
        polyad.getArgumments().get(1).evaluate(state);
        if (!isStem(arg2)) {
            throw new IllegalArgumentException("Error: The " + RENAME_KEYS + " command requires a stem as its second argument.");
        }

        StemVariable target = (StemVariable) arg;


        StemVariable newKeys = (StemVariable) arg2;
        for (String key : newKeys.keySet()) {
            if (target.containsKey(key)) {
                String newKey = newKeys.getString(key);
                if (target.containsKey(newKey)) {
                    throw new IllegalArgumentException("Error: The current stem already has a key named \"" + newKeys.getString(key)
                            + "\". This operation does not replace values, it only renames existing keys.");
                }
                target.put(newKey, target.get(key));
                target.remove(key);
            }
        }

        polyad.setResult(target);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * <code>common_keys(stem1., stem2.)</code><br/><br/> Return a list of keys common to both stems.
     *
     * @param polyad
     * @param state
     */
    protected void doCommonKeys(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + COMMON_KEYS + " function requires 2 arguments");
        }
        polyad.getArgumments().get(0).evaluate(state);
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + COMMON_KEYS + " command requires a stem as its first argument.");
        }
        polyad.getArgumments().get(1).evaluate(state);

        Object arg2 = polyad.getArgumments().get(1).getResult();
        polyad.getArgumments().get(1).evaluate(state);
        if (!isStem(arg2)) {
            throw new IllegalArgumentException("Error: The " + COMMON_KEYS + " command requires a stem as its second argument.");
        }

        StemVariable target = (StemVariable) arg;
        StemVariable result = new StemVariable();
        int index = 0;
        StemVariable newKeys = (StemVariable) arg2;
        for (String key : newKeys.keySet()) {
            if (target.containsKey(key)) {
                String currentIndex = Integer.toString(index++);
                result.put(currentIndex, key);
            }
        }

        polyad.setResult(result);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * Sets the default value for a stem. returns the default value set.
     *
     * @param polyad
     * @param state
     */
    protected void doSetDefault(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + SET_DEFAULT + " function requires 2 arguments");
        }
        polyad.getArgumments().get(0).evaluate(state);
        polyad.getArgumments().get(1).evaluate(state);
        Object r = polyad.getArgumments().get(0).getResult();
        StemVariable stemVariable;
        if (r == null) {
            stemVariable = new StemVariable();
            VariableNode variableNode = (VariableNode) polyad.getArgumments().get(0);
            state.getSymbolStack().setStemVariable(variableNode.getVariableReference(), stemVariable);
        } else {
            if (!isStem(r)) {
                throw new IllegalArgumentException("Error: the " + SET_DEFAULT + " command accepts   only a stem variable as its first argument.");
            }
            stemVariable = (StemVariable) r;

        }
        Object defaultValue = polyad.getArgumments().get(1).getResult();
        if (isStem(defaultValue)) {
            throw new IllegalArgumentException("Error: the " + SET_DEFAULT + " command accepts only a scalar as its second argument.");
        }
        stemVariable.setDefaultValue(defaultValue);
        polyad.setResult(defaultValue);
        polyad.setResultType(polyad.getArgumments().get(1).getResultType());
        polyad.setEvaluated(true);
    }

    protected void doMask(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + MASK + " function requires 2 arguments");
        }
        polyad.getArgumments().get(0).evaluate(state);
        polyad.getArgumments().get(1).evaluate(state);
        Object obj1 = polyad.getArgumments().get(0).getResult();
        Object obj2 = polyad.getArgumments().get(1).getResult();
        if (!areAllStems(obj1, obj2)) {
            throw new IllegalArgumentException("Error: the " + MASK + " requires both arguments be stem variables");
        }
        StemVariable stem1 = (StemVariable) obj1;
        StemVariable stem2 = (StemVariable) obj2;
        StemVariable result = new StemVariable();
        for (String key : stem2.keySet()) {
            if (!stem1.containsKey(key)) {
                throw new IllegalArgumentException("Error: \"" + key + "\" is not a key in the first stem. " +
                        "Every key in the second argument must be a key in the first.");
            }
            Object rawBit = stem2.get(key);
            if (!isBoolean(rawBit)) {
                throw new IllegalArgumentException("Error: Every value of the second argument must be boolean");
            }
            Boolean b = (Boolean) rawBit;
            if (b) {
                result.put(key, stem1.get(key));
            }
        }
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setResult(result);
        polyad.setEvaluated(true);

    }
}
