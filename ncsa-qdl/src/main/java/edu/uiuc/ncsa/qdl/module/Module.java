package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import net.sf.json.JSONArray;
import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:03 AM
 */
public abstract class Module implements XThing, Serializable {
    @Override
    public String getName() {
        return getAlias();
    }

    XKey key = null;

    @Override
    public XKey getKey() {
        if (key == null) {
            key = new XKey(getName());
        }
        return key;
    }

    MTKey mtKey = null;

    public MTKey getMTKey() {
        if (mtKey == null) {
            mtKey = new MTKey(getNamespace());
        }
        return mtKey;
    }

    /**
     * This returns true only if the module is from another language than a QDL module.
     *
     * @return
     */
    public boolean isExternal() {
        return false;
    }

    /**
     * The system will mark loaded modules as template.
     *
     * @return
     */
    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    boolean template = false;

    public Module() {
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    State state;

    public Module(URI namespace, String alias, State state) {
        this.state = state;
        this.alias = alias;
        this.namespace = namespace;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    String alias;


    public URI getNamespace() {
        return namespace;
    }

    public void setNamespace(URI namespace) {
        this.namespace = namespace;
    }

    URI namespace;

    public JSONArray toJSON() {
        JSONArray array = new JSONArray();
        array.add(getNamespace().toString());
        array.add(alias);
        return array;
    }

    public void fromJSON(JSONArray array) {
        namespace = URI.create(array.getString(0));
        alias = array.getString(1);
    }

    @Override
    public String toString() {
        return "Module{" +
                ", namespace=" + namespace +
                ", alias='" + alias + '\'' +
                (isExternal() ? ", isJava" : "") +
                '}';
    }

    /**
     * Modules are effectively templates. This passes in the state of the parser at the point a new
     * module is required and the contract is to create a new instance of this module with the state.
     * Note that the state passed in may have nothing to do with the state here. You are creating
     * a new module for the given state using this as a template.  <br/><br/>
     * All implementations should gracefully handle a null state with the assumption that
     * the full state will be set later. This is because of bootstrapping networks of modules
     * during deserialization.
     *
     * @param state
     * @return
     */
    public abstract Module newInstance(State state);

    /**
     * Called in {@link #newInstance(State)} to finish setting up the module for things like
     * serialization. When you write {@link #newInstance(State)} the very last thing you should
     * do is invoke this on your new module.
     * @param module
     */
    protected void setupModule(Module module){
        module.setParentTemplateID(getId());
    }

    public void toXML(XMLStreamWriter xsw, String alias) throws XMLStreamException {

    }

    public void toXML(XMLStreamWriter xsw,
                      String alias,
                      boolean fullSerialization,
                      XMLSerializationState XMLSerializationState) throws XMLStreamException {
        xsw.writeStartElement(MODULE_TAG);

        if (fullSerialization) {
            xsw.writeAttribute(XMLConstants.MODULE_NS_ATTR, getNamespace().toString());
            xsw.writeAttribute(XMLConstants.MODULE_ALIAS_ATTR, StringUtils.isTrivial(alias) ? getAlias() : alias);
            xsw.writeAttribute(XMLConstants.UUID_TAG, getId().toString());
            writeExtraXMLAttributes(xsw);
            State state = getState();
            // if(state != null) {
            if (!(isTemplate() || state == null)) {
                if (XMLSerializationState.addState(state)) {
                    state.toXML(xsw, XMLSerializationState);
                }
                //serializationObjects.stateMap.put(state.getUuid(), state);
            }

        } else {
            xsw.writeAttribute(XMLConstants.UUID_TAG, getId().toString());
            if (!(isTemplate() || state == null)) {
                xsw.writeAttribute(XMLConstants.STATE_REFERENCE_TAG, getState().getUuid().toString());
                XMLSerializationState.stateMap.put(getState().getUuid(), getState());
            }

        }
        // Note there is documentation in the source code, but since we save the source,
        // there is no need to serialize it (or anything else in the source for that matter).
        writeExtraXMLElements(xsw); // Do this first so they get read first later on deserialization

        xsw.writeEndElement();
    }

    /**
     * Add any attributes you want to the module tag (you must read them later).
     *
     * @param xsw
     * @throws XMLStreamException
     */
    public void writeExtraXMLAttributes(XMLStreamWriter xsw) throws XMLStreamException {

    }

    /**
     * Write extra elements. You must control for the opening and closing tags. These are
     * inserted right after the state element.
     *
     * @param xsw
     * @throws XMLStreamException
     */
    public void writeExtraXMLElements(XMLStreamWriter xsw) throws XMLStreamException {
    }

    public boolean FDOC_CONVERT = true;
    /**
     * Used in version 2,0 serialization
     *
     * @param xer
     * @param XMLSerializationState
     * @param isTemplate
     * @throws XMLStreamException
     */
    public void fromXML(XMLEventReader xer, XMLSerializationState XMLSerializationState, boolean isTemplate) throws XMLStreamException {
        readExtraXMLAttributes(xer.peek());
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    // *** IF *** it has a state object, process it.
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        case MODULE_DOCUMENTATION_TAG:
                            setDocumentation(getListByTag(xer, MODULE_DOCUMENTATION_TAG));
                            break;
                        case MODULE_SOURCE_TAG:
                            QDLModule qdlModule = (QDLModule) this;
                            List<String> source = getListByTag(xer, MODULE_SOURCE_TAG);
                            // Version 2: The source is stored. In order to recoder this module
                            // the only reliable way is to re-interpret the source and pilfer the
                            // ModuleStatement which contains the documentation, executable statements etc.
                            // Again, this is because any QDL module statement can be quite complex and
                            // attempting to somehow serialize its module statements is vastly harder (and twitchier)
                            // than using the parser.
                            State state = new State();
                            QDLInterpreter qdlInterpreter = new QDLInterpreter(state);
                            try {
                                String x = StringUtils.listToString(source);
                                x = FDOC_CONVERT?x.replace(">>","»"):x;
                                qdlInterpreter.execute(x);
                                if(state.getMTemplates().isEmpty()) {
                                    // fall through case -- nothing resulted.
                                    throw new IllegalStateException("no module found");
                                }else{
                                    // Get the actual interpreted ModuleStatement
                                    QDLModule tempM = (QDLModule) state.getMTemplates().getAll().get(0);
                                    qdlModule.setModuleStatement(tempM.getModuleStatement());
                                    qdlModule.setDocumentation(tempM.getModuleStatement().getDocumentation());
                                    qdlModule.setAlias(tempM.getAlias());
                                    qdlModule.setNamespace(tempM.getNamespace());
                                    qdlModule.setParentTemplateID(tempM.getParentTemplateID());
                                    // Note that the source is gotten from the ModuleStatement
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                            break;
                    }

                    if (xe.asStartElement().getName().getLocalPart().equals(STATE_TAG)) {
                        //    getState().fromXML(xer, xp);
                    } else {
                        readExtraXMLElements(xe, xer);  // contract is that it gets the start tag...
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(MODULE_TAG)) {
                        return;
                    }
                default:
                    break;
            }
            xer.next();
        }
        throw new XMLMissingCloseTagException(MODULE_TAG);

    }

    /**
     * Getting the documentation from the XML serialization 2.0.
     *
     * @param xer
     * @return
     * @throws XMLStreamException
     */
    protected List<String> getListByTag(XMLEventReader xer, String tag) throws XMLStreamException {
        List<String> documentation = null;
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            switch (xe.getEventType()) {
                case XMLEvent.CHARACTERS:
                    if (xe.asCharacters().isWhiteSpace()) {
                        break;
                    }
                    String raw = new String(Base64.decodeBase64(xe.asCharacters().getData()));
                    documentation = StringUtils.stringToList(raw);
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(tag)) {

                        return documentation;
                    }
            }
            xe = xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(tag);
    }

