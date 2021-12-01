package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.statements.Documentable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTIONS_TAG;
import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTION_TAG;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/19/21 at  7:48 AM
 */
public class FTable<V extends FunctionRecord>  extends HashMap<XKey, V>  implements Documentable,XTable<V> {
    @Override
    public V put(XThing value) {
        return super.put(value.getKey(), (V) value);
    }


    /**
     * If argCount === -1, remove all named functions, otherwise only remove the one with the
     * exact argCount.
     * @param key
     * @return
     */
    @Override
    public V remove(Object key) {
        if(!(key instanceof FKey)){
            throw new IllegalArgumentException(key + " is not an FKey");
        }
        FKey fKey = (FKey) key;
        if (fKey.getArgCount() == -1) {
            for (XKey key1 : keySet()) {
                if (((FKey)key1).hasName(fKey.getfName())) {
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
            if (((FKey)key).hasName(name)) {
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
        if(!(key instanceof FKey)){
            throw new IllegalArgumentException(key + " is not an FKey");
        }
        FKey fkey = (FKey) key;
        if(fkey.getArgCount() == -1){
            for (XKey key0 : keySet()) {
                if (((FKey)key0).hasName(fkey.getfName())) {
                    return true;
                }
            }

        }
        return super.containsKey(key);
    }

    @Override
    public TreeSet<String> listFunctions(String regex) {
        TreeSet<String> names = new TreeSet<>();

        for (XKey key : keySet()) {
            String name = ((FKey)key).getfName(); // de-munge
            FunctionRecord fr = get(key);
            if (regex != null && !regex.isEmpty()) {
                if (name.matches(regex)) {
                    name = name + "(" + fr.getArgCount() + ")";
                    names.add(name);
                }
            } else {
                name = name + "(" + fr.getArgCount() + ")";
                names.add(name);
            }
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
            String name = ((FKey)key).getfName(); // de-munge
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
            if (((FKey)key).hasName(fname)) {
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

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        if (isEmpty()) {
            return;
        }
        xsw.writeStartElement(FUNCTIONS_TAG);
        for (XKey key : keySet()) {
            if (get(key).sourceCode.isEmpty()) {
                // No source code usually means it is from some external function
                // and we cannot recreate it.
                continue;
            }
            String name = ((FKey)key).getfName(); // de-munge

            xsw.writeStartElement(XMLConstants.FUNCTION_TAG);
            xsw.writeAttribute(XMLConstants.FUNCTION_NAME_TAG, name);
            xsw.writeAttribute(XMLConstants.FUNCTION_ARG_COUNT_TAG, Integer.toString(get(key).getArgCount()));

            xsw.writeCData(StringUtils.listToString(get(key).sourceCode));
            xsw.writeEndElement();
        }
        xsw.writeEndElement();
    }


    /**
     * Deserialize a single function using the interpreter (and its current state);
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
                        try {
                            qi.execute(xe.asCharacters().getData());
                        } catch (Throwable t) {
                            // should do something else??
                            System.err.println("Error deserializing function:" + t.getMessage());
                        }
                    }
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
    public String toString() {
        return getClass().getSimpleName() + "[size=" + size() + "]";
    }

/*    public FunctionRecord getFunctionReference(String name) {
          FKey key = new FKey(name,-1);
            if (super.containsKey(key)) {
                return get(key);
            }
            return null;
        }*/
}

