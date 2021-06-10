package edu.uiuc.ncsa.qdl.evaluate;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.exceptions.RankException;
import edu.uiuc.ncsa.qdl.exceptions.UnknownSymbolException;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.QDLConstants;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.*;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.qdl.state.SymbolTable.var_regex;
import static edu.uiuc.ncsa.qdl.variables.StemUtility.LAST_AXIS_ARGUMENT_VALUE;
import static edu.uiuc.ncsa.qdl.variables.StemUtility.axisWalker;
import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:19 AM
 */
public class StemEvaluator extends AbstractFunctionEvaluator {
    public static final String STEM_NAMESPACE = "stem";
    public static final String LIST_NAMESPACE = "list";
    public static final String STEM_FQ = STEM_NAMESPACE + ImportManager.NS_DELIMITER;
    public static final String LIST_FQ = LIST_NAMESPACE + ImportManager.NS_DELIMITER;
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

    public static final String DIMENSION = "dim";
    public static final String FQ_DIMENSION = STEM_FQ + DIMENSION;
    public static final int DIMENSION_TYPE = 14 + STEM_FUNCTION_BASE_VALUE;

    public static final String RANK = "rank";
    public static final String FQ_RANK = STEM_FQ + RANK;
    public static final int RANK_TYPE = 15 + STEM_FUNCTION_BASE_VALUE;

    public static final String FOR_EACH = "for_each";
    public static final String FQ_FOR_EACH = STEM_FQ + FOR_EACH;
    public static final int FOR_EACH_TYPE = 16 + STEM_FUNCTION_BASE_VALUE;

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
    // older ones are prepended wit a list_. These still work but won't show up
    // in lists of functions.

    public static final String LIST_APPEND = "append";
    public static final String LIST_APPEND2 = "list_append";
    public static final String FQ_LIST_APPEND = LIST_FQ + LIST_APPEND;
    public static final int LIST_APPEND_TYPE = 200 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_INSERT_AT = "insert_at";
    public static final String LIST_INSERT_AT2 = "list_insert_at";
    public static final String FQ_LIST_INSERT_AT = LIST_FQ + LIST_INSERT_AT;
    public static final int LIST_INSERT_AT_TYPE = 201 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_SUBSET = "subset";
    public static final String LIST_SUBSET2 = "list_subset";
    public static final String FQ_LIST_SUBSET = LIST_FQ + LIST_SUBSET;
    public static final int LIST_SUBSET_TYPE = 202 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_COPY = "list_copy";
    public static final String LIST_COPY2 = "copy";
    public static final String FQ_LIST_COPY = LIST_FQ + LIST_COPY;
    public static final int LIST_COPY_TYPE = 203 + STEM_FUNCTION_BASE_VALUE;

    public static final String IS_LIST = "is_list";
    public static final String FQ_IS_LIST = STEM_FQ + IS_LIST;
    public static final int IS_LIST_TYPE = 204 + STEM_FUNCTION_BASE_VALUE;

    public static final String TO_LIST = "to_list";
    public static final String FQ_TO_LIST = STEM_FQ + TO_LIST;
    public static final int TO_LIST_TYPE = 205 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_STARTS_WITH = "starts_with";
    public static final String LIST_STARTS_WITH2 = "list_starts_with";
    public static final String FQ_LIST_STARTS_WITH = LIST_FQ + LIST_STARTS_WITH;
    public static final int LIST_STARTS_WITH_TYPE = 206 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_REVERSE = "reverse";
    public static final String LIST_REVERSE2 = "list_reverse";
    public static final String FQ_LIST_REVERSE = LIST_FQ + LIST_REVERSE;
    public static final int LIST_REVERSE_TYPE = 207 + STEM_FUNCTION_BASE_VALUE;

    // Conversions to/from JSON.
    public static final String TO_JSON = "to_json";
    public static final String FQ_TO_JSON = STEM_FQ + TO_JSON;
    public static final int TO_JSON_TYPE = 300 + STEM_FUNCTION_BASE_VALUE;

