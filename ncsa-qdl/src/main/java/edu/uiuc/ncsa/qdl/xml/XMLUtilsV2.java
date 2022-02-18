package edu.uiuc.ncsa.qdl.xml;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.MIStack;
import edu.uiuc.ncsa.qdl.module.MTStack;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.QDLModule;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StateUtils;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.UUID;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.*;

/**
 * XML Utilities for version 2 of serialization.
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/22 at  6:36 AM
 */
public class XMLUtilsV2 {

    public static void deserializeTemplateStore(XMLEventReader xer, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();

        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(MODULE_TAG)) {
                        // Have to get the attributes first because they are needed to determine which
                        // type of module to create.
                        XMLUtils.ModuleAttributes moduleAttributes = XMLUtils.getModuleAttributes(xe);
                        Module module = deserializeTemplate(xer, moduleAttributes, XMLSerializationState);
                        XMLSerializationState.addTemplate(module);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(MODULE_TEMPLATE_TAG)) {
                        return;
                    }
            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(TEMPLATE_STACK);
    }

    public static void deserializeStateStore(XMLEventReader xer, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();

        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(STATE_TAG)) {
                        // Have to get the attributes first because they are needed to determine which
                        // type of module to create.
                        StateAttributes stateAttributes = getStateAttributes(xe);
                        State state;
                        if (XMLSerializationState.processedState(stateAttributes.uuid)) {
                            state = XMLSerializationState.getState(stateAttributes.uuid);
                        } else {
                            state = StateUtils.newInstance();
                            state.setUuid(stateAttributes.uuid);
                            XMLSerializationState.addState(state);
                        }
                        StateUtils.load(state, XMLSerializationState, xer);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(STATES_TAG)) {
                        return;
                    }
            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(TEMPLATE_STACK);
    }

    /**
     * Deserializes a single template, either from sources or from the class reference.
     *
     * @param xer
     * @param moduleAttributes
     * @param XMLSerializationState
     * @return
     */
    public static Module deserializeTemplate(XMLEventReader xer, XMLUtils.ModuleAttributes moduleAttributes, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        Module module = null;

        if (moduleAttributes.isJavaModule()) {
            if (StringUtils.isTrivial(moduleAttributes.className)) {
                throw new IllegalStateException("Error: serialized java module without a classs -- cannot deserialize");
            }
            try {
                Class klasse = XMLUtilsV2.class.forName(moduleAttributes.className);
                module = ((JavaModule) klasse.newInstance()).newInstance(null); // this populates the functions and variables!!
                ((JavaModule) module).setClassName(moduleAttributes.className);
            } catch (Throwable t) {
                System.out.println("Warn: cannot deserialize class \"" + moduleAttributes.className + "\". Skipping. '" + t.getMessage() + "' (" + t.getClass().getName() + ")");
                //throw new DeserializationException("cannot deserialize class \"" + moduleAttributes.className + "\".", t);
            }

        } else {
            module = new QDLModule();
        }
        module.fromXML(xer, XMLSerializationState, true);
        module.setId(moduleAttributes.uuid);
        module.setAlias(moduleAttributes.alias);
        module.setNamespace(moduleAttributes.ns);
        module.setTemplate(true);
        module.setParentTemplateID(null);


        //  module.setDocumentation(StringUtils.stringToList(raw));

        // scorecard: at this point, enough information existed in the attributes to re-assemble the module
        // The cursor is still on this tag. Now send it to the module for further processing.
        //module.fromXML(xer, xp, qi);
        if (module instanceof JavaModule) {
            // call this at the right time -- after everything else has been done.
            ((JavaModule) module).init(null);
        }
        return module;
    }

    /**
     * Deserializes a template stack of references.
     *
     * @param xer
     * @param XMLSerializationState
     * @throws XMLStreamException
     */
    public static void deserializeTemplates(XMLEventReader xer, State state, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();// advance cursor
        MTStack mtStack = new MTStack();
        mtStack.fromXML(xer, XMLSerializationState);
        state.setMTemplates(mtStack);
    }

    public static void deserializeInstances(XMLEventReader xer, State state, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();// advance cursor
        MIStack miStack = new MIStack();
        miStack.fromXML(xer, XMLSerializationState);
        state.setMInstances(miStack);
    }

    public static class StateAttributes {
        public UUID uuid;
    }

    public static StateAttributes getStateAttributes(XMLEvent xe) {
        StateAttributes stateAttributes = new StateAttributes();
        Iterator iterator = xe.asStartElement().getAttributes(); // Use iterator since it tracks state
        while (iterator.hasNext()) {
            Attribute a = (Attribute) iterator.next();
            String v = a.getValue();
            switch (a.getName().getLocalPart()) {
                case UUID_TAG:
                    stateAttributes.uuid = UUID.fromString(v);
                    break;
            }
        }
        return stateAttributes;
    }
}
