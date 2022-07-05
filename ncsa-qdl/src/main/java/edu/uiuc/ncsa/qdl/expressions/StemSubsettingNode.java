package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.exceptions.QDLExceptionWithTrace;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.statements.TokenPosition;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLStem;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This will allow for creating a subset (copy of portion) of a stem. The result is
 * a stem completely indep. of the original.
 * <p>Created by Jeff Gaynor<br>
 * on 6/27/22 at  4:02 PM
 */
public class StemSubsettingNode extends ExpressionImpl {
    public static final String EXTRACT = "\\";
    public static final String EXTRACT_STAR = "\\*";
    public static final String EXTRACT_UNIQUE = "\\!";
    public static final String EXTRACT_UNIQUE_STAR = "\\!*";

    public static final String EXTRACT_LIST = "\\>";
    public static final String EXTRACT_LIST_STAR = "\\>*";
    public static final String EXTRACT_LIST_UNIQUE = "\\!>";
    public static final String EXTRACT_LIST_UNIQUE_STAR = "\\!>*";

    public StemSubsettingNode() {
    }

    public StemSubsettingNode(TokenPosition tokenPosition) {
        super(tokenPosition);
    }

    public StemSubsettingNode(int operatorType, TokenPosition tokenPosition) {
        super(operatorType, tokenPosition);
    }

    public StemSubsettingNode(int operatorType) {
        super(operatorType);
    }

    IndexArgs indexArgs = new IndexArgs();


    @Override
    public Object evaluate(State state) {
        // The 0th element of args is the left-most argument.
        IndexArgs args = new IndexArgs();
        args.addAll(linearize(state));
        indexArgs = args;
        for (int i = 0; i < indexArgs.size(); i++) {
            IndexArg ia = indexArgs.get(i);
            ia.swri.evaluate(state);
            if (0 < i) {
                if (ia.swri.getResult() == null) {
                    if (ia.swri instanceof VariableNode) {
                        VariableNode vNode = (VariableNode) ia.swri;
                        if (vNode.getVariableReference().endsWith(QDLStem.STEM_INDEX_MARKER)) {
                            throw new QDLExceptionWithTrace(vNode.getVariableReference() + " not found", this);
                        }
                        vNode.setResult(vNode.getVariableReference());
                    }
                }
            }
            //     System.out.println(ia);
        }
        Object larg = args.get(0).swri.getResult();
        if (!(larg instanceof QDLStem)) {
            throw new QDLExceptionWithTrace("Extraction operator only applies to stems.", getLeftArg());
        }
        QDLStem inStem = (QDLStem) larg;
        if (indexArgs.isAllWildcards()) {
            // special case of a\* or a\*\* etc.
            setResultType(Constant.STEM_TYPE);
            setEvaluated(true);
            setResult(inStem);
            return getResult();
        }

        ArrayList<IndexList> sourceIndices = null;
        ArrayList<IndexList> targetIndices = null;
        if (indexArgs.hasWildcard()) {
            QDLStem out = recurse(inStem, indexArgs);
            setResult(out);
            setResultType(Constant.STEM_TYPE);
            setEvaluated(true);
            return getResult();
        } else {
            // Special case no wild cards because there is no dynamic allocation of
            // indices. No wildcards should be very fast.
            sourceIndices = indexArgs.createSourceIndices();
            targetIndices = indexArgs.createTargetIndices();
        }
        // edge case. They requested something like b\2\3 -- this should be a scalar
        if (sourceIndices.size() == 1 && targetIndices.size() == 1 && targetIndices.get(0).size() == 0) {
            IndexList value = inStem.get(sourceIndices.get(0), false);
            if (value != null && !value.isEmpty()) {
                setResult(value.get(0));
                setEvaluated(true);
                setResultType(Constant.getType(getResult()));
                return getResult();
            }
            throw new QDLExceptionWithTrace("no such value ", this);
        }
        QDLStem out = new QDLStem();
        for (int i = 0; i < sourceIndices.size(); i++) {
            try {
                IndexList value = inStem.get(sourceIndices.get(i), false);
                if (value != null && !value.isEmpty()) {
                    out.set(targetIndices.get(i), value.get(0));
                }
            } catch (IndexError ie) {
                // rock on. This is benign in this context.
            }

        }
        setResultType(Constant.STEM_TYPE);
        setEvaluated(true);
        setResult(out);
        return getResult();


    }

    /*
    b.0 := [;3]
    b.1 := [;5]+10
    b.2 := [;7] + 20
    b.3 := [;11] + 100
          b\*\[0,2]

        b\[2,1]\[5,3,1]


      a. := n(3,4,5,n(60))
a\[1,2]\![1,2]\[3,4]

{1:{1:{3:[28], 4:[29]}, 2:{3:[33], 4:[34]}}, 2:{1:{3:[48], 4:[49]}, 2:{3:[53], 4:[54]}}}
a.
[
[[0,1,2,3,4],[5,6,7,8,9],[10,11,12,13,14],[15,16,17,18,19]],
[[20,21,22,23,24],[25,26,27,28,29],[30,31,32,33,34],[35,36,37,38,39]],
[[40,41,42,43,44],[45,46,47,48,49],[50,51,52,53,54],[55,56,57,58,59]]
]

     */


