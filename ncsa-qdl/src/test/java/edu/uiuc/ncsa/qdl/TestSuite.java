package edu.uiuc.ncsa.qdl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Top-level test suite for this module.
 * <p>Created by Jeff Gaynor<br>
 * on 7/23/18 at  11:44 AM
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        QDLVariableTest.class,
        TestMonadicOperations.class,
        TestDyadicOperations.class,
        ExpressionTest.class,
        StatementTest.class,
        IOFunctionTest.class,
        StringFunctionTests.class,
        MathFunctionsTest.class,
        StemFunctionsTest.class,
        ParserTest.class,
        VFSTest.class
   
})
public class TestSuite extends junit.framework.TestSuite {
}
