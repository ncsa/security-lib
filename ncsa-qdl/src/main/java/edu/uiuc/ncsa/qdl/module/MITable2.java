package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.functions.FKey;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.statements.Documentable;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.MODULE_TAG;

/**
 * Table of modules keyed by <b>alias</b>.
 * <p>Created by Jeff Gaynor<br>
 * on 12/1/21 at  1:03 PM
 */
public class MITable2<K extends XKey, V extends MIWrapper> extends XTable<K, V> implements Documentable {

 /*   @Override
    public V put(XThing value) {
        return put(value.getKey(), value);
    }*/

    public V put(XKey xKey, XThing xThing) {
        return super.put((K) xKey, (V) xThing);
    }

    @Override
    public void toXML(XMLStreamWriter xsw, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        for (XKey key : keySet()) {
            xsw.writeStartElement(getXMLElementTag());
            MIWrapper wrapper = get(key);
            Module module = wrapper.getModule();
            //if(serializationObjects.addInstance(module)) {
            //serializationObjects.addState(module.getState());
            //serializationObjects.stateMap.put(module.getState().getUuid(), module.getState());
            xsw.writeAttribute(XMLConstants.UUID_TAG, module.getId().toString());
            xsw.writeAttribute(XMLConstants.TEMPLATE_REFERENCE_TAG, module.getParentTemplateID().toString());
            xsw.writeAttribute(XMLConstants.MODULE_ALIAS_ATTR, key.getKey()); // What this was imported as
            xsw.writeAttribute(XMLConstants.STATE_REFERENCE_TAG, module.getState().getInternalID());
            //}
            xsw.writeEndElement(); // end module tag
        }
    }

    @Override
    public String getXMLTableTag() {
        return XMLConstants.MODULES_TAG;
    }

    @Override
    public String getXMLElementTag() {
        return MODULE_TAG;
    }

    @Override
    public V deserializeElement(XMLEventReader xer, XMLSerializationState XMLSerializationState, QDLInterpreter qi) throws XMLStreamException {
        XMLEvent xe = xer.peek();
        XMLUtils.ModuleAttributes moduleAttributes = XMLUtils.getModuleAttributes(xe);
        if(XMLSerializationState.processedInstance(moduleAttributes.uuid)){
            return (V) XMLSerializationState.getInstance(moduleAttributes.uuid);
        }
        if (!XMLSerializationState.processedTemplate(moduleAttributes.templateReference)) {
            throw new IllegalStateException("template '" + moduleAttributes.uuid + "' not found");
        }
        Module template = XMLSerializationState.getTemplate(moduleAttributes.templateReference);
        Module newInstance = template.newInstance(null);
        State state ;
        if(XMLSerializationState.processedState(moduleAttributes.stateReference)) {
            state = XMLSerializationState.getState(moduleAttributes.stateReference);
        }else{
            // edge case that the state does not yet exist, so create a new one, assuming that it
            // will get populated later.
            state = new State();
            state.setUuid(moduleAttributes.stateReference);
            XMLSerializationState.addState(state);
        }
        newInstance.setState(state);
        newInstance.setId(moduleAttributes.uuid);
        // no stash it with whatever it was stashed with
        MIWrapper miWrapper = new MIWrapper(new XKey(moduleAttributes.alias), newInstance);
        XMLSerializationState.addInstance(miWrapper);
        return (V) miWrapper;
    }


    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {

    }

    @Override
    public TreeSet<String> listFunctions(String regex) {
        return null;
    }

    @Override
    public List<String> listAllDocs() {
        return null;
    }

    @Override
    public List<String> listAllDocs(String functionName) {
        return null;
    }

    @Override
    public List<String> getDocumentation(String fName, int argCount) {
        return null;
    }

    @Override
    public List<String> getDocumentation(FKey key) {
        if (get(key) == null) {
            return new ArrayList<>(); // never null
        } else {
            return get(key).getModule().getListByTag();
        }
    }

    UUID id = UUID.randomUUID();

    @Override
    public UUID getID() {
        return id;
    }

}