    public void fromXML(XMLEventReader xer, XProperties xp, QDLInterpreter qi) throws XMLStreamException {
        readExtraXMLAttributes(xer.peek());
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    // *** IF *** it has a state object, process it.
                    if (xe.asStartElement().getName().getLocalPart().equals(STATE_TAG)) {
                        getState().fromXML(xer, xp);
                    } else {
                        readExtraXMLElements(xe, xer);  // contract is that it gets the start tag...
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(MODULE_TAG)) {
                        return;
                    }
                default:
                    break;
            }
            xer.next();
        }
        throw new XMLMissingCloseTagException(MODULE_TAG);

    }

    /**
     * This is passed the current event and should only have calls to read the attributes.
     *
     * @param xe
     * @throws XMLStreamException
     */
    public void readExtraXMLAttributes(XMLEvent xe) throws XMLStreamException {

    }

    /**
     * This passes in the current start event so you can add your own event loop and cases.
     * Note you need have only a switch on the tag names you want.
     *
     * @param xe
     * @param xer
     * @throws XMLStreamException
     */
    public void readExtraXMLElements(XMLEvent xe, XMLEventReader xer) throws XMLStreamException {

    }

    public abstract List<String> getListByTag();

    public abstract void setDocumentation(List<String> documentation);

    public abstract List<String> getDocumentation();

    public UUID getParentTemplateID() {
        return parentTemplateID;
    }

    public void setParentTemplateID(UUID parentTemplateID) {
        this.parentTemplateID = parentTemplateID;
    }

    UUID parentTemplateID;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    UUID id = UUID.randomUUID();
}