    public static final String FROM_JSON = "from_json";
    public static final String FQ_FROM_JSON = STEM_FQ + FROM_JSON;
    public static final int FROM_JSON_TYPE = 301 + STEM_FUNCTION_BASE_VALUE;

    public static final String JSON_PATH_QUERY = "query";
    public static final String FQ_JSON_PATH_QUERY = STEM_FQ + JSON_PATH_QUERY;
    public static final int JSON_PATH_QUERY_TYPE = 302 + STEM_FUNCTION_BASE_VALUE;

    /**
     * A list of the names that this Evaluator knows about. NOTE that this must be kept in sync
     * by the developer since it is used to determine if a function is built in or a user-defined function.
     */
    public static String FUNC_NAMES[] = new String[]{
            DIMENSION, RANK,
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
            FOR_EACH,
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
            FROM_JSON, JSON_PATH_QUERY};
    public static String FQ_FUNC_NAMES[] = new String[]{
            FQ_DIMENSION, FQ_RANK,
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
            FQ_FOR_EACH,
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
            FQ_FROM_JSON, FQ_JSON_PATH_QUERY};


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
            case DIMENSION:
            case FQ_DIMENSION:
                return DIMENSION_TYPE;
            case RANK:
            case FQ_RANK:
                return RANK_TYPE;
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
            case LIST_APPEND2:
            case FQ_LIST_APPEND:
                return LIST_APPEND_TYPE;
            case LIST_COPY:
            case LIST_COPY2:
            case FQ_LIST_COPY:
                return LIST_COPY_TYPE;
            case LIST_REVERSE:
            case LIST_REVERSE2:
            case FQ_LIST_REVERSE:
                return LIST_REVERSE_TYPE;
            case LIST_STARTS_WITH:
            case LIST_STARTS_WITH2:
            case FQ_LIST_STARTS_WITH:
                return LIST_STARTS_WITH_TYPE;
            case LIST_INSERT_AT:
            case LIST_INSERT_AT2:
            case FQ_LIST_INSERT_AT:
                return LIST_INSERT_AT_TYPE;
            case LIST_SUBSET:
            case LIST_SUBSET2:
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
            case JSON_PATH_QUERY:
            case FQ_JSON_PATH_QUERY:
                return JSON_PATH_QUERY_TYPE;
            case FOR_EACH:
            case FQ_FOR_EACH:
                return FOR_EACH_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case DIMENSION:
            case FQ_DIMENSION:
                doDimension(polyad, state);
                return true;
            case RANK:
            case FQ_RANK:
                doRank(polyad, state);
                return true;
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
            case LIST_APPEND2:
            case FQ_LIST_APPEND:
                doListAppend(polyad, state);
                return true;
            case LIST_COPY:
            case LIST_COPY2:
            case FQ_LIST_COPY:
                doListCopyOrInsert(polyad, state, false);
                return true;
            case LIST_INSERT_AT:
            case LIST_INSERT_AT2:
            case FQ_LIST_INSERT_AT:
                doListCopyOrInsert(polyad, state, true);
                return true;
            case LIST_SUBSET:
            case LIST_SUBSET2:
            case FQ_LIST_SUBSET:
                doListSubset(polyad, state);
                return true;
            case LIST_REVERSE:
            case LIST_REVERSE2:
            case FQ_LIST_REVERSE:
                doListReverse(polyad, state);
                return true;
            case LIST_STARTS_WITH:
            case LIST_STARTS_WITH2:
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
            case FOR_EACH:
            case FQ_FOR_EACH:
                doForEach(polyad, state);
                return true;
            case FROM_JSON:
            case FQ_FROM_JSON:
                doFromJSON(polyad, state);
                return true;
            case JSON_PATH_QUERY:
            case FQ_JSON_PATH_QUERY:
                doJPathQuery(polyad, state);
                return true;
        }
        return false;
    }

    /**
     * Apply n-ary function to outer product of stems. There n stems passed in.
     *
     * @param polyad
     * @param state
     */
    protected void doForEach(Polyad polyad, State state) {
        FunctionReferenceNode frn = getFunctionReferenceNode(state, polyad.getArguments().get(0), true);

        StemVariable[] stems = new StemVariable[polyad.getArgCount() - 1];
        for (int i = 1; i < polyad.getArgCount(); i++) {

            Object arg = polyad.evalArg(i, state);
            if (!isStem(arg)) {
                throw new IllegalArgumentException("All arguments to except the first must be stem. Argument " + i + " is not a stem.");
            }
            stems[i - 1] = (StemVariable) arg;
        }
        ExpressionImpl f = getOperator(state, frn, stems.length);

        StemVariable output = new StemVariable();
        // special case single args. Otherwise have to special case a bunch of stuff in forEachRecursion

        if (stems.length == 1) {
            for (String key0 : stems[0].keySet()) {
                Object obj = stems[0].get(key0);
                ArrayList<Object> rawArgs = new ArrayList<>();
                rawArgs.add(obj);
                f.setArguments(toConstants(rawArgs));
                f.evaluate(state);
                output.put(key0, f.getResult());
            }
        } else {
            forEachRecursion(output, f, state, stems);
        }
        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);

    }

    protected void forEachRecursion(StemVariable output, ExpressionImpl f, State state, StemVariable[] stems) {
        int argCount = stems.length - 1;
        for (String key : stems[0].keySet()) {
            ArrayList<Object> rawArgs = new ArrayList<>();
            rawArgs.add(stems[0].get(key));
            StemVariable output1 = new StemVariable();
                forEachRecursion(output1, f, state, stems, rawArgs, argCount - 1);
            output.put(key, output1);
        }
    }

    protected void forEachRecursion(StemVariable output, ExpressionImpl f, State state, StemVariable[] stems, ArrayList<Object> rawArgs, int depth) {
        ArrayList<StatementWithResultInterface> args = null;
        int currentIndex = stems.length - depth - 1;
        if (depth == 0) {
            args = toConstants(rawArgs); // convert 0,...n-1 args
            args.add(null);
        } else {
            rawArgs.add(null);
        }
        for (String key : stems[currentIndex].keySet()) {
            if (depth == 0) {
                Object o = stems[currentIndex].get(key);
                args.set(currentIndex, new ConstantNode(o, Constant.getType(o)));
                f.setArguments(args);
                f.evaluate(state);
                output.put(key, f.getResult());
            } else {
                rawArgs.set(currentIndex, stems[currentIndex].get(key));
                StemVariable output1 = new StemVariable();
                forEachRecursion(output1, f, state, stems, rawArgs, depth - 1);
                output.put(key, output1);
            }
        }
    }

    /*
     f(x)->x+2
    for_each(@f, n(5))
[2,3,4,5,6]
  for_each(@size, n(4,5))

  z(x,y)->x^2+y^2
  for_each(@z, n(3),n(5))
[
   [0,1,4,9,16],
   [1,2,5,10,17],
   [4,5,8,13,20]
]

      w(x,y,z)->x^2+y^2+z^2
 for_each(@w, n(4), n(3), n(5))
 // yields a 4 x 3 x 5 array.
[
 [
   [0,1,4,9,16],
   [1,2,5,10,17],
   [4,5,8,13,20]
 ],
 [
   [1,2,5,10,17],
   [2,3,6,11,18],
   [5,6,9,14,21]
 ],
 [
    [4,5,8,13,20],
    [5,6,9,14,21],
    [8,9,12,17,24]
 ],
 [
    [9,10,13,18,25],
    [10,11,14,19,26],
    [13,14,17,22,29]
 ]
]
     */
    protected ArrayList<StatementWithResultInterface> toConstants(ArrayList<Object> objects) {
        ArrayList<StatementWithResultInterface> args = new ArrayList<>();
        for (Object obj : objects) {
            int type = Constant.getType(obj);
            if (type == Constant.UNKNOWN_TYPE) {
                // Future proofing in case something changes in the future internally
                throw new IllegalArgumentException("error: unknown object type");
            }
            args.add(new ConstantNode(obj, type));
        }
        return args;
    }

    protected void doJPathQuery(Polyad polyad, State state) {
        Object arg0 = polyad.evalArg(0, state);
        if (!isStem(arg0)) {
            throw new IllegalArgumentException(JSON_PATH_QUERY + " requires a stem as its first argument");
        }
        StemVariable stemVariable = (StemVariable) arg0;
        Object arg1 = polyad.evalArg(1, state);
        if (!isString(arg1)) {
            throw new IllegalArgumentException(JSON_PATH_QUERY + " requires a string as its second argument");
        }

        String query = (String) arg1;
        Configuration conf = null;
        boolean returnAsPaths = false;
        if (polyad.getArgCount() == 3) {
            Object arg2 = polyad.evalArg(2, state);
            if (!isBoolean(arg2)) {
                throw new IllegalArgumentException(JSON_PATH_QUERY + " requires a boolean as its third argument");
            }
            returnAsPaths = (Boolean) arg2;
            conf = Configuration.builder()
                    .options(Option.AS_PATH_LIST).build();
        }
        String output;
        if (returnAsPaths) {
            try {
                output = JsonPath.using(conf).parse(stemVariable.toJSON().toString()).read(query).toString();
            } catch (JsonPathException jpe) {
                throw new IllegalArgumentException("error processing query:" + jpe.getMessage());
            }

            output = crappyConverter(output);
        } else {
            try {
                conf = Configuration.builder()
                        .options(Option.ALWAYS_RETURN_LIST).build();

                //  output = JsonPath.read(stemVariable.toJSON().toString(), query).toString();
                output = JsonPath.using(conf).parse(stemVariable.toJSON().toString()).read(query).toString();

            } catch (JsonPathException jpe) {
                throw new IllegalArgumentException("error processing query:" + jpe.getMessage());
            }
        }
        StemVariable outStem = new StemVariable();
        try {
            JSONArray array = JSONArray.fromObject(output);
            outStem.fromJSON(array);
        } catch (JSONException x) {
            JSONObject jo = JSONObject.fromObject(output);
            outStem.fromJSON(jo);
        }
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /**
     * This converts a list of JSON Path indices to stem indices. It is very simple
     * minded.
     *
     * @param indexList
     * @return
     */
    protected String crappyConverter(String indexList) {
        return crappyConverterNew(indexList);
    }

    /*
      x. := {'sub':'http://cilogon.org/serverT/users/17048', 'idp_name':'Supercomputing at BSU', 'eppn':'rbriuis@bigstate.edu', 'cert_subject_dn':'/DC=org/DC=cilogon/C=US/O=Big State Supercomputing Center/CN=Robert Bruce T17099', 'eptid':'https://idp.bigstate.edu/idp/shibboleth!https://cilogon.org/shibboleth!65P3o9FNjrp4z6+WI7Dir/4I=', 'iss':'https://test.cilogon.org', 'given_name':'Robert', 'voPersonExternalID':'rbriuis@bigstate.edu', 'nonce':'R72KPZ4Pwo9nPd9z1qCA04hBALMC-yVGUOGyTn-miHo', 'aud':'myproxy:oa4mp,2012:/client_id/910d7984412870aa6e199f9afrab8', 'acr':'https://refeds.org/profile/mfa', 'uid':'rbriuis', 'idp':'https://idp.bigstate.edu/idp/shibboleth', 'affiliation':'staff@bigstate.edu;employee@bigstate.edu;member@bigstate.edu', 'uidNumber':'55939', 'auth_time':'1623103279', 'name':'Roibert a Briuis', 'isMemberOf':[{'name':'all_users', 'id':13002},{'name':'staff_reporting', 'id':16405},{'name':'list_allbsu', 'id':18942}], 'exp':1624053679, 'iat':1623103279, 'family_name':'Bruce', 'email':'bob@bigstate.edu'}
     ndx. :=  query(x., '$..name', true)
     x.ndx.1

     */
    protected String crappyConverterNew(String indexList) {
        QDLCodec codec = new QDLCodec();
        JSONArray arrayIn = JSONArray.fromObject(indexList);
        JSONArray arrayOut = new JSONArray();
        for (int i = 0; i < arrayIn.size(); i++) {
            String x = arrayIn.getString(i);
            x = x.substring(2); // All JSON paths start with a $.
            StringTokenizer tokenizer = new StringTokenizer(x, "[");
            String r = "";
            while (tokenizer.hasMoreTokens()) {
                String nextOne = tokenizer.nextToken();
                if (nextOne.startsWith("'")) {
                    nextOne = nextOne.substring(1);
                }
                nextOne = nextOne.substring(0, nextOne.length() - 1);
                if (nextOne.endsWith("'")) {
                    nextOne = nextOne.substring(0, nextOne.length() - 1);
                }
                r = r + QDLConstants.STEM_PATH_MARKER2 + codec.encode(nextOne);
            }
            arrayOut.add(r);

        }
        return arrayOut.toString();
    }

    protected String crappyConverterStem(String indexList) {
        JSONArray arrayIn = JSONArray.fromObject(indexList);
        JSONArray arrayOut = new JSONArray();
        for (int i = 0; i < arrayIn.size(); i++) {
            String x = arrayIn.getString(i);
            x = x.substring(2); // All JSON paths start with a $.
            StringTokenizer tokenizer = new StringTokenizer(x, "[");
            boolean isFirst = true;
            String r = "";
            while (tokenizer.hasMoreTokens()) {
                String nextOne = tokenizer.nextToken();
                if (nextOne.startsWith("'")) {
                    nextOne = nextOne.substring(1);
                }
                nextOne = nextOne.substring(0, nextOne.length() - 1);
                if (nextOne.endsWith("'")) {
                    nextOne = nextOne.substring(0, nextOne.length() - 1);
                }
                if (isFirst) {
                    isFirst = false;
                    r = r + nextOne;
                } else {
                    r = r + StemVariable.STEM_INDEX_MARKER + nextOne;
                }
            }
            arrayOut.add(r);

        }
        return arrayOut.toString();
    }


    private void doRank(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0 || !isStem(polyad.evalArg(0, state))) {
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setResult(0L);
            return;
        }
        polyad.setEvaluated(true);
        StemVariable s = (StemVariable) polyad.getArguments().get(0).getResult();
        polyad.setResult(s.getRank());
        polyad.setResultType(Constant.LONG_TYPE);

    }

    private void doDimension(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0 || !isStem(polyad.evalArg(0, state))) {
            polyad.setEvaluated(true);
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setResult(0L);
            return;
        }
        // so its a stem
        polyad.setEvaluated(true);
        StemVariable s = (StemVariable) polyad.getArguments().get(0).getResult();
        polyad.setResult(s.dim());
        polyad.setResultType(Constant.STEM_TYPE);

    }

    protected void doListReverse(Polyad polyad, State state) {
        Object arg1 = polyad.evalArg(0, state);
        if (!isStem(arg1)) {
            throw new IllegalArgumentException("error:" + LIST_REVERSE + " requires a stem as its argument.");
        }
        int axis = 0;
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            if (!isLong(arg2)) {
                throw new IllegalArgumentException("error:" + LIST_REVERSE + " an integer as its axis.");
            }
            axis = ((Long) arg2).intValue();
        }

        StemVariable input = (StemVariable) arg1;

        DoReverse reverse = this.new DoReverse();

        Object result = axisWalker(input, axis, reverse);
        polyad.setResult(result);
        polyad.setResultType(Constant.getType(result));
        polyad.setEvaluated(true);


