package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/12/21 at  6:42 AM
 */
public class ESN2 extends ExpressionImpl {
    @Override
    public Object evaluate(State state) {
        return get(state);
    }


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

    protected Object get(State state) {
        ArrayList<StatementWithResultInterface> leftArgs = new ArrayList<>();
        ArrayList<StatementWithResultInterface> rightArgs = new ArrayList<>();
        linearizeTree(leftArgs, rightArgs);

        // Evaluation pass. Make sure everything resolves w.r.t. the state
        IndexList indexList = getIndexList(state, rightArgs);
        if (indexList.size() == 0) {
            // just wants whole stem, no indices
            Object r0 = leftArgs.get(leftArgs.size() - 1).evaluate(state);
            if (!(r0 instanceof StemVariable)) {
                throw new IllegalStateException("error: left argument must evaluate to be a stem ");
            }
            StemVariable stemVariable = (StemVariable) r0;
            setResult(stemVariable);
            setResultType(Constant.STEM_TYPE);
            setEvaluated(true);
            return stemVariable;
        }
        // Made it this far. Now we need to do this again, but handing off indices
        // to the stem as needed.
        whittleIndices(indexList);

        Object r0 = leftArgs.get(leftArgs.size() - 1).evaluate(state);
        if (!(r0 instanceof StemVariable)) {
            throw new IllegalStateException("error: left argument must evaluate to be a stem ");
        }
        StemVariable stemVariable = (StemVariable) r0;
        IndexList r = stemVariable.get(indexList,true);
        Object result = r.get(0);
        setResult(result);
        setResultType(Constant.getType(result));
        setEvaluated(true);
        return result;

    }

    /**
     * Actual stem contract: Evaluates the indices from right to left and does the
     * evaluations, When this is done, the index set is simply indices, ready fo
     * set or get in the stem.
     *
     * @param indexList
     */
    protected void whittleIndices(IndexList indexList) {
        if(experimental) {
            newWhittle(indexList);
        }else{
            oldWhittle(indexList);
        }
    }
    protected void oldWhittle(IndexList indexList){
        IndexList r;

            for (int i = indexList.size() - 1; 0 <= i; i--) {
                if (indexList.get(i) instanceof StemVariable) {
                    r = ((StemVariable) indexList.get(i)).get(indexList.tail(i + 1), false);
                    indexList.truncate(i);
                    indexList.addAll(i, r);
                }
            }
    }

    /**
     * Turns on or off all machinery associated with the allowing . to accept stem lists
     * as multi indices.
     */
    public static boolean experimental = true;

    protected void newWhittle(IndexList indexList){
        IndexList r;

            for (int i = indexList.size() - 1; 0 <= i; i--) {
                if (indexList.get(i) instanceof StemVariable) {
                    if(i == indexList.size() - 1 ){
                        continue;
                       }
                    r = ((StemVariable) indexList.get(i)).get(indexList.tail(i + 1), false);
                    indexList.truncate(i);
                    indexList.addAll(i, r);
                }else{
                    // Case that left most argument is not a stem, but that the rhs is
                    // which implies the user made a boo-boo
                    if(i<indexList.size()-1){
                        if(indexList.get(i+1) instanceof StemVariable){
                            throw new IndexError("error: lhs is not a stem.");
                        }
                    }
                }
            }
    }

