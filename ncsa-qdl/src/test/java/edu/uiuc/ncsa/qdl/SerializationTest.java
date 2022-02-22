package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import net.sf.json.JSONArray;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/18/22 at  6:08 AM
 */
public class SerializationTest extends AbstractQDLTester{
    /**
     * Create the {@link XMLStreamWriter}
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
     * Very specific utility. It assumes that the writer is XML, formats it <i>as a string</i>
     * and creates a reader. This is because the XML is assumed to be written in this form
     * @param writer
     * @return
     * @throws Throwable
     */
    protected StringReader writerToReader(StringWriter writer) throws Throwable {
        String pp = XMLUtils.prettyPrint(writer.toString());
        return new StringReader(pp);
    }

    /**
     * Most basic test. This is proof of concept and has all the parts done to test a single state object.
     *
     * @throws Throwable
     */
    public void  testBasicState() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=3;");
        addLine(script, "c:=pi()^2567;");
        addLine(script, "b.:=[;5];");
        addLine(script, "d.:=n(2,3,4,n(24));");
        addLine(script, "f(x)->x^2;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        SymbolStack ss = state.getSymbolStack();
        JSONArray jsonArray = (JSONArray)ss.toJSON();
        ss.fromJSON(jsonArray);
        // The state has been created and populated. Now we serialize it, then deserialize it.
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xsw = createXSW(stringWriter);
        XMLSerializationState so = new XMLSerializationState();
        so.setVersion(XMLConstants.VERSION_2_0_TAG);
        state.toXML(xsw, so);
        // Whitespace does matter at least at this point. It should not, but tracking down the edge
        // cases is going to be a chore. Since we control what is written as the final form, this can wait.
        StringReader reader = writerToReader(stringWriter);
        XMLEventReader xer = createXER(reader);
        XMLEvent xe = xer.nextEvent(); // start iteration. the cursor is at the "<xml" tag, so skip that

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
        assert getBooleanValue("oka", state2);
        assert getBooleanValue("okb", state2);
        assert getBooleanValue("okf2", state2);
    }
}
