package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/26/21 at  12:34 PM
 */
public class ComparisonDyad extends Dyad {
    public ComparisonDyad(int operatorType) {
        super(operatorType);
    }

    public ComparisonDyad(int operatorType, ExpressionNode leftNode, ExpressionNode rightNode) {
        super(operatorType, leftNode, rightNode);
    }

    @Override
    public Object evaluate(State state) {
        return newEvaluate(state);
    }


    protected Object newEvaluate(State state) {
        boolean anyCD = isLeftArgCD() || (getRightArgument() instanceof ComparisonDyad);
        if (isOpEquality() && anyCD ) {
              Object r = handleEquals(state);
              setResult(r);
              setResultType(Constant.getType(r));
              setEvaluated(true);
              return r;
          }
        if (!anyCD) {
            // handles simple case and stops descent
            Object obj = super.evaluate(state);
            leftmostNode = getLeftArgument();
            return obj;
        }
        // At this point we do 3 passes -- I'm sure that can be improved
        // 1. re-order the nodes since the parser has them in opposite from execution order
        // 2. evaluate each pair of nodes. This requires some surgery to get the arguments
        // 3. And the results from 2.
        // Doing it all at once is makes for very messy code, but if performance is
        // an issue (how often are there lots of huge chained expressions like this?)
        // then think about it more.
        List<ComparisonDyad> nodes = new ArrayList<>();
        ComparisonDyad currentLeft = (ComparisonDyad) getLeftArgument();
        leftmostNode = null;
        while (leftmostNode == null) {
            // March from right to left
            // Can't evaluate since the parser returns these in reverse order.
            nodes.add(0, currentLeft);
            if (currentLeft.isLeftArgCD()) {
                currentLeft = (ComparisonDyad) currentLeft.getLeftArgument();
            } else {
                leftmostNode = currentLeft.getLeftArgument();
            }
        }
        leftmostNode.evaluate(state);

        ComparisonDyad oldLeft = nodes.get(0);
        List<Dyad> evaluatedDyads = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            currentLeft = nodes.get(i);
            Dyad dyad = new Dyad(currentLeft.getOperatorType());
            if (i == 0) {
                dyad.setLeftArgument(oldLeft.getLeftArgument());
            } else {
                dyad.setLeftArgument(oldLeft.getRightArgument());
            }
            dyad.setRightArgument(currentLeft.getRightArgument());
  //          System.out.println(dyad.getLeftArgument() + " " + dyad.getOperatorType() + " " + dyad.getRightArgument());
            dyad.evaluate(state);
            evaluatedDyads.add(0, dyad);
        }
        Dyad dyad = new Dyad(getOperatorType());
        dyad.setLeftArgument(currentLeft.getRightArgument());
        dyad.setRightArgument(getRightArgument());
  //      System.out.println(dyad.getLeftArgument() + " " + dyad.getOperatorType() + " " + dyad.getRightArgument());
        dyad.evaluate(state);
        evaluatedDyads.add(dyad); // Last one in the list.

        // Compute the result -- and each previous result which may be a scalar (boolean) or stem of them.
        // TODO -- in scalar only case, do short-circuit, but that will require a bunch of bookkeeping earlier.
        // There is some trickery here: By letting the dyad do the operation, if there are embedded
        // stems (like 1 < [;10]/6 < 2)  then the scalars are seamless converted to default values of stems
        // so there is never subsetting.
        Object actualResult = Boolean.TRUE;
        for (int i = 0; i < evaluatedDyads.size(); i++) {
            dyad = evaluatedDyads.get(i);
            Dyad and = new Dyad(OpEvaluator.AND_VALUE);
            and.setLeftArgument(new ConstantNode(actualResult));
            and.setRightArgument(new ConstantNode(dyad.getResult()));
            actualResult = and.evaluate(state);
        }
        setResult(actualResult);
        setResultType(Constant.getType(actualResult));
        setEvaluated(true);
        return actualResult;
    }


    protected boolean isLeftArgCD() {
        if (getLeftArgument() == null) {
            return false;
        }
        return getLeftArgument() instanceof ComparisonDyad;
    }

    protected ComparisonDyad getLeftCD() {
        return (ComparisonDyad) getLeftArgument();
    }

    protected boolean isOpEquality() {
        return getOperatorType() == OpEvaluator.EQUALS_VALUE
                || getOperatorType() == OpEvaluator.NOT_EQUAL_VALUE
                || getOperatorType() == OpEvaluator.REGEX_MATCH_VALUE;
    }

    /**
     * In this case, we have A == B or A != B. Complications is that A or B may be comparison dyads.
     * So there are 4 cases to handle
     *
     * @param state
     * @return
     */
    protected Object handleEquals(State state) {
        Dyad dyad = new Dyad(getOperatorType());
        Object leftResult = Boolean.TRUE;
        Object rightResult = Boolean.TRUE;

        if (getLeftArgument() instanceof ComparisonDyad) {
            ComparisonDyad cdLeft = getLeftCD();
            leftResult =  cdLeft.evaluate(state);
            dyad.setLeftArgument(cdLeft.getRightArgument());
        } else {
            dyad.setLeftArgument(getLeftArgument());
        }
        if (getRightArgument() instanceof ComparisonDyad) {
            ComparisonDyad cdRight = (ComparisonDyad) getRightArgument();
            rightResult =  cdRight.evaluate(state);
            dyad.setRightArgument(cdRight.leftmostNode());
        } else {
            dyad.setRightArgument(getRightArgument());
        }
    //    System.out.println(dyad.getLeftArgument() + " " + dyad.getOperatorType() + " " + dyad.getRightArgument());
        Object r = dyad.evaluate(state);
        // Now we logically AND this with the previous results.
        // Left && this && right <==> (left && this) && right
        Dyad and0 = new Dyad(OpEvaluator.AND_VALUE);
        and0.setLeftArgument(new ConstantNode(leftResult));
        and0.setRightArgument(new ConstantNode(r));
        and0.evaluate(state);

        Dyad and1 = new Dyad(OpEvaluator.AND_VALUE);
        and1.setLeftArgument(new ConstantNode(and0.getResult()));
        and1.setRightArgument(new ConstantNode(rightResult));
        Object actualValue = and1.evaluate(state);

        return actualValue;
    }

    StatementWithResultInterface leftmostNode = null;

    protected StatementWithResultInterface leftmostNode() {
        return leftmostNode;
    }
}
