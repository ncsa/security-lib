package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.QDLStatementExecutionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemEntry;
import edu.uiuc.ncsa.qdl.variables.StemUtility;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.util.Iterator;

import static edu.uiuc.ncsa.qdl.state.NamespaceAwareState.NS_DELIMITER;
import static edu.uiuc.ncsa.qdl.variables.StemUtility.axisWalker;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/30/21 at  5:07 PM
 */
public class ListEvaluator extends AbstractFunctionEvaluator{
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

    @Override
    public String[] getFunctionNames() {
        if(fNames == null){
            fNames = new String[]{
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
        try{
            return evaluate2(polyad, state);
        }catch(QDLException q){
              throw q;
        }catch(Throwable t){
            QDLStatementExecutionException qq = new QDLStatementExecutionException(t, polyad);
            throw qq;
        }
    }
    public boolean evaluate2(Polyad polyad, State state) {
        switch (polyad.getName()){
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
                doListSubset(polyad, state);
                return true;
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
        switch (name){
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
    protected void doListCopyOrInsert(Polyad polyad, State state, boolean doInsert) {
           if (5 != polyad.getArgCount()) {
               throw new IllegalArgumentException("the " + (doInsert?LIST_INSERT_AT:LIST_COPY) + " function requires 5 arguments");
           }
           Object arg1 = polyad.evalArg(0, state);
           checkNull(arg1, polyad.getArgAt(0));

           if (!isStem(arg1)) {
               throw new IllegalArgumentException((doInsert?LIST_INSERT_AT:LIST_COPY) + " requires a stem as its first argument");
           }
           StemVariable souorceStem = (StemVariable) arg1;

           Object arg2 = polyad.evalArg(1, state);
           checkNull(arg2, polyad.getArgAt(1));
           if (!isLong(arg2)) {
               throw new IllegalArgumentException((doInsert?LIST_INSERT_AT:LIST_COPY) + " requires an integer as its second argument");
           }
           Long startIndex = (Long) arg2;

           Object arg3 = polyad.evalArg(2, state);
           checkNull(arg3, polyad.getArgAt(2));

           if (!isLong(arg3)) {
               throw new IllegalArgumentException((doInsert?LIST_INSERT_AT:LIST_COPY) + " requires an integer as its third argument");
           }
           Long length = (Long) arg3;

           // need to handle case that the target does not exist.
           StemVariable targetStem;
           if (polyad.getArgAt(3) instanceof VariableNode) {
               targetStem = getOrCreateStem(polyad.getArgAt(3),
                       state, (doInsert?LIST_INSERT_AT:LIST_COPY) + " requires a stem as its fourth argument"
               );
           } else {
               Object obj = polyad.evalArg(3, state);
               checkNull(obj, polyad.getArgAt(3));

               if (!isStem(obj)) {
                   throw new IllegalArgumentException((doInsert?LIST_INSERT_AT:LIST_COPY) + " requires an integer as its fifth argument");

               }
               targetStem = (StemVariable) obj;
           }

           Object arg5 = polyad.evalArg(4, state);
           checkNull(arg5, polyad.getArgAt(4));

           if (!isLong(arg5)) {
               throw new IllegalArgumentException((doInsert?LIST_INSERT_AT:LIST_COPY) + " requires an integer as its fifth argument");
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
          subset(a., [[0,1],[1,1],[2,3]])
       [1,5,11]
          subset(a., {'foo':[0,1],'bar':[1,1], 'baz':[2,3]})
      {bar:5, foo:1, baz:11}
       */
      protected void doListSubset(Polyad polyad, State state) {
          if (polyad.getArgCount() < 2 || 3 < polyad.getArgCount()) {
              throw new IllegalArgumentException("the " + LIST_SUBSET + " function requires  two or three arguments");
          }
          Object arg1 = polyad.evalArg(0, state);
          checkNull(arg1, polyad.getArgAt(0));
          if (!isStem(arg1)) {
              throw new IllegalArgumentException(LIST_SUBSET + " requires stem as its first argument");
          }
          StemVariable stem = (StemVariable) arg1;
          Object arg2 = polyad.evalArg(1, state);
          checkNull(arg2, polyad.getArgAt(1));
          if (!isLong(arg2)) {
              throw new IllegalArgumentException(LIST_SUBSET + " requires an integer as its second argument");
          }
          Long startIndex = (Long) arg2;
          Long endIndex = (long) stem.getStemList().size();
          if (startIndex < 0) {
              startIndex = startIndex + endIndex;
          }
          if (polyad.getArgCount() == 3) {
              Object arg3 = polyad.evalArg(2, state);
              checkNull(arg3, polyad.getArgAt(2));

              if (!isLong(arg3)) {
                  throw new IllegalArgumentException(LIST_SUBSET + " requires an integer as its third argument");
              }
              endIndex = (Long) arg3;
          }

          StemVariable outStem = stem.listSubset(startIndex, endIndex);
          polyad.setResult(outStem);
          polyad.setResultType(Constant.STEM_TYPE);
          polyad.setEvaluated(true);
          return;

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
             throw new IllegalArgumentException(LIST_STARTS_WITH + " requires 2 arguments.");
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
                 throw new IllegalArgumentException(LIST_STARTS_WITH + " requires a stem for the left argument.");
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
                 throw new IllegalArgumentException( LIST_STARTS_WITH + " requires a stem for the right argument.");
             }
         }
        /*
          g. := ['wlcg.groups', 'wlcg.groups:/cms/uscms', 'wlcg.groups:/cms/ALARM','wlcg.groups:/cms/users']
   w := 'wlcg.groups'
   s. := 'openid' ~ 'email' ~ 'profile' ~ g.
         */
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
        Object arg1 = polyad.evalArg(0, state);
        checkNull(arg1, polyad.getArgAt(0));

        if (!isStem(arg1)) {
            throw new IllegalArgumentException( LIST_REVERSE + " requires a stem as its argument.");
        }
        int axis = 0;
        if (polyad.getArgCount() == 2) {
            Object arg2 = polyad.evalArg(1, state);
            checkNull(arg2, polyad.getArgAt(1));

            if (!isLong(arg2)) {
                throw new IllegalArgumentException( LIST_REVERSE + " an integer as its axis.");
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
            Iterator<StemEntry> iterator = inStem.getStemList().descendingIterator();
            while (iterator.hasNext()) {
                StemEntry s = iterator.next();
                output.listAppend(s.entry);
            }
            return output;
        }
    }
}
