package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XStack;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
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
    public String getXMLStackTag() {
        return XMLConstants.TEMPLATE_STACK;
    }

    @Override
    public String getXMLTableTag() {
        return XMLConstants.MODULES_TAG;
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
          throw new NotImplementedException("implement version 1 serialization for new template stack");
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
        changeList.add(((Module) value).getMTKey());
        return super.put(value);
    }

    public Module getModule(MTKey mtKey) {
        return (Module) get(mtKey);
    }


}
