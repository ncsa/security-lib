package edu.uiuc.ncsa.qdl.workspace;

import edu.uiuc.ncsa.qdl.exceptions.QDLRuntimeException;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.StateUtils;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.StringUtils;

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

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/2/21 at  5:54 AM
 */
public class WSXMLSerializer {
    public WSXMLSerializer(WorkspaceCommands workspaceCommands) {
        this.workspaceCommands = workspaceCommands;
    }

    WorkspaceCommands workspaceCommands;

    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {

        xsw.writeStartElement(WORKSPACE_TAG);
        xsw.writeStartElement(WS_ENV_TAG);
        xsw.writeAttribute(PRETTY_PRINT, Boolean.toString(workspaceCommands.prettyPrint));
        xsw.writeAttribute(ECHO_MODE, Boolean.toString(workspaceCommands.echoModeOn));
        xsw.writeAttribute(DEBUG_MODE, Boolean.toString(workspaceCommands.debugOn));
        xsw.writeAttribute(START_TS, Iso8601.date2String(workspaceCommands.startTimeStamp));
        xsw.writeAttribute(CURRENT_PID, Integer.toString(workspaceCommands.currentPID));
        if (workspaceCommands.envFile != null) {
            xsw.writeAttribute(ENV_FILE, workspaceCommands.envFile.getAbsolutePath());
        }
        if (!StringUtils.isTrivial(workspaceCommands.runScriptPath)) {
            xsw.writeAttribute(RUN_SCRIPT_PATH, workspaceCommands.runScriptPath);
        }
        if (workspaceCommands.currentWorkspace != null) {
            xsw.writeAttribute(CURRENT_WORKSPACE, workspaceCommands.currentWorkspace.getAbsolutePath());
        }

        if (workspaceCommands.rootDir != null) {
            xsw.writeAttribute(ROOT_DIR, workspaceCommands.rootDir.getAbsolutePath());
        }
        if (workspaceCommands.saveDir != null) {
            xsw.writeAttribute(SAVE_DIR, workspaceCommands.rootDir.getAbsolutePath());
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
            workspaceCommands.bufferManager.toXML(xsw);
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
        if (!state.getModuleMap().isEmpty()) {
            xsw.writeStartElement(MODULE_TEMPLATE_TAG);
            xsw.writeComment("Loaded modules, templates available when importing modules.");
            for (URI uri : state.getModuleMap().keySet()) {
                state.getModuleMap().get(uri).toXML(xsw, null);
            }
            xsw.writeEndElement(); //end templates
        }

        state.toXML(xsw);
        xsw.writeEndElement(); // end workspace tag

    }

    public WorkspaceCommands fromXML(XMLEventReader xer) throws XMLStreamException {
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
                            case MODULE_PATH:
                                testCommands.state.setModulePaths(getStemAsListFromXML(MODULE_PATH, xer));
                                break;
                            case EDITOR_CLIPBOARD:
                                testCommands.editorClipboard = getStemAsListFromXML(EDITOR_CLIPBOARD, xer);
                                break;
                            case STATE_TAG:
                                testCommands.state = StateUtils.load(testCommands.state, xer);
                                break;
                            case BUFFER_MANAGER:
                                testCommands.bufferManager = new BufferManager();
                                testCommands.bufferManager.fromXML(xer);
                                break;
                            case ENV_PROPERTIES:
                                doEnvProps(xer);
                                break;
                            case IMPORTS_COMMAND:
                                XMLUtils.deserializeImports(xer, testCommands.getEnv(), testCommands.getState());
                                break;
                            case MODULE_TEMPLATE_TAG:
                                XMLUtils.deserializeTemplates(xer, testCommands.getEnv(), testCommands.getState());
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
            String message = t.getMessage();
            if (t.getCause() != null) {
                message = t.getCause().getMessage();
            }
            String m = "Error parsing XML at line " + xe.getLocation().getLineNumber() + ", col " + xe.getLocation().getColumnNumber() +
                    ": " + message;
            throw new QDLRuntimeException(m, t);

        }
        throw new NFWException("Error: Could not deserialize the file from XML."); // should not happen, and yet...
    }

    protected void doEnvProps(XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(ENV_PROPERTIES)) {
                        return;
                    }
                case XMLEvent.CHARACTERS:
                    if(xe.asCharacters().isIgnorableWhiteSpace()){
                        break;
                    }
                    String rawEnv = xe.asCharacters().getData();
                    rawEnv = rawEnv.trim();
                    if(StringUtils.isTrivial(rawEnv)){
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
                        say("Could not deserialize stored properties:" + e.getMessage() + (e.getCause()==null?"":", cause = " + e.getCause().getMessage()));
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
                case RUN_SCRIPT_PATH:
                    testCommands.runScriptPath = v;
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
