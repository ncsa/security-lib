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
        addLine(script, "ok := {2,4}==a∩b;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testASCIIIntersection() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        addLine(script, "ok := {2,4}==a/\\b;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testUnion() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        addLine(script, "ok := {0,1,2,3,4,6}==a∪b;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    public void testASCIIUnion() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        addLine(script, "ok := {0,1,2,3,4,6}==(a\\/b);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    /**
     * Gratutious serialization test.
     * @throws Throwable
     */
    public void testDifference() throws Throwable {
        testDifference(false);
        testDifference(true);
    }

    protected void testDifference(boolean testXML) throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {0,1,2,3,4};");
        addLine(script, "b := {2,4,6};");
        QDLInterpreter interpreter;
        if (testXML) {
            state = roundTripStateSerialization(state, script);
            script = new StringBuffer();
        }
        addLine(script, "ok := {0,1,3}==(a/b);");
        addLine(script, "ok1 := {6}==(b/a);");

        interpreter = new QDLInterpreter(null, state);
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
        addLine(script, "ok := 2 ∈ a;");
        addLine(script, "ok1 := reduce(@&&, [0,1]∈a);"); // list can be used
        addLine(script, "ok2 := 7 ∉ a;");
        addLine(script, "ok3 := reduce(@&&, [8,6]∉a);"); // list can be used
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert getBooleanValue("ok3", state);
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

    public void testNested() throws Throwable {
        testNested(false);
        testNested(true);
    }

    /**
     * Sets do work when nested but only with scalar entries, since comparing
     * two stems, e.g., is very complex. Don't forget that asking something like
     * [1,2] == [1,2] return [true, true] since operations component wise.
     * @throws Throwable
     */
    protected void testNested(boolean testXML) throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := {{1,2},{3,4}};");
        addLine(script, "b := {{1,3},{2,4},{3,4}};");
        addLine(script, "ok := {{3,4}} == (a /\\ b);");
        addLine(script, "ok0 := a == a;");
        addLine(script, "ok1 := a==b;"); //false
        addLine(script, "ok2 := a != b;"); //true
        addLine(script, "ok3 := {{1,2}} == (a/b);");
        if (testXML) {
            state = roundTripStateSerialization(state, script);
            script = new StringBuffer();
        }
        addLine(script, "ok4 := {{1,3},{2,4}} == (b/a);");
        addLine(script, "ok5 := {{1,2},{1,3},{2,4}} == a%b;");//true
        addLine(script, "ok5a := {{1,2},{1,3},{2,4}} < a%b;");//false -- inclusion is strict
        addLine(script, "ok5b := {{1,2},{1,3},{2,4}} <= a%b;");//true
        addLine(script, "ok6 := {1,2} ∈ a;");
        addLine(script, "ok7 := {1,3} ∈ a;");//false
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("ok0", state);
        assert !getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert getBooleanValue("ok3", state);
        assert getBooleanValue("ok4", state);
        assert getBooleanValue("ok5", state);
        assert !getBooleanValue("ok5a", state);
        assert getBooleanValue("ok5b", state);
        assert getBooleanValue("ok6", state);
        assert !getBooleanValue("ok7", state);
    }

}
/*
      while[j∈{1,2,3,4,5}][a:=a*j;];

 */
