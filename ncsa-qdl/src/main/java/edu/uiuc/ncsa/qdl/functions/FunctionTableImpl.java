package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTION_TAG;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:52 AM
 */
public class FunctionTableImpl extends HashMap<String, FunctionRecord> implements FunctionTable {
    String munger = "$$$";

    public String createKey(String name, int argCount) {
        if(argCount == FunctionRecord.FREF_ARG_COUNT){
            return name + munger;
        }
        return name + munger + argCount;
    }

    public FunctionRecord getFunctionReference(String name){
        if(super.containsKey(name + munger)){
            return get(name + munger);
        }
        return null;
    }
    public String createKey(FunctionRecord fr) {
        return createKey(fr.name, fr.getArgCount());
    }

    @Override
    public FunctionRecord put(FunctionRecord value) {
        return super.put(createKey(value), value);
    }


    @Override
    public FunctionRecord get(String key, int argCount) {
        return super.get(createKey(key, argCount));
    }

    @Override
    public void remove(String fName, int argCount) {
          if(argCount == -1){
              String almostMungedName = fName + munger;
                    for (String key : keySet()) {
                        if (key.startsWith(almostMungedName)) {
                            remove(key);
                        }
                    }

              return;
          }
          remove(createKey(fName, argCount));
    }

    @Override
    public List<FunctionRecord> getByAllName(String name) {
        List<FunctionRecord> fList = new ArrayList<>();
        String almostMungedName = name + munger;
              for (String key : keySet()) {
                  if (key.startsWith(almostMungedName)) {
                      fList.add(get(key));
                  }
              }
        return fList;
    }

    @Override
    public boolean isDefined(String var, int argCount) {
        if (argCount == -1) {
            for (String k : keySet()) {
                if (k.startsWith(var + munger)) {
                    return true;
                }
            }
            return false;
        }
        return super.containsKey(createKey(var, argCount));
    }

    @Override
    public TreeSet<String> listFunctions(String regex) {
        TreeSet<String> names = new TreeSet<>();

        for (String key : keySet()) {
            String name = key.substring(0, key.indexOf(munger)); // de-munge
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
        for (String key : keySet()) {
            String name = key.substring(0, key.lastIndexOf(munger)); // de-munge
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
        for (String key : keySet()) {
            if (key.startsWith(fname + munger)) {
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
        if (get(fName, argCount) == null) {
            return null;
        } else {
            return get(fName, argCount).documentation;
        }
    }

    /**
     * This looks up to see if there is any such named function, regardless of number of arguments.
     *
     * @param name
     * @return
     */
    @Override
    public FunctionRecord getSomeFunction(String name) {
        String almostMungedName = name + munger;
        for (String key : keySet()) {
            if (key.startsWith(almostMungedName)) return get(key);
        }
        return null;
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        if (isEmpty()) {
            return;
        }
        xsw.writeStartElement(XMLConstants.FUNCTIONS_TAG);
        for (String key : keySet()) {
            if (StringUtils.isTrivial(get(key).sourceCode)) {
                // No source code usually means it is from some external function
                // and we cannot recreate it.
                continue;
            }
            String name = key.substring(0, key.lastIndexOf(munger)); // de-munge

            xsw.writeStartElement(XMLConstants.FUNCTION_TAG);
            xsw.writeAttribute(XMLConstants.FUNCTION_NAME_TAG, name);
            xsw.writeAttribute(XMLConstants.FUNCTION_ARG_COUNT_TAG, Integer.toString(get(key).getArgCount()));

            xsw.writeCData(get(key).sourceCode);
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
    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
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
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + FUNCTION_TAG);


    }
}
