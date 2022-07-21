package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.exceptions.QDLExceptionWithTrace;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.statements.TokenPosition;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.QDLStem;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
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
public class StemExtractionNode extends ExpressionImpl {
    public static final String EXTRACT = "\\";
    public static final String EXTRACT_STAR = "\\*";
    public static final String EXTRACT_UNIQUE = "\\!";
    public static final String EXTRACT_UNIQUE_STAR = "\\!*";

    public static final String EXTRACT_LIST = "\\>";
    public static final String EXTRACT_LIST_STAR = "\\>*";
    public static final String EXTRACT_LIST_UNIQUE = "\\!>";
    public static final String EXTRACT_LIST_UNIQUE_STAR = "\\!>*";

    public StemExtractionNode() {
    }

    public StemExtractionNode(TokenPosition tokenPosition) {
        super(tokenPosition);
    }

    public StemExtractionNode(int operatorType, TokenPosition tokenPosition) {
        super(operatorType, tokenPosition);
    }

    public StemExtractionNode(int operatorType) {
        super(operatorType);
    }

    IndexArgs indexArgs = new IndexArgs();

    /**
     * Remove any trailing *'s. These do nothing but make the system loop through everything.
     * <pre>
     *     a\2\*\*\* == a\2 == a.2
     * </pre>
     *
     * @param args
     * @return
     */
    protected IndexArgs normalize(IndexArgs args) {
        IndexArgs outArgs = new IndexArgs();
        boolean allStars = true;
        for (int i = args.size() - 1; -1 < i; i--) {
            IndexArg currentIA = args.get(i);
            if (currentIA.swri instanceof AllIndices) {
                if (!allStars) {
                    outArgs.add(currentIA);
                }
            } else {
                outArgs.add(currentIA);
                allStars = false;
            }
        }
        Collections.reverse(outArgs);
        return outArgs;
    }

    @Override
    public Object evaluate(State state) {
        IndexArgs args = new IndexArgs();
        args.addAll(linearize(state));
        indexArgs = normalize(args);
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
        Object out = recurse(inStem, indexArgs);
        setResult(out);
        setResultType(Constant.getType(out));
        setEvaluated(true);
        return getResult();
    }

