package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.statements.Documentable;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.util.Map;

/**
 * A symbol table. This should hold functions modules and (eventually) variables.
 * Sequences of these are managed by {@link XStack}.
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/21 at  5:14 AM
 */
public interface XTable<V extends QDLStateThing> extends Map<String, V>, Cloneable, Serializable, Documentable {
    /**
     * Should add the {@link QDLStateThing} based on its {@link QDLStateThing#getName()} as the key.
     * @param value
     * @return
     */
    V put(QDLStateThing value);

    void toXML(XMLStreamWriter xsw) throws XMLStreamException;

    void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException;

    boolean containsKey(String key, int startTableIndex);
}
