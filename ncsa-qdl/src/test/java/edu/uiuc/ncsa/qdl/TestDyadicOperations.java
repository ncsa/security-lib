package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Dyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.math.BigDecimal;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  4:07 PM
 */
public class TestDyadicOperations extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();

     
    public void testDyadicBDPlus() throws Exception {
        ConstantNode left = new ConstantNode(new BigDecimal("123.456"), Constant.DECIMAL_TYPE);
        ConstantNode right = new ConstantNode(new BigDecimal("-123.4560000"), Constant.DECIMAL_TYPE);
        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResultType() == Constant.DECIMAL_TYPE;
        assert ((BigDecimal) dyad.getResult()).compareTo(BigDecimal.ZERO) == 0;
    }

     
    public void testDyadicBDMinus() throws Exception {
        ConstantNode left = new ConstantNode(new BigDecimal("123.456"), Constant.DECIMAL_TYPE);
        ConstantNode right = new ConstantNode(new BigDecimal("123.056"), Constant.DECIMAL_TYPE);
        BigDecimal expectedResult = new BigDecimal((".4"));
        Dyad dyad = new Dyad(OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResultType() == Constant.DECIMAL_TYPE;
        BigDecimal returnedResult = (BigDecimal) dyad.getResult();
        assert (returnedResult.subtract(expectedResult)).compareTo(BigDecimal.ZERO) == 0;
    }


     
    public void testDyadicMixedMinus() throws Exception {
        ConstantNode left = new ConstantNode(new BigDecimal("123.456"), Constant.DECIMAL_TYPE);
        ConstantNode right = new ConstantNode(new Long(23L), Constant.LONG_TYPE);
        BigDecimal expectedResult = new BigDecimal(("100.456"));
        Dyad dyad = new Dyad(OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResultType() == Constant.DECIMAL_TYPE;
        BigDecimal returnedResult = (BigDecimal) dyad.getResult();
        assert (returnedResult.subtract(expectedResult)).compareTo(BigDecimal.ZERO) == 0;
    }

     
    public void testDyadicMixedPlus() throws Exception {
        ConstantNode left = new ConstantNode(new BigDecimal("123.456"), Constant.DECIMAL_TYPE);
        ConstantNode right = new ConstantNode(new Long(23L), Constant.LONG_TYPE);
        BigDecimal expectedResult = new BigDecimal(("146.456"));
        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResultType() == Constant.DECIMAL_TYPE;
        BigDecimal returnedResult = (BigDecimal) dyad.getResult();
        assert (returnedResult.subtract(expectedResult)).compareTo(BigDecimal.ZERO) == 0;
    }

     
    public void testStemScalarPlus() throws Exception {
        State state = testUtils.getNewState();
        StemVariable stemVariable = new StemVariable();
        stemVariable.put("foo", 1L);
        stemVariable.put("bar", new BigDecimal("345.432"));
        stemVariable.put("baz", -34L);
        stemVariable.put("3", new BigDecimal("-123.987"));
        state.getSymbolStack().setStemVariable("myStem.", stemVariable);
        ConstantNode left = new ConstantNode(new Long(3L), Constant.LONG_TYPE);
        VariableNode right = new VariableNode("myStem.");

        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate(state);
        assert dyad.getResultType() == Constant.STEM_TYPE;
        StemVariable result = (StemVariable)dyad.getResult();
        assert result.get("foo").equals(4L);
        assert result.get("baz").equals(-31L);
        assert testNumberEquals(result.get("bar"), new BigDecimal("348.432"));
        assert testNumberEquals(result.get("3"), new BigDecimal("-120.987"));
    }

     
    public void testDyadicLongPlus() throws Exception {
        ConstantNode left = new ConstantNode(1L, Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(2L, Constant.LONG_TYPE);
        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResult().equals(3L);
    }

     
    public void testDyadicLongMinus() throws Exception {
        ConstantNode left = new ConstantNode(1L, Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(2L, Constant.LONG_TYPE);
        Dyad dyad = new Dyad(OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResult().equals(-1L);
    }

     
    public void testDyadicStringPlus() throws Exception {
        ConstantNode left = new ConstantNode("abc", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("def", Constant.STRING_TYPE);
        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResult().equals("abcdef");
    }

     
    public void testDyadicStringMinus() throws Exception {
        ConstantNode left = new ConstantNode("abcdef", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("def", Constant.STRING_TYPE);
        Dyad dyad = new Dyad(OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResult().equals("abc");
    }

     
    public void testDyadicStringMinus2() throws Exception {
        // A - B for strings. removes *every* occurance of B found in A
        /// here abcabdeabf - ab = cdef
        ConstantNode left = new ConstantNode("abcabdeabf", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("ab", Constant.STRING_TYPE);
        Dyad dyad = new Dyad(OpEvaluator.MINUS_VALUE, left, right);
        dyad.evaluate(testUtils.getNewState());
        assert dyad.getResult().equals("cdef");
    }

    /**
     * tests that we can create variable nodes, evaluate them and get back the expected results.
     *
     * @throws Exception
     */
     
    public void testVariableExpression() throws Exception {
        State state = testUtils.getTestState();
        SymbolTable st = state.getSymbolStack();
        // String test
        String testValue = (String) st.resolveValue("string");
        VariableNode variableNode = new VariableNode("string");
        variableNode.evaluate(state);
        assert variableNode.getResult().equals(testValue);
        assert variableNode.getResultType() == Constant.STRING_TYPE;
        // random string test
        variableNode = new VariableNode("random.0");
        testValue = (String) st.resolveValue("random.0");
        variableNode.evaluate(state);
        assert variableNode.getResult().equals(testValue);
        assert variableNode.getResultType() == Constant.STRING_TYPE;
        // Long-valued test
        variableNode = new VariableNode("long");
        Long testLong = (Long) st.resolveValue("long");
        variableNode.evaluate(state);
        assert variableNode.getResult().equals(testLong);
        assert variableNode.getResultType() == Constant.LONG_TYPE;
    }

     
    public void testLongEquality() throws Exception {
        ConstantNode left = new ConstantNode(new Long(4), Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(new Long(5), Constant.LONG_TYPE);
        State state = testUtils.getNewState();
        Dyad dyad = new Dyad(OpEvaluator.EQUALS_VALUE, left, right);
        dyad.evaluate(state);
        assert !(Boolean) dyad.getResult();
        dyad = new Dyad(OpEvaluator.NOT_EQUAL_VALUE, left, right);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
    }

     
    public void testBDEquality() throws Exception {
        ConstantNode left = new ConstantNode(new BigDecimal("4.43000000"), Constant.DECIMAL_TYPE);
        ConstantNode right = new ConstantNode(new BigDecimal("4.43"), Constant.DECIMAL_TYPE);
        State state = testUtils.getNewState();
        Dyad dyad = new Dyad(OpEvaluator.EQUALS_VALUE, left, right);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
        dyad = new Dyad(OpEvaluator.NOT_EQUAL_VALUE, left, right);
        dyad.evaluate(state);
        assert !(Boolean) dyad.getResult();
    }

     
    public void testMixedEquality() throws Exception {
        ConstantNode left = new ConstantNode(new BigDecimal("4.000000"), Constant.DECIMAL_TYPE);
        ConstantNode right = new ConstantNode(new Long(4L), Constant.DECIMAL_TYPE);
        State state = testUtils.getNewState();
        Dyad dyad = new Dyad(OpEvaluator.EQUALS_VALUE, left, right);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
        dyad = new Dyad(OpEvaluator.NOT_EQUAL_VALUE, left, right);
        dyad.evaluate(state);
        assert !(Boolean) dyad.getResult();
    }

     
    public void testStringEquality() throws Exception {
        ConstantNode left = new ConstantNode("little bunny foo foo", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("It was a dark and stormy night", Constant.STRING_TYPE);
        State state = testUtils.getNewState();
        Dyad dyad = new Dyad(OpEvaluator.EQUALS_VALUE, left, right);
        dyad.evaluate(state);
        assert !(Boolean) dyad.getResult();
        dyad = new Dyad(OpEvaluator.NOT_EQUAL_VALUE, left, right);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
        // And check that a string is equal to itself. 
        dyad = new Dyad(OpEvaluator.EQUALS_VALUE, left, left);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
    }

     
    public void testLongComparison() throws Exception {
        ConstantNode left = new ConstantNode(new Long(4), Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(new Long(5), Constant.LONG_TYPE);
        State state = testUtils.getNewState();
        // test 4 < 5
        Dyad dyad = new Dyad(OpEvaluator.LESS_THAN_VALUE, left, right);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
        // test 4 > 5
        dyad = new Dyad(OpEvaluator.MORE_THAN_VALUE, left, right);
        dyad.evaluate(state);
        assert !(Boolean) dyad.getResult();
        // test 4 <= 4
        dyad = new Dyad(OpEvaluator.LESS_THAN_EQUAL_VALUE, left, left);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
        // test 4 >= 4
        dyad = new Dyad(OpEvaluator.MORE_THAN_EQUAL_VALUE, left, left);
        dyad.evaluate(state);
        assert (Boolean) dyad.getResult();
    }


}
