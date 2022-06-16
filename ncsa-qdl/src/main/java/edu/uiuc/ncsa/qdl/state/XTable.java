package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * A symbol table. This should hold functions modules and (eventually) variables.
 * Sequences of these are managed by {@link XStack}.
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/21 at  5:14 AM
 */
public abstract class XTable<K extends XKey, V extends XThing> extends HashMap<K, V> implements Cloneable, Serializable {
    protected boolean FDOC_CONVERT = true; // allows for older workspaces to be read. Remove this later
    protected String convertFDOC(String x){
        return x.replace(">>", "»");
    }

    /**
     * Should add the {@link XThing} based on its {@link XThing#getName()} as the key.
     *
     * @param value
     * @return
     */
        public V put(XThing value) {
            return put((K) value.getKey(), (V) value);
        }

    /**
     * @deprecated
     * @param xsw
     * @param XMLSerializationState
     * @throws XMLStreamException
     */
    public abstract void toXML(XMLStreamWriter xsw, XMLSerializationState XMLSerializationState) throws XMLStreamException;

    public abstract String toJSONEntry(V xThing, XMLSerializationState xmlSerializationState);
    public abstract String fromJSONEntry(String x, XMLSerializationState xmlSerializationState);

    /**
     * @deprecated
     * @param xer
     * @param qi
     * @throws XMLStreamException
     */
    public abstract void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException;
    /**
     * Version 2.0 serialization
     * @param xer
     * @param XMLSerializationState
     * @throws XMLStreamException
     */

    public void fromXML(XMLEventReader xer, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(getXMLElementTag())) {
                        put(deserializeElement(xer, XMLSerializationState, null)); // no interpreter needed
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(getXMLTableTag())) {
                        return;
                    }
            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(getXMLTableTag());
    }

    /**
     * @deprecated
     * @return
     */
    public abstract String getXMLTableTag();

    /**
     * @deprecated 
     * @return
     */
    public abstract String getXMLElementTag();

    /**
     * @deprecated 
     * @param xer
     * @param XMLSerializationState
     * @param qi
     * @return
     * @throws XMLStreamException
     */
    public abstract V deserializeElement(XMLEventReader xer, XMLSerializationState XMLSerializationState, QDLInterpreter qi) throws XMLStreamException;


    UUID uuid = UUID.randomUUID();

    public UUID getID() {
        return uuid;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "uuid=" + uuid +
                "size=" + size() +
                '}';
    }
}
