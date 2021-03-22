package edu.uiuc.ncsa.qdl.util;

import java.util.Arrays;

/**
 * A class that does n-depth loops. 
 * <p>Created by Jeff Gaynor<br>
 * on 3/22/21 at  7:21 AM
 */
public class NFor {

    /**
     * Does the actual work. The indices are in order from the top.
     */
    public  interface NForActionInterface {
         void evaluate(int[] indices);
    }

    private final int[] endIndices;
    private final NForActionInterface action;

    public NFor(int[] endIndices, NForActionInterface action) {
        this.endIndices = endIndices;
        this.action = action;
    }

    /**
     * Loops n deep, then executes the {@link NForActionInterface#evaluate(int[])} method.<br/><br/>
     * E.g.<br/><br/>
     * startIndex = 0, endIndex = 3<br/><br/>
     * nfor(2) yields  indices
     * <pre>
     *    [0, 0]
     *    [0, 1]
     *    [0, 2]
     *    [1, 0]
     *    [1, 1]
     *    [1, 2]
     *    [2, 0]
     *    [2, 1]
     *    [2, 2]
     * </pre>
     * @param depth
     */
    public void nFor (int depth) {
        nfor(0, new int[0], depth);
    }

    private void nfor(int level, int[] indices, int maxLevel) {
        if (level == maxLevel) {
            action.evaluate(indices);
        } else {
            int newLevel = level + 1;
            int[] newIndices = new int[newLevel];
            System.arraycopy(indices, 0, newIndices, 0, level);
            newIndices[level] = 0;
            while (newIndices[level] < endIndices[level]) {
                nfor(newLevel, newIndices, maxLevel);
                ++newIndices[level];
            }
        }
    }

    public static void main(String[] args){
        NForActionInterface ai = new NForActionInterface() {
            @Override
            public void evaluate(int[] indices) {
                // Just spits out the indices.
                System.out.println("indices #" + indices.length + " = " + Arrays.toString(indices));
            }
        };
        NFor nFor = new NFor(new int[]{2,3,4}, ai);
        nFor.nFor(3);
    }
}