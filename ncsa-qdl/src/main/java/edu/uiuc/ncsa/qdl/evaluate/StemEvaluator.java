package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.StemEntry;
import edu.uiuc.ncsa.qdl.util.StemList;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:19 AM
 */
public class StemEvaluator extends AbstractFunctionEvaluator {
    public static final int STEM_FUNCTION_BASE_VALUE = 2000;
    public static String SIZE = "size";
    public static final int SIZE_TYPE = 1 + STEM_FUNCTION_BASE_VALUE;


    public static String MAKE_INDICES = "indices";
    public static final int MAKE_INDICES_TYPE = 4 + STEM_FUNCTION_BASE_VALUE;

    public static String REMOVE = "remove";
    public static final int REMOVE_TYPE = 5 + STEM_FUNCTION_BASE_VALUE;


    public static String IS_DEFINED = "is_defined";
    public static final int IS_DEFINED_TYPE = 6 + STEM_FUNCTION_BASE_VALUE;

    public static String SET_DEFAULT = "set_default";
    public static final int SET_DEFAULT_TYPE = 7 + STEM_FUNCTION_BASE_VALUE;

    public static String BOX = "box";
    public static final int BOX_TYPE = 8 + STEM_FUNCTION_BASE_VALUE;

    public static String UNBOX = "unbox";
    public static final int UNBOX_TYPE = 9 + STEM_FUNCTION_BASE_VALUE;

    public static String UNION = "union";
    public static final int UNION_TYPE = 10 + STEM_FUNCTION_BASE_VALUE;

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

    // list functions


    public static String LIST_APPEND = "list_append";
    public static final int LIST_APPEND_TYPE = 200 + STEM_FUNCTION_BASE_VALUE;

    public static String LIST_INSERT_AT = "list_insert_at";
    public static final int LIST_INSERT_AT_TYPE = 201 + STEM_FUNCTION_BASE_VALUE;

    public static String LIST_SUBSET = "list_subset";
    public static final int LIST_SUBSET_TYPE = 202 + STEM_FUNCTION_BASE_VALUE;

    public static String LIST_COPY = "list_copy";
    public static final int LIST_COPY_TYPE = 203 + STEM_FUNCTION_BASE_VALUE;

    public static String IS_LIST = "is_list";
    public static final int IS_LIST_TYPE = 204 + STEM_FUNCTION_BASE_VALUE;

    public static String TO_LIST = "to_list";
    public static final int TO_LIST_TYPE = 205 + STEM_FUNCTION_BASE_VALUE;

    // Conversions to/from JSON.
    public static String TO_JSON = "to_json";
    public static final int TO_JSON_TYPE = 300 + STEM_FUNCTION_BASE_VALUE;

    public static String FROM_JSON = "from_json";
    public static final int FROM_JSON_TYPE = 301 + STEM_FUNCTION_BASE_VALUE;


    public static String FUNC_NAMES[] = new String[]{SIZE, MAKE_INDICES, REMOVE, IS_DEFINED,
            SET_DEFAULT, BOX, UNBOX, UNION, COMMON_KEYS, EXCLUDE_KEYS, GET_KEYS, HAS_KEYS, INCLUDE_KEYS, RENAME_KEYS, MASK,
            LIST_APPEND, LIST_INSERT_AT, LIST_SUBSET, LIST_COPY, IS_LIST, TO_LIST, TO_JSON, FROM_JSON};

