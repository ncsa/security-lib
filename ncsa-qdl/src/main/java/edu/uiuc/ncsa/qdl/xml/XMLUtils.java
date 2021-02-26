package edu.uiuc.ncsa.qdl.xml;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.QDLModule;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemEntry;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * A class for all those XML related snippets that are re-used everywhere.
 * <p>Created by Jeff Gaynor<br>
 * on 12/27/20 at  7:16 AM
 */
public class XMLUtils implements XMLConstants {


    protected static StemVariable makeStem(XMLStreamReader xsr) throws XMLStreamException {
        StemVariable stem = new StemVariable();
        // what SHOULD happen is that there is sequence of entry tags
        // This processes entries, in other words.
        while (xsr.hasNext()) {
            String keyName = null;
            Object value = null;
            switch (xsr.getEventType()) {
                case XMLStreamReader.CHARACTERS:
                case XMLStreamReader.COMMENT:
                case XMLStreamReader.CDATA:
                case XMLStreamReader.SPACE:
                case XMLStreamReader.ENTITY_REFERENCE:
                    System.out.println(xsr.getText());
                    break;
                case XMLStreamReader.START_ELEMENT:
                    String name = xsr.getLocalName();
                    System.out.println("local name = " + name);
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    xsr.getAttributeLocalName(0);
                    keyName = xsr.getAttributeValue(0);
                    System.out.println("attributes # = " + xsr.getAttributeCount());
                    break;
            }
            value = read(xsr);
            stem.put(keyName, value);
        }
        return stem;
    }

    public static void write(XMLStreamWriter xsw, Long ll) throws XMLStreamException {
        xsw.writeStartElement(INTEGER_TAG);
        xsw.writeCharacters(ll.toString());
        xsw.writeEndElement();
    }

    public static void write(XMLStreamWriter xsw, String ll) throws XMLStreamException {
        xsw.writeStartElement(STRING_TAG);
        xsw.writeCharacters(ll.toString());
        xsw.writeEndElement();
    }

    public static void write(XMLStreamWriter xsw, StemEntry stemEntry) throws XMLStreamException {
        xsw.writeStartElement(STEM_ENTRY_TAG);
        xsw.writeAttribute(LIST_INDEX_ATTR, Long.toString(stemEntry.index));
        write(xsw, stemEntry.entry);
        xsw.writeEndElement();
    }

    public static void write(XMLStreamWriter xsw, BigDecimal decimal) throws XMLStreamException {
        xsw.writeStartElement(DECIMAL_TAG);
        xsw.writeCharacters(decimal.toString());
        xsw.writeEndElement();
    }

    /**
     * write a list of anything Java (String, URIs etc.)  as a stem of strings.
     *
     * @param xsw
     * @param list
     * @throws XMLStreamException
     */
    public static void write(XMLStreamWriter xsw, List list) throws XMLStreamException {
        if (list != null && !list.isEmpty()) {
            StemVariable s = new StemVariable();
            for (Object obj : list) {
                if (obj != null) {
                    s.listAppend(obj.toString());
                }
            }
            write(xsw, s);
        }

    }

    public static void write(XMLStreamWriter xsw, StemVariable stem) throws XMLStreamException {
        xsw.writeStartElement(STEM_TAG);
        if (0 < stem.size()) {
            for (String key : stem.keySet()) {
                xsw.writeStartElement(STEM_ENTRY_TAG);
                xsw.writeAttribute(STEM_KEY_TAG, key);
                write(xsw, stem.get(key));
                xsw.writeEndElement();
            }
        }
        xsw.writeEndElement();

    }

