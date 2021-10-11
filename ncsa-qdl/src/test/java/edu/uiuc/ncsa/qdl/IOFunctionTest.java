package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.SystemEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  10:26 AM
 */
public class IOFunctionTest extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();

    public void scanExample() throws Exception {
        State state = testUtils.getNewState();

        // This has to be run in the CLIRunner class in this package, not
        // as a unit test, because jUnit will hang since it does not process input
        // right during testing.
        Polyad polyad = new Polyad(IOEvaluator.SCAN_FUNCTION);
        ConstantNode prompt = new ConstantNode("sayit>", Constant.STRING_TYPE);
        polyad.getArguments().add(prompt);
        polyad.evaluate(state);
        System.out.println("you entered:\"" + polyad.getResult() + "\"");
    }

     
    public void testSay() throws Exception {
        State state = testUtils.getNewState();
        Polyad polyad = new Polyad(SystemEvaluator.SAY_FUNCTION);
        String testString = "These are not the droids you are looking for";
        ConstantNode prompt = new ConstantNode(testString, Constant.STRING_TYPE);
        polyad.getArguments().add(prompt);
        polyad.evaluate(state);
        System.out.println("Check that the phrase \"" + testString + "\" was printed");
    }

    public void testIniFileRead() throws Throwable{
        String file = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/resources/sample.ini";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "out. := file_read('" + file + "', 2);"); // INIT files are type 2
        
        addLine(script, "ok0 := 'Fiona Smythe' == out.owner.name;");
        addLine(script, "ok1 := 'Big State University/Physics' == out.owner.organization.0;");
        addLine(script, "ok2 := 'Big State University/Astronomy' == out.owner.organization.1;");
        addLine(script, "ok3 := '192.168.1.42' == out.database.server;");
        addLine(script, "ok4 := 1032 == out.database.port + 3;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert getBooleanValue("ok3", state);
        assert getBooleanValue("ok4", state);

    }
    public void testIniFileWrite() throws Throwable{
        String rawIni = "{'owner':{'organization':['Big State University/Physics','Big State University/Astronomy'], 'name':'Fiona Smythe'}, 'database':{'server':'192.168.1.42', 'port':1029}}";
        String file = "/tmp/ini_test.ini";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        // Just tests round tripping
        addLine(script, "out0. := " + rawIni + ";");
        addLine(script, "file_write('" + file + "', out0.,  2);"); // INIT files are type 2
        addLine(script, "out. := file_read('" + file + "', 2);"); // INIT files are type 2

        addLine(script, "ok0 := 'Fiona Smythe' == out.owner.name;");
        addLine(script, "ok1 := 'Big State University/Physics' == out.owner.organization.0;");
        addLine(script, "ok2 := 'Big State University/Astronomy' == out.owner.organization.1;");
        addLine(script, "ok3 := '192.168.1.42' == out.database.server;");
        addLine(script, "ok4 := 1032 == out.database.port + 3;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert getBooleanValue("ok3", state);
        assert getBooleanValue("ok4", state);

    }
}
