package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StemMultiIndex;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static edu.uiuc.ncsa.qdl.variables.Constant.*;
import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/5/21 at  5:58 AM
 */
public class ExpressionStemNode implements StatementWithResultInterface {
    public ArrayList<StatementWithResultInterface> getStatements() {
        return statements;
    }

    public void setStatements(ArrayList<StatementWithResultInterface> statements) {
        this.statements = statements;
    }

    ArrayList<StatementWithResultInterface> statements = new ArrayList<>();

    Object result = null;

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object object) {
        this.result = object;
    }

    int resultType = UNKNOWN_TYPE;

    @Override
    public int getResultType() {
        return resultType;
    }

    @Override
    public void setResultType(int type) {
        resultType = type;
    }


    @Override
    public boolean isEvaluated() {
        return evaluated;
    }

    boolean evaluated = false;

    @Override
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    protected StatementWithResultInterface getLeftArg() {
        return getStatements().get(0);
    }

    protected StatementWithResultInterface getRightArg() {
        return getStatements().get(1);
    }

    /*
       Extended notes, just in case I have to revisit this later and figure out what it does.

       Test presets
       j:=2;k:=1;p:=4;q:=5;r:=6;a. := [n(4),n(5),n(6)];
       b. := [[n(4),n(5)],[n(6),n(7)]]
       Not working
       n(3).0 -- gives parser error for .0 since it thinks it is a decimal.
       b.i(0).i(1).i(2) --  syntax error at char 3, 'missing ; at ("

        b. := [[-n(4)^2,2+n(5)],[10 - 3*n(6),4+5*n(7)]];
        (b.).i(0).i(1).(2) == b.0.1.2

      ** TO DO -- Find stem ex in documentation and create one with functions. w.x.y.z

The following are working:
         (n(4)^2-5).i(3)
         [n(5),-n(6)].i(1).i(3)
         [n(5),n(4)].k
         [2+3*n(5),10 - n(4)].(k.0)
         {'a':'b','c':'d'}.i('a')
         {'a':'b','c':'d'}.'a'
         n(3).n(4).j
         n(3).n(4).n(5).n(6).n(7).n(8).j;
         n(3).n(4).n(5).n(6).i(2);
         n(3).n(4).n(5).(a.k).n(6).n(7).j
        (4*n(5)-21).(n(3).n(4).i(2))
         3 rank exx.
         [[-n(4),3*n(5)],[11+n(6), 4-n(5)^2]].i(0).i(1).i(2)
         (b.).i(0).i(1).i(2)

        Embedded stem example. This get a.1.2 in a very roundabout way
        k:=1;j:=2;a.:=[-n(4),3*n(5),11+n(6)];
        x := n(12).n(11).n(10).(a.k).n(6).n(7).j;
        x==6;
     */
    /*
    E.g. in
    a. := [-n(4),3*n(5),11+n(6)];k:=1;j:=2;
    x := n(12).n(11).n(10).(a.k).n(6).n(7).j;
   the parse tree is (n(n) written in)

                                x
                              /  \
                            x     j = 2
                          /  \
                        x     i7
                      /  \
                    x    i6
                  /  \
               x     a.k  = [0,3,6,9,12]
             /  \
           x     i10
         /  \
        i12  i11

        This is a very elaborate way to get a.1.2 == 6, requiring pretty much all of the machinery.
    */


    /**
     * Since this is a dyadic operation in the parser, the result will be a tree of objects of the form
     * <pre>
     *              x
     *            /  \
     *        ...     scalar
     *      x
     *    / \
     *   A   B
     *  </pre>
     *
     * @param state
     * @return
     */
    @Override
    public Object evaluate(State state) {
        return getOrSetValue(state, false, null);
    }

    public Object setValue(State state, Object newValue) {
        return getOrSetValue(state, true, newValue);
    }

    protected Object getOrSetValue(State state, boolean setValue, Object newValue) {
        getLeftArg().evaluate(state);
        getRightArg().evaluate(state);

        if (getRightArg().getResultType() == STEM_TYPE) {
            // See note above in comment block. Since we get the tree in the wrong order
            // (which is fine, since we require it evaluate from right to left and the parser
            // doesn't do that) we skip over everything until we finally have a scalar as
            // the right hand argument for the stem. This means the stem can start resolving.
            return null;
        }

        // Simplest case.
        if (getLeftArg().getResultType() == STEM_TYPE) {
            if (setValue) {
                StemVariable s = (StemVariable) getLeftArg().getResult();
                if (getRightArg().getResult() instanceof Long) {
                    s.put((Long) getRightArg().getResult(), newValue);
                } else {
                    String targetKey = null;
                    if(getRightArg().getResult() == null){
                        if(getRightArg() instanceof VariableNode){
                            VariableNode v = (VariableNode) getRightArg();
                            if(v.getResult() == null){
                                targetKey = v.getVariableReference();
                            }else{
                                targetKey = v.getResult().toString();
                            }
                        }else{
                            throw new IllegalArgumentException("error: could not determine key for stem");
                        }

                    }else{
                        targetKey = getRightArg().getResult().toString();
                    }
                    s.put(targetKey, newValue);
                }
                result = newValue;
            } else {
                result = doLeftSVCase(getLeftArg(), getRightArg(), state);
            }
            setResultType(Constant.getType(result));
            setEvaluated(true);
            return result;
        }
        // other case is that the left hand argumement is this class.
        if (!(getLeftArg() instanceof ExpressionStemNode)) {
            // This means the user passed in something as the left most argument that
            // cannot be a stem, e.g. (1).(2).
            throw new IllegalStateException("Error: left hand argument not a valid stem.");
        }

        StatementWithResultInterface swri = getLeftArg();
        StatementWithResultInterface lastSWRI = getRightArg();
        Object r = null;
        ExpressionStemNode esn = null;
        ArrayList<StatementWithResultInterface> indices = new ArrayList<>();

        while (swri instanceof ExpressionStemNode) {
            esn = (ExpressionStemNode) swri;
            indices.add(lastSWRI);

            if (esn.getRightArg().getResultType() == STEM_TYPE) {
                r = doLeftSVCase(esn.getRightArg(), indices, state);
                indices = new ArrayList<>();
            } else {
                r = esn.getRightArg().getResult();
            }
            esn.setResult(r);
            esn.setResultType(Constant.getType(r));
            esn.setEvaluated(true);
            lastSWRI = esn;
            swri = esn.getLeftArg();
        }

        StemVariable stemVariable = (StemVariable) esn.getLeftArg().getResult();
        Object r1 = null;
        if (setValue) {
            r1 = stemVariable.put(r.toString(), newValue);
        } else {
            r1 = stemVariable.get(r);
        }

        setResult(r1);
        setEvaluated(true);
        setResultType(Constant.getType(r1));
        return r1;

    }

    protected Object doLeftSVCase(StatementWithResultInterface leftArg, StatementWithResultInterface rightArg, State state) {
        List<StatementWithResultInterface> x = new ArrayList<>();
        x.add(rightArg);
        return doLeftSVCase(leftArg, x, state);
    }

    /**
     * Case that the left hand argument is a stem variable. This does the lookup.
     * In the variable case, a {@link StemMultiIndex} is created and interacts with
     * the {@link State} to do the resolutions.
     *
     * @param leftArg
     * @param state
     * @return
     */
    protected Object doLeftSVCase(StatementWithResultInterface leftArg, List<StatementWithResultInterface> indices, State state) {
        StemVariable stemLeft = (StemVariable) leftArg.getResult();
        String rawMI = "_"; // dummy name for stem
        for (StatementWithResultInterface rightArg : indices) {

            boolean gotOne = false;
            if (rightArg instanceof VariableNode) {
                VariableNode vn = (VariableNode) rightArg;
                if (vn.getResult() == null) {
                    // no such variable, so use name
                    StringTokenizer st = new StringTokenizer(vn.getVariableReference(), STEM_INDEX_MARKER);
                    while (st.hasMoreTokens()) {
                        String name = st.nextToken();
                        Object v = state.getValue(name);
                        if (v == null) {
                            rawMI = rawMI + STEM_INDEX_MARKER + name;
                        } else {
                            rawMI = rawMI + STEM_INDEX_MARKER + v;
                        }
                    }
                    gotOne = true;
                }
            }
            if (!gotOne && (rightArg.getResultType() == STRING_TYPE || rightArg.getResultType() == LONG_TYPE)) {
                rawMI = rawMI + STEM_INDEX_MARKER + rightArg.getResult().toString();
            } else {
                // do what?
                if (rightArg.getResultType() == STEM_TYPE) {
                    result = getLeftArg();
                    return result;
                }
            }


        }

        StemMultiIndex multiIndex = new StemMultiIndex(rawMI); // dummy variable
        return stemLeft.get(multiIndex);
    }

    List<String> sourceCode = new ArrayList<>();

    @Override
    public List<String> getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public StatementWithResultInterface makeCopy() {
        return null;
    }
}
