package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XStack;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/22 at  6:14 AM
 */
public class VStack<V extends VTable<? extends XKey, ? extends VThing>> extends XStack<V> {
    @Override
    public XStack newInstance() {
        return new VStack();
    }

    @Override
    public XTable newTableInstance() {
        return new VTable();
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {

    }

    @Override
    public String getXMLStackTag() {
        return XMLConstants.FUNCTION_TABLE_STACK_TAG;
    }

    @Override
    public String getXMLTableTag() {
        return XMLConstants.FUNCTIONS_TAG;
    }

    @Override
    public void setStateStack(State state, XStack xStack) {
                //state.setSymbolStack(this);
    }

    @Override
    public XStack getStateStack(State state) {
        // return state.getSymbolStack();
        return null;
    }
}
