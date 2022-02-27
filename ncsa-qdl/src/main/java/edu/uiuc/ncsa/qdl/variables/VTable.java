package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

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
        String raw = xThing.getKey().getKey() + ":=" + InputFormUtil.inputForm(xThing.getValue()) + ";";
        return Base64.encodeBase64URLSafeString(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String fromJSONEntry(String x, XMLSerializationState xmlSerializationState) {
        return new String(Base64.decodeBase64(x));
    }

    public Set<String> listVariables() {
        Set<String> vars = new HashSet<>();
        for(XKey xKey: keySet()){
               vars.add(xKey.getKey());
        }
        return vars;
    }
}
