package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.exceptions.DeserializationException;
import edu.uiuc.ncsa.qdl.exceptions.QDLRuntimeException;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StateUtils;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.Iso8601;
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
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands.IMPORTS_COMMAND;
import static edu.uiuc.ncsa.qdl.xml.XMLConstants.*;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/2/21 at  5:54 AM
 */
public class WSXMLSerializer {
    public WSXMLSerializer() {
    }


    public void toXML(WorkspaceCommands workspaceCommands, XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartDocument();
        xsw.writeStartElement(WORKSPACE_TAG);
        String comment = "";
        String indent = "     ";
        if (!isTrivial(workspaceCommands.getWSID())) {
            comment = "\n" + indent + "workspace id: " + workspaceCommands.getWSID() + "\n";
        }
        if (!isTrivial(workspaceCommands.getDescription())) {
            comment = comment + indent + workspaceCommands.getDescription() + "\n";
        }
        comment = comment + indent + "saved on " + Iso8601.date2String(System.currentTimeMillis()) + "\n";
        xsw.writeComment(comment);
        xsw.writeStartElement(WS_ENV_TAG);
        xsw.writeAttribute(PRETTY_PRINT, Boolean.toString(workspaceCommands.prettyPrint));
        xsw.writeAttribute(BUFFER_DEFAULT_SAVE_PATH, workspaceCommands.getBufferDefaultSavePath());
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
        if(!isTrivial(workspaceCommands.getExternalEditorName())) {
            xsw.writeAttribute(EXTERNAL_EDITOR_PATH, workspaceCommands.getExternalEditorName());
        }
        if (!isTrivial(workspaceCommands.getWSID())) {
            // Note that since we want this to be an attribute, we are limited to what characters normally
            // can be set. Base64 encoding the id allows the user to give this id of just about anything
            // without having to do a bunch of complex escaping.
            xsw.writeAttribute(WS_ID, Base64.encodeBase64String(workspaceCommands.getWSID().getBytes()));
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

        xsw.writeEndElement(); // end WS env
        if (workspaceCommands.bufferManager != null && !workspaceCommands.bufferManager.isEmpty()) {
            xsw.writeStartElement(BUFFER_MANAGER);
            workspaceCommands.bufferManager.toXML(xsw);
            xsw.writeEndElement();
        }

        List<String> ch = workspaceCommands.commandHistory;
        if(ch!=null && !ch.isEmpty()){
            xsw.writeStartElement(COMMAND_HISTORY);
            StemVariable stemVariable = new StemVariable();
            stemVariable.addList(ch);
            XMLUtils.write(xsw, stemVariable);
            xsw.writeEndElement();
        }

        List<String> s = workspaceCommands.getState().getScriptPaths();
        if (s != null && !s.isEmpty()) {
            xsw.writeStartElement(SCRIPT_PATH);
            StemVariable stemVariable = new StemVariable();
            stemVariable.addList(s);
            XMLUtils.write(xsw, stemVariable);
            xsw.writeEndElement(); // end script paths
        }

        s = workspaceCommands.getState().getModulePaths();
        if (s != null && !s.isEmpty()) {
            xsw.writeStartElement(MODULE_PATH);
            StemVariable stemVariable = new StemVariable();
            stemVariable.addList(s);
            XMLUtils.write(xsw, stemVariable);
            xsw.writeEndElement(); // end module paths
        }
        s = workspaceCommands.editorClipboard;
        if (s != null && !s.isEmpty()) {
            xsw.writeStartElement(EDITOR_CLIPBOARD);
            StemVariable stemVariable = new StemVariable();
            stemVariable.addList(s);
            XMLUtils.write(xsw, stemVariable);
            xsw.writeEndElement(); // end editor clipboard
        }

        /*
           NOTE There is a SINGLE ModuleMap = all the modules loaded, either from a file with the
           module_load() command or just with a module statement. This is accessed through the State
           object, but is identical in all derived states. It logically therefore is part of the workspace
           and is serialized here.
            */
        State state = workspaceCommands.getState();
        if (!state.getMTemplates().isEmpty()) {
            xsw.writeStartElement(OLD_MODULE_TEMPLATE_TAG);
            xsw.writeComment("Loaded modules, templates available when importing modules.");
            for (URI uri : state.getMTemplates().keySet()) {
                state.getMTemplates().get(uri).toXML(xsw, null);
            }
            xsw.writeEndElement(); //end templates
        }

        state.toXML(xsw);
        xsw.writeEndElement(); // end workspace tag

    }

    public WorkspaceCommands fromXML(XMLEventReader xer) throws XMLStreamException {
        return fromXML(xer, false);

    }

    public WorkspaceCommands fromXML(XMLEventReader xer, boolean workspaceAttributesOnly) throws XMLStreamException {
        if (!xer.hasNext()) {
            say("Error! no XML found to deserialize");
        }
        XMLEvent xe = xer.nextEvent();
        if (!xe.isStartDocument()) {
            say("Error! no XML start of document to deserialize");
        }
        // search for first workspace tag
        boolean hasWorkspaceTag = false;
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
                                processAttr(testCommands, xe);
                                break;
                            case SCRIPT_PATH:
                                testCommands.state.setScriptPaths(getStemAsListFromXML(SCRIPT_PATH, xer));
                                break;
                            case COMMAND_HISTORY:
                                testCommands.commandHistory = getStemAsListFromXML(COMMAND_HISTORY, xer);
                                break;
                            case MODULE_PATH:
                                testCommands.state.setModulePaths(getStemAsListFromXML(MODULE_PATH, xer));
                                break;
                            case DESCRIPTION:
                                testCommands.setDescription(getDescription(xer));
                                break;
                            case EDITOR_CLIPBOARD:
                                if (!workspaceAttributesOnly) {
                                    testCommands.editorClipboard = getStemAsListFromXML(EDITOR_CLIPBOARD, xer);
                                }
                                break;
                            case STATE_TAG:
                                if (!workspaceAttributesOnly) {
                                    testCommands.state = StateUtils.load(testCommands.state, xer);
                                }
                                break;
                            case BUFFER_MANAGER:
                                if (!workspaceAttributesOnly) {
                                    testCommands.bufferManager = new BufferManager();
                                    testCommands.bufferManager.fromXML(xer);
                                }
                                break;
                            case ENV_PROPERTIES:
                                if (!workspaceAttributesOnly) {
                                    doEnvProps(testCommands, xer);
                                }
                                break;
                            case IMPORTS_COMMAND:
                                if (!workspaceAttributesOnly) {
                                    XMLUtils.deserializeImports(xer, testCommands.getEnv(), testCommands.getState());
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
            if(t instanceof DeserializationException){
                throw (DeserializationException)t;
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
                    if(v!= null) {
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
