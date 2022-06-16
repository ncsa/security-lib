package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.statements.Documentable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTIONS_TAG;
import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTION_TAG;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/19/21 at  7:48 AM
 */
public class FTable<K extends FKey, V extends FunctionRecord> extends XTable<K, V> implements Documentable {

    /**
     * If argCount === -1, remove all named functions, otherwise only remove the one with the
     * exact argCount.
     *
     * @param key
     * @return
     */
    @Override
    public V remove(Object key) {
        if (!(key instanceof FKey)) {
            throw new IllegalArgumentException(key + " is not an FKey");
        }
        FKey fKey = (FKey) key;
        if (fKey.getArgCount() == -1) {
            for (XKey key1 : keySet()) {
                if (((FKey) key1).hasName(fKey.getfName())) {
                    remove(key1);
                }
            }
            return null;
        }

        return super.remove(key);
    }

    public List<V> getByAllName(String name) {
        List<V> fList = new ArrayList<>();
        for (XKey key : keySet()) {
            if (((FKey) key).hasName(name)) {
                fList.add(get(key));
            }
        }
        return fList;
    }

    @Override
    public Collection<V> values() {
        List<V> fList = new ArrayList<>();
        for (XKey key : keySet()) {
            fList.add(get(key));
        }
        return fList;
    }


    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof FKey)) {
            throw new IllegalArgumentException(key + " is not an FKey");
        }
        FKey fkey = (FKey) key;
        if (fkey.getArgCount() == -1) {
            for (XKey key0 : keySet()) {
                if (((FKey) key0).hasName(fkey.getfName())) {
                    return true;
                }
            }

        }
        return super.containsKey(key);
    }

    @Override
    public TreeSet<String> listFunctions(String regex) {
        // The tree set keeps the argument counts in order since the functions are
        // iterated out of order. 
        HashMap<String, Set<Integer>> fAndArgs = new HashMap<>();

        for (XKey key : keySet()) {
            String name = ((FKey) key).getfName(); // de-munge
            FunctionRecord fr = get(key);
            if (regex != null && !regex.isEmpty()) {
                if (name.matches(regex)) {
                    if (!fAndArgs.containsKey(name)) {
                        Set<Integer> list = new TreeSet<>();
                        fAndArgs.put(name, list);
                    }
                    fAndArgs.get(name).add(fr.getArgCount());
                }
            } else {
                if (!fAndArgs.containsKey(name)) {
                    Set<Integer> list = new TreeSet<>();
                    fAndArgs.put(name, list);
                }
                fAndArgs.get(name).add(fr.getArgCount());
            }
        }
        TreeSet<String> names = new TreeSet<>();
        for (String key : fAndArgs.keySet()) {
            String args = fAndArgs.get(key).toString();
            args = args.replace(" ", ""); // no blanks in arg list. Makes regexes easier
            names.add(key + "(" + args + ")");
        }
        return names;
    }

    /**
     * Just lists the first line of every function with documentation
     *
     * @return
     */
    @Override
    public List<String> listAllDocs() {
        ArrayList<String> docs = new ArrayList<>();
        for (XKey key : keySet()) {
            String name = ((FKey) key).getfName(); // de-munge
            FunctionRecord fr = get(key);
            name = name + "(" + fr.getArgCount() + ")";
            if (0 < fr.documentation.size()) {
                if (!fr.documentation.get(0).contains(name)) {
                    name = fr.documentation.get(0);
                } else {
                    name = name + ": " + fr.documentation.get(0);
                }
            } else {
                name = name + ": (none)";

            }
            docs.add(name);
        }

        return docs;
    }

    // Filter by fname.
    public List<String> listAllDocs(String fname) {
        ArrayList<String> docs = new ArrayList<>();
        for (XKey key : keySet()) {
            if (((FKey) key).hasName(fname)) {
                FunctionRecord fr = get(key);
                String name = fname + "(" + fr.getArgCount() + ")";
                if (0 < fr.documentation.size()) {
                    if (!fr.documentation.get(0).contains(name)) {
                        name = fr.documentation.get(0);
                    } else {
                        name = name + ": " + fr.documentation.get(0);
                    }
                } else {
                    name = name + ": (none)";

                }
                docs.add(name);
            }
        }
        return docs;
    }

    /**
     * Returns the specific documentation for a function. The request is of the form name(args);
     *
     * @param fName
     * @return
     */
    @Override
    public List<String> getDocumentation(String fName, int argCount) {
        throw new NotImplementedException("not implemented in XTables");
    }

    @Override
    public List<String> getDocumentation(FKey key) {
        if (get(key) == null) {
            return new ArrayList<>(); // never null
        } else {
            return get(key).documentation;
        }
    }

    /**
     * Writes every function in no particular order by its source code. Look at
     * {@link FStack#toXML(XMLStreamWriter, XMLSerializationState)} for top level of functions
     */
    @Override
    public void toXML(XMLStreamWriter xsw, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        for (XKey key : keySet()) {
            if (get(key).sourceCode.isEmpty()) {
                // No source code usually means it is from some external function
                // and we cannot recreate it.
                continue;
            }
            String name = ((FKey) key).getfName(); // de-munge

            xsw.writeStartElement(XMLConstants.FUNCTION_TAG);
            xsw.writeAttribute(XMLConstants.FUNCTION_NAME_TAG, name);
            xsw.writeAttribute(XMLConstants.FUNCTION_ARG_COUNT_TAG, Integer.toString(get(key).getArgCount()));

            xsw.writeCData(StringUtils.listToString(get(key).sourceCode));
            xsw.writeEndElement();
        }
    }


    /**
     * Deserialize a single function using the interpreter (and its current state); This assumes that the function
     * will get stuffed into the current state and that will get processed.
     *
     * @param xer
     * @param qi
     * @throws XMLStreamException
     */
    public void processSingleFunction(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(FUNCTION_TAG)) {
                        return;
                    }
                case XMLEvent.CHARACTERS:
                    if (!xe.asCharacters().isIgnorableWhiteSpace()) {
                        String x = xe.asCharacters().getData();
                        x = FDOC_CONVERT ? convertFDOC(x) : x;
                        try {
                            qi.execute(x);
                        } catch (Throwable t) {
                            // should do something else??
                            System.err.println("Error deserializing function '" + x + "': " + t.getMessage());
                        }
                    }
            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(FUNCTION_TAG);
    }

    @Override
    public V deserializeElement(XMLEventReader xer, XMLSerializationState XMLSerializationState, QDLInterpreter qi) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        //Since this requires parsing from the source which can get extremely complex (that's why we have a parser)
        // about the only way to do this is to black box it, viz., look at the state (set of functions) beforehand
        // then let the magic happen and look at the set afterwords. Return the difference.
        Set<XKey> oldKeys = qi.getState().getFTStack().keySet();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    break;
                case XMLEvent.CHARACTERS:
                    if (!xe.asCharacters().isIgnorableWhiteSpace()) {
                        String x = xe.asCharacters().getData();
                        x = FDOC_CONVERT ? convertFDOC(x) : x;
                        try {
                            qi.execute(x);
                        } catch (Throwable t) {
                            // should do something else??
                            System.err.println("Error deserializing function '" + x + "': " + t.getMessage());
                        }
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(getXMLElementTag())) {
                        Set<XKey> newKeys = qi.getState().getFTStack().keySet();
                        newKeys.removeAll(oldKeys);
                        if (newKeys.isEmpty()) {
                            throw new IllegalStateException("no function found to deserialize");
                        }
                        if (newKeys.size() != 1) {
                            throw new IllegalStateException(newKeys.size() + " functions deserialized. A single one was expected");
                        }
                        return (V) qi.getState().getFTStack().get(newKeys.iterator().next());
                    }
                    break;

            }
            xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(FUNCTION_TAG);
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    if (xe.asStartElement().getName().getLocalPart().equals(FUNCTION_TAG)) {
                        processSingleFunction(xer, qi);
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

    @Override
    public String getXMLTableTag() {
        return FUNCTIONS_TAG;
    }

    @Override
    public String getXMLElementTag() {
        return FUNCTION_TAG;
    }

    /*
     Big note on serializing functions. All that are serialized here are QDL native functions.
     The source is stored and on deserialization they are run through the interpreter again to
     populate this table. This is because such functions are extremely dynamic and can be redefined
     at any time. Java functions, however, live in Java code. When a module is deserialized, if it
     is a Java module, the function table is populated from the template since such functions live
     there and are immutable. 
     */
    @Override
    public String toJSONEntry(V xThing, XMLSerializationState xmlSerializationState) {
        String src = StringUtils.listToString(xThing.sourceCode);
        return Base64.encodeBase64URLSafeString(src.getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public String fromJSONEntry(String x, XMLSerializationState xmlSerializationState) {
        // Conversion away from >> in function documentation. This allows for converting older
        // workspaces
        x = new String(Base64.decodeBase64(x));
        x = FDOC_CONVERT ? convertFDOC(x) : x;
        return x;
    }
}

