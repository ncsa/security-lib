package edu.uiuc.ncsa.security.util.qdl.expressions;

import edu.uiuc.ncsa.security.util.qdl.variables.Constant;

/**
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
    public static String CONTAINS = "contains";
    public static final int CONTAINS_TYPE = 1 + STRING_FUNCTION_BASE_VALUE;

    public static String TO_LOWER = "to_lower";
    public static final int TO_LOWER_TYPE = 2 + STRING_FUNCTION_BASE_VALUE;

    public static String TO_UPPER = "to_upper";
    public static final int TO_UPPER_TYPE = 3 + STRING_FUNCTION_BASE_VALUE;

    public static String TRIM = "trim";
    public static final int TRIM_TYPE = 4 + STRING_FUNCTION_BASE_VALUE;

    public static String INSERT = "insert";
    public static final int INSERT_TYPE = 5 + STRING_FUNCTION_BASE_VALUE;

    public static String SUBSTRING = "substring";
    public static final int SUBSTRING_TYPE = 6 + STRING_FUNCTION_BASE_VALUE;

    public static String REPLACE = "replace";
    public static final int REPLACE_TYPE = 7 + STRING_FUNCTION_BASE_VALUE;

    public static String INDEX_OF = "index_of";
    public static final int INDEX_OF_TYPE = 8 + STRING_FUNCTION_BASE_VALUE;


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
        return UNKNOWN_VALUE;
    }

    @Override
    public void evaluate(Polyad polyad) {
        switch (polyad.getOperatorType()) {
            case CONTAINS_TYPE:
                doContains(polyad);
                return;
            case TRIM_TYPE:
                doTrim(polyad);
                return;
            case INDEX_OF_TYPE:
                doIndexOf(polyad);
                return;
            case TO_LOWER_TYPE:
                doSwapCase(polyad, true);
                return;
            case TO_UPPER_TYPE:
                doSwapCase(polyad, false);
                return;
            case REPLACE_TYPE:
                doReplace(polyad);
                return;
            case INSERT_TYPE:
                doInsert(polyad);
                return;
        }
        throw new IllegalArgumentException("Unknown function type");
    }

    protected void doTrim(Polyad polyad) {
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
        process1(polyad, pointer, TRIM);
    }

    protected void doIndexOf(Polyad polyad) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                Long pos = new Long(-1L);
                if (areAllStrings(objects)) {
                    pos = new Long(objects[0].toString().indexOf(objects[1].toString()));
                }
                r.result = pos;
                r.resultType = Constant.LONG_TYPE;

                return r;
            }
        };
        process2(polyad, pointer, INDEX_OF);
    }

    protected void doContains(Polyad polyad) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllStrings(objects)) {
                    r.result = objects[0].toString().contains(objects[1].toString());
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = Boolean.FALSE;
                    r.resultType = Constant.BOOLEAN_TYPE;
                }

                return r;
            }
        };
        process2(polyad, pointer, CONTAINS);

    }


    protected void doSwapCase(Polyad polyad, boolean isLower) {
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
        process1(polyad, pointer, isLower ? TO_LOWER : TO_UPPER);
    }
     protected void doReplace(Polyad polyad){
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if(areAllStrings(objects)){
                    r.result = objects[0].toString().replace(objects[1].toString(), objects[2].toString());
                    r.resultType = Constant.STRING_TYPE;
                }else{
                    r.result = objects[0];
                    r.resultType = polyad.getArgumments().get(0).getResultType();
                }
                return r;
            }
        };
        process3(polyad, pointer, REPLACE);
     }
     protected void doInsert(Polyad polyad){
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if(areAllStrings(objects[0], objects[1]) && areAllLongs(objects[2])){
                    int index = ((Long)objects[2]).intValue();
                    String src = (String)objects[0];
                    String snippet = (String) objects[1];
                    r.result = src.substring(0,index) + snippet + src.substring(index);
                    r.resultType = Constant.STRING_TYPE;
                }else{
                    r.result = objects[0];
                    r.resultType = polyad.getArgumments().get(0).getResultType();
                }
                return r;
            }
        };
        process3(polyad, pointer, INSERT);
     }
}
