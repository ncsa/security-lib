package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;

import javax.xml.stream.*;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/18/22 at  6:08 AM
 */
public class SerializationTest extends AbstractQDLTester {
    /**
     * Create the {@link XMLStreamWriter}
     *
     * @param w
     * @return
     * @throws XMLStreamException
     */
    protected XMLStreamWriter createXSW(Writer w) throws XMLStreamException {
        return XMLOutputFactory.newInstance().createXMLStreamWriter(w);
    }

    protected XMLEventReader createXER(Reader reader) throws XMLStreamException {
        return XMLInputFactory.newInstance().createXMLEventReader(reader);
    }


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

        // The state has been created and populated. Now we serialize it, then deserialize it.
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xsw = createXSW(stringWriter);
        XMLSerializationState so = new XMLSerializationState();
        so.setVersion(XMLConstants.VERSION_2_0_TAG);
        state.toXML(xsw, so);

        // NB This does not reformat the XML so this implicitly tests for whitespace handling -- which is good.
        StringReader reader = new StringReader(stringWriter.toString());
        XMLEventReader xer = createXER(reader);
        xer.nextEvent(); // start iteration. the cursor is at the "<xml" tag, so skip that

        State state2 = new State();
        so = new XMLSerializationState();
        so.setVersion(XMLConstants.VERSION_2_0_TAG);
        state2.fromXML(xer, null, so);

        // Test that the things we set are faithfully recreated
        script = new StringBuffer();
        addLine(script, "oka := a == 3;");
        addLine(script, "okb := reduce(@&&, b.==[;5]);");
        addLine(script, "okf2 := f(2)==4;");
        interpreter = new QDLInterpreter(null, state2);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state2);
        assert getBooleanValue("oka", state2);
        assert getBooleanValue("okb", state2);
        assert getBooleanValue("okf2", state2);
    }

    /**
     * Repeat of {@link ModuleTest#testMultipleNestedModules} but doing serialization in the middle of it.
     * This should exercise all the capabilities of serialization version 2.
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
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xsw = createXSW(stringWriter);
        WorkspaceCommands workspaceCommands = new WorkspaceCommands();
        workspaceCommands.setState(state);
        workspaceCommands.toXML(xsw);

        // Deserialize the workspace
        //System.out.println(XMLUtils.prettyPrint(stringWriter.toString()));
        // Need pretty print.
        StringReader reader = new StringReader(XMLUtils.prettyPrint(stringWriter.toString()));
        XMLEventReader xer = createXER(reader);
        workspaceCommands.fromXML(xer);

        // Test that the things we set are faithfully recreated
        script = new StringBuffer();
        addLine(script, "g3 := g(3);"); // g(x) ends up being 10*x^4
        addLine(script, "g1 := g(1);");
        addLine(script, "ok := 810 ==g3;");
        addLine(script, "ok1 := 10 ==g1;");
        //interpreter = new QDLInterpreter(null, state2);
        //interpreter.execute(script.toString());
        workspaceCommands.getInterpreter().execute(script.toString());
        State state2 = workspaceCommands.getInterpreter().getState();
        assert getBooleanValue("ok", state2) : "Expected g(3)==810, but got " + getLongValue("g3", state2);
        assert getBooleanValue("ok1", state2) : "Expected g(1)==10, but got " + getLongValue("g1", state2);
    }
}
