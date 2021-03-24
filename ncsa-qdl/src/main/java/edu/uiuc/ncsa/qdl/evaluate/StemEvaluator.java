package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.RankException;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:19 AM
 */
public class StemEvaluator extends AbstractFunctionEvaluator {
    public static final String STEM_NAMESPACE = "stem";
    public static final String STEM_FQ = STEM_NAMESPACE + ImportManager.NS_DELIMITER;
    public static final int STEM_FUNCTION_BASE_VALUE = 2000;
    public static final String SIZE = "size";
    public static final String FQ_SIZE = STEM_FQ + SIZE;
    public static final int SIZE_TYPE = 1 + STEM_FUNCTION_BASE_VALUE;


    public static final String MAKE_INDICES = "indices";
    public static final String SHORT_MAKE_INDICES = "n";
    public static final String FQ_MAKE_INDICES = STEM_FQ + MAKE_INDICES;
    public static final int MAKE_INDICES_TYPE = 4 + STEM_FUNCTION_BASE_VALUE;


    public static final String REMOVE = "remove";
    public static final String FQ_REMOVE = STEM_FQ + REMOVE;
    public static final int REMOVE_TYPE = 5 + STEM_FUNCTION_BASE_VALUE;


    public static final String IS_DEFINED = "is_defined";
    public static final String FQ_IS_DEFINED = SYS_FQ + IS_DEFINED;
    public static final int IS_DEFINED_TYPE = 6 + STEM_FUNCTION_BASE_VALUE;

    public static final String SET_DEFAULT = "set_default";
    public static final String FQ_SET_DEFAULT = STEM_FQ + SET_DEFAULT;
    public static final int SET_DEFAULT_TYPE = 7 + STEM_FUNCTION_BASE_VALUE;

    public static final String BOX = "box";
    public static final String FQ_BOX = STEM_FQ + BOX;
    public static final int BOX_TYPE = 8 + STEM_FUNCTION_BASE_VALUE;

    public static final String UNBOX = "unbox";
    public static final String FQ_UNBOX = STEM_FQ + UNBOX;
    public static final int UNBOX_TYPE = 9 + STEM_FUNCTION_BASE_VALUE;

    public static final String UNION = "union";
    public static final String FQ_UNION = STEM_FQ + UNION;
    public static final int UNION_TYPE = 10 + STEM_FUNCTION_BASE_VALUE;

    public static final String VAR_TYPE = "var_type";
    public static final String FQ_VAR_TYPE = SYS_FQ + VAR_TYPE;
    public static final int VAR_TYPE_TYPE = 11 + STEM_FUNCTION_BASE_VALUE;

    public static final String HAS_VALUE = "has_value";
    public static final String FQ_HAS_VALUE = STEM_FQ + HAS_VALUE;
    public static final int HAS_VALUE_TYPE = 12 + STEM_FUNCTION_BASE_VALUE;


    // Key functions
    public static final String COMMON_KEYS = "common_keys";
    public static final String FQ_COMMON_KEYS = STEM_FQ + COMMON_KEYS;
    public static final int COMMON_KEYS_TYPE = 100 + STEM_FUNCTION_BASE_VALUE;

    public static final String EXCLUDE_KEYS = "exclude_keys";
    public static final String FQ_EXCLUDE_KEYS = STEM_FQ + EXCLUDE_KEYS;
    public static final int EXCLUDE_KEYS_TYPE = 101 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_KEYS = "list_keys";
    public static final String FQ_LIST_KEYS = STEM_FQ + LIST_KEYS;
    public static final int LIST_KEYS_TYPE = 102 + STEM_FUNCTION_BASE_VALUE;

    public static final String HAS_KEYS = "has_keys";
    public static final String FQ_HAS_KEYS = STEM_FQ + HAS_KEYS;
    public static final int HAS_KEYS_TYPE = 103 + STEM_FUNCTION_BASE_VALUE;


    public static final String INCLUDE_KEYS = "include_keys";
    public static final String FQ_INCLUDE_KEYS = STEM_FQ + INCLUDE_KEYS;
    public static final int INCLUDE_KEYS_TYPE = 104 + STEM_FUNCTION_BASE_VALUE;


    public static final String RENAME_KEYS = "rename_keys";
    public static final String FQ_RENAME_KEYS = STEM_FQ + RENAME_KEYS;
    public static final int RENAME_KEYS_TYPE = 105 + STEM_FUNCTION_BASE_VALUE;

    public static final String MASK = "mask";
    public static final String FQ_MASK = STEM_FQ + MASK;
    public static final int MASK_TYPE = 106 + STEM_FUNCTION_BASE_VALUE;

    public static final String KEYS = "keys";
    public static final String FQ_KEYS = STEM_FQ + KEYS;
    public static final int KEYS_TYPE = 107 + STEM_FUNCTION_BASE_VALUE;

    public static final String SHUFFLE = "shuffle";
    public static final String FQ_SHUFFLE = STEM_FQ + SHUFFLE;
    public static final int SHUFFLE_TYPE = 108 + STEM_FUNCTION_BASE_VALUE;

    public static final String UNIQUE_VALUES = "unique";
    public static final String FQ_UNIQUE_VALUES = STEM_FQ + UNIQUE_VALUES;
    public static final int UNIQUE_VALUES_TYPE = 109 + STEM_FUNCTION_BASE_VALUE;


    public static final String JOIN = "join";
    public static final String FQ_JOIN = STEM_FQ + JOIN;
    public static final int JOIN_TYPE = 110 + STEM_FUNCTION_BASE_VALUE;


    // list functions

    public static final String LIST_APPEND = "list_append";
    public static final String FQ_LIST_APPEND = STEM_FQ + LIST_APPEND;
    public static final int LIST_APPEND_TYPE = 200 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_INSERT_AT = "list_insert_at";
    public static final String FQ_LIST_INSERT_AT = STEM_FQ + LIST_INSERT_AT;
    public static final int LIST_INSERT_AT_TYPE = 201 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_SUBSET = "list_subset";
    public static final String FQ_LIST_SUBSET = STEM_FQ + LIST_SUBSET;
    public static final int LIST_SUBSET_TYPE = 202 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_COPY = "list_copy";
    public static final String FQ_LIST_COPY = STEM_FQ + LIST_COPY;
    public static final int LIST_COPY_TYPE = 203 + STEM_FUNCTION_BASE_VALUE;

