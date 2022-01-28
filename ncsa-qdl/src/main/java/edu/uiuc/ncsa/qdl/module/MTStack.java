package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XStack;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 12/13/21 at  7:17 AM
 */

public class MTStack<V extends MTTable2<? extends MTKey, ? extends Module>> extends XStack<V> implements Serializable {
    public boolean isTemplates() {
        return templates;
    }

    public void setTemplates(boolean templates) {
        this.templates = templates;
    }

    boolean templates = false;

    @Override
    public XStack newInstance() {
        return null;
    }

    @Override
    public XTable newTableInstance() {
        return null;
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        if (isEmpty()) {
            return;
        }
        xsw.writeStartElement(XMLConstants.MODULE_STACK_TAG);
        xsw.writeComment("Modules.");
        // lay these in in reverse order so we just have to read them in the fromXML method
        // and push them on the stack
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            getStack().get(i).toXML(xsw);
        }
        xsw.writeEndElement(); // end of tables.
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
        /*
        // points to stacks tag
        XMLEvent xe = xer.nextEvent(); // moves off the stacks tag.
        // no attributes or such with the stacks tag.
        boolean foundStack = false;
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        // Legacy case -- just a single functions block, not a stack.
                        case XMLConstants.MODULE_STACK_TAG:
                            if (foundStack) break; // if a stack is being processed, skip this
                            FTable functionTable1 = (FTable) qi.getState().getFTStack().peek();
                            functionTable1.fromXML(xer, qi);
                            break;
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(FUNCTION_TABLE_STACK_TAG)) {
                        return;
                    }
                    break;
            }
            xe = xer.nextEvent();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + FUNCTION_TABLE_STACK_TAG);
       */
    }
    //public class MTStack<V extends XThing>  extends HashMap<XKey, V> implements XTable<V> {

}
