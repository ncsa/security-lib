package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.statements.TokenPosition;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.math.BigDecimal;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/21 at  8:38 AM
 */
public class OpenSliceNode extends ExpressionImpl {
    public OpenSliceNode(TokenPosition tokenPosition) {
        super(tokenPosition);
    }

    @Override
    public Object evaluate(State state) {
        Object arg0 = evalArg(0, state);
        if (!longOrBD(arg0)) {
            throw new IllegalArgumentException("error: slice requires a number for the first argument ");
        }
        Object arg1 = evalArg(1, state);
        if (!longOrBD(arg1)) {
            throw new IllegalArgumentException("error: slice requires a number for the second argument ");
        }
        Object arg2;
        if (getArgCount() == 3) {
            arg2 = evalArg(2, state);
        } else {
            arg2 = 1L;
        }
        StemVariable out;
        if (areAnyBD(arg0, arg1, arg2)) {
            out = doDecimalCase(arg0, arg1, arg2);
        } else {
            out = doLongCase(arg0, arg1, arg2);
        }

        setResult(out);
        setResultType(Constant.STEM_TYPE);
        setEvaluated(true);
        return result;
    }

    protected boolean areAnyBD(Object... args) {
        boolean areAnyDB = false;
        for (int i = 0; i < args.length; i++) {
            areAnyDB = areAnyDB || (args[i] instanceof BigDecimal);
        }
        return areAnyDB;
    }

    protected boolean longOrBD(Object args) {
        return (args instanceof BigDecimal) || (args instanceof Long);
    }

    public StemVariable doDecimalCase(Object... args) {
        BigDecimal start = null;
        if (args[0] instanceof BigDecimal) {
            start = (BigDecimal) args[0];
        } else {
            start = new BigDecimal(args[0].toString());
        }
        BigDecimal stop = null;
        if (args[1] instanceof BigDecimal) {
            stop = (BigDecimal) args[1];
        } else {
            stop = new BigDecimal(args[1].toString());
        }
        BigDecimal step = null;
        if (args[2] instanceof BigDecimal) {
            step = (BigDecimal) args[2];
        } else {
            step = new BigDecimal(args[2].toString());
        }
        long i = 1L;
        // Now check that this isn't goofy.
        // step < 0 and start < stop means an infinite loop would happen

        if((step.compareTo(BigDecimal.ZERO) == 0) || ((step.compareTo(BigDecimal.ZERO) <0) && (start.compareTo(stop) < 0 )) ||
                ((0<step.compareTo(BigDecimal.ZERO)) && (stop.compareTo(start) < 0 ))){
              throw new IllegalArgumentException("cannot do slice from " + start + " to " + stop + " by increment of " + step);
           }

        StemVariable out = new StemVariable();
        out.put(0L, start);
        BigDecimal result = start.add(step, OpEvaluator.getMathContext());
        while (result.compareTo(stop) < 0) {
            out.put(i++, result);
            result = result.add(step, OpEvaluator.getMathContext());
        }
        if (stop.compareTo(start) < 0) {
                  // decrement case
            while (result.compareTo(stop) > 0) {
                out.put(i++, result);
                result = result.add(step, OpEvaluator.getMathContext());
            }
              } else {
                  //increment case
            while (result.compareTo(stop) < 0) {
                out.put(i++, result);
                result = result.add(step, OpEvaluator.getMathContext());
            }
              }
        return out;
    }

    protected StemVariable doLongCase(Object... args) {
        long start = (Long) args[0];
        long stop = (Long) args[1];
        long step = (Long) args[2];
        // step == 0 ∨ (step < 0 ∧ start < stop) ∨ (0 < step ∧ stop < start)
        // means an infinite loop would happen
        if((step == 0)||((step < 0) && (start < stop)) || ((0 < step ) && (stop < start))){
           throw new IllegalArgumentException("cannot do slice from " + start + " to " + stop + " by increment of " + step);
        }

        Long result = start;
        Long i = 0L;

        StemVariable out = new StemVariable();
        if (stop < start) {
            // decrement case, so step <0
            while (result > stop) {
                out.put(i++, result);
                result = result + step;
            }
        } else {
            //increment case
            while (result < stop) {
                out.put(i++, result);
                result = result + step;
            }
        }

        return out;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        OpenSliceNode sliceNode = new OpenSliceNode(getTokenPosition());
        sliceNode.setArguments(getArguments());
        return sliceNode;
    }
}