    public static final String IS_LIST = "is_list";
    public static final String FQ_IS_LIST = STEM_FQ + IS_LIST;
    public static final int IS_LIST_TYPE = 204 + STEM_FUNCTION_BASE_VALUE;

    public static final String TO_LIST = "to_list";
    public static final String FQ_TO_LIST = STEM_FQ + TO_LIST;
    public static final int TO_LIST_TYPE = 205 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_STARTS_WITH = "list_starts_with";
    public static final String FQ_LIST_STARTS_WITH = STEM_FQ + LIST_STARTS_WITH;
    public static final int LIST_STARTS_WITH_TYPE = 206 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_REVERSE = "list_reverse";
    public static final String FQ_LIST_REVERSE = STEM_FQ + LIST_REVERSE;
    public static final int LIST_REVERSE_TYPE = 207 + STEM_FUNCTION_BASE_VALUE;

    // Conversions to/from JSON.
    public static final String TO_JSON = "to_json";
    public static final String FQ_TO_JSON = STEM_FQ + TO_JSON;
    public static final int TO_JSON_TYPE = 300 + STEM_FUNCTION_BASE_VALUE;

    public static final String FROM_JSON = "from_json";
    public static final String FQ_FROM_JSON = STEM_FQ + FROM_JSON;
    public static final int FROM_JSON_TYPE = 301 + STEM_FUNCTION_BASE_VALUE;

    /**
     * A list of the names that this Evaluator knows about. NOTE that this must be kept in sync
     * by the developer since it is used to determine if a function is built in or a user-defined function.
     */
    public static String FUNC_NAMES[] = new String[]{
            SIZE,
            JOIN,
            MAKE_INDICES,
            SHORT_MAKE_INDICES,
            HAS_VALUE,
            REMOVE,
            IS_DEFINED,
            VAR_TYPE,
            SET_DEFAULT,
            BOX,
            UNBOX,
            UNION,
            COMMON_KEYS,
            EXCLUDE_KEYS,
            LIST_KEYS,
            HAS_KEYS,
            INCLUDE_KEYS,
            RENAME_KEYS,
            SHUFFLE,
            MASK,
            KEYS,
            LIST_APPEND,
            LIST_INSERT_AT,
            LIST_SUBSET,
            LIST_COPY,
            LIST_REVERSE,
            LIST_STARTS_WITH,
            IS_LIST,
            TO_LIST,
            UNIQUE_VALUES,
            TO_JSON,
            FROM_JSON};
    public static String FQ_FUNC_NAMES[] = new String[]{
            FQ_SIZE,
            FQ_JOIN,
            FQ_MAKE_INDICES,
            FQ_HAS_VALUE,
            FQ_REMOVE,
            FQ_IS_DEFINED,
            FQ_VAR_TYPE,
            FQ_SET_DEFAULT,
            FQ_BOX,
            FQ_UNBOX,
            FQ_UNION,
            FQ_COMMON_KEYS,
            FQ_EXCLUDE_KEYS,
            FQ_LIST_KEYS,
            FQ_HAS_KEYS,
            FQ_INCLUDE_KEYS,
            FQ_RENAME_KEYS,
            FQ_SHUFFLE,
            FQ_MASK,
            FQ_KEYS,
            FQ_LIST_APPEND,
            FQ_LIST_INSERT_AT,
            FQ_LIST_SUBSET,
            FQ_LIST_COPY,
            FQ_LIST_REVERSE,
            FQ_LIST_STARTS_WITH,
            FQ_IS_LIST,
            FQ_TO_LIST,
            FQ_UNIQUE_VALUES,
            FQ_TO_JSON,
            FQ_FROM_JSON};


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
    public int getType(String name) {
        switch (name) {
            case JOIN:
            case FQ_JOIN:
                return JOIN_TYPE;
            case SIZE:
            case FQ_SIZE:
                return SIZE_TYPE;
            case SET_DEFAULT:
            case FQ_SET_DEFAULT:
                return SET_DEFAULT_TYPE;
            case HAS_VALUE:
            case FQ_HAS_VALUE:
                return HAS_VALUE_TYPE;
            case MASK:
            case FQ_MASK:
                return MASK_TYPE;
            case COMMON_KEYS:
            case FQ_COMMON_KEYS:
                return COMMON_KEYS_TYPE;
            case KEYS:
            case FQ_KEYS:
                return KEYS_TYPE;
            case LIST_KEYS:
            case FQ_LIST_KEYS:
                return LIST_KEYS_TYPE;
            case VAR_TYPE:
            case FQ_VAR_TYPE:
                return VAR_TYPE_TYPE;
            case HAS_KEYS:
            case FQ_HAS_KEYS:
                return HAS_KEYS_TYPE;
            case INCLUDE_KEYS:
            case FQ_INCLUDE_KEYS:
                return INCLUDE_KEYS_TYPE;
            case EXCLUDE_KEYS:
            case FQ_EXCLUDE_KEYS:
                return EXCLUDE_KEYS_TYPE;
            case RENAME_KEYS:
            case FQ_RENAME_KEYS:
                return RENAME_KEYS_TYPE;
            case SHUFFLE:
            case FQ_SHUFFLE:
                return SHUFFLE_TYPE;
            case UNIQUE_VALUES:
            case FQ_UNIQUE_VALUES:
                return UNIQUE_VALUES_TYPE;
            case IS_LIST:
            case FQ_IS_LIST:
                return IS_LIST_TYPE;
            case TO_LIST:
            case FQ_TO_LIST:
                return TO_LIST_TYPE;
            case LIST_APPEND:
            case FQ_LIST_APPEND:
                return LIST_APPEND_TYPE;
            case LIST_COPY:
            case FQ_LIST_COPY:
                return LIST_COPY_TYPE;
            case LIST_REVERSE:
            case FQ_LIST_REVERSE:
                return LIST_REVERSE_TYPE;
            case LIST_STARTS_WITH:
            case FQ_LIST_STARTS_WITH:
                return LIST_STARTS_WITH_TYPE;
            case LIST_INSERT_AT:
            case FQ_LIST_INSERT_AT:
                return LIST_INSERT_AT_TYPE;
            case LIST_SUBSET:
            case FQ_LIST_SUBSET:
                return LIST_SUBSET_TYPE;
            case MAKE_INDICES:
            case SHORT_MAKE_INDICES:
            case FQ_MAKE_INDICES:
                return MAKE_INDICES_TYPE;
            case REMOVE:
            case FQ_REMOVE:
                return REMOVE_TYPE;
            case BOX:
            case FQ_BOX:
                return BOX_TYPE;
            case UNBOX:
            case FQ_UNBOX:
                return UNBOX_TYPE;
            case IS_DEFINED:
            case FQ_IS_DEFINED:
                return IS_DEFINED_TYPE;
            case UNION:
            case FQ_UNION:
                return UNION_TYPE;
            case TO_JSON:
            case FQ_TO_JSON:
                return TO_JSON_TYPE;
            case FROM_JSON:
            case FQ_FROM_JSON:
                return FROM_JSON_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case JOIN:
            case FQ_JOIN:
                doJoin(polyad, state);
                return true;
            case VAR_TYPE:
            case FQ_VAR_TYPE:
                doVarType(polyad, state);
                return true;
            case SIZE:
            case FQ_SIZE:
                doSize(polyad, state);
                return true;
            case SET_DEFAULT:
            case FQ_SET_DEFAULT:
                doSetDefault(polyad, state);
                return true;
            case MASK:
            case FQ_MASK:
                doMask(polyad, state);
                return true;
            case COMMON_KEYS:
            case FQ_COMMON_KEYS:
                doCommonKeys(polyad, state);
                return true;
            case KEYS:
            case FQ_KEYS:
                doKeys(polyad, state);
                return true;
            case LIST_KEYS:
            case FQ_LIST_KEYS:
                doListKeys(polyad, state);
                return true;
            case HAS_KEYS:
            case FQ_HAS_KEYS:
                doHasKeys(polyad, state);
                return true;
            case INCLUDE_KEYS:
            case FQ_INCLUDE_KEYS:
                doIncludeKeys(polyad, state);
                return true;
            case HAS_VALUE:
            case FQ_HAS_VALUE:
                doIsMemberOf(polyad, state);
                return true;
            case EXCLUDE_KEYS:
            case FQ_EXCLUDE_KEYS:
                doExcludeKeys(polyad, state);
                return true;
            case RENAME_KEYS:
            case FQ_RENAME_KEYS:
                doRenameKeys(polyad, state);
                return true;
            case SHUFFLE:
            case FQ_SHUFFLE:
                shuffleKeys(polyad, state);
                return true;
            case UNIQUE_VALUES:
            case FQ_UNIQUE_VALUES:
                doUniqueValues(polyad, state);
                return true;
            case IS_LIST:
            case FQ_IS_LIST:
                doIsList(polyad, state);
                return true;
            case TO_LIST:
            case FQ_TO_LIST:
                doToList(polyad, state);
                return true;
            case LIST_APPEND:
            case FQ_LIST_APPEND:
                doListAppend(polyad, state);
                return true;
            case LIST_COPY:
            case FQ_LIST_COPY:
                doListCopyOrInsert(polyad, state, false);
                return true;
            case LIST_INSERT_AT:
            case FQ_LIST_INSERT_AT:
                doListCopyOrInsert(polyad, state, true);
                return true;
            case LIST_SUBSET:
            case FQ_LIST_SUBSET:
                doListSubset(polyad, state);
                return true;
            case LIST_REVERSE:
            case FQ_LIST_REVERSE:
                doListReverse(polyad, state);
                return true;
            case LIST_STARTS_WITH:
            case FQ_LIST_STARTS_WITH:
                doListStartsWith(polyad, state);
                return true;
            case MAKE_INDICES:
            case SHORT_MAKE_INDICES:
            case FQ_MAKE_INDICES:
                doMakeIndex(polyad, state);
                return true;
            case REMOVE:
            case FQ_REMOVE:
                doRemove(polyad, state);
                return true;
            case BOX:
            case FQ_BOX:
                doBox(polyad, state);
                return true;
            case UNBOX:
            case FQ_UNBOX:
                doUnBox(polyad, state);
                return true;
            case IS_DEFINED:
            case FQ_IS_DEFINED:
                isDefined(polyad, state);
                return true;
            case UNION:
            case FQ_UNION:
                doUnion(polyad, state);
                return true;
            case TO_JSON:
            case FQ_TO_JSON:
                doToJSON(polyad, state);
                return true;
            case FROM_JSON:
            case FQ_FROM_JSON:
                doFromJSON(polyad, state);
                return true;
        }
        return false;
    }

