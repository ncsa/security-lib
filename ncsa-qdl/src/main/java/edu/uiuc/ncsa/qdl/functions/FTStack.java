package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

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
 * @deprecated use {@link FStack}
 */
public class FTStack implements FunctionTable {
    public FTStack() {
        pushNew();
    }

    /**
     * Add the table to the end of the stack. Let's you control where the tables end up.
     *
     * @param functionTable
     */
    public void append(FunctionTable functionTable) {
        ftables.add(functionTable);
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
    public boolean containsKey(String var, int argCount) {
        return containsKey(var, argCount, 0);
    }

    public boolean containsKey(String var, int argCount, int startTableIndex) {
        for (int i = startTableIndex; i < ftables.size(); i++) {
            if (ftables.get(i).containsKey(var, argCount)) {
                return true;
            }
        }
        return false;
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

    public List<FunctionRecord> getAll() {
        List<FunctionRecord> all = new ArrayList<>();
        for (FunctionTable ft : ftables) {
            all.addAll(ft.getAll());
        }
        return all;
    }

    /**
     * Retrieve every instance of the entries in all the tables. Necessary to capture all the
     * overrides, e.g., that a user might have made to a function. So if f(x) is defined
     * and the user redefines it, say, inside another function, this will pick up the most recent
     * definition. <br/><br/>
     * See edu.uiuc.ncsa.qdl.evaluate.FunctionEvaluator#doFunctionEvaluation(Polyad, State, FR_WithState).
     * @param name
     * @return
     */
    public List<FunctionRecord> getByAllName(String name) {
        List<FunctionRecord> all = new ArrayList<>();
        // Note this walks backwards through the stack.
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            all.addAll(ftables.get(i).getByAllName(name));
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
    public List<String> getDocumentation(FKey key) {
        throw new NotImplementedException("not implemented in old FTStack");
    }

    /**
     * Find and return the first reference to this function, starting in the local
     * stack.
     * @param name
     * @return
     */
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


    public boolean isEmpty() {
        boolean empty = true;
        for (FunctionTable functionTable : ftables) {
            empty = empty && functionTable.isEmpty();
        }
        return empty;
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
    public TreeSet<String> listFunctions(String regex) {
        TreeSet<String> all = new TreeSet<>();
        // Note this walks backwards through the stack since this means that if
        // there is local documentation it overwrites the global documentation.
        for (int i = ftables.size() - 1; 0 <= i; i--) {
            all.addAll(ftables.get(i).listFunctions(regex));
        }
        return all;
    }


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
        for (FunctionTable functionTable : ftables) {
            if (functionTable.containsKey(value.name, value.getArgCount())) {
                functionTable.put(value);
                return value;
            }
        }

        return peek().put(value);
    }
    /**
     * remove  instance of the function from the local stack
     *
     * @param name
     * @param argCount
     * @return
     */
    public void remove(String name, int argCount) {
        peek().remove(name, argCount);
    }

    /**
     * Used in the workspace mostly to tell how many of these total are
     * used, also in this class to do {@link #toString()}.
     * @return
     */
    @Override
    public int size() {
        int totalSymbols = 0;
        for (FunctionTable functionTable : ftables) {
            totalSymbols += functionTable.size();
        }
        return totalSymbols;
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
            if (isFirst) {
                isFirst = false;
                out = out + functionTable.size();
            } else {
                out = out + "," + functionTable.size();
            }
            totalSymbols += functionTable.size();
        }
        out = out + "], total=" + totalSymbols;
        out = out + "]";
        return out;
    }

    ArrayList<FunctionTable> ftables = new ArrayList<>();

    public ArrayList<FunctionTable> getFtables() {
        return ftables;
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
                        // Legacy case -- just a single functions block, not a stack.
                        case XMLConstants.FUNCTIONS_TAG:
                            if (foundStack) break; // if a stack is being processed, skip this
                            FunctionTable functionTable1 = (FunctionTable) qi.getState().getFTStack().peek();
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

}
