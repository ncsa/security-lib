package edu.uiuc.ncsa.qdl.evaluate;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.QDLStatementExecutionException;
import edu.uiuc.ncsa.qdl.exceptions.RankException;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;
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

import static edu.uiuc.ncsa.qdl.state.legacy.SymbolTable.var_regex;
import static edu.uiuc.ncsa.qdl.variables.Constant.*;
import static edu.uiuc.ncsa.qdl.variables.StemUtility.LAST_AXIS_ARGUMENT_VALUE;
import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:19 AM
 */
public class StemEvaluator extends AbstractFunctionEvaluator {
    public static final String STEM_NAMESPACE = "stem";

    @Override
    public String getNamespace() {
        return STEM_NAMESPACE;
    }

    public static final String STEM_FQ = STEM_NAMESPACE + State.NS_DELIMITER;
    public static final int STEM_FUNCTION_BASE_VALUE = 2000;

    public static final String SIZE = "size";
    public static final int SIZE_TYPE = 1 + STEM_FUNCTION_BASE_VALUE;

    public static final String SHORT_MAKE_INDICES = "n";
    public static final int MAKE_INDICES_TYPE = 4 + STEM_FUNCTION_BASE_VALUE;


    public static final String REMOVE = "remove";
    public static final int REMOVE_TYPE = 5 + STEM_FUNCTION_BASE_VALUE;

    public static final String SET_DEFAULT = "set_default";
    public static final int SET_DEFAULT_TYPE = 7 + STEM_FUNCTION_BASE_VALUE;

    public static final String BOX = "box";
    public static final int BOX_TYPE = 8 + STEM_FUNCTION_BASE_VALUE;

    public static final String UNBOX = "unbox";
    public static final int UNBOX_TYPE = 9 + STEM_FUNCTION_BASE_VALUE;

    public static final String UNION = "union";
    public static final int UNION_TYPE = 10 + STEM_FUNCTION_BASE_VALUE;


    public static final String HAS_VALUE = "has_value";
    public static final int HAS_VALUE_TYPE = 12 + STEM_FUNCTION_BASE_VALUE;

    public static final String DIMENSION = "dim";
    public static final int DIMENSION_TYPE = 14 + STEM_FUNCTION_BASE_VALUE;

    public static final String RANK = "rank";
    public static final int RANK_TYPE = 15 + STEM_FUNCTION_BASE_VALUE;

    public static final String FOR_EACH = "for_each";
    public static final int FOR_EACH_TYPE = 16 + STEM_FUNCTION_BASE_VALUE;

    // Key functions
    public static final String COMMON_KEYS = "common_keys";
    public static final int COMMON_KEYS_TYPE = 100 + STEM_FUNCTION_BASE_VALUE;

    public static final String EXCLUDE_KEYS = "exclude_keys";
    public static final int EXCLUDE_KEYS_TYPE = 101 + STEM_FUNCTION_BASE_VALUE;

    public static final String LIST_KEYS = "list_keys";
    public static final int LIST_KEYS_TYPE = 102 + STEM_FUNCTION_BASE_VALUE;

    public static final String HAS_KEYS = "has_keys";
    public static final int HAS_KEYS_TYPE = 103 + STEM_FUNCTION_BASE_VALUE;


    public static final String INCLUDE_KEYS = "include_keys";
    public static final int INCLUDE_KEYS_TYPE = 104 + STEM_FUNCTION_BASE_VALUE;


    public static final String RENAME_KEYS = "rename_keys";
    public static final int RENAME_KEYS_TYPE = 105 + STEM_FUNCTION_BASE_VALUE;

    public static final String MASK = "mask";
    public static final int MASK_TYPE = 106 + STEM_FUNCTION_BASE_VALUE;

    public static final String KEYS = "keys";
    public static final int KEYS_TYPE = 107 + STEM_FUNCTION_BASE_VALUE;

    public static final String SHUFFLE = "shuffle";
    public static final int SHUFFLE_TYPE = 108 + STEM_FUNCTION_BASE_VALUE;

    public static final String UNIQUE_VALUES = "unique";
    public static final int UNIQUE_VALUES_TYPE = 109 + STEM_FUNCTION_BASE_VALUE;


    public static final String JOIN = "join";
    public static final int JOIN_TYPE = 110 + STEM_FUNCTION_BASE_VALUE;

    public static final String ALL_KEYS = "indices";
    public static final int ALL_KEYS_TYPE = 111 + STEM_FUNCTION_BASE_VALUE;

    public static final String TRANSPOSE = "transpose";
    public static final String TRANSPOSE2 = "τ";
    public static final int TRANSPOSE_TYPE = 112 + STEM_FUNCTION_BASE_VALUE;

    public static final String REMAP = "remap";
    public static final int REMAP_TYPE = 114 + STEM_FUNCTION_BASE_VALUE;

    public static final String IS_LIST = "is_list";
    public static final int IS_LIST_TYPE = 204 + STEM_FUNCTION_BASE_VALUE;

    public static final String VALUES = "values";
    public static final int VALUES_TYPE = 208 + STEM_FUNCTION_BASE_VALUE;


    // Conversions to/from JSON.
    public static final String TO_JSON = "to_json";
    public static final int TO_JSON_TYPE = 300 + STEM_FUNCTION_BASE_VALUE;

    public static final String FROM_JSON = "from_json";
    public static final int FROM_JSON_TYPE = 301 + STEM_FUNCTION_BASE_VALUE;

    public static final String JSON_PATH_QUERY = "query";
    public static final int JSON_PATH_QUERY_TYPE = 302 + STEM_FUNCTION_BASE_VALUE;

    /**
     * A list of the names that this Evaluator knows about. NOTE that this must be kept in sync
     * by the developer since it is used to determine if a function is built in or a user-defined function.
     */

    @Override
    public String[] getFunctionNames() {
        if (fNames == null) {
            fNames = new String[]{
                    DIMENSION, RANK,
                    TRANSPOSE, TRANSPOSE2,
                    REMAP,
                    SIZE,
                    JOIN,
                    SHORT_MAKE_INDICES,
                    HAS_VALUE,
                    REMOVE,
                    SET_DEFAULT,
                    BOX,
                    UNBOX,
                    UNION,
                    FOR_EACH,
                    COMMON_KEYS,
                    EXCLUDE_KEYS,
                    LIST_KEYS,
                    ALL_KEYS,
                    HAS_KEYS,
                    INCLUDE_KEYS,
                    RENAME_KEYS,
                    SHUFFLE,
                    MASK,
                    KEYS, VALUES,
                    IS_LIST,
                    UNIQUE_VALUES,
                    TO_JSON,
                    FROM_JSON, JSON_PATH_QUERY};
        }
        return fNames;
    }


