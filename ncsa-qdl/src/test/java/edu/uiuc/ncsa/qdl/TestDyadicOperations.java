package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Dyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.VStack;
import edu.uiuc.ncsa.qdl.variables.VThing;

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
        state.getVStack().put(new VThing(new XKey("myStem."), stemVariable));
        ConstantNode left = new ConstantNode(new Long(3L), Constant.LONG_TYPE);
        VariableNode right = new VariableNode("myStem.");

        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        dyad.evaluate(state);
        assert dyad.getResultType() == Constant.STEM_TYPE;
        StemVariable result = (StemVariable) dyad.getResult();
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
        // A - B for strings. removes *every* occurrence of B found in A
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
        VStack vStack = state.getVStack();
        // String test

        VThing testValue = (VThing) vStack.get(new XKey("string"));
        VariableNode variableNode = new VariableNode("string");
        variableNode.evaluate(state);
        assert variableNode.getResult().equals(testValue.getValue());
        assert variableNode.getResultType() == Constant.STRING_TYPE;
        // random string test
        variableNode = new VariableNode("random.0");
        testValue = (VThing) vStack.get(new XKey("random."));
        variableNode.evaluate(state);
        assert variableNode.getResult().equals(testValue.getStemValue().get(0L));
        assert variableNode.getResultType() == Constant.STRING_TYPE;
        // Long-valued test
        variableNode = new VariableNode("long");
        testValue = (VThing) vStack.get(new XKey("long"));
        variableNode.evaluate(state);
        assert variableNode.getResult().equals(testValue.getLongValue());
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

    public void testLongEquality2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok0 := 4 == 5;");
        addLine(script, "ok1 := 4 != 5;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert !getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);
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

    public void testBDEquality2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok0 := 4.430000 == 4.43;");
        addLine(script, "ok1 := 4.430000 != 4.43;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert !getBooleanValue("ok1", state);
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

    public void testMixedEquality2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok0 := 4.000 == 4;");
        addLine(script, "ok1 := 4.000 != 4;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert !getBooleanValue("ok1", state);
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

    public void testLongComparison2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok0 := 4 < 5;");
        addLine(script, "ok1 := 4 > 5;");
        addLine(script, "ok2 := 4 <= 4;");
        addLine(script, "ok3 := 4 >= 4;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert !getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert getBooleanValue("ok3", state);
    }

    /**
     * Checks that different types return false from equality. Found a bug where things like
     * <pre>
     *     3.21 == 'a'
     *     false == 0
     * </pre>
     * would throw an illegal argument exceptions rather than return false
     *
     * @throws Throwable
     */
    public void testMixedUnequals() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x. := ['a','b','c',  'd','e',   1, 5,3,   4,  5,   true,false,false,true,true, null,null,null, null,'null',0.2,0.2,0.2,  0.2,0.2  ];");
        addLine(script, "y. := ['q', 2,  true,3.4, null,'a',2,true,3.4,null,'a', 2,    true, 3.4,  null,'a',  2,  true, 3.4,null,  'a', 2,   true,3.4,null];");
        addLine(script, "ok := !reduce(@∨, y.==x.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    /**
     * Similar to the previous one, this is needed to check that the contract for not equals
     * was not broken either at some point.
     *
     * @throws Throwable
     */
    public void testMixedUnequals2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x. := ['a','b','c',  'd','e',   1, 5,3,   4,  5,   true,false,false,true,true, null,null,null, null,'null',0.2,0.2,0.2,  0.2,0.2  ];");
        addLine(script, "y. := ['q', 2,  true,3.4, null,'a',2,true,3.4,null,'a', 2,    true, 3.4,  null,'a',  2,  true, 3.4,null,  'a', 2,   true,3.4,null];");
        addLine(script, "ok := reduce(@∧, y.!=x.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    /**
     * As of QDL 1.4.4 allow for chaining inequalities, like a &lt; b &lt; c &lt;==&gt; (a&lt;b) ∧ (b&lt;c)
     * Also allows for (in)equality too, so a &lt; b != c &lt; d &lt;==&gt; (a&lt;b)&&(b!=c)&&(c&lt;d)
     * <br/>
     *     Note that in the code, single cases like a &gt; b are handled independently of
     *     chained cases, so chaining should be tested separately.
     * @throws Throwable
     */
    public void testChainedConstantInequalties() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok := 2<=3 < 4 <=5;"); //true
        addLine(script, "ok0 := 2<=3 < 5 <=4;"); //false
        addLine(script, "x. := 2<[;5]<5;"); // mixed
        addLine(script, "ok1 := reduce(@&&, x. == [false,false,false,true,true]);");
        addLine(script, "y. := 2<[;5]<2;"); // all false
        addLine(script, "ok2 := reduce(@&&, y. == [false,false,false,false,false]);");
        // Now check for cases of embedded ==
        addLine(script, "ok3 := 2 < 3 == 3 <=4;"); //true
        addLine(script, "ok4 := 2 < 3 == 4 <=4;"); //false
        addLine(script, "z. := [;10]/5<tan(1)<tan(1.1);"); // mixed
        addLine(script, "ok5 := reduce(@&&, z. == [true,true,true,true,true,true,true,true,false,false]);");
        addLine(script, "w. := 1<[;10]/6<2;"); // mixed
        addLine(script, "ok6 := reduce(@&&, w. == [false,false,false,false,false,false,false,true,true,true]);");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert !getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert getBooleanValue("ok3", state);
        assert !getBooleanValue("ok4", state);
        assert getBooleanValue("ok5", state);
    }

    public void testChainedVariableInequalties() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=2;b:=3;c:=4;d:=5;s.:=[;6];");
        addLine(script, "ok0 := a < b < c;"); //true
        addLine(script, "ok1 := b>a<c;"); //true
        addLine(script, "ok2 := a<b!=c<d;"); //true
        addLine(script, "ok3 := a<b==c<d;"); //false
        addLine(script, "ok4 := b>a>c;"); //false
        addLine(script, "x. := a<s.<c;"); // mixed
        addLine(script, "ok5 := reduce(@&&, x. == [false,false,false,true,false,false]);");
        addLine(script, "y. := b>s.<c;"); // mixed
        addLine(script, "ok6 := reduce(@&&, y. == [true,true,true,false,false,false]);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert !getBooleanValue("ok3", state);
        assert !getBooleanValue("ok4", state);
        assert getBooleanValue("ok5", state);
        assert getBooleanValue("ok6", state);

    }

    /**
     * Very simple test that chained expressions are used correctly in a loop.
     * @throws Throwable
     */
    public void testChainedWhile() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "k := 0;");
        addLine(script, "while[0<=k<=3][k++;];");
        addLine(script, "ok := k==4;"); // has to exceed 3 to stop loop
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    /**
     * Very simple test that regexs can be chained. Part of the problem is coming up with a test that isn't
     * just daft/
     * @throws Throwable
     */
    public void testChainedRegex() throws Throwable {
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "ok := 'foo'!= '[a-zA-Z]{3}' =~ 'aBc' == 'aBc';");//silly but true
         addLine(script, "ok1:='[a-zA-Z]{3}' =~ 'aBc' =='p';"); // true for regex, false for ==
         addLine(script, "ok2:='[a-zA-Z]{3}' =~ 'aBcq' =='aBcq';"); // false for regex, true for ==
         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         interpreter.execute(script.toString());
         assert getBooleanValue("ok", state);
         assert !getBooleanValue("ok1", state);
         assert !getBooleanValue("ok2", state);
     }
}
