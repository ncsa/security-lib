package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.functions.FKey;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.statements.Documentable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

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


    public V put(XKey xKey, XThing xThing) {
        return super.put((K) xKey, (V) xThing);
    }

    @Override
    public void toXML(XMLStreamWriter xsw, XMLSerializationState xmlSerializationState) throws XMLStreamException {
        for (XKey key : keySet()) {
            xsw.writeStartElement(getXMLElementTag());
            MIWrapper wrapper = get(key);
            Module module = wrapper.getModule();
            xsw.writeAttribute(XMLConstants.UUID_TAG, module.getId().toString());
            xsw.writeAttribute(XMLConstants.TEMPLATE_REFERENCE_TAG, module.getParentTemplateID().toString());
            xsw.writeAttribute(XMLConstants.MODULE_ALIAS_ATTR, key.getKey()); // What this was imported as
            xsw.writeAttribute(XMLConstants.STATE_REFERENCE_TAG, module.getState().getInternalID());
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

    public V deserializeElement(XMLUtils.ModuleAttributes moduleAttributes, XMLSerializationState xmlSerializationState) {
        if (xmlSerializationState.processedInstance(moduleAttributes.uuid)) {
            return (V) xmlSerializationState.getInstance(moduleAttributes.uuid);
        }
        if (!xmlSerializationState.processedTemplate(moduleAttributes.templateReference)) {
            throw new IllegalStateException("template '" + moduleAttributes.uuid + "' not found");
        }
        Module template = xmlSerializationState.getTemplate(moduleAttributes.templateReference);
        Module newInstance = template.newInstance(null);
        State state;
        if (xmlSerializationState.processedState(moduleAttributes.stateReference)) {
            state = xmlSerializationState.getState(moduleAttributes.stateReference);
        } else {
            // edge case that the state does not yet exist, so create a new one, assuming that it
            // will get populated later.
            state = new State();
            state.setUuid(moduleAttributes.stateReference);
            xmlSerializationState.addState(state);
        }
        newInstance.setState(state);
        newInstance.setId(moduleAttributes.uuid);
        // now stash it with whatever it was stashed with
        MIWrapper miWrapper = new MIWrapper(new XKey(moduleAttributes.alias), newInstance);
        xmlSerializationState.addInstance(miWrapper);
        return (V) miWrapper;
    }

    @Override
    public V deserializeElement(XMLEventReader xer, XMLSerializationState xmlSerializationState, QDLInterpreter qi) throws XMLStreamException {
        XMLEvent xe = xer.peek();
        XMLUtils.ModuleAttributes moduleAttributes = XMLUtils.getModuleAttributes(xe);
        return deserializeElement(moduleAttributes, xmlSerializationState);
    }


    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
        throw new NotImplementedException("implement me for XML version 1 legacy support.");
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

    @Override
    public String toJSONEntry(V wrapper, XMLSerializationState xmlSerializationState) {
        Module module = wrapper.getModule();
        K key = (K) wrapper.getKey();
        XMLUtils.ModuleAttributes moduleAttributes = new XMLUtils.ModuleAttributes();
        moduleAttributes.uuid = module.getId();
        moduleAttributes.alias = key.getKey();
        moduleAttributes.templateReference = module.parentTemplateID;
        moduleAttributes.stateReference = module.getState().getUuid();

        return moduleAttributes.toJSON().toString();
    }


    @Override
    public String fromJSONEntry(String x, XMLSerializationState xmlSerializationState) {
        XMLUtils.ModuleAttributes moduleAttributes = new XMLUtils.ModuleAttributes();
        moduleAttributes.fromJSON(x);
        V m = deserializeElement(moduleAttributes, xmlSerializationState);
        put(new XKey(moduleAttributes.alias), m);
        if(m.getModule() instanceof JavaModule){
            ((JavaModule)m.getModule()).init(m.getModule().getState(), true);
        }
        return null; // this returns what the interpreter should process. Nothing in this case.
    }
}