    @Override
    public int getType(String name) {
        switch (name) {
            case DIMENSION:
                return DIMENSION_TYPE;
            case RANK:
                return RANK_TYPE;
            case JOIN:
                return JOIN_TYPE;
            case SIZE:
                return SIZE_TYPE;
            case SET_DEFAULT:
                return SET_DEFAULT_TYPE;
            case HAS_VALUE:
                return HAS_VALUE_TYPE;
            case REMAP:
                return REMAP_TYPE;
            case MASK:
                return MASK_TYPE;
            case COMMON_KEYS:
                return COMMON_KEYS_TYPE;
            case KEYS:
                return KEYS_TYPE;
            case VALUES:
                return VALUES_TYPE;
            case LIST_KEYS:
                return LIST_KEYS_TYPE;
            case ALL_KEYS:
                return ALL_KEYS_TYPE;
            case HAS_KEYS:
                return HAS_KEYS_TYPE;
            case INCLUDE_KEYS:
                return INCLUDE_KEYS_TYPE;
            case EXCLUDE_KEYS:
                return EXCLUDE_KEYS_TYPE;
            case RENAME_KEYS:
                return RENAME_KEYS_TYPE;
            case SHUFFLE:
                return SHUFFLE_TYPE;
            case UNIQUE_VALUES:
                return UNIQUE_VALUES_TYPE;
            case IS_LIST:
                return IS_LIST_TYPE;

            case TRANSPOSE:
            case TRANSPOSE2:
                return TRANSPOSE_TYPE;

            case SHORT_MAKE_INDICES:
                return MAKE_INDICES_TYPE;
            case REMOVE:
                return REMOVE_TYPE;
            case BOX:
                return BOX_TYPE;
            case UNBOX:
                return UNBOX_TYPE;

            case UNION:
                return UNION_TYPE;
            case TO_JSON:
                return TO_JSON_TYPE;
            case FROM_JSON:
                return FROM_JSON_TYPE;
            case JSON_PATH_QUERY:
                return JSON_PATH_QUERY_TYPE;
            case FOR_EACH:
                return FOR_EACH_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        try {
            return evaluate2(polyad, state);
        } catch (QDLException q) {
            throw q;
        } catch (Throwable t) {
            QDLStatementExecutionException qq = new QDLStatementExecutionException(t, polyad);
            throw qq;
        }
    }

    public boolean evaluate2(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case DIMENSION:
                doDimension(polyad, state);
                return true;
            case RANK:
                doRank(polyad, state);
                return true;
            case JOIN:
                doJoin(polyad, state);
                return true;
            case SIZE:
                return doSize(polyad, state);
            case SET_DEFAULT:
                doSetDefault(polyad, state);
                return true;
            case TRANSPOSE:
            case TRANSPOSE2:
                doTransform(polyad, state);
                return true;
            case MASK:
                doMask(polyad, state);
                return true;
            case COMMON_KEYS:
                doCommonKeys(polyad, state);
                return true;
            case KEYS:
                doKeys(polyad, state);
                return true;
            case VALUES:
                doValues(polyad, state);
                return true;
            case LIST_KEYS:
                doListKeys(polyad, state);
                return true;
            case ALL_KEYS:
                doIndices(polyad, state);
                return true;
            case HAS_KEYS:
                doHasKeys(polyad, state);
                return true;
            case INCLUDE_KEYS:
                doIncludeKeys(polyad, state);
                return true;
            case HAS_VALUE:
                doIsMemberOf(polyad, state);
                return true;
            case EXCLUDE_KEYS:
                doExcludeKeys(polyad, state);
                return true;
            case RENAME_KEYS:
                doRenameKeys(polyad, state);
                return true;
            case SHUFFLE:
                shuffleKeys(polyad, state);
                return true;
            case UNIQUE_VALUES:
                doUniqueValues(polyad, state);
                return true;
            case IS_LIST:
                doIsList(polyad, state);
                return true;

            case REMAP:
                doRemap(polyad, state);
                return true;

            case SHORT_MAKE_INDICES:
                doMakeIndex(polyad, state);
                return true;
            case REMOVE:
                doRemove(polyad, state);
                return true;
            case BOX:
                doBox(polyad, state);
                return true;
            case UNBOX:
                doUnBox(polyad, state);
                return true;

            case UNION:
                doUnion(polyad, state);
                return true;
            case TO_JSON:
                doToJSON(polyad, state);
                return true;
            case FOR_EACH:
                doForEach(polyad, state);
                return true;
            case FROM_JSON:
                doFromJSON(polyad, state);
                return true;
            case JSON_PATH_QUERY:
                doJPathQuery(polyad, state);
                return true;
        }
        return false;
    }

