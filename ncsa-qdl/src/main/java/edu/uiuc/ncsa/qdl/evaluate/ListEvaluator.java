package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.*;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static edu.uiuc.ncsa.qdl.state.NamespaceAwareState.NS_DELIMITER;
import static edu.uiuc.ncsa.qdl.variables.StemUtility.axisWalker;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/30/21 at  5:07 PM
 */
public class ListEvaluator extends AbstractEvaluator {
    public static final String LIST_NAMESPACE = "list";
    public static final String LIST_FQ = LIST_NAMESPACE + NS_DELIMITER;
    public static final int LIST_BASE_VALUE = 10000;
    public static final String LIST_INSERT_AT = "insert_at";
    public static final String LIST_INSERT_AT2 = "list_insert_at";
    public static final int LIST_INSERT_AT_TYPE = 1 + LIST_BASE_VALUE;

    public static final String LIST_SUBSET = "subset";
    public static final String LIST_SUBSET2 = "list_subset";
    public static final int LIST_SUBSET_TYPE = 2 + LIST_BASE_VALUE;

    public static final String LIST_COPY = "list_copy";
    public static final String LIST_COPY2 = "copy";
    public static final int LIST_COPY_TYPE = 3 + LIST_BASE_VALUE;

    public static final String LIST_STARTS_WITH = "starts_with";
    public static final String LIST_STARTS_WITH2 = "list_starts_with";
    public static final int LIST_STARTS_WITH_TYPE = 6 + LIST_BASE_VALUE;

    public static final String LIST_REVERSE = "reverse";
    public static final String LIST_REVERSE2 = "list_reverse";
    public static final int LIST_REVERSE_TYPE = 7 + LIST_BASE_VALUE;

    public static final String LIST_SORT = "sort";
    public static final int LIST_SORT_TYPE = 8 + LIST_BASE_VALUE;


    @Override
    public String[] getFunctionNames() {
        if (fNames == null) {
            fNames = new String[]{
                    LIST_SORT,
                    LIST_INSERT_AT,
                    LIST_SUBSET,
                    LIST_COPY,
                    LIST_REVERSE,
                    LIST_STARTS_WITH
            };
        }
        return fNames;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        try {
            return evaluate2(polyad, state);
        } catch (QDLException q) {
            throw q;
        } catch (Throwable t) {
            QDLExceptionWithTrace qq = new QDLExceptionWithTrace(t, polyad);
            throw qq;
        }
    }

