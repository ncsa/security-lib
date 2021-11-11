package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.statements.Documentable;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/21 at  5:14 AM
 */
public interface XTable<V extends QDLStateThing> extends Cloneable, Serializable, Documentable {
    V put(V value);

    V get(String key);


    List<V> getAll();

    void toXML(XMLStreamWriter xsw) throws XMLStreamException;

    void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException;

    boolean isEmpty();

    int size();

    boolean isDefined(String key);
    boolean isDefined(String key, int startTableIndex);
}