    private void doRemap(Polyad polyad, State state) {
        if (polyad.getArgCount() < 2 || 3 < polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + REMAP + " function requires  two or three arguments");
        }
        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));
        if (!isStem(arg1)) {
            throw new IllegalArgumentException(REMAP + " requires stem as its first argument");
        }
        StemVariable stem = (StemVariable) arg1;
        Object arg2 = polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1));

        if (!isStem(arg2)) {
            throw new IllegalArgumentException(REMAP + " requires an stem or integer as its second argument");
        }
        StemVariable newIndices = null;
        if (polyad.getArgCount() == 3) {
            Object arg3 = polyad.evalArg(2, state);
            checkNull(arg3, polyad.getArgAt(2), state);
            if (!isStem(arg3)) {
                throw new IllegalArgumentException(REMAP + " requires an stem or integer as its third argument");
            }

            newIndices = (StemVariable) arg3;
            threeArgRemap(polyad, stem, (StemVariable) arg2, newIndices);
            return;
        }
        twoArgRemap(polyad, stem, (StemVariable) arg2);


    }

    protected void doIndices(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException(ALL_KEYS + " requires at least one argument");
        }

        if (2 < polyad.getArgCount()) {
            throw new IllegalArgumentException(ALL_KEYS + " requires at most two arguments");
        }

        Object arg0 = polyad.evalArg(0, state);
        checkNull(arg0, polyad.getArgAt(0), state);
        if (!isStem(arg0)) {
            throw new IllegalArgumentException(ALL_KEYS + " requires a stem as its first argument");
        }
        StemVariable stem = (StemVariable) arg0;
        boolean returnAll = true;
        long axis = 0L;
        if (polyad.getArgCount() == 2) {
            returnAll = false;
            Object arg1 = polyad.evalArg(1, state);
            checkNull(arg1, polyad.getArgAt(1), state);
            if (!isLong(arg1)) {
                throw new IllegalArgumentException(ALL_KEYS + " requires the second argumement be an integer if present.");
            }
            axis = (Long) arg1;
        }
        StemVariable rc = returnAll ? stem.indices() : stem.indices(axis);
        polyad.setResult(rc);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(Boolean.TRUE);
    }

    protected void doValues(Polyad polyad, State state) {
        // create a list of values for a stem.
        StemVariable out = new StemVariable();
        Object object0 = polyad.evalArg(0, state);
        checkNull(object0, polyad.getArgAt(0));

        if (isStem(object0)) {
            StemVariable inStem = (StemVariable) object0;
            ArrayList values = new ArrayList();
            for (Object key : inStem.keySet()) {
                Object obj = inStem.get(key);
                if (!values.contains(obj)) {
                    values.add(inStem.get(key));
                }
            }
            out.addList(values);

        } else {
            out.put(0L, object0);
        }

        polyad.setResult(out);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);

    }

    /**
     * Apply n-ary function to outer product of stems. There n stems passed in.
     *
     * @param polyad
     * @param state
     */
    protected void doForEach(Polyad polyad, State state) {
        FunctionReferenceNode frn = getFunctionReferenceNode(state, polyad.getArgAt(0), true);

        StemVariable[] stems = new StemVariable[polyad.getArgCount() - 1];
        for (int i = 1; i < polyad.getArgCount(); i++) {

            Object arg = polyad.evalArg(i, state);
            checkNull(arg, polyad.getArgAt(i));

/*        If we wanted to allow for stems by converting them over to stems.
          Downside is for_each(@f,a,b,c,d) is a 4 rank stem of dim [1,1,1,1]
          which is messy to unpack. Either prohibit non-stems (now) or
          possibly allow them but don't add rank for stems.
            if (!isStem(arg)) {
              //  throw new IllegalArgumentException("All arguments to except the first must be stem. Argument " + i + " is not a stem.");
                StemVariable ss = new StemVariable();
                ss.listAppend(arg);
                stems[i - 1] = ss;
            }else {
                stems[i - 1] = (StemVariable) arg;
            }
*/
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
        polyad.setResultType(STEM_TYPE);
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

    protected void forEachRecursion(StemVariable output,
                                    ExpressionImpl f,
                                    State state,
                                    StemVariable[] stems,
                                    ArrayList<Object> rawArgs,
                                    int depth) {
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

    /**
     * Takes a list of Java objects and converts them to QDL constants to be used as
     * arguments to functions. Checks also that there are no illegal values first.
     *
     * @param objects
     * @return
     */
    protected ArrayList<StatementWithResultInterface> toConstants(ArrayList<Object> objects) {
        ArrayList<StatementWithResultInterface> args = new ArrayList<>();
        for (Object obj : objects) {
            int type = Constant.getType(obj);
            if (type == UNKNOWN_TYPE) {
                // Future proofing in case something changes in the future internally
                throw new IllegalArgumentException(" unknown object type");
            }
            args.add(new ConstantNode(obj, type));
        }
        return args;
    }

    protected void doJPathQuery(Polyad polyad, State state) {
        Object arg0 = polyad.evalArg(0, state);
        checkNull(arg0, polyad.getArgAt(0));

        if (!isStem(arg0)) {
            throw new IllegalArgumentException(JSON_PATH_QUERY + " requires a stem as its first argument");
        }
        StemVariable stemVariable = (StemVariable) arg0;
        Object arg1 = polyad.evalArg(1, state);
        checkNull(arg1, polyad.getArgAt(1));

        if (!isString(arg1)) {
            throw new IllegalArgumentException(JSON_PATH_QUERY + " requires a string as its second argument");
        }

        String query = (String) arg1;
        Configuration conf = null;
        boolean returnAsPaths = false;
        if (polyad.getArgCount() == 3) {
            Object arg2 = polyad.evalArg(2, state);
            checkNull(arg2, polyad.getArgAt(2));

            if (!isBoolean(arg2)) {
                throw new IllegalArgumentException(JSON_PATH_QUERY + " requires a boolean as its third argument");
            }
            returnAsPaths = (Boolean) arg2;
            conf = Configuration.builder()
                    .options(Option.AS_PATH_LIST).build();
        }
        String output;
        StemVariable outStem;

        if (returnAsPaths) {
            try {
                output = JsonPath.using(conf).parse(stemVariable.toJSON(false).toString()).read(query).toString();
                outStem = stemPathConverter(output);
            } catch (JsonPathException jpe) {
                if (jpe.getMessage().contains("No results")) {
                    // A "feature" of this API is to return an empty list if there are no values
                    // but throw a path exception if you want the results as paths.
                    // Only way to check is to look at the message. 
                    outStem = new StemVariable();
                } else {
                    throw new IllegalArgumentException("error processing query:" + jpe.getMessage());
                }
            }
        } else {
            try {
                conf = Configuration.builder()
                        .options(Option.ALWAYS_RETURN_LIST).build();
                // This type of query returns the values of the query, not the indices, so
                // we just have to convert it to a stem and return that. Handles the couple cases
                // of a JSON array vs object. The JsonPath generally tends to return arrays so we
                // test for that first.
                output = JsonPath.using(conf).parse(stemVariable.toJSON(false).toString()).read(query).toString();
                outStem = new StemVariable();
                try {
                    JSONArray array = JSONArray.fromObject(output);
                    outStem.fromJSON(array);
                } catch (JSONException x) {
                    JSONObject jo = JSONObject.fromObject(output);
                    outStem.fromJSON(jo);
                }

            } catch (JsonPathException jpe) {
                throw new IllegalArgumentException("error processing query:" + jpe.getMessage());
            }
        }
        polyad.setResult(outStem);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /*
     * This converts a list of JSON Path indices to stem indices. It is very simple
     * minded.
     *
     * @param indexList
     * @return
     */
/*
    protected String crappyConverter(String indexList) {
        return vPathConverter(indexList);
    }
*/

    /*
      x. := {'sub':'http://cilogon.org/serverT/users/17048', 'idp_name':'Supercomputing at BSU', 'eppn':'rbriuis@bigstate.edu', 'cert_subject_dn':'/DC=org/DC=cilogon/C=US/O=Big State Supercomputing Center/CN=Robert Bruce T17099', 'eptid':'https://idp.bigstate.edu/idp/shibboleth!https://cilogon.org/shibboleth!65P3o9FNjrp4z6+WI7Dir/4I=', 'iss':'https://test.cilogon.org', 'given_name':'Robert', 'voPersonExternalID':'rbriuis@bigstate.edu', 'nonce':'R72KPZ4Pwo9nPd9z1qCA04hBALMC-yVGUOGyTn-miHo', 'aud':'myproxy:oa4mp,2012:/client_id/910d7984412870aa6e199f9afrab8', 'acr':'https://refeds.org/profile/mfa', 'uid':'rbriuis', 'idp':'https://idp.bigstate.edu/idp/shibboleth', 'affiliation':'staff@bigstate.edu;employee@bigstate.edu;member@bigstate.edu', 'uidNumber':'55939', 'auth_time':'1623103279', 'name':'Roibert a Briuis', 'isMemberOf':[{'name':'all_users', 'id':13002},{'name':'staff_reporting', 'id':16405},{'name':'list_allbsu', 'id':18942}], 'exp':1624053679, 'iat':1623103279, 'family_name':'Bruce', 'email':'bob@bigstate.edu'}
     ndx. :=  query(x., '$..name', true)
     x.ndx.1

     */

    /**
     * Converts the result of a JSON Path query to a list of vPaths like
     * <pre>
     * ['·isMemberOf·0·name','·isMemberOf·2·name']
     * </pre>
     * This works peachy but  QDL 1.4 has support for index lists directly, so
     * we should use that.
     * <p>
     * Might want to have these returned as an option though so keep.
     * </p>
     *
     * @param indexList
     * @return
     */
    protected StemVariable vPathConverter(String indexList) {
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
        StemVariable outStem = new StemVariable();
        try {
            JSONArray array = JSONArray.fromObject(arrayOut.toString());
            outStem.fromJSON(array);
        } catch (JSONException x) {
            JSONObject jo = JSONObject.fromObject(arrayOut.toString());
            outStem.fromJSON(jo);
        }
        return outStem;
    }

    /**
     * Convert output of a JSON query to a stem of lists. Each list is an
     * index entry.
     *
     * @param indexList
     * @return
     */
    protected StemVariable stemPathConverter(String indexList) {
        QDLCodec codec = new QDLCodec();
        JSONArray arrayIn = JSONArray.fromObject(indexList);
        StemVariable arrayOut = new StemVariable();
        for (int i = 0; i < arrayIn.size(); i++) {
            String x = arrayIn.getString(i);
            x = x.substring(2); // All JSON paths start with a $.
            StringTokenizer tokenizer = new StringTokenizer(x, "[");
            StemVariable r = new StemVariable();
            while (tokenizer.hasMoreTokens()) {
                String nextOne = tokenizer.nextToken();
                if (nextOne.startsWith("'")) {
                    nextOne = nextOne.substring(1);
                }
                nextOne = nextOne.substring(0, nextOne.length() - 1);
                if (nextOne.endsWith("'")) {
                    nextOne = nextOne.substring(0, nextOne.length() - 1);
                }
                r.listAppend(nextOne);
                //  r = r + QDLConstants.STEM_PATH_MARKER2 + codec.encode(nextOne);
            }
            arrayOut.put(i, r);

        }
        return arrayOut;
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
            polyad.setResultType(LONG_TYPE);
            polyad.setResult(0L);
            return;
        }
        polyad.setEvaluated(true);
        StemVariable s = (StemVariable) polyad.getArgAt(0).getResult();
        polyad.setResult(s.getRank());
        polyad.setResultType(LONG_TYPE);

    }

    private void doDimension(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0 || !isStem(polyad.evalArg(0, state))) {
            polyad.setEvaluated(true);
            polyad.setResultType(LONG_TYPE);
            polyad.setResult(0L);
            return;
        }
        // so its a stem
        polyad.setEvaluated(true);
        StemVariable s = (StemVariable) polyad.getArgAt(0).getResult();
        polyad.setResult(s.dim());
        polyad.setResultType(STEM_TYPE);

    }

    private void doUniqueValues(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("the " + UNIQUE_VALUES + " function requires 1 argument");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArgAt(0).getResult();
        checkNull(arg, polyad.getArgAt(0));

        if (!isStem(arg)) {
            polyad.setResult(new StemVariable()); // just an empty stem
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable stemVariable = (StemVariable) arg;
        StemVariable out = stemVariable.almostUnique().almostUnique();

        polyad.setResult(out);
        polyad.setResultType(STEM_TYPE);
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
            throw new IllegalArgumentException(HAS_VALUE + " requires 2 arguments.");
        }
        Object leftArg = polyad.evalArg(0, state);
        checkNull(leftArg, polyad.getArgAt(0));

        Object rightArg = polyad.evalArg(1, state);
        checkNull(rightArg, polyad.getArgAt(1));
        // breaks  down tidily in to 4 cases.
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
                if(isSet(rightArg)){
                    QDLSet rSet = (QDLSet) rightArg;
                      for(String key : lStem.keySet()){
                            result.put(key, rSet.contains(lStem.get(key)));
                      }
                }else {
                    // check if each element in the left stem matches the value of the right arg.
                    for (String lKey : lStem.keySet()) {
                        result.put(lKey, lStem.get(lKey).equals(rightArg) ? Boolean.TRUE : Boolean.FALSE); // got to finagle these are right Java objects
                    }
                }
            }
            polyad.setResult(result);
            polyad.setResultType(STEM_TYPE);

        } else {
            // left arg is not a stem.
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
                if(isSet(rightArg)){
                   result = ((QDLSet)rightArg).contains(leftArg);
                }else {
                    result = leftArg.equals(rightArg);
                }
            }
            polyad.setResult(result);
            polyad.setResultType(BOOLEAN_TYPE);

        }
        polyad.setEvaluated(true);
    }


    protected void doFromJSON(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException(FROM_JSON + " requires an argument");
        }
        Object arg = polyad.evalArg(0, state);
        checkNull(arg, polyad.getArgAt(0));

        if (!isString(arg)) {
            throw new IllegalArgumentException(FROM_JSON + " requires a string as its argument");
        }
        boolean convertKeys = false;

        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            checkNull(arg2, polyad.getArgAt(2));
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
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);

    }

    protected void doToJSON(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException(TO_JSON + " requires an argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));

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
            checkNull(arg2, polyad.getArgAt(1));

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
            checkNull(arg2, polyad.getArgAt(1));
            if (isBoolean(arg2)) {
                convertNames = (Boolean) arg2;
            } else {
                throw new IllegalArgumentException(TO_JSON + " with 3 arguments requires a boolean as its second argument");
            }

            Object arg3 = polyad.evalArg(2, state);
            checkNull(arg3, polyad.getArgAt(2));
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
        polyad.setResultType(STRING_TYPE);
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
            Object arg = polyad.evalArg(i, state);
            checkNull(arg, polyad.getArgAt(i));
            if (!isStem(arg)) {
                throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
            }
            outStem = outStem.union((StemVariable) arg);

/*   really old stuff.
            if (polyad.getArgAt(i) instanceof StatementWithResultInterface) {
                StatementWithResultInterface vn = polyad.getArgAt(i);
                stem = (StemVariable) vn.getResult();
            }
            if ((polyad.getArgAt(i) instanceof StemVariable)) {
                stem = (StemVariable) polyad.getArgAt(i);
            }
            if (stem == null) {
                throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
            }
*/
        }
        polyad.setResult(outStem);
        polyad.setEvaluated(true);
        polyad.setResultType(STEM_TYPE);

    }

    private void doUnBox(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + UNBOX + " function requires  at least 1 argument");
        }
        Object arg0 = polyad.evalArg(0, state);
        checkNull(arg0, polyad.getArgAt(0));

        // should take either a stem or a variable reference to it.
        StemVariable stem = null;
        Boolean safeMode = Boolean.TRUE;
        if (polyad.getArgCount() == 2) {
            Object o = polyad.evalArg(1, state);
            checkNull(o, polyad.getArgAt(1));

            if (!isBoolean(o)) {
                throw new IllegalArgumentException("The second argument of " + UNBOX + " must be a boolean.");
            }
            safeMode = (Boolean) o;
        }
        String varName = null;
        if (polyad.getArgAt(0) instanceof VariableNode) {
            VariableNode vn = (VariableNode) polyad.getArgAt(0);
            varName = vn.getVariableReference();
            if (!(vn.getResult() instanceof StemVariable)) {
                throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
            }
            stem = (StemVariable) vn.getResult();
        }
        if (polyad.getArgAt(0) instanceof StemVariableNode) {
            stem = (StemVariable) polyad.evalArg(0, state);
        }

        if ((polyad.getArgAt(0) instanceof StemVariable)) {
            stem = (StemVariable) polyad.getArgAt(0);
        }
        if (stem == null) {
            throw new IllegalArgumentException("You can only unbox a stem. This is not a stem.");
        }
        if (stem.getStemList().size() != 0) {
            throw new IllegalArgumentException("You can only unbox a stem without a list. List elements cannot be reasonably unboxed.");
        }
        // Make a safe copy of the state to unpack this in case something bombs
        List<String> keys = new ArrayList<>();
        State localState = state.newCleanState();
        QDLCodec codec = new QDLCodec();

        for (String key : stem.keySet()) {
            Object ob = stem.get(key);
            key = key + (isStem(ob) ? STEM_INDEX_MARKER : "");
            if (safeMode) {
                if (!pattern.matcher(key).matches()) {
                    key = codec.encode(key);
                }
                if (state.isDefined(key)) {
                    throw new IllegalArgumentException("name clash in safe mode for '" + key + "'");
                }


            } else {
                if (!pattern.matcher(key).matches()) {
                    throw new IllegalArgumentException("the variable name '" + key + "' is not a legal variable name.");
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
        polyad.setResultType(BOOLEAN_TYPE);

        polyad.setEvaluated(true);
    }

    Pattern pattern = Pattern.compile(var_regex);

    /**
     * Take a collection of variables and stem them up, removing them from the symbol table.
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
            if (!(polyad.getArgAt(i) instanceof VariableNode)) {
                throw new IllegalArgumentException("You must supply a list of variables to box.");
            }
            VariableNode vn = (VariableNode) polyad.getArgAt(i);
            varNames.add(vn.getVariableReference());
            stem.put(vn.getVariableReference(), vn.getResult());
        }
        for (String varName : varNames) {
            state.remove(varName);
        }
        polyad.setResult(stem);
        polyad.setEvaluated(true);
        polyad.setResultType(STEM_TYPE);
    }

    protected boolean doSize(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            //throw new IllegalArgumentException("the " + SIZE + " function requires 1 argument");
            return false;
        }
        Object arg = polyad.evalArg(0, state);
        checkNull(arg, polyad.getArgAt(0));

        long size = 0;
        if (isStem(arg)) {
            size = new Long(((StemVariable) arg).size());
        }
        if (arg instanceof String) {
            size = new Long(arg.toString().length());
        }
        polyad.setResult(size);
        polyad.setResultType(LONG_TYPE);
        polyad.setEvaluated(true);
        return true;
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
        checkNull(arg, polyad.getArgAt(0));

        int returnScope = all_keys;
        int returnType = UNKNOWN_TYPE;
        boolean returnByType = false;
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            checkNull(arg2, polyad.getArgAt(1));

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
            polyad.setResultType(STEM_TYPE);
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
                        if (Constant.getType(stemVariable.get(key)) != STEM_TYPE) {
                            if (out.isLongIndex(key)) {
                                out.put(i++, Long.parseLong(key));
                            } else {
                                out.put(i++, key);
                            }
                        }
                        break;
                    case only_stems:
                        if (Constant.getType(stemVariable.get(key)) == STEM_TYPE) {
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
        polyad.setResultType(STEM_TYPE);
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
        Object arg = polyad.evalArg(0, state);
        checkNull(arg, polyad.getArgAt(0));
        if (!isStem(arg)) {
            polyad.setResult(new StemVariable()); // just an empty stem
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        int returnScope = all_keys;
        int returnType = UNKNOWN_TYPE;
        boolean returnByType = false;
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            checkNull(arg2, polyad.getArgAt(1));

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
                        if (Constant.getType(stemVariable.get(key)) != STEM_TYPE) {
                            putLongOrStringKey(out, key);
                        }
                        break;
                    case only_stems:
                        if (Constant.getType(stemVariable.get(key)) == STEM_TYPE) {
                            putLongOrStringKey(out, key);
                        }
                        break;
                }
            }
        }
        polyad.setResult(out);
        polyad.setResultType(STEM_TYPE);
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
        Object arg = polyad.evalArg(0, state);
        checkNull(arg, polyad.getArgAt(0));
        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + HAS_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        Object arg2 = polyad.getArgAt(1).getResult();
        checkNull(arg2, polyad.getArgAt(1));

        if (!isStem(arg2)) {
            polyad.setResult(target.containsKey(arg2.toString()));
            polyad.setResultType(BOOLEAN_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable result = target.hasKeys((StemVariable) arg2);
        polyad.setResult(result);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);
    }


    protected void doMakeIndex(Polyad polyad, State state) {
        if (0 == polyad.getArgCount()) {
            throw new IllegalArgumentException("the " + SHORT_MAKE_INDICES + " function requires at least 1 argument");
        }
        Object arg = polyad.evalArg(0, state);
        checkNull(arg, polyad.getArgAt(0));
        boolean gotOne = false;
        // First argument always has to be an integer.
        boolean isLongArg = arg instanceof Long;
        boolean isStemArg = arg instanceof StemVariable;

        if (!(isLongArg || isStemArg)) {
            throw new IndexError("only non-negative integer arguments or a stem are allowed as first argument  in " + SHORT_MAKE_INDICES);
        }

        int[] lengths = null;

        if (isStemArg) {
            StemVariable argStem = (StemVariable) arg;
            JSON json = argStem.toJSON();
            if (!json.isArray()) {
                throw new IndexError("first argument must be a stem list in " + SHORT_MAKE_INDICES);

            }
            JSONArray array = (JSONArray) json;
            lengths = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                lengths[i] = array.getInt(i);
            }

        }
        Object[] fill = null;
        CyclicArgList cyclicArgList = null;
        boolean hasFill = false;
        if (polyad.getArgCount() != 1) {
            Object lastArg = polyad.evalArg(polyad.getArgCount() - 1, state);
            checkNull(lastArg, polyad.getArgAt(polyad.getArgCount() - 1));

            if (!isStem(lastArg)) {
                // fine, no fill.
                hasFill = false;
            } else {
                StemVariable fillStem = (StemVariable) lastArg;
                if (!fillStem.isList()) {
                    throw new IllegalArgumentException("fill argument must be a list of scalars");
                }
                StemList stemList = fillStem.getStemList();
                fill = stemList.toArray(true, false);
                cyclicArgList = new CyclicArgList(fill);

                hasFill = true;
            }
        }

        // Special case is a simple list. n(3) should yield [0,1,2] rather than a 1x3
        // array (recursion automatically boxes it into at least a 2 rank array).
        if (isLongArg && (polyad.getArgCount() == 1 || (polyad.getArgCount() == 2 && hasFill))) {
            long size = (Long) arg;
            StemVariable out = createSimpleStemVariable(polyad, cyclicArgList, hasFill, size);
            if (out == null) return; // special case where zero length requested


            polyad.setResult(out);
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        // so the left arg is a stem Check simple case
        if (isStemArg) {
            StemVariable argStem = (StemVariable) arg;
            if (argStem.size() == 1) {
                long size = (Long) argStem.get(0L);
                StemVariable out = createSimpleStemVariable(polyad, cyclicArgList, hasFill, size);
                if (out == null) return; // special case where zero length requested


                polyad.setResult(out);
                polyad.setResultType(STEM_TYPE);
                polyad.setEvaluated(true);
                return;

            }
        }

        int lastArgIndex = polyad.getArgCount() - 1;
        if (fill != null && fill.length != 0) {
            lastArgIndex--; // last arg is the fill pattern
        }

        if (lengths == null) {
            lengths = new int[lastArgIndex + 1];
            for (int i = 0; i < lastArgIndex + 1; i++) {
                Object obj = polyad.evalArg(i, state);
                if (!isLong(obj)) {
                    throw new IndexError("argument " + i + " is not an integer. All dimensions must be positive integers.");
                }
                lengths[i] = ((Long) obj).intValue();
                // Any dimension of 0 returns an empty list
                if (lengths[i] == 0) {
                    polyad.setResult(new StemVariable());
                    polyad.setResultType(STEM_TYPE);
                    polyad.setEvaluated(true);
                    return;
                }
                if (lengths[i] < 0L) {
                    throw new IndexError("argument " + i + " is negative. All dimensions must be positive integers.");
                }
            }

        }
        StemVariable out = new StemVariable();
        indexRecursion(out, lengths, 0, cyclicArgList);
        polyad.setResult(out);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    private StemVariable createSimpleStemVariable(Polyad polyad, CyclicArgList cyclicArgList, boolean hasFill, long size) {
        if (size == 0) {
            polyad.setResult(new StemVariable());
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return null;
        }
        if (size < 0L) {
            throw new IndexError("negative index encountered");
        }
        StemList stemList;
        if (hasFill) {
            stemList = new StemList(size, cyclicArgList.next((int) size));
        } else {
            stemList = new StemList(size);
        }
        StemVariable out = new StemVariable();
        out.setStemList(stemList);
        return out;
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
     * Processs case subset(arg., list.) so that
     * <pre>
     * out.i := arg.list.i
     * </pre>
     *
     * @param polyad
     * @param stem
     * @param arg2
     */
    private void twoArgRemap(Polyad polyad, StemVariable stem, StemVariable arg2) {
        StemVariable indices = arg2;
        StemVariable output = new StemVariable();
        for (Object key : indices.keySet()) {
            Object v = indices.get(key);
            Object gotValue = null;
            if (v instanceof StemVariable) {
                StemVariable ii = (StemVariable) v;
                if (!ii.isList()) {
                    throw new IndexError("stem indices must be lists.");
                }
                IndexList indexList = new IndexList(ii);
                IndexList returnedIL = stem.get(indexList, true);
                if (returnedIL.size() == 1) {
                    gotValue = returnedIL.get(0);
                } else {
                    throw new IndexError("index does not resolve to a value.");
                }
            } else {
                gotValue = stem.get(v);
            }
            if (gotValue != null) {
                if (key instanceof Long) {
                    output.put((Long) key, gotValue);
                } else {
                    output.put((String) key, gotValue);
                }
            }
        }

        polyad.setResult(output);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /*
      a. := n(3,5,n(15))
  old. := all_keys(a.-1)
  new. := for_each(@reverse,  all_keys(a.-1))
  subset(a., new., old.)
     */

    /**
     * Subset with remapping such that for subset(arg., new_indices., old_indices.) out. satisfies
     * <pre>
     *     out.new_indices.i := arg.old_indices.i;
     * </pre>
     *
     * @param polyad
     * @param stem
     * @param newIndices
     * @param oldIndices
     */
    private void threeArgRemap(Polyad polyad, StemVariable stem,
                               StemVariable oldIndices,
                               StemVariable newIndices
    ) {
        StemVariable output = new StemVariable();
        for (long i = 0L; i < newIndices.size(); i++) {

            Object newI = newIndices.get(i);
            Object oldI = oldIndices.get(i);
            IndexList newIndex;
            if (isStem(newI)) {
                newIndex = new IndexList((StemVariable) newI);
            } else {
                newIndex = new IndexList();
                newIndex.add(newI);
            }
            IndexList oldIndex;
            if (isStem(oldI)) {
                oldIndex = new IndexList((StemVariable) oldI);
            } else {
                oldIndex = new IndexList();
                oldIndex.add(oldI);
            }
            // Note that if there is strict matching on and it works, there is a single
            // value at index 0 in the result.
            output.set(newIndex, stem.get(oldIndex, true).get(0));
        }

        polyad.setResult(output);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);
    }

    protected void doIsList(Polyad polyad, State state) {
        if (1 != polyad.getArgCount()) {
            throw new IllegalArgumentException(IS_LIST + " requires 1 argument");
        }
        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));
        if (!isStem(arg1)) {
            throw new IllegalArgumentException(IS_LIST + " requires stem as its first argument");
        }
        StemVariable stemVariable = (StemVariable) arg1;
        Boolean isList = stemVariable.isList();
        polyad.setResult(isList);
        polyad.setResultType(BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

/*
    protected void doListAppend(Polyad polyad, State state) {
        if (2 != polyad.getArgCount()) {
            throw new IllegalArgumentException(LIST_APPEND + " requires 2 arguments");
        }
        // surgery. If the first argument is null and a stem, turn it in to one.
        StemVariable stem1 = getOrCreateStem(
                polyad.getArgAt(0),
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
    }*/

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
        try {
            polyad.evalArg(0, state);
        } catch (IndexError indexError) {
            // it is possible that the user is trying to grab something impossible
            polyad.setEvaluated(true);
            polyad.setResult(Boolean.TRUE);
            polyad.setResultType(BOOLEAN_TYPE);
            return;
        }
        String var = null;
        if (polyad.getArgAt(0) instanceof VariableNode) {
            VariableNode variableNode = (VariableNode) polyad.getArgAt(0);
            // Don't evaluate this because it might not exist (that's what we are testing for). Just check
            // if the name is defined.
            var = variableNode.getVariableReference();
            if (var == null) {
                polyad.setResult(Boolean.FALSE);
            } else {
                state.remove(var);
                polyad.setResult(Boolean.TRUE);
            }
        }
        if (polyad.getArgAt(0) instanceof ConstantNode) {
            throw new IllegalArgumentException(" cannot remove a constant");
        }
        if (polyad.getArgAt(0) instanceof ESN2) {
            ESN2 esn2 = (ESN2) polyad.getArgAt(0);
            polyad.setResult(esn2.remove(state));
        }

        polyad.setResultType(BOOLEAN_TYPE);
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
        Object arg = polyad.getArgAt(0).getResult();
        checkNull(arg, polyad.getArgAt(0));

        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + INCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        Object arg2 = polyad.getArgAt(1).getResult();
        checkNull(arg2, polyad.getArgAt(1));
        if (!isStem(arg2)) {
            StemVariable result = new StemVariable();
            if (target.containsKey(arg2.toString())) {
                result.put(arg2.toString(), target.get(arg2.toString()));
            }
            polyad.setResult(result);
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable result = target.includeKeys((StemVariable) arg2);
        polyad.setResult(result);
        polyad.setResultType(STEM_TYPE);
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
        Object arg = polyad.getArgAt(0).getResult();
        checkNull(arg, polyad.getArgAt(0));
        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + EXCLUDE_KEYS + " command requires a stem as its first argument.");
        }
        StemVariable target = (StemVariable) arg;
        polyad.evalArg(1, state);
        Object arg2 = polyad.getArgAt(1).getResult();
        checkNull(arg2, polyad.getArgAt(1));

        if (!isStem(arg2)) {
            StemVariable result = new StemVariable();
            String excluded = arg2.toString();
            for (String ndx : target.keySet()) {
                result.put(ndx, target.get(ndx));
            }
            result.remove(excluded);
            polyad.setResult(result);
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemVariable result = target.excludeKeys((StemVariable) arg2);
        polyad.setResult(result);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);
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
     * read that in [4,2,3,1,0]: old → new, so 0 → 4, 1 → 2, 2→3, 3→1, 4→0
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
        Object arg = polyad.getArgAt(0).getResult();
        checkNull(arg, polyad.getArgAt(0));

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
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }

        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + SHUFFLE + " command requires a stem as its first argument.");
        }

        Object arg2 = polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1));

        // polyad.getArgAt(1).getResult();
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
            throw new IllegalArgumentException(" the supplied set of keys must match every key in the source stem.");
        }

        for (String key : keys) {
            if (newKeys.contains(key)) {
                String kk = newKeyStem.getString(key);
                usedKeys.remove(kk);
                Object vv = target.get(kk);
                output.put(key, vv);
            } else {
                throw new IllegalArgumentException("'" + key + "' is not a key in the second argument.");
            }
        }
        if (!usedKeys.isEmpty()) {
            throw new IllegalArgumentException(" each key in the left argument must be used as a value in the second argument. This assures that all elements are shuffled.");
        }

        polyad.setResult(output);
        polyad.setResultType(STEM_TYPE);
        polyad.setEvaluated(true);
    }

    /*
    This only renames keys in situ, i.e. it changes the stem given.
    THE critical difference between rename_keys and using remap is that
    rename_keys alters its argument and as such, only the changes you want
    to make happen. Using remap, omitting unchanged elements removes them
    from the result.
     */

    /**
     * <code>rename_keys(arg., indices.);</code><br/><br/> list. contains entries of the form
     * <pre>
     *    indices.old = new
     * </pre>
     * Note that this is different from {@link #doRemap(Polyad, State)}.
     * The result is that each key in arg. is renamed, but the values are not changed.
     * If a key is in indices. and does not correspond to one on the left, it is skipped,
     * by subsetting rule.
     * <br/><br/> Limitations are that it applies to the zeroth axis, modifies arg. and
     * the indices. are different than remap.
     *
     * @param polyad
     * @param state
     */

    protected void doRenameKeys(Polyad polyad, State state) {
        if (!(polyad.getArgCount() == 2 || polyad.getArgCount() == 3)) {
            throw new IllegalArgumentException("the " + RENAME_KEYS + " function requires 2 or 3 arguments");
        }
        polyad.evalArg(0, state);
        Object arg = polyad.getArgAt(0).getResult();
        checkNull(arg, polyad.getArgAt(0));

        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + RENAME_KEYS + " command requires a stem as its first argument.");
        }
        polyad.evalArg(1, state);

        Object arg2 = polyad.getArgAt(1).getResult();
        polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1));

        if (!isStem(arg2)) {
            throw new IllegalArgumentException("The " + RENAME_KEYS + " command requires a stem as its second argument.");
        }

        boolean overwriteKeys = false; //default
        if (polyad.getArgCount() == 3) {
            polyad.evalArg(2, state);

            Object arg3 = polyad.getArgAt(2).getResult();
            polyad.evalArg(2, state);
            checkNull(arg2, polyad.getArgAt(2));
            if (arg3 instanceof Boolean) {
                overwriteKeys = (Boolean) arg3;
            } else {
                throw new IllegalArgumentException(RENAME_KEYS + " third argument, if present, must be a boolean");
            }

        }
        StemVariable target = (StemVariable) arg;
        target.renameKeys((StemVariable) arg2, overwriteKeys);

        polyad.setResult(target);
        polyad.setResultType(STEM_TYPE);
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
        Object arg = polyad.getArgAt(0).getResult();
        checkNull(arg, polyad.getArgAt(0));

        if (!isStem(arg)) {
            throw new IllegalArgumentException("The " + COMMON_KEYS + " command requires a stem as its first argument.");
        }
        polyad.evalArg(1, state);

        Object arg2 = polyad.getArgAt(1).getResult();
        checkNull(arg2, polyad.getArgAt(1));

        if (!isStem(arg2)) {
            throw new IllegalArgumentException("The " + COMMON_KEYS + " command requires a stem as its second argument.");
        }

        StemVariable target = (StemVariable) arg;
        StemVariable result = target.commonKeys((StemVariable) arg2);

        polyad.setResult(result);
        polyad.setResultType(STEM_TYPE);
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
        StemVariable stemVariable = getOrCreateStem(polyad.getArgAt(0),
                state,
                "the " + SET_DEFAULT + " command accepts   only a stem variable as its first argument.");

        polyad.evalArg(1, state);
        Object oldDefault = stemVariable.getDefaultValue();

        Object defaultValue = polyad.getArgAt(1).getResult();
        stemVariable.setDefaultValue(defaultValue);
        // now return the previous result or null if there was none
        if (oldDefault == null) {
            polyad.setResult(QDLNull.getInstance());
            polyad.setResultType(NULL_TYPE);
        } else {
            polyad.setResult(oldDefault);
            polyad.setResultType(Constant.getType(oldDefault));
        }
        polyad.setEvaluated(true);
    }

    protected void doMask(Polyad polyad, State state) {
        if (polyad.getArgCount() != 2) {
            throw new IllegalArgumentException("the " + MASK + " function requires 2 arguments");
        }
        polyad.evalArg(0, state);
        polyad.evalArg(1, state);
        Object obj1 = polyad.getArgAt(0).getResult();
        checkNull(obj1, polyad.getArgAt(0));

        Object obj2 = polyad.getArgAt(1).getResult();
        checkNull(obj2, polyad.getArgAt(1));

        if (!areAllStems(obj1, obj2)) {
            throw new IllegalArgumentException("the " + MASK + " requires both arguments be stem variables");
        }
        StemVariable stem1 = (StemVariable) obj1;
        StemVariable stem2 = (StemVariable) obj2;
        StemVariable result = stem1.mask(stem2);
        polyad.setResultType(STEM_TYPE);
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
            checkNull(args[i], polyad.getArgAt(i));
        }
        int axis = 0;
        if (args.length == 3) {
            axis = ((Long) args[2]).intValue();
        }
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
            polyad.setResultType(STEM_TYPE);
            polyad.setResult(outStem);
            return;
        }
        if (leftStem.dim().size() == 1) {
            if (axis == -1) {
                StemVariable outStem = leftStem.union(rightStem);
                polyad.setEvaluated(true);
                polyad.setResultType(STEM_TYPE);
                polyad.setResult(outStem);
                return;
            }
            throw new RankException("axis of " + axis + " exceeds rank");
        }
        StemVariable outStem = new StemVariable();

        if (leftStem.isEmpty() || rightStem.isEmpty()) {
            // edge case -- they sent an empty argument, so don't blow up, just return nothing
            polyad.setResult(outStem);
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        StemUtility.DyadAxisAction joinAction = new StemUtility.DyadAxisAction() {
            @Override
            public void action(StemVariable out, String key, StemVariable leftStem, StemVariable rightStem) {
                out.put(key, leftStem.union(rightStem));

            }
        };
        if (Math.max(leftStem.getRank(), rightStem.getRank()) <= axis) {
            throw new RankException("axis of " + axis + " exceeds rank");
        }
        StemUtility.axisDayadRecursion(outStem, leftStem, rightStem, doJoinOnLastAxis ? 1000000 : (axis - 1), doJoinOnLastAxis, joinAction);
        polyad.setResult(outStem);
        polyad.setResultType(STEM_TYPE);
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

    protected void doTransform(Polyad polyad, State state) {
        /*  Waaaay easier to do this in QDL, but this should be in base system
           not a module.
         old. := all_keys(x., -1);
         rank := size(old.0);
         axis := (axis<0)?axis+rank:axis;
         permute. := axis~[;axis]~[axis+1;rank];
         new. := for_each(@shuffle, old., [permute.]);
         return(subset(x., new., old.));

                           old. := indices(x., -1);
                   rank := size(old.0);
                   perm. := p~~exclude_keys([;rank], p);
                   new. := for_each(@shuffle, old., [perm.]);
                   return(subset(x., new., old.));
         */
        if (polyad.getArgCount() == 0) {
            throw new IllegalArgumentException(TRANSPOSE + " requires at least one argument.");
        }

        if (2 < polyad.getArgCount()) {
            throw new IllegalArgumentException(TRANSPOSE + " takes at most two arguments.");
        }

        Object arg0 = polyad.evalArg(0, state);
        checkNull(arg0, polyad.getArgAt(0), state);
        if (!isStem(arg0)) {
            throw new IllegalArgumentException(TRANSPOSE + " requires a stem as its first argument");
        }
        StemVariable stem = (StemVariable) arg0;

        StemVariable oldIndices = stem.indices(-1L);
        // kludge, assume that the rank of all at the last axis is the same.
        int rank = ((StemVariable) oldIndices.get(0L)).size();

        if (rank == 1) {
            // nothing to do. This is just a list
            polyad.setResult(stem);
            polyad.setResultType(STEM_TYPE);
            polyad.setEvaluated(Boolean.TRUE);
            return;
        }

        StemVariable pStem0;
        // Start QDL. sliceNode is [;rank]
        OpenSliceNode sliceNode = new OpenSliceNode(polyad.getTokenPosition());
        sliceNode.getArguments().add(new ConstantNode(0L, LONG_TYPE));
        sliceNode.getArguments().add(new ConstantNode(Integer.toUnsignedLong(rank), LONG_TYPE));

        if (polyad.getArgCount() == 2) {
            Object arg1 = polyad.evalArg(1, state);
            checkNull(arg1, polyad.getArgAt(1), state);
            StemVariable stem1 = null;
            boolean arg2ok = false;
            if (isLong(arg1)) {
                Long longArg = (Long) arg1;
                if (longArg == 0L) {
                    // The are requesting essentially the identity permutation, so don't jump through hoops.
                    polyad.setResult(stem);
                    polyad.setResultType(STEM_TYPE);
                    polyad.setEvaluated(Boolean.TRUE);
                    return;
                }
                stem1 = new StemVariable();
                if (longArg < 0) {
                    long newArg = rank + longArg;
                    if (newArg < 0) {
                        throw new IndexError("the requested axis of " + longArg + " is not valid for a stem of rank " + rank);
                    }
                    stem1.listAppend(newArg);
                } else {
                    if (rank <= longArg) {
                        throw new IndexError("the requested axis of " + longArg + " is not valid for a stem of rank " + rank);
                    }
                    stem1.listAppend(arg1);
                }
                arg2ok = true;
            }
            if (isStem(arg1)) {
                stem1 = (StemVariable) arg1;
                arg2ok = true;
            }
            if (!arg2ok) {
                throw new IllegalArgumentException(TRANSPOSE + " requires an axis or stem of them as its second argument");
            }
            // If the second argument is p., then the new reshuffing is
            // p. ~ ~ exclude_keys([;rank], p.)
            Polyad excludeKeys = new Polyad(EXCLUDE_KEYS);
            excludeKeys.addArgument(sliceNode);
            excludeKeys.addArgument(new ConstantNode(stem1, STEM_TYPE));
            Dyad monadicTilde = new Dyad(OpEvaluator.TILDE_VALUE); // mondic tilde does not exist. It is done as []~arg.
            monadicTilde.setLeftArgument(new ConstantNode(new StemVariable(), STEM_TYPE));
            monadicTilde.setRightArgument(excludeKeys);
            Dyad dyadicTilde = new Dyad(OpEvaluator.TILDE_VALUE);
            dyadicTilde.setLeftArgument(new ConstantNode(stem1, STEM_TYPE));
            dyadicTilde.setRightArgument(monadicTilde);
            dyadicTilde.evaluate(state);
            pStem0 = (StemVariable) dyadicTilde.getResult();
        } else {
            // default is to use reverse([;rank]) as the second argument
            Polyad reverse = new Polyad(ListEvaluator.LIST_REVERSE);
            reverse.addArgument(sliceNode);
            ;
            reverse.evaluate(state);
            pStem0 = (StemVariable) reverse.getResult();
        }

        // Now we need to create QDL for
        // newIndices. := shuffle(oldIndices., pStem0.)
        StemVariable pStem = new StemVariable();
        pStem.put(0L, pStem0); // makes [pstem.]
        Polyad makeNew = new Polyad(FOR_EACH);
        FunctionReferenceNode frn = new FunctionReferenceNode();
        frn.setFunctionName(SHUFFLE);
        makeNew.addArgument(frn);
        makeNew.addArgument(new ConstantNode(oldIndices, STEM_TYPE));
        makeNew.addArgument(new ConstantNode(pStem, STEM_TYPE));
        StemVariable newIndices = (StemVariable) makeNew.evaluate(state);

        // QDl to remap everything.
        Polyad subset = new Polyad(REMAP);
        subset.addArgument(new ConstantNode(stem, STEM_TYPE));
        subset.addArgument(new ConstantNode(oldIndices, STEM_TYPE));
        subset.addArgument(new ConstantNode(newIndices, STEM_TYPE));
        polyad.setResult(subset.evaluate(state));
        polyad.setEvaluated(Boolean.TRUE); // set evaluated true or next line bombs.
        polyad.setResultType(Constant.getType(polyad.getResult()));
    }
}
