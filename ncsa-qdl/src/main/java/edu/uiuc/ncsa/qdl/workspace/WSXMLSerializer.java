package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.exceptions.DeserializationException;
import edu.uiuc.ncsa.qdl.exceptions.QDLRuntimeException;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StateUtils;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import edu.uiuc.ncsa.qdl.xml.XMLUtilsV2;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.*;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/2/21 at  5:54 AM
 */
public class WSXMLSerializer {
    public WSXMLSerializer() {
    }


    public void toXML(WorkspaceCommands workspaceCommands, XMLStreamWriter xsw) throws XMLStreamException {
        XMLSerializationState xmlSerializationState = new XMLSerializationState();
        xmlSerializationState.setVersion(VERSION_2_0_TAG);
        xsw.writeStartDocument();

        xsw.writeStartElement(WORKSPACE_TAG);
        xsw.writeAttribute(SERIALIZATION_VERSION_TAG, VERSION_2_0_TAG);
        String comment = "";
        String indent = "     ";
        if (!isTrivial(workspaceCommands.getWSID())) {
            comment = "\n" + indent + "workspace id: " + workspaceCommands.getWSID() + "\n";
        }
        if (!isTrivial(workspaceCommands.getDescription())) {
            comment = comment + indent + workspaceCommands.getDescription() + "\n";
        }
        comment = comment + indent + "serialized on " + Iso8601.date2String(System.currentTimeMillis()) + "\n";
        xsw.writeComment(comment);
        // Lay in the object store for templates then states. Since we use an event driven XML parser
        // order matters for deserialization.
        State state = workspaceCommands.getState();
        xmlSerializationState.addState(state);
        xsw.writeComment("Top-level state object for the workspace.");
        // Serialize main workspace state. This kicks off all the other serializations.
        state.buildSO(xmlSerializationState);


        // Do the workspace proper. This comes first since it is basically a header and when listing
        // the workspace, the system will jump out at the end of the header and not process the
        // templates or states. If you move things aorund it will work, but it will slow
        // listing workspaces quite a bit since it will deserialize the entire workspace before moving
        // on to the next.
        xsw.writeStartElement(WS_ENV_TAG);
        if (xmlSerializationState.isVersion2_0()) {
            processWSEnvNEW(workspaceCommands, xsw);
        } else {
            processWSEnvOLD(workspaceCommands, xsw);
        }

        xsw.writeEndElement(); // end WS env
        // Buffer manager can stay full XML for a bit since it is pretty complex and not a simple
        // data structure.
        if (workspaceCommands.bufferManager != null && !workspaceCommands.bufferManager.isEmpty()) {
            xsw.writeStartElement(BUFFER_MANAGER);
            workspaceCommands.bufferManager.toXML(xsw);
            xsw.writeEndElement();
        }
        if (workspaceCommands.env != null && !workspaceCommands.env.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(workspaceCommands.env);
            xsw.writeStartElement(ENV_PROPERTIES);
            xsw.writeCData(encodeBase64String(jsonObject.toString().getBytes(StandardCharsets.UTF_8)));
            xsw.writeEndElement();

            /*
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                xsw.writeStartElement(ENV_PROPERTIES);
                workspaceCommands.env.storeToXML(baos, "workspace serialization");
                xsw.writeCData(encodeBase64String(baos.toByteArray()));
                xsw.writeEndElement();
            } catch (IOException e) {
                workspaceCommands.logger.warn("Could not serialize environment to XML:\"" + e.getMessage() + "\".");
            }*/
        }
        saveWSList(xsw, workspaceCommands.commandHistory, COMMAND_HISTORY, xmlSerializationState);
        saveWSList(xsw, workspaceCommands.getState().getScriptPaths(), SCRIPT_PATH, xmlSerializationState);
        saveWSList(xsw, workspaceCommands.getState().getModulePaths(), MODULE_PATH, xmlSerializationState);
        saveWSList(xsw, workspaceCommands.editorClipboard, EDITOR_CLIPBOARD, xmlSerializationState);

        // Global list of templates
        xsw.writeStartElement(MODULE_TEMPLATE_TAG);
        xsw.writeComment("templates for all modules");
        for (UUID key : xmlSerializationState.templateMap.keySet()) {
            Module module = xmlSerializationState.getTemplate(key);
            module.toXML(xsw, null, true, xmlSerializationState);
        }
        xsw.writeEndElement(); // end module templates

        // Save other states (in modules).
        xsw.writeStartElement(STATES_TAG);
        xsw.writeComment("module states");

        /**
         * At this point we have a flat list of states. Serialize all of them.
         */
        Set<UUID> currentKeys = new HashSet<>();
        currentKeys.addAll(xmlSerializationState.stateMap.keySet());
        for (UUID key : currentKeys) {
            if (!key.equals(state.getUuid()))
                xmlSerializationState.getState(key).toXML(xsw, xmlSerializationState);
        }


        xsw.writeEndElement(); // end states reference

        // Absolute last thing to write is the actual state object for the workspace.
        state.toXML(xsw, xmlSerializationState);
        if (!state.getExtrinsicVars().isEmpty()) {
            xsw.writeStartElement(EXTRINSIC_VARIABLES_TAG);
            state.getExtrinsicVars().toXML(xsw, xmlSerializationState);
            xsw.writeEndElement();
        }
        xsw.writeEndElement(); // end workspace tag
    }