    public boolean evaluate2(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case LIST_SORT:
                doListSort(polyad, state);
                return true;
            case LIST_COPY:
            case LIST_COPY2:
                doListCopyOrInsert(polyad, state, false);
                return true;
            case LIST_INSERT_AT:
            case LIST_INSERT_AT2:
                doListCopyOrInsert(polyad, state, true);
                return true;
            case LIST_SUBSET:
            case LIST_SUBSET2:
                return doListSubset(polyad, state);
            case LIST_REVERSE:
            case LIST_REVERSE2:
                doListReverse(polyad, state);
                return true;
            case LIST_STARTS_WITH:
            case LIST_STARTS_WITH2:
                doListStartsWith(polyad, state);
                return true;
        }
        return false;
    }


    @Override
    public int getType(String name) {
        switch (name) {
            case LIST_SORT:
                return LIST_SORT_TYPE;
            case LIST_COPY:
            case LIST_COPY2:
                return LIST_COPY_TYPE;
            case LIST_REVERSE:
            case LIST_REVERSE2:
                return LIST_REVERSE_TYPE;
            case LIST_STARTS_WITH:
            case LIST_STARTS_WITH2:
                return LIST_STARTS_WITH_TYPE;
            case LIST_INSERT_AT:
            case LIST_INSERT_AT2:
                return LIST_INSERT_AT_TYPE;
            case LIST_SUBSET:
            case LIST_SUBSET2:
                return LIST_SUBSET_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public String getNamespace() {
        return LIST_NAMESPACE;
    }

    /**
     * Always returns a sorted list.
     *
     * @param polyad
     * @param state
     */
    protected void doListSort(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1, 2});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(LIST_SORT + " requires at least 1 argument", polyad);
        }

        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(LIST_SORT + " requires at most 2 arguments", polyad.getArgAt(2));
        }
        // Contract is to take everything in a list sort it and return it.
        Object arg0 = polyad.evalArg(0, state);
        ArrayList list = null;
        switch (Constant.getType(arg0)) {
            case Constant.SET_TYPE:
                list = new ArrayList();
                list.addAll((QDLSet) arg0);
                break;
            case Constant.STEM_TYPE:
                StemVariable inStem = (StemVariable) arg0;
                if (inStem.isList()) {
                    list = inStem.getQDLList().values(); // fast
                } else {
                    list = new ArrayList();
                    list.addAll(inStem.values());
                }
                break;
            default:
                StemVariable stemVariable = new StemVariable();
                stemVariable.put(0, arg0);
                polyad.setEvaluated(true);
                polyad.setResult(stemVariable);
                polyad.setResultType(Constant.getType(arg0));
                return;
        }
        boolean sortUp = true;
        if (polyad.getArgCount() == 2) {
            Object arg1 = polyad.evalArg(1, state);
            if (arg1 instanceof Boolean) {
                sortUp = (Boolean) arg1;
            } else {
                throw new BadArgException(LIST_SORT + " requires a boolean as it second argument if present", polyad.getArgAt(1));
            }
        }
        try {
            doSorting(list, sortUp);
            StemVariable output = new StemVariable((long) list.size(), list.toArray());
            polyad.setEvaluated(true);
            polyad.setResult(output);
            polyad.setResultType(Constant.STEM_TYPE);
            return;
        } catch (ClassCastException classCastException) {

        }
        // So there is mixed information. we'll have to do this the hard way....
        ArrayList numbers = new ArrayList();
        ArrayList strings = new ArrayList();
        ArrayList others = new ArrayList();
        ArrayList booleans = new ArrayList();
        ArrayList nulls = new ArrayList();
        for (Object element : list) {
            switch (Constant.getType(element)) {
                case Constant.STRING_TYPE:
                    strings.add(element);
                    break;
                case Constant.DECIMAL_TYPE:
                    numbers.add(element);
                    break;
                case Constant.LONG_TYPE:
                    // BigDecimals and longs are not comparable. Have to fudge it
                    numbers.add(BigDecimal.valueOf((long) element));
                    break;
                case Constant.BOOLEAN_TYPE:
                    booleans.add(element);
                    break;
                case Constant.NULL_TYPE:
                    nulls.add(element);
                    break;
                default:
                    others.add(element);
                    break;
            }
        }
        doSorting(nulls, sortUp);
        doSorting(numbers, sortUp);
        doSorting(strings, sortUp);
        doSorting(booleans, sortUp);
        list.clear();
        if (sortUp) {
            list.addAll(nulls);
            list.addAll(booleans);
            list.addAll(strings);
            list.addAll(numbers);
            list.addAll(others);
        } else {
            list.addAll(others);
            list.addAll(numbers);
            list.addAll(strings);
            list.addAll(booleans);
            list.addAll(nulls);

        }
        StemVariable output = new StemVariable((long) list.size(), list.toArray());
        polyad.setEvaluated(true);
        polyad.setResult(output);
        polyad.setResultType(Constant.STEM_TYPE);

    }

    private void doSorting(ArrayList list, boolean sortUp) {
        if (sortUp) {
            Collections.sort(list);
        } else {
            Collections.sort(list, Collections.reverseOrder());
        }
    }

    protected void doListCopyOrInsert(Polyad polyad, State state, boolean doInsert) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{5});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 5) {
            throw new MissingArgException((doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires 5 arguments", polyad);
        }

        if (5 < polyad.getArgCount()) {
            throw new ExtraArgException((doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires 5 arguments", polyad.getArgAt(5));
        }

        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));

        if (!isStem(arg1)) {
            throw new BadArgException((doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires a stem as its first argument", polyad.getArgAt(0));
        }
        StemVariable souorceStem = (StemVariable) arg1;

        Object arg2 = polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1));
        if (!isLong(arg2)) {
            throw new BadArgException((doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires an integer as its second argument", polyad.getArgAt(1));
        }
        Long startIndex = (Long) arg2;

        Object arg3 = polyad.evalArg(2, state);
        checkNull(arg3, polyad.getArgAt(2));

        if (!isLong(arg3)) {
            throw new BadArgException((doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires an integer as its third argument", polyad.getArgAt(2));
        }
        Long length = (Long) arg3;

        // need to handle case that the target does not exist.
        StemVariable targetStem;
        if (polyad.getArgAt(3) instanceof VariableNode) {
            targetStem = getOrCreateStem(polyad.getArgAt(3),
                    state, (doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires a stem as its fourth argument"
            );
        } else {
            Object obj = polyad.evalArg(3, state);
            checkNull(obj, polyad.getArgAt(3));

            if (!isStem(obj)) {
                throw new BadArgException((doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires an integer as its fourth argument", polyad.getArgAt(3));
            }
            targetStem = (StemVariable) obj;
        }

        Object arg5 = polyad.evalArg(4, state);
        checkNull(arg5, polyad.getArgAt(4));

        if (!isLong(arg5)) {
            throw new BadArgException((doInsert ? LIST_INSERT_AT : LIST_COPY) + " requires an integer as its fifth argument", polyad.getArgAt(4));
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

    /*
          subset(3*[;15], {'foo':3,'bar':5,'baz':7})
          subset(3*[;15], 2*[;5]+1)
          a. := n(3,4,n(12))
          remap(a., [[0,1],[1,1],[2,3]])
       [1,5,11]
          remap(a., {'foo':[0,1],'bar':[1,1], 'baz':[2,3]})
      {bar:5, foo:1, baz:11}
       */
    protected boolean doListSubset(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{2, 3});
            polyad.setEvaluated(true);
            return true;
        }
        if (polyad.getArgCount() < 2) {
            throw new MissingArgException(LIST_SUBSET + " requires at least 2 arguments", polyad);
        }

        if (3 < polyad.getArgCount()) {
            throw new ExtraArgException(LIST_SUBSET + " requires at most 3 arguments", polyad.getArgAt(3));
        }

        // Another case if subset(@f, arg) will pick elements of arg and return them
        // based on the first boolean-valued function
        if (isFunctionRef(polyad.getArgAt(0))) {
            return doPickSubset(polyad, state);
        }

        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));
        if (isScalar(arg1)) {
            QDLList qdlList = new QDLList();
            qdlList.add(arg1);
            StemVariable stemVariable = new StemVariable();
            stemVariable.setQDLList(qdlList);
            polyad.setResult(stemVariable);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return true;
        }
        Object arg2 = polyad.evalArg(1, state);
        checkNull(arg2, polyad.getArgAt(1));
        if (!isLong(arg2)) {
            throw new BadArgException(LIST_SUBSET + " requires an integer as its second argument", polyad.getArgAt(1));
        }
        Object arg3 = null;
        if (polyad.getArgCount() == 3) {
            arg3 = polyad.evalArg(2, state);
            checkNull(arg3, polyad.getArgAt(2));

            if (!isLong(arg3)) {
                throw new BadArgException(LIST_SUBSET + " requires an integer as its third argument", polyad.getArgAt(2));
            }
        }
        StemVariable stem = null;
        QDLSet set = null;

        long startIndex = 0L;
        Long count = -1L; // return rest of list

        switch (Constant.getType(arg1)) {
            case Constant.STEM_TYPE:
                stem = (StemVariable) arg1;
                if (!stem.isList()) {
                    throw new BadArgException(LIST_SUBSET + " requires a list", polyad.getArgAt(0));
                }
                startIndex = (Long) arg2;


              /*  if (polyad.getArgCount() == 2) {
                    count = (long) stem.size() - startIndex;
                } else {*/
                if(polyad.getArgCount() == 3){
                    // must be 3
                    count = (Long) arg3;
                    if (count < 0) {
                        throw new BadArgException(LIST_SUBSET + " requires that the number of elements be positive", polyad.getArgAt(2));
                    }
/*
                    if(stem.size() < startIndex + count){
                        count = stem.size() - startIndex; // run to end of list
                    }
*/
                }

                break;
            case Constant.SET_TYPE:
                if (polyad.getArgCount() == 3) {
                    throw new ExtraArgException(LIST_SUBSET + " takes a single argument for a set", polyad.getArgAt(1));
                }
                set = (QDLSet) arg1;
                count = (Long) arg2;
                if (count < 0) {
                    count = -count; // no wrap around possible for sets
                }
                if(set.size() < count){
                    count = (long)set.size();
                }
                break;
            default:
                QDLList qdlList = new QDLList();
                qdlList.add(arg1);
                StemVariable stemVariable = new StemVariable();
                stemVariable.setQDLList(qdlList);
                polyad.setResult(stemVariable);
                polyad.setResultType(Constant.STEM_TYPE);
                polyad.setEvaluated(true);
                return true;
        }


        if (set != null) {
            QDLSet outSet = new QDLSet();
            Iterator iterator = set.iterator();

            for (long i = 0; i < count; i++) {
                outSet.add(iterator.next());
            }

            polyad.setResult(outSet);
            polyad.setResultType(Constant.SET_TYPE);
            polyad.setEvaluated(true);
            return true;
        }

        StemVariable outStem;
        if (count == 0) {
            outStem = new StemVariable();
        } else {
            outStem = stem.listSubset(startIndex, count);
        }
        polyad.setResult(outStem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return true;

    }

    /*
      subset((x,y)->2<x, [;10])
{3:3, 4:4, 5:5, 6:6, 7:7, 8:8, 9:9}
  subset((x)->x<0, [-2;3])
[-2,-1]
    subset((x)->x<0, [-2;3])
[-2,-1]
  my_f(x,y)->2<x
  subset(@my_f, [;10])
{3:3, 4:4, 5:5, 6:6, 7:7, 8:8, 9:9}
  my_f(x)->x<0
  subset(@my_f, [-2;5])
    subset((x,y)->mod(x,2)==0, [-4;5])
{0:-4, 2:-2, 4:0, 6:2, 8:4}
     */
    /**
     * Pick elements based on a function that is supplied.
     *
     * @param polyad
     * @param state
     * @return
     */
    protected boolean doPickSubset(Polyad polyad, State state) {
        FunctionReferenceNode frn = getFunctionReferenceNode(state, polyad.getArgAt(0), true);
        Object arg1 = polyad.evalArg(1, state);
        ExpressionImpl f = null;
        int argCount = 1;
        try {
            List<FunctionRecord> functionRecordList = state.getFTStack().getByAllName(frn.getFunctionName());
            if(functionRecordList.isEmpty()){
                throw new NFWException("no functions found for pick function at all. Did state management change?");
            }
            for(FunctionRecord fr: functionRecordList){
                if(2 < fr.getArgCount()){
                    continue;
                }
                argCount = Math.max(argCount, fr.getArgCount());
            }
            f = getOperator(state, frn, argCount); // single argument
        } catch (UndefinedFunctionException ufx) {
            ufx.setStatement(polyad.getArgAt(0));
            throw ufx;
        }
        // 3 cases
        if (isSet(arg1)) {
            if(argCount != 1){
                throw new BadArgException(LIST_SUBSET + " pick function for sets can only have a single argument", polyad.getArgAt(0));
            }
            QDLSet result = new QDLSet();
            QDLSet argSet = (QDLSet) arg1;
            ArrayList<Object> rawArgs = new ArrayList<>();
            for (Object element : argSet) {
                rawArgs.clear();
                rawArgs.add(element);
                f.setArguments(toConstants(rawArgs));
                Object test = f.evaluate(state);
                if (isBoolean(test)) {
                    if ((Boolean) test) {
                        result.add(element);
                    }
                }
            }
            polyad.setResult(result);
            polyad.setResultType(Constant.SET_TYPE);
            polyad.setEvaluated(true);
            return true;
        }
        // For stems it's quite similar, but not enough to have a single piece of code.
        if (isStem(arg1)) {
            StemVariable outStem = new StemVariable();
            StemVariable stemArg = (StemVariable) arg1;
            ArrayList<Object> rawArgs = new ArrayList<>();
            for (Object key : stemArg.keySet()) {
                rawArgs.clear();
                Object value = stemArg.get(key);
                if(argCount == 2){
                    rawArgs.add(key);
/*
                    if(outStem.isLongIndex(key)){
                         rawArgs.add(Long.parseLong(key));
                    }else {
                        rawArgs.add(key);
                    }
*/
                }
                rawArgs.add(stemArg.get(key));
                f.setArguments(toConstants(rawArgs));
                Object test = f.evaluate(state);
                if (isBoolean(test)) {
                    if ((Boolean) test) {
                        outStem.putLongOrString(key, value);
                    }
                }
            }
            polyad.setResult(outStem);
            polyad.setResultType(Constant.STEM_TYPE);
            polyad.setEvaluated(true);
            return true;
        }
        // final case is that it is a scalar.
        ArrayList<Object> rawArgs = new ArrayList<>();
        rawArgs.add(arg1);
        f.setArguments(toConstants(rawArgs));
        Object test = f.evaluate(state);
        Object result = null;
        if (isBoolean(test)) {
            if ((Boolean) test) {
                result = arg1;
            } else {
                result = QDLNull.getInstance();
            }
        }
        polyad.setResult(result);
        polyad.setResultType(Constant.getType(result));
        polyad.setEvaluated(true);
        return true;
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
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{2});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 2) {
            throw new MissingArgException(LIST_STARTS_WITH + " requires 2 arguments", polyad);
        }
        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(LIST_STARTS_WITH + " requires 2 arguments", polyad.getArgAt(2));
        }

        Object leftArg = polyad.evalArg(0, state);
        checkNull(leftArg, polyad.getArgAt(0));

        StemVariable leftStem = null;
        if (isString(leftArg)) {
            leftStem = new StemVariable();
            leftStem.put(0L, leftArg);
        }
        if (leftStem == null) {
            if (isStem(leftArg)) {
                leftStem = (StemVariable) leftArg;
            } else {
                throw new BadArgException(LIST_STARTS_WITH + " requires a stem for the left argument.", polyad.getArgAt(0));
            }
        }

        Object rightArg = polyad.evalArg(1, state);
        checkNull(rightArg, polyad.getArgAt(1));

        StemVariable rightStem = null;
        if (isString(rightArg)) {
            rightStem = new StemVariable();
            rightStem.put(0L, rightArg);
        }
        if (rightStem == null) {
            if (isStem(rightArg)) {
                rightStem = (StemVariable) rightArg;

            } else {
                throw new BadArgException(LIST_STARTS_WITH + " requires a stem for the right argument.", polyad.getArgAt(1));
            }
        }
        StemVariable output = new StemVariable();
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

    protected void doListReverse(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1, 2});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(LIST_REVERSE + " requires at least 1 argument", polyad);
        }

        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(LIST_REVERSE + " requires at most 2 arguments", polyad.getArgAt(2));
        }

        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));

        if (!isStem(arg1)) {
            throw new BadArgException(LIST_REVERSE + " requires a stem as its argument.", polyad.getArgAt(0));
        }
        int axis = 0;
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            checkNull(arg2, polyad.getArgAt(1));

            if (!isLong(arg2)) {
                throw new BadArgException(LIST_REVERSE + " an integer as its axis.", polyad.getArgAt(1));
            }
            axis = ((Long) arg2).intValue();
        }

        StemVariable input = (StemVariable) arg1;

        DoReverse reverse = this.new DoReverse();

        Object result = axisWalker(input, axis, reverse);
        polyad.setResult(result);
        polyad.setResultType(Constant.getType(result));
        polyad.setEvaluated(true);

    }

    protected class DoReverse implements StemUtility.StemAxisWalkerAction1 {
        @Override
        public Object action(StemVariable inStem) {
            StemVariable output = new StemVariable();
            Iterator iterator = inStem.getQDLList().descendingIterator(true);
            while (iterator.hasNext()) {
                output.listAppend(iterator.next());
            }
            return output;
        }
    }
}
