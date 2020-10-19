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
        GlomTest.class,
        // Without the VFS tests, all other tests (134 of them) take 784 ms. (av. 5.85 ms per test)
        // Running this next test adds a full 2 seconds for the initial database connection
        // and unzipping.
        // Point is that this is quite fast since pretty much every test creates a parser and executes it.
        VFSTest.class

})
public class TestSuite extends junit.framework.TestSuite {
}
