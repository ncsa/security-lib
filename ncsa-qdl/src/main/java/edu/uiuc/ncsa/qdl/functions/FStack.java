package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XStack;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.statements.Documentable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTION_TABLE_STACK_TAG;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/15/21 at  6:22 AM
 */
public class FStack<V extends FTable<? extends FunctionRecord>> extends XStack<V> implements Serializable, Documentable {

    public FStack() {
        pushNewTable();
    }


    public FunctionRecord getFunctionReference(String name) {
         for (XTable functionTable : getStack()) {
             FunctionRecord fr = (FunctionRecord) functionTable.get(new FKey(name, -1));
             if (fr != null) {
                 return fr;
             }
         }
         return null;
     }

    /**
     * Returns all of the named functions for any arg count. This is needed
     * to populate copies of local state.
     * @param name
     * @return
     */
    public List<FunctionRecord> getByAllName(String name) {
        List<FunctionRecord> all = new ArrayList<>();
        // Note this walks backwards through the stack.
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            all.addAll(((FTable)getStack().get(i)).getByAllName(name));
        }
        return all;

    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        if (isEmpty()) {
            return;
        }
        xsw.writeStartElement(XMLConstants.FUNCTION_TABLE_STACK_TAG);
        xsw.writeComment("The functions for this state.");
        // lay these in in reverse order so we just have to read them in the fromXML method
        // and push them on the stack
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            getStack().get(i).toXML(xsw);
        }
        xsw.writeEndElement(); // end of tables.
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
        // points to stacks tag
        XMLEvent xe = xer.nextEvent(); // moves off the stacks tag.
        // no attributes or such with the stacks tag.
        boolean foundStack = false;
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        // Legacy case -- just a single functions block, not a stack.
                        case XMLConstants.FUNCTIONS_TAG:
                            if (foundStack) break; // if a stack is being processed, skip this
                            FTable functionTable1 = (FTable) qi.getState().getFTStack().peek();
                            functionTable1.fromXML(xer, qi);
                            break;
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(FUNCTION_TABLE_STACK_TAG)) {
                        return;
                    }
                    break;
            }
            xe = xer.nextEvent();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + FUNCTION_TABLE_STACK_TAG);


    }
    @Override
    public TreeSet<String> listFunctions(String regex) {
        TreeSet<String> all = new TreeSet<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            all.addAll(((V)getStack().get(i)).listFunctions(regex));
        }
        return all;
    }

    @Override
    public List<String> listAllDocs() {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            all.addAll(((V)getStack().get(i)).listAllDocs());
        }
        return all;
    }

    @Override
    public List<String> listAllDocs(String functionName) {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            all.addAll(((V)getStack().get(i)).listAllDocs(functionName));
        }
        return all;
    }

    @Override
    public List<String> getDocumentation(String fName, int argCount) {
    throw new NotImplementedException("not implemented in XStack");
    }

    @Override
    public List<String> getDocumentation(FKey key) {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            all.addAll(((V)getStack().get(i)).getDocumentation(key));
        }
        return all;
    }

    @Override
    public XStack newInstance() {
        return new FStack();
    }

    @Override
    public XTable newTableInstance() {
        return new FTable<>();
    }
}