    public static Object read(XMLEventReader xer) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        Object obj = null;
        String name = xe.asStartElement().getName().getLocalPart();
        while (xer.hasNext()) {
            xe = xer.nextEvent();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    break;
                case XMLEvent.END_ELEMENT:
                    xe.asEndElement().getName().getLocalPart().equals(name);
                    return obj;


            }
            xer.next();
        }

        return null;

    }

    /**
     * Note that this requires the stem to be exactly a standard list.
     *
     * @param xer
     * @return
     * @throws XMLStreamException
     */
    public static List<String> readStemAsStrings(XMLEventReader xer) throws XMLStreamException {
        Object obj = resolveConstant(xer);
        DebugUtil.trace(XMLUtils.class, "returned obj= " + obj);
        StemVariable stem = new StemVariable();

        if (obj == null) {
            return new ArrayList<>();
        }
        if (!(obj instanceof StemVariable)) {
            throw new IllegalArgumentException("Error: expected a stem and got a " + obj.getClass().getSimpleName());
        }
        return stem.getStemList().toJSON();
    }

    /**
     * resolve a constant of type integer, decimal, stem, null, boolean or string
     *
     * @param xer
     * @return
     * @throws XMLStreamException
     */
    public static Object resolveConstant(XMLEventReader xer) throws XMLStreamException {
        // score card, we have a start event that either has char data to be converted to a
        // scalar or a stem.
        XMLEvent xe = xer.nextEvent(); // get current event
        if (!xe.isStartElement()) {
            throw new IllegalStateException("Error: Wrong XML tag type. line " + xe.getLocation().getLineNumber() + ", col " + xe.getLocation().getColumnNumber()); // just in case
        }
        Object output = null;
        String tagName = xe.asStartElement().getName().getLocalPart();
        if (tagName.equals(STEM_TAG)) {
            // farm it out
            output = resolveStem(xer);
            if (output != null) {
                return output;
            }
        } else {
            xe = xer.nextEvent();
            // A null is of the form <null/> so there is never any character data.
            if (xe.isEndElement() && xe.asEndElement().getName().getLocalPart().equals(NULL_TAG)) {
                output = QDLNull.getInstance();
            } else if (xe.isEndElement() && xe.asEndElement().getName().getLocalPart().equals(STRING_TAG)) {
                // A string may be emoty and of the form <string/> so there is never any character data too.
                output = ""; // case of empty string tag
            } else {

                if (xe.getEventType() != XMLEvent.CHARACTERS) {
                    throw new IllegalStateException("Error: Wrong XML tag type. line " + xe.getLocation().getLineNumber() + ", col " + xe.getLocation().getColumnNumber()); // just in case
                }
                String raw = xe.asCharacters().getData();
                // several of these strip out the whitespace (which may include line feeds and other cruft.
                switch (tagName) {
                    case INTEGER_TAG:
                        output = Long.parseLong(raw.replaceAll("\\s", ""));
                        break;
                    case DECIMAL_TAG:
                        output = new BigDecimal(raw.replaceAll("\\s", ""));
                        break;
                    case BOOLEAN_TAG:
                        output = Boolean.parseBoolean(raw.replaceAll("\\s", ""));
                        break;
                    case STRING_TAG:
                        output = raw;
                        break;
                }
            }
        }
        while (xer.hasNext()) {
            if (xe.getEventType() == XMLEvent.END_ELEMENT) {
                if (xe.asEndElement().getName().getLocalPart().equals(tagName)) {
                    return output;
                }
            }
            xe = xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(tagName);

    }

    protected static StemVariable resolveStem(XMLEventReader xer) throws XMLStreamException {
        StemVariable stem = new StemVariable();
        XMLEvent xe = xer.nextEvent();
        if (xe.isEndElement() && xe.asEndElement().getName().getLocalPart().equals(STEM_TAG)) {
            return stem; // this covers the edge case that the entry is exactly <stem/>
        }
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(STEM_ENTRY_TAG)) {
                        String key = xe.asStartElement().getAttributeByName(new QName(STEM_KEY_TAG)).getValue();
                        xer.next(); // advance cursor to contents, hand it off.
                        XMLEvent xe1 = xer.peek();

                        while (xe1.getEventType() != XMLEvent.START_ELEMENT) {
                            xer.nextEvent();
                            xe1 = xer.peek();
                        }
                        stem.put(key, resolveConstant(xer));

                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    // This will also get closing STEM_ENTRY_TAGs too, but we just ignore those.
                    if (xe.asEndElement().getName().getLocalPart().equals(STEM_TAG)) {
                        return stem;
                    }
                    break;
            }
            xe = xer.nextEvent();

        }
        // If nothing is found, return null.
        return null;
    }

    public static Object read(XMLStreamReader xsr) throws XMLStreamException {
        switch (xsr.getLocalName()) {
            case INTEGER_TAG:
                return Long.parseLong(xsr.getText());
            case DECIMAL_TAG:
                return new BigDecimal(xsr.getText());
            case BOOLEAN_TAG:
                return Boolean.parseBoolean(xsr.getText());
            case STRING_TAG:
                return xsr.getElementText();
            case STEM_ENTRY_TAG:
                return makeStem(xsr);
            case NULL_TAG:
                return QDLNull.getInstance();
        }
        throw new IllegalArgumentException("Error: unknown constant type.");
    }

    public static void write(XMLStreamWriter xsw, Object obj) throws XMLStreamException {
        if (obj instanceof StemEntry) {
            write(xsw, (StemEntry) obj);
            return;
        }
        switch (Constant.getType(obj)) {
            case Constant.LONG_TYPE:
                write(xsw, (Long) obj);
                break;
            case Constant.BOOLEAN_TYPE:
                write(xsw, (Boolean) obj);
                break;
            case Constant.STRING_TYPE:
                write(xsw, (String) obj);
                break;
            case Constant.DECIMAL_TYPE:
                write(xsw, (BigDecimal) obj);
                break;
            case Constant.STEM_TYPE:
                write(xsw, (StemVariable) obj);
                break;
            case Constant.NULL_TYPE:
                write(xsw, (QDLNull) obj);
                break;
            case Constant.UNKNOWN_TYPE:
                throw new IllegalArgumentException("Error:Unknown constants type for \"" + obj + "\"");
        }

    }

    public static void write(XMLStreamWriter xsw, QDLNull qdlNull) throws XMLStreamException {
        xsw.writeStartElement(NULL_TAG);
        xsw.writeEndElement();
    }

    public static void write(XMLStreamWriter xsw, Boolean bool) throws XMLStreamException {
        xsw.writeStartElement(BOOLEAN_TAG);
        xsw.writeCharacters(bool.toString());
        xsw.writeEndElement();
    }

    public static void deserializeFunctions(XMLEventReader xer, XProperties xp, State state) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        QDLInterpreter qi = new QDLInterpreter(xp, state);
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(FUNCTION_TAG)) {
                        state.getFunctionTable().fromXML(xer, qi);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(FUNCTIONS_TAG)) {
                        return;
                    }
            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(FUNCTIONS_TAG);
    }

    public static void deserializeImports(XMLEventReader xer, XProperties xp, State state) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();

        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(MODULE_TAG)) {
                        ModuleAttributes moduleAttributes = getModuleAttributes(xe);
                        Module module = deserializeModule(xer, moduleAttributes, xp, state);
                        state.getImportedModules().put(moduleAttributes.alias, module);
                        state.getImportManager().addImport(moduleAttributes.ns, moduleAttributes.alias);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(IMPORTED_MODULES)) {
                        return;
                    }
            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(IMPORTED_MODULES);
    }


    public static void deserializeTemplates(XMLEventReader xer, XProperties xp, State state) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();

        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(MODULE_TAG)) {
                        ModuleAttributes moduleAttributes = getModuleAttributes(xe);
                        Module module = deserializeModule(xer, moduleAttributes, xp, state);
                        state.getModuleMap().put(moduleAttributes.ns, module);
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(MODULE_TEMPLATE_TAG)) {
                        return;
                    }
            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(MODULE_TEMPLATE_TAG);
    }

    /**
     * process a single module. This is charged with creating the module (Java modules require introspection)
     * and then deserializing any addtional state. Modules should put additional (de)serialization into
     * their definitions.
     *
     * @param xer
     * @param state
     * @throws XMLStreamException
     */

    protected static Module deserializeModule(XMLEventReader xer,
                                              ModuleAttributes moduleAttributes,
                                              XProperties xp,
                                              State state) throws XMLStreamException {
        // Cursor management: The stream points to the module tag or we would not be here, but we need to
        // create the
        Module module = null;
        State moduleState = state.newModuleState();

        if (moduleAttributes.isJavaModule()) {
            if (StringUtils.isTrivial(moduleAttributes.className)) {
                throw new IllegalStateException("Error: serialized java module without a classs -- cannot deserialize");
            }
            try {
                Class klasse = state.getClass().forName(moduleAttributes.className);
                module = ((JavaModule) klasse.newInstance()).newInstance(moduleState); // this populates the functions and variables!!
            } catch (Throwable t) {
                throw new IllegalStateException("Error: cannot deserialize class \"" + moduleAttributes.className + "\".");
            }

        } else {
            module = new QDLModule();
        }
        module.setState(moduleState);
        module.setAlias(moduleAttributes.alias);
        module.setNamespace(moduleAttributes.ns);

        // scorecard: at this point, enough information existed in the attributes to re-assemble the module
        // The cursor is still on this tag. Now send it to the module for further processing.
        module.fromXML(xer, xp);
        if (module instanceof JavaModule) {
            // call this at the right time -- after everything else has been done.
            ((JavaModule) module).init(moduleState);
        }
        return module;
    }

    /**
     * Internal class to manage attributes for a module.
     */
    protected static class ModuleAttributes {
        String type = null;
        URI ns = null;
        String alias = null;
        String className = null;

        protected boolean isJavaModule() {
            if (type == null) {
                return false;
            }
            return type.equals(MODULE_TYPE_JAVA_TAG);
        }

    }

    protected static ModuleAttributes getModuleAttributes(XMLEvent xe) throws XMLStreamException {
        ModuleAttributes moduleAttributes = new ModuleAttributes();
        Iterator iterator = xe.asStartElement().getAttributes(); // Use iterator since it tracks state
        while (iterator.hasNext()) {
            Attribute a = (Attribute) iterator.next();
            String v = a.getValue();
            switch (a.getName().getLocalPart()) {
                case MODULE_ALIAS_ATTR:
                    moduleAttributes.alias = v;
                    break;
                case MODULE_NS_ATTR:
                    moduleAttributes.ns = URI.create(v);
                    break;
                case MODULE_CLASS_NAME_TAG:
                    moduleAttributes.className = v;
                    break;
                case MODULE_TYPE_TAG:
                    moduleAttributes.type = v;
                    break;
            }
        }
        return moduleAttributes;
    }


    public static String prettyPrint(String input) throws Throwable {
        return prettyPrint(input, 2);
    }

    public static String prettyPrint(String input, int indent) throws Throwable {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indent));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);

        Source xmlInput = new StreamSource(new StringReader(input));
        transformer.transform(xmlInput, xmlOutput);

        return xmlOutput.getWriter().toString();

    }

    public static XMLEventReader getReader(File f) {
        try {
            FileReader reader = new FileReader(f);
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            return xmlif.createXMLEventReader(reader);
        } catch (Throwable t) {
        }
        return null;

    }

    public static XMLEventReader getZippedReader(File f) {
        FileInputStream fis = null;
        XMLEventReader xer = null;
        GZIPInputStream gzipInputStream = null;
        try {
            fis = new FileInputStream(f);
            byte[] b = new byte[(int) f.length()];
            fis.read(b);
            fis.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            // Reconstruct the XML as a string, preserving whitespace.
            gzipInputStream = new GZIPInputStream(bais, 65536);
            Reader r = new InputStreamReader(gzipInputStream);
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            return xmlif.createXMLEventReader(r);
        } catch (Throwable t) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException iox) {
                }
            }
        }
        return null;
    }

}
