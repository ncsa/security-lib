package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/7/22 at  1:51 PM
 */
public class SetTest extends AbstractQDLTester {
    public void testSetCreate() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok := {0,1,2,3} == to_set([;4]);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testSetToList() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := ~{0,1,2,3};"); // convert to a list
        addLine(script, "ok := reduce(@&&, [0,1,2,3] == a.);"); // convert to a list
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testSetInclusion() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3};");
        addLine(script, "ok0 := {1,2}<a;");
        addLine(script, "ok1 := a>{1,2};");
        addLine(script, "ok2 := a<={1,2};");
        addLine(script, "ok3 := a=={1,2};");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);
        assert !getBooleanValue("ok2", state);
        assert !getBooleanValue("ok3", state);
    }

    public void testIntersection() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        addLine(script, "ok := {2,4}==(a&&b);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testUnion() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        addLine(script, "ok := {0,1,2,3,4,6}==(a||b);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testDifference() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        addLine(script, "ok := {0,1,3}==(a/b);");
        addLine(script, "ok1 := {6}==(b/a);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("ok1", state);
    }
    public void testSymmetricDifference() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        addLine(script, "ok := {0,1,3,6}==(a%b);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testMemberOf() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "ok := 2∈a;");
        addLine(script, "ok1 := reduce(@&&, [0,1]∈a);"); // list can be used
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("ok1", state);
    }
    public void testSize() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "ok := size(a) == 5;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testSubset() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok := {0,-2} == subset((x)->x<3, {-2,0,4,5});");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testReduce() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok := 120 == reduce(@*, {1,2,3,4,5});");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testLoop() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 6;");
        addLine(script, "while[j∈{1,2,3,4,5}][a:=a*j;];");
        addLine(script, "ok := a== 720;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

}
/*
      while[j∈{1,2,3,4,5}][a:=a*j;];

 */