    public TreeSet<String> listFunctions() {
        TreeSet<String> names = new TreeSet<>();
        for (String key : FUNC_NAMES) {
            names.add(key + "()");
        }
        return names;
    }

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
        if (name.equals(MAKE_INDICES)) return MAKE_INDICES_TYPE;
        if (name.equals(REMOVE)) return REMOVE_TYPE;
        if (name.equals(IS_DEFINED)) return IS_DEFINED_TYPE;
        if (name.equals(BOX)) return BOX_TYPE;
        if (name.equals(UNBOX)) return UNBOX_TYPE;
        if (name.equals(UNION)) return UNION_TYPE;
        if (name.equals(IS_LIST)) return IS_LIST_TYPE;
        if (name.equals(LIST_APPEND)) return LIST_APPEND_TYPE;
        if (name.equals(LIST_INSERT_AT)) return LIST_INSERT_AT_TYPE;
        if (name.equals(LIST_SUBSET)) return LIST_SUBSET_TYPE;
        if (name.equals(LIST_COPY)) return LIST_COPY_TYPE;
        if (name.equals(TO_LIST)) return TO_LIST_TYPE;
        if (name.equals(TO_JSON)) return TO_JSON_TYPE;
        if (name.equals(FROM_JSON)) return FROM_JSON_TYPE;
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
                doRenameKeys(polyad, state);
                return true;
            case IS_LIST_TYPE:
                doIsList(polyad, state);
                return true;
            case TO_LIST_TYPE:
                doToList(polyad, state);
                return true;
            case LIST_APPEND_TYPE:
                doListAppend(polyad, state);
                return true;
            case LIST_COPY_TYPE:
                doListCopyOrInsert(polyad, state, false);
                return true;
            case LIST_INSERT_AT_TYPE:
                doListCopyOrInsert(polyad, state, true);
                return true;
            case LIST_SUBSET_TYPE:
                doListSubset(polyad, state);
                return true;
            case MAKE_INDICES_TYPE:
                doMakeIndex(polyad, state);
                return true;
            case REMOVE_TYPE:
                doRemove(polyad, state);
                return true;
            case BOX_TYPE:
                doBox(polyad, state);
                return true;
            case UNBOX_TYPE:
                doUnBox(polyad, state);
                return true;
            case IS_DEFINED_TYPE:
                isDefined(polyad, state);
                return true;
            case UNION_TYPE:
                doUnion(polyad, state);
                return true;
            case TO_JSON_TYPE:
                doToJSON(polyad, state);
                return true;
            case FROM_JSON_TYPE:
                doFromJSON(polyad, state);
                return true;
        }
        return false;
    }

    protected void doFromJSON(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: " + FROM_JSON + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        if (!isString(arg)) {
            throw new IllegalArgumentException("Error: " + FROM_JSON + " requires a string as its argument");
        }
        JSONObject jsonObject = null;
        StemVariable output = new StemVariable();
        try {
            jsonObject = JSONObject.fromObject((String) arg);
            output.fromJSON(jsonObject);
        } catch (Throwable t) {
            try {
                JSONArray array = JSONArray.fromObject((String) arg);
                output.fromJSON(array);
            } catch (Throwable tt) {
                // ok, so this is not valid JSON.
                throw new IllegalArgumentException("Error: " + FROM_JSON + " could not parse the argument as valid JSON");
            }
        }
        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);

    }

    protected void doToJSON(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size() ) {
            throw new IllegalArgumentException("Error: " + TO_JSON + " requires an argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("Error: " + TO_JSON + " requires a stem as its first argument");
        }
        int indent = -1;
        if (polyad.getArgumments().size() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (!isLong(arg2)) {
                throw new IllegalArgumentException("Error: " + TO_JSON + " requires an integer as its second argument");
            }
            Long argL = (Long) arg2;
            indent = argL.intValue(); // best we can do
        }
        JSONObject j = ((StemVariable) arg1).toJSON();
        if (0 < indent) {
            polyad.setResult(j.toString(indent));
        } else {
            polyad.setResult(j.toString());
        }
        polyad.setResultType(Constant.STRING_TYPE);
        polyad.setEvaluated(true);
    }


    /**
     * Do a union of stems.
     *
     * @param polyad
     * @param state
     */
    private void doUnion(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: the " + UNION + " function requires at least 1 argument");
        }
        StemVariable outStem = new StemVariable();

        for (int i = 0; i < polyad.getArgumments().size(); i++) {
            StemVariable stem = null;
            polyad.evalArg(i, state);
            if (polyad.getArgumments().get(i) instanceof VariableNode) {
                VariableNode vn = (VariableNode) polyad.getArgumments().get(i);
                if (!(vn.getResult() instanceof StemVariable)) {
                    throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
                }
                stem = (StemVariable) vn.getResult();
            }
            if ((polyad.getArgumments().get(i) instanceof StemVariable)) {
                stem = (StemVariable) polyad.getArgumments().get(i);
            }
            if (stem == null) {
                throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
            }
            outStem.union(stem);
        }
        polyad.setResult(outStem);
        polyad.setEvaluated(true);
        polyad.setResultType(Constant.STEM_TYPE);

    }

    private void doUnBox(Polyad polyad, State state) {
        if (1 != polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: the " + UNBOX + " function requires  1 argument");
        }
        polyad.evalArg(0, state);
        // should take either a stem or a variable reference to it.
        StemVariable stem = null;
        String varName = null;
        if (polyad.getArgumments().get(0) instanceof VariableNode) {
            VariableNode vn = (VariableNode) polyad.getArgumments().get(0);
            varName = vn.getVariableReference();
            if (!(vn.getResult() instanceof StemVariable)) {
                throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
            }
            stem = (StemVariable) vn.getResult();
        }
        if ((polyad.getArgumments().get(0) instanceof StemVariable)) {
            stem = (StemVariable) polyad.getArgumments().get(0);
        }
        if (stem == null) {
            throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
        }
        State localState = state.newModuleState();
        for (String key : stem.keySet()) {
            localState.setValue(key, stem.get(key));
        }
        // once all is said and done and none of this bombed copy it.. That way we don't leave the actual state in disaary
        for (String key : stem.keySet()) {
            state.setValue(key, localState.getValue(key));
        }
        if (varName != null) {
            state.remove(varName);
        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);

        polyad.setEvaluated(true);
    }


    /**
     * Take a collection of variables and stem them up, removing them from the symbole table.
     *
     * @param polyad
     * @param state
     */

    private void doBox(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: the " + BOX + " function requires at least 1 argument");
        }
        ArrayList<String> varNames = new ArrayList<>();
        StemVariable stem = new StemVariable();

        for (int i = 0; i < polyad.getArgumments().size(); i++) {
            polyad.evalArg(i, state);
            if (!(polyad.getArgumments().get(i) instanceof VariableNode)) {
                throw new IllegalArgumentException("Error: You must supply a list of variables to box.");
            }
            VariableNode vn = (VariableNode) polyad.getArgumments().get(i);
            varNames.add(vn.getVariableReference());
            stem.put(vn.getVariableReference(), vn.getResult());
        }
        for (String varName : varNames) {
            state.remove(varName);
        }
        polyad.setResult(stem);
        polyad.setEvaluated(true);
        polyad.setResultType(Constant.STEM_TYPE);
    }

    protected void doSize(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + SIZE + " function requires 1 argument");
        }
        polyad.evalArg(0, state);
        ;
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
        polyad.evalArg(0, state);
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
        polyad.evalArg(0, state);
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + HAS_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        Object arg2 = polyad.getArgumments().get(1).getResult();

        if (!isStem(arg2)) {
            polyad.setResult(target.containsKey(arg2.toString()));
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable result = target.hasKeys((StemVariable) arg2);
        polyad.setResult(result);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }


    protected void doMakeIndex(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + MAKE_INDICES + " function requires 1 argument");
        }
        polyad.evalArg(0, state);
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
    protected void doToList(Polyad polyad, State state) {
        StemVariable out = new StemVariable();
        StemList<StemEntry> stemList = new StemList<>();

        Long index = 0L;

        for (ExpressionNode arg : polyad.getArgumments()) {
            Object r = arg.evaluate(state);
            stemList.add(new StemEntry(index++, r));
        }
        out.setStemList(stemList);
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    protected void doListCopyOrInsert(Polyad polyad, State state, boolean doInsert) {
        if (5 != polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: the " + LIST_COPY + " function requires 5 arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("Error: " + LIST_COPY + " requires a stem as its first argument");
        }
        StemVariable souorceStem = (StemVariable) arg1;

        Object arg2 = polyad.evalArg(1, state);
        if (!isLong(arg2)) {
            throw new IllegalArgumentException("Error: " + LIST_COPY + " requires an integer as its second argument");
        }
        Long startIndex = (Long) arg2;

        Object arg3 = polyad.evalArg(2, state);
        if (!isLong(arg3)) {
            throw new IllegalArgumentException("Error: " + LIST_COPY + " requires an integer as its third argument");
        }
        Long length = (Long) arg3;


        Object arg4 = polyad.evalArg(3, state);
        if (!isStem(arg4)) {
            throw new IllegalArgumentException("Error: " + LIST_COPY + " requires a stem as its fourth argument");
        }
        StemVariable targetStem = (StemVariable) arg4;

        Object arg5 = polyad.evalArg(4, state);
        if (!isLong(arg5)) {
            throw new IllegalArgumentException("Error: " + LIST_COPY + " requires an integer as its fifth argument");
        }
        Long targetIndex = (Long) arg5;

        if (doInsert) {
            souorceStem.listInsertAt(startIndex, length, targetStem, targetIndex);
        } else {
            souorceStem.listCopy(startIndex, length, targetStem, targetIndex);
        }
        polyad.setResult(targetStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return;

    }

    protected void doListSubset(Polyad polyad, State state) {
        if (polyad.getArgumments().size() < 2 || 3 < polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: the " + LIST_SUBSET + " function requires  two or three arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("Error: " + LIST_SUBSET + " requires stem as its first argument");
        }
        StemVariable stem = (StemVariable) arg1;
        Object arg2 = polyad.evalArg(1, state);
        if (!isLong(arg2)) {
            throw new IllegalArgumentException("Error: " + LIST_SUBSET + " requires an integer as its second argument");
        }
        Long startIndex = (Long) arg2;
        Long endIndex = (long) stem.getStemList().size();
        if (polyad.getArgumments().size() == 3) {
            Object arg3 = polyad.evalArg(2, state);
            if (!isLong(arg3)) {
                throw new IllegalArgumentException("Error: " + LIST_SUBSET + " requires an integer as its third argument");
            }
            endIndex = (Long) arg3;
        }

        StemVariable outStem = stem.listSubset(startIndex, endIndex);
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    protected void doIsList(Polyad polyad, State state) {
        if (1 != polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: the " + IS_LIST + " function requires 1 argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("Error: " + IS_LIST + " requires stem as its first argument");
        }
        StemVariable stemVariable = (StemVariable) arg1;
        Boolean isList = stemVariable.isList();
        polyad.setResult(isList);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    protected void doListAppend(Polyad polyad, State state) {
        if (2 != polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: the " + LIST_APPEND + " function requires 2 arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        Object arg2 = polyad.evalArg(1, state);
        if (!(arg1 instanceof StemVariable)) {
            throw new IllegalArgumentException("Error: " + LIST_APPEND + " requires stem as its first argument");
        }
        StemVariable stem1 = (StemVariable) arg1;
        if (arg2 instanceof StemVariable) {
            stem1.listAppend((StemVariable) arg2);
        } else {
            stem1.listAppend(arg2);
        }
        polyad.setResult(stem1);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    /**
     * Remove the entire variable from the symbol table.
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
        state.remove(variableNode.getVariableReference());
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    protected void isDefined(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + IS_DEFINED + " function requires 1 argument");
        }

        VariableNode variableNode = (VariableNode) polyad.getArgumments().get(0);
        // Don't evaluate this because it might not exist (that's what we are testing for). Just check
        // if the name is defined.
        //    variableNode.evaluate(state);
        boolean isDef = state.isDefined(variableNode.getVariableReference());
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
        polyad.evalArg(0, state);
        ;
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + INCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        ;
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
        StemVariable result = target.includeKeys((StemVariable) arg2);
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
        polyad.evalArg(0, state);
        ;
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + EXCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        ;
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
        StemVariable result = target.excludeKeys((StemVariable) arg2);
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
    protected void doRenameKeys(Polyad polyad, State state) {
        if (polyad.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: the " + RENAME_KEYS + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        ;
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + RENAME_KEYS + " command requires a stem as its first argument.");
        }
        polyad.evalArg(1, state);
        ;

        Object arg2 = polyad.getArgumments().get(1).getResult();
        polyad.evalArg(1, state);
        ;
        if (!isStem(arg2)) {
            throw new IllegalArgumentException("Error: The " + RENAME_KEYS + " command requires a stem as its second argument.");
        }

        StemVariable target = (StemVariable) arg;
        target.renameKeys((StemVariable) arg2);

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
        polyad.evalArg(0, state);
        ;
        Object arg = polyad.getArgumments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("Error: The " + COMMON_KEYS + " command requires a stem as its first argument.");
        }
        polyad.evalArg(1, state);
        ;

        Object arg2 = polyad.getArgumments().get(1).getResult();
        polyad.evalArg(1, state);
        ;
        if (!isStem(arg2)) {
            throw new IllegalArgumentException("Error: The " + COMMON_KEYS + " command requires a stem as its second argument.");
        }

        StemVariable target = (StemVariable) arg;
        StemVariable result = target.commonKeys((StemVariable) arg2);

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
        StemVariable stemVariable;

        polyad.evalArg(0, state);
        Object r = polyad.getArgumments().get(0).getResult();
        if (r == null) {
            // this does not exist, create it
            stemVariable = new StemVariable();
            VariableNode variableNode = (VariableNode) polyad.getArgumments().get(0);
            state.setValue(variableNode.getVariableReference(), stemVariable);
        } else {
            // Other option is that this exists, so check that the person is trying to set the default
            // for a stem and not something else.

            if (!isStem(r)) {
                throw new IllegalArgumentException("Error: the " + SET_DEFAULT + " command accepts   only a stem variable as its first argument.");
            }
            stemVariable = (StemVariable) r;
        }

        polyad.evalArg(1, state);

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
        polyad.evalArg(0, state);
        ;
        polyad.evalArg(1, state);
        ;
        Object obj1 = polyad.getArgumments().get(0).getResult();
        Object obj2 = polyad.getArgumments().get(1).getResult();
        if (!areAllStems(obj1, obj2)) {
            throw new IllegalArgumentException("Error: the " + MASK + " requires both arguments be stem variables");
        }
        StemVariable stem1 = (StemVariable) obj1;
        StemVariable stem2 = (StemVariable) obj2;
        StemVariable result = stem1.mask(stem2);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setResult(result);
        polyad.setEvaluated(true);

    }


}
