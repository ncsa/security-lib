package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLCodec;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.state.QDLConstants.*;

/**
 * This evaluates all string functions.
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:17 AM
 */

/*
Note that there is an inheritance hierarchy here with String being a super class of Math, etc.
This is driven by Java because it is better to have small classes that specialize in, say, String
functions rather than a massive single class. So the inheritence is just encapsulating the logic of this.
 */
public class StringEvaluator extends AbstractFunctionEvaluator {

    public static final int STRING_FUNCTION_BASE_VALUE = 3000;
    public static final String CONTAINS = "contains";
    public static final String SYS_CONTAINS = SYS_FQ + CONTAINS;
    public static final int CONTAINS_TYPE = 1 + STRING_FUNCTION_BASE_VALUE;

    public static final String TO_LOWER = "to_lower";
    public static final String SYS_TO_LOWER = SYS_FQ + TO_LOWER;
    public static final int TO_LOWER_TYPE = 2 + STRING_FUNCTION_BASE_VALUE;

    public static final String TO_UPPER = "to_upper";
    public static final String SYS_TO_UPPER = SYS_FQ + TO_UPPER;
    public static final int TO_UPPER_TYPE = 3 + STRING_FUNCTION_BASE_VALUE;

    public static final String TRIM = "trim";
    public static final String SYS_TRIM = SYS_FQ + TRIM;
    public static final int TRIM_TYPE = 4 + STRING_FUNCTION_BASE_VALUE;

    public static final String INSERT = "insert";
    public static final String SYS_INSERT = SYS_FQ + INSERT;
    public static final int INSERT_TYPE = 5 + STRING_FUNCTION_BASE_VALUE;

    public static final String SUBSTRING = "substring";
    public static final String SYS_SUBSTRING = SYS_FQ + SUBSTRING;
    public static final int SUBSTRING_TYPE = 6 + STRING_FUNCTION_BASE_VALUE;

    public static final String REPLACE = "replace";
    public static final String SYS_REPLACE = SYS_FQ + REPLACE;
    public static final int REPLACE_TYPE = 7 + STRING_FUNCTION_BASE_VALUE;

    public static final String INDEX_OF = "index_of";
    public static final String SYS_INDEX_OF = SYS_FQ + INDEX_OF;
    public static final int INDEX_OF_TYPE = 8 + STRING_FUNCTION_BASE_VALUE;

    public static final String TOKENIZE = "tokenize";
    public static final String SYS_TOKENIZE = SYS_FQ + TOKENIZE;
    public static final int TOKENIZE_TYPE = 9 + STRING_FUNCTION_BASE_VALUE;

    public static final String ENCODE = "vencode";
    public static final String SYS_ENCODE = SYS_FQ + ENCODE;
    public static final int ENCODE_TYPE = 10 + STRING_FUNCTION_BASE_VALUE;

    public static final String DECODE = "vdecode";
    public static final String SYS_DECODE = SYS_FQ + DECODE;
    public static final int DECODE_TYPE = 11 + STRING_FUNCTION_BASE_VALUE;

    public static final String DETOKENIZE = "detokenize";
    public static final String SYS_DETOKENIZE = SYS_FQ + DETOKENIZE;
    public static final int DETOKENIZE_TYPE = 12 + STRING_FUNCTION_BASE_VALUE;

    public static final String TO_URI = "to_uri";
    public static final String SYS_TO_URI = SYS_FQ + TO_URI;
    public static final int TO_URI_TYPE = 13 + STRING_FUNCTION_BASE_VALUE;

    public static final String FROM_URI = "from_uri";
    public static final String SYS_FROM_URI = SYS_FQ + FROM_URI;
    public static final int FROM_URI_TYPE = 14 + STRING_FUNCTION_BASE_VALUE;

