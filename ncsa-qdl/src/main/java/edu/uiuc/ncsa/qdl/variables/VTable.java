package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/22 at  6:12 AM
 */
public class VTable<K extends XKey, V extends VThing> extends XTable<K,V> {
    @Override
    public void toXML(XMLStreamWriter xsw, XMLSerializationState XMLSerializationState) throws XMLStreamException {

    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {

    }

    @Override
    public String getXMLTableTag() {
        return XMLConstants.FUNCTIONS_TAG;
    }

    @Override
    public String getXMLElementTag() {
        return XMLConstants.FUNCTION_TAG;
    }

    @Override
    public V deserializeElement(XMLEventReader xer, XMLSerializationState XMLSerializationState, QDLInterpreter qi) throws XMLStreamException {
        return null;
    }

    @Override
    public String toJSONEntry(V xThing, XMLSerializationState xmlSerializationState) {
        return xThing.getKey().getKey() + ":=" + InputFormUtil.inputForm(xThing.getValue()) + ";";
    }

    @Override
    public String fromJSONEntry(String x, XMLSerializationState xmlSerializationState) {
        return x;
    }
}
