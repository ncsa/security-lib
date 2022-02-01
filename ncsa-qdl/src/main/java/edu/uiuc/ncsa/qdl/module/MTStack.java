package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XStack;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 12/13/21 at  7:17 AM
 */

public class MTStack<V extends MTTable2<? extends MTKey, ? extends Module>> extends XStack<V> implements Serializable {

    public MTStack() {
        pushNewTable();
    }

    @Override
    public XStack newInstance() {
        return new MTStack();
    }

    @Override
    public XTable newTableInstance() {
        return new MTTable2();
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        if (isEmpty()) {
            return;
        }
        xsw.writeStartElement(XMLConstants.MODULE_TEMPLATE_TAG);
        xsw.writeComment("Templates.");
        super.toXML(xsw);
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

    public void clearChangeList() {
        changeList = new ArrayList<>();
    }

    // On updates, the change list will track additions or replacements.
    // clear it before updates, read it it after, then clear it again.
    public List<MTKey> getChangeList() {
        return changeList;
    }

    public void setChangeList(List<MTKey> changeList) {
        this.changeList = changeList;
    }

    List<MTKey> changeList = new ArrayList<>();

    @Override
    public XThing put(XThing value) {
        changeList.add(((Module)value).getMTKey());
        return super.put(value);
    }

/*
    public V put(Module value) {
        changeList.add(value.getMTKey());
        return (V) super.put(value);
    }
*/
   public Module getModule(MTKey mtKey){
        return (Module) get(mtKey);
   }


}
