package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.qdl.expressions.Assignment;
import edu.uiuc.ncsa.security.util.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.security.util.qdl.expressions.Dyad;
import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  10:57 AM
 */
public class StatementTest {
    TestUtils testUtils = TestUtils.newInstance();
    @Test
     public void testAssignment() throws Exception {
         SymbolTable st = testUtils.getTestSymbolTable();
         ConstantNode left = new ConstantNode(1L, Constant.LONG_TYPE);
         ConstantNode right = new ConstantNode(2L, Constant.LONG_TYPE);
         OpEvaluator opEvaluator = new OpEvaluator();

         Dyad dyad = new Dyad(opEvaluator, OpEvaluator.PLUS_VALUE, left, right);
         Assignment assignmentNode = new Assignment("A", dyad, st);
         assignmentNode.evaluate();
         assert st.isDefined("A");
         assert st.resolveValue("A").equals(3L);
     }
}
