package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLCodec;

import java.util.StringTokenizer;
import java.util.TreeSet;

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
    public static final int CONTAINS_TYPE = 1 + STRING_FUNCTION_BASE_VALUE;

    public static final String TO_LOWER = "to_lower";
    public static final int TO_LOWER_TYPE = 2 + STRING_FUNCTION_BASE_VALUE;

    public static final String TO_UPPER = "to_upper";
    public static final int TO_UPPER_TYPE = 3 + STRING_FUNCTION_BASE_VALUE;

    public static final String TRIM = "trim";
    public static final int TRIM_TYPE = 4 + STRING_FUNCTION_BASE_VALUE;

    public static final String INSERT = "insert";
    public static final int INSERT_TYPE = 5 + STRING_FUNCTION_BASE_VALUE;

    public static final String SUBSTRING = "substring";
    public static final int SUBSTRING_TYPE = 6 + STRING_FUNCTION_BASE_VALUE;

    public static final String REPLACE = "replace";
    public static final int REPLACE_TYPE = 7 + STRING_FUNCTION_BASE_VALUE;

    public static final String INDEX_OF = "index_of";
    public static final int INDEX_OF_TYPE = 8 + STRING_FUNCTION_BASE_VALUE;

    public static final String TOKENIZE = "tokenize";
    public static final int TOKENIZE_TYPE = 9 + STRING_FUNCTION_BASE_VALUE;

    public static final String ENCODE = "vencode";
        public static final int ENCODE_TYPE = 10 + STRING_FUNCTION_BASE_VALUE;

    public static final String DECODE = "vdecode";
        public static final int DECODE_TYPE = 11 + STRING_FUNCTION_BASE_VALUE;

    public static String FUNC_NAMES[] = new String[]{CONTAINS,TO_LOWER,TO_UPPER,TRIM,INSERT,
            SUBSTRING,REPLACE,INDEX_OF,TOKENIZE,
    ENCODE, DECODE};

   public TreeSet<String> listFunctions() {
          TreeSet<String> names = new TreeSet<>();
          for (String key : FUNC_NAMES) {
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
        if (name.equals(CONTAINS)) return CONTAINS_TYPE;
        if (name.equals(TO_LOWER)) return TO_LOWER_TYPE;
        if (name.equals(TO_UPPER)) return TO_UPPER_TYPE;
        if (name.equals(SUBSTRING)) return SUBSTRING_TYPE;
        if (name.equals(REPLACE)) return REPLACE_TYPE;
        if (name.equals(INSERT)) return INSERT_TYPE;
        if (name.equals(TRIM)) return TRIM_TYPE;
        if (name.equals(INDEX_OF)) return INDEX_OF_TYPE;
        if (name.equals(TOKENIZE)) return TOKENIZE_TYPE;
        if (name.equals(ENCODE)) return ENCODE_TYPE;
        if (name.equals(DECODE)) return DECODE_TYPE;
        return UNKNOWN_VALUE;
    }

    @Override
       public boolean evaluate(Polyad polyad, State state) {
           switch (polyad.getName()) {
               case CONTAINS:
                   doContains(polyad, state);
                   return true;
               case TRIM:
                   doTrim(polyad, state);
                   return true;
               case INDEX_OF:
                   doIndexOf(polyad, state);
                   return true;
               case TO_LOWER:
                   doSwapCase(polyad, state, true);
                   return true;
               case TO_UPPER:
                   doSwapCase(polyad, state, false);
                   return true;
               case REPLACE:
                   doReplace(polyad, state);
                   return true;
               case INSERT:
                   doInsert(polyad, state);
                   return true;
               case TOKENIZE:
                   doTokenize(polyad, state);
                   return true;
               case SUBSTRING:
                   doSubstring(polyad, state);
                   return true;
               case ENCODE:
                   doEncode(polyad, state);
                   return true;
               case DECODE:
                   doDecode(polyad, state);
                   return true;
           }
           return false;
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
                          r.resultType = polyad.getArgumments().get(0).getResultType();
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
                          r.resultType = polyad.getArgumments().get(0).getResultType();
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
                    throw new IllegalArgumentException("Error: The first argument to " + SUBSTRING + " must be a string.");
                }
                String arg = objects[0].toString();
                if (!isLong(objects[1])) {
                    throw new IllegalArgumentException("Error: The second argument to " + SUBSTRING + " must be an integer.");
                }
                int n = ((Long) objects[1]).intValue();
                int length = arg.length(); // default
                String padding = null;
                if (2 < objects.length) {
                    if (!isLong(objects[2])) {
                        throw new IllegalArgumentException("Error: The third argument to " + SUBSTRING + " must be an integer.");
                    }
                    length = ((Long) objects[2]).intValue();
                }
                if (3 < objects.length) {
                    if (!isString(objects[3])) {
                        throw new IllegalArgumentException("Error: The fourth argument to " + SUBSTRING + " must be a string.");
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
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
                        throw new IllegalArgumentException("Error: if the 3rd argument is given, it must be a boolean.");
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
                            throw new IllegalArgumentException("Error: if the 3rd argument is given, it must be a boolean.");
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
                }
                return r;
            }
        };
        process2(polyad, pointer, INSERT, state);
    }
}