    protected void doListReverse(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("error:" + LIST_REVERSE + " requires a single argument.");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("error:" + LIST_REVERSE + " requires a stem as its argument.");
        }
        StemVariable input = (StemVariable) arg1;
        StemVariable output = new StemVariable();
        Iterator<StemEntry> iterator = input.getStemList().descendingIterator();
        while (iterator.hasNext()) {
            StemEntry s = iterator.next();
            output.listAppend(s.entry);
        }
        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    private void doUniqueValues(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("the " + UNIQUE_VALUES + " function requires 1 argument");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (!isStem(arg)) {
            polyad.setResult(new StemVariable()); // just an empty stem
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable stemVariable = (StemVariable) arg;
        StemVariable out = stemVariable.unique();

        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * Returns a list of indices. The results is conformable to the left argument and the values in it
     * are the indices of the right argument.
     *
     * @param polyad
     * @param state
     */
    // list_starts_with(['a','qrs','pqr'],['a','p','s','t'])
    protected void doListStartsWith(Polyad polyad, State state) {
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("Error: " + LIST_STARTS_WITH + " requires 2 arguments.");
        }
        Object leftArg = polyad.evalArg(0, state);
        if (!isStem(leftArg)) {
            throw new IllegalArgumentException("Error: " + LIST_STARTS_WITH + " requires a stem for the left argument.");
        }
        Object rightArg = polyad.evalArg(1, state);
        if (!isStem(rightArg)) {
            throw new IllegalArgumentException("Error: " + LIST_STARTS_WITH + " requires a stem for the right argument.");
        }

        StemVariable output = new StemVariable();
        StemVariable leftStem = (StemVariable) leftArg;
        StemVariable rightStem = (StemVariable) rightArg;
        for (long i = 0; i < leftStem.size(); i++) {
            boolean gotOne = false;
            for (long j = 0; j < rightStem.size(); j++) {
                if (leftStem.get(i).toString().startsWith(rightStem.get(j).toString())) {
                    output.put(i, j);
                    gotOne = true;
                    break;
                }
            }
            if (!gotOne) {
                output.put(i, -1L);
            }
        }
        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * Compute if the left argument is a member of the right argument. result is always conformable to the left argument.
     *
     * @param polyad
     * @param state
     */
    protected void doIsMemberOf(Polyad polyad, State state) {
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("Error: " + HAS_VALUE + " requires 2 arguments.");
        }
        Object leftArg = polyad.evalArg(0, state);
        Object rightArg = polyad.evalArg(1, state);
        // breask down tidily in to 4 cases.
        if (isStem(leftArg)) {
            StemVariable lStem = (StemVariable) leftArg;
            StemVariable result = new StemVariable(); // result is always conformable to left arg

            if (isStem(rightArg)) {
                StemVariable rStem = (StemVariable) rightArg;
                for (String lkey : lStem.keySet()) {
                    Boolean rc = Boolean.FALSE;
                    for (String rKey : rStem.keySet()) {
                        if (lStem.get(lkey).equals(rStem.get(rKey))) {
                            rc = Boolean.TRUE;
                            break;
                        }
                    }
                    result.put(lkey, rc);

                }
            } else {
                // check if each element in the left stem matches the value of the right arg.
                for (String lKey : lStem.keySet()) {
                    result.put(lKey, lStem.get(lKey).equals(rightArg) ? Boolean.TRUE : Boolean.FALSE); // got to finagle these are right Java objects
                }
            }
            polyad.setResult(result);
            polyad.setResultType(Constant.STEM_TYPE);

        } else {
            Boolean result = Boolean.FALSE;
            if (isStem(rightArg)) {
                StemVariable rStem = (StemVariable) rightArg;
                for (String rKey : rStem.keySet()) {
                    if (leftArg.equals(rStem.get(rKey))) {
                        result = Boolean.TRUE;
                        break;
                    }
                }
            } else {
                result = leftArg.equals(rightArg);
            }
            polyad.setResult(result);
            polyad.setResultType(Constant.BOOLEAN_TYPE);

        }
        polyad.setEvaluated(true);
    }


    /**
     * Get the type of the argument.
     *
     * @param polyad
     * @param state
     */
    public void doVarType(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            polyad.setResult(Constant.NULL_TYPE);
            polyad.setResultType(Constant.NULL_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        polyad.evalArg(0, state);
        if (polyad.getArgCount() == 1) {
            polyad.setResult(new Long(Constant.getType(polyad.getArguments().get(0).getResult())));
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable output = new StemVariable();
        output.put(0L, new Long(Constant.getType(polyad.getArguments().get(0).getResult())));
        for (int i = 1; i < polyad.getArgCount(); i++) {
            Object r = polyad.evalArg(i, state);
            output.put(new Long(i), new Long(Constant.getType(r)));
        }
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setResult(output);
        polyad.setEvaluated(true);
    }

    protected void doFromJSON(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException(FROM_JSON + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        if (!isString(arg)) {
            throw new IllegalArgumentException(FROM_JSON + " requires a string as its argument");
        }
        boolean convertKeys = false;

        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (!isBoolean(arg2)) {
                throw new IllegalArgumentException(FROM_JSON + " requires a boolean as its second argument if present.");
            }
            convertKeys = (Boolean) arg2;
        }
        JSONObject jsonObject = null;
        StemVariable output = new StemVariable();
        try {
            jsonObject = JSONObject.fromObject((String) arg);
            output.fromJSON(jsonObject, convertKeys);
        } catch (Throwable t) {
            try {
                JSONArray array = JSONArray.fromObject((String) arg);
                output.fromJSON(array);
            } catch (Throwable tt) {
                // ok, so this is not valid JSON.
                throw new IllegalArgumentException(FROM_JSON + " could not parse the argument as valid JSON");
            }
        }
        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);

    }

    protected void doToJSON(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException(TO_JSON + " requires an argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (arg1 == null) {
            throw new IllegalArgumentException("null argument passed to " + TO_JSON);
        }
        if (!isStem(arg1)) {
            throw new IllegalArgumentException(TO_JSON + " requires a stem as its first argument");
        }
        int indent = -1;
        boolean convertNames = false;
        /*
        Two args means the second is either a boolean for conversion or it an  int as the indent factor.
         */
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            boolean argOK = false; // got a valid input, boolean or long.
            if (isBoolean(arg2)) {
                argOK = true;
                convertNames = (Boolean) arg2;
            }

            if (isLong(arg2)) {
                Long argL = (Long) arg2;
                indent = argL.intValue(); // best we can do

            }
            if (!argOK) {
                throw new IllegalArgumentException(TO_JSON + " requires an integer or boolean as its second argument");
            }
        }
        /*
        3 arguments means second is the flag for conversion, 3rd is the indent factor
         */
        if (polyad.getArgCount() == 3) {
            Object arg2 = polyad.evalArg(1, state);
            if (isBoolean(arg2)) {
                convertNames = (Boolean) arg2;
            } else {
                throw new IllegalArgumentException(TO_JSON + " with 3 arguments requires a boolean as its second argument");
            }

            Object arg3 = polyad.evalArg(2, state);
            if (!isLong(arg3)) {
                throw new IllegalArgumentException(TO_JSON + " requires an integer as its third argument");
            }
            Long argL = (Long) arg3;
            indent = argL.intValue(); // best we can do
        }

        JSON j = ((StemVariable) arg1).toJSON(convertNames);
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
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + UNION + " function requires at least 1 argument");
        }
        StemVariable outStem = new StemVariable();

