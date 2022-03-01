package edu.uiuc.ncsa.qdl.state.legacy;

import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;
import static edu.uiuc.ncsa.qdl.xml.XMLConstants.*;

/**
 * The interface for access to symbols (aka variables) in QDL. Note that this is not assumed
 * to be namespace aware at all. It is not the task of this component to understand or resolve
 * namespaces.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  5:04 PM
 */
public abstract class AbstractSymbolTable implements SymbolTable {

    protected String getStemHead(String stem) {
        return getHead(stem, STEM_INDEX_MARKER);
    }

    protected String getStemTail(String stem) {
        return getTail(stem, STEM_INDEX_MARKER);
    }


    protected String getHead(String var, String delimiter) {
        return var.substring(0, var.indexOf(delimiter) + 1);
    }

    protected String getTail(String var, String delimiter) {
        return var.substring(var.indexOf(delimiter) + 1);

    }

    protected boolean isStem(String variable) {
        return variable.contains(STEM_INDEX_MARKER);
    }


    /**
     * This tests if the stem is compound like a.b.c
     *
     * @param var
     * @return
     */
    protected boolean isCompoundStem(String var) {
        return isStem(var) && (var.indexOf(STEM_INDEX_MARKER) != var.lastIndexOf(STEM_INDEX_MARKER));
    }

    /**
     * For the case of a stem like a. Returns false if it is of the form a.b. or some such.
     *
     * @param var
     * @return
     */
    protected boolean isTotalStem(String var) {
        return isStem(var) && var.endsWith(STEM_INDEX_MARKER) && !isCompoundStem(var);
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        // We actually DO want empty stacks since we need to preserve hierachies of variable states
        //  xsw.writeStartElement(STACK_TAG);
        xsw.writeStartElement(VARIABLES_TAG);
        for (Object key : getMap().keySet()) {
            xsw.writeStartElement(VARIABLE_TAG);
            xsw.writeAttribute(VARIABLE_NAME_TAG, key.toString());
            XMLUtils.write(xsw, getMap().get(key));
            xsw.writeEndElement();
        }
        xsw.writeEndElement();
    }

    @Override
    public void toXML(XMLStreamWriter xsw, XMLSerializationState XMLSerializationState) throws XMLStreamException {
            toXML(xsw);
    }

    @Override
    public void fromXML(XMLEventReader xer, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        // no attributes for this tag.
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        case VARIABLE_TAG:
                            varFromXML(xer);
                            break;
                        default:
                            return;
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(VARIABLES_TAG)) {
                        return;
                    }
                    break;
            }

            xe = xer.nextEvent();
        }
    }

    @Override
    public void fromXML(XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        // no attributes for this tag.
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        case VARIABLE_TAG:
                            varFromXML(xer);
                            break;
                        default:
                            return;
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(STACK_TAG)) {
                        return;
                    }
                    break;
            }

            xe = xer.nextEvent();
        }
    }

    protected void varFromXML(XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        String name = xe.asStartElement().getAttributeByName(new QName(VARIABLE_NAME_TAG)).getValue();

        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    setValue(name, XMLUtils.resolveConstant(xer));
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(VARIABLE_TAG)) {
                        return;
                    }
                    break;
            }
            xer.next();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + VARIABLE_TAG);

    }
}
