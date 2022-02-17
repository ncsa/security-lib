package edu.uiuc.ncsa.qdl.xml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/1/21 at  6:49 AM
 */
public interface XMLSerializable {
    void toXML(XMLStreamWriter xsw) throws XMLStreamException;
    void toXML(XMLStreamWriter xsw, SerializationObjects serializationObjects) throws XMLStreamException;

     void fromXML(XMLEventReader xer, SerializationObjects serializationObjects) throws XMLStreamException;
     void fromXML(XMLEventReader xer) throws XMLStreamException;
}