    private IndexList getIndexList(State state, ArrayList<StatementWithResultInterface> rightArgs) {
        IndexList indexList = new IndexList(rightArgs.size());
        boolean isFirst = true;
        Object obj = null;

        for (int i = rightArgs.size() - 1; 0 <= i; i--) {
            if (isFirst) {
                obj = rightArgs.get(i).evaluate(state);
                isFirst = false;
            } else {
                // Stem contract: Assume everything is a stem, if not, check if scalar.
                if (rightArgs.get(i) instanceof VariableNode) {
                    VariableNode vNode = (VariableNode) rightArgs.get(i);
                    if (vNode.isStem()) {
                        obj = vNode.evaluate(state);

                    } else {
                        VariableNode vNode1 = new VariableNode(vNode.getVariableReference() + StemVariable.STEM_INDEX_MARKER);
                        obj = vNode1.evaluate(state);
                        if (obj == null) {
                            // try it as a simple scalar
                            obj = vNode.evaluate(state);
                        }

                    }

                } else {
                    obj = rightArgs.get(i).evaluate(state);

                }
            }

/*            if (rightArgs.get(i) instanceof VariableNode) {
                if (obj == null) {
                    obj = ((VariableNode) rightArgs.get(i)).getVariableReference();
                }
            }

 */
            if (obj == null) {
                VariableNode vNode = null;
                StatementWithResultInterface x;
                if (rightArgs.get(i) instanceof ParenthesizedExpression) {
                    x = ((ParenthesizedExpression) rightArgs.get(i)).getExpression();
                    if (x instanceof VariableNode) {
                        vNode = (VariableNode) x;
                    } else {
                        throw new IllegalStateException("Unknown/unexpected value for variable");
                    }
                }
                if (rightArgs.get(i) instanceof VariableNode) {
                    vNode = (VariableNode) rightArgs.get(i);
                }
                obj = vNode.getVariableReference();
            }
            indexList.set(i, obj);
        }
        return indexList;
    }

    /**
     * Takes the node structure and converts it to a list structure.
     *
     * @param leftArgs
     * @param rightArgs
     */
    private void linearizeTree(ArrayList<StatementWithResultInterface> leftArgs, ArrayList<StatementWithResultInterface> rightArgs) {
        //     leftArgs.add(getLeftArg());
        if (getRightArg() != null) {
            // This can have a null right arg, e.g. a.b.
            rightArgs.add(getRightArg());
        }
        StatementWithResultInterface swri = getLeftArg();
        while (swri instanceof ESN2) {
            ESN2 esn2 = (ESN2) swri;
            leftArgs.add(esn2.getLeftArg());
            rightArgs.add(esn2.getRightArg());
            swri = esn2.getLeftArg();
        }
        leftArgs.add(swri);
        Collections.reverse(rightArgs);
    }

    public void set(State state, Object newValue) {
        ArrayList<StatementWithResultInterface> leftArgs = new ArrayList<>();
        ArrayList<StatementWithResultInterface> rightArgs = new ArrayList<>();
        linearizeTree(leftArgs, rightArgs);
        // Evaluation pass. Make sure everything resolves w.r.t. the state
        IndexList indexList = getIndexList(state, rightArgs);

        // Made it this far. Now we need to do this again, but handing off indices
        // to the stem as needed.
        whittleIndices(indexList);
        StemVariable stemVariable = null;
        boolean gotOne = false;
        StatementWithResultInterface realLeftArg = leftArgs.get(leftArgs.size() - 1);
        realLeftArg.evaluate(state);
        boolean isParenthesized = realLeftArg instanceof ParenthesizedExpression;
        if (isParenthesized) {
            realLeftArg = ((ParenthesizedExpression) realLeftArg).getExpression();
        }
        //leftArgs.get(leftArgs.size() - 1).evaluate(state);
        if (realLeftArg.getResult() instanceof StemVariable) {
            stemVariable = (StemVariable) realLeftArg.getResult();
            gotOne = true;
        }
        if (realLeftArg instanceof VariableNode) {
            VariableNode vNode = (VariableNode) realLeftArg;
            // Either it is not set or set to QDLNull
            if (vNode.getResult() == null || (vNode.getResult() instanceof QDLNull)) {
                // then this variable does not exist in the symbol table. Add it
                stemVariable = new StemVariable();
                state.setValue(vNode.getVariableReference(), stemVariable);
            }
            gotOne = true;
        }
        if (!gotOne) {
            System.out.println("uh-oh");
        }
        // it is possible that the left most expression is a stem node, so make sure there is
        // something to return
        if (stemVariable == null) {
            stemVariable = new StemVariable();
        }
        stemVariable.set(indexList, newValue); // let the stem set its value internally

        setResult(stemVariable);
        setResultType(Constant.STEM_TYPE);
        setEvaluated(true);

    }

}
