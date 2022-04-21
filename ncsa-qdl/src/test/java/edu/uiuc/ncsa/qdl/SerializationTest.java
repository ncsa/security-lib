package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;

import static edu.uiuc.ncsa.qdl.ModuleTest.testModulePath;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/18/22 at  6:08 AM
 */
public class SerializationTest extends AbstractQDLTester {


    /**
     * Most basic test. This is proof of concept and has all the parts done to test a single state object.
     *
     * @throws Throwable
     */
    public void testBasicState() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=3;");
        addLine(script, "c:=pi()^2567;");
        addLine(script, "b.:=[;5];");
        addLine(script, "d.:=n(2,3,4,n(24));");
        addLine(script, "ok := true;");
        addLine(script, "f(x)->x^2;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        state = pickleXMLState(state);
        script = new StringBuffer();

        addLine(script, "oka := a == 3;");
        addLine(script, "okb := reduce(@&&, b.==[;5]);");
        addLine(script, "okf2 := f(2)==4;");
        interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("oka", state);
        assert getBooleanValue("okb", state);
        assert getBooleanValue("okf2", state);
    }

    /**
     * Repeat of {@link ModuleTest#testMultipleNestedModules} but doing serialization in the middle of it.
     * This should exercise all the capabilities of serialization version 2.
     *
     * @throws Throwable
     */
    public void testModules() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','A'][module['b:b','B'][a:=1;f(x)->a*x^2;];module_import('b:b','X');module_import('b:b','Y');X#a:=2;Y#a:=5;g(x)->X#f(x)*Y#f(x);];");
        addLine(script, "module_import('a:a');");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // The state has been created and populated. Now we serialize it, then deserialize it.

        // Serialize the workspace
        state = pickleXMLState(state);

        // Test that the things we set are faithfully recreated
        script = new StringBuffer();
        addLine(script, "g3 := g(3);"); // g(x) ends up being 10*x^4
        addLine(script, "g1 := g(1);");
        addLine(script, "ok := 810 ==g3;");
        addLine(script, "ok1 := 10 ==g1;");
        interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "Expected g(3)==810, but got " + getLongValue("g3", state);
        assert getBooleanValue("ok1", state) : "Expected g(1)==10, but got " + getLongValue("g1", state);
    }

    /**
     * Test as per above but with Java serialization. Mostly Java serialization fails if something is not flagged as
     * serializable, so this is basically a test for that. We don't need to really hammer it like the XML case
     * since we don't control the serialization mechanism -- we are just a user of it.
     * @throws Throwable
     */
    public void testModulesJava() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','A'][module['b:b','B'][a:=1;f(x)->a*x^2;];module_import('b:b','X');module_import('b:b','Y');X#a:=2;Y#a:=5;g(x)->X#f(x)*Y#f(x);];");
        addLine(script, "module_import('a:a');");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // The state has been created and populated. Now we serialize it, then deserialize it.

        // Serialize the workspace
        state = pickleJavaState(state);

        // Test that the things we set are faithfully recreated
        script = new StringBuffer();
        addLine(script, "g3 := g(3);"); // g(x) ends up being 10*x^4
        addLine(script, "g1 := g(1);");
        addLine(script, "ok := 810 ==g3;");
        addLine(script, "ok1 := 10 ==g1;");
        interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "Expected g(3)==810, but got " + getLongValue("g3", state);
        assert getBooleanValue("ok1", state) : "Expected g(1)==10, but got " + getLongValue("g1", state);
    }

    /**
     * Repeat a test that reads a module from a file, serializes the workspace, deserializes it and
     * runs the rest of the test.
     *
     * @throws Throwable
     */
    public void testExternalModule() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module_load('" + testModulePath + "') =: q;");
        addLine(script, "module_import(q,'X');");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // The state has been created and populated. Now we serialize it, then deserialize it.
        state = pickleXMLState(state);

        script = new StringBuffer();
        addLine(script, "ok := 4 == X#get_private();");
        addLine(script, "X#set_private(-10);");
        addLine(script, "ok1 := -10 == X#get_private();");
        interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "Could not access getter for private variable.";
        assert getBooleanValue("ok1", state) : "Could not access getter for private variable.";
    }

    /**
     * Tests that embedding XML snippets in a function and variable does not break the serialization
     *
     * @throws Exception
     */
    public void testEmbeddedXML() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := '<foo>bar</bar>';");
        addLine(script, "b := '</baz></baz>';");
        addLine(script, "c := '<baz><baz>';");
        addLine(script, "d := 'woof]]>';"); // close tag of XML CDATA section
        addLine(script, "f(x)->'<![CDATA[[' + x + ']]>';");
        addLine(script, "module['a:a','A'][f(x)->'<![CDATA[[' + x + '2]]>';];");
        state = roundTripXMLSerialization(state, script);
        script = new StringBuffer();
        addLine(script, "okf := f('foo') == '<![CDATA[[foo]]>';");
        addLine(script, "oka := a=='<foo>bar</bar>';");
        addLine(script, "okb := b=='</baz></baz>';");
        addLine(script, "okc := c=='<baz><baz>';");
        addLine(script, "okd := d=='woof]]>';");
        addLine(script,"module_import('a:a');");
        addLine(script,"okmf := A#f('foo') == '<![CDATA[[foo2]]>';");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getBooleanValue("oka", state) : "open + close XML tag in variable fails.";
        assert getBooleanValue("okb", state) : "multiple close XML tags in variable fails.";
        assert getBooleanValue("okc", state) : "multiple open XML tags in variable fails.";
        assert getBooleanValue("okd", state) : "close CDATA XML tag in variable fails.";
        assert getBooleanValue("okf", state) : "embedded CDATA section in function fails.";
        assert getBooleanValue("okmf", state) : "embedded CDATA section in module function fails.";

    }

}