        for (int i = 0; i < polyad.getArgCount(); i++) {
            StemVariable stem = null;
            polyad.evalArg(i, state);
            if (polyad.getArguments().get(i) instanceof StatementWithResultInterface) {
                StatementWithResultInterface vn = polyad.getArguments().get(i);
                stem = (StemVariable) vn.getResult();
            }
            if ((polyad.getArguments().get(i) instanceof StemVariable)) {
                stem = (StemVariable) polyad.getArguments().get(i);
            }
            if (stem == null) {
                throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
            }
            outStem = outStem.union(stem);
        }
        polyad.setResult(outStem);
        polyad.setEvaluated(true);
        polyad.setResultType(Constant.STEM_TYPE);

    }

    private void doUnBox(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + UNBOX + " function requires  at least 1 argument");
        }
        polyad.evalArg(0, state);
        // should take either a stem or a variable reference to it.
        StemVariable stem = null;
        Boolean safeMode = Boolean.TRUE;
        if (polyad.getArgCount() == 2) {
            Object o = polyad.evalArg(1, state);
            if (!isBoolean(o)) {
                throw new IllegalArgumentException("The second argument of " + UNBOX + " must be a boolean.");
            }
            safeMode = (Boolean) o;
        }
        String varName = null;
        if (polyad.getArguments().get(0) instanceof VariableNode) {
            VariableNode vn = (VariableNode) polyad.getArguments().get(0);
            varName = vn.getVariableReference();
            if (!(vn.getResult() instanceof StemVariable)) {
                throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
            }
            stem = (StemVariable) vn.getResult();
        }
        if (polyad.getArguments().get(0) instanceof StemVariableNode) {
            stem = (StemVariable) polyad.evalArg(0, state);
        }

        if ((polyad.getArguments().get(0) instanceof StemVariable)) {
            stem = (StemVariable) polyad.getArguments().get(0);
        }
        if (stem == null) {
            throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
        }
        if (stem.getStemList().size() != 0) {
            throw new IllegalArgumentException("You can only unbox a stem without a list. List elements cannot be reasonably unboxed.");
        }
        // Make a safe copy of the state to unpack this in case something bombs
        List<String> keys = new ArrayList<>();
        State localState = state.newModuleState();
        for (String key : stem.keySet()) {
            Object ob = stem.get(key);
            key = key + (isStem(ob) ? STEM_INDEX_MARKER : "");
            if (safeMode) {
                if (state.isDefined(key)) {
                    throw new IllegalArgumentException("Error: name clash in safe mode for \"" + key + "\"");
                }
            }
            keys.add(key);
            localState.setValue(key, ob);
        }
        // once all is said and done and none of this bombed copy it. That way we don't leave the actual state in disaary
        for (String key : keys) {
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
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + BOX + " function requires at least 1 argument");
        }
        ArrayList<String> varNames = new ArrayList<>();
        StemVariable stem = new StemVariable();

        for (int i = 0; i < polyad.getArgCount(); i++) {
            polyad.evalArg(i, state);
            if (!(polyad.getArguments().get(i) instanceof VariableNode)) {
                throw new IllegalArgumentException("You must supply a list of variables to box.");
            }
            VariableNode vn = (VariableNode) polyad.getArguments().get(i);
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
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("the " + SIZE + " function requires 1 argument");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
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


    static final int all_keys = 0;
    static final int only_stems = 1;
    static final int only_scalars = 2;

    /**
     * Returns the keys in a stem as a list, filtering if wanted.
     *
     * @param polyad
     * @param state
     */
    protected void doListKeys(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException("the " + LIST_KEYS + " function requires at least one argument");
        }
        Object arg = polyad.evalArg(0, state);
        int returnScope = all_keys;
        int returnType = Constant.UNKNOWN_TYPE;
        boolean returnByType = false;
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (isBoolean(arg2)) {
                returnByType = false;
                if ((Boolean) arg2) {
                    returnScope = only_scalars;
                } else {
                    returnScope = only_stems;
                }

            } else if (isLong(arg2)) {
                returnByType = true;
                Long arg2Long = (Long) arg2;
                returnType = arg2Long.intValue();
            } else {
                throw new IllegalArgumentException(LIST_KEYS + " second argument must be a boolean or integer if present.");
            }
        }
        long size = 0;
        if (!isStem(arg)) {
            polyad.setResult(new StemVariable()); // just an empty stem
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable stemVariable = (StemVariable) arg;
        StemVariable out = new StemVariable();
        if (returnByType) {
            long i = 0L;
            for (String key : stemVariable.keySet()) {
                if (returnType == Constant.getType(stemVariable.get(key))) {
                    if (out.isLongIndex(key)) {
                        out.put(i++, Long.parseLong(key));
                    } else {
                        out.put(i++, key);
                    }
                }
            }
        } else {
            long i = 0L;

            for (String key : stemVariable.keySet()) {
                switch (returnScope) {
                    case all_keys:
                        if (out.isLongIndex(key)) {
                            out.put(i++, Long.parseLong(key));
                        } else {
                            out.put(i++, key);
                        }
                        break;
                    case only_scalars:
                        if (Constant.getType(stemVariable.get(key)) != Constant.STEM_TYPE) {
                            if (out.isLongIndex(key)) {
                                out.put(i++, Long.parseLong(key));
                            } else {
                                out.put(i++, key);
                            }
                        }
                        break;
                    case only_stems:
                        if (Constant.getType(stemVariable.get(key)) == Constant.STEM_TYPE) {
                            if (out.isLongIndex(key)) {
                                out.put(i++, Long.parseLong(key));
                            } else {
                                out.put(i++, key);
                            }
                        }
                        break;
                }
            }

        }
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);

    }

    /**
     * Return a stem of nothing key the keys, possibly filtering, so the final stem is of the form
     * <pre>
     *     {key0=key0,key1=key1,...}
     * </pre>
     * This is useful in conjunction with the rename keys call, so you can get the keys and do some
     * operations on them then rename the keys in the original stem.
     *
     * @param polyad
     * @param state
     */
    protected void doKeys(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + KEYS + " function requires at least 1 argument");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (!isStem(arg)) {
            polyad.setResult(new StemVariable()); // just an empty stem
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        int returnScope = all_keys;
        int returnType = Constant.UNKNOWN_TYPE;
        boolean returnByType = false;
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (isBoolean(arg2)) {
                returnByType = false;
                if ((Boolean) arg2) {
                    returnScope = only_scalars;
                } else {
                    returnScope = only_stems;
                }

            } else if (isLong(arg2)) {
                returnByType = true;
                Long arg2Long = (Long) arg2;
                returnType = arg2Long.intValue();
            } else {
                throw new IllegalArgumentException(LIST_KEYS + " second argument must be a boolean or integer if present.");
            }
        }
        StemVariable stemVariable = (StemVariable) arg;

        StemVariable out = new StemVariable();

        if (returnByType) {
            int i = 0;
            for (String key : stemVariable.keySet()) {
                if (returnType == Constant.getType(stemVariable.get(key))) {
                    putLongOrStringKey(out, key);
                }
            }
        } else {
            for (String key : stemVariable.keySet()) {
                switch (returnScope) {
                    case all_keys:
                        putLongOrStringKey(out, key);
                        break;
                    case only_scalars:
                        if (Constant.getType(stemVariable.get(key)) != Constant.STEM_TYPE) {
                            putLongOrStringKey(out, key);
                        }
                        break;
                    case only_stems:
                        if (Constant.getType(stemVariable.get(key)) == Constant.STEM_TYPE) {
                            putLongOrStringKey(out, key);
                        }
                        break;
                }
            }
        }
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);

    }

    /**
     * Needed by list_keys and keys so that returned indices that are longs are indeed longs.
     * Otherwise everything returned would be a string (e.g., '0' not 0) which makes subsequent
     * algebraic operations  on them fail.
     *
     * @param out
     * @param key
     */
    protected void putLongOrStringKey(StemVariable out, String key) {
        Long k = null;
        if (out.isLongIndex(key)) {
            k = Long.parseLong(key);
            out.put(k, k);
        } else {
            out.put(key, key);
        }

    }

    /**
     * has_keys(stem. var | keysList.) returns a var or  boolean stem if the key in the list is a key in the stem
     *
     * @param polyad
     * @param state
     */
    protected void doHasKeys(Polyad polyad, State state) {
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + HAS_KEYS + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + HAS_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        Object arg2 = polyad.getArguments().get(1).getResult();

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
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("the " + MAKE_INDICES + " function requires 1 argument");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
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
     * (Their keys are not added because you could have just added to the existing stem variable).
     *
     * @param polyad
     */
    protected void doToList(Polyad polyad, State state) {
        StemVariable out = new StemVariable();
        StemList<StemEntry> stemList = new StemList<>();

        Long index = 0L;

        for (StatementWithResultInterface arg : polyad.getArguments()) {
            Object r = arg.evaluate(state);
/*            if(r instanceof StemVariable){
                out.put((index++) + ".", r);
            }else{
                stemList.add(new StemEntry(index++, r));
            }*/
            stemList.add(new StemEntry(index++, r));

        }
        out.setStemList(stemList);
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    protected void doListCopyOrInsert(Polyad polyad, State state, boolean doInsert) {
        if (5 != polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + LIST_COPY + " function requires 5 arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException(LIST_COPY + " requires a stem as its first argument");
        }
        StemVariable souorceStem = (StemVariable) arg1;

        Object arg2 = polyad.evalArg(1, state);
        if (!isLong(arg2)) {
            throw new IllegalArgumentException(LIST_COPY + " requires an integer as its second argument");
        }
        Long startIndex = (Long) arg2;

        Object arg3 = polyad.evalArg(2, state);
        if (!isLong(arg3)) {
            throw new IllegalArgumentException(LIST_COPY + " requires an integer as its third argument");
        }
        Long length = (Long) arg3;


        StemVariable targetStem = getOrCreateStem(polyad.getArguments().get(3),
                state, LIST_COPY + " requires a stem as its fourth argument"
        );

        Object arg5 = polyad.evalArg(4, state);
        if (!isLong(arg5)) {
            throw new IllegalArgumentException(LIST_COPY + " requires an integer as its fifth argument");
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
        if (polyad.getArgCount() < 2 || 3 < polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + LIST_SUBSET + " function requires  two or three arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException(LIST_SUBSET + " requires stem as its first argument");
        }
        StemVariable stem = (StemVariable) arg1;
        Object arg2 = polyad.evalArg(1, state);
        if (!isLong(arg2)) {
            throw new IllegalArgumentException(LIST_SUBSET + " requires an integer as its second argument");
        }
        Long startIndex = (Long) arg2;
        Long endIndex = (long) stem.getStemList().size();
        if (polyad.getArgCount() == 3) {
            Object arg3 = polyad.evalArg(2, state);
            if (!isLong(arg3)) {
                throw new IllegalArgumentException(LIST_SUBSET + " requires an integer as its third argument");
            }
            endIndex = (Long) arg3;
        }

        StemVariable outStem = stem.listSubset(startIndex, endIndex);
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    protected void doIsList(Polyad polyad, State state) {
        if (1 != polyad.getArgCount()) {
            throw new IllegalArgumentException(IS_LIST + " requires 1 argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException(IS_LIST + " requires stem as its first argument");
        }
        StemVariable stemVariable = (StemVariable) arg1;
        Boolean isList = stemVariable.isList();
        polyad.setResult(isList);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }


    protected void doListAppend(Polyad polyad, State state) {
        if (2 != polyad.getArgCount()) {
            throw new IllegalArgumentException(LIST_APPEND + " requires 2 arguments");
        }
        // surgery. If the first argument is null and a stem, turn it in to one.
        StemVariable stem1 = getOrCreateStem(
                polyad.getArguments().get(0),
                state,
                LIST_APPEND + " requires stem as its first argument");
        Object arg2 = polyad.evalArg(1, state);
        StemVariable outStem = new StemVariable();
        outStem = (StemVariable) stem1.clone();
        //outStem.(stem1);
        if (arg2 instanceof StemVariable) {
            outStem.listAppend((StemVariable) arg2);
        } else {
            outStem.listAppend(arg2);
        }
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * Remove the entire variable from the symbol table.
     *
     * @param polyad
     * @param state
     */
    protected void doRemove(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException(REMOVE + " requires 1 argument");
        }
        polyad.evalArg(0, state);
        String var = null;
        if (polyad.getArguments().get(0) instanceof VariableNode) {
            VariableNode variableNode = (VariableNode) polyad.getArguments().get(0);
            // Don't evaluate this because it might not exist (that's what we are testing for). Just check
            // if the name is defined.
            var = variableNode.getVariableReference();
        }
        if (polyad.getArguments().get(0) instanceof ConstantNode) {
            ConstantNode variableNode = (ConstantNode) polyad.getArguments().get(0);
            Object x = variableNode.getResult();
            if (x != null) {
                var = x.toString();
            }
        }
        if (var == null) {
            polyad.setResult(Boolean.FALSE);
        } else {
            state.remove(var);
            polyad.setResult(Boolean.TRUE);
        }
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

    protected void isDefined(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("the " + IS_DEFINED + " function requires 1 argument");
        }
        boolean isDef = false;
        polyad.evalArg(0, state);
        if (polyad.getArguments().get(0) instanceof VariableNode) {
            VariableNode variableNode = (VariableNode) polyad.getArguments().get(0);
            // Don't evaluate this because it might not exist (that's what we are testing for). Just check
            // if the name is defined.
            isDef = state.isDefined(variableNode.getVariableReference());
        }
        if (polyad.getArguments().get(0) instanceof ConstantNode) {
            ConstantNode variableNode = (ConstantNode) polyad.getArguments().get(0);
            Object x = variableNode.getResult();
            if (x == null) {
                isDef = false;
            } else {
                isDef = state.isDefined(x.toString());
            }
        }
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
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + INCLUDE_KEYS + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + INCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        Object arg2 = polyad.getArguments().get(1).getResult();

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
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + EXCLUDE_KEYS + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + EXCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        Object arg2 = polyad.getArguments().get(1).getResult();

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
        oldRenameKeys(polyad, state);
    }

    /**
     * Permute the elements in a stem. The right argument must contain every key in the left argument or
     * an exception is raised. This is really just using cycle notation from abstract algebra...
     * E.g.,
     * <pre>
     * 10+3*indices(5)
     * [10,13,16,19,22]
     * rename_keys(10+3*indices(5), [4,2,3,1,0])
     * [22,19,13,16,10]
     * </pre>
     * read that in [4,2,3,1,0]: old --> new, so 0 --> 4, 1 --> 2, 2-->3, 3-->1, 4-->0
     */
    /*
    Test commands
    a.p:='foo';a.q:='bar';a.r:='baz';a.0:=10;a.1:=15;
    b.q :='r';b.0:='q';b.1:=0;b.p:=1;b.r:='p';
     shuffle(a., b.);
     */
    protected void shuffleKeys(Polyad polyad, State state) {
        if (0 == polyad.getArgCount() || 2 < polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + SHUFFLE + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (isLong(arg)) {
            Long argL = (Long) arg;
            int argInt = argL.intValue();
            if (argL < 0L) {
                throw new IllegalArgumentException("the argument to" + SHUFFLE + " must be > 0");
            }
            Long[] array = new Long[argInt];
            long j = 0L;
            for (int i = 0; i < argInt; i++) {
                array[i] = j++; // fill it with longs
            }
            List<Long> longList = Arrays.asList(array);
            Collections.shuffle(longList);
            StemVariable stem = new StemVariable();
            stem.addList(longList);
            polyad.setResult(stem);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }

        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + SHUFFLE + " command requires a stem as its first argument.");
        }

        polyad.evalArg(1, state);

        Object arg2 = polyad.getArguments().get(1).getResult();
        polyad.evalArg(1, state);
        if (!isStem(arg2)) {
            throw new IllegalArgumentException("The " + SHUFFLE + " command requires a stem as its second argument.");
        }
        StemVariable target = (StemVariable) arg;
        StemVariable newKeyStem = (StemVariable) arg2;
        Set<String> newKeys = newKeyStem.keySet();
        Set<String> usedKeys = target.keySet();

        StemVariable output = new StemVariable();

        Set<String> keys = target.keySet();
        // easy check is to count. If this fails, then we throw and exception.
        if (keys.size() != newKeys.size()) {
            throw new IllegalArgumentException("Error: the supplied set of keys must match every key in the source stem.");
        }

        for (String key : keys) {
            if (newKeys.contains(key)) {
                String kk = newKeyStem.getString(key);
                usedKeys.remove(kk);
                Object vv = target.get(kk);
                output.put(key, vv);
            } else {
                throw new IllegalArgumentException("Error: \"" + key + "\" is not a key in the second argument.");
            }
        }
        if (!usedKeys.isEmpty()) {
            throw new IllegalArgumentException("Error: each key in the left argument must be used as a value in the second argument. This assures that all elements are shuffled.");
        }

        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /*
    This only renames keys in situ, i.e. it changes the stem given.
     */
    protected void oldRenameKeys(Polyad polyad, State state) {
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + RENAME_KEYS + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + RENAME_KEYS + " command requires a stem as its first argument.");
        }
        polyad.evalArg(1, state);

        Object arg2 = polyad.getArguments().get(1).getResult();
        polyad.evalArg(1, state);
        if (!isStem(arg2)) {
            throw new IllegalArgumentException("The " + RENAME_KEYS + " command requires a stem as its second argument.");
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
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + COMMON_KEYS + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArguments().get(0).getResult();
        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + COMMON_KEYS + " command requires a stem as its first argument.");
        }
        polyad.evalArg(1, state);

        Object arg2 = polyad.getArguments().get(1).getResult();
        polyad.evalArg(1, state);
        if (!isStem(arg2)) {
            throw new IllegalArgumentException("The " + COMMON_KEYS + " command requires a stem as its second argument.");
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
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + SET_DEFAULT + " function requires 2 arguments");
        }
        StemVariable stemVariable = getOrCreateStem(polyad.getArguments().get(0),
                state,
                "the " + SET_DEFAULT + " command accepts   only a stem variable as its first argument.");

        polyad.evalArg(1, state);

        Object defaultValue = polyad.getArguments().get(1).getResult();
        if (isStem(defaultValue)) {
            throw new IllegalArgumentException("the " + SET_DEFAULT + " command accepts only a scalar as its second argument.");
        }
        stemVariable.setDefaultValue(defaultValue);
        polyad.setResult(defaultValue);
        polyad.setResultType(polyad.getArguments().get(1).getResultType());
        polyad.setEvaluated(true);
    }

    protected void doMask(Polyad polyad, State state) {
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + MASK + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        polyad.evalArg(1, state);
        Object obj1 = polyad.getArguments().get(0).getResult();
        Object obj2 = polyad.getArguments().get(1).getResult();
        if (!areAllStems(obj1, obj2)) {
            throw new IllegalArgumentException("the " + MASK + " requires both arguments be stem variables");
        }
        StemVariable stem1 = (StemVariable) obj1;
        StemVariable stem2 = (StemVariable) obj2;
        StemVariable result = stem1.mask(stem2);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setResult(result);
        polyad.setEvaluated(true);

    }

    /*
    Long block of QDL here to show how the Java should work. It is easy to do cases of this in QDL, but this
    ought to be a built in function.
    q. has different length of things
    q. := [[n(4), 4+n(4)],[8+n(4),12+n(4)], [16+n(5),21+n(5)]]
    w. := 100 + q.
     join(q., w., 1)
     join(q., w., 2)
     join(q., w., 3)
    q.
[
 [[0,1,2,3],[4,5,6,7]],
 [[8,9,10,11],[12,13,14,15]],
 [[16,17,18,19,20],[21,22,23,24,25]]
]
    w.
[
 [[100,101,102,103],[104,105,106,107]],
 [[108,109,110,111],[112,113,114,115]],
 [[116,117,118,119,120],[121,122,123,124,125]]
]
  // QDL to do the first few cases of this directly
  join0(x., y.)->[z.:=null;z.:=x.~y.;return(z.);]
  join1(x., y.)->[z.:=null;while[for_keys(i0,x.)][z.i0. := x.i0~y.i0;];return(z.);]
  join2(x., y.)->[z.:=null;while[for_keys(i0,x.)][while[for_keys(i1, x.i0)][z.i0.i1.:=x.i0.i1~y.i0.i1;];];return(z.);]
  join3(x., y.)->[z.:=null;while[for_keys(i0,x.)][while[for_keys(i1, x.i0)][while[for_keys(i2, x.i0.i1)][z.i0.i1.i2.:=x.i0.i1.i2~y.i0.i1.i2;];];];return(z.);]
  join4(x., y.)->[z.:=null;while[for_keys(i0,x.)][while[for_keys(i1, x.i0)][while[for_keys(i2, x.i0.i1)][while[for_keys(i3, x.i0.i1)][z.i0.i1.i2.i3.:=x.i0.i1.i2.i3~y.i0.i1.i2.i3;];];];];return(z.);]

  // also q.~w.
  z. := join0(q.,w.)
  //  *   <--- You are here
  //  z.i.j.k
          join0(q.,w.)
[
 [[0,1,2,3],[4,5,6,7]],
 [[8,9,10,11],[12,13,14,15]],
 [[16,17,18,19,20],[21,22,23,24,25]],
 [[100,101,102,103],[104,105,106,107]],
 [[108,109,110,111],[112,113,114,115]],
 [[116,117,118,119,120],[121,122,123,124,125]]
]
// result is list of combined lengths size(z.) == size(q.) + size(w.)

 z. :=  join1(q., w.)
  //   *  <--- You are here
  // z.i.j.k
[
 [[0,1,2,3],[4,5,6,7],[100,101,102,103],[104,105,106,107]],
 [[8,9,10,11],[12,13,14,15],[108,109,110,111],[112,113,114,115]],
 [[16,17,18,19,20],[21,22,23,24,25],[116,117,118,119,120],[121,122,123,124,125]]
]
// z. has same shape, but z.k == q.k ~ w.k


       z. := join2(q.,w.)
  //     *  <--- You are here
  // z.i.j.k
[
 [[0,1,2,3,100,101,102,103],[4,5,6,7,104,105,106,107]],
 [[8,9,10,11,108,109,110,111],[12,13,14,15,112,113,114,115]],
 [[16,17,18,19,20,116,117,118,119,120],[21,22,23,24,25,121,122,123,124,125]]
]
// z. now has size(z.k) == size(q.k) + size(w.k)

z. :=  join3(q.,w.)
  //       * <--- You are here
  // z.i.j.k
[
 [[[0,100],[1,101],[2,102],[3,103]],[[4,104],[5,105],[6,106],[7,107]]],
 [[[8,108],[9,109],[10,110],[11,111]],[[12,112],[13,113],[14,114],[15,115]]],
 [[[16,116],[17,117],[18,118],[19,119],[20,120]],[[21,121],[22,122],[23,123],[24,124],[25,125]]]
]

  // Since this is the last index this joins each element into new elements, increasing the
  // rank of the stem by 1
     */
    public static final Long JOIN_LAST_ARGUMENT_VALUE = new Long(-0xcafed00d);

    protected void doJoin(Polyad polyad, State state) {
        Object[] args = new Object[polyad.getArgCount()];
        int argCount = polyad.getArgCount();
        for (int i = 0; i < argCount; i++) {
            args[i] = polyad.evalArg(i, state);
        }
        int depth = ((Long) args[2]).intValue();
        StemVariable leftStem;
        if (isStem(args[0])) {
            leftStem = (StemVariable) args[0];
        } else {
            leftStem = new StemVariable();
            leftStem.put(0L, args[0]);
        }
        StemVariable rightStem;
        if (isStem(args[1])) {
            rightStem = (StemVariable) args[1];
        } else {
            rightStem = new StemVariable();
            rightStem.put(0L, args[1]);
        }
        boolean doJoinOnLastAxis = false;
        if (depth == JOIN_LAST_ARGUMENT_VALUE) {
            doJoinOnLastAxis = true;
        }
        if (depth == 0) {
            StemVariable outStem = leftStem.union(rightStem);
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setResult(outStem);
            return;
        }
        StemVariable outStem = new StemVariable();

        if (leftStem.isEmpty() || rightStem.isEmpty()) {
            // edge case -- they sent an empty argument, so don't blow up, just return nothing
            polyad.setResult(outStem);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemUtility.AxisAction  joinAction = new StemUtility.AxisAction() {
            @Override
            public void action(StemVariable out, String key, StemVariable leftStem, StemVariable rightStem) {
                out.put(key, leftStem.union(rightStem));

            }
        };
        if (doJoinOnLastAxis) {
            StemUtility.axisRecursion(outStem, leftStem, rightStem, 1000000, doJoinOnLastAxis, joinAction);
        } else {
            StemUtility.axisRecursion(outStem, leftStem, rightStem, depth - 1, doJoinOnLastAxis, joinAction);
        }
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    public interface AxisAction {
        void action(StemVariable out, String key, StemVariable leftStem, StemVariable rightStem);
    }

    protected void axisRecursion(StemVariable out0,
                                 StemVariable left0,
                                 StemVariable right0,
                                 int depth,
                                 boolean maxDepth,
                                 AxisAction axisAction) {
        StemVariable commonKeys = left0.commonKeys(right0);
        for (String key0 : commonKeys.keySet()) {
            Object leftObj = left0.get(key0);
            Object rightObj = right0.get(key0);

            StemVariable left1 = null;
            if (isStem(leftObj)) {
                left1 = (StemVariable) leftObj;
            } else {
                if (rightObj == null) {
                    throw new RankException("There are no more elements in the left argument.");
                }

                left1 = new StemVariable();
                left1.put(0L, leftObj);
            }
            StemVariable right1 = null;
            if (isStem(rightObj)) {
                right1 = (StemVariable) right0.get(key0);
            } else {
                if (rightObj == null) {
                    throw new RankException("There are no more elements in the right argument.");
                }
                right1 = new StemVariable();
                right1.put(0L, rightObj);
            }
            boolean bottomedOut = areNoneStems(leftObj, rightObj) && maxDepth && 0 < depth;
            if (bottomedOut) {
                axisAction.action(out0, key0, left1, right1);
            } else {
                if (0 < depth) {
                    if (areNoneStems(leftObj, rightObj)) {
                        throw new RankException("rank error");
                    }
                    StemVariable out1 = new StemVariable();
                    out0.put(key0, out1);
                    axisRecursion(out1, left1, right1, depth - 1, maxDepth, axisAction);
                } else {
                    out0.put(key0, left1.union(right1));
                }
            }
        }
    }


}
