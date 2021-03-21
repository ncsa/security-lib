package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTION_TABLE_STACK_TAG;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/15/21 at  6:22 AM
 */
public class FTStack implements FunctionTable {
    public FTStack() {
        pushNew();
    }

    /**
     * Add the table to the end of the stack. Let's you control where the tables end up.
     * @param functionTable
     */
    public void append(FunctionTable functionTable){
        ftables.add(functionTable);
    }
    public boolean isEmpty() {
        boolean empty = true;
        for (FunctionTable functionTable : ftables) {
            empty = empty && functionTable.isEmpty();
        }
        return empty;
    }

    /**
     * remove <b>all</b> instances of the function from the local stack
     *
     * @param name
     * @param argCount
     * @return
     */
    public void remove(String name, int argCount) {
        peek().remove(name, argCount);
    }

    /**
     * Take the FT stack and add all of the tables in this stack in the correct order.
     * This is needed when, e.g., creating new local state for function reference resolution
     *
     * @param ftStack
     */
    public void addTables(FTStack ftStack) {
        // add backwards
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            ftStack.push(ftables.get(i));
        }
    }

    ArrayList<FunctionTable> ftables = new ArrayList<>();

    public FunctionTable peek() {
        return ftables.get(0);
    }

    public void push(FunctionTable ft) {
        ftables.add(0, ft);
    }

    public void pushNew() {
        push(new FunctionTableImpl());
    }

    @Override
    public FunctionRecord put(FunctionRecord value) {
        for(FunctionTable functionTable : ftables){
            if(functionTable.isDefined(value.name, value.getArgCount())){
                functionTable.put(value);
                return value;
            }
        }

        return peek().put(value);
    }

    @Override
    public FunctionRecord get(String key, int argCount) {
        for (FunctionTable functionTable : ftables) {
            FunctionRecord fr = functionTable.get(key, argCount);
            if (fr != null) {
                return fr;
            }
        }
        return null;
    }

    @Override
    public FunctionRecord getFunctionReference(String name) {
        for (FunctionTable functionTable : ftables) {
            FunctionRecord fr = functionTable.getFunctionReference(name);
            if (fr != null) {
                return fr;
            }
        }
        return null;
    }

    @Override
    public boolean isDefined(String var, int argCount) {
        for (FunctionTable functionTable : ftables) {
            if (functionTable.isDefined(var, argCount)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used in the case there is a single function by this name so we don't have to know ahead
     * of time the arg count (which might not be available).
     *
     * @param name
     * @return
     */
    @Override
    public FunctionRecord getSomeFunction(String name) {
        for (FunctionTable functionTable : ftables) {
            FunctionRecord fr = functionTable.getSomeFunction(name);
            if (fr != null) {
                return fr;
            }
        }

        return null;
    }

    public List<FunctionRecord> getByAllName(String name) {
        List<FunctionRecord> all = new ArrayList<>();
        // Note this walks backwards through the stack.
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            all.addAll(ftables.get(i).getByAllName(name));
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
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            ftables.get(i).toXML(xsw);
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
            /*            case XMLConstants.FUNCTION_TABLE_STACK_TAG:
                            foundStack = true;
                            FunctionTableImpl functionTable = new FunctionTableImpl();
                            functionTable.fromXML(xer, qi);
                            if(!functionTable.isEmpty()) {
                                this.push(functionTable);
                            }
                            break;*/
                        // Legacy case -- just a single functions block, not a stack.
                        case XMLConstants.FUNCTIONS_TAG:
                            if (foundStack) break; // if a stack is being processed, skip this
                            //FunctionTableImpl functionTable1 = new FunctionTableImpl();
                            FunctionTable functionTable1 = qi.getState().getFTStack().peek();
                            functionTable1.fromXML(xer, qi);
                          /*  if(!functionTable1.isEmpty()) {
                                this.push(functionTable1);
                            }*/
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
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            all.addAll(ftables.get(i).listFunctions(regex));
        }
        return all;
    }

    @Override
    public List<String> listAllDocs() {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            all.addAll(ftables.get(i).listAllDocs());
        }
        return all;
    }

    @Override
    public List<String> listAllDocs(String functionName) {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            all.addAll(ftables.get(i).listAllDocs(functionName));
        }
        return all;
    }

    @Override
    public List<String> getDocumentation(String fName, int argCount) {
        List<String> all = new ArrayList<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            all.addAll(ftables.get(i).getDocumentation(fName, argCount));
        }
        return all;
    }

    @Override
    public FTStack clone() {
        FTStack cloned = new FTStack();
        for (FunctionTable ft : ftables) {
               cloned.append(ft);
        }
        return cloned;
    }

    @Override
    public String toString() {
        String out = "[" + getClass().getSimpleName();
        out = out + ", table#=" + ftables.size();
        int i = 0;
        int totalSymbols = 0;
        boolean isFirst = true;
        out = ", counts=[";
        for (FunctionTable functionTable : ftables) {
            if(isFirst){
                isFirst = false;
                out = out + functionTable.size();
            }else {
                out = out + "," + functionTable.size();
            }
            totalSymbols += functionTable.size();
        }
        out = out + "], total=" + totalSymbols;
        out = out + "]";
        return out;
    }

    @Override
    public int size() {
        int totalSymbols = 0;
        for (FunctionTable functionTable : ftables) {
            totalSymbols += functionTable.size();
        }
        return totalSymbols;
    }
}