    private void saveWSList(XMLStreamWriter xsw,
                            List<String> s,
                            String tag,
                            XMLSerializationState xmlSerializationState) throws XMLStreamException {
        if (s != null && !s.isEmpty()) {
            xsw.writeStartElement(tag);
            if (xmlSerializationState.isVersion2_0()) {
                toCDataB64(xsw, s);
            } else {
                StemVariable stemVariable = new StemVariable();
                stemVariable.addList(s);
                XMLUtils.write(xsw, stemVariable);
            }
            xsw.writeEndElement(); // end script paths
        }
    }

    private void toCDataB64(XMLStreamWriter xsw, List<String> s) throws XMLStreamException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(s);
        xsw.writeCData(encodeBase64String(jsonArray.toString().getBytes(StandardCharsets.UTF_8)));
    }

    private void processWSEnvNEW(WorkspaceCommands workspaceCommands, XMLStreamWriter xsw) throws XMLStreamException {
        JSONObject json = new JSONObject();
        json.put(PRETTY_PRINT, Boolean.toString(workspaceCommands.prettyPrint));
        String zzz = workspaceCommands.getBufferDefaultSavePath();
        if (zzz != null) {
            json.put(BUFFER_DEFAULT_SAVE_PATH, zzz);
        }
        // booleans
        json.put(ECHO_MODE, workspaceCommands.echoModeOn);
        json.put(DEBUG_MODE, workspaceCommands.debugOn);
        json.put(AUTOSAVE_ON, workspaceCommands.isAutosaveOn());
        json.put(RUN_INIT_ON_LOAD, workspaceCommands.runInitOnLoad);
        json.put(AUTOSAVE_MESSAGES_ON, workspaceCommands.isAutosaveMessagesOn());
        json.put(COMPRESS_XML, workspaceCommands.compressXML);
        json.put(USE_EXTERNAL_EDITOR, workspaceCommands.isUseExternalEditor());
        json.put(ASSERTIONS_ON, workspaceCommands.isAssertionsOn());
        json.put(PRETTY_PRINT, workspaceCommands.isPrettyPrint());
        json.put(ENABLE_LIBRARY_SUPPORT, workspaceCommands.getState().isEnableLibrarySupport());
        // ints
        json.put(CURRENT_PID, Integer.toString(workspaceCommands.currentPID));
        // longs
        json.put(AUTOSAVE_INTERVAL, workspaceCommands.getAutosaveInterval());
        // dates
        json.put(START_TS, Iso8601.date2String(workspaceCommands.startTimeStamp));

        // strings

        if (!isTrivial(workspaceCommands.getExternalEditorName())) {
            json.put(EXTERNAL_EDITOR_NAME, workspaceCommands.getExternalEditorName());
        }

        if (!isTrivial(workspaceCommands.getWSID())) {
            json.put(WS_ID, workspaceCommands.getWSID());
        }
        if (workspaceCommands.envFile != null) {
            json.put(ENV_FILE, workspaceCommands.envFile.getAbsolutePath());
        }
        if (!isTrivial(workspaceCommands.runScriptPath)) {
            json.put(RUN_SCRIPT_PATH, workspaceCommands.runScriptPath);
        }
        if (workspaceCommands.currentWorkspace != null) {
            json.put(CURRENT_WORKSPACE, workspaceCommands.currentWorkspace.getAbsolutePath());
        }

        if (workspaceCommands.rootDir != null) {
            json.put(ROOT_DIR, workspaceCommands.rootDir.getAbsolutePath());
        }
        if (workspaceCommands.saveDir != null) {
            json.put(SAVE_DIR, workspaceCommands.saveDir.getAbsolutePath());
        }
        if (workspaceCommands.description != null) {
            json.put(DESCRIPTION, workspaceCommands.description);
        }

        xsw.writeCData(encodeBase64String(json.toString().getBytes(StandardCharsets.UTF_8)));
    }

    private void processWSEnvOLD(WorkspaceCommands workspaceCommands, XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeAttribute(PRETTY_PRINT, Boolean.toString(workspaceCommands.prettyPrint));
        String zzz = workspaceCommands.getBufferDefaultSavePath();
        if (zzz != null) {
            xsw.writeAttribute(BUFFER_DEFAULT_SAVE_PATH, zzz);
        }
        xsw.writeAttribute(ECHO_MODE, Boolean.toString(workspaceCommands.echoModeOn));
        xsw.writeAttribute(DEBUG_MODE, Boolean.toString(workspaceCommands.debugOn));
        xsw.writeAttribute(AUTOSAVE_ON, Boolean.toString(workspaceCommands.isAutosaveOn()));
        xsw.writeAttribute(AUTOSAVE_INTERVAL, Long.toString(workspaceCommands.getAutosaveInterval()));
        xsw.writeAttribute(AUTOSAVE_MESSAGES_ON, Boolean.toString(workspaceCommands.isAutosaveMessagesOn()));
        xsw.writeAttribute(START_TS, Iso8601.date2String(workspaceCommands.startTimeStamp));
        xsw.writeAttribute(CURRENT_PID, Integer.toString(workspaceCommands.currentPID));
        xsw.writeAttribute(RUN_INIT_ON_LOAD, Boolean.toString(workspaceCommands.runInitOnLoad));

        xsw.writeAttribute(COMPRESS_XML, Boolean.toString(workspaceCommands.compressXML));
        xsw.writeAttribute(USE_EXTERNAL_EDITOR, Boolean.toString(workspaceCommands.isUseExternalEditor()));
        xsw.writeAttribute(ASSERTIONS_ON, Boolean.toString(workspaceCommands.isAssertionsOn()));
        if (!isTrivial(workspaceCommands.getExternalEditorName())) {
            xsw.writeAttribute(EXTERNAL_EDITOR_PATH, workspaceCommands.getExternalEditorName());
        }
        if (!isTrivial(workspaceCommands.getWSID())) {
            // Note that since we want this to be an attribute, we are limited to what characters normally
            // can be set. Base64 encoding the id allows the user to give this id of just about anything
            // without having to do a bunch of complex escaping.
            xsw.writeAttribute(WS_ID, encodeBase64String(workspaceCommands.getWSID().getBytes()));
        }
        if (workspaceCommands.envFile != null) {
            xsw.writeAttribute(ENV_FILE, workspaceCommands.envFile.getAbsolutePath());
        }
        if (!isTrivial(workspaceCommands.runScriptPath)) {
            xsw.writeAttribute(RUN_SCRIPT_PATH, workspaceCommands.runScriptPath);
        }
        if (workspaceCommands.currentWorkspace != null) {
            xsw.writeAttribute(CURRENT_WORKSPACE, workspaceCommands.currentWorkspace.getAbsolutePath());
        }

        if (workspaceCommands.rootDir != null) {
            xsw.writeAttribute(ROOT_DIR, workspaceCommands.rootDir.getAbsolutePath());
        }
        if (workspaceCommands.saveDir != null) {
            xsw.writeAttribute(SAVE_DIR, workspaceCommands.saveDir.getAbsolutePath());
        }

        // Done with attributes
        if (!isTrivial(workspaceCommands.getDescription())) {
            xsw.writeStartElement(DESCRIPTION);
            xsw.writeCData(Base64.encodeBase64URLSafeString(workspaceCommands.getDescription().getBytes()));
            xsw.writeEndElement(); // end description paths
        }
        /*
        NOTE for properly recreating the state, this needs to be the first element in the
        serialization so it is available to be passed around as part of the interpreter.
        It  would be bad form  to have, e.g., properties as part of a function definition,
        but nothing stops a user from doing that and we do try to recreate all from the
        source that the user supplied.
         */
        if (workspaceCommands.env != null && !workspaceCommands.env.isEmpty()) {
            xsw.writeStartElement(ENV_PROPERTIES);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                workspaceCommands.env.storeToXML(baos, "workspace serialization");
                xsw.writeCData(new String(baos.toByteArray()));
            } catch (IOException e) {
                workspaceCommands.logger.warn("Could not serialize environment to XML:\"" + e.getMessage() + "\".");
            }
            xsw.writeEndElement(); // end env properties file
        }
    }


    public WorkspaceCommands fromXML(XMLEventReader xer, boolean skipBadModules) throws XMLStreamException {
        return fromXML(xer, false, skipBadModules);

    }

    public WorkspaceCommands fromXML(XMLEventReader xer, boolean workspaceAttributesOnly, boolean skipBadModules) throws XMLStreamException {
        if (!xer.hasNext()) {
            say("Error! no XML found to deserialize");
        }
        XMLEvent xe = xer.nextEvent();
        if (!xe.isStartDocument()) {
            say("Error! no XML start of document to deserialize");
        }
        // search for first workspace tag
        boolean hasWorkspaceTag = false;
        XMLSerializationState xmlSerializationState = new XMLSerializationState();
        xmlSerializationState.skipBadModules = skipBadModules;
        while (xer.hasNext()) {
            xe = xer.nextEvent(); // SHOULD be the workspace tag but there can be comments, DTDs etc.
            if (xe.isStartElement() && xe.asStartElement().getName().getLocalPart().equals(WORKSPACE_TAG)) {
                hasWorkspaceTag = true;
                break;
            }
        }

        if (!hasWorkspaceTag) {
            throw new IllegalArgumentException("No workspace found to deserialize.");
        }
        Iterator iterator = xe.asStartElement().getAttributes(); // Use iterator since it tracks state
        while (iterator.hasNext()) {
            Attribute a = (Attribute) iterator.next();
            switch (a.getName().getLocalPart()) {
                case SERIALIZATION_VERSION_TAG:
                    xmlSerializationState.setVersion(a.getValue());
            }
        }

        WorkspaceCommands testCommands = new WorkspaceCommands();
        State state = StateUtils.newInstance();
        testCommands.state = state;
        try {
            while (xer.hasNext()) {
                xe = xer.peek(); // May have to slog through a bunch of events (like whitespace)
                switch (xe.getEventType()) {
                    case XMLEvent.START_ELEMENT:
                        switch (xe.asStartElement().getName().getLocalPart()) {
                            case WS_ENV_TAG:
                                if (xmlSerializationState.isVersion2_0()) {
                                    processJSONAttr(xer, testCommands);
                                } else {
                                    processAttr(testCommands, xe);
                                }
                                break;
                            case SCRIPT_PATH:
                                if (xmlSerializationState.isVersion2_0()) {
                                    testCommands.state.setScriptPaths(getStringsFromJSON(xer, SCRIPT_PATH));
                                } else {
                                    testCommands.state.setScriptPaths(getStemAsListFromXML(SCRIPT_PATH, xer));
                                }
                                break;
                            case COMMAND_HISTORY:
                                if (xmlSerializationState.isVersion2_0()) {
                                    testCommands.commandHistory = getStringsFromJSON(xer, COMMAND_HISTORY);
                                } else {
                                    testCommands.commandHistory = getStemAsListFromXML(COMMAND_HISTORY, xer);
                                }
                                break;
                            case MODULE_PATH:
                                if (xmlSerializationState.isVersion2_0()) {
                                    testCommands.state.setModulePaths(getStringsFromJSON(xer, MODULE_PATH));
                                } else {
                                    testCommands.state.setModulePaths(getStemAsListFromXML(MODULE_PATH, xer));
                                }
                                break;
                            case DESCRIPTION:
                                testCommands.setDescription(getDescription(xer));
                                break;
                            case EDITOR_CLIPBOARD:
                                if (!workspaceAttributesOnly) {
                                    if (xmlSerializationState.isVersion2_0()) {
                                        testCommands.editorClipboard = getStringsFromJSON(xer, EDITOR_CLIPBOARD);
                                    } else {
                                        testCommands.editorClipboard = getStemAsListFromXML(EDITOR_CLIPBOARD, xer);
                                    }
                                }
                                break;
                            case STATES_TAG:
                                XMLUtilsV2.deserializeStateStore(xer, xmlSerializationState);
                                break;
                            case MODULE_TEMPLATE_TAG:
                                XMLUtilsV2.deserializeTemplateStore(xer, xmlSerializationState);
                                break;
                            case STATE_TAG:
                                if (!workspaceAttributesOnly) {
                                    if (xmlSerializationState.isVersion2_0()) {
                                        testCommands.state = StateUtils.load(testCommands.state, xmlSerializationState, xer);
                                    } else {
                                        testCommands.state = StateUtils.load(testCommands.state, xer);
                                    }
                                }
                                break;
                            case EXTRINSIC_VARIABLES_TAG:
                                // Actually, there is exactly one of these. So this populates whatever is current.
                                // Clear it, then re-populate it.
                                testCommands.state.getExtrinsicVars().clear();
                                XMLUtilsV2.deserializeExtrinsicVariables(xer, testCommands.state, xmlSerializationState);
                           break;
                            case BUFFER_MANAGER:
                                if (!workspaceAttributesOnly) {
                                    testCommands.bufferManager = new BufferManager();
                                    testCommands.bufferManager.fromXML(xer);
                                }
                                break;
                            case ENV_PROPERTIES:
                                if (!workspaceAttributesOnly) {
                                    if (xmlSerializationState.isVersion2_0()) {
                                        String text = XMLUtilsV2.getText(xer, ENV_PROPERTIES);
                                        String raw = new String(Base64.decodeBase64(text));
                                        JSONObject jsonObject = JSONObject.fromObject(raw);
                                        XProperties xp = new XProperties();
                                        xp.add(jsonObject, true);
                                        testCommands.env = xp;
                                    } else {
                                        doEnvProps(testCommands, xer);
                                    }
                                }
                                break;

                            case OLD_MODULE_TEMPLATE_TAG:
                                if (!workspaceAttributesOnly) {
                                    XMLUtils.deserializeTemplates(xer, testCommands.getEnv(), testCommands.getState());
                                }
                                break;
                        }
                        break;
                    case XMLEvent.END_ELEMENT:
                        if (xe.asEndElement().getName().getLocalPart().equals(WORKSPACE_TAG)) {
                            return testCommands;
                        }

                        break;
                }
                xer.next(); // advance cursor
            }
        } catch (Throwable t) {
            if (t instanceof DeserializationException) {
                throw (DeserializationException) t;
            }
            String message = t.getMessage();
            if (t.getCause() != null) {
                message = t.getCause().getMessage();
            }
            String m;
            if (t instanceof XMLStreamException) {
                m = "Error parsing XML at line " + xe.getLocation().getLineNumber() + ", col " + xe.getLocation().getColumnNumber() +
                        ": ";
            } else {
                m = "Error processing XML:";
            }
            m = m + (message == null ? "(no message)" : message);
            throw new QDLRuntimeException(m, t);

        }
        throw new NFWException("Error: Could not deserialize the file from XML."); // should not happen, and yet...
    }

    private List<String> getStringsFromJSON(XMLEventReader xer, String tag) throws XMLStreamException {
        String text = XMLUtilsV2.getText(xer, tag);
        String rawJSON = new String(Base64.decodeBase64(text));
        JSONArray array = JSONArray.fromObject(rawJSON);
        List<String> xx = new ArrayList<>();
        xx.addAll(array);
        return xx;
    }

    protected String getDescription(XMLEventReader xer) throws XMLStreamException {
        String description = null;
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            switch (xe.getEventType()) {
                case XMLEvent.CHARACTERS:
                    if (xe.asCharacters().isWhiteSpace()) {
                        break;
                    }
                    description = new String(Base64.decodeBase64(xe.asCharacters().getData()));

                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(DESCRIPTION)) {
                        return description;
                    }
            }
            xe = xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(DESCRIPTION);
    }

    protected void doEnvProps(WorkspaceCommands workspaceCommands, XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(ENV_PROPERTIES)) {
                        return;
                    }
                case XMLEvent.CHARACTERS:
                    if (xe.asCharacters().isIgnorableWhiteSpace()) {
                        break;
                    }
                    String rawEnv = xe.asCharacters().getData();
                    rawEnv = rawEnv.trim();
                    if (isTrivial(rawEnv)) {
                        break;
                    }
                    ByteArrayInputStream bais = new ByteArrayInputStream(rawEnv.getBytes());
                    XProperties xp = new XProperties();
                    try {
                        xp.loadFromXML(bais);
                        bais.close();
                        if (workspaceCommands.env == null) {
                            workspaceCommands.env = xp;
                        } else {
                            workspaceCommands.env.add(xp, true);
                        }

                    } catch (IOException e) {
                        say("Could not deserialize stored properties:" + e.getMessage() + (e.getCause() == null ? "" : ", cause = " + e.getCause().getMessage()));
                    }
                default:
                    // Skip unknown tags.
            }
            xer.next();
        }
    }

    protected List<String> getStemAsListFromXML(String tag, XMLEventReader xer) throws XMLStreamException {
        StemVariable stem = null;
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(STEM_TAG)) {
                        Object obj = XMLUtils.resolveConstant(xer);
                        // it is possible that the XML does not have a serialized stem, in which case, ignore it.
                        if (obj instanceof StemVariable) {
                            stem = (StemVariable) obj;
                        }
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(tag)) {
                        if (stem != null) {
                            return stem.getStemList().toJSON();
                        }
                        return null;
                    }
            }
            xer.nextEvent();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + STATE_TAG);

    }

    protected void processJSONAttr(XMLEventReader xer, WorkspaceCommands workspaceCommands) throws Throwable {
        String text = XMLUtilsV2.getText(xer, WS_ENV_TAG); // should be JSON b64 encoded
        String rawJSON = new String(Base64.decodeBase64(text));
        JSONObject json = JSONObject.fromObject(rawJSON);

        // booleans
        workspaceCommands.echoModeOn = json.getBoolean(ECHO_MODE);
        workspaceCommands.debugOn = json.getBoolean(DEBUG_MODE);
        workspaceCommands.setAutosaveOn(json.getBoolean(AUTOSAVE_ON));
        workspaceCommands.runInitOnLoad = json.getBoolean(RUN_INIT_ON_LOAD);
        workspaceCommands.setAutosaveMessagesOn(json.getBoolean(AUTOSAVE_MESSAGES_ON));
        workspaceCommands.compressXML = json.getBoolean(COMPRESS_XML);
        workspaceCommands.setUseExternalEditor(json.getBoolean(USE_EXTERNAL_EDITOR));
        workspaceCommands.setAssertionsOn(json.getBoolean(ASSERTIONS_ON));
        workspaceCommands.setPrettyPrint(json.getBoolean(PRETTY_PRINT));

        // numbers
        workspaceCommands.setAutosaveInterval(json.getLong(AUTOSAVE_INTERVAL));
        workspaceCommands.currentPID = json.getInt(CURRENT_PID);

        //dates
        workspaceCommands.startTimeStamp = Iso8601.string2Date(json.getString(START_TS)).getTime();

        // strings
        if (json.containsKey(EXTERNAL_EDITOR_NAME)) {
            workspaceCommands.setExternalEditorName(json.getString(EXTERNAL_EDITOR_NAME));
        }
        if (json.containsKey(WS_ID)) {
            workspaceCommands.setWSID(json.getString(WS_ID));
        }
        if (json.containsKey(ENV_FILE)) {
            workspaceCommands.envFile = new File(json.getString(ENV_FILE));
        }
        if (json.containsKey(RUN_SCRIPT_PATH)) {
            workspaceCommands.runScriptPath = json.getString(RUN_SCRIPT_PATH);
        }
        if (json.containsKey(CURRENT_WORKSPACE)) {
            workspaceCommands.currentWorkspace = new File(json.getString(CURRENT_WORKSPACE));
        }
        if (json.containsKey(ROOT_DIR)) {
            workspaceCommands.rootDir = new File(json.getString(ROOT_DIR));
        }
        if (json.containsKey(SAVE_DIR)) {
            workspaceCommands.saveDir = new File(json.getString(SAVE_DIR));
        }
        if (json.containsKey(DESCRIPTION)) {
            workspaceCommands.description = json.getString(DESCRIPTION);
        }
    }

    protected void processAttr(WorkspaceCommands testCommands, XMLEvent xe) throws Throwable {
        Iterator iterator = xe.asStartElement().getAttributes(); // Use iterator since it tracks state
        while (iterator.hasNext()) {
            Attribute a = (Attribute) iterator.next();
            String v = a.getValue();
            switch (a.getName().getLocalPart()) {
                case PRETTY_PRINT:
                    testCommands.setPrettyPrint(Boolean.parseBoolean(v));
                    break;
                case BUFFER_DEFAULT_SAVE_PATH:
                    testCommands.bufferDefaultSavePath = v;
                    break;
                case AUTOSAVE_INTERVAL:
                    if (v != null) {
                        testCommands.setAutosaveInterval(Long.parseLong(v));
                    }
                    break;
                case ASSERTIONS_ON:
                    testCommands.setAssertionsOn(Boolean.parseBoolean(v));
                    break;
                case AUTOSAVE_MESSAGES_ON:
                    testCommands.setAutosaveMessagesOn(Boolean.parseBoolean(v));
                    break;
                case AUTOSAVE_ON:
                    testCommands.setAutosaveOn(Boolean.parseBoolean(v));
                    break;
                case ECHO_MODE:
                    testCommands.setEchoModeOn(Boolean.parseBoolean(v));
                    break;
                case DEBUG_MODE:
                    testCommands.setDebugOn(Boolean.parseBoolean(v));
                    break;
                case CURRENT_WORKSPACE:
                    testCommands.currentWorkspace = new File(v);
                    break;
                case START_TS:
                    testCommands.startTimeStamp = Iso8601.string2Date(v).getTime();
                    break;
                case ROOT_DIR:
                    testCommands.rootDir = new File(v);
                    break;
                case SAVE_DIR:
                    testCommands.saveDir = new File(v);
                    break;
                case ENV_FILE:
                    testCommands.envFile = new File(v);
                    break;
                case CURRENT_PID:
                    testCommands.currentPID = Integer.parseInt(v);
                    break;
                case RUN_INIT_ON_LOAD:
                    testCommands.runInitOnLoad = Boolean.parseBoolean(v);
                case RUN_SCRIPT_PATH:
                    testCommands.runScriptPath = v;
                    break;
                case COMPRESS_XML:
                    testCommands.setCompressXML(Boolean.parseBoolean(v));
                    break;
                case WS_ID:
                    testCommands.setWSID(new String(Base64.decodeBase64(v)));
                    break;
                case EXTERNAL_EDITOR_PATH:
                    testCommands.setExternalEditorName(v);
                    break;
                case USE_EXTERNAL_EDITOR:
                    testCommands.setUseExternalEditor(Boolean.parseBoolean(v));
                    break;

                default:
                    // do nothing
                    say("unknown workspace attribute " + a.getName().getLocalPart() + "=" + v);

            }
        }
    }

    protected void say(String x) {
        System.out.println(x);
    }
}