    /**
     * Starts descent through all of the
     *
     * @param in
     * @param sourceIndices
     * @return
     */
    protected QDLStem recurse(QDLStem in, IndexArgs sourceIndices) {
        QDLStem out = new QDLStem();
        int startIndex = 1;
        IndexArg root = indexArgs.get(startIndex);
        long autoIndex = 0L;
        for (Object key : root.createKeySet(in)) {
            Object value = in.get(key);
            if (value == null) {
                continue;
            }
            if (value instanceof QDLStem) {
                IndexList indexList = new IndexList();

                if ((root.swri instanceof AllIndices) || !Constant.isScalar(root.swri.getResult())) {
                    if (root.isWildcard()) {
                        indexList.add(key);
                    } else {
                        if (root.strictOrder || Constant.isString(key)) {
                            indexList.add(key);
                        } else {
                            indexList.add(autoIndex++);
                        }
                    }
                }
       //           System.out.println("recurse 1 indexList=" + indexList);
                recurse((QDLStem) value, out, indexList, sourceIndices, startIndex + 1, 0L);
            } else {
                if (indexArgs.size() - 1 == startIndex) {
                    // set the value, but only if it is the end of an index list (so there
                    // are no more indices to traverse.
                    if (root.isWildcard()) {
                        out.putLongOrString(key, value);
                    } else {
                        if (!Constant.isScalar(root.swri.getResult())) {
                            if (root.strictOrder) {
                                out.putLongOrString(key, value);
                            } else {
                                out.putLongOrString(autoIndex++, value);
                            }

                        }

                    }
                }
            }
        }

        return out;
    }

    protected void recurse(QDLStem in,
                           QDLStem out,
                           IndexList targetIndex,
                           List<IndexArg> sourceIndices,
                           int indexLocation,
                           long strictIndex) {

        if (sourceIndices.size() <= indexLocation) {
            return;
        }
        IndexArg indexArg = sourceIndices.get(indexLocation);
        for (Object key : indexArg.createKeySet(in)) {
            Object value = in.get(key);
            //   System.out.println("recurse 2 key =" + key + ", value=" + value);

            if (value == null) {
                continue;
            }
            if (value instanceof QDLStem) {
                IndexList indexList = targetIndex.clone();
                if ((indexArg.swri instanceof AllIndices) || !Constant.isScalar(indexArg.swri.getResult())) {
                    if (indexArg.strictOrder && indexArg.isWildcard()) {
                        indexList.add(key);
                    } else {
                        indexList.add(strictIndex++);
                    }
                }

                recurse((QDLStem) value, out, indexList, sourceIndices, indexLocation + 1, 0L);

            } else {
                if (sourceIndices.size() - 1 == indexLocation) {
                    // Only set it if there are more indices. otherwise you get a ton of garbage
                    IndexList indexList = targetIndex.clone();
                    if (indexArg.isWildcard()) {
                        indexList.add(key);
                    } else {
                        if (!Constant.isScalar(indexArg.swri.getResult())) {
                            if (indexArg.strictOrder) {
                                indexList.add(key);
                            } else {
                                indexList.add(strictIndex++);
                            }
                        }

                    }
                    //              System.out.println("recurse: setting value key =" + indexList + ", value = " + value);
                    out.set(indexList, value);
                }

            }
        }
    }

    /*
       a. := n(3,4,5,n(3*4*5))
     a\[;2]\![3,1]

     expected:
     b.0.3 == a.0.3; b.0.1 := a.0.1
     b.1.3 == a.1.3; b.1.1 := a.1.1

     a\[;2]\[3,1]
     expected:
     b.0.0 := a.0.3; b.0.1 == a.0.1;
     b.1.0 := a.1.3; b.1.1 == a.1.1

    a.:=[[0,1,2,3,4],[5,6,7,8,9],[10,11,12,13,14],[15,16,17,18,19],[20,21,22,23,24]]~{'p':{'t':'a', 'u':'b', 'v':'c'}, 'q':{'t':'d', 'u':'e', 'v':'f'}, 'r':{'t':'g', 'u':'h', 'v':'i'}}

     a.:=n(5,5,n(25))
     m_set(x., y., z.)->local[while[k∈[;size(y.)]][x.y.k :=z.k];return(x.);]
     [[p,t],[p,u],[p,v],[q,t],[q,u],[q,v],[r,t],[r,u],[r,v]]
     m_indices(
     */