    public static String FUNC_NAMES[] = new String[]{
            CONTAINS,
            TO_LOWER,
            TO_UPPER,
            TRIM,
            INSERT,
            SUBSTRING,
            REPLACE,
            INDEX_OF,
            TOKENIZE,
            DETOKENIZE,
            ENCODE,
            DECODE,
            TO_URI,
            FROM_URI};
    public static String FQ_FUNC_NAMES[] = new String[]{
            SYS_CONTAINS,
            SYS_TO_LOWER,
            SYS_TO_UPPER,
            SYS_TRIM,
            SYS_INSERT,
            SYS_SUBSTRING,
            SYS_REPLACE,
            SYS_INDEX_OF,
            SYS_TOKENIZE,
            SYS_DETOKENIZE,
            SYS_ENCODE,
            SYS_DECODE,
            SYS_TO_URI,
            SYS_FROM_URI};

    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        String[] funcNames = listFQ ? FQ_FUNC_NAMES : FUNC_NAMES;
        for (String key : funcNames) {
            names.add(key + "()");
        }
        return names;
    }

    @Override
    public String[] getFunctionNames() {
        return FUNC_NAMES;
    }

    @Override
    public int getType(String name) {
        switch (name) {
            case CONTAINS:
            case SYS_CONTAINS:
                return CONTAINS_TYPE;
            case TO_LOWER:
            case SYS_TO_LOWER:
                return TO_LOWER_TYPE;
            case TO_UPPER:
            case SYS_TO_UPPER:
                return TO_UPPER_TYPE;
            case SUBSTRING:
            case SYS_SUBSTRING:
                return SUBSTRING_TYPE;
            case REPLACE:
            case SYS_REPLACE:
                return REPLACE_TYPE;
            case TRIM:
            case SYS_TRIM:
                return TRIM_TYPE;
            case INSERT:
            case SYS_INSERT:
                return INSERT_TYPE;
            case INDEX_OF:
            case SYS_INDEX_OF:
                return INDEX_OF_TYPE;
            case TOKENIZE:
            case SYS_TOKENIZE:
                return TOKENIZE_TYPE;
            case DETOKENIZE:
            case SYS_DETOKENIZE:
                return DETOKENIZE_TYPE;
            case ENCODE:
            case SYS_ENCODE:
                return ENCODE_TYPE;
            case DECODE:
            case SYS_DECODE:
                return DECODE_TYPE;
            case TO_URI:
            case SYS_TO_URI:
                return TO_URI_TYPE;
            case FROM_URI:
            case SYS_FROM_URI:
                return FROM_URI_TYPE;
        }
        return UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case CONTAINS:
            case SYS_CONTAINS:
                doContains(polyad, state);
                return true;
            case TRIM:
            case SYS_TRIM:
                doTrim(polyad, state);
                return true;
            case INDEX_OF:
            case SYS_INDEX_OF:
                doIndexOf(polyad, state);
                return true;
            case TO_LOWER:
            case SYS_TO_LOWER:
                doSwapCase(polyad, state, true);
                return true;
            case TO_UPPER:
            case SYS_TO_UPPER:
                doSwapCase(polyad, state, false);
                return true;
            case REPLACE:
            case SYS_REPLACE:
                doReplace(polyad, state);
                return true;
            case INSERT:
            case SYS_INSERT:
                doInsert(polyad, state);
                return true;
            case TOKENIZE:
            case SYS_TOKENIZE:
                doTokenize(polyad, state);
                return true;
            case DETOKENIZE:
            case SYS_DETOKENIZE:
                doDetokeninze(polyad, state);
                return true;
            case SUBSTRING:
            case SYS_SUBSTRING:
                doSubstring(polyad, state);
                return true;
            case ENCODE:
            case SYS_ENCODE:
                doEncode(polyad, state);
                return true;
            case DECODE:
            case SYS_DECODE:
                doDecode(polyad, state);
                return true;
            case TO_URI:
            case SYS_TO_URI:
                doToURI(polyad, state);
                return true;
            case FROM_URI:
            case SYS_FROM_URI:
                doFromURI(polyad, state);
                return true;
        }
        return false;
    }

    private void doFromURI(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("Error: " + FROM_URI + " requires an argument");
        }
        Object object = polyad.evalArg(0, state);
        if (!isStem(object)) {
            throw new IllegalArgumentException("Error: " + FROM_URI + " requires a stem as its argument");
        }

        StemVariable s = (StemVariable) object;
        try {
            Long port = s.getLong("port");

            URI uri = new URI(s.getString(URI_SCHEME),
                    s.getString(URI_USER_INFO),
                    s.getString(URI_HOST),
                    port.intValue(),
                    s.getString(URI_PATH),
                    s.getString(URI_QUERY),
                    s.getString(URI_FRAGMENT));
            polyad.setResult(uri.toString());
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setEvaluated(Boolean.TRUE);
        } catch (URISyntaxException usx) {
            throw new IllegalArgumentException("error: this is not a valid uri");
        }
    }

    /**
     * Turn a string into a parsed uri.
     *
     * @param polyad
     * @param state
     */
    private void doToURI(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("Error: " + TO_URI + " requires an argument");
        }
        Object object = polyad.evalArg(0, state);
        if (!isString(object)) {
            throw new IllegalArgumentException("Error: " + TO_URI + " requires a string as its argument");
        }
        try {
            URI uri = URI.create(object.toString());
            StemVariable output = new StemVariable();
            putURIAttrib(output, URI_AUTHORITY, uri.getAuthority());
            putURIAttrib(output, URI_FRAGMENT, uri.getFragment());
            putURIAttrib(output, URI_HOST, uri.getHost());
            putURIAttrib(output, URI_PATH, uri.getPath());
            putURIAttrib(output, URI_QUERY, uri.getQuery());
            putURIAttrib(output, URI_SCHEME_SPECIFIC_PART, uri.getSchemeSpecificPart());
            putURIAttrib(output, URI_SCHEME, uri.getScheme());
            putURIAttrib(output, URI_USER_INFO, uri.getUserInfo());
            output.put(URI_PORT, new Long(uri.getPort()));
            polyad.setResult(output);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(Boolean.TRUE);
            return;
        } catch (Throwable t) {
            throw new IllegalArgumentException("Error: \"" + object + "\" is not a valid uri: " + t.getMessage());
        }

    }


    void putURIAttrib(StemVariable s, String key, String value) {
        if (StringUtils.isTrivial(value)) {
            return;
        }
        s.put(key, value);
    }

    public static final Long DETOKENIZE_PREPEND_VALUE = 1L;
    public static final Long DETOKENIZE_OMIT_DANGLING_DELIMITER_VALUE = 2L;

    /*
    Change a stem into a string with each value separated by a delimiter. Note that
    in lists, the order is preserved but in general stems there is no canonical order.
     */
    protected void doDetokeninze(Polyad polyad, State state) {
        if (polyad.getArgCount() != 2 && polyad.getArgCount() != 3) {
            throw new IllegalArgumentException("Error: " + DETOKENIZE + " requires two or three arguments");
        }
        Object leftArg = polyad.evalArg(0, state);
        Object rightArg = polyad.evalArg(1, state);
        boolean isPrepend = false;
        boolean omitDanglingDelimiter = false;
        if (polyad.getArgCount() == 3) {
            Object prepend = polyad.evalArg(2, state);
            if (!isLong(prepend)) {
                throw new IllegalArgumentException("Error: the third argument for " + DETOKENIZE + " must be a n integer. You supplied '" + prepend + "'");
            }
            int options = ((Long) prepend).intValue();
            switch (options) {
                case 0:
                    isPrepend = false;
                    omitDanglingDelimiter = false;
                    break;
                case 1:  //DETOKENIZE_PREPEND_VALUE
                    isPrepend = true;
                    omitDanglingDelimiter = false;
                    break;

                case 2:
                    isPrepend = false;
                    omitDanglingDelimiter = true;
                    break;

                case 3: // DETOKENIZE_PREPEND_VALUE + DETOKENIZE_OMIT_DANGLING_DELIMITER_VALUE
                    isPrepend = true;
                    omitDanglingDelimiter = true;
                    break;


            }
        }
        String result = "";

        if (isStem(leftArg)) {
            StemVariable leftStem = (StemVariable) leftArg;
            int lsize = leftStem.size();
            int currentCount = 0;

            if (isStem(rightArg)) {
                StemVariable rightStem = (StemVariable) rightArg;
                for (String key : leftStem.keySet()) {
                    if (rightStem.containsKey(key)) {
                        String delim = "";

                        if (isPrepend) {
                            if (omitDanglingDelimiter && currentCount == 0) {
                                result = leftStem.getString(key);
                            } else {
                                result = result + rightStem.getString(key) + leftStem.getString(key);
                            }
                        } else {
                            if (omitDanglingDelimiter && currentCount == lsize - 1) {

                                result = result + leftStem.getString(key);
                            } else {

                                result = result + leftStem.getString(key) + rightStem.getString(key);
                            }

                        }
                    }
                    currentCount++;
                }
            } else {
                // propagate the right arg as delimiter everywhere.

                for (String key : leftStem.keySet()) {
                    if (isPrepend) {
                        if (omitDanglingDelimiter && currentCount == 0) {

                            result = leftStem.getString(key);
                        } else {
                            result = result + rightArg + leftStem.getString(key);

                        }

                    } else {
                        if (omitDanglingDelimiter && currentCount == lsize - 1) {
                            result = result + leftStem.getString(key);
                        } else {
                            result = result + leftStem.getString(key) + rightArg;
                        }
                    }
                    currentCount++;
                }
            }

        } else {
            if (isStem(rightArg)) {
                throw new IllegalArgumentException("Error: a stem of delimiters cannot be applied to a scalar.");
            }
            if (omitDanglingDelimiter) {
                result = leftArg.toString();
            } else {
                if (isPrepend) {
                    result = rightArg.toString() + leftArg.toString();
                } else {
                    result = leftArg.toString() + rightArg.toString();
                }
            }

        }
        polyad.setResult(result);
        polyad.setResultType(Constant.STRING_TYPE);
        polyad.setEvaluated(true);

    }

    protected void doDecode(Polyad polyad, State state) {
        QDLCodec codec = new QDLCodec();
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (objects[0] instanceof String) {
                    r.result = codec.decode(objects[0].toString());
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, TRIM, state);

    }

    protected void doEncode(Polyad polyad, State state) {
        QDLCodec codec = new QDLCodec();
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (objects[0] instanceof String) {
                    r.result = codec.encode(objects[0].toString());
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, TRIM, state);
    }


    protected void doSubstring(Polyad polyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult result = new fpResult();
                if (!isString(objects[0])) {
                    throw new IllegalArgumentException("The first argument to " + SUBSTRING + " must be a string.");
                }
                String arg = objects[0].toString();
                if (!isLong(objects[1])) {
                    throw new IllegalArgumentException("The second argument to " + SUBSTRING + " must be an integer.");
                }
                int n = ((Long) objects[1]).intValue();
                int length = arg.length(); // default
                String padding = null;
                if (2 < objects.length) {
                    if (!isLong(objects[2])) {
                        throw new IllegalArgumentException("The third argument to " + SUBSTRING + " must be an integer.");
                    }
                    length = ((Long) objects[2]).intValue();
                }
                if (3 < objects.length) {
                    if (!isString(objects[3])) {
                        throw new IllegalArgumentException("The fourth argument to " + SUBSTRING + " must be a string.");
                    }
                    padding = objects[3].toString();
                }
                String r = arg.substring(n, Math.min(length, arg.length())); // the Java way
                if (arg.length() < length && padding != null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < 1 + (length - arg.length()) / padding.length(); i++) {
                        stringBuffer.append(padding);
                    }
                    r = r + stringBuffer.toString().substring(0, 1 + length - arg.length());
                }
                result.result = r;
                result.resultType = Constant.STRING_TYPE;

                return result;
            }
        };
        process2(polyad, pointer, SUBSTRING, state, true);
    }

    protected void doTrim(Polyad polyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (objects[0] instanceof String) {
                    r.result = objects[0].toString().trim();
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, TRIM, state);
    }

    protected void doIndexOf(Polyad polyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                Long pos = new Long(-1L);
                boolean caseSensitive = true;
                if (objects.length == 3) {
                    if (!(objects[2] instanceof Boolean)) {
                        throw new IllegalArgumentException("if the 3rd argument is given, it must be a boolean.");
                    }
                    caseSensitive = (Boolean) objects[2];
                }

                if (areAllStrings(objects[0], objects[1])) {
                    if (caseSensitive) {
                        pos = new Long(objects[0].toString().indexOf(objects[1].toString()));

                    } else {

                        pos = new Long(objects[0].toString().toLowerCase().indexOf(objects[1].toString().toLowerCase()));
                    }
                }
                r.result = pos;
                r.resultType = Constant.LONG_TYPE;

                return r;
            }
        };
        process2(polyad, pointer, INDEX_OF, state, true);
    }

    protected void doContains(Polyad polyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllStrings(objects[0], objects[1])) {
                    boolean caseSensitive = true;
                    if (objects.length == 3) {
                        if (!(objects[2] instanceof Boolean)) {
                            throw new IllegalArgumentException("if the 3rd argument is given, it must be a boolean.");
                        }
                        caseSensitive = (Boolean) objects[2];
                    }
                    if (caseSensitive) {
                        r.result = objects[0].toString().contains(objects[1].toString());
                    } else {
                        r.result = objects[0].toString().toLowerCase().contains(objects[1].toString().toLowerCase());
                    }
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = Boolean.FALSE;
                    r.resultType = Constant.BOOLEAN_TYPE;
                }

                return r;
            }
        };
        process2(polyad, pointer, CONTAINS, state, true);

    }


    protected void doSwapCase(Polyad polyad, State state, boolean isLower) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (objects[0] instanceof String) {
                    if (isLower) {
                        r.result = objects[0].toString().toLowerCase();
                    } else {
                        r.result = objects[0].toString().toUpperCase();
                    }
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, isLower ? TO_LOWER : TO_UPPER, state);
    }

    protected void doReplace(Polyad polyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllStrings(objects)) {
                    r.result = objects[0].toString().replace(objects[1].toString(), objects[2].toString());
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process3(polyad, pointer, REPLACE, state);
    }

    protected void doInsert(Polyad polyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllStrings(objects[0], objects[1]) && areAllLongs(objects[2])) {
                    int index = ((Long) objects[2]).intValue();
                    String src = (String) objects[0];
                    String snippet = (String) objects[1];
                    r.result = src.substring(0, index) + snippet + src.substring(index);
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process3(polyad, pointer, INSERT, state);
    }

    protected void doTokenize(Polyad polyad, State state) {
        // contract is tokenize(string, delimiter) returns stem of tokens
        // tokenize(stem, delimiter) returns a list whose elements are stems of tokens.
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllStrings(objects[0], objects[1])) {
                    StringTokenizer st = new StringTokenizer(objects[0].toString(), objects[1].toString());
                    StemVariable outStem = new StemVariable();
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        outStem.put(Integer.toString(i++), st.nextToken());
                    }
                    r.result = outStem;
                    r.resultType = Constant.STEM_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process2(polyad, pointer, INSERT, state);
    }
}
