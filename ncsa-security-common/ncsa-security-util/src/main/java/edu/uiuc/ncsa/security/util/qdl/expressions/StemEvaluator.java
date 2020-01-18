package edu.uiuc.ncsa.security.util.qdl.expressions;

import edu.uiuc.ncsa.security.util.qdl.util.StemVariable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:19 AM
 */
public class StemEvaluator extends IOEvaluator {
    public static final int STEM_FUNCTION_BASE_VALUE = 2000;
    public static String SIZE = "size";
    public static final int SIZE_TYPE = 1 + STEM_FUNCTION_BASE_VALUE;

    public static String KEYS = "keys";
    public static final int KEYS_TYPE = 2 + STEM_FUNCTION_BASE_VALUE;

    public static String TO_STEM = "to_stem";
    public static final int TO_STEM_TYPE = 3 + STEM_FUNCTION_BASE_VALUE;

    public static String MAKE_INDEX = "make_index";
    public static final int MAKE_INDEX_TYPE = 4 + STEM_FUNCTION_BASE_VALUE;

    public static String REMOVE = "remove";
    public static final int REMOVE_TYPE = 5 + STEM_FUNCTION_BASE_VALUE;


    @Override
    public int getType(String name) {
        int out = super.getType(name);
        if (out != UNKNOWN_VALUE) return out;
        if (name.equals(SIZE)) return SIZE_TYPE;
        if (name.equals(KEYS)) return KEYS_TYPE;
        if (name.equals(TO_STEM)) return TO_STEM_TYPE;
        if (name.equals(MAKE_INDEX)) return MAKE_INDEX_TYPE;
        if (name.equals(REMOVE)) return REMOVE_TYPE;
        return UNKNOWN_VALUE;
    }


    @Override
    public void evaluate(Polyad polyad) {
        switch (polyad.getOperatorType()) {
            case SIZE_TYPE:
                doSize(polyad);
                return;
            case KEYS_TYPE:
                doKeys(polyad);
                return;
            case TO_STEM_TYPE:
                doToStem(polyad);
                return;
            case MAKE_INDEX_TYPE:
                doMakeIndex(polyad);
                return;
            case REMOVE_TYPE:
                doRemove(polyad);
                return;
        }
        super.evaluate(polyad);
    }

    protected void doSize(Polyad polyad) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + SIZE + " function requires 1 argument");
        }
        polyad.getArgumments().get(0).evaluate();
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

    protected void doKeys(Polyad polyad) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + KEYS + " function requires 1 argument");
        }
        polyad.getArgumments().get(0).evaluate();
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

    protected void doMakeIndex(Polyad polyad) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + MAKE_INDEX + " function requires 1 argument");
        }
        polyad.getArgumments().get(0).evaluate();
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
    protected void doToStem(Polyad polyad) {
        StemVariable out = new StemVariable();
        Long index = 0L;
        for (ExpressionNode arg : polyad.getArgumments()) {
            Object r = arg.evaluate();
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

    protected void doRemove(Polyad polyad) {
        if (polyad.getArgumments().size() != 1) {
            throw new IllegalArgumentException("Error: the " + REMOVE + " function requires 1 argument");
        }
        VariableNode variableNode = (VariableNode) polyad.getArgumments().get(0);
        variableNode.evaluate();
        variableNode.getSymbolTable().remove(variableNode.getVariableReference());
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }
}
