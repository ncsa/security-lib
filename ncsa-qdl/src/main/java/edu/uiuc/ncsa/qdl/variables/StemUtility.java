package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.RankException;

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

}
