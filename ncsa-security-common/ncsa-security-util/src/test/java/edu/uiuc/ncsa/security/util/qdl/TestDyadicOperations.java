package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.TestBase;
import edu.uiuc.ncsa.security.util.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.security.util.qdl.expressions.Dyad;
import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  4:07 PM
 */
public class TestDyadicOperations extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testDyadicLongPlus() throws Exception {
        ConstantNode left = new ConstantNode(1L, Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(2L, Constant.LONG_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate();
        assert dyad.getResult().equals(3L);
    }

    @Test
    public void testDyadicLongMinus() throws Exception {
        ConstantNode left = new ConstantNode(1L, Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(2L, Constant.LONG_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate();
        assert dyad.getResult().equals(-1L);
    }

    @Test
    public void testDyadicStringPlus() throws Exception {
        ConstantNode left = new ConstantNode("abc", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("def", Constant.STRING_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate();
        assert dyad.getResult().equals("abcdef");
    }

    @Test
    public void testDyadicStringMinus() throws Exception {
        ConstantNode left = new ConstantNode("abcdef", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("def", Constant.STRING_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate();
        assert dyad.getResult().equals("abc");
    }

    @Test
    public void testDyadicStringMinus2() throws Exception {
        // A - B for strings. removes *every* occurance of B found in A
        /// here abcabdeabf - ab = cdef
        ConstantNode left = new ConstantNode("abcabdeabf", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("ab", Constant.STRING_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate();
        assert dyad.getResult().equals("cdef");
    }

    /**
     * tests that we can create variable nodes, evaluate them and get back the expected results.
     *
     * @throws Exception
     */
    @Test
    public void testVariableExpression() throws Exception {
        SymbolTable st = testUtils.getTestSymbolTable();
        // String test
        String testValue = (String) st.resolveValue("string");
        VariableNode variableNode = new VariableNode("string", st);
        variableNode.evaluate();
        assert variableNode.getResult().equals(testValue);
        assert variableNode.getResultType() == Constant.STRING_TYPE;
        // random string test
        variableNode = new VariableNode("random.0", st);
        testValue = (String) st.resolveValue("random.0");
        variableNode.evaluate();
        assert variableNode.getResult().equals(testValue);
        assert variableNode.getResultType() == Constant.STRING_TYPE;
        // Long-valued test
        variableNode = new VariableNode("long", st);
        Long testLong = (Long) st.resolveValue("long");
        variableNode.evaluate();
        assert variableNode.getResult().equals(testLong);
        assert variableNode.getResultType() == Constant.LONG_TYPE;
    }

    @Test
    public void testLongEquality() throws Exception {
        ConstantNode left = new ConstantNode(new Long(4), Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(new Long(5), Constant.LONG_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.EQUALS_VALUE, left, right);
        dyad.evaluate();
        assert !(Boolean) dyad.getResult();
        dyad = new Dyad(opEvaluator, OpEvaluator.NOT_EQUAL_VALUE, left, right);
        dyad.evaluate();
        assert (Boolean) dyad.getResult();

    }

    @Test
    public void testStringEquality() throws Exception {
        ConstantNode left = new ConstantNode("little bunny foo foo", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("It was a dark and stormy night", Constant.STRING_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.EQUALS_VALUE, left, right);
        dyad.evaluate();
        assert !(Boolean) dyad.getResult();
        dyad = new Dyad(opEvaluator, OpEvaluator.NOT_EQUAL_VALUE, left, right);
        dyad.evaluate();
        assert (Boolean) dyad.getResult();
        // And check that a string is equal to itself. 
        dyad = new Dyad(opEvaluator, OpEvaluator.EQUALS_VALUE, left, left);
        dyad.evaluate();
        assert (Boolean) dyad.getResult();
    }

    @Test
    public void testLongComparison() throws Exception {
        ConstantNode left = new ConstantNode(new Long(4), Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(new Long(5), Constant.LONG_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();
        // test 4 < 5
        Dyad dyad = new Dyad(opEvaluator, OpEvaluator.LESS_THAN_VALUE, left, right);
        dyad.evaluate();
        assert (Boolean) dyad.getResult();
        // test 4 > 5
        dyad = new Dyad(opEvaluator, OpEvaluator.MORE_THAN_VALUE, left, right);
        dyad.evaluate();
        assert !(Boolean) dyad.getResult();
        // test 4 <= 4
        dyad = new Dyad(opEvaluator, OpEvaluator.LESS_THAN_EQUAL_VALUE, left, left);
        dyad.evaluate();
        assert (Boolean) dyad.getResult();
        // test 4 >= 4
        dyad = new Dyad(opEvaluator, OpEvaluator.MORE_THAN_EQUAL_VALUE, left, left);
        dyad.evaluate();
        assert (Boolean) dyad.getResult();
    }


}
