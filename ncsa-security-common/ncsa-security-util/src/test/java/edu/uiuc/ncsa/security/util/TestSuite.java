package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.qdl.*;
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
        StemFunctionsTest.class
    /*    TemplateTest.class,
        FunctorParserTest.class,
        JFunctorFactoryTests.class,
        JFunctorTest.class,
        JSONPreprocessorTest.class,
        CacheTest.class,
        EditorTest.class */
})
public class TestSuite extends junit.framework.TestSuite {
}
