package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.functions.FKey;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.statements.Documentable;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * Table of modules keyed by <b>alias</b>.
 * <p>Created by Jeff Gaynor<br>
 * on 12/1/21 at  1:03 PM
 */
public class MITable2<K extends XKey, V extends MIWrapper> extends HashMap<K, V> implements XTable<K, V>, Documentable {
    @Override
    public V put(XThing value) {
        return put(value.getKey(), value);
    }

    @Override
    public V put(XKey xKey, XThing xThing) {
        return super.put( (K)xKey,  (V)xThing);
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        if (isEmpty()) {
            return;
        }
        for (XKey key : keySet()) {
            get(key).getModule().toXML(xsw, null);
        }
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {

    }

    @Override
    public TreeSet<String> listFunctions(String regex) {
        return null;
    }

    @Override
    public List<String> listAllDocs() {
        return null;
    }

    @Override
    public List<String> listAllDocs(String functionName) {
        return null;
    }

    @Override
    public List<String> getDocumentation(String fName, int argCount) {
        return null;
    }

    @Override
    public List<String> getDocumentation(FKey key) {
        if (get(key) == null) {
             return new ArrayList<>(); // never null
         } else {
             return get(key).getModule().getDocumentation();
         }
    }


}
