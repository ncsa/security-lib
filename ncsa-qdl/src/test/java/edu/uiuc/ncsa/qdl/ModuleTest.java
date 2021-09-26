package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.exceptions.IntrinsicViolation;
import edu.uiuc.ncsa.qdl.exceptions.NamespaceException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.math.BigDecimal;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/23/21 at  2:20 PM
 */
public class ModuleTest extends AbstractQDLTester {
    /**
     * Shows that importing module functions g and h outside a function body but referencing them
     * inside it will work: This means that, global functions can be used.
     *
     * @throws Throwable
     */

    public void testFunctionAndModules2() throws Throwable {
        String f_body = "(192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) + \n" +
                "     68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/\n" +
                "   (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) + \n" +
                "     280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 + \n" +
                "     384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4)";

        String g_x = "define[g(x)]body[return((x^2-1)/(x^4+1));];";
        String g_module = "module['a:a','a']body[" + g_x + "];";
        String h_y = "define[h(y)]body[return((3*y^3-2)/(4*y^2+y+1));];";
        String h_module = "module['b:b','b']body[" + h_y + "];";
        // import the modules outside of f and try to use them.
        String import_g = "module_import('a:a');";
        String import_h = "module_import('b:b');";
        String f_xy = "define[f(x,y)]body[return(" + f_body + ");];";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_x);
        addLine(script, g_module);
        addLine(script, h_module);
        addLine(script, import_g);
        addLine(script, import_h);
        addLine(script, f_xy);
        // It will ingest the function fine. It is attempting to use it later that will cause the error
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        // all that is set up. Now put in some values and try to evaluate it
        script = new StringBuffer();
        addLine(script, "x :=-3;");
        addLine(script, "y := 5;");
        addLine(script, "z := f(x,y);");
        //  try {
        interpreter.execute(script.toString());

