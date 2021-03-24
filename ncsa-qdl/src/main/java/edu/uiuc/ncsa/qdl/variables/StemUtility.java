package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.RankException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/24/21 at  12:38 PM
 */
public class StemUtility {
    public interface AxisAction {
          void action(StemVariable out, String key, StemVariable leftStem, StemVariable rightStem);
      }

      public static boolean isStem(Object o){
        return o instanceof StemVariable;
      }

      public static boolean areNoneStems(Object... objects){
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
     * Apply some action along an axis.
     * @param out0
     * @param left0
     * @param right0
     * @param depth
     * @param maxDepth
     * @param axisAction
     */
      public static void axisRecursion(StemVariable out0,
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

}
