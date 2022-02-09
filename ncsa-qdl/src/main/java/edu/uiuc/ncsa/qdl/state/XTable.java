package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
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
    /**
     * Should add the {@link XThing} based on its {@link XThing#getName()} as the key.
     *
     * @param value
     * @return
     */
        public V put(XThing value) {
            return put((K) value.getKey(), (V) value);
        }

    public abstract void toXML(XMLStreamWriter xsw) throws XMLStreamException;

    public abstract void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException;

    UUID uuid = UUID.randomUUID();

    public UUID getID() {
        return uuid;
    }
}