/*
        StemVariable output = new StemVariable();
        Iterator<StemEntry> iterator = input.getStemList().descendingIterator();
        while (iterator.hasNext()) {
            StemEntry s = iterator.next();
            output.listAppend(s.entry);
        }
        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
*/
    }

    protected class DoReverse implements StemUtility.StemAxisWalkerAction1 {
        @Override
        public Object action(StemVariable inStem) {
            StemVariable output = new StemVariable();
            Iterator<StemEntry> iterator = inStem.getStemList().descendingIterator();
            while (iterator.hasNext()) {
                StemEntry s = iterator.next();
                output.listAppend(s.entry);
            }
            return output;
        }
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
        QDLCodec codec = new QDLCodec();

        for (String key : stem.keySet()) {
            Object ob = stem.get(key);
            key = key + (isStem(ob) ? STEM_INDEX_MARKER : "");
            if (safeMode) {
                if (!pattern.matcher(key).matches()) {
                    key = codec.encode(key);
                }
                if (state.isDefined(key)) {
                    throw new IllegalArgumentException("Error: name clash in safe mode for \"" + key + "\"");
                }


            } else {
                if (!pattern.matcher(key).matches()) {
                    throw new IllegalArgumentException("Error: The variable name \"" + key + "\" is not a legal variable name.");
                }
            }
            keys.add(key);
            localState.setValue(key, ob);
        }
        // once all is said and done and none of this bombed copy it. That way we don't leave the actual state in disarray
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

    Pattern pattern = Pattern.compile(var_regex);

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
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + MAKE_INDICES + " function requires at least 1 argument");
        }
        polyad.evalArg(0, state);

        Object arg = polyad.getArguments().get(0).getResult();
        // First argument always has to be an integer.
        if (!(arg instanceof Long)) {
            throw new IndexError("error: only non-negative integer arguments are allowed in " + SHORT_MAKE_INDICES + " or " + MAKE_INDICES);
        }
        Object[] fill = null;
        CyclicArgList cyclicArgList = null;
        boolean hasFill = false;
        if (polyad.getArgCount() != 1) {
            Object lastArg = polyad.evalArg(polyad.getArgCount() - 1, state);
            if (!isStem(lastArg)) {
                // fine, no fill.
                hasFill = false;
            } else {
                StemVariable fillStem = (StemVariable) lastArg;
                if (!fillStem.isList()) {
                    throw new IllegalArgumentException("error: fill argument must be a list of scalars");
                }
                StemList stemList = fillStem.getStemList();
                fill = stemList.toArray(true, false);
                cyclicArgList = new CyclicArgList(fill);

                hasFill = true;
            }
        }
        // Special case is a simple list. n(3) should yield [0,1,2] rather than a 1x3
        // array (recursion automatically boxes it into at least a 2 rank array).
        if (polyad.getArgCount() == 1 || (polyad.getArgCount() == 2 && hasFill)) {
            long size = (Long) arg;
            if (size == 0) {
                polyad.setResult(new StemVariable());
                polyad.setResultType(Constant.STEM_TYPE);
                polyad.setEvaluated(true);
                return;
            }
            if (size < 0L) {
                throw new IndexError("error: negative index encountered");
            }
            StemList stemList;
            if (hasFill) {
                stemList = new StemList(size, cyclicArgList.next((int) size));
            } else {
                stemList = new StemList(size);
            }
            StemVariable out = new StemVariable();
            out.setStemList(stemList);


            polyad.setResult(out);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }

        int lastArgIndex = polyad.getArgCount() - 1;
        if (fill != null && fill.length != 0) {
            lastArgIndex--; // last arg is the fill pattern
        }
        int[] lengths = new int[lastArgIndex + 1];
        for (int i = 0; i < lastArgIndex + 1; i++) {
            Object obj = polyad.evalArg(i, state);
            if (!isLong(obj)) {
                throw new IndexError("error: argument " + i + " is not an integer. All dimensions must be positive integers.");
            }
            lengths[i] = ((Long) obj).intValue();
            // Any dimension of 0 returns an empty list
            if (lengths[i] == 0) {
                polyad.setResult(new StemVariable());
                polyad.setResultType(Constant.STEM_TYPE);
                polyad.setEvaluated(true);
                return;
            }
            if (lengths[i] < 0L) {
                throw new IndexError("error: argument " + i + " is negative. All dimensions must be positive integers.");
            }
        }
        StemVariable out = new StemVariable();
        indexRecursion(out, lengths, 0, cyclicArgList);
        polyad.setResult(out);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    /**
     * This makes an infinite arg list for creating new stems.
     * <pre>
     *       Object[] myArgs = new Object[]{"a","b",0L};
     *       CyclicArgClist cal = new CyclicArgList(myArgs);
     *       cal.next(17);
     * </pre>
     * would return the array
     * <pre>
     *     ["a","b",),"a","b",...]
     * </pre>
     * with 17 elements
     */
    public static class CyclicArgList {
        public CyclicArgList(Object[] args) {
            this.args = args;
        }

        Object[] args;

        /**
         * Cylically get the next n elements
         *
         * @param n
         * @return
         */
        public Object[] next(int n) {
            Object[] out = new Object[n];
            for (int i = 0; i < n; i++) {
                out[i] = args[((currentIndex++) % args.length)];
            }
            return out;
        }

        int currentIndex = 0;
    }

    /**
     * Fills in the elements for the n(x,y,z,...) function.adv74008
     *
     * @param out
     * @param lengths
     * @param index
     * @param cyclicArgList
     */
    protected void indexRecursion(StemVariable out, int[] lengths, int index, CyclicArgList cyclicArgList) {
        for (int i = 0; i < lengths[index]; i++) {
            if (lengths.length == index + 2) {
                // end of recursion
                StemVariable out1;
                if (cyclicArgList == null) {
                    out1 = new StemVariable((long) lengths[lengths.length - 1], null);
                } else {

                    out1 = new StemVariable((long) lengths[lengths.length - 1], cyclicArgList.next(lengths[lengths.length - 1]));
                }
                out.put((long) i, out1);

            } else {
                StemVariable out1 = new StemVariable();
                indexRecursion(out1, lengths, index + 1, cyclicArgList);
                out.put((long) i, out1);
            }
        }

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

        // need to handle case that the target does not exist.
        StemVariable targetStem;
        if (polyad.getArguments().get(3) instanceof VariableNode) {
            targetStem = getOrCreateStem(polyad.getArguments().get(3),
                    state, LIST_COPY + " requires a stem as its fourth argument"
            );
        } else {
            Object obj = polyad.evalArg(3, state);
            if (!isStem(obj)) {
                throw new IllegalArgumentException(LIST_COPY + " requires an integer as its fifth argument");

            }
            targetStem = (StemVariable) obj;
        }

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
        try {
            polyad.evalArg(0, state);
        } catch (IndexError | UnknownSymbolException exception) {
            polyad.setResult(isDef);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setEvaluated(true);
            return;
        }
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
     /*   StemUtility.StemAxisWalkerAction1 walker = new StemUtility.StemAxisWalkerAction1() {
            @Override
            public Object action(StemVariable inStem) {
                
            }
        }*/
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

        Object arg2 = polyad.evalArg(1, state);

        // polyad.getArguments().get(1).getResult();
        //  polyad.evalArg(1, state);
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

    protected void doJoin(Polyad polyad, State state) {
        Object[] args = new Object[polyad.getArgCount()];
        int argCount = polyad.getArgCount();
        for (int i = 0; i < argCount; i++) {
            args[i] = polyad.evalArg(i, state);
        }
        int axis = ((Long) args[2]).intValue();
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
        if (axis == LAST_AXIS_ARGUMENT_VALUE) {
            doJoinOnLastAxis = true;
        }
        if (axis == 0) {
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
        StemUtility.DyadAxisAction joinAction = new StemUtility.DyadAxisAction() {
            @Override
            public void action(StemVariable out, String key, StemVariable leftStem, StemVariable rightStem) {
                out.put(key, leftStem.union(rightStem));

            }
        };
        StemUtility.axisDayadRecursion(outStem, leftStem, rightStem, doJoinOnLastAxis ? 1000000 : (axis - 1), doJoinOnLastAxis, joinAction);
/*
        if (doJoinOnLastAxis) {
            StemUtility.axisDayadRecursion(outStem, leftStem, rightStem, 1000000, doJoinOnLastAxis, joinAction);
        } else {
            StemUtility.axisDayadRecursion(outStem, leftStem, rightStem, axis - 1, doJoinOnLastAxis, joinAction);
        }
*/
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