    /**
     * Some parts of parse trees are left balanced, not right. linearizes a left tree
     *
     * @return
     */
    protected IndexArgs linearizeLeftArgs(List<IndexArg> indices, State state) {
        IndexArgs swris = new IndexArgs();
        int left = 0;
        int right = 1; // indices
        if ((!(indices.get(left).swri instanceof StemSubsettingNode)) && (!(indices.get(right).swri instanceof StemSubsettingNode))) {
            // simplest case: a\* so nothing to linearize.
            swris.add(indices.get(left), state);
            swris.add(indices.get(right), state);
            return swris;
        }

        if (!(indices.get(left).swri instanceof StemSubsettingNode)) {
            swris.add(indices.get(left), state);
            return swris;
        }
        List<IndexArg> currentIAs = indices;
        //List<IndexArg> currentIAs = ((StemSubsettingNode) indices.get(left).swri).indexArgs;
        IndexArg leftArg = currentIAs.get(left);
        IndexArg rightArg = currentIAs.get(right);
        while (leftArg.swri instanceof StemSubsettingNode) {
            swris.add(rightArg, state);
            currentIAs = ((StemSubsettingNode) leftArg.swri).indexArgs;
            rightArg = currentIAs.get(right);
            leftArg = currentIAs.get(left);
        }
        if (!(rightArg.swri instanceof StemSubsettingNode)) {
            swris.add(rightArg, state); // only add it if it is simple, not a tree. The tree will get parsed later
        }
        swris.add(leftArg, state);
        Collections.reverse(swris);
        return swris;
    }

    /**
     * Start linearizing the tree. This treats trees balanced on the right directly,and calls another
     * method if there is a left hand tree.
     *
     * @return
     */
    protected List<IndexArg> linearize(State state) {
        IndexArgs swris = new IndexArgs();
        int left = 0;
        int right = 1; // indices
        if ((!(indexArgs.get(left).swri instanceof StemSubsettingNode)) && (!(indexArgs.get(right).swri instanceof StemSubsettingNode))) {
            // simplest case: a\*, a\i. so nothing to linearize.
            swris.add(indexArgs.get(left));
            swris.add(indexArgs.get(right), state);
            return swris;
        }
        boolean isStrict = indexArgs.get(right).strictOrder; // This is read as part of the operator, so previous op has it.
        boolean interpretAsList = indexArgs.get(right).interpretListArg;
        List<IndexArg> currentIAs = indexArgs;
        //List<IndexArg> currentIAs = ((StemSubsettingNode) indexArgs.get(right).swri).indexArgs;
        IndexArg leftArg = currentIAs.get(left);
        IndexArg rightArg = currentIAs.get(right);
        while (rightArg.swri instanceof StemSubsettingNode) {
            if (leftArg.swri instanceof StemSubsettingNode) {
                List<IndexArg> ll = linearizeLeftArgs(((StemSubsettingNode) leftArg.swri).indexArgs, state);
                swris.addAll(ll);
            } else {
                leftArg.strictOrder = isStrict; // This is read as part of the operator
                leftArg.interpretListArg = interpretAsList;
                swris.add(leftArg);
            }
            isStrict = rightArg.strictOrder;
            interpretAsList = rightArg.interpretListArg;
            currentIAs = ((StemSubsettingNode) rightArg.swri).indexArgs;
            rightArg = currentIAs.get(right);
            leftArg = currentIAs.get(left);
        }

        if (leftArg.swri instanceof StemSubsettingNode) {
            swris.addAll(linearizeLeftArgs(((StemSubsettingNode) leftArg.swri).indexArgs, state));
        } else {
            leftArg.strictOrder = isStrict; // This is read as part of the operator
            leftArg.interpretListArg = interpretAsList;

            swris.add(leftArg, state);
        }
        swris.add(rightArg, state);
        return swris;
    }
    /*
    a. := n(3,4,5,n(60))
     a\[1,0]\![1,2]\[3,4]

         b. := n(8,8,8,8, n(8^4))
      b\[0,1]\![2,3]\![4,5]\[6,7]
    [{2:{4:[[166],[167]], 5:[[174],[175]]}, 3:{4:[[230],[231]], 5:[[238],[239]]}},{2:{4:[[678],[679]], 5:[[686],[687]]}, 3:{4:[[742],[743]], 5:[[750],[751]]}}]

  c. := b\0\[1,2]\3\[5,6,7]

     */

    @Override
    public StatementWithResultInterface makeCopy() {
        throw new NotImplementedException();
    }

    public StatementWithResultInterface getLeftArg() {
        if (getArguments().isEmpty()) {
            return null;
        }
        return getArguments().get(0);
    }

    public void setLeftArg(StatementWithResultInterface swri) {
        if (getArguments().size() == 0) {
            getArguments().add(swri);
        } else {
            getArguments().set(0, swri);
        }
    }

    public void setRightArg(StatementWithResultInterface swri) {
        if (getArguments().size() == 1) {
            getArguments().add(swri);
        } else {
            getArguments().set(1, swri);
        }
    }

    public StatementWithResultInterface getRightArg() {
        if (getArguments().size() < 2) {
            return null;
        }
        return getArguments().get(1);
    }


    public void addArgument(IndexArg indexArg) {
        indexArgs.add(indexArg);
        getArguments().add(indexArg.swri); // Maybe
 /*       System.out.println("add arg #" + (getArguments().size() - 1) + ", "
                + indexArg + ", class =" + indexArg.swri.getClass().getSimpleName());*/
    }

    @Override
    public String toString() {
        return "StemSubsettingNode{" +
                "indexArgs=" + indexArgs +
                '}';
    }

}