    protected Object evaluateOLD(State state) {
        // The 0th element of args is the left-most argument.
        IndexArgs args = new IndexArgs();
        args.addAll(linearize(state));
        indexArgs = normalize(args);
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
            QDLStem out = (QDLStem) recurse(inStem, indexArgs);
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
    protected Object recurse(QDLStem in, IndexArgs sourceIndices) {
        QDLStem out = new QDLStem();
        Object otherOut = null;
        int startIndex = 1;
        IndexArg root = indexArgs.get(startIndex);
        long autoIndex = 0L;
        boolean isList = indexArgs.size() == 2; // This does list processing only
        // Very simple case of list only -- this is just an optimization for a very common case, so we don't
        // start the recursion.
        if (isList) {
            for (Object key : root.createKeySet(in)) {
                Object value = in.get(key);
                if (Constant.isScalar(root.swri.getResult())) {
                    if (value == null) {
                        // edge case, they asked for a non-existent index
                        // E.g. v.:=[;5]; v\42;
                        return QDLNull.getInstance();
                    }
                    return value;
                }
                if (value == null) {
                    continue;
                }
                if (root.strictOrder || (key instanceof String)) {
                    out.putLongOrString(key, value);
                } else {
                    out.put(autoIndex++, value);

                }
            } // end for
            return out;
        }


        for (Object key : root.createKeySet(in)) {
            Object value = in.get(key);
            if (value == null) {
                if (Constant.isScalar(root.swri.getResult()) && sourceIndices.size() == 2) {
                    // edge case, they asked for a non-existent index
                    // E.g. v.:=[;5]; v\42;
                    return QDLNull.getInstance();
                }

                continue;
            }

            if (value instanceof QDLStem) {
                IndexList indexList = new IndexList();

                if (root.isWildcard()) {
                    indexList.add(key);
                } else {
                    if (!Constant.isScalar(root.swri.getResult())) {
                        if (root.strictOrder || Constant.isString(key)) {
                            indexList.add(key);
                        } else {
                            indexList.add(autoIndex++);
                        }
                    }
                }
                otherOut = recurse((QDLStem) value, out, indexList, sourceIndices, startIndex + 1, 0L);
            } else {
                if (indexArgs.size() - 1 == startIndex) {
             //       System.out.println("recurse: last args index size =" + (indexArgs.size() - 1));
                    // set the value, but only if it is the end of an index list (so there
                    // are no more indices to traverse.
                    if (root.isWildcard()) {
                //        System.out.println("         set wildcard key=" + key + ", value=" + value);
                        out.putLongOrString(key, value);
                    } else {
                        if (Constant.isScalar(root.swri.getResult())) {
                  //          System.out.println("         return value=" + value);
                            return value;
                        } else {
                            if (root.strictOrder || (key instanceof String)) {
                   //             System.out.println("         put key=" + key + ", value=" + value);
                                out.putLongOrString(key, value);
                            } else {
                      //          System.out.println("         autoindex put key=" + key + ", value=" + value);
                                out.put(autoIndex++, value);
                            }
                        }

                    }
                }
            }
        }
        if (otherOut != null && !(otherOut instanceof QDLStem)) {
            if (!out.isEmpty()) {
                throw new NFWException("both a scalar and stem were returned as values in " + getClass().getCanonicalName());
            }
            return otherOut;
        }
        return out;
    }

    protected Object recurse(QDLStem in,
                             QDLStem out,
                             IndexList targetIndex,
                             List<IndexArg> sourceIndices,
                             int indexLocation,
                             long strictIndex) {

        if (sourceIndices.size() <= indexLocation) {
           /* System.out.println("*** recurseNEW: " +
                    "\n        in=" + in +
                    "\n        out=" + out +
                    "\n        targetIndex = " + targetIndex +
                    "\n        sourceIndices = " + sourceIndices +
                    "\n        loc=" + indexLocation +
                    "\n        strictIndex=" + strictIndex
            );*/
            IndexArg lastIndex = sourceIndices.get(indexLocation - 1);
            if (Constant.isScalar(lastIndex.swri.getResult())) {
                //System.out.println("    recurseNEW: adding all inStem");
                out.addAll(in);
            } else {
                //System.out.println("    recurseNEW: strict add inStem");
                out.putLongOrString(strictIndex, in);
            }
            return out;
        }
        IndexArg indexArg = sourceIndices.get(indexLocation);
        Object otherOut = null;
        for (Object key : indexArg.createKeySet(in)) {
            Object value = in.get(key);
            //    System.out.println("recurse 2 key =" + key + ", value=" + value);

            if (value == null) {
                continue;
            }
            if (sourceIndices.size() - 1 == indexLocation) {
                // Only set it if there are more indices. otherwise you get a ton of garbage
                IndexList indexList = targetIndex.clone();
                if (indexArg.isWildcard()) {
                    indexList.add(key);
                } else {
                    if (Constant.isScalar(indexArg.swri.getResult())) {
                        if (indexList.isEmpty()) {
                            if (value instanceof QDLStem) {
                                out.addAll((QDLStem) value);
                            } else {
                                //          out.put(0l, value);
                                return value;
                            }
                        } else {
                            out.set(indexList, value);
                        }
                        return out;
                    } else {
                        if (indexArg.strictOrder || (key instanceof String)) {
                            indexList.add(key);
                        } else {
                            indexList.add(strictIndex++);

                        }
                    }
                    out.set(indexList, value);
                }
                //System.out.println("    recurseNEW: setting value key =" + indexList + ", value = " + value);
            }else{
                if (value instanceof QDLStem) {
                                IndexList indexList = targetIndex.clone();
                                if (indexLocation + 1 < sourceIndices.size()) {
                                    //  System.out.println("recurseNEW: INDEX CHECK, targetIndex = " + targetIndex + ", strictIndex=" + strictIndex + ", loc=" + indexLocation + ", in=" + in + ", out=" + out);
                                }

                                if ((indexArg.swri instanceof AllIndices) || !Constant.isScalar(indexArg.swri.getResult())) {
                                    if (indexArg.strictOrder && indexArg.isWildcard()) {
                                        indexList.add(key);
                                    } else {
                                        indexList.add(strictIndex++);
                                    }
                                }
                                if (sourceIndices.size() <= indexLocation) {
                                    IndexArg lastIndex = sourceIndices.get(indexLocation - 1);
                                    if (Constant.isScalar(lastIndex.swri.getResult())) {
                                        out.addAll(in);
                                    } else {
                                        out.putLongOrString(strictIndex++, in);
                                    }

                                } else {
                                    otherOut = recurse((QDLStem) value, out, indexList, sourceIndices, indexLocation + 1, 0L);
                                }
                            }
            }


        }
        if (!(otherOut instanceof QDLStem)) {
            return otherOut;
        }
        return out;
    }



    /*

       a. := n(5,5,[;25])~ {'p':{'t':'a', 'u':'b', 'v':'c'}, 'q':{'t':'d', 'u':'e', 'v':'f'}, 'r':{'t':'g', 'u':'h', 'v':'i'}}
       a\[1,2,'p']\[3,'t','q',1]
    {0:[8,6], 1:[13,11], p:{t:a}}


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
        if ((!(indices.get(left).swri instanceof StemExtractionNode)) && (!(indices.get(right).swri instanceof StemExtractionNode))) {
            // simplest case: a\* so nothing to linearize.
            swris.add(indices.get(left), state);
            swris.add(indices.get(right), state);
            return swris;
        }

        if (!(indices.get(left).swri instanceof StemExtractionNode)) {
            swris.add(indices.get(left), state);
            return swris;
        }
        List<IndexArg> currentIAs = indices;
        //List<IndexArg> currentIAs = ((StemSubsettingNode) indices.get(left).swri).indexArgs;
        IndexArg leftArg = currentIAs.get(left);
        IndexArg rightArg = currentIAs.get(right);
        while (leftArg.swri instanceof StemExtractionNode) {
            swris.add(rightArg, state);
            currentIAs = ((StemExtractionNode) leftArg.swri).indexArgs;
            rightArg = currentIAs.get(right);
            leftArg = currentIAs.get(left);
        }
        if (!(rightArg.swri instanceof StemExtractionNode)) {
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
        if ((!(indexArgs.get(left).swri instanceof StemExtractionNode)) && (!(indexArgs.get(right).swri instanceof StemExtractionNode))) {
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
        while (rightArg.swri instanceof StemExtractionNode) {
            if (leftArg.swri instanceof StemExtractionNode) {
                List<IndexArg> ll = linearizeLeftArgs(((StemExtractionNode) leftArg.swri).indexArgs, state);
                swris.addAll(ll);
            } else {
                leftArg.strictOrder = isStrict; // This is read as part of the operator
                leftArg.interpretListArg = interpretAsList;
                swris.add(leftArg);
            }
            isStrict = rightArg.strictOrder;
            interpretAsList = rightArg.interpretListArg;
            currentIAs = ((StemExtractionNode) rightArg.swri).indexArgs;
            rightArg = currentIAs.get(right);
            leftArg = currentIAs.get(left);
        }

        if (leftArg.swri instanceof StemExtractionNode) {
            swris.addAll(linearizeLeftArgs(((StemExtractionNode) leftArg.swri).indexArgs, state));
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
