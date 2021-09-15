package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.RankException;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.ExpressionStemNode;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;

import java.util.ArrayList;

import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/24/21 at  12:38 PM
 */
public class StemUtility {
    public static final Long LAST_AXIS_ARGUMENT_VALUE = new Long(-0xcafed00d);

/*

    public interface MonadAxisAction {
        void action(StemVariable out, String key, StemVariable arg);
    }
*/

    /**
     * Action to be applied at a given axis.
     */
    public interface DyadAxisAction {
        /**
         * Action to be applied
         *
         * @param out       -  the result
         * @param key       - current key
         * @param leftStem  - The left hand stem's argument at this axis
         * @param rightStem - the right hand stem's argument at this axis.
         */
        void action(StemVariable out, String key, StemVariable leftStem, StemVariable rightStem);
    }

    public static boolean isStem(Object o) {
        return o instanceof StemVariable;
    }

    public static boolean areNoneStems(Object... objects) {
        for (Object arg : objects) {
            if (isStem(arg)) return false;
        }
        return true;
    }

    public static boolean areAllStems(Object... objects) {
        for (Object arg : objects) {
            if (!isStem(arg)) return false;
        }
        return true;
    }

    /**
     * Apply some action along an axis. This will recurse to a given axis and apply an action there
     *
     * @param out0
     * @param left0
     * @param right0
     * @param depth
     * @param maxDepth
     * @param axisAction
     */
    public static void axisDayadRecursion(StemVariable out0,
                                          StemVariable left0,
                                          StemVariable right0,
                                          int depth,
                                          boolean maxDepth,
                                          DyadAxisAction axisAction) {
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
                    axisDayadRecursion(out1, left1, right1, depth - 1, maxDepth, axisAction);
                } else {
                    out0.put(key0, left1.union(right1));
                }
            }
        }
    }

  /*  public static void axisMonadRecursion(StemVariable out0,
                                          StemVariable arg,
                                          int depth,
                                          boolean maxDepth,
                                          MonadAxisAction axisAction) {
        System.out.println("starting depth=" + depth + ", maxDepth=" + maxDepth + ", arg=" + arg);
        for (String key0 : arg.keySet()) {
            Object argObj = arg.get(key0);

            StemVariable arg1 = null;
            if (isStem(argObj)) {
                arg1 = (StemVariable) argObj;
            } else {
                arg1 = new StemVariable();
                arg1.put(0L, argObj);
            }

            // bottomedout means that the axis was greater than the depth. If this is trying
            // to work on an unknown last axis, that is ok.
            boolean bottomedOut = !isStem(argObj) && maxDepth && 0 < depth;
            //boolean bottomedOut =  0 < depth;
            if (bottomedOut) {
                axisAction.action(out0, key0, arg1);
            } else {
                if (0 < depth) {
                    if (!isStem(argObj)) {
                        throw new RankException("rank error");
                    }
                    StemVariable out1 = new StemVariable();
                    out0.put(key0, out1);
                    axisMonadRecursion(out1, arg1, depth - 1, maxDepth, axisAction);
                } else {
                    //out0.put(key0, arg1);
                    axisAction.action(out0, key0, arg1);
                }
            }
        }
    }*/


    /**
     * For operations that return a stem.
     */
    public interface StemAxisWalkerAction1 {
        Object action(StemVariable inStem);
    }

    /**
     * Recurses through stem that has arbitrary rank.
     * @param inStem
     * @param depth
     * @param walker
     * @return
     */
    public static Object axisWalker(StemVariable inStem, int depth, StemAxisWalkerAction1 walker) {
        if (inStem.getRank() < depth+1) {
            throw new RankException("error: axis " + depth + " requested on stem of rank " + inStem.getRank() );
        }
        if (depth <= 0) {
            return walker.action(inStem);
        }
        StemVariable outStem = new StemVariable();
        for (String key1 : inStem.keySet()) {
            Object obj = inStem.get(key1);
            if (!isStem(obj)) {
                continue;
            }
            outStem.put(key1, axisWalker((StemVariable) obj, depth - 1, walker));
        }
        return outStem;
    }
    /*
             a.b.c.d.i(0).j := 1

             This exists because the parser was changed to try and get rid of a lot of
             annoying parentheses, e.g.
             (a.).(0).i(x).(2)

             which can now be entered as
             a.0.i(x).2
             However, the machinery for accessing stems still treats the variable references to stem
             markers as units, so 'a.b.c' is by the stem and resolved -- only the stem knows about b.c.
             Now this is a tree of elements
                  /\
               /\   c
             a   b

             So this idea si to try and reverse the process here and pass along what used to be the case to the
             system. This is horribly inefficient, since Antlr prases it, we deparse it, then reparse it in the
             stem, but the alternative is a rewrite of how stems are managed which would be a large-scale
             undertaking.
         */
        public static void doNodeSurgery(ExpressionStemNode ESN, State state) {

            ArrayList<StatementWithResultInterface> leftArgs = new ArrayList<>();
            ArrayList<StatementWithResultInterface> rightArgs = new ArrayList<>();
            StatementWithResultInterface swri = ESN;
            while (swri != null) {
                leftArgs.add(swri);
                if (swri instanceof ExpressionStemNode) {
                    ExpressionStemNode esn = (ExpressionStemNode) swri;
                    rightArgs.add(esn.getRightArg());
                    swri = esn.getLeftArg();
                } else {
                    swri = null;
                }
            }
            if (!(leftArgs.get(leftArgs.size() - 1) instanceof VariableNode)) {
                return; // do nothing
            }
            VariableNode actualStem = (VariableNode) leftArgs.get(leftArgs.size() - 1);
            int i = 0;
            String newVariableReference = actualStem.getVariableReference();
            boolean isFirst = true;
            for (i = rightArgs.size() - 1; 0 <= i; i--) {
                swri = rightArgs.get(i);
                boolean didIt = false;
                String nextToken = "";
                if (swri instanceof VariableNode) {
                    VariableNode vNode = (VariableNode) swri;
                    nextToken = vNode.getVariableReference();
                    didIt = true;
                }
                if (swri instanceof ConstantNode) {
                    ConstantNode cNode = (ConstantNode) swri;
                    cNode.evaluate(state);
                    nextToken = cNode.getResult().toString();
                    didIt = true;
                }
                if (!didIt) {

                    break; // jump out at first non-variable node
                }
                if (isFirst) {
                    isFirst = false;
                    newVariableReference = newVariableReference + nextToken;
                } else {
                    newVariableReference = newVariableReference + STEM_INDEX_MARKER + nextToken;
                }
            }

            // If this ended with a "." then the r arg is set to a Java null.  This means
            // we add it back in to the new variable reference or we'll get an error
            // about setting a non-stem value.
            if (rightArgs.get(0) == null) {
                newVariableReference = newVariableReference + STEM_INDEX_MARKER;
            }
            VariableNode variableNode = new VariableNode(newVariableReference);
            ExpressionStemNode newESN;
            if (i <= 0) {
                ESN.setLeftArg(variableNode);
            } else {
                newESN = (ExpressionStemNode) leftArgs.get(i);
                newESN.setLeftArg(variableNode);
            }

           state.setValue(newVariableReference, null);
            // last one
        }
        /*
               x := 'h.i.j'
              x1 := 'h.i.j.'
             w.x := 5
            w.x1 := 10
            is_defined(w.h.i.j); // false
         w.h.i.j := 100
         w.x == w.'h.i.j'; // true
         w.x1 == (w.).'h.i.j.';  // true
         */
    // See list_formatting.txt for possible improvement to display stems.
    public static void formatList(StemVariable stem){
        if(!stem.isList()){
             throw new IllegalArgumentException("cannot format general stem");
        }
    //     if(stem.getRank() == 1L){
             for(String key : stem.keySet()){
                 Object obj = stem.get(key);
                 if(obj instanceof StemVariable){
                     StemVariable stemVariable = (StemVariable) obj;
                     String row = "";
                     for(Object key2: stemVariable.keySet()){
                         row = row + stemVariable.get(key2) + " ";
                     }

                     System.out.println(row);
                 }else {
                     System.out.println(stem.get(key));
                 }
             }
             return;
      //   }
    }
    public static void main(String[] args){
        StemVariable outerStem = new StemVariable();
        for(int k = 0; k <5l ; k++){
            StemVariable stemVariable0 = new StemVariable();
            for(int j = 0 ;j < 4; j++){
                StemVariable stemVariable = new StemVariable();
                for(int i = 0; i<5; i++){
                    stemVariable.put(i, k + "_" + j + "_" + i);
                }
                stemVariable0.put(j, stemVariable);
            }
            outerStem.put(k, stemVariable0);

        }
                formatList(outerStem);
    }
}