        BigDecimal[] results = {
                new BigDecimal("0.224684095740375"),
                new BigDecimal("0.542433484968442"),
                new BigDecimal("1.22827852483025"),
                new BigDecimal("-0.454986621086766"),
                new BigDecimal("-0.749612627182112")
        };

        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "x :=-3/" + i + ";");
            addLine(script, "y := 5/" + i + ";");
            addLine(script, "z := f(x,y);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = getBDValue("z", state);
            assert areEqual(d, bd);
        }
    }
        /*
            module['b:b','b'][h(x)->(3*x^3-2)/(4*x^2+x+1);];
            module['a:a','a'][g(x)->(x^2-1)/(x^4+1);];
            define[f(x,y)][module_import('a:a'); module_import('b:b')
         */

    /**
     * Shows importing a module into the body of a function works.
     *
     * @throws Throwable
     */
    public void testFunctionAndModules_Good() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("0.224684095740375"),
                new BigDecimal("0.542433484968442"),
                new BigDecimal("1.22827852483025"),
                new BigDecimal("-0.454986621086766"),
                new BigDecimal("-0.749612627182112")
        };
        String f_body = "(192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) + \n" +
                "     68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/\n" +
                "   (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) + \n" +
                "     280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 + \n" +
                "     384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4)";

        String g_x = "define[g(x)]body[return((x^2-1)/(x^4+1));];";
        String g_module = "module['a:a','a']body[" + g_x + "];";
        String h_y = "define[h(y)]body[return((3*y^3-2)/(4*y^2+y+1));];";
        String h_module = "module['b:b','b']body[" + h_y + "];";
        String import_g = "module_import('a:a');";
        String import_h = "module_import('b:b');";
        String f_xy = "define[f(x,y)]body[" +
                import_g + "\n" +
                import_h + "\n" +
                "return(" + f_body + ");];";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_module);
        addLine(script, h_module);
        addLine(script, import_g);
        addLine(script, import_h);
        addLine(script, f_xy);
        addLine(script, "ok := !is_function(g,1);");// imported into f means not in session
        addLine(script, "ok2 := !is_function(h,1);");// imported into f means not in session
        // It will ingest the function fine. It is attempting to use it later that will cause the error
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getBooleanValue("ok", state);
        assert getBooleanValue("ok2", state);

        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "x :=-3/" + i + ";");
            addLine(script, "y := 5/" + i + ";");
            addLine(script, "z := f(x,y);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = getBDValue("z", state);
            assert areEqual(d, bd);
        }
    }

    /**
     * Import the same module several times and show that the state of each
     * is kept separate.
     *
     * @throws Throwable
     */

    public void testMultipleModuleImport() throws Throwable {
        String g_x = "define[g(x)]body[return(x+1);];";
        String h_y = "define[h(y)]body[return(y-1);];";
        String g_module = "module['a:a','a']body[q:=2;w:=3;" + g_x + h_y + "];";
        String import_g = "module_import('a:a');";
        String import_g1 = "module_import('a:a', 'b');";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_module);
        addLine(script, import_g);
        addLine(script, import_g1);
        addLine(script, "a#q:=10;");
        addLine(script, "b#q:=11;");
        addLine(script, "oka := a#q==10;");
        // have to stash the checks someplace other than the state object, since these are now
        // squirreled away in a module entry's state.
        addLine(script, "okb := b#q==11;");
        // It will ingest the function fine. It is attempting to use it later that will cause the error
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("oka", state);
        assert getBooleanValue("okb", state);
    }

    /**
     * Make modules with the same variables, import then use NS qualification on the stem and its
     * indices to access them.
     *
     * @throws Throwable
     */
    public void testNSAndStem() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','a']body[i:=2;j:=3;list. := -10 + n(5);];");
        addLine(script, "module['a:b','b']body[i:=1;j:=4;list. := -20 + n(5);];");
        addLine(script, "i:=0;");
        addLine(script, "j:=5;");
        addLine(script, "list. := n(10);");
        addLine(script, "module_import('a:a');");
        addLine(script, "module_import('a:b');");
        addLine(script, "d := a#list.b#i;");// Should resolve index og b#i to 1.
        addLine(script, "e := b#list.i;");// should resolve i to 1 since it is in the module.
        addLine(script, "okd := d == -9;");
        addLine(script, "oke := e == -19;");


        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("okd", state);
        assert getBooleanValue("oke", state);
    }

    /**
     * If a variable is in a module and that module is imported, you should be able
     * to access the variable without a namespace if it has been imported and there
     * are no clashes
     *
     * @throws Throwable
     */
    public void testNSAndVariableResolution() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','a']body[i:=2;list. := -10 + n(5);];");
        addLine(script, "module['a:b','b']body[j:=4;list2. := -20 + n(5);];");
        addLine(script, "module_import('a:a');");
        addLine(script, "module_import('a:b');");
        addLine(script, "p := i;");
        addLine(script, "q := list.0;");
        addLine(script, "r := j;");
        addLine(script, "s := list2.0;");
        // Note that if we want to change the value, we need to qualify it still, however.
        // Since an unqualified name gets created in the local state, not the module.
        // May want to revisit how this is done in the design though....
        addLine(script, "a#i := 5;");


        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        Long p = getLongValue("p", state);
        Long q = getLongValue("q", state);
        Long r = getLongValue("r", state);
        Long s = getLongValue("s", state);
        Long i = getLongValue("i", state);
        assert q.equals(-10L);
        assert s.equals(-20l);
        assert p.equals(2L);
        assert r.equals(4L);
        assert i.equals(5L);
    }

    /**
     * In this case, modules have, of course, unique namespaces, but the aliases conflict so that is
     * changed in import.
     *
     * @throws Throwable
     */

    public void testImportAndAlias() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','a']body[i:=2;j:=3;list. := -10 + n(5);];");
        addLine(script, "module['b:b','b']body[i:=1;j:=4;list. := -20 + n(5);];");
        addLine(script, "module['a:b','b']body[i:=1;j:=4;list. := n(5);];");
        addLine(script, "i:=0;");
        addLine(script, "j:=5;");
        addLine(script, "list. := n(10);");
        addLine(script, "module_import('a:a');");
        addLine(script, "module_import('b:b');");
        addLine(script, "module_import('a:b', 'd');");
        addLine(script, "d := d#list.b#i;");
        addLine(script, "e := b#list.d#i;");


        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        Long d = getLongValue("d", state);
        Long e = getLongValue("e", state);
        assert d.equals(1L);
        assert e.equals(-19l);
    }

    /**
     * Create a module, then import it to another module. The variables should be resolvable transitively
     * and the states should all be separate.
     *
     * @throws Throwable
     */

    public void testNestedVariableImport() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/a','a']body[q:=1;];");
        addLine(script, "module_import('a:/a');");
        addLine(script, "module_import('a:/a','b');");
        addLine(script, "module['q:/q','w']body[module_import('a:/a');zz:=a#q+2;];");
        addLine(script, "a#q:=10;");
        addLine(script, "b#q:=11;");
        // Make sure that some of the state has changed to detect state management issues.
        addLine(script, "module_import('q:/q');");
        addLine(script, "w#a#q:=3;");
        addLine(script, "okw := w#a#q==3;");
        addLine(script, "okaq := a#q==10;");
        addLine(script, "okbq := b#q==11;");

        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("okw", state);
        assert getBooleanValue("okaq", state);
        assert getBooleanValue("okbq", state);
    }
  /*
     module['a:/a','a']body[q:=1;];module_import('a:/a');module_import('a:/a','b')
     module['q:/q','w']body[module_import('a:/a');zz:=a#q+2;];
     a#q:=10;b#q:=11
     module_import('q:/q')
     w#a#q := -2
     w#zz
   */

    /*
    Define a function then define variants in modules.  Values are checked to track whether the state
    gets corrupted. This can be put into a QDL workspace to check manually:

        define[f(x)]body[return(x+100);];
        module['a:/t','a']body[define[f(x)]body[return(x+1);];];
        module['q:/z','w']body[module_import('a:/t');define[g(x)]body[return(a#f(x)+3);];];
        module_import('q:/z');
        w#a#f(3)
        w#g(2)
     */

    public void testNestedFunctionImport() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "define[f(x)]body[return(x+100);];");
        addLine(script, "module['a:/t','a']body[define[f(x)]body[return(x+1);];];");
        addLine(script, "module['q:/z','w']body[module_import('a:/t');define[g(x)]body[return(a#f(x)+3);];];");
        addLine(script, "test_f:=f(1);");
        addLine(script, "module_import('a:/t');");
        addLine(script, "test_a:=a#f(1);");
        // Make sure that some of the state has changed to detect state management issues.
        addLine(script, "module_import('q:/z');");
        addLine(script, "test_waf := w#a#f(2);");
        addLine(script, "test_wg := w#g(2);");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("test_f", state) == 101L;
        assert getLongValue("test_a", state) == 2L;
        assert getLongValue("test_waf", state) == 3L;
        assert getLongValue("test_wg", state) == 6L;

    }

    /**
     * Case of a simple module. The point is that a function f, is defined in the module and that
     * another module function g calls it. What should happen (assuming no other definitions of these
     * in the state, which would throw a namespace exception) is that g uses f and that is
     * that. If the module functions are not being added only to the module state, then there would be errors.
     * Critical simple use case.
     * <pre>
     *     module['a:a','a'][f(x)->x^2;g(x)->f(x+1);];
     *     module_import('a:a');
     *     g(1);
     *  4
     * </pre>
     *
     * @throws Throwable
     */
    public void testModuleFunctionVisibility() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " module['a:a','a'][f(x)->x^2;g(x)->f(x+1);];");
        addLine(script, "module_import('a:a');");
        addLine(script, "ok := !is_function(f,1);"); // f didn't end up outside the module
        addLine(script, "ok1 := 4 == g(1);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        StemVariable stem = getStemValue("x.", state);
        assert getBooleanValue("ok1", state);
        assert getBooleanValue("ok", state);
    }

    /**
     * Same as above, with spaces.
     *
     * @throws Throwable
     */
    public void testModuleFunctionVisibilitySpaces() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " module   \n[\n'a:a',\n'a'\n]   \n\n[\n  f(x)->x^2;g(x)->f(x+1);  \n]   \n\n;");
        addLine(script, "module_import('a:a');");
        addLine(script, "y := g(1);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        StemVariable stem = getStemValue("x.", state);
        assert getLongValue("y", state).equals(4L);
    }

    /**
     * Tests that a function inside a module defined in terms of another inner function is resolved
     * right.
     *
     * @throws Throwable
     */

    public void testFunctionVisibility() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "f(x)->x;");
        addLine(script, " module['a:a','a'][f(x)->x^2;g(x)->f(x+1);];");
        addLine(script, "module_import('a:a');");
        addLine(script, "ok := 16 == g(3);"); // uses f inside the module
        addLine(script, "okf := 2 == f(2);"); // does not effect f outside
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("okf", state);
    }
        /*
        module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];
         */

    /**
     * Test that importing two modules keeps the namespaces straight with their state.
     *
     * @throws Throwable
     */
    public void testMultipleModules() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "X#u := 5;X#v := 7;");
        addLine(script, "okxf := 31 == X#f(2,3);"); // uses f inside the module
        addLine(script, "okxg := 12 ==  X#g();"); // uses f inside the module
        addLine(script, "module_import('a:/b','Y');");
        addLine(script, "Y#u := -2; Y#v := -3;");
        addLine(script, "okyg := Y#g() == -5;");
        addLine(script, "okyf := Y#f(3,4) == -18;");
        addLine(script, "okxg2 := X#g() == 12;");
        addLine(script, "okxf2 := X#f(3,4) == 43;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("okxf", state);
        assert getBooleanValue("okxg", state);
        assert getBooleanValue("okyf", state);
        assert getBooleanValue("okyg", state);
        assert getBooleanValue("okxf2", state);
        assert getBooleanValue("okxg2", state);
    }

    /**
     * Makes sure that importing different module versions are kept straight for input form,
     * so if <code>X</code> and <code>Y</code> are two versions of the same module. Setting state and
     * querying functions works
     *
     * <pre>
     *
     * </pre>
     *
     * @throws Throwable
     */
    public void testModuleInputForm() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "X#u:=42;");
        addLine(script, "xf0 :=input_form(X#f,2);");
        addLine(script, "module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)/times(y,v);g()->u+v;];");
        addLine(script, "module_import('a:/b','Y');");
        addLine(script, "Y#u :=-7;");
        addLine(script, "yf :=input_form(Y#f,2);");
        addLine(script, "yu :=input_form(Y#u);");
        addLine(script, "xf :=input_form(X#f,2);");
        addLine(script, "xu :=input_form(X#u);");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("yf", state).contains("times(x,u)/times(y,v)");
        assert getStringValue("xf", state).contains("times(x,u)+times(y,v)");
        assert getStringValue("xu", state).contains("42");
        assert getStringValue("yu", state).contains("-7");

    }

    /**
     * Test that creating a module inside another module works completely locally.
     * <pre>
     *   module['a:a','A'][module['b:b','B'][u:=2;f(x)->x+1;];module_import('b:b');];
     * </pre>
     */
    public void testNestedModule() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','A'][module['b:b','B'][u:=2;f(x)->x+1;];module_import('b:b');];");
        addLine(script, "module_import('a:a');");
        addLine(script, "-11 =:  A#B#u;");
        addLine(script, "ok := -11 == A#B#u;"); // pull it out of the local state so we can test the value easily.

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "was not able to nest a module definition in another module.";

    }

    public void testUnqualifiedVariable() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "ok := u == 2;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "was not able to get unqualified variable name";
    }

    /**
     * Import 2 modules, repeat test. Should get a namespace exception.
     *
     * @throws Throwable
     */
    public void testUnqualifiedVariable2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "module_import('a:/b','Y');");
        addLine(script, "ok := u == 2;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "multiple namespaces with same variable failed unqualified test.";
        } catch (NamespaceException nsx) {
            assert true;
        }
    }

    public void testUnqualifiedFunction() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "ok := f(2,2) == 10;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "was not able to get unqualified function name.";
    }

    public void testUnqualifiedFunction2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][u := 2;v := 3;times(x,y)->x*y;f(x,y)->times(x,u)+times(y,v);g()->u+v;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "module_import('a:/b','Y');");
        addLine(script, "ok := f(2,2) == 10;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "multiple namespaces with same funtions failed unqualified test.";
        } catch (NamespaceException nsx) {
            assert true;
        }
    }
    /*
      Intrinsic variable tests
     */
    public void testIntrinsicFunction() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][__f(x,y)->x*y;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "__f(2,2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "was able to access an intrinsic function.";
        }catch(UndefinedFunctionException iv){
            assert true;
        }
    }
    public void testIntrinsicFunction2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][__f(x,y)->x*y;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "X#__f(2,2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "was able to access an intrinsic function.";
        }catch(IntrinsicViolation iv){
            assert true;
        }
    }
    public void testIntrinsicFunctionDefine() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][__f(x,y)->x*y;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "X#__f(x,y)->x*y;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "was able to access an intrinsic function.";
        }catch(IntrinsicViolation iv){
            assert true;
        }
    }

    public void testIntrinsicVariable() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][__a:=1;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "say(X#__a);"); // FQ
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "was able to access an intrinsic variable.";
        }catch(IntrinsicViolation iv){
            assert true;
        }
    }

    public void testIntrinsicVariable1() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][__a:=1;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "say(__a);"); // unqualified
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "was able to access an intrinsic variable.";
        }catch(IntrinsicViolation iv){
            assert true;
        }
    }

    public void testIntrinsicVariableSet() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/b','X'][__a:=1;];");
        addLine(script, "module_import('a:/b','X');");
        addLine(script, "X#__a := 42;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "was able to access an intrinsic variable.";
        }catch(IntrinsicViolation iv){
            assert true;
        }
    }
    /*
    The next set of tests is pretty much the same as the previous set, except that it does done on a QDL module
    loaded from disk. This is because the structure of the State is a little different when loading a module
    (vs. running a module) and is special cased in the VariableState
     */
    protected String testModulePath = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/modules/test.mdl";
    // ML = module_load
    public void testMLIntrinsicFunction() throws Throwable {
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "module_load('" + testModulePath + "') =: q;");
          addLine(script, "module_import(q,'X');");
          addLine(script, "__f(2,2);");
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          try {
              interpreter.execute(script.toString());
              assert false : "was able to access an intrinsic function.";
          }catch(UndefinedFunctionException iv){
              assert true;
          }
      }
      public void testMLIntrinsicFunction2() throws Throwable {
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "module_load('" + testModulePath + "') =: q;");
          addLine(script, "module_import(q,'X');");
          addLine(script, "X#__f(2,2);");
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          try {
              interpreter.execute(script.toString());
              assert false : "was able to access an intrinsic function.";
          }catch(IntrinsicViolation iv){
              assert true;
          }
      }
      public void testMLIntrinsicFunctionDefine() throws Throwable {
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "module_load('" + testModulePath + "') =: q;");
          addLine(script, "module_import(q,'X');");
          addLine(script, "X#__f(x,y)->x*y;");
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          try {
              interpreter.execute(script.toString());
              assert false : "was able to access an intrinsic function.";
          }catch(IntrinsicViolation iv){
              assert true;
          }
      }

      public void testMLIntrinsicVariable() throws Throwable {
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "module_load('" + testModulePath + "') =: q;");
          addLine(script, "module_import(q,'X');");
          addLine(script, "say(X#__a);"); // FQ
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          try {
              interpreter.execute(script.toString());
              assert false : "was able to access an intrinsic variable.";
          }catch(IntrinsicViolation iv){
              assert true;
          }
      }

      public void testMLIntrinsicVariable1() throws Throwable {
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "module_load('" + testModulePath + "') =: q;");
          addLine(script, "module_import(q,'X');");
          addLine(script, "say(__a);"); // unqualified
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          try {
              interpreter.execute(script.toString());
              assert false : "was able to access an intrinsic variable.";
          }catch(IntrinsicViolation iv){
              assert true;
          }
      }

      public void testMLIntrinsicVariableSet() throws Throwable {
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "module_load('" + testModulePath + "') =: q;");
          addLine(script, "module_import(q,'X');");
          addLine(script, "X#__a := 42;");
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          try {
              interpreter.execute(script.toString());
              assert false : "was able to access an intrinsic variable.";
          }catch(IntrinsicViolation iv){
              assert true;
          }
      }
}
