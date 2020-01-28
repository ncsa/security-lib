package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.expressions.Assignment;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Dyad;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  10:57 AM
 */
public class StatementTest {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testAssignment() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable st = state.getSymbolStack();

        ConstantNode left = new ConstantNode(1L, Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(2L, Constant.LONG_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();

        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        Assignment assignmentNode = new Assignment("A", dyad);
        assignmentNode.evaluate(state);
        assert st.isDefined("A");
        assert st.resolveValue("A").equals(3L);
    }
}
